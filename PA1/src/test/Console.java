package test;

import peer.Peer;

/**
 * @description: test console.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class Console {
    public static void main(String[] args) {
        Peer p1 = new Peer(1, "p1", 8081,
                "src/dir/peer1");
        Peer p2 = new Peer(2, "p2", 8082,
                "src/dir/peer2");
        Peer p3 = new Peer(3, "p3", 8083,
                "src/dir/peer3");
        p1.start();
        p2.start();
        p3.start();
    }

}
