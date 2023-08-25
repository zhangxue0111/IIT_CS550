package peer.client.service;

import db.bean.Peer;
import util.Response;

import java.io.File;
import java.util.List;

/**
 * @description: The indexing server provides services for clients.
 * @author: Xue Zhang
 * @date: 2022-02-06
 * @version: 1.0
 **/
public interface IndexServerService {
    Response registry(Integer peerId, String peerName, List<File> lists);
    List<Peer> search(String fileName);
    Response deregister(Integer peerId, String fileName);
}
