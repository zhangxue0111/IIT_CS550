package server;


import utils.Invocation;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * @description: A helper to unpack messages from the client
 * @author: Xue Zhang
 * @date: 2022-02-02
 * @version: 1.0
 **/
public class Skeleton implements Runnable{
    private final Socket socket;
    private final Map<String, Class<?>> services;

    public Skeleton(Socket socket, Map<String, Class<?>> services) {
        this.socket = socket;
        this.services = services;
    }

    @Override
    public void run() {
        ObjectInputStream ois = null;
        ObjectOutputStream oos = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            oos = new ObjectOutputStream(socket.getOutputStream());

            Invocation invocation = (Invocation) ois.readObject();

            Class<?> clazz = services.get(invocation.getInterfaceName());
            Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParamTypes());
            Object invoke = method.invoke(clazz.getDeclaredConstructor().newInstance(), invocation.getArgs());
            //System.out.println(invoke);
            oos.writeObject(invoke);
            oos.flush();
            socket.shutdownOutput();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
