package index;

import peer.client.service.IndexServerService;
import util.Response;
import db.bean.Peer;
import db.dao.PeerDao;
import db.dao.PeerDaoImpl;
import db.util.JDBCUtils;

import java.io.File;
import java.sql.Connection;
import java.util.List;


/**
 * @description: The implementation of all services offered by index server.
 * @author: Xue Zhang
 * @date: 2022-02-09
 * @version: 1.0
 **/
public class IndexServerServiceImpl implements IndexServerService {

    private static final  PeerDao dao = new PeerDaoImpl();

    @Override
    public Response registry(Integer peerId, String peerName, List<File> lists) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            int i = 1;
            for(File file: lists) {
                Peer peer = new Peer(null, peerId, peerName, file.getName(), file.getPath(), i++);
                if(dao.getPeerByAll(conn, peer) == null) {
                    dao.insert(conn, peer);
                }
            }
            Response response;
            response = new Response(200, "register success");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
        return new Response(401, "register failed!");
    }

    @Override
    public List<Peer> search(String fileName) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            //System.out.println("file name = " + fileName);
            List<Peer> lists;
            lists = dao.getPeerByFileName(conn, fileName);
            return lists;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }

    // added by Yihua
    @Override
    public Response deregister(Integer peerId, String fileName) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            // 1. checking whether the file to be deleted exists
            List<Peer> lists = dao.getPeerByPeerIdAndFileName(conn, peerId, fileName);
            if(lists.isEmpty()) {
                //System.out.println("the file you want to deregister doesn't exist!");
                return new Response(303, "no such a registered file");
            }
            // 2. exits:
            for (Peer peer: lists){
                dao.deleteById(conn, peer.getId());
            }
            return new Response(200, "deregister operation is over");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }
}
