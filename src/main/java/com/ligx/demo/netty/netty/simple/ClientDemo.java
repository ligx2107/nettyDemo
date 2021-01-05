package com.ligx.demo.netty.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * netty client demo
 */
public class ClientDemo {
    public static void main(String[] args) {
        // 创建group
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            // 创建客户端启动器
            Bootstrap bootstrap = new Bootstrap();

            // 配置
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            System.out.println("client is ok....");

            // 链接服务器
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6688).sync();

            // 监听管道关闭
            channelFuture.channel().closeFuture().sync();

        }catch(Exception e){
            e.printStackTrace();
        }finally{
            eventLoopGroup.shutdownGracefully();
        }
    }
}
