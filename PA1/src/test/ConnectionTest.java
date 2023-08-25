package test;

import db.util.JDBCUtils;

import java.sql.Connection;

/**
 * @description: A connection test.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public class ConnectionTest {

    public static void main(String[] args) throws Exception {
        Connection conn = JDBCUtils.getConnection();
        System.out.println(conn);
    }
}
