import Server.ServerThread;
import topology.Node;
import utils.YamlReader;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: A start program for this project
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public class Application {

    private static final List<Node> superPeers = new ArrayList<>();

    public static void readConfig() {
        for(int i = 1; i < 11; i++) {
            Node superPeer = new Node();
            String key = "superPeer_" + i;
            String ip = (String) YamlReader.getInstance().getValueByKey(key, "ip");
            Integer serverPort = (Integer) YamlReader.getInstance().getValueByKey(key, "serverPort");
            Integer clientPort = (Integer) YamlReader.getInstance().getValueByKey(key, "clientPort");
            Object links = YamlReader.getInstance().getValueByKey(key, "links");
            List<Node> nodes = YamlReader.convertMapToObject(links);
            superPeer.setIp(ip);
            superPeer.setClientPort(clientPort);
            superPeer.setNodeName(key);
            superPeer.setServerPort(serverPort);
            superPeer.setLinks(nodes);
            superPeers.add(superPeer);
        }

    }

    public static void main(String[] args) throws InterruptedException {
        readConfig();
        for(Node node: superPeers) {
            ServerThread server = new ServerThread(node.getNodeName(), node.getIp(), node.getServerPort());
            server.addServer();
            server.start();
        }
    }
}
