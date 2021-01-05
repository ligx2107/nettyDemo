package com.ligx.demo.netty.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ServerHandler extends SimpleChannelInboundHandler<String> {

    //定义channle组，管理所有的channel
    //GlobalEventExecutor.INSTANCE 是全局的事件执行器，是一个单例
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 客户端链接成功，第一个被执行方法，为管道增加handler
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将客户端上线消息推送到其他在线客户端，该方法自动轮训所有的channel并发送消息
        Channel channel = ctx.channel();
        channels.writeAndFlush(channel.remoteAddress() + "加入聊天");

        // 将当前channel加入组
        channels.add(channel);
    }

    /**
     * 客户端断开链接时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channels.writeAndFlush(ctx.channel().remoteAddress() + "离开了");
    }

    /**
     * 客户端处于活动状态时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channels.forEach(ch -> {
            if(ch != ctx.channel()){
                ch.writeAndFlush(ctx.channel().remoteAddress() + "上线了");
            }
        });
    }

    /**
     * 客户端处于非活动状态时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        channels.writeAndFlush(ctx.channel().remoteAddress() + "下线了");
    }

    /**
     * 数据读取
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        channels.forEach(channel -> {
            // 判断是否为当前channel
            if(channel != ctx.channel()){
                // 消息推送
                channel.writeAndFlush(ctx.channel().remoteAddress() + "说: " + msg);
            }
        });
    }

    /**
     * 发生异常
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
