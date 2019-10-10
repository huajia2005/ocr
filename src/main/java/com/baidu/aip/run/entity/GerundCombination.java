package com.baidu.aip.run.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GerundCombination {
	
	private Integer id;
	private Integer keyWordTypeId;
	private String name;
	private String type;
	private Date createTime;
}
