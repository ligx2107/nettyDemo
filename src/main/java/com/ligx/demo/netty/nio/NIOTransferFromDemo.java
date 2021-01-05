package com.ligx.demo.netty.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * 利用channel的transferFrom, transferTo完成文件零拷贝
 */
public class NIOTransferFromDemo {
    public static void main(String[] args) throws Exception{
        // 获取文件输入输出流
        FileInputStream fileInputStream = new FileInputStream("./timg.jpg");
        FileOutputStream fileOutputStream = new FileOutputStream("./timg1.jpg");

        // 获取流对应的channel
        FileChannel inChannel = fileInputStream.getChannel();
        FileChannel outChannel = fileOutputStream.getChannel();

        // 调用channel的transferFrom方法
        outChannel.transferFrom(inChannel, 0, inChannel.size());

        // 关闭channel和流
        inChannel.close();
        outChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
    }
}
