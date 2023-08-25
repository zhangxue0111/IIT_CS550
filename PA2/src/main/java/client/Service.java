package client;

import db.bean.Peer;
import utils.QueryHitMessage;
import utils.QueryMessage;
import utils.Response;

import java.io.File;
import java.util.List;

/**
 * @description: Services interface used by remote clients.
 * @author: Xue Zhang
 * @date: 2022-03-01
 * @version: 1.0
 **/
public interface Service {

    Response registry(Integer clientID, String leafName, List<File> fileName);

    List<Peer> leafQuery(QueryMessage message);

    QueryHitMessage queryForward (QueryMessage message);

    Response obtain (Peer peer);

    Response deregister(Integer clientID, String fileName);

}
