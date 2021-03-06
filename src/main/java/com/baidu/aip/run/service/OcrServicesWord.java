package com.baidu.aip.run.service;

import com.baidu.aip.ocr.AipOcr;
import com.baidu.aip.run.entity.AppInfo;
import com.baidu.aip.run.entity.Response;
import com.baidu.aip.run.mapper.OcrMapper;
import com.baidu.aip.run.util.WordUtil;
import com.baidu.aip.util.CheckResult;
import com.baidu.aip.util.FileUtil;
import com.baidu.aip.util.ZhStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apdplat.word.segmentation.Word;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.util.*;

/**
 * OCR识别并用word分词进行分割
 *
 * @version 1.0
 * @ClassName OcrServices
 * @Author Administrator
 * @Date 2019/9/26 11:31
 */
@Slf4j
@Order(value = 1)
@Service
@EnableScheduling
public class OcrServicesWord {
    @Resource
    private OcrMapper ocrMapper;

    @Value("${errImagePath}")
    private String errImagePath;

    @Value("${txtPath}")
    private String txtPath;

    @Value("${shellPath}")
    private String shellPath;

    @Value("${imagePath}")
    private String imagePath;

    @Value("${txtShellPath}")
    private String txtShellPath;

    /**
     * 用于记录账号的顺序
     */
    private Integer id = 0;
    private List<AppInfo> appInfoList = AppInfo.getAppInfoList();
    private Map<String, String> map;

