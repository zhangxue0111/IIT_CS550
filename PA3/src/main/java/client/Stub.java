package client;

import db.bean.Server;
import utils.Invocation;
import utils.Request;
import utils.ServiceCenter;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;

/**
 * @description: A stub to provide communication between a client and a server.
 * @author: Xue Zhang
 * @date: 2022-02-02
 * @version: 1.0
 **/
public class Stub {

        private final ServiceCenter serviceCenter;

        public Stub(ServiceCenter serviceCenter) {
            this.serviceCenter = serviceCenter;
        }

        public Object getStub(final Class clazz, String serverName) {
            return Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    //System.out.println(clazz.getName());
                    Socket socket = null;
                    Server register = serviceCenter.subscribe(serverName, clazz.getName());
                    socket = new Socket(register.getIp(), register.getPort());
                    //System.out.println("socket = " + socket);
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    //System.out.println("out= " + out);
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    //System.out.println("in = " + in);

                    String className = clazz.getName();
                    //System.out.println("className = " + className);
                    String methodName = method.getName();
                    //System.out.println("method = " + method);
                    Class[] paramTypes = method.getParameterTypes();
                    //System.out.println("types = " + paramTypes);
                    Invocation invocation = new Invocation(className, methodName, paramTypes, args);
                    //System.out.println("invocation = " + invocation);
                    out.writeObject(invocation);
                    out.flush();

                    Object object = in.readObject();
                    in.close();
                    out.close();
                    socket.close();
                    return object;
                }
            });
        }
}

