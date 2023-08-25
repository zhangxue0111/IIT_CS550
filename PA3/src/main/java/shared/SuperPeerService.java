package shared;

import db.bean.Peer;
import utils.InvalidationMessage;
import utils.QueryHitMessage;
import utils.QueryMessage;
import utils.Response;

import java.io.File;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * @description: A set of services offered by super peer servers.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public interface SuperPeerService {

    Response registry(Integer clientID, String serverName, File file);

    Response registryAll(Integer clientID, String serverName, List<File> files);

    Response deregister(Integer clientID, String fileName, Boolean isUpdate);

    List<Peer> queryFiles(QueryMessage message);

    void pushInvalidation(InvalidationMessage message);

    Response pollVersion(File file, int peerId);

}
