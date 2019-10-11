package com.baidu.aip.util;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 使用ansj插件对从图片提取出的文字进行分词
 * @author zyl
 * @date 2019年6月5日
 */
@Component
public class AnsjTest {

	@Value("${userLibraryPath}")
	private String userLibraryPath;

	@Value("${stopLibraryPath}")
	private String stopLibraryPath;

	public static void main(String[] args) {
		// 关闭名字识别
		MyStaticValue.isNameRecognition = false;
		// 配置自定义词典的位置。注意是绝对路径
		MyStaticValue.ENV.put(DicLibrary.DEFAULT,"D:\\ideaproject\\ocr\\src\\main\\resources\\userLibrary.dic");

		//去停用词
		List<String> stopWords = getStopWords("D:\\ideaproject\\ocr\\src\\main\\resources\\library\\stopLibrary.dic");
		StopRecognition filter = new StopRecognition();
		filter.insertStopWords(stopWords);

        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!" ;
        Result result = ToAnalysis.parse(str).recognition(filter); //分词结果的一个封装，主要是一个List<Term>的terms
        System.out.println(result.getTerms());

        List<Term> terms = result.getTerms(); //拿到terms
        System.out.println(terms.size());

        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            String natureStr = terms.get(i).getNatureStr(); //拿到词性
			System.out.println(word + ":" + natureStr);
        }
    }

		public String splitWords(String value) {
			// 关闭名字识别
			MyStaticValue.isNameRecognition = false;
			// 配置自定义词典的位置。注意是绝对路径
			MyStaticValue.ENV.put(DicLibrary.DEFAULT,userLibraryPath);
			StringBuffer stringBuffer = new StringBuffer();
//		String str = "这是一段测试文字非主流";
//		DicLibrary.insert(DicLibrary.DEFAULT, "色久久桃花综合");//设置自定义分词
//		DicLibrary.insert(DicLibrary.DEFAULT, "买秃鹰pcp配件");//设置自定义分词
		
		//去停用词
		List<String> stopWords = getStopWords(stopLibraryPath);
		StopRecognition filter = new StopRecognition();
		filter.insertStopWords(stopWords);

		Result result = DicAnalysis.parse(value).recognition(filter);
			List<Term> termList = result.getTerms();
			for (Term term : termList) {
//			System.out.println(term.getName() + ":" + term.getNatureStr());
//				System.out.println(term.getName());
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
        	InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
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
	
//	public static void main(String[] args) {
//			String data = "很真实地欣赏着自己的心偶然间我与你面对面坐下来而你只是说出两个字明天现在我们俩只能在静寂中煎熬悲哀于瞬息之间其实人生并不重要高攀不起色久久桃花综合大国天销售信用卡复制器天色综合区防务买秃鹰pcp配件中国毕竟联通ESS系统身份证读卡器插件我们和你们...银行卡密码数据解码器设备atm机隔空采集器销售一比一枪模你好战术直刀网我爱你红牛青蛙黄货真的好爱我恨你MGM贵宾会对的成人USB影视棒hello请求购柯尔特m1911哈哈慌乱云少妇色之";
//			String splitWords = splitWords("男性吧之声你好色之啊哇喔苍老师的超时空双飞之旅行呀热搜");
//			System.out.println(splitWords);
//		test();
//		test1();
//	}
}