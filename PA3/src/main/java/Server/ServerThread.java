package Server;

import db.bean.Server;
import shared.LeafNodeService;
import shared.SuperPeerService;
import utils.Constants;
import utils.ServiceCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: An abstract of server function
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class ServerThread extends Thread{

    private static final Map<String, Class<?>> services = new HashMap<>();

    private static final ServiceCenter serviceCenter = new ServiceCenter();

    private final String serverName;

    private final String serverIP;

    private final Integer serverPort;


    public ServerThread (String serverName, String serverIP, Integer serverPort) {
        this.serverName = serverName;
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void addServer(){
        if(serverName.startsWith("super")) {
            services.put(SuperPeerService.class.getName(), SuperPeerServiceImpl.class);
        } else {
            services.put(LeafNodeService.class.getName(), LeafNodeServiceImpl.class);
        }
        for(String key: services.keySet()) {
            serviceCenter.addServer(serverName, serverIP, serverPort, "active", key);
        }
    }

    @Override
    public void run() {

        // 1. create a server
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();

        ServerSocket serverSocket;

        try {
            serverSocket = new ServerSocket(serverPort);

            serverSocket.setSoTimeout(Constants.SOCKET_SO_TIMEOUT);

            while (true) {
                final Socket socket = serverSocket.accept();

                newCachedThreadPool.execute(new Skeleton(socket, services));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
