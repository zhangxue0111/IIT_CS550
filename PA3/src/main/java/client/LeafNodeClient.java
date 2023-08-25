package client;

import Server.ServerThread;
import db.bean.Peer;
import shared.LeafNodeService;
import shared.SuperPeerService;
import topology.Node;
import utils.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;

import static java.nio.file.Files.exists;

/**
 * @description: A leaf node works as a client.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public class LeafNodeClient extends Thread {

    private final String leafNodeName;

    private final String path;

    private final String downloadPath;

    private final List<Node> links;

    private final List<File> lists = new ArrayList<>();

    private static Integer seqNumb = 0;

    private static Integer ttr = 5;

    private Timer timer = new Timer();


    public LeafNodeClient(String leafNodeName, List<Node> links) {
        this.leafNodeName = leafNodeName;
        this.path = "src/dir/" + leafNodeName;
        this.downloadPath = "src/dir/" + leafNodeName + "/download/";
        if (! new File(this.downloadPath).exists())
            new File(this.downloadPath).mkdirs();
        this.links = links;
    }

    private void getFiles() {
        List<File> list = new ArrayList<>();
        File dir = new File(path);
        listAllFile(dir);
    }

    private List<File> getLocalFiles() {
        List<File> list = new ArrayList<>();
        File dir = new File(path);
        listAllFile(dir, list);
        return list;
    }

    private void listAllFile(File f, List<File> list) {
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isDirectory())
                listAllFile(file, list);
            if (file.isFile()) {
                list.add(file);
            }
        }
    }

    private void listAllFile(File f) {
        for (File file : Objects.requireNonNull(f.listFiles())) {
            if (file.isDirectory())
                listAllFile(file);
            if (file.isFile()) {
                lists.add(file);
            }
        }
    }

    private synchronized void addSequence(){
        seqNumb++;
        System.out.println(Thread.currentThread().getName() + seqNumb);
    }

    private int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    private void downloadFile(String fileName, SuperPeerService superPeerService, Stub stub) {
        String messageId = UUID.randomUUID().toString();
        addSequence();
        QueryMessage message = new QueryMessage(leafNodeName, messageId, links);
        message.setFileName(fileName);
        message.setSequenceNum(seqNumb);
        List<Peer> lists = null;
        lists = superPeerService.queryFiles(message);
        // System.out.println(message);
        assert lists != null;
        int size = lists.size();
        System.out.println(size);
        if(size <= 0) {
            System.out.println("resource doesn't exist!");
//            break;
            return;
        }
        Peer masterPeer = null;
        for(Peer peer: lists) {
            if(peer.getType().equals("master")) {
                masterPeer = peer;
            }
        }

        System.out.println("choose = " + masterPeer);

        String key = "leafNode_" + masterPeer.getPeerid();

        String ip = (String) YamlReader.getInstance().getValueByKey(key, "ip");

        Integer port = (Integer) YamlReader.getInstance().getValueByKey(key, "serverPort");

        Node node = new Node(key, ip, port, null);

        ServerThread server = new ServerThread(node.getNodeName(), node.getIp(), node.getServerPort());

        server.addServer();

        server.start();

        LeafNodeService service = (LeafNodeService) stub.getStub(LeafNodeService.class, node.getNodeName());

        Response res = service.obtain(masterPeer);

        if (res.getCode() == Constants.RETRIEVE_CODE_SUC) {
            File file = null;
            // to create the file
            try {
                file = new File(downloadPath+fileName);
                if (file.createNewFile()) {
                    System.out.println("File created: " + file.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // to write data by using stream
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // to write data into the file
            ArrayList<byte[]> data = (ArrayList<byte[]>) res.getData();
            // to get blockNum & tailLen first
            int tailLen = convertByteArrayToInt(data.get(data.size()-1));
            data.remove(data.size()-1);
            int blockNum = convertByteArrayToInt(data.get(data.size()-1));
            data.remove(data.size()-1);
            for ( byte[] buffer : data) {
                try {
                    if (blockNum <= 0 && tailLen != 0) {
                        fileOutputStream.write(buffer,0, tailLen);
                        fileOutputStream.flush();
                    } else {
                        fileOutputStream.write(buffer);
                        fileOutputStream.flush();
                    }
                    blockNum -= 1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            boolean readOnly = file.setReadOnly();
            System.out.println(readOnly);
        }
        String filePath = downloadPath + fileName;
        File file = new File(filePath);
        String[] strings = leafNodeName.split("_");
        int clientID = Integer.parseInt(strings[strings.length - 1]);
        if(file.exists()) {
            Response response = superPeerService.registry(clientID, links.get(0).getNodeName(), file);
            System.out.println(response);
        } else {
            System.out.println("file not exits...");
        }
    }

    @Override
    public void run() {
        ServiceCenter serviceCenter = new ServiceCenter();
        Stub stub = new Stub(serviceCenter);
        String superName = links.get(0).getNodeName();
        SuperPeerService superPeerService = (SuperPeerService) stub.getStub(SuperPeerService.class,
                superName);
        Scanner input = new Scanner(System.in);
        String action = (String) YamlReader.getInstance().getValueByKey(leafNodeName, "action");
        if (action.equals("pull"))
            timer.schedule(new PollTask(), 0, ttr*1000);
        while (true) {
            System.out.println("*******************************************************");
            System.out.println("Welcome to peer to peer distributed system.");
            System.out.println("Current client is " + leafNodeName);
            System.out.println("0: Register one file of the working directory.");
            System.out.println("1: Register all files of the working directory.");
            System.out.println("2: Query a file from network.");
            System.out.println("3: Download file from a specific node.");
            System.out.println("4: List all files of the current directory.");
            System.out.println("5: Deregister the shared file.");
            System.out.println("6: Modify a local file.");
            System.out.println("7: fresh a file.");
            System.out.println("8: To exit the program.");
            System.out.println("*******************************************************");
            System.out.println("please input you request index: ");
            int requestIndex = input.nextInt();
            switch(requestIndex) {
                case 0:{
                    System.out.println("register one file!");
                    System.out.println("please input the file name to register: ");
                    String fileName = input.next();
                    String filePath = "src/dir/" + fileName;
                    File file = new File(filePath);
                    String[] strings = leafNodeName.split("_");
                    int clientID = Integer.parseInt(strings[strings.length - 1]);
                    if(file.exists()) {
                        Response response = superPeerService.registry(clientID, superName, file);
                        System.out.println(response);
                    } else {
                        System.out.println("file not exits...");
                    }
                    break;
                }
                case 1:{
                    List<File> list = getLocalFiles();
                    String[] strings = leafNodeName.split("_");
                    int clientID = Integer.parseInt(strings[strings.length - 1]);
                    Response response = superPeerService.registryAll(clientID, superName, list);
                    System.out.println(response);
                    break;
                }
                case 2:{
                    System.out.println("please input the file name: ");
                    String fileName = input.next();
                    String messageId = UUID.randomUUID().toString().substring(1,10);
                    addSequence();
                    messageId += seqNumb;
                    QueryMessage message = new QueryMessage(leafNodeName, messageId, links);
                    message.setFileName(fileName);
                    message.setSequenceNum(seqNumb);
                    List<Peer> peers = superPeerService.queryFiles(message);
                    for(Peer peer: peers) {
                        System.out.println(peer);
                    }
                    break;
                }
                case 3: {
                    System.out.println("This is download request");
                    System.out.println("Please input the file you want to download: ");
                    String fileName = input.next();
                    downloadFile(fileName, superPeerService, stub);
                    break;
                }
                case 4: {
                    List<File> list = getLocalFiles();
                    for(File file: list) {
                        System.out.println(file);
                    }
                    break;
                }
                case 5: {
                    System.out.println("Please input the file you want to deregister: ");
                    String fileName = input.next();
                    String[] strings = leafNodeName.split("_");
                    int clientID = Integer.parseInt(strings[strings.length - 1]);
                    Response response =  superPeerService.deregister(clientID, fileName, false);
                    if(response.getCode() == 200) {
                        System.out.println("deregister success!");
                    } else{
                        System.out.println(response.getMessage());
                    }
                    break;
                }
                case 6: {
                    System.out.println("Please input the file you want to modify: ");
                    String fileName = input.next();
                    String[] strings = leafNodeName.split("_");
                    int clientID = Integer.parseInt(strings[strings.length - 1]);
                    // 1. modify this file
                    String filePath = "src/dir/" +leafNodeName + "/"+ fileName;
                    // System.out.println(filePath);
                    File file = new File(filePath);
                    if(file.exists()) {
                        if(file.canRead() && file.canWrite()) {
                            try {
                                BufferedWriter writer = new BufferedWriter(
                                        new FileWriter(filePath, true));
                                writer.write("textToAppend");
                                writer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            Response response = superPeerService.registry(clientID, superName, file);
                            System.out.println(response);

                            // 2. deregister the previous version register file.
                            response =  superPeerService.deregister(clientID, fileName, true);
                            if(response.getCode() == 200) {
                                System.out.println("deregister success!");
                            } else{
                                System.out.println(response.getMessage());
                            }

                            // 3. broadcast invalid message to all nodes in the network
                            String messageId = UUID.randomUUID().toString().substring(1,10);
                            addSequence();
                            InvalidationMessage message = new InvalidationMessage(leafNodeName, messageId, links);
                            message.setFileName(fileName);
                            message.setSequenceNum(seqNumb);
                            System.out.println(response.getData());
                            message.setVersion((Integer) response.getData());
                            superPeerService.pushInvalidation(message);
                        } else {
                            System.out.println("You can't modify the copy because it's read only.");
                        }
                    } else {
                        System.out.println("file not exits...");
                    }
                    break;
                }
                case 7: {
                    System.out.println("Enter a file name you want to refresh:");
                    String fileName = input.next();
                    String filePath = downloadPath + fileName;
                    System.out.println(filePath);
                    File file = new File(filePath);
                    if(file.exists()){
                        System.out.println("hit here...");
                    // deregister the entry
                        String[] strings = leafNodeName.split("_");
                        int clientID = Integer.parseInt(strings[strings.length - 1]);
                        Response response =  superPeerService.deregister(clientID, fileName, false);
                    // delete the file


                        if (file.delete()) {
                            downloadFile(fileName, superPeerService, stub);
                        } else {
                            System.out.println("others are using the file.");
                        }
                    }
                    break;
                }
                case 8: {
                    System.out.println("you exit this service!");
                    System.exit(0);
                    input.close();
                    break;
                }
                default:{
                    System.err.println("Wrong action, please try again!");
                    break;
                }
            }
        }
    }

    private class PollTask extends TimerTask {
        @Override
        public void run() {
            ServiceCenter serviceCenter = new ServiceCenter();
            Stub stub = new Stub(serviceCenter);
            String superName = links.get(0).getNodeName();
            SuperPeerService superPeerService = (SuperPeerService) stub.getStub(SuperPeerService.class, superName);
            List<File> listFiles = getCopyFiles();
            String[] strings = leafNodeName.split("_");
            int clientID = Integer.parseInt(strings[strings.length - 1]);
            for (File file : listFiles) {
                if (file == null) {
                    System.out.println("no file");
                    break;
                }
                Response res = superPeerService.pollVersion(file, clientID);
                if ((int) res.getData() < 0) {
                    System.out.println("You need to refresh "+file.getName());
                } else {
                    ttr = (int) res.getData();
                    System.out.println(" TTR is " + ttr);
                }
            }
        }
    }

    private List<File> getCopyFiles() {
        List<File> list = new ArrayList<>();
        File dir = new File(downloadPath);
        listAllFile(dir, list);
        return list;    }
}