    //项目启动时加载txt中的关键词放到map中供匹配
    {
        map = new HashMap<>();
        try {
            FileReader fr = new FileReader("/bigdata/keywords.txt");
            //FileReader fr = new FileReader("E:\\test\\keywords.txt");
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                String[] a = str.split(":");
                map.put(a[1], a[0]);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 对文件夹下的图片进行OCR识别
     * @Author xuhongchun
     * @Description
     * @Date 9:00 2019/11/1
     * @Param []
     * @return com.baidu.aip.run.entity.Response
     */
    public Response body() throws Exception {
        Response response = new Response();
        //判断，如账户是否用完用完则不往下走
        if (appInfoList.size() == 0) {
            log.info("今天账号的免费使用次数以用完!");
            Process ps = Runtime.getRuntime().exec(txtShellPath);
            ps.waitFor();
            response.setCode(200);
            response.setMsg("您今天的免费次数已用完！");
            return response;
        }
        //从指定文件夹获取批量图片
        File file = new File(imagePath);
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            log.info("------------开始处理第" + (i + 1) + "张图片------------");
            log.info("现在使用的id账号为s：" + id);
            String imagePath = fileList[i].getPath();
            String imageName = fileList[i].getName();
            //调用百度接口识别图片
            long aa = System.currentTimeMillis();
            JSONObject res = transferInterface(imagePath);
            long bb = System.currentTimeMillis();
            log.info("调用百度接口耗时:" + (bb - aa) + "毫秒");
            if (null == res) {
                log.info("今天账号的免费使用次数以用完!");
                Process ps = Runtime.getRuntime().exec(txtShellPath);
                ps.waitFor();
                break;
            }
            // 假如图片上的文字没有被提取到，那么将该图片放入一个指定的文件夹存放并将原文件删除
            if (!res.toString().contains("words_result") || !CheckResult.checkSDK(res, imagePath)) {
                log.info("图片" + imagePath + "没有被提取到文字");
                FileUtil.copyFile(imagePath, errImagePath + File.separator + imageName);
                fileList[i].delete();
                moreAccount();
                continue;
            }
            //提取百度的ai接口返回的数据放入list中
            List<Word> splitWordList = getReturnContent(res);
            //对结果list进行关键字匹配和拼接
            long a = System.currentTimeMillis();
            String content = processList(splitWordList, imageName);
            long b = System.currentTimeMillis();
            log.info("关键词匹配用时:" + (b - a) + "毫秒");
            if (Objects.equals("", content)) {
                log.info("图片" + imagePath + "没有被提取到文字");
                //将图片转存到错误文件夹
                FileUtil.copyFile(imagePath, errImagePath + File.separator + imageName);
                fileList[i].delete();
                moreAccount();
                continue;
            }
            if (Objects.equals("-1", content)) {
                log.info("图片" + imageName + "以匹配关键词");
                fileList[i].delete();
                moreAccount();
                continue;
            }
            String txtName = imageName.substring(0, imageName.lastIndexOf("."));
            // 创建txt文本的路径
            String txtFilePath = txtPath + File.separator + txtName + ".txt";
            log.info("txt文件保存的路径为：" + txtFilePath);
            createTxt(content, txtFilePath);
            //删除图片以及txt
            fileList[i].delete();
            moreAccount();
            log.info("第" + (i + 1) + "条数据ocr识别完成！");
        }
        File[] txtFiles = new File(txtPath).listFiles();
        if (null == txtFiles || txtFiles.length < 1) {
            response.setCode(200);
            response.setMsg("图片识别运行成功,无txt文件生成!");
            return response;
        }
        if (transferTxtShell()) {
            log.info("图片识别运行成功,调用脚本处理text失败!");
            response.setCode(200);
            response.setMsg("运行失败,调用脚本处理txt文件错误!");
            return response;
        }
        for (File txtFile : txtFiles) {
            txtFile.delete();
        }
        log.info("删除txt文件成功");
        response.setCode(200);
        response.setMsg("图片识别运行成功,调用脚本处理txt文件成功!");
        return response;
    }

    /**
     * 调用百度接口识别图片
     *
     * @return org.json.JSONObject
     * @Author xuhongchun
     * @Description
     * @Date 14:18 2019/9/26
     * @Param [imagePath]
     */
    private JSONObject transferInterface(String imagePath) {
        // 初始化账号
        AipOcr client = init(appInfoList.get(id));
        // 调用接口
        JSONObject res = client.basicAccurateGeneral(imagePath, new HashMap<>(3));
        // 检测服务端返回结果
        int checkResult = CheckResult.checkresult(res, id);
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
     *
     * @return com.baidu.aip.ocr.AipOcr
     * @Author xuhongchun
     * @Description
     * @Date 11:37 2019/9/26
     * @Param [appInfo]
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
     *
     * @return java.lang.String
     * @Author xuhongchun
     * @Description
     * @Date 11:39 2019/9/26
     * @Param [word]
     */
    public String filterWord(String word) {
        String result = "";
        if ("cm".equals(word) || "c".equals(word) || "kg".equals(word)) {
            return result;
        }
        if (word.length() == 1 && ZhStringUtil.check(word)) {
            return result;
        }
        if (ZhStringUtil.check(word) || ZhStringUtil.isChineseChar(word.charAt(0))) {
            return word;
        }
        return result;
    }

    /**
     * 将字符串写入txt文件中
     *
     * @return void
     * @Author xuhongchun
     * @Description
     * @Date 11:43 2019/9/26
     * @Param [content, path]
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
            log.error(file.getName() + "写入失败");
            e.printStackTrace();
        }
    }

    /**
     * 将调用百度接口的返回值拼成字符串
     *
     * @return java.lang.String
     * @Author xuhongchun
     * @Description
     * @Date 14:05 2019/9/26
     * @Param [jsonObject, imageName]
     */
    private List<Word> getReturnContent(JSONObject jsonObject) {
        JSONArray jsonArray = (JSONArray) jsonObject.get("words_result");
        log.info("收到的返回值为：" + jsonArray.toString());
        List<Word> splitWordList = new ArrayList<>();
        // 遍历jsonarray
        if (jsonArray.length() > 0) {
            for (int j = 0; j < jsonArray.length(); j++) {
                // 遍历 jsonarray 数组，把每一个对象转成 json 对象
                JSONObject json = jsonArray.getJSONObject(j);
                List<Word> splits = WordUtil.splitWords(json.getString("words"));
                splitWordList.addAll(splits);
            }
        }
        return splitWordList;
    }

    /**
     * 对分词后的list进行关键词匹配
     *
     * @return java.lang.String
     * @Author xuhongchun
     * @Description
     * @Date 9:46 2019/10/11
     * @Param [splitWordList, imageName]
     */
    private String processList(List<Word> splitWordList, String imageName) {
        if (splitWordList.size() < 1) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        /*对分词后的list进行循环匹配,成功则入库,并跳过检测图片的剩余步骤
         * 失败则追加到content等待接下来的处理
         */
        for (Word words : splitWordList) {
            String word = words.getText();
            if ("".equals(word) || "".equals(word.replaceAll(" ", ""))) {
                continue;
            }
            word = filterWord(word.replaceAll(" ", ""));
            if (Objects.equals("", word)) {
                continue;
            }
            String sort = map.get(word);
            if (sort == null) {
                stringBuilder.append(word).append(" ");
                continue;
            }
            //匹配成功,进行入库操作
            storageImage(imageName, sort, word);
            return "-1";
        }
        String content = stringBuilder.toString().replaceAll("\\s+", " ").trim();
        log.info("分词后的content为:" + content);
        if (Objects.equals(content, "")) {
            return content;
        }
        content = "1 " + imageName + "," + content;
        return content;
    }

    /**
     * 将违规信息入库
     *
     * @return int
     * @Author xuhongchun
     * @Description
     * @Date 13:38 2019/10/11
     * @Param [imageName, sort]
     */
    private void storageImage(String imageName, String sort, String word) {
        String[] sorts = sort.split(",");
        for (String s : sorts) {
            String[] gradings = s.split("-");
            int insertCount = ocrMapper.insertViolationRecords(imageName, Double.parseDouble(gradings[0]), gradings[1], word);
            if (insertCount > 0) {
                log.info("图片" + imageName + "成功入库");
            }
        }
    }

    /**
     * 调用脚本处理txt
     *
     * @return boolean
     * @Author xuhongchun
     * @Description
     * @Date 15:01 2019/9/26
     * @Param [txtFilePath]
     */
    private boolean transferTxtShell() {
        int returnCode = -1;
        try {
            Long a = System.currentTimeMillis();
            Process ps = Runtime.getRuntime().exec(shellPath);
            ps.waitFor();
            Long b = System.currentTimeMillis();
            log.info("执行脚本时间:" + (b - a));
            returnCode = ps.exitValue();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        log.info("脚本返回值:" + returnCode);
        return returnCode == -1 || returnCode == 200;
    }

    /**
     * 定時器注解，每天0点将id置1
     *
     * @return void
     * @Author xuhongchun
     * @Description
     * @Date 11:42 2019/9/26
     * @Param []
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void timer() {
        log.info("定时任务开始执行!");
        id = 0;
        appInfoList = AppInfo.getAppInfoList();
        log.info("定时任务开始执行成功!");
    }

    /**
     * 将百度接口调用id+1
     *
     * @return void
     * @Author xuhongchun
     * @Description
     * @Date 14:04 2019/10/21
     * @Param []
     */
    private void moreAccount() {
        if (id == appInfoList.size() - 1) {
            id = 0;
        } else {
            id++;
        }
    }
}
