package server;

import client.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: An abstract of server function
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class ServerThread extends Thread{

    private static final Map<String, Class<?>> services = new HashMap<>();

    private final Integer serverPort;

    static {
        services.put(Service.class.getName(), ServiceImpl.class);
    }

    public ServerThread (Integer serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public void run() {
        Rpc rpc = new Rpc(services, serverPort);
        try {
            rpc.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
