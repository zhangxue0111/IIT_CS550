package db.util;

import db.bean.Peer;
import db.dao.PeerDao;
import db.dao.PeerDaoImpl;

import java.sql.Connection;
import java.util.List;

/**
 * @description: TODO
 * @author: Xue Zhang
 * @date: 2022-03-05
 * @version: 1.0
 **/
public class TestDb {

    private static final PeerDao dao = new PeerDaoImpl();

    private static List<Peer> localQuery(String serverName, String filename) {
        Connection conn = null;
        List<Peer> leafNodes = null;
        try {
            conn = JDBCUtils.getConnection();
            leafNodes = dao.getPeerByFileNameAndSeverName(conn, serverName, filename);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return leafNodes;
    }

    public static void main(String[] args) {
        List<Peer> lists = localQuery("s_3", "foo.txt");
        for(Peer peer: lists) {
            System.out.println(peer);
        }

    }


}
