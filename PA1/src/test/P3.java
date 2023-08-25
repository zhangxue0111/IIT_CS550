package test;

import peer.Peer;

/**
 * @description: Peer 3 client.
 * @author: Xue Zhang
 * @date: 2022-02-15
 * @version: 1.0
 **/
public class P3 {
    public static void main(String[] args) {
        Peer p3 = new Peer(3, "p3", 8083,
                "src/dir/peer3");
        p3.start();
    }
}
