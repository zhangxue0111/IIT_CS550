package server;

import client.Service;
import client.Stub;
import db.bean.Peer;
import db.dao.PeerDao;
import db.dao.PeerDaoImpl;
import db.util.JDBCUtils;
import topology.Node;
import utils.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.util.*;


/**
 * @description: A services implementations.
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public class ServiceImpl implements Service {

    private static final PeerDao dao = new PeerDaoImpl();

    private static final Map<String, List<QueryMessage>> maps = new HashMap<>();

    private final List<QueryHitMessage> lists = new ArrayList<>();


    @Override
    public Response registry(Integer clientID, String serverName, List<File> lists) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            int i = 1;
            for(File file: lists) {
                Peer peer = new Peer(null, clientID, serverName, file.getName(), file.getPath(), i++);
                if(dao.getPeerByAll(conn, peer) == null) {
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


    private List<Peer> localQuery(String serverName, String filename) {
        Connection conn = null;
        List<Peer> leafNodes = null;
        try {
            conn = JDBCUtils.getConnection();
            leafNodes = dao.getPeerByFileNameAndSeverName(conn, serverName, filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return leafNodes;
    }

    @Override
    public List<Peer> leafQuery(QueryMessage message) {
        // 1. get current server's connections
        // 2. get local result
        QueryHitMessage hitMessage = new QueryHitMessage();
        hitMessage.setClientIP(message.getClientIP());
        hitMessage.setClientPort(message.getClientPort());
        hitMessage.setServerPort(message.getTarget().getServerPort());
        hitMessage.setFileName(message.getFileName());
        hitMessage.setMessageId(message.getMessageId());
        List<Peer> peers = localQuery(message.getTarget().getNodeName(), message.getFileName());
        if(!peers.isEmpty()) {
            hitMessage.setMatches(peers);
        }
        lists.add(hitMessage);

        // 3. broadcast
        String curServerName = message.getTarget().getNodeName();
        Node forwardClient = message.getTarget();
        List<Object> infos = new ArrayList<>();
        infos.add(forwardClient);
        infos.add(message);

        Queue<List<Object>> queue = new ArrayDeque<>();
        queue.add(infos);

        List<Object> cur;
        QueryHitMessage ans;

        List<QueryMessage> seenQuery = maps.getOrDefault(curServerName, new ArrayList<>());
        seenQuery.add(message);
        maps.put(curServerName, seenQuery);

        while(!queue.isEmpty()) {
            int size = queue.size();
            for(int i = 0; i < size; i++) {
                cur = queue.poll();
                assert cur != null;
                Node node = (Node) cur.get(0);
                QueryMessage msg = (QueryMessage) cur.get(1);

                for(Node link: node.getLinks()) {
                    boolean hasSeen = false;
                    String key = link.getNodeName();
                    if(maps.containsKey(key)) {
                        List<QueryMessage> seen = maps.get(key);
                        for (QueryMessage mess : seen) {
                            if (mess.getMessageId().equals(msg.getMessageId())) {
                                hasSeen = true;
                            }
                        }
                    }
                    if (!maps.containsKey(key) || !hasSeen) {
                        Service service = (Service) Stub.getStub(Service.class, link);
                        QueryMessage queryMessage = new QueryMessage();
                        queryMessage.setClientIP(msg.getClientIP());
                        queryMessage.setClientPort(msg.getClientPort());
                        queryMessage.setFileName(msg.getFileName());
                        queryMessage.setSequenceNum(msg.getSequenceNum());
                        queryMessage.setMessageId(msg.getMessageId());
                        queryMessage.setTtl((byte) (msg.getTtl() - 1));
                        queryMessage.setTarget(link);
                        if(queryMessage.getTtl() > 0) {
                            ans = service.queryForward(queryMessage);
                            if (ans.getMatches() != null) {
                                lists.add(ans);
                            }
                            seenQuery = maps.getOrDefault(link.getNodeName(), new ArrayList<>());
                            seenQuery.add(queryMessage);
                            maps.put(link.getNodeName(), seenQuery);
                            List<Object> next = new ArrayList<>();
                            next.add(link);
                            next.add(queryMessage);
                            queue.add(next);
                        }
                    }
                }
            }
        }

        List<Peer> results = new ArrayList<>();
        for(QueryHitMessage hits: lists) {
            if(hits.getMatches() != null) {
                results.addAll(hits.getMatches());
            }
        }
        return results;
    }

    @Override
    public QueryHitMessage queryForward(QueryMessage message) {
        QueryHitMessage hitMessage = new QueryHitMessage();
        hitMessage.setClientIP(message.getClientIP());
        hitMessage.setClientPort(message.getClientPort());
        hitMessage.setServerPort(message.getTarget().getServerPort());
        hitMessage.setFileName(message.getFileName());
        hitMessage.setMessageId(message.getMessageId());
        List<Peer> peers = localQuery(message.getTarget().getNodeName(), message.getFileName());
        if(!peers.isEmpty()) {
            hitMessage.setMatches(peers);
        } else {
            hitMessage.setMatches(null);
        }
        return hitMessage;
    }

    private byte[] intToBytes( final int i ) {
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }
    @Override
    public Response obtain(Peer peer) {
        // to read a file by using stream
        FileInputStream fileInputStream = null;
        byte[] buffer = new byte[Constant.BUFFER_SIZE];
        // the last 2 nodes used to store blockNum & tailLen
        ArrayList<byte[]> data = new ArrayList<byte[]>();
        Response response = null;
        int numberOfBytes;
        try {
            fileInputStream = new FileInputStream(peer.getFilepath());
            numberOfBytes = fileInputStream.read(buffer);
            int tailLen = 0, blockNum = 0;

            while (numberOfBytes != -1) {
                if (numberOfBytes == Constant.BUFFER_SIZE) {
                    blockNum += 1;
                } else {
                    tailLen = numberOfBytes;
                }
                data.add(buffer);
                numberOfBytes = fileInputStream.read(buffer);
            }
            // to store blockNum & tailLen for creating a new file
            data.add(intToBytes(blockNum));
            data.add(intToBytes(tailLen));

            response = new Response(Constant.RETRIEVE_CODE_SUC, "Read all data of the file.", data);
        } catch (Exception e) {
            e.printStackTrace();
            response = new Response(Constant.RETRIEVE_CODE_ERR_READ, "IO exception at reading step");
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
                response = new Response(Constant.RETRIEVE_CODE_ERR_CLOSE, "IO exception at close step");
            } finally {
                return response;
            }
        }
    }

    @Override
    public Response deregister(Integer clientID, String fileName) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            // 1. checking whether the file to be deleted exists
            List<Peer> lists = dao.getPeerByPeerIdAndFileName(conn, clientID, fileName);
            if(lists.isEmpty()) {
                //System.out.println("the file you want to deregister doesn't exist!");
                return new Response(303, "no such a registered file");
            }
            // 2. exits:
            for (Peer peer: lists){
                dao.deleteById(conn, peer.getId());
            }
            return new Response(200, "deregister operation is over");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }

}
