package com.ligx.demo.netty.nio;

import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * MappedByteBuffer可以使文件直接在堆外内存修改，OS无需拷贝一次
 */
public class MappedByteBufferDemo {
    public static void main(String[] args) throws Exception{

        // 创建文件
        RandomAccessFile randomAccessFile = new RandomAccessFile("./test.txt", "rw");

        // 获取文件channel
        FileChannel channel = randomAccessFile.getChannel();

        /**
         * 调用channel的map方法，将文件映射到内存中
         * mode: 文件映射模式
         * position: 可以直接操作的起始位置
         * size: 映射到内容的大小
         */
        MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 2, channel.size());

        // 对映射到buffer范围内的指定位置进行修改
        buffer.put(0, (byte)'e');
        buffer.put(2, (byte)'e');

        // 关闭文件
        channel.close();
        randomAccessFile.close();
    }
}
