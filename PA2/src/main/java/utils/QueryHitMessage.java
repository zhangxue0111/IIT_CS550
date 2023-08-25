package utils;

import db.bean.Peer;

import java.io.Serializable;
import java.util.List;

/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class QueryHitMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientIP;
    private Integer clientPort;
    private Integer serverPort;
    private List<Peer> matches;
    private String messageId;
    private String fileName;


    public QueryHitMessage() {

    }

    public QueryHitMessage(String clientIP, Integer clientPort, Integer serverPort, String messageId, String fileName) {
        this.clientIP = clientIP;
        this.clientPort = clientPort;
        this.serverPort = serverPort;
        this.messageId = messageId;
        this.fileName = fileName;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setClientIP(String clientIP) {
        this.clientIP = clientIP;
    }

    public Integer getClientPort() {
        return clientPort;
    }

    public void setClientPort(Integer clientPort) {
        this.clientPort = clientPort;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public List<Peer> getMatches() {
        return matches;
    }

    public void setMatches(List<Peer> matches) {
        this.matches = matches;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "QueryHitMessage{" +
                "clientIP='" + clientIP + '\'' +
                ", clientPort=" + clientPort +
                ", serverPort=" + serverPort +
                ", messageId='" + messageId + '\'' +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
