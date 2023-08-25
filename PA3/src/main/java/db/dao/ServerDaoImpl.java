package db.dao;

import db.bean.Server;

import java.sql.Connection;

/**
 * @description: The implementation of all server's operation.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public class ServerDaoImpl extends BaseDAO<Server> implements ServerDao{
    @Override
    public void insert(Connection conn, Server server) {
        System.out.println(server.getName());
        System.out.println(server.getService());
        String sql = "insert into server(name,ip,port,status,service) values(?,?,?,?,?)";
        update(conn, sql,server.getName(), server.getIp(), server.getPort(), server.getStatus(), server.getService());
    }

    @Override
    public Server getServerByNameAndService(Connection conn, String serverName, String serviceName) {
        String sql = "select * from server where name=? and service = ?";
        Server instance;
        instance = getInstance(conn, sql, serverName, serviceName);
        return instance;
    }

    @Override
    public Server getServerIgnoreStatue(Connection conn, Server server) {
        String sql = "select * from server where name=? and ip=? and port=? and service = ?";
        Server instance;
        instance = getInstance(conn, sql, server.getName(), server.getIp(), server.getPort(), server.getService());
        return instance;
    }

    @Override
    public void update(Connection conn, Server server) {
        String sql = "update server set status = ? where name = ? and ip=? and port=? and service=?";
        update(conn, sql, server.getStatus(), server.getName(),
                server.getIp(), server.getPort(), server.getService());
    }

    @Override
    public void deleteServer(Connection conn, Server server) {
        String sql = "delete from server where name= ? and ip=? and port=? and service=?";
        update(conn, sql, server.getName(), server.getIp(), server.getPort(), server.getService());
    }
}
