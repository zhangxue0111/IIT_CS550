package db.dao;

import db.bean.Register;

import java.sql.Connection;
import java.util.List;

/**
 * @description: Encapsulate common operations related to register.
 * @author: Xue Zhang
 * @date: 2022-02-10
 * @version: 1.0
 **/
public interface RegisterDAO {

    void insert(Connection conn, Register register);

    void deleteById(Connection conn,int id);

    void update(Connection conn,Register register);

    Register getRegisterByID(Connection conn,int id);

    Register getRegisterByAll(Connection conn, Register register);

    List<Register> getAll(Connection conn);

    Register getRegisterByName(Connection conn, String name);

    Register getRegisterByServerAndServiceName(Connection conn, String serverName, String serviceName);

}

