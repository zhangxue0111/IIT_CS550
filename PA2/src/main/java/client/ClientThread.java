package client;

import db.bean.Peer;
import topology.Node;
import utils.Constant;
import utils.QueryMessage;
import utils.Response;
import utils.YamlReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * @description: A client thread.
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public class ClientThread extends Thread{

    private final String clientName;
    private final String clientIP;
    private final Integer clientPort;
    private final List<Node> links;
    private final Integer requestIndex;
    private final String path;
    private final List<File> lists = new ArrayList<>();
    private static Integer seqNumb = 0;


    public ClientThread(String clientName, String clientIP, Integer clientPort, List<Node> links, Integer requestIndex) {
        this.clientName = clientName;
        this.clientIP = clientIP;
        this.clientPort = clientPort;
        this.links = links;
        this.requestIndex = requestIndex;
        this.path = "src/dir/" + clientName;

    }

    private synchronized void addSequence(){
        seqNumb++;
        System.out.println(Thread.currentThread().getName() + " add seqNum, no: " + seqNumb);
    }

    private void getFiles() {
        File dir = new File(path);
        listAllFile(dir);
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

    public Response registerAction() {
        Node curServer = links.get(0);
        Service service = (Service) Stub.getStub(Service.class, links.get(0));
        getFiles();
        String[] strings = clientName.split("_");
        int clientID = Integer.parseInt(strings[strings.length - 1]);
        Response response;
        response = service.registry(clientID, curServer.getNodeName(), lists);
        return response;
    }

    private List<Peer> leafQueryAction(String fileName) {
        Node currSuperServer = links.get(0);
        Service service = (Service) Stub.getStub(Service.class, currSuperServer);
        addSequence();
        String messageId = clientName + seqNumb;
        System.out.println("current messageId = " + messageId);
        QueryMessage queryMsg = new QueryMessage(clientIP, clientPort, fileName, seqNumb, messageId, currSuperServer);
        List<Peer> peers;
        peers = service.leafQuery(queryMsg);
        return peers;
    }

    private Response downloadAction(String fileName) {
        List<Peer> lists = leafQueryAction(fileName);
        int size = lists.size();
        System.out.println(size);
        if(size <= 0) {
            return new Response(404, "resource doesn't exist!");
        }
        Random random = new Random();
        Peer peer = lists.get(random.nextInt(size));
        System.out.println("choose = " + peer);
        String key = "h_" + peer.getPeerid();

        String ip = (String) YamlReader.getInstance().getValueByKey(key, "ip");

        Integer port = (Integer) YamlReader.getInstance().getValueByKey(key, "serverPort");

        Node node = new Node(key, ip, port, null);

        Service service = (Service) Stub.getStub(Service.class, node);

        Response res = service.obtain(peer);

        if (res.getCode() == Constant.RETRIEVE_CODE_SUC) {
            File file = null;
            // to create the file
            try {
                file = new File(path+'/'+fileName);
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
        }
        return res;
    }
    private int convertByteArrayToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }

    @Override
    public void run() {
        process();
    }

    public void process() {
        Scanner input = new Scanner(System.in);
        switch (requestIndex) {
            case 1:{
                Response response = registerAction();
                System.out.println(response);
                break;
            }
            case 2: {
                System.out.print("please input the name of the file you want to search: ");
                String fileName = input.next();
                List<Peer> peers = leafQueryAction(fileName);
                System.out.println("Following peers hold " + fileName + ":\n");
                for (Peer peer : peers) {
                    System.out.println("server id = " + peer.getPeerid() + ", file path = " + peer.getFilepath());
                }
                break;
            }

            case 3: {
                System.out.println("please input the file you want to download: ");
                String fileName = input.next();
                // 1. find all peers registering this file.
                Response res = downloadAction(fileName);
                System.out.println(res.getMessage());
                break;
            }

            case 4:{
                getFiles();
                for(File file: lists) {
                    System.out.println(file);
                }
                break;
            }
            case 5: {
                Map<String, Long> response = new HashMap<>();
                System.out.println("test registry........");
                long start = System.currentTimeMillis();
                for(int i = 0; i< 10; i++) {
                    registerAction();
                }
                long end = System.currentTimeMillis();
                long total = end - start;
                response.put("registry", total);

                String fileName = input.next();
                System.out.println("test query........");
                start = System.currentTimeMillis();
                for(int i = 0; i< 50; i++) {
                    leafQueryAction(fileName);
                }
                end = System.currentTimeMillis();
                total = end - start;
                response.put("query", total);

                System.out.println("test download........");
                start = System.currentTimeMillis();
                for(int i = 0; i< 50; i++) {
                    downloadAction(fileName);
                }
                end = System.currentTimeMillis();
                total = end - start;
                response.put("download", total);
                for(String key: response.keySet()) {
                    System.out.println(key + "==> " + response.get(key));
                }
                break;
            }
            case 6: {
                System.out.println("you exit this service!");
                input.close();
                System.exit(0);
                break;
            }
            case 7: {
                String fileName = input.next();
                Node curServer = links.get(0);
                Service service = (Service) Stub.getStub(Service.class, links.get(0));
                String[] strings = clientName.split("_");
                int clientID = Integer.parseInt(strings[strings.length - 1]);
                Response response =  service.deregister(clientID, fileName);
                if(response.getCode() == 200) {
                    System.out.println("deregister success!");
                } else{
                    System.out.println(response.getMessage());
                }
                break;
            }
            default: {
                System.err.println("Wrong action, please try again!");
                break;
            }
        }
    }
}
