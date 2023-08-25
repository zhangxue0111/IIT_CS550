package db.bean;

/**
 * @description: A register object containing service's name, ip and port.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public class Register {

    private Integer id;
    private String servername;
    private String name;
    private String ip;
    private Integer port;

    public Register() {
    }

    public Register(Integer id, String servername, String name, String ip, Integer port) {
        this.id = id;
        this.servername = servername;
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Register{" +
                "id=" + id +
                ", servername='" + servername + '\'' +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
