package com.baidu.aip.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NunberUtil {
	
	public static String getCertificateNumber(String id) {
//		String example = "00601201904171032110000001";
		System.out.println("进来前："+id);
		String substring = "";
		if(id == null || id.equals("")) {
			substring = "000000";
		}else {
			String sixId = substring = id.substring(id.length() - 6);
			if(sixId != null && sixId.equals("999999")) {
				substring = "000000";
			}else {
				substring = sixId;
			}
		}
		String zz = "01001";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String format = sdf.format(new Date());
		String count = "";
//		String count = String.valueOf(Integer.parseInt(substring)+1);
		
		if(Integer.parseInt(substring) <= 999999) {
			if(getNumLenght(Integer.parseInt(substring)) < 6) {
				Integer aa = Integer.parseInt(substring) + 1;
				count = getLeft(6-getNumLenght(aa)+1,aa);
			}else {
				count = String.valueOf(Integer.parseInt(substring) + 1);
			}
		}
		System.out.println("重组后："+zz+format+count);
		return zz+format+count;
		
	}
	
	private static int getNumLenght(long num){
        num = num>0?num:-num;      
        System.out.println(String.valueOf(num).length());
        return String.valueOf(num).length();
 
    }
	
	public static String getLeft(Integer count ,Integer youNumber) {
	    // 0 代表前面补充0      
	    // 4 代表长度为4      
	    // d 代表参数为正数型      
	    String str = String.format("%0"+count+"d", youNumber);      
	    System.out.println(str); // 0001
	    return str;
	}
	
	public static void main(String[] args) {
		String certificateNumber = getCertificateNumber("1564471869322");
		System.out.println(certificateNumber);
	}
}
