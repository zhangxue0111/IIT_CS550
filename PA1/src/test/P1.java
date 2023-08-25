package test;

import peer.Peer;

/**
 * @description: Peer 1 client
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class P1 {
    public static void main(String[] args) {
        Peer p1 = new Peer(1, "p1", 8081,
                "src/dir/peer1");
        p1.start();
    }
}
