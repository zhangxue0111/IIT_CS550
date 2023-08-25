package Server;

import db.bean.Peer;
import db.dao.PeerDao;
import db.dao.PeerDaoImpl;
import db.util.JDBCUtils;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @description: // TODO
 * @author: Xue Zhang
 * @date: 2022-03-22
 * @version: 1.0
 */
public class Sender implements Callable<List<Peer>> {

    private static final PeerDao dao = new PeerDaoImpl();

    private final String serverName;
    private final String fileName;

    public Sender(String serverName, String fileName) {
        this.serverName = serverName;
        this.fileName = fileName;
    }

    private synchronized List<Peer> getLocalFiles(Connection conn){
        return dao.getPeerByFileNameAndSeverName(conn, serverName, fileName);
    }

    @Override
    public List<Peer> call() throws Exception {
        Connection conn = null;
        List<Peer> leafNodes = null;
        try {
            conn = JDBCUtils.getConnection();
            leafNodes = getLocalFiles(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return leafNodes;
    }
}
