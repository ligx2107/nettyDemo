package com.ligx.demo.netty.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BIODemo {

    // 线程池
    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception{

        // 新建服务端socket
        ServerSocket serverSocket = new ServerSocket(6666);

        // 等待客户端链接
        while(true){
            System.out.println("等待链接...");
            // 此处会阻塞等待客户端链接
            Socket socket = serverSocket.accept();
            executorService.execute(() -> handle(socket));
        }
    }

    private static void handle(Socket socket){
        try {
            InputStream inputStream = socket.getInputStream();
            System.out.println("等待输入...");
            byte[] bytes = new byte[1024];
            // 此处会阻塞等待客户端输入
            int read = inputStream.read(bytes);
            while(read != -1){
                // 打印客户端发送内容
                System.out.println(new String(bytes, 0, read));
                System.out.println("等待输入...");
                read = inputStream.read(bytes);
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }finally{
            // 关闭socket
            try {
                socket.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
