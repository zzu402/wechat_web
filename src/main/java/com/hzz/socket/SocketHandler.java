package com.hzz.socket;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
public class SocketHandler implements Runnable{

    private static Map<String,Socket> socketMap=new ConcurrentHashMap<String,Socket>();

    public static Map<String,Socket> getSocketMap(){
        return  socketMap;
    }

    public static Socket getSocket(String name){
        return socketMap.get(name);
    }

    public static void putSocket(String name,Socket socket){
        synchronized (SocketHandler.class){
            socketMap.put(name,socket);
        }
    }
    public SocketHandler(Socket socket){

    }
    @Override
    public void run() {

    }
}
