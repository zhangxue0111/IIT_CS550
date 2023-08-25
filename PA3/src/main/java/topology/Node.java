package topology;

import java.io.Serializable;
import java.util.List;

/**
 * @description: A node entity in the network.
 * @author: Xue Zhang
 * @date: 2022-02-28
 * @version: 1.0
 **/
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nodeName;
    private String ip;
    private Integer serverPort;
    private Integer clientPort;
    private List<Node> links;

    public Node() {
    }

    public Node(String nodeName, String ip, Integer serverPort, Integer clientPort) {
        this.nodeName = nodeName;
        this.ip = ip;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
    }

    public Node(String nodeName, String ip, Integer serverPort, Integer clientPort, List<Node> links) {
        this.nodeName = nodeName;
        this.ip = ip;
        this.serverPort = serverPort;
        this.clientPort = clientPort;
        this.links = links;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public List<Node> getLinks() {
        return links;
    }

    public void setLinks(List<Node> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeName='" + nodeName + '\'' +
                ", ip='" + ip + '\'' +
                ", serverPort=" + serverPort +
                ", clientPort=" + clientPort +
                '}';
    }
}
