package com.baidu.aip.run.controller;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.run.entity.Request;
import com.baidu.aip.run.entity.Response;
import com.baidu.aip.run.service.OcrServicesAnsj;
import com.baidu.aip.run.service.OcrServicesWord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Administrator
 */
@Controller
@Slf4j
public class OcrController {
    @Resource
    private OcrServicesAnsj ocrServices;

    @Resource
    private OcrServicesWord ocrServicesWord;

    @Value("${secretKey}")
    private String secretKey;

    @Value("${pingUrl}")
    private String pingUrl;

    /**
     * ansj分词模式
     *
     * @return java.lang.String
     * @Author xuhongchun
     * @Description
     * @Date 15:03 2019/10/31
     * @Param [request]
     */
    @PostMapping("imageCheck")
    @ResponseBody
    public String imageCheck(@RequestBody Request request) {
        long startTime = System.currentTimeMillis();
        log.info("图片识别开始运行!");
        String check = Request.check(request, secretKey);
        if (!"验证通过！".equals(check)) {
            return check;
        }
        // 执行主逻辑程序
        Response response = new Response();
        try {
            response = ocrServices.body();
        } catch (Exception e) {
            log.info("调用服务失败!");
            response.setCode(200);
            response.setMsg("执行错误,调用图片处理服务失败!");
            return JSON.toJSONString(response);
        }
        long execTile = System.currentTimeMillis() - startTime;
        double timeDouble = Double.parseDouble(Long.toString(execTile));
        log.info("执行完该接口所需时间为：" + (timeDouble / (double) 1000) + "秒");
        return JSON.toJSONString(response);
    }

    /**
     * word分词模式
     *
     * @return java.lang.String
     * @Author xuhongchun
     * @Description
     * @Date 15:03 2019/10/31
     * @Param [request]
     */
    @PostMapping("imageCheckWord")
    @ResponseBody
    public String imageCheckWord(@RequestBody Request request) {
        long startTime = System.currentTimeMillis();
        log.info("图片识别开始运行!");
        String check = Request.check(request, secretKey);
        if (!"验证通过！".equals(check)) {
            return check;
        }
        // 执行主逻辑程序
        Response response = new Response();
        try {
            response = ocrServicesWord.body();
        } catch (Exception e) {
            log.info("调用服务失败!");
            response.setCode(200);
            response.setMsg("执行错误,调用图片处理服务失败!");
            return JSON.toJSONString(response);
        }
        long execTile = System.currentTimeMillis() - startTime;
        double timeDouble = Double.parseDouble(Long.toString(execTile));
        log.info("执行完该接口所需时间为：" + (timeDouble / (double) 1000) + "秒");
        return JSON.toJSONString(response);
    }

    @PostMapping("test")
    @ResponseBody
    public String test() {
        try {
            long a = System.currentTimeMillis();
            Response response = ocrServicesWord.body();
            long b = System.currentTimeMillis();
            System.out.println(b - a);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "success";
    }

    /**
     * 测试集群是否健康
     *
     * @return boolean
     * @Author xuhongchun
     * @Description
     * @Date 16:08 2019/10/24
     * @Param []
     */
    @GetMapping("ping")
    @ResponseBody
    public boolean ping() {
        Integer code = null;
        HttpURLConnection connection = null;
        try {
            URL realUrl = new URL(pingUrl);
            connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            code = connection.getResponseCode();
        } catch (IOException e) {
            log.info("访问请求失败!");
            e.printStackTrace();
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        return null != code && HttpURLConnection.HTTP_OK == code;
    }

}
