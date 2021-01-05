package com.ligx.demo.netty.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import jdk.nashorn.internal.ir.CallNode;

import java.util.concurrent.Callable;

/**
 * 自定义handler
 */
public class ServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 异步处理耗时任务方式1：handler内定义任务线程组，并将耗时任务加入到线程组内处理
     */
    private static EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    /**
     * 数据读取方法
     * @param ctx 上下文，含有pipelLine，channel等
     * @param msg 客户端发送数据，已Object形式存在
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("ServerHandler channelRead 线程：" + Thread.currentThread().getName());

        /**
         * 如果存在耗时操作，则可以执行异步操作，将任务交给channel对应的eventLoop的taskQueue
         * 第一种方式：自定义普通任务
        ctx.channel().eventLoop().execute(() -> {
            try {
                // 休眠10秒钟
                Thread.sleep(10 * 1000);
                System.out.println("ServerHandler channelRead execute 线程：" + Thread.currentThread().getName());
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端2", CharsetUtil.UTF_8));
            }catch(Exception e){
                e.printStackTrace();
            }
        });

         第二种方式：自定义定时任务，提交到NioEventLoop的scheduleTaskQueue
         ctx.pipeline().channel().eventLoop().schedule(() -> {
            try {
                Thread.sleep(10 * 1000);
                ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端3", CharsetUtil.UTF_8));
            }catch(Exception e){
                e.printStackTrace();
            }
         }, 10 * 1000, TimeUnit.MILLISECONDS);

         注：无论将任务放入到eventLoop的taskQueue还是scheduleTaskQueue中，最后都是由当前eventLoop线程执行，并未真正做到异步操作
         */

        // 将耗时任务添加到业务线程组
        group.submit(() -> {
            // 休眠10秒钟
            Thread.sleep(10 * 1000);
            System.out.println("ServerHandler channelRead execute 线程：" + Thread.currentThread().getName());
            ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端2", CharsetUtil.UTF_8));
            return null;
        });


        System.out.println("context : " + ctx);
        // 将msg转换成netty 的ByteBuf
        ByteBuf buf = (ByteBuf) msg;

        // 打印客户端发送数据
        System.out.println("客户端发送：" + buf.toString(CharsetUtil.UTF_8));

        // 打印客户端地址
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());
    }

    /**
     * 读取完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // write + flush
        ctx.writeAndFlush(Unpooled.copiedBuffer("hello 客户端", CharsetUtil.UTF_8));
    }

    /**
     * 异常处理
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 关闭channel
        ctx.channel().close();
    }
}
