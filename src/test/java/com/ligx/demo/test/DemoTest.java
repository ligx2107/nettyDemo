package com.ligx.demo.test;

import org.junit.Test;
import org.openjdk.jol.info.ClassLayout;

public class DemoTest {
    @Test
    public void t1(){
        System.out.println(2<<3);
    }

    @Test
    public void t2(){
        Object o = new Object();
        String s = ClassLayout.parseInstance(o).toPrintable();
        System.out.println(s);
    }

    @Test
    public void t3(){
        for(int i = 0;i < 10;i++){
            System.out.println(i);
        }
    }
}
