package server;


import server.Skeleton;
import topology.Node;
import utils.Constant;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description: Communication between a client and a server.
 * @author: Xue Zhang
 * @date: 2022-02-09
 * @version: 1.0
 **/
public class Rpc {

    private final Map<String, Class<?>> services;
    private final Integer port;

    public Rpc(Map<String, Class<?>> services, Integer port) {
        this.services = services;
        this.port = port;
    }

    public void start() throws IOException {
        // 1. create a server
        ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ServerSocket serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(Constant.SOCKET_SO_TIMEOUT);
        // System.out.println("The server is booting.....");

        // 2. wait for the client's connection
        while (true) {
            // System.out.println("waiting for connection....");
            // 3. one client connecting with the server
            final Socket socket = serverSocket.accept();
            //System.out.println("there is one client connecting with the server.");

            // 4. create a thread to communicate between the client and the server
            newCachedThreadPool.execute(new Skeleton(socket, services));
        }
    }
}
