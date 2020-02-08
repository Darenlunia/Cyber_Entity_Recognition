package org.cyber.regexpTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @Description WhiteList
 * 其中，(map)whiteList的key为pattern（URL/HOST...），value为whitelist中每个ini文件对应的正则表达式解析（编译）值
 *
 * 
 */
public class WhiteList {

	private HashMap<String, List<Pattern>> whiteList=new HashMap<String, List<Pattern>>();

	public WhiteList() {//不知道是否能重写无参构造函数？？
		try {
			this.compileWhiteList();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 * @throws IOException
	 * @basedir：白名单ini所在路径 获取白名单中的所有patterns 返回map，key为ini文件名，value为编译后的正则表达式
	 * 
	 */
	public void compileWhiteList() throws IOException {
		String basedir = "G:\\extraEclipseWork\\ioc-parse\\wlini\\whitelists";
		File file = new File(basedir);
		String[] iniFileArray = file.list();// fileName是whitelists下所有ini文件名称列表
		List<Pattern> patternList=new ArrayList<Pattern>();
		for (int i = 0; i < iniFileArray.length; i++) { // i为其中一个白名单
			// 分割出白名单主名称,用_分割成两部分，取前面的，放入key
			String key = iniFileArray[i].substring(0,iniFileArray[i].lastIndexOf("."));//获取除后缀1位的名称
			key = key.substring(key.lastIndexOf("_")+1);//获取后缀名
			//System.out.println(key);
			String searchdir = basedir + "\\" + iniFileArray[i];
			// System.out.print(fileName[i]);
			FileReader fr = new FileReader(searchdir);
			BufferedReader in = new BufferedReader(fr);// 读取某个白名单
			String temp = "";
			if (in.readLine() == null)//按行编译
				continue;
			while ((temp = in.readLine()) != null) {
				// System.out.println(temp);
				temp.replaceAll("\n", "");//处理每行数据并编译，并放入list
				Pattern pattern = Pattern.compile(temp);
				patternList.add(pattern);
			}
//			System.out.println("处理的pattern"+i+"..."+key+"...");
			whiteList.put(key, patternList);
			in.close();
		}
		
	}
	
	/**
	 * 输出白名单
	 * @return
	 */
	public HashMap<String, List<Pattern>> retWhiteList(){
		System.out.println("whiteList"+whiteList);
		return whiteList;
	}
	
	/**
	 * 没调用，改成了下面的函数
	 * 返回 的value为(map)whitelist每个ini文件对应每行的解析值（双层list）
	 * @return
	 */
	public List<List<Pattern>> retValue() {
		List<List<Pattern>> list = new ArrayList<List<Pattern>>(); 
		for (int i = 0; i < whiteList.size(); i++) {//将whietList的value值之List<Pattern>，放入新list并返回
			String key = "" + i;
			list.add(whiteList.get(key));//突然觉得双层的List可以取消外层LIST是各个ini文件，内层是每一行，其实可以合并成所有行，减少一层循环
		}
		return list;
		
	}
	
	/**
	 * 通过输入whitelist文件夹下ini文件的类型（url/host...）称查找对应ini文件的解析值
	 * 通过输入key值返回对应ini文件的解析值，放入list
	 */	
	public List<Pattern> getValue(String key) {
		List<Pattern> list = new ArrayList<Pattern>();
		list=whiteList.get(key);
		return list;
	}
}
