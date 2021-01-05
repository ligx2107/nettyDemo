package com.ligx.demo.netty.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * 使用Selector实现服务端
 */
public class SelectorServerDemo {
    public static void main(String[] args) throws Exception{
        // 获取serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 绑定监听端口
        serverSocketChannel.socket().bind(new InetSocketAddress(6666));

        // 设置为非阻塞 TODO
        serverSocketChannel.configureBlocking(false);

        // 获取多路复用器，
        // select,poll,epoll三种复用器
        // select和poll在jvm内存空间保存fd，epoll则是在内核空间保存fd
        Selector selector = Selector.open();

        // serverSocketChannel注册到selector中
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 循环监听
        while(true){
            // 调用select方法进行监听，阻塞2000ms，select(), selectNow()
            // 如果所监听的channel发生注册事件，则返回发生事件channel个数
            // select和poll需将jvm内存空间内保存的fds传递给内核函数进行处理，epoll则在内核空间直接处理fds
            if(selector.select(2000) == 0){
                System.out.println("等待2秒钟，没有链接...");
                continue;
            }

            // 如果被监听channel发生监听事件，调用selectedKeys获取seletionKey集合
            // 每个selectionKey包含了发生事件的具体channel及事件类型等
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            // 通过迭代器对selectionKeys进行遍历
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while(keyIterator.hasNext()){
                // 获取selectionKey
                SelectionKey key = keyIterator.next();

                // 判断channel发生事件
                if(key.isAcceptable()){
                    // 链接事件，则生成SocketChannel
                    // 链接事件已经发生，此处accept不会阻塞
                    SocketChannel socketChannel = serverSocketChannel.accept();

                    // 设置为非阻塞模式
                    socketChannel.configureBlocking(false);

                    // 将生成的socketChannel注册到selector中，监听读事件，同时绑定buffer
                    socketChannel.register(selector, SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                }

                if(key.isReadable()){
                    // 读事件
                    // 通过selectionKey反向取channel
                    SocketChannel readChannel = (SocketChannel)key.channel();

                    // 获取客户端输入数据
                    ByteBuffer buffer = (ByteBuffer)key.attachment();
                    readChannel.read(buffer);
                    System.out.println("客户端数据 : " + new String(buffer.array()));
                }

                // 手动删除selectionKey，避免重复处理
                keyIterator.remove();
            }
        }
    }
}
