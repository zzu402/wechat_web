package com.hzz.utils;

import java.io.*;
import java.net.Socket;

/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/5/2
 */
public class SocketUtils {


    public String read(Socket socket) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(),
                "UTF-8"));
        // 读取每一行的数据.注意大部分端口操作都需要交互数据。
        StringBuilder stringBuilder=new StringBuilder();
        String str=null;
        while ((str = rd.readLine()) != null) {
            stringBuilder.append(str);
        }
        //TODO 关闭流
        return stringBuilder.toString();
    }

    public void write(Socket socket,String data) throws IOException {
        OutputStream os=socket.getOutputStream();
        PrintWriter pw=new PrintWriter(os);
        pw.write(data);
        pw.flush();
        socket.shutdownOutput();
        //TODO 关闭流
    }




}
