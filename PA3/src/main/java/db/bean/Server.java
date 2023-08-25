package db.bean;

/**
 * @description: A server object registered on the service center.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public class Server {
    private Integer id;
    private String name;
    private String ip;
    private Integer port;
    private String status;
    private String service;

    public Server() {
    }

    public Server(Integer id, String name, String ip, Integer port, String status, String service) {
        this.id = id;
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.status = status;
        this.service = service;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", status='" + status + '\'' +
                ", service='" + service + '\'' +
                '}';
    }
}
