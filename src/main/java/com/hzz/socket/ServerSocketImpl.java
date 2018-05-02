package com.hzz.socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
public class ServerSocketImpl {

    private ServerSocket serverSocket;

    public ServerSocketImpl() throws IOException {
        serverSocket=new ServerSocket(8088);
    }

    public void monitor(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Socket socket=serverSocket.accept();
//                        new Thread(new SocketHandler(socket)).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }





}
