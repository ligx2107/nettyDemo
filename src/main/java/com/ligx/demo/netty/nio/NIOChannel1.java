package com.ligx.demo.netty.nio;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NIOChannel1 {
    public static void main(String[] args) throws Exception{
        String str = "hello world";
        // 文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream("./test.txt");

        // 文件channel
        FileChannel fileChannel = fileOutputStream.getChannel();

        // 缓存区
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        // 缓存区写入数据
        byteBuffer.put(str.getBytes());

        // 写 -> 读模式切换，即：修改position，limit等属性值
        byteBuffer.flip();

        // 缓存区数据循环写入channel
        while(byteBuffer.hasRemaining()){
            fileChannel.write(byteBuffer);
        }

        // 关闭流
        fileOutputStream.close();
    }
}
