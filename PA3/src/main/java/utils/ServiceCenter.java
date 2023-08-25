package utils;

import db.bean.Server;
import db.dao.ServerDao;
import db.dao.ServerDaoImpl;
import db.util.JDBCUtils;

import java.sql.Connection;

/**
 * @description: A collection of all services being offered by servers.
 * @author: Xue Zhang
 * @date: 2022-02-09
 * @version: 1.0
 **/
public class ServiceCenter {

    private final ServerDao dao = new ServerDaoImpl();

    public void addServer(String serverName, String serverIp, Integer serverPort, String status, String service) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Server server = new Server(null, serverName, serverIp, serverPort, status, service);
            Server serverIgnoreStatue = dao.getServerIgnoreStatue(conn, server);
            if(serverIgnoreStatue == null) {
                dao.insert(conn, server);
            } else {
                if(serverIgnoreStatue.getStatus().equals("close")) {
                    serverIgnoreStatue.setStatus("active");
                    dao.update(conn, serverIgnoreStatue);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
    }

    public Server subscribe(String serverName, String serviceName) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Server registeredServiceName = dao.getServerByNameAndService(conn, serverName, serviceName);
            if(registeredServiceName == null) {
                return null;
            }
            return registeredServiceName;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }

    public void shutdownServer(Server server) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Server currServer = dao.getServerIgnoreStatue(conn, server);
            if(currServer != null && currServer.getStatus().equals("active")) {
                currServer.setStatus("close");
                dao.update(conn, currServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
    }

    public void deleteServer(Server server) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Server currServer = dao.getServerIgnoreStatue(conn, server);
            if(currServer != null ) {
                dao.update(conn, currServer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
    }

}
