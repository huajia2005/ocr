package com.baidu.aip.run.entity;

import lombok.Data;

import java.util.List;

@Data
public class Response {
	
	private String msg;
	private Integer code = 500;
	private List<Result> result;
	
}
