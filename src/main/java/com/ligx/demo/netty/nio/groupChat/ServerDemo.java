package com.ligx.demo.netty.nio.groupChat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

/**
 * 基于nio的群聊服务端
 */
public class ServerDemo {
    private Selector selector;

    private static final int PORT = 6666;

    public ServerDemo() throws Exception{
        // 初始化
        init();
    }

    /**
     * 初始化
     * @throws Exception
     */
    private void init() throws Exception{
        // 开启serverSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 设置非阻塞模式
        serverSocketChannel.configureBlocking(false);

        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

        // 开始selector
        selector = Selector.open();

        // 注册
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 监听方法
     */
    private void listen(){
        try {
            // 循环监听
            while (true) {
                // 阻塞模式
                int count = selector.select();
                if (count > 0) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        // 链接事件
                        if (key.isAcceptable()) {
                            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                            // 获取客户端链接channel
                            SocketChannel channel = serverSocketChannel.accept();

                            // 设置非阻塞模式
                            channel.configureBlocking(false);

                            // 注册
                            channel.register(selector, SelectionKey.OP_READ);

                            // 打印上线提醒
                            System.out.println(channel.getRemoteAddress() + " 已上线...");
                        }

                        // 可读事件
                        if (key.isReadable()) {
                            // 读取客户端发送数据
                            readMsg(key);
                        }

                        // 删除当前key，避免重复处理
                        iterator.remove();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读取客户端发送数据
     * @param key
     */
    private void readMsg(SelectionKey key){
        // 客户端链接
        SocketChannel channel = (SocketChannel)key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(10);
        try {
            int read = channel.read(buffer);
            if(read > 0){
                String msg = new String(buffer.array()).trim();
                System.out.println("客户端" + channel.getRemoteAddress() + "发送消息：" + msg);
                // 转发消息
                sendMsgToOthers(msg, key);
            }else{
                System.out.println(channel.getRemoteAddress() + " 已下线....");
                key.cancel();
                channel.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * 转发消息到其他客户端
     * @param msg
     * @param exclusion
     */
    private void sendMsgToOthers(String msg, SelectionKey exclusion){
        Iterator<SelectionKey> iterator = selector.keys().iterator();
        while(iterator.hasNext()){
            SelectionKey key = iterator.next();
            Channel channel = key.channel();
            // 客户端链接channel并且不是发送消息客户端
            if(channel instanceof SocketChannel && key != exclusion){
                // 发送消息
                SocketChannel socketChannel = (SocketChannel)channel;
                try {
                    socketChannel.write(ByteBuffer.wrap(msg.trim().getBytes()));
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws Exception{
        ServerDemo demo = new ServerDemo();
        // 启动监听
        demo.listen();
    }
}
