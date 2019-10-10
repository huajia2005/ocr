package com.baidu.aip.run.entity;

import lombok.Data;

import java.util.List;

@Data
public class Result {
	
	private Integer status = 0;
	private List<Labels> labels;
	private String name;
}
