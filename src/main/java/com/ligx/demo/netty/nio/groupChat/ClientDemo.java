package com.ligx.demo.netty.nio.groupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class ClientDemo {

    private Selector selector;
    SocketChannel socketChannel;

    private static final int PORT = 6666;
    private static final String HOST = "127.0.0.1";

    public ClientDemo() throws Exception{
        init();
    }

    private void init() throws Exception{
        // 开启selector
        selector = Selector.open();
        // 开启channel并绑定服务端地址
        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        // 设置非阻塞模式
        socketChannel.configureBlocking(false);
        // 组册读事件
        socketChannel.register(selector, SelectionKey.OP_READ);

        System.out.println(socketChannel.getLocalAddress() + "is ok...");
    }

    /**
     * 发送消息
     * @param msg
     */
    private void sendMsg(String msg){
        try {
            socketChannel.write(ByteBuffer.wrap(msg.trim().getBytes()));
        }catch(IOException o){
            o.printStackTrace();
        }
    }

    /**
     * 读取消息
     */
    private void readMsg(){
        try {
            // 阻塞模式
            int count = selector.select();
            if(count > 0){
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        SocketChannel channel = (SocketChannel)key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        int readCount = channel.read(buffer);
                        if(readCount > 0){
                            System.out.println(new String(buffer.array()).trim());
                        }
                    }

                    iterator.remove();
                }
            }
        }catch(IOException o){
            o.printStackTrace();
        }
    }



    public static void main(String[] args) throws Exception{
        ClientDemo demo = new ClientDemo();

        // 单独起一个线程，循环读数据
        new Thread(() -> {
            while(true){
                demo.readMsg();
                try {
                    Thread.currentThread().sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while(scanner.hasNextLine()){
            String info = scanner.nextLine();
            demo.sendMsg(info);
        }
    }
}
