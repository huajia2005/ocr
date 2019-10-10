package com.baidu.aip.run.test;

import com.alibaba.fastjson.JSON;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
   * 利用Java代码给图片加水印（搜索关键词）
 * @author zyl
 * @date 2019年6月17日
 */
public class AddTextToImageByGaShieldkw {
	/**
     * @param srcImgPath 源图片路径
     * @param tarImgPath 保存的图片路径
     * @param waterMarkContent 水印内容
     * @param markContentColor 水印颜色
     * @param font 水印字体
     */
    public void addWaterMark(String srcImgPath, String tarImgPath, String waterMarkContent,Color markContentColor,Font font,int i) {

        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色
            g.setFont(font);              //设置字体

            //设置水印的坐标
            int x = srcImgWidth - 12*getWatermarkLength(waterMarkContent, g);  
            int y = srcImgHeight - 8*getWatermarkLength(waterMarkContent, g);  
            g.drawString(waterMarkContent, x, y);  //画出水印
            g.dispose();  
            // 输出图片  
            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);  
            ImageIO.write(bufImg, "jpg", outImgStream);
            System.out.println("第"+i+"次添加水印完成");  
            outImgStream.flush();  
            outImgStream.close();  

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
    
    public int getWatermarkLength(String waterMarkContent, Graphics2D g) {  
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, 1);  
    }
    
//    public static void main(String[] args) {
//        Font font = new Font("微软雅黑", Font.PLAIN, 50);                     //水印字体
//        String srcImgPath="C:\\Users\\Lenovo\\Desktop\\imageDirPath\\test5.png"; //源图片地址
//        String tarImgPath="C:\\Users\\Lenovo\\Desktop\\zz.png"; //待存储的地址
//        String waterMarkContent="你RKI系列哈哈哈哈哈哈哈哈哈";  //水印内容
//        Color color=new Color(255,0,0);                               //水印图片色彩以及透明度
//        new AddTextToImageByGaShieldkw().addWaterMark(srcImgPath, tarImgPath, waterMarkContent,color, font,1);
//
//    }
    
    public static void main(String[] args) throws Exception {
		String excel2 = "E:\\网安需求文档\\app图片检测\\相关文件\\公安部关键词\\公安部关键词\\搜索型关键词.xls";
		List<String> contentList = getExcel(excel2);
		
    	// 指定专门用于存放图片的文件目录
    	String imageDirPath = "C:\\Users\\Lenovo\\Desktop\\样本2";
    	String tarImgPath="C:\\Users\\Lenovo\\Desktop\\文字图片"; //待存储的地址
    	Font font = new Font("微软雅黑", Font.PLAIN, 50);                     //水印字体
    	
    	File file = new File(imageDirPath);
		String[] filelist = file.list();
		int j = 0;
		for (int i = 0; i < contentList.size(); i++) {
			if(j == filelist.length) {
	        	j = 0;
	        }
			// 如果关键字第一个为字母则加一个汉字
			if(isNumeric((String)contentList.get(i).subSequence(0, 1))) {
				Color color=new Color(255,0,0);                               //水印图片色彩以及透明度
		        new AddTextToImageByGaShieldkw().addWaterMark(
		        		imageDirPath+"\\"+filelist[j], 
		        		tarImgPath+"\\"+i+new SimpleDateFormat("yyyymmddHHmmssSSS").format(new Date())+".png", 
		        		"你"+contentList.get(i),
		        		color, 
		        		font,
		        		i+1);
		        j++;
		        continue;
			}
			Color color=new Color(255,0,0);                               //水印图片色彩以及透明度
	        new AddTextToImageByGaShieldkw().addWaterMark(
	        		imageDirPath+"\\"+filelist[j], 
	        		tarImgPath+"\\"+i+new SimpleDateFormat("yyyymmddHHmmssSSS").format(new Date())+".png", 
	        		contentList.get(i),
	        		color, 
	        		font,
	        		i+1);
	        j++;
		}
	}
    
    public static List<String> getExcel(String excelUrl) throws Exception {
		InputStream is = new FileInputStream(excelUrl);
		String[] split = excelUrl.split("\\.");
		if(split[1].equals("xls")) {
			System.out.println("您已进入HSSFWorkbook类型方法...");
			List<String> list = getHSSFWorkbook(is);
			System.out.println(JSON.toJSONString(list));
			return list;
		}
		return null;
	}
    
    public static List<String> getHSSFWorkbook(InputStream is) throws IOException {
		// HSSFWorkbook 标识整个excel
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        List<String> rowList = new ArrayList<String>();
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
                // 遍历改行，获取处理每个cell元素
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    // HSSFCell 表示单元格
                    HSSFCell cell = hssfRow.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    if(colIx == 0) {
                    	rowList.add(cell.toString());
                    }else {
                    	continue;
                    }
                }
            }
        }
        return rowList;
	}
	
	public static boolean isNumeric(String str){  
		String regex = "^[a-z0-9A-Z]+$";
        return str.matches(regex);
	}
}
