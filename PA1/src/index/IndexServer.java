package index;

import peer.client.service.IndexServerService;
import util.Constant;
import util.Server;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: A centralized indexing server offering registry, search and deregister to the client.
 * @author: Xue Zhang
 * @date: 2022-02-02
 * @version: 1.0
 **/
public class IndexServer {

    private static String ip;
    private static final Map<String, Class<?>> services = new HashMap<>();

    static {
        services.put(IndexServerService.class.getName(), IndexServerServiceImpl.class);
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            //System.out.println("ip = " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Server indexServer = new Server(Constant.INDEX_SERVER_NAME, ip, Constant.INDEX_SERVER_PORT, services);
        indexServer.start();
    }
}
