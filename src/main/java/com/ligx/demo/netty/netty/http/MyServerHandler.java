package com.ligx.demo.netty.netty.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * 自定义处理器
 */
public class MyServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        // 判断是否为httpRequest请求
        if(msg instanceof HttpRequest){

            // 过滤特定请求
            HttpRequest httpRequest = (HttpRequest) msg;
            if("/favicon.ico".equalsIgnoreCase(httpRequest.uri())){
                System.out.println("不响应请求/favicon.ico");
                return;
            }

            System.out.println("msg 类型：" + msg.getClass());

            // 向客户端发送消息
            ByteBuf buf = Unpooled.copiedBuffer("我是服务端", CharsetUtil.UTF_8);

            // 创建httpResponse对象，指定http协议版本，响应状态及发送消息内容
            HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

            // 设置响应头参数
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
            httpResponse.headers().set(HttpHeaderNames.ACCEPT_CHARSET, CharsetUtil.UTF_8);

            ctx.writeAndFlush(httpResponse);
        }
    }
}
