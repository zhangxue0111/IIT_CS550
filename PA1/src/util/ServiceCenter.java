package util;

import db.bean.Register;
import db.dao.RegisterDAO;
import db.dao.RegisterDaoImpl;
import db.util.JDBCUtils;

import java.sql.Connection;

/**
 * @description: A collection of all services being offered by servers.
 * @author: Xue Zhang
 * @date: 2022-02-09
 * @version: 1.0
 **/
public class ServiceCenter {

    private final RegisterDAO dao = new RegisterDaoImpl();

    public void register(String serverName, String serviceName, String ip, Integer servicePort) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Register register = new Register(null, serverName, serviceName, null, servicePort);
            Register registeredServiceName = dao.getRegisterByAll(conn, register);
            //System.out.println("register = " + registeredServiceName);
            if(!(registeredServiceName == null)) {
                if(registeredServiceName.getServername().equals(serverName)) {
                    if (!registeredServiceName.getIp().equals(ip)) {
                        registeredServiceName.setIp(ip);
                        dao.update(conn, registeredServiceName);
                        //System.out.println("update success!");
                    }
                } else {
                    register = new Register(null, serverName, serviceName, ip, servicePort);
                    dao.insert(conn, register);
                }
                return;
            }
            register = new Register(null, serverName, serviceName, ip, servicePort);
            dao.insert(conn, register);
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
    }

    public Register subscribe(String serverName, String serviceName) {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();
            Register registeredServiceName = dao.getRegisterByServerAndServiceName(conn, serverName, serviceName);
            if(registeredServiceName == null) {
                return null;
            }
            //System.out.println("subscribe success!!");
            return registeredServiceName;
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            JDBCUtils.closeResource(conn, null);
        }
        return null;
    }

}
