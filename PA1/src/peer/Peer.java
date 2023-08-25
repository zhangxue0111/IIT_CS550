package peer;


import peer.client.PeerClient;
import peer.server.PeerServer;

/**
 * @description: A peer node which serving as a client and a server at same time.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class Peer {
    public final Integer peerId;
    public final String peerName;
    public final Integer peerPort;
    public final String filePath;

    public Peer(Integer peerId, String peerName, Integer peerPort, String filePath) {
        this.peerId = peerId;
        this.peerName = peerName;
        this.peerPort = peerPort;
        this.filePath = filePath;
    }

    public void start(){
        PeerClient peerClient = new PeerClient(peerId, peerName, filePath);
        PeerServer peerServer = new PeerServer(peerName, peerPort);
        peerClient.setName(peerName);
        peerClient.start();
        peerServer.start();
    }

}
