package utils;

import topology.Node;

import java.io.Serializable;
import java.util.List;


/**
 * @description: A query message from leaf node to super peer.
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class QueryMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final byte DEFAULT_TTL = 9;

    private String clientName;
    private String fileName;
    private Integer sequenceNum;
    private String messageId;
    private List<Node> links;
    private Byte ttl;


    public QueryMessage() {
    }

    public QueryMessage(String clientName, String messageId, List<Node> links) {
        this.clientName = clientName;
        this.messageId = messageId;
        this.links = links;
        this.ttl = DEFAULT_TTL;
    }

    public QueryMessage(String clientName, String fileName, Integer sequenceNum, String messageId, List<Node> links) {
        this.clientName = clientName;
        this.fileName = fileName;
        this.sequenceNum = sequenceNum;
        this.messageId = messageId;
        this.links = links;
        this.ttl = DEFAULT_TTL;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
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

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Byte getTtl() {
        return ttl;
    }

    public void setTtl(Byte ttl) {
        this.ttl = ttl;
    }

    public void updateTTL() {
        this.ttl--;
    }

    public List<Node> getLinks() {
        return links;
    }

    public void setLinks(List<Node> links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "QueryMessage{" +
                "clientName='" + clientName + '\'' +
                ", fileName='" + fileName + '\'' +
                ", sequenceNum=" + sequenceNum +
                ", messageId='" + messageId + '\'' +
                ", links=" + links +
                ", ttl=" + ttl +
                '}';
    }
}
