package com.baidu.aip.run.entity;

import lombok.Data;

@Data
public class ImageData {

	private String data;  //图片的base64编码
	private Integer type;  //检测类型
	private String name;  //图片的标识
}
