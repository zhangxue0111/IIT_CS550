package Server;

import db.bean.Peer;
import db.dao.PeerDao;
import db.dao.PeerDaoImpl;
import db.util.JDBCUtils;
import shared.SuperPeerService;
import topology.Node;
import utils.InvalidationMessage;
import utils.QueryMessage;
import utils.Response;
import utils.YamlReader;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @description: An implementation of services offered by remote servers.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public class SuperPeerServiceImpl implements SuperPeerService {

    private static final PeerDao dao = new PeerDaoImpl();

    private static final Map<String, Map<String, QueryMessage>> maps;
    private static final Map<String, Map<String, InvalidationMessage>> invalidationMaps;

    static {
        maps = new HashMap<>();
        invalidationMaps = new HashMap<>();
    }

    private String getFileMd5(String fileName) throws IOException, NoSuchAlgorithmException {
        InputStream is = Files.newInputStream(Paths.get(fileName));
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(Files.readAllBytes(Paths.get(fileName)));
        byte[] digest = md.digest();
        String fileChecksum = Base64.getEncoder().encodeToString(digest);
        return fileChecksum;
    }

    @Override
    public Response registry(Integer clientID, String serverName, File file) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            FileInputStream fis= new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            String fileType = "copy";
            String fileTTR = "unexpired";
            // System.out.println(peer);
            if(file.canRead() && file.canWrite()) {
                fileType = "master";
            }
            Peer peer = new Peer(null, clientID, serverName, file.getName(), file.getPath());
            // System.out.println(peer);
            Peer newPeer = dao.getPeerByAll(conn, peer);
            peer.setTtr(fileTTR);
            if(newPeer == null) {
                if(fileType.equals("copy")) {
                    Peer masterPeer = dao.getPeerByFileNameAndType(conn, file.getName(), "master");
                    peer.setVersion(masterPeer.getVersion());
                } else {
                    peer.setVersion(0);
                }
                peer.setFilesize(fc.size());
                peer.setType(fileType);
                peer.setState("valid");
                dao.insert(conn, peer);
            } else if(newPeer.getFilesize() != fc.size()){
                Integer versionId = newPeer.getVersion() + 1;
                peer.setVersion(versionId);
                peer.setType(fileType);
                peer.setFilesize(fc.size());
                peer.setState("valid");
                newPeer.setState("invalid");
                dao.update(conn, newPeer);
                dao.insert(conn, peer);
            }
            Response response;
            response = new Response(200, "register success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(conn, null);
        }
        return new Response(401, "register failed!");
    }

    @Override
    public Response registryAll(Integer clientID, String serverName, List<File> lists) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            // System.out.println(conn);
            String fileType = "copy";
            String fileState = "invalid";
            String fileTTR = "expired";
            for(File file: lists) {
                FileInputStream fis= new FileInputStream(file);
                FileChannel fc = fis.getChannel();
                // System.out.println(file.getName());
                if(file.canRead() && file.canWrite()) {
                    System.out.println(file.getName());
                    fileType = "master";
                    fileState = "valid";
                    fileTTR = "unexpired";
                }
                Peer peer = new Peer(null, clientID, serverName, file.getName(), file.getPath());
                // System.out.println(peer);
                Peer newPeer = dao.getPeerByAll(conn, peer);
                if(newPeer == null) {
                    if(fileType.equals("copy")) {
                        Peer masterPeer = dao.getPeerByFileNameAndType(conn, file.getName(), "master");
                        peer.setVersion(masterPeer.getVersion());
                    } else {
                        peer.setVersion(0);
                    }
                    peer.setFilesize(fc.size());
                    peer.setType(fileType);
                    peer.setState(fileState);
                    peer.setTtr(fileTTR);
                    peer.setState("valid");
                    dao.insert(conn, peer);
                } else if(newPeer.getFilesize() != fc.size()){
                    Integer versionId = newPeer.getVersion() + 1;
                    peer.setVersion(versionId);
                    peer.setType(fileType);
                    peer.setFilesize(fc.size());
                    peer.setState("valid");
                    newPeer.setState("invalid");
                    dao.update(conn, newPeer);
                    dao.insert(conn, peer);
                }
            }
            Response response;
            response = new Response(200, "register success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            JDBCUtils.closeResource(conn, null);
        }
        return new Response(401, "register failed!");
    }

    @Override
    public Response deregister(Integer clientID, String fileName, Boolean isUpdate) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            // System.out.println(conn);
            // 1. checking whether the file to be deleted exists
            List<Peer> lists = dao.getPeerByPeerIdAndFileName(conn, clientID, fileName);
            if(lists.isEmpty()) {
                //System.out.println("the file you want to deregister doesn't exist!");
                return new Response(303, "no such a registered file");
            }
            // 2. exits:
            Integer versionID = null;
            for (Peer peer: lists){
                if(isUpdate) {
                    if(peer.getState().equals("invalid")) {
                        versionID = peer.getVersion();
                        dao.deleteById(conn, peer.getId());
                    }
                } else {
                    dao.deleteById(conn, peer.getId());
                }
            }
            return new Response(200, "deregister operation is over", versionID);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }

    private void deleteLocalFiles(String serverName, String fileName, Integer version, Integer clientID ) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            List<Peer> lists = dao.getPeerByServerAndFileName(conn, serverName, fileName, version, clientID);
            if(lists.isEmpty()) {
                // System.out.println("the file you want to deregister doesn't exist!");
                return;
            }
            // 2. exits:
            for (Peer peer: lists){
                dao.deleteById(conn, peer.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
    }

    public List<Peer> getLocalFiles(String serverName, String fileName) {
        Connection conn = null;
        List<Peer> leafNodes = null;
        try {
            conn = JDBCUtils.getConnection();
            leafNodes = dao.getPeerByFileNameAndSeverName(conn, serverName, fileName);;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return leafNodes;
    }

    @Override
    public List<Peer> queryFiles(QueryMessage message) {
        List<Peer> resultList = new ArrayList<>();
        Queue<QueryMessage> queue = new ArrayDeque<>();
        queue.add(message);
        Map<String, QueryMessage> seen;
        while(!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                QueryMessage msg = queue.poll();
                assert msg != null;
                seen = new HashMap<>();
                seen.put(msg.getMessageId(), msg);
                for(Node node: msg.getLinks()) {
                    if(maps.containsKey(node.getNodeName())) {
                        if(maps.get(node.getNodeName()).containsKey(msg.getMessageId())) {
                            continue;
                        } else {
                            maps.get(node.getNodeName()).put(msg.getMessageId(), msg);
                        }
                    } else {
                        maps.put(node.getNodeName(), seen);
                    }
                    List<Peer> result = getLocalFiles(node.getNodeName(), msg.getFileName());
                    resultList.addAll(result);
                    if(msg.getTtl() != 0) {
                        QueryMessage newMsg = new QueryMessage();
                        newMsg.setClientName(msg.getClientName());
                        newMsg.setMessageId(msg.getMessageId());
                        newMsg.setSequenceNum(msg.getSequenceNum());
                        newMsg.setFileName(msg.getFileName());
                        int newTtl =  msg.getTtl() - 1;
                        newMsg.setTtl((byte) newTtl);
                        Object links = YamlReader.getInstance().getValueByKey(node.getNodeName(), "links");
                        List<Node> nodes = YamlReader.convertMapToObject(links);
                        newMsg.setLinks(nodes);
                        queue.add(newMsg);
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public void pushInvalidation(InvalidationMessage message) {
        Queue<InvalidationMessage> queue = new ArrayDeque<>();
        queue.add(message);
        Map<String, InvalidationMessage> seen;
        while(!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                InvalidationMessage msg = queue.poll();
                assert msg != null;
                seen = new HashMap<>();
                seen.put(msg.getMessageId(), msg);
                for(Node node: msg.getLinks()) {
                    if(invalidationMaps.containsKey(node.getNodeName())) {
                        if(invalidationMaps.get(node.getNodeName()).containsKey(msg.getMessageId())) {
                            continue;
                        } else {
                            invalidationMaps.get(node.getNodeName()).put(msg.getMessageId(), msg);
                        }
                    } else {
                        invalidationMaps.put(node.getNodeName(), seen);
                    }

                    // delete registered file in the super peer;
                    String[] s = node.getNodeName().split("_");
                    int id = Integer.parseInt(s[s.length - 1]);
                    List<String> leafNodes = new ArrayList<>();
                    for(int j = 1; j<= 3; j++) {
                        int leafNodeId = (id -1) * 3 + j;
                        leafNodes.add("leafNode_" + leafNodeId);
                    }

                    // delete registered file in the super peer;
                    for(String name: leafNodes) {
                        String action = (String) YamlReader.getInstance().getValueByKey(name, "action");
                        if(action.equals("push")) {
                            String[] temp = name.split("_");
                            Integer clientID = Integer.parseInt(temp[temp.length-1]);
                            deleteLocalFiles(node.getNodeName(), msg.getFileName(), message.getVersion(), clientID);
                        }
                    }

                    if(msg.getTtl() != 0) {
                        InvalidationMessage newMsg = new InvalidationMessage();
                        newMsg.setClientName(msg.getClientName());
                        newMsg.setMessageId(msg.getMessageId());
                        newMsg.setSequenceNum(msg.getSequenceNum());
                        newMsg.setFileName(msg.getFileName());
                        int newTtl =  msg.getTtl() - 1;
                        newMsg.setTtl((byte) newTtl);
                        Object links = YamlReader.getInstance().getValueByKey(node.getNodeName(), "links");
                        List<Node> nodes = YamlReader.convertMapToObject(links);
                        newMsg.setLinks(nodes);
                        queue.add(newMsg);
                    }
                    // notification that local leafNode delete cached files

                    for(String name: leafNodes) {
                        String path = "src/dir/" + name;
                        File file = new File(path);
                        if(!name.equals(message.getClientName())) {
                            try {
                                deleteSpecificFile(file, name, message.getFileName());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }

    }
    @Override
    public Response pollVersion(File file, int peerId) {
        // get local version
        Connection conn = null;
        int data = 5;
        try {
            conn = JDBCUtils.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Peer peer = dao.getPeerByFileNameAndPeerId(conn, file.getName(), peerId);
        if (peer != null) {
//            if (peer.getTtr() == "expired") {
            peer.setTtr("expired");
            dao.update(conn, peer);
                // get original version
            int originalVer = dao.getPeerByFileNameAndType(conn, file.getName(), "master").getVersion();
            if (peer.getVersion() != originalVer) {
                peer.setState("invalid");
                dao.update(conn, peer);
                data = -1;
            } else {
                peer.setTtr("unexpired");
                dao.update(conn, peer);
            }
//            }
        }
        return new Response(401, "register failed!", data);
    }



    private static void deleteSpecificFile(File dir, String name, String fileName) throws IOException {
        if (dir.isDirectory()) {
            File[] fileList = dir.listFiles();
            for (int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
                deleteSpecificFile(fileList[i], name, fileName);
            }
        } else {
            if (dir.getName().equals(fileName)) {
                String action = (String) YamlReader.getInstance().getValueByKey(name, "action");
                if(action.equals("push")) {
                    System.out.println("Hit an outdated file, please delete it.");
                    System.out.println(dir.toPath());
                    // Files.delete(dir.toPath());
                    boolean delete = dir.delete();
                    // System.out.println(delete);
                }
            }
        }
    }
}
