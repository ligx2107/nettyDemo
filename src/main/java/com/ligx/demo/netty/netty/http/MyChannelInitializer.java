package com.ligx.demo.netty.netty.http;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * 自定义通道初始化器
 */
public class MyChannelInitializer extends ChannelInitializer {
    // 管道初始化
    @Override
    protected void initChannel(Channel ch) throws Exception {
        // 获取管道
        ChannelPipeline pipeline = ch.pipeline();

        // 添加netty提供的http编解码器
        pipeline.addLast("myServerCodec", new HttpServerCodec());

        // 添加自定义处理器
        pipeline.addLast("myServerHandler", new MyServerHandler());

        System.out.println("ok");
    }
}
