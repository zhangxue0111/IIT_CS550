package db.bean;

import java.io.Serializable;

/**
 * @description: A Peer file object.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.1
 **/
public class Peer implements Serializable {

    private static final long serialVersionID = 1L;

    private Integer id;
    private Integer peerid;
    private String peername;
    private String filename;
    private String filepath;
    private Long filesize;
    private Integer version;
    private String type;
    private String state;
    private String ttr;

    public Peer() {
    }

    public Peer(Integer id, Integer peerid, String peername, String filename, String filepath) {
        this.id = id;
        this.peerid = peerid;
        this.peername = peername;
        this.filename = filename;
        this.filepath = filepath;
    }

    public Peer(Integer id, Integer peerid, String peername, String filename, String filepath, Long filesize) {
        this.id = id;
        this.peerid = peerid;
        this.peername = peername;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
    }

    public Peer(Integer id, Integer peerid, String peername, String filename, String filepath, Long filesize,
                Integer version, String type) {
        this.id = id;
        this.peerid = peerid;
        this.peername = peername;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
        this.version = version;
        this.type = type;
    }
    public Peer(Integer id, Integer peerid, String peername, String filename, String filepath, Long filesize,
                Integer version, String type, String state) {
        this.id = id;
        this.peerid = peerid;
        this.peername = peername;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;
        this.version = version;
        this.type = type;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPeerid() {
        return peerid;
    }

    public void setPeerid(Integer peerid) {
        this.peerid = peerid;
    }

    public String getPeername() {
        return peername;
    }

    public void setPeername(String peername) {
        this.peername = peername;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Long getFilesize() {
        return filesize;
    }

    public void setFilesize(Long filesize) {
        this.filesize = filesize;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getTtr() {
        return ttr;
    }

    public void setTtr(String ttr) {
        this.ttr = ttr;
    }

    @Override
    public String toString() {
        return "Peer{" +
                "id=" + id +
                ", peerid=" + peerid +
                ", peername='" + peername + '\'' +
                ", filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", filesize=" + filesize +
                ", version=" + version +
                ", type='" + type + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
