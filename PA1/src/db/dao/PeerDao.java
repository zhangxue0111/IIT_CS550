package db.dao;

import db.bean.Peer;

import java.sql.Connection;
import java.util.List;

/**
 * @description: Defines methods when accessing the peer table.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public interface PeerDao {

    void insert(Connection conn, Peer peer);

    void deleteById(Connection conn,int id);

    Peer getPeerByID(Connection conn,int id);

    List<Peer> getAll(Connection conn);

    List<Peer> getPeerByFileName(Connection conn, String name);

    Peer getPeerByAll(Connection conn, Peer peer);

    List<Peer> getPeerByPeerIdAndFileName(Connection conn, Integer peerId, String fileName);

}
