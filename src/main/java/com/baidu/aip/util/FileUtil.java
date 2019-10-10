package com.baidu.aip.util;

import java.io.*;

public class FileUtil {

    /**
     * 根据路径获取文件内容
     */
    public static String getContentByPath(String filePath) throws Exception {
        StringBuffer sb = new StringBuffer();
        FileInputStream fis = new FileInputStream(filePath);
        // 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = "";
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * 将一个文件复制到另一个文件夹中
     * srcPath：原路径
     * destPath：新路径
     */
    public static void copyFile(String srcPath, String destPath) throws IOException {
        // 打开输入流
        FileInputStream fis = new FileInputStream(srcPath);
        // 打开输出流
        FileOutputStream fos = new FileOutputStream(destPath);

        // 读取和写入信息
        int len = 0;
        while ((len = fis.read()) != -1) {
            fos.write(len);
        }
        // 关闭流  先开后关  后开先关
        fos.close(); // 后开先关
        fis.close(); // 先开后关
    }
}
