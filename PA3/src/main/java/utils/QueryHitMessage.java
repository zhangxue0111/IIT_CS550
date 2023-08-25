package utils;

import db.bean.Peer;

import java.io.Serializable;
import java.util.List;

/**
 * @description: An object of Query Hit Message.
 * @author: Xue Zhang
 * @date: 2022-03-04
 * @version: 1.0
 **/
public class QueryHitMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private String clientName;
    private String serverName;
    private List<Peer> matches;
    private String messageId;
    private String fileName;
    private Integer sequenceNum;


    public QueryHitMessage() {

    }

    public QueryHitMessage(String clientName, String serverName, List<Peer> matches, String messageId, String fileName,
                           Integer sequenceNum) {
        this.clientName = clientName;
        this.serverName = serverName;
        this.matches = matches;
        this.messageId = messageId;
        this.fileName = fileName;
        this.sequenceNum = sequenceNum;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
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

    public Integer getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(Integer sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    @Override
    public String toString() {
        return "QueryHitMessage{" +
                "clientName='" + clientName + '\'' +
                ", serverName='" + serverName + '\'' +
                ", matches=" + matches +
                ", messageId='" + messageId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", sequenceNum=" + sequenceNum +
                '}';
    }
}
