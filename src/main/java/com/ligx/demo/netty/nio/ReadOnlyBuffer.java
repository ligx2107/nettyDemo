package com.ligx.demo.netty.nio;

import java.nio.ByteBuffer;

/**
 * 1. 按类型存取数据
 * 2. 只读buffer
 */
public class ReadOnlyBuffer {
    public static void main(String[] args) {
        // 创建buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(64);

        byteBuffer.putInt(10);
        byteBuffer.putLong(100L);
        byteBuffer.putChar('1');

        // 写转读
        byteBuffer.flip();

        // 按照写入类型顺序读取
        System.out.println(byteBuffer.getInt());
        System.out.println(byteBuffer.getLong());
        System.out.println(byteBuffer.getChar());

        // 清空buffer
        byteBuffer.clear();

        // 重新写入数据
        byteBuffer.putInt(1);
        byteBuffer.putInt(2);
        byteBuffer.putInt(3);

        // 调用asReadOnlyBuffer()方法转成只读buffer
        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();

        // 读取只读buffer中内容
        while(readOnlyBuffer.hasRemaining()){
            System.out.println(readOnlyBuffer.get());
        }

        // 写数据报异常 -> ReadOnlyBufferException
        readOnlyBuffer.putInt(4);
    }
}
