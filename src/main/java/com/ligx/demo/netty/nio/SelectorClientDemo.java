package com.ligx.demo.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 客户端
 */
public class SelectorClientDemo {
    public static void main(String[] args) throws Exception{
        // 获取socketChannel
        SocketChannel socketChannel = SocketChannel.open();

        // 设置非阻塞模式
        socketChannel.configureBlocking(false);

        // 链接地址
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 6666);

        // 链接服务端
        if(!socketChannel.connect(inetSocketAddress)){
            while(!socketChannel.finishConnect()){
                System.out.println("链接失败...");
            }
        }

        String str = "hello, everyone";

        ByteBuffer buffer = ByteBuffer.wrap(str.getBytes());

        // 发送数据
        socketChannel.write(buffer);

        System.in.read();
    }
}
