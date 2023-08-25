package peer.client;
import db.bean.Peer;
import peer.client.service.IndexServerService;
import peer.client.service.PeerService;
import util.Response;
import util.ServiceCenter;
import java.io.File;
import java.util.*;

/**
 * @description: A peer serving as a client.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class PeerClient extends Thread {

    private final String path;
    private final Integer peerId;
    private final String peerName;

    private final List<File> lists = new ArrayList<>();
    private final List<Peer> peers = new ArrayList<>();

    public final Map<String, Long> result = new HashMap<>();

    public PeerClient(Integer peerId, String peerName, String filePath) {
        this.peerId = peerId;
        this.path = filePath;
        this.peerName = peerName;
        Map<String, Integer> result = new HashMap<>();
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

    public void register(IndexServerService indexServerService) {
        getFiles();
        System.out.println(lists);
        Response response;
        response = indexServerService.registry(peerId, peerName, lists);
        if(response.getCode() == 200) {
            System.out.println("register success!");
        } else{
            System.out.println("register fail!");
        }
    }

    public void lookup(IndexServerService indexServerService, String fileName){
        List<Peer> lists = indexServerService.search(fileName);
        if(lists.isEmpty()){
            System.out.println("no such a file being registered.");
        }
        for(Peer peer: lists){
            System.out.println("peerId = " + peer.getPeerid() + ", path = " + peer.getFilepath());
        }
    }

    @Override
    public void run() {
        process();
    }

    public void process() {
        ServiceCenter serviceCenter = new ServiceCenter();
        Stub stub = new Stub(serviceCenter);
        long start = System.currentTimeMillis();
        IndexServerService indexServerService = (IndexServerService) stub.getStub(IndexServerService.class,
                "IndexServer");
        long end = System.currentTimeMillis();
        long base = end - start;
        System.out.println("Welcome to peer to peer distributed system.");
        Scanner input = new Scanner(System.in);
        while (true) {
            System.out.println("Current client is " + Thread.currentThread().getName());
            System.out.println("please input you request index: " +
                    "1: register, 2: search, 3: deregister, 4: retrieve, 5: exit");
            int requestIndex = input.nextInt();
            switch (requestIndex) {
                case 1: {
                    start = System.currentTimeMillis();
                    register(indexServerService);
                    end = System.currentTimeMillis();
                    result.put("registry", base + end - start);
                    break;
                }
                case 2: {
                    System.out.print("please input the name of the file you search: ");
                    String fileName = input.next();
                    start = System.currentTimeMillis();
                    lookup(indexServerService, fileName);
                    end = System.currentTimeMillis();
                    result.put("search", base + end - start);
                    break;
                }
                case 3: {
                    System.out.println("this is deregister.");
                    String fileName = input.next();
                    start = System.currentTimeMillis();
                    Response response = indexServerService.deregister(peerId, fileName);
                    end = System.currentTimeMillis();
                    result.put("deregister", base + end - start);
                    if(response.getCode() == 200) {
                        System.out.println("deregister success!");
                    } else{
                        System.out.println(response.getMessage());
                    }
                    break;
                }
                case 4: {
                    System.out.println("this is a request to download files from other peer.");
                    System.out.println("please input the file you want to download:");
                    String fileName = input.next();
                    start = System.currentTimeMillis();
                    // 1. find all peers registering this file.
                    List<Peer> lists = indexServerService.search(fileName);
                    // 2. excluded myself
                    List<Peer> availablePeers = new ArrayList<>();
                    for(Peer peer: lists){
                        if(!Objects.equals(peer.getPeerid(), peerId)) {
                            availablePeers.add(peer);
                        }
                    }
                    System.out.println(availablePeers);
                    if(availablePeers.isEmpty()) {
                        System.out.println("the file you want to download is not registered by other peers");
                    } else{
                        int size = availablePeers.size();
                        Random random = new Random();
                        Peer peer = availablePeers.get(random.nextInt(size));
                        System.out.println("choose = " + peer);
                        PeerService peerService = (PeerService) stub.getStub(PeerService.class,
                                peer.getPeername());
                        peerService.retrieve(fileName);
                    }
                    end = System.currentTimeMillis();
                    result.put("retrieve", base + end - start);
                    break;
                }
                case 5:
                    System.out.println("you exit this service!");
                    System.exit(0);
                default: {
                    System.out.println("Wrong action, please try again!");
                    break;
                }
            }
            for(String name: result.keySet()) {
                System.out.println(Thread.currentThread().getName() + ": operation = " + name  + ", cost time = " + result.get(name));
            }
        }
    }
}
