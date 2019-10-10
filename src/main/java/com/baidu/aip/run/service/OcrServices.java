package com.baidu.aip.run.service;

import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.run.entity.AppInfo;
import com.baidu.aip.run.entity.Response;
import com.baidu.aip.util.AnsjTest;
import com.baidu.aip.util.CheckResult;
import com.baidu.aip.util.FileUtil;
import com.baidu.aip.util.ZhStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @version 1.0
 * @ClassName OcrServices
 * @Author Administrator
 * @Date 2019/9/26 11:31
 */
@Slf4j
@Order(value = 1)
@Service
@EnableScheduling
public class OcrServices {
    @Autowired
    private AnsjTest ansjTest;

    @Value("${imagePath}")
    private String errImagePath;

    @Value("${txtPath}")
    private String txtPath;

    @Value("${shellPath}")
    private String shellPath;

    @Value("${ftpSaveImagePath}")
    private String ftpSaveImagePath;

    /**用于记录账号的顺序*/
    private Integer id = 0;
    private List<AppInfo> appInfoList = AppInfo.getAppInfoList();
    public Response body() throws Exception {
        Response response = new Response();
        //判断，如账户是否用完用完则不往下走
        if (appInfoList.size() == 0) {
            response.setMsg("您今天的免费次数已用完！");
            return response;
        }
        //从指定文件夹获取批量图片
        File file = new File(ftpSaveImagePath);
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            log.info("现在使用的id账号为s：" + id);
            String imagePath = fileList[i].getPath();
            String imageName = fileList[i].getName();
            //调用百度接口识别图片
            JSONObject res = transferInterface(imagePath);
            if (null == res) {
                log.info("现在使用的id账号为s：" + id);
                continue;
            }
            // 假如图片上的文字没有被提取到，那么将该图片放入一个指定的文件夹存放并将原文件删除
            if (!res.toString().contains("words_result") || !CheckResult.checkSDK(res, imagePath)) {
                log.info("图片" + imagePath + "没有被提取到文字");
                FileUtil.copyFile(imagePath, errImagePath + File.separator + imageName);
                fileList[i].delete();
                continue;
            }
            //提取百度的ai接口返回的数据
            String content = getReturnContent(res, imageName);
            if (Objects.equals(content,"")) {
                log.info("图片" + imagePath + "没有被提取到文字");
                //将图片转存到错误文件夹
                FileUtil.copyFile(imagePath, errImagePath + File.separator + imageName);
                fileList[i].delete();
                continue;
            }
           // String[] split1 = imageName.split("\\.");
            String txtName = imageName.substring(0,imageName.lastIndexOf("."));
            // 创建txt文本的路径
            String txtFilePath = txtPath + File.separator + txtName + ".txt";
            log.info("txt文件保存的路径为：" + txtFilePath);
            createTxt(content, txtFilePath);
            //删除图片以及txt
            fileList[i].delete();
            if (id == appInfoList.size() - 1) {
                id = 0;
            } else {
                id++;
            }
            log.info("第" + (i + 1) + "条数据ocr识别完成！");
        }
        File[] txtFiles = new File(txtPath).listFiles();
        if (null == txtFiles || txtFiles.length < 1) {
            response.setCode(200);
            response.setMsg("图片识别运行成功,无text文件生成!");
            return response;
        }
        if (transferTxtShell()) {
            response.setCode(200);
            response.setMsg("运行失败,调用脚本处理text文件错误!");
            return response;
        }
        for (File txtFile : txtFiles) {
            txtFile.delete();
        }
        log.info("删除txt文件成功");
        response.setCode(200);
        response.setMsg("图片识别运行成功,调用脚本处理text文件成功!");
        return response;
    }

    /**
     * 调用百度接口识别图片
     * @Author xuhongchun
     * @Description
     * @Date 14:18 2019/9/26
     * @Param [imagePath]
     * @return org.json.JSONObject
     */
    private JSONObject transferInterface(String imagePath) {
        // 初始化账号
        AipOcr client = init(appInfoList.get(id));
        // 调用接口
        JSONObject res = client.basicAccurateGeneral(imagePath, new HashMap<>(3));
        // 检测服务端返回结果
        Integer checkResult = CheckResult.checkresult(res, id);
        if (checkResult != 0) {
            if (checkResult == 2) {
                appInfoList.remove(appInfoList.get(id));
                id = id - 1;
            }
            log.info("现在使用下一账号进行扫描...");
            if (appInfoList.size() == 0) {
                return null;
            }
            if (id == appInfoList.size() - 1) {
                id = 0;
            } else {
                id++;
            }
            // 初始化账号
            client = init(appInfoList.get(id));
            // 再次调用接口
            res = client.basicAccurateGeneral(imagePath, new HashMap<>(3));
        }
        return res;
    }

    /**
     * 初始化用户对象
     * @Author xuhongchun
     * @Description
     * @Date 11:37 2019/9/26
     * @Param [appInfo]
     * @return com.baidu.aip.ocr.AipOcr
     */
    private static AipOcr init(AppInfo appInfo) {
        // 初始化一个AipOcr
        AipOcr client = new AipOcr(appInfo.getAPP_ID(), appInfo.getAPI_KEY(), appInfo.getSECRET_KEY());
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        return client;
    }

    /**
     * 如果不是汉字、数字、字符就替换为空字符
     * @Author xuhongchun
     * @Description
     * @Date 11:39 2019/9/26
     * @Param [word]
     * @return java.lang.String
     */
    public String filterWord(String word) {
        if("cm".equals(word) || "c".equals(word) || "kg".equals(word) || (word.length() == 1 && ZhStringUtil.check(word))){
            return "";
        }
        if (ZhStringUtil.check(word) || ZhStringUtil.isChineseChar(word.charAt(0))) {
            return word;
        }
        return "";
    }

    /**
     * 将字符串写入txt文件中
     * @Author xuhongchun
     * @Description
     * @Date 11:43 2019/9/26
     * @Param [content, path]
     * @return void
     */
    private static void createTxt(String content, String path) {
        File file = new File(path);
        try {
            file.createNewFile();
            // 将文本内容写入txt文件中
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            // 往文件里写入字符串
            ps.print(content);
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将调用百度接口的返回值拼成字符串
     * @Author xuhongchun
     * @Description
     * @Date 14:05 2019/9/26
     * @Param [jsonObject, imageName]
     * @return java.lang.String
     */
    private String getReturnContent(JSONObject jsonObject, String imageName) {
        String content = "";
        StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = (JSONArray) jsonObject.get("words_result");
        log.info("收到的返回值为：" + jsonArray.toString());
        // 遍历jsonarray
        if (jsonArray.length() > 0) {
            for (int j = 0; j < jsonArray.length(); j++) {
                // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                JSONObject json = (JSONObject) jsonArray.get(j);
                // 调用ansj分词插件进行分词
                String splitWords = ansjTest.splitWords(json.get("words").toString());
                if (splitWords == null || splitWords.length() == 0) {
                    continue;
                }
                String[] split = splitWords.split(" ");
                for (String s : split) {
                    if ("".equals(s) || "".equals(s.replaceAll(" ", ""))) {
                        continue;
                    }
                    stringBuilder.append(filterWord(s.replaceAll(" ", ""))).append(" ");
                }
            }
            // 先将多个空格替换成一个空格在去掉末尾的空格
            content = stringBuilder.toString().replaceAll("\\s+", " ").trim();
            log.info("分词后的content为:" + content);
            if (Objects.equals(content, "")) {
                return content;
            }
            content = "1 "+imageName + "," + content;
        }
        return content;
    }

    /**
     * 调用脚本处理txt
     * @Author xuhongchun
     * @Description
     * @Date 15:01 2019/9/26
     * @Param [txtFilePath]
     * @return boolean
     */
    private boolean transferTxtShell()  {
        int returnCode = -1;
        try {
            Long a = System.currentTimeMillis();
            Process  ps = Runtime.getRuntime().exec(shellPath);
            ps.waitFor();
            Long b = System.currentTimeMillis();
            log.info("执行脚本时间:" + (b-a));
            returnCode = ps.exitValue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("脚本返回值:"+returnCode);
        if (returnCode == -1 || returnCode == 200) {
            return true;
        }
     return false;
    }

    /**
     * 定時器注解，每天0点将id置1
     * @Author xuhongchun
     * @Description
     * @Date 11:42 2019/9/26
     * @Param []
     * @return void
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void timer() {
        log.info("定时任务开始执行!");
        id = 0;
        appInfoList = AppInfo.getAppInfoList();
        log.info("定时任务开始执行成功!");
    }
}