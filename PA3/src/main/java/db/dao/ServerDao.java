package db.dao;

import db.bean.Server;
import java.sql.Connection;

/**
 * @description: An interface defines all operations done for the server.
 * @author: Xue Zhang
 * @date: 2022-03-18
 * @version: 1.0
 */
public interface ServerDao {

    void insert(Connection conn, Server server);

    Server getServerByNameAndService(Connection conn, String serverName, String serviceName);

    Server getServerIgnoreStatue(Connection conn, Server server);

    void update(Connection conn, Server server);

    void deleteServer(Connection conn, Server server);
}
