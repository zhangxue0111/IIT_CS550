import client.LeafNodeClient;
import topology.Node;
import utils.YamlReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @description: A console to run clients to test system.
 * @author: Xue Zhang
 * @date: 2022-03-22
 * @version: 1.0
 */
public class RunClient {
    private static final List<Node> leafNodes = new ArrayList<>();

    private static void readConfig() {
        for(int i = 1; i < 31; i++) {
            Node leafNode = new Node();
            String key = "leafNode_" + i;
            String ip = (String) YamlReader.getInstance().getValueByKey(key, "ip");
            Integer serverPort = (Integer) YamlReader.getInstance().getValueByKey(key, "serverPort");
            Integer clientPort = (Integer) YamlReader.getInstance().getValueByKey(key, "clientPort");
            Object links = YamlReader.getInstance().getValueByKey(key, "links");
            List<Node> nodes = YamlReader.convertMapToObject(links);
            leafNode.setIp(ip);
            leafNode.setClientPort(clientPort);
            leafNode.setNodeName(key);
            leafNode.setServerPort(serverPort);
            leafNode.setLinks(nodes);
            leafNodes.add(leafNode);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Scanner input = new Scanner(System.in);
        readConfig();
        while (true) {
            System.out.println("choose the client: ");
            String leafNodeName = input.next();
            for (Node node : leafNodes) {
                if(node.getNodeName().equals(leafNodeName)) {
                    LeafNodeClient client = new LeafNodeClient(node.getNodeName(), node.getLinks());
                    System.out.println(client);
                    client.start();
                    client.join();
                }
            }
        }
    }
}
