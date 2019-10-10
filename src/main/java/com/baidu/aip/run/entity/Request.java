package com.baidu.aip.run.entity;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.util.Md5Util;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Data
@Slf4j
public class Request {
	
	private Integer nonce;  //随机数
	private String version;  //版本
	private String businessId;  //业务编号，用于验证身份
	private Long timestamp;  //时间戳
	private String secretId;  //用于身份验证
	private String signature;  //token
	private List<ImageData> images;  //图片数据,改为从ftp批量取数据，暂时不用

	@Value("${businessIdValue}")
	private String businessIdValue;

	public static String check(Request request, String secretKey) {
		Response response = new Response();
		response.setCode(500);
		
		if(request == null) {
			response.setMsg("请求参数为空！");
			return JSON.toJSONString(response);
		}
		if(request.getNonce() == null || request.getNonce().equals("")) {
			response.setMsg("nonce为空！");
			return JSON.toJSONString(response);
		}
		if(request.getVersion() == null || request.getVersion().equals("")) {
			response.setMsg("version为空！");
			return JSON.toJSONString(response);
		}
		if(request.getBusinessId() == null || request.getBusinessId().equals("")) {
			response.setMsg("businessId为空！");
			return JSON.toJSONString(response);
		}
		if(request.getTimestamp() == null || request.getTimestamp().equals("")) {
			response.setMsg("timestamp为空！");
			return JSON.toJSONString(response);
		}
		if(request.getSignature() == null || request.getSignature().equals("")) {
			response.setMsg("signature为空！");
			return JSON.toJSONString(response);
		}
//		if(request.getImages() == null || request.getImages().size() == 0) {
//			response.setMsg("images为空！");
//			return JSON.toJSONString(response);
//		}
//		if(request.getImages().get(0).getData() == null || request.getImages().get(0).getData().equals("")) {
//			response.setMsg("图片的base64编码数据为空！");
//			return JSON.toJSONString(response);
//		}
//		if(request.getImages().get(0).getName() == null || request.getImages().get(0).getName().equals("")) {
//			response.setMsg("原图片的标识地址为空！");
//			return JSON.toJSONString(response);
//		}
//		String jsonToken = JSON.toJSONString(request);
//		String token = jsonToken.replaceAll("\"", "'").substring(1,jsonToken.length()-1);
//		log.info(token);
//		JSONObject parseObject = JSONObject.parseObject(JSON.toJSONString(request));
//		parseObject.remove("signature");
//		String jsonToken = parseObject.toJSONString();
//		String token = jsonToken.replaceAll("\"", "'").substring(1,jsonToken.length()-1);
		StringBuffer token = new StringBuffer();
		Map maps = (Map)JSON.parse(JSON.toJSONString(request));
		Map treeMap = new TreeMap();
		for (Object o : maps.entrySet()) {
			treeMap.put(((Map.Entry)o).getKey(), ((Map.Entry)o).getValue());
		}
		treeMap.remove("signature");
        log.info("这个是用JSON类来解析JSON字符串!!!");  
        for (Object map : treeMap.entrySet()){  
        	token.append(((Map.Entry)map).getKey()+"" + ((Map.Entry)map).getValue());  
        }
//        int beginIndex = token.toString().indexOf("\"name\"");
////        int endIndex = token.toString().indexOf("\"type\"");
////        String substring = token.toString().substring(beginIndex, endIndex);
////        log.info(beginIndex+"  "+endIndex+"  "+substring);
        
        String resultToken = token.toString();
		log.info("构建的字符串为："+resultToken);
        resultToken = resultToken.replaceAll("\"", "'");
//        substring = substring.replace("\"name\":\"", ", 'name': '");
//        substring = substring.replace("}\",", "}'");
        int indexOf = resultToken.indexOf("}]nonce");
        
        StringBuffer sb = new StringBuffer();
        sb.append(resultToken);
//        sb.insert(indexOf, substring);
        resultToken = sb.toString().replace("'data':", "'data': ").replace("'type':", "'type': ")
        		.replace("'type'", " 'type'").replace("\\\"", "\"")+secretKey;
		log.info("最后解析的token为："+resultToken);
		log.info("加密的token为："+Md5Util.getMd5(resultToken.toString()));
		if(token == null || !request.getSignature().equals(Md5Util.getMd5(resultToken))) {
			response.setMsg("token验证错误");
			return JSON.toJSONString(response);
		}
		log.info("token验证通过");
		return "验证通过！";
	}
}
