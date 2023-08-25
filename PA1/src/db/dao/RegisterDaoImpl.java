package db.dao;

import db.bean.Register;

import java.sql.Connection;
import java.util.List;

/**
 * @description: The implementation of all register methods.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public class RegisterDaoImpl extends BaseDAO<Register> implements RegisterDAO{

    @Override
    public void insert(Connection conn, Register register) {
        System.out.println(register.getServername());
        System.out.println(register.getName());
        String sql = "insert into register(servername,name,ip,port) values(?,?,?,?)";
        update(conn, sql,register.getServername(), register.getName(),register.getIp(), register.getPort());
    }

    @Override
    public void deleteById(Connection conn, int id) {
        String sql = "delete from register where id = ?";
        update(conn, sql, id);
    }

    @Override
    public void update(Connection conn, Register register) {
        String sql = "update register set ip = ? where name = ?";
        update(conn, sql, register.getIp(), register.getName());
    }

    @Override
    public Register getRegisterByID(Connection conn, int id) {
        String sql = "select * from register where id = ?";
        Register register = getInstance(conn, sql,id);
        //System.out.println(register);
        return register;
    }

    @Override
    public Register getRegisterByAll(Connection conn, Register register) {
        String sql = "select * from register where servername=?and name = ? and port=?";
        Register instance;
        instance = getInstance(conn, sql, register.getServername(), register.getName(), register.getPort());
        return instance;
    }

    @Override
    public List<Register> getAll(Connection conn) {
        String sql = "select * from register";
        List<Register> list = getForList(conn, sql);
        //System.out.println(list);
        return list;
    }

    @Override
    public Register getRegisterByName(Connection conn, String serviceName) {
        String sql = "select * from register where name = ?";
        Register register;
        register = getInstance(conn, sql,serviceName);
        return register;
    }

    @Override
    public Register getRegisterByServerAndServiceName(Connection conn, String serverName, String serviceName) {
        String sql = "select * from register where servername=? and name = ?";
        Register register;
        register = getInstance(conn, sql,serverName, serviceName);
        return register;
    }
}
