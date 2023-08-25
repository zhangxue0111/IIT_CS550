import client.ClientThread;
import server.ServerThread;
import topology.GenerateTopology;
import topology.Node;
import utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * @description: A start program for this project
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public class Application {

    public static void main(String[] args) throws IOException, InterruptedException {
        // 1. generate network topology.
        GenerateTopology topology = new GenerateTopology(10, 3);
        topology.generatePod();
        System.out.println("*******************************************************");
        System.out.println("1 : Linear topology.");
        System.out.println("2 : Mesh topology.");
        System.out.println("3 : Exit.");
        System.out.println("*******************************************************");
        System.out.println("please input you option: ");
        Scanner input = new Scanner(System.in);
        int option = input.nextInt();
        switch (option) {
            case 1:
                topology.createLinearLinksAmongSuperPeers();
                break;
            case 2:
                topology.createMeshLinksAmongSuperPeers();
                break;
            case 3:
                exit(1);
                break;
            default:
                System.err.println("wrong parameters, please input again");
                break;
        }
        String path = "src/main/resources/config.yaml";
        List<Node> lists = new ArrayList<>();
        lists.addAll(topology.getSuperPeers());
        lists.addAll(topology.getLeafNodes());

        FileUtils.generateConfigFile(path, lists);
        System.out.println("Generate topology success!");

        // 2. start all servers in the network
        for(Node node: lists) {
            ServerThread server = new ServerThread(node.getServerPort());
            server.setName(node.getNodeName());
            server.start();
        }

        // 3. create share directory for every leaf node.
        FileUtils.generateFiles(topology.getLeafNodes());

        //4. choose one client to work.
        System.out.println("all leaf nodes in this topology: ");
        List<Node> leafNodes = topology.getLeafNodes();
        for(Node node: leafNodes) {
            System.out.print(node.getNodeName() + " ");
        }
        System.out.println();
        Node leafNode = null;
        while (true) {
            System.out.println("please input the name of the host: ");
            String nodeName = input.next();
            for (Node node : leafNodes) {
                if (node.getNodeName().equals(nodeName)) {
                    leafNode = node;
                    System.out.println(nodeName + " has been chosen");
                    break;
                }
            }
            if (leafNode == null) {
                System.out.println("Please choose the leaf node you want to use: ");
            } else{
                break;
            }
        }
        while (true) {
            System.out.println("*******************************************************");
            System.out.println("Welcome to peer to peer distributed system.");
            System.out.println("Current client is " + leafNode.getNodeName());
            System.out.println("1: Register all files of the working directory.");
            System.out.println("2: Query a file from network.");
            System.out.println("3: Download file from a specific node.");
            System.out.println("4: List all files of the current directory.");
            System.out.println("5: Calculate the performance of query requests.");
            System.out.println("6: To exit the program.");
            System.out.println("7: Deregister the shared file.");
            System.out.println("*******************************************************");
            System.out.println("please input you request index: ");
            int requestIndex = input.nextInt();
            ClientThread client = new ClientThread(leafNode.getNodeName(), leafNode.getIp(),
                    leafNode.getClientPort(), leafNode.getLinks(), requestIndex);
            client.start();
            client.join();
            System.out.println("execution success!");
        }
    }

}
