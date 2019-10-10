package com.baidu.aip.util;

import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
   * 用于对扫描接口返回值的错误过滤
 * @author zyl
 * @date 2019年6月27日
 */
@Slf4j
public class CheckResult {
	
	// 检查SDK本地检测参数返回的结果
	public static boolean checkSDK(JSONObject res, String path) {
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("image size error") && res.get("error_code").equals("SDK100")) {
			log.info("图片"+path+"----------------大小超限");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("image length error") && res.get("error_code").equals("SDK101")) {
			log.info("图片"+path+"----------------边长不符合要求");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("read image file error") && res.get("error_code").equals("SDK102")) {
			log.info("图片"+path+"----------------读取错误");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("connection or read data time out") && res.get("error_code").equals("SDK108")) {
			log.info("图片"+path+"----------------连接超时或读取数据超时");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("unsupported image format") && res.get("error_code").equals("SDK109")) {
			log.info("图片"+path+"----------------不支持的图片格式");
			return false;
		}
		
		// 服务端返回结果
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("image format error") && (Integer)res.get("error_code") == 216201) {
			log.info("图片"+path+"----------------上传的图片格式错误，现阶段我们支持的图片格式为：PNG、JPG、JPEG、BMP，请进行转码或更换图片");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("empty image") && (Integer)res.get("error_code") == 216200) {
			log.info("图片"+path+"----------------图片为空，请检查后重新尝试");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("image size error") && (Integer)res.get("error_code") == 216202) {
			log.info("图片"+path+"----------------上传的图片大小错误，现阶段我们支持的图片大小为：base64编码后小于4M，分辨率不高于4096*4096，请重新上传图片");
			return false;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").equals("image recognize error") && (Integer)res.get("error_code") == 282810) {
			log.info("图片"+path+"----------------图像识别错误");
			return false;
		}
		return true;
	}
	
	// 检查服务端返回结果
	public static Integer checkresult(JSONObject res, Integer id) {
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("Open api request limit reached") && (Integer)res.get("error_code") == 4) {
//			log.info("id为"+id+"的账号----------------集群超限额");
//			return false;
//		}
		// 这种情况是本账号IAM认证失败，那么使用下一账号
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("IAM Certification failed") && (Integer)res.get("error_code") == 14) {
			log.info("id为"+id+"的账号----------------IAM鉴权失败，建议用户参照文档自查生成sign的方式是否正确，或换用控制台中ak sk的方式调用");
			return 1;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("Open api daily request limit reached") && (Integer)res.get("error_code") == 17) {
			log.info("id为"+id+"的账号----------------每天流量超限额");
			return 2;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0
				&& res.get("error_msg").toString().equals("Open api qps request limit reached") && (Integer)res.get("error_code") == 18) {
			log.info("id为"+id+"的账号----------------QPS超限额");
			return 3;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("Open api total request limit reached") && (Integer)res.get("error_code") == 19) {
			log.info("id为"+id+"的账号----------------IAM鉴权失败，建议用户参照文档自查生成sign的方式是否正确，或换用控制台中ak sk的方式调用");
			return 4;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("Invalid parameter") && (Integer)res.get("error_code") == 100) {
			log.info("id为"+id+"的账号----------------无效参数");
			return 5;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("Access token invalid or no longer valid") && (Integer)res.get("error_code") == 110) {
			log.info("id为"+id+"的账号----------------Access Token失效");
			return 6;
		}
		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
				&& res.get("error_msg").toString().equals("Access token expired") && (Integer)res.get("error_code") == 111) {
			log.info("id为"+id+"的账号----------------Access token过期");
			return 7;
		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("internal error") && (Integer)res.get("error_code") == 282000) {
//			log.info("id为"+id+"的账号----------------服务器内部错误，请再次请求， 如果持续出现此类错误，请通过QQ群（631977213）或工单联系技术支持团队。");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("invalid param") && (Integer)res.get("error_code") == 216100) {
//			log.info("id为"+id+"的账号----------------请求中包含非法参数，请检查后重新尝试");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("not enough param") && (Integer)res.get("error_code") == 216101) {
//			log.info("id为"+id+"的账号----------------缺少必须的参数，请检查参数是否有遗漏");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("service not support") && (Integer)res.get("error_code") == 216102) {
//			log.info("id为"+id+"的账号----------------请求了不支持的服务，请检查调用的url");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("param too long") && (Integer)res.get("error_code") == 216103) {
//			log.info("id为"+id+"的账号----------------请求中某些参数过长，请检查后重新尝试");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("appid not exist") && (Integer)res.get("error_code") == 216110) {
//			log.info("id为"+id+"的账号----------------appid不存在，请重新核对信息是否为后台应用列表中的appid");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("recognize error") && (Integer)res.get("error_code") == 216630) {
//			log.info("id为"+id+"的账号----------------识别错误，请再次请求，如果持续出现此类错误，请通过QQ群（631977213）或工单联系技术支持团队。");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").toString().equals("detect error") && (Integer)res.get("error_code") == 216634) {
//			log.info("id为"+id+"的账号----------------检测错误，请再次请求，如果持续出现此类错误，请通过QQ群（631977213）或工单联系技术支持团队。");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.toString().indexOf("missing parameters:") >= 0 && (Integer)res.get("error_code") == 282003) {
//			String param = res.get("error_msg").toString().split(":")[1];
//			log.info("id为"+id+"的账号----------------请求参数缺失："+param);
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").equals("batch processing error") && (Integer)res.get("error_code") == 282005) {
//			log.info("id为"+id+"的账号----------------处理批量任务时发生部分或全部错误，请根据具体错误码排查");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").equals("batch task limit reached") && (Integer)res.get("error_code") == 282006) {
//			log.info("id为"+id+"的账号----------------批量任务处理数量超出限制，请将任务数量减少到10或10以下");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").equals("url size error") && (Integer)res.get("error_code") == 282114) {
//			log.info("id为"+id+"的账号----------------URL长度超过1024字节或为0");
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.toString().indexOf("request id:") >= 0 && (Integer)res.get("error_code") == 282808) {
//			log.info("id为"+id+"的账号----------------"+res.get("error_code").toString());
//			return false;
//		}
//		if(res.toString().indexOf("error_code") >= 0 && res.toString().indexOf("error_msg") >= 0 
//				&& res.get("error_msg").equals("result type error") && (Integer)res.get("error_code") == 282809) {
//			log.info("id为"+id+"的账号----------------返回结果请求错误（不属于excel或json）");
//			return false;
//		}
		return 0;
	}
}
