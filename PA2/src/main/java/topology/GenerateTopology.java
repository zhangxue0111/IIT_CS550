package topology;

import utils.FileUtils;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.exit;

/**
 * @description: Generate a network topology.
 * @author: Xue Zhang
 * @date: 2022-02-28
 * @version: 1.0
 **/
public class GenerateTopology {

    private final Integer branchNum;
    private final Integer superPeerNum;

    private final List<Node> superPeers = new ArrayList<>();
    private final List<Node> leafNodes = new ArrayList<>();



    public GenerateTopology(Integer superPeerNum, Integer branchNum) {
        this.branchNum = branchNum;
        this.superPeerNum = superPeerNum;
    }

    public List<Node> getSuperPeers() {
        return superPeers;
    }

    public List<Node> getLeafNodes() {
        return leafNodes;
    }

    public void generatePod() throws UnknownHostException {
        int baseSuperPeerPort = 60000;
        int baseLeafServerPort = 50000;
        int baseLeafClientPort = 7000;
        int baseSuperClientPort = 8080;
        int baseLeafNum = 0;
        for (int i = 1; i <= superPeerNum; i++) {
            String superPeerName = "s_" + i;
            String ip = InetAddress.getLocalHost().getHostAddress();
            int serverPort = baseSuperPeerPort + i;
            baseSuperClientPort++;
            Node superPeerNode = new Node(superPeerName, ip, serverPort, baseSuperClientPort, new ArrayList<>());
            this.superPeers.add(superPeerNode);

            for (int j = 0; j < branchNum; j++) {
                baseLeafNum++;
                String leafNodeName = "h_" + baseLeafNum;
                ip = InetAddress.getLocalHost().getHostAddress();
                serverPort = ++baseLeafServerPort;
                baseLeafClientPort++;
                Node leafNode = new Node(leafNodeName, ip, serverPort, baseLeafClientPort, new ArrayList<>());
                this.leafNodes.add(leafNode);
                createLink(superPeerNode, leafNode);
            }
        }
    }

    public void createMeshLinksAmongSuperPeers() {
        for(Node superPeerA: superPeers) {
            for(Node superPeerB: superPeers) {
                superPeerA.getLinks().add(superPeerB);
            }
        }
    }

    public void createLinearLinksAmongSuperPeers() {
        Node next;
        for(int i = 0; i < this.superPeerNum; i++) {
            Node cur = superPeers.get(i);
            cur.getLinks().add(cur);
        }
        for(int i = 0; i< this.superPeerNum; i++) {
            if (i == 0) {
                next = this.superPeers.get(i+1);
            } else if(i == superPeerNum -1) {
                next = this.superPeers.get(i-1);
            } else{
                next = this.superPeers.get(i-1);
                this.superPeers.get(i).getLinks().add(next);
                next = this.superPeers.get(i+1);
            }
            this.superPeers.get(i).getLinks().add(next);
        }
    }

    private void createLink(Node node1, Node node2) {
        node2.getLinks().add(node1);
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("please input the topology you used. 1: linear, 2: mesh, 3: exit");
        }

        GenerateTopology topology = new GenerateTopology(10, 3);
        topology.generatePod();
        int option = Integer.parseInt(args[0]);
        switch (option) {
            case 1:
                topology.createMeshLinksAmongSuperPeers();
                break;
            case 2:
                topology.createLinearLinksAmongSuperPeers();
                break;
            case 3:
                exit(1);
                break;
            default:
                System.out.println("wrong parameters, please input again");
                break;
        }
        String path = "src/main/resources/config.yaml";
        List<Node> lists = new ArrayList<>();
        lists.addAll(topology.getSuperPeers());
        lists.addAll(topology.getLeafNodes());
        FileUtils.generateConfigFile(path, lists);
    }

}
