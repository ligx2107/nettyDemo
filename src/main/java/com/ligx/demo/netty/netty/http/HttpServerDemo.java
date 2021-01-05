package com.ligx.demo.netty.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * netty处理http请求demo，服务端
 */
public class HttpServerDemo {
    public static void main(String[] args) {
        // 创建工作组
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            // 创建并配置启动器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new MyChannelInitializer());

            // 绑定端口
            ChannelFuture channelFuture = serverBootstrap.bind(6677).sync();

            channelFuture.addListener(listener -> {
                if(listener.isSuccess()){
                    System.out.println("绑定6677端口成功...");
                }
            });

            // 监听通道关闭事件
            channelFuture.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
