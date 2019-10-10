package com.baidu.aip.run.test;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.IndexAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 使用ansj插件对从图片提取出的文字进行分词
 * @author zyl
 * @date 2019年6月5日
 */
public class AnsjTest {

    public static void test() {
        //只关注这些词性的词
        Set<String> expectedNature = new HashSet<String>() {{
				add("n");add("v");add("vd");add("vn");add("vf");
				add("vx");add("vi");add("vl");add("vg");
            add("nt");add("nz");add("nw");add("nl");
            add("ng");add("userDefine");add("wh");
        }};
        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!" ;
        Result result = ToAnalysis.parse(str); //分词结果的一个封装，主要是一个List<Term>的terms
        System.out.println(result.getTerms());

        List<Term> terms = result.getTerms(); //拿到terms
        System.out.println(terms.size());

        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
            if(expectedNature.contains(natureStr)) {
                System.out.println(word + ":" + natureStr);
            }
        }
    }

		public static String splitWords(String value) {
			// 关闭名字识别
			MyStaticValue.isNameRecognition = false;
			// 配置自定义词典的位置。注意是绝对路径
			MyStaticValue.ENV.put(DicLibrary.DEFAULT,
					"D:\\ideaproject\\ocr\\src\\main\\resources\\library\\userLibrary1.dic");
			StringBuffer stringBuffer = new StringBuffer();
//		String str = "这是一段测试文字非主流";
//		DicLibrary.insert(DicLibrary.DEFAULT, "色久久桃花综合");//设置自定义分词
//		DicLibrary.insert(DicLibrary.DEFAULT, "买秃鹰pcp配件");//设置自定义分词
		
		//去停用词
		List<String> stopWords = getStopWords("D:\\ideaproject\\ocr\\src\\main\\resources\\library\\stopLibrary.dic");
		StopRecognition filter = new StopRecognition();
		filter.insertStopWords(stopWords);
		
		Result result = IndexAnalysis.parse(value);
		List<Term> termList = result.getTerms();
		for (Term term : termList) {
//			System.out.println(term.getName() + ":" + term.getNatureStr());
			System.out.println(term.getName());
			stringBuffer.append(term.getName()+" ");
		}
		return stringBuffer.toString();
	}
	
	private static List<String> getStopWords(String url){
		// 使用一个字符串集合来存储文本中的路径 ，也可用String []数组
        List<String> list = new ArrayList<String>();
        try {
        	FileInputStream fis = new FileInputStream(url);
        	// 防止路径乱码   如果utf-8 乱码  改GBK     eclipse里创建的txt  用UTF-8，在电脑上自己创建的txt  用GBK
        	InputStreamReader isr = new InputStreamReader(fis, "GBK");
        	BufferedReader br = new BufferedReader(isr);
        	String line = "";
        	while ((line = br.readLine()) != null) {
        		// 如果 t x t文件里的路径 不包含---字符串       这里是对里面的内容进行一个筛选
        		if (line.lastIndexOf("---") < 0) {
        			list.add(line);
        		}
        	}
        	br.close();
        	isr.close();
        	fis.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static void main(String[] args) {
		String data = "紧随其后 还珠格格 系列剧 20世纪 90年代末 新世纪 开启 形成 一股 青春 新潮流";
		String splitWords = splitWords(data);
		// 将多个空格替换成一个空格
		String contnet = splitWords.replaceAll("\\s+", " ");
		System.out.println(contnet.trim());
//		test();
//		test1();
	}
}