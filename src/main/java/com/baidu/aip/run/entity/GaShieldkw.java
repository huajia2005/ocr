package com.baidu.aip.run.entity;

import java.util.Date;
import lombok.Data;

@Data
public class GaShieldkw {
	private Integer id;
	private Integer keyWordTypeId;
	private String kwName;
	private String levelOne;
	private String levelTwo;
	private Date reportingTime;
	private Date createTime;
}
