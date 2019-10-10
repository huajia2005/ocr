package com.baidu.aip.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * 执行shell脚本的工具类
 * 
 * @author Sean.Yang
 *
 */
public class ShellUtil {
	/**
	 * 执行shell脚本
	 *
	 * @param shStr 需要执行的shell, shStr包括需要执行的shell脚本的路径及参数，空格分隔
	 * @return 返回执行脚本返回的每一行数据
	 * @throws IOException
	 * 
	 */
	public static String runShell(String shStr) throws Exception {
		String returnCode = "";
		try {
//			Process process = Runtime.getRuntime().exec("chmod 755 /tmp/upgrade.sh");
//			process.waitFor();
			// test2.sh是要执行的shell文件，param1参数值，test2.sh和param1之间要有空格
			// 多个参数可以在param1后面继续增加，但不要忘记空格！！
			Process process = Runtime.getRuntime().exec(new String[] { "/bin/sh", "-c", shStr });
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			StringBuffer sb = new StringBuffer("");
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			br.close();
			System.out.println("调用shell脚本的返回值为："+sb.toString());
			returnCode = process.waitFor() + "";
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return returnCode;
	}
}
