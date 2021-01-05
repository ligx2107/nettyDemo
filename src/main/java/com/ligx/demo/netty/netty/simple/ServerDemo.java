package com.ligx.demo.netty.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * netty server demo
 */
public class ServerDemo {

    /**
     * 处理耗时任务方式2：直接将业务处理handler放入到自定义线程组中
     */
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    public static void main(String[] args){
        /**
         * 创建bossGrop及workerGroup,
         * bossGroup只处理客户端链接请求，workerGroup负责处理与客户端的业务处理
         * bossGroup和workerGroup含有的NioEventLoop数量，默认为服务器CPU核心数 * 2，可通过构造方法直接设定具体数量
         *
         * 每个EventLoopGroup下可包含多个EventLoop
         * 每个EventLoop包含一个selector、taskQueue、scheduleTaskQueue
         * 每个EventLoop的selector可以注册并监听多个NioChannel
         * 每个Niochannel只会绑定到唯一一个EventLoop上，EventLoop和NioChannel是一对多的关系
         * 每个NioChannel都绑定有一个自己的ChannelPipelLine
         */
        EventLoopGroup bossGrop = new NioEventLoopGroup(1);
        EventLoopGroup workerGrop = new NioEventLoopGroup();

        try {
            // 创建服务器端启动对象，配置参数
            ServerBootstrap bootStrp = new ServerBootstrap();
            bootStrp.group(bossGrop, workerGrop) // 设置两个线程组
                    .channel(NioServerSocketChannel.class) // 设置服务端通道实现
                    .option(ChannelOption.SO_BACKLOG, 128) // 设置可连接队列大小
                    .childOption(ChannelOption.SO_KEEPALIVE, true) // 设置保持活动连接状态
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        // 为pipelLine设置处理器
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ServerHandler());

                            /**
                             * addLast方法指定线程池，则业务处理handler默认由指定线程池中线程处理
                                ch.pipeline().addLast(group, new ServerHandler());
                             */
                        }
                    }); // 为workerGroup的eventLoop对应的管道设置处理器

            System.out.println("server is ok....");

            // 绑定端口号, Future异步模型 ---> (Future-Listener机制)
            // sync, 等待异步操作完成
            ChannelFuture channelFuture = bootStrp.bind(6688).sync();

            // 为channelFuture绑定监听器
            channelFuture.addListener((ChannelFutureListener)listener -> {
                if(listener.isSuccess()){
                    System.out.println("绑定端口6688成功");
                }else{
                    System.out.println("绑定端口6688失败");
                }
            });

            // 监听通道关闭事件
            channelFuture.channel().closeFuture().sync();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            // 关闭group，断开链接同时关闭线程
            bossGrop.shutdownGracefully();
            workerGrop.shutdownGracefully();
        }
    }
}
