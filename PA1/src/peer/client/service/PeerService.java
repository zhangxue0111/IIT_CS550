package peer.client.service;

import util.Response;

/**
 * @description: The peer provides services for clients.
 * @author: Xue Zhang
 * @date: 2022-02-07
 * @version: 1.0
 **/
public interface PeerService {
    Response retrieve(String fileName);
}
