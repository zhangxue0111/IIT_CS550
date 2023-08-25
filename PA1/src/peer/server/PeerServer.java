package peer.server;

import peer.client.service.PeerService;
import util.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: A peer serving as a server.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class PeerServer extends Thread{

    private static String ip;
    private static final Map<String, Class<?>> services = new HashMap<>();

    private final String peerServerName;
    private final Integer peerPort;

    static {
        services.put(PeerService.class.getName(), PeerServiceImpl.class);
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
            // System.out.println("ip = " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public PeerServer (String peerServerName, Integer peerPort) {
        this.peerServerName = peerServerName;
        this.peerPort = peerPort;
    }

    @Override
    public void run() {
        Server peerServer = new Server(peerServerName, ip, peerPort, services);;
        try {
            peerServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
