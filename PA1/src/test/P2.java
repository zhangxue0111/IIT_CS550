package test;

import peer.Peer;

/**
 * @description: Peer 2 client.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class P2 {

    public static void main(String[] args) {
        Peer p2 = new Peer(2, "p2", 8082,
                "src/dir/peer1");
        p2.start();
    }
}
