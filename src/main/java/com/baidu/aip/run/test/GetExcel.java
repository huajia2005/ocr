package com.baidu.aip.run.test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.alibaba.fastjson.JSON;
import com.baidu.aip.run.entity.Shieldkw;
import com.baidu.aip.run.entity.GaShieldkw;

/**
 * 用于将关键字数据导入数据库
 * @author zyl
 * @date 2019年6月5日
 */
public class GetExcel {
	public static void main(String[] args) throws Exception {
		String excel1 = "D:\\QQfile\\1154929934\\FileRecv\\屏蔽关键词2019-05-05 (1).xlsx";
		String excel2 = "D:\\QQfile\\1154929934\\FileRecv\\新建文件夹\\公安部关键词\\公安部关键词\\搜索型关键词.xls";
		InputStream is = new FileInputStream(excel1);
		String[] split = excel1.split("\\.");
		if(split[1].equals("xlsx")) {
			System.out.println("您已进入XSSFWorkbook类型方法...");
			List<List<String>> resultList = getXSSFWorkbook(is,3);
			List<Shieldkw> intoDataExcel1 = intoDataExcel1(resultList);
			System.out.println("最终组装的数据为："+JSON.toJSONString(intoDataExcel1));
		}else {
			System.out.println("您已进入HSSFWorkbook类型方法...");
			List<List<String>> list = getHSSFWorkbook(is);
			List<GaShieldkw> intoDataExcel2 = intoDataExcel2(list);
			System.out.println(JSON.toJSONString(intoDataExcel2));
		}
	}
	
	public static List<List<String>> getHSSFWorkbook(InputStream is) throws IOException {
		// HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        // 循环每一页，并处理当前循环页
        for (int numSheet = 0; numSheet < size; numSheet++) {
            // HSSFSheet 标识某一页
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    HSSFCell cell = hssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(cell.toString());
                }
                result.add(rowList);
            }
        }
        return result;
	}
	
	public static List<List<String>> getXSSFWorkbook(InputStream is, Integer index) throws Exception {
		// HSSFWorkbook 标识整个excel
		XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
		List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        List<List<String>> fristRow = getFristRow();
        List<String> list = fristRow.get(0);
        // 循环每一页，并处理当前循环页
        for (int numSheet = 0; numSheet < size; numSheet++) {
            // HSSFSheet 标识某一页
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = index; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                System.out.println("进入单元格便利阶段："+minColIx);
                List<String> rowList = new ArrayList<String>();
                // 遍历该行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    XSSFCell cell = hssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
					if (colIx % 2 == 1) {
						rowList.add(cell.toString());
						// 匹配关键字类型
						for (int i = 0; i < list.size(); i++) {
							String[] split = list.get(i).split("。");
							String[] domain = split[1].split(",");
							if (Integer.parseInt(domain[0]) <= colIx && colIx <= Integer.parseInt(domain[1])) {
								rowList.add(list.get(i).toString().split("。")[0]);
								break;
							}
						}
						// 匹配关键字词性
						if(colIx%3==1) {
							rowList.add("动词");
						}else if(colIx%3==0) {
							rowList.add("名词");
						}else {
							rowList.add("专属词语");
						}
					}else {
						rowList.add(cell.toString());
					}
				}
				result.add(rowList);
				break;
			}
        }
        return result;
	}
	
	public static List<List<String>> getFristRow() throws Exception {
		Integer a = 0;
		Integer b = 5;
        InputStream is = new FileInputStream("D:\\QQfile\\1154929934\\FileRecv\\屏蔽关键词2019-05-05 (1).xlsx");
        // HSSFWorkbook 标识整个excel
        XSSFWorkbook hssfWorkbook = new XSSFWorkbook(is);
        List<List<String>> result = new ArrayList<List<String>>();
        int size = hssfWorkbook.getNumberOfSheets();
        // 循环每一页，并处理当前循环页
        for (int numSheet = 0; numSheet < size; numSheet++) {
            // HSSFSheet 标识某一页
            XSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // 处理当前页，循环读取每一行
            for (int rowNum = 0; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                // HSSFRow表示行
                XSSFRow hssfRow = hssfSheet.getRow(rowNum);
                int minColIx = hssfRow.getFirstCellNum();
                int maxColIx = hssfRow.getLastCellNum();
                List<String> rowList = new ArrayList<String>();
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    XSSFCell cell = hssfRow.getCell(colIx);
					if (cell == null) {
						continue;
					}
					rowList.add(cell.toString() + "。" + a + "," + b);
					a = a + 6;
					b = b + 6;
                }
                result.add(rowList);
                break;
            }
        }
        return result;
    }
	
	public static List<Shieldkw> intoDataExcel1(List<List<String>> list) throws ParseException {
		Integer a = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		List<String> dataList = list.get(0);
		System.out.println(JSON.toJSONString(dataList));
		List<Shieldkw> excelList = new ArrayList<Shieldkw>();
		System.out.println(dataList.size());
		for (int i = 0; i <= dataList.size() / 4 - 1; i++) {
			System.out.println(a);
			Shieldkw excel1 = new Shieldkw();
			excel1.setKwName(dataList.get(a));
			excel1.setCreateTime(sdf.parse(dataList.get(a + 1)));
			excel1.setKwType(dataList.get(a + 2));
			excel1.setPartOfSpeech(dataList.get(a + 3));
			excelList.add(excel1);
			a = a + 4;
		}
		
		System.out.println("鉆裝的數據一共有"+excelList.size());
		System.out.println(JSON.toJSONString(excelList));
		
		return excelList;
	}
	
	public static List<GaShieldkw> intoDataExcel2(List<List<String>> list) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
		List<GaShieldkw> excelList = new ArrayList<GaShieldkw>();
		System.out.println(list.size());
		System.out.println(JSON.toJSONString(list));
		for (int i = 0; i <= list.size() - 1; i++) {
			GaShieldkw excel2 = new GaShieldkw();
			if (list.get(i).size() == 4) {
				excel2.setKwName(list.get(i).get(0));
				excel2.setLevelOne("");
				excel2.setLevelTwo("");
				excel2.setReportingTime(sdf.parse(list.get(i).get(2)));
				excel2.setCreateTime(sdf.parse(list.get(i).get(3)));
				excelList.add(excel2);
				continue;
			}
			excel2.setKwName(list.get(i).get(0));
			excel2.setLevelOne(list.get(i).get(1));
			excel2.setLevelTwo(list.get(i).get(2));
			if (!list.get(i).get(3).equals("") && list.get(i).get(3) != null) {
				excel2.setReportingTime(sdf.parse(list.get(i).get(3)));
			}
			excel2.setCreateTime(sdf.parse(list.get(i).get(4)));
			excelList.add(excel2);
		}

		System.out.println("鉆裝的數據一共有" + excelList.size());
		System.out.println(JSON.toJSONString(excelList));

		return excelList;
	}
}
