package com.ligx.demo.netty.netty.buffer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyBufDemo {
    public static void main(String[] args) {
        /**
         * 创建一个netty的buffer
         * 容量为10，底层为一个byte[10]数组
         * 三个属性：
         * readerIndex: 下一个要读取的数据索引
         * writerIndex: 下一个要写的数据索引
         * capacity: buffer容量
         * 三个属性将buffer划分为三段：
         * 0 -- readerIndex：已读取数据段，readerIndex -- writerIndex：可读数据段，writerIndex -- capacity：可写数据段
         */
        ByteBuf buffer = Unpooled.buffer(10);

        // 写入数据, buffer可自动扩容
        for(int i=0;i<11;i++){
            buffer.writeByte(i);
        }

        // 读数据
        while(buffer.isReadable()){
            System.out.println(buffer.readByte());
        }
    }
}
