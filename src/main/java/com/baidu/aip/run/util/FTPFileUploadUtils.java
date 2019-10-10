package com.baidu.aip.run.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Description：ftp文件上传工具类
 * @author Wang JinLei
 * @date 2019年5月14日 上午9:51:26
 */
public class FTPFileUploadUtils {

    //ftp服务器ip地址
    private static final String FTP_ADDRESS = "61.144.253.5";
    //端口号
    private static final int FTP_PORT = 12902;
    //用户名
    private static final String FTP_USERNAME = "NstUser";
    //密码
    private static final String FTP_PASSWORD = "123456o-0";
    //图片路径
    private static final String FTP_BASEPATH = "/ocr";

    public static boolean uploadFile(String originFileName, InputStream input){
        boolean success = false;
        FTPClient ftp = new FTPClient();
        ftp.setControlEncoding("GBK");
        try {
            int reply;
            ftp.connect(FTP_ADDRESS, FTP_PORT);// 连接FTP服务器
            ftp.login(FTP_USERNAME, FTP_PASSWORD);// 登录
            ftp.enterLocalPassiveMode();
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.makeDirectory(FTP_BASEPATH );
            ftp.changeWorkingDirectory(FTP_BASEPATH );
            ftp.storeFile(originFileName,input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }
}