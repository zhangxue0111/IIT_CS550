package utils;

import topology.Node;

import java.io.Serializable;


/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class QueryMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final byte DEFAULT_TTL = 3;

    private String clientIP;
    private Integer clientPort;
    private String fileName;
    private Integer sequenceNum;
    private Byte ttl;
    private Node target;
    private String messageId;

    public QueryMessage() {
    }

    public QueryMessage(String clientIP, Integer clientPort, String fileName, Integer sequenceNum, String messageId,Node target) {
        this.clientIP = clientIP;
        this.clientPort = clientPort;
        this.fileName = fileName;
        this.sequenceNum = sequenceNum;
        this.messageId = messageId;
        this.ttl = DEFAULT_TTL;
        this.target = target;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public Node getTarget() {
        return target;
    }

    public void setTarget(Node target) {
        this.target = target;
    }

    public Byte updateTTL() {
        this.ttl--;
        return this.ttl;
    }

    public Byte getTtl() {
        return ttl;
    }

    public void setTtl(Byte ttl) {
        this.ttl = ttl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "QueryMessage{" +
                "clientIP='" + clientIP + '\'' +
                ", clientPort=" + clientPort +
                ", fileName='" + fileName + '\'' +
                ", sequenceNum=" + sequenceNum +
                ", ttl=" + ttl +
                ", target=" + target +
                '}';
    }
}
