package util;

import java.io.IOException;
import java.util.Map;

/**
 * @description: An abstract of server function
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class Server {
    private final String serverName;
    private final String ip;
    private final Integer port;
    private final Map<String, Class<?>> services;

    public Server(String serverName, String ip, Integer port, Map<String, Class<?>> services) {
        this.serverName = serverName;
        this.ip = ip;
        this.port = port;
        this.services = services;
    }

    public void start() throws IOException {
        ServiceCenter serviceCenter = new ServiceCenter();
        for(String key: services.keySet()) {
            serviceCenter.register(serverName, key, ip, port);
            //System.out.println("execute register center...");
        }
        Rpc rpc = new Rpc(services, port);
        rpc.start();
    }
}
