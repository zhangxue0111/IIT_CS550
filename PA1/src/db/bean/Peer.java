package db.bean;

import java.io.Serializable;

/**
 * @description: A Peer file object.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public class Peer implements Serializable {
    private static final long serialVersionID = 1L;

    private Integer id;
    private Integer peerid;
    private String peername;
    private String filename;
    private String filepath;
    private Integer filesize;

    public Peer() {
    }

    public Peer(Integer id, Integer peerid, String peername, String filename, String filepath, Integer filesize) {
        this.id = id;
        this.peerid = peerid;
        this.peername = peername;
        this.filename = filename;
        this.filepath = filepath;
        this.filesize = filesize;

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

    public Integer getFilesize() {
        return filesize;
    }

    public void setFilesize(Integer filesize) {
        this.filesize = filesize;
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
                '}';
    }
}
