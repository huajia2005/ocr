package com.baidu.aip.run.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Shieldkw {
	private Integer id;
	private Integer keyWordTypeId;
	private String kwName;
	private String kwType;
	private String partOfSpeech;
	private Date createTime;
	
	// 用于接收动词和名词集合
	private List<String> wordDcList;
	private List<String> wordMcList;
	
	public static Shieldkw getDcList(List<Shieldkw> list) {
		List<String> arrayList = new ArrayList<String>();
		for(Shieldkw shieldkw:list) {
			arrayList.add(shieldkw.getKwName());
		}
		Shieldkw shieldkw = new Shieldkw();
		shieldkw.setWordDcList(arrayList);
		return shieldkw;
	}
	
	public static Shieldkw getMcList(List<Shieldkw> list) {
		List<String> arrayList = new ArrayList<String>();
		for(Shieldkw shieldkw:list) {
			arrayList.add(shieldkw.getKwName());
		}
		Shieldkw shieldkw = new Shieldkw();
		shieldkw.setWordMcList(arrayList);
		return shieldkw;
	}
}
