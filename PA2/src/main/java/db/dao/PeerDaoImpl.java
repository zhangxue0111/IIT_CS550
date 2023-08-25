package db.dao;

import db.bean.Peer;

import java.sql.Connection;
import java.util.List;

/**
 * @description: The implementation of all peer methods.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public class PeerDaoImpl extends BaseDAO<Peer> implements PeerDao{

    @Override
    public void insert(Connection conn, Peer peer) {
        String sql = "insert into peer(peerid,peername,filename,filepath, filesize) values(?,?,?,?,?)";
        update(conn, sql, peer.getPeerid(), peer.getPeername(), peer.getFilename(), peer.getFilepath(), peer.getFilesize());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from peer where id = ?";
        update(conn, sql, id);
    }

    @Override
    public void deleteByServerName(Connection conn, String serverName, Integer clientID) {
        String sql = "delete from peer where peername = ? and peerid=?";
        update(conn, sql, serverName, clientID);
    }

    @Override
    public Peer getPeerByID(Connection conn, int id) {
        String sql = "select * from peer where id = ?";
        Peer peer;
        peer = getInstance(conn, sql,id);
        return peer;
    }

    @Override
    public List<Peer> getAll(Connection conn) {
        String sql = "select * from peer";
        List<Peer> list = getForList(conn, sql);
        //System.out.println(list);
        return list;
    }

    @Override
    public List<Peer> getPeerByFileName(Connection conn, String name) {
        String sql = "select * from peer where filename = ?";
        List<Peer> list = getForList(conn, sql, name);
        //System.out.println(list);
        return list;
    }

    @Override
    public List<Peer> getPeerByFileNameAndSeverName(Connection conn, String serverName, String fileName) {
        String sql = "select * from peer where peername =? and filename = ?";
        List<Peer> list = getForList(conn, sql, serverName, fileName);
        //System.out.println(list);
        return list;
    }

    @Override
    public Peer getPeerByAll(Connection conn, Peer peer) {
        String sql = "select * from peer where peerid=? and peername=? and filename=? and filepath=?";
        Peer newPeer = getInstance(conn, sql, peer.getPeerid(), peer.getPeername(), peer.getFilename(), peer.getFilepath());
        //System.out.println(newPeer);
        return newPeer;
    }

    @Override
    public List<Peer> getPeerByPeerIdAndFileName(Connection conn, Integer peerId, String fileName) {
        String sql = "select * from peer where peerid=? and filename=?";
        List<Peer> lists;
        lists = getForList(conn, sql, peerId, fileName);
        //System.out.println(lists);
        return lists;
    }
}
