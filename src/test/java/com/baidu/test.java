package com.baidu;

import org.junit.Test;

/**
 * @version 1.0
 * @ClassName test
 * @Author Administrator
 * @Date 2019/9/26 20:36
 */
public class test {
    @Test
    public void test1() {
    String a = "123.43.jpg";
    int aa = a.lastIndexOf(".");
    String b = a.substring(0,a.lastIndexOf("."));
        System.out.println(b);
    }


}
