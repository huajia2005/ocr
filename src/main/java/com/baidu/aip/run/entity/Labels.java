package com.baidu.aip.run.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Labels {
	
	private Integer rate = 0;
	private Integer level = 0;
	private Double label;

	public static List<Labels> getLabelsList(){
		List<Labels> labelList = new ArrayList<Labels>();
		Labels labels = new Labels();
		labels.setLabel(100D);
		labelList.add(labels);
		Labels labels1 = new Labels();
		labels1.setLabel(110D);
		labelList.add(labels1);
		Labels labels2 = new Labels();
		labels2.setLabel(210D);
		labelList.add(labels2);
		Labels labels3 = new Labels();
		labels3.setLabel(200D);
		labelList.add(labels3);
		Labels labels4 = new Labels();
		labels4.setLabel(300D);
		labelList.add(labels4);
		Labels labels5 = new Labels();
		labels5.setLabel(400D);
		labelList.add(labels5);
		Labels labels6 = new Labels();
		labels6.setLabel(500D);
		labelList.add(labels6);

		return labelList;
	}
}
