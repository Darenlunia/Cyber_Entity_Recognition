package org.cyber.regexpTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cyber.util.GetTextFromPdf;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

/**
 * @Title  模式匹配
 * @Description  pdf数据匹配ini文件正则表达式
 * @param 构造函数参数为要传入的正则表达式配置文件
 * @终极接口  printPatternResult(fpath)，参数为pdf的path
 * @author Administrator
 *
 */

public class PatternsMatch {

	private boolean repeatCheck = false;//默认不查重，查重需要设置repeat=true
	
	private Map<String, Pattern> patterns=new HashMap<String, Pattern>();
	// key为pattern名称，value-Pattern为编译后的字串
	private Map<String, Map<String, String>> temp=new HashMap<String, Map<String, String>>();// 等价于ConfigReader的属性值key,value;
	// 用于过度ConfigReader对象和patterns，patterns本来应该用ConfigReader直接表示的，修改麻烦所以才用了一个temp值。
	private Map<String, Boolean> defang= new HashMap<String, Boolean>();// key为pattern名称，value为有或无
	// private HashMap<String, Pattern> whiteList;
	private WhiteList whiteList= new WhiteList();
	// 白名单
	private String dir ="G:\\extraEclipseWork\\ioc-parse\\wlini\\patterns.ini";
	//默认配置文件
	private String data;
	//pdf数据，匹配处理后data将剔除已匹配的文字
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data=data;
	}
	/**
	 * 正则表达式  查重传入true
	 * @param repeat
	 */
	public void setRepeatCheck(boolean repeat) {//如果查重需要传入 true值
		this.repeatCheck = repeat;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
	public PatternsMatch() {
		this.loadPatterns();
	}
	
	/**
	 * 
	 * @param fpath  要抽取的pdf所在目录
	 */
	public PatternsMatch(String fpath) {//pattern.ini所在目录
		// TODO Auto-generated constructor stub
		this.loadPatterns();
		GetTextFromPdf gtfp = new GetTextFromPdf(fpath);
		this.data = gtfp.getResult();
	}
	
	/**
	 * 第一个参数用于区别构造函数，可以输入“test”
	 * @param teststring 
	 */
	public PatternsMatch(String str,String teststring) {//默认使用了pattern.ini所在目录
		// TODO Auto-generated constructor stub
		this.data = teststring;
		this.loadPatterns();	
	}
	

	/**
	 * @Description  构造函数式时调用。
	 * 读配置文件，对每个key的正则表达式进行编译并放入(Map)patterns与(Map)defang，
	 * 并且把cfr的key和value挨个放入temp
	 * @throws IOException
	 */
	public void loadPatterns() {
		//抽取配置文件
		ConfigReader cfr = new ConfigReader(dir);
		Iterator<?> it = cfr.get().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Map<String, String> value = (Map<String, String>) entry.getValue();
			temp.put(key, value);
			// 对每个key的pattern属性进行compile解析并放入pattern
			Pattern pattern = Pattern.compile(value.get("pattern"));
			patterns.put(key, pattern);
			if (value.containsKey("defang")) {
				defang.put(key, true);
			}
		}
		// System.out.println(patterns);
	}

	/**
	 * 没调用
	 * 匹配白名单
	 * @param matchs
	 * @return
	 */
	public boolean is_whitelisted(String matchs,String key) {
		if(whiteList.getValue(key)!=null){
			for (int i = 0; i < whiteList.getValue(key).size(); i++) {
				Pattern w = whiteList.getValue(key).get(i);
				if (w.matcher(matchs) != null) {
					return true;
				}
			}
		}		
		return false;
	}


	/**
	 * 匹配patterns.ini（whiteList下的ini被注释了）;匹配后提出正则表达式匹配的文本
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<String>> patternsMatch() throws IOException {
		System.out.println("正在进行正则表达式匹配... ...");
		Map<String, List<String>> iocs = new HashMap<String, List<String>>();// 记录二次匹配的全部结果
		// 对data的匹配操作
//		System.out.println("匹配内容："+data);
		Iterator<?> it = patterns.entrySet().iterator();
		while (it.hasNext()) {
			List<String> ioc = new ArrayList<String>();
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Pattern value = (Pattern) entry.getValue();
			System.out.println("正在匹配的pattern为" + key + "... ...");
			Matcher m = value.matcher(data);//匹配patterns.ini	
			String rec=temp.get(key).get("pattern").replace("\b", "");
			data=data.replaceAll(rec, " ** ");//把匹配完后的正则表达式用**表示
			Set<String> s = new HashSet<String>();//查重
			while (m.find()) {
				String mm = m.group(1);
//				if (is_whitelisted(m.group(1),key)) {//匹配whitelist/ini
//					continue;
//				}

				if ((temp.get(key).get("defang")) != null) {
					mm.replaceAll("\\[\\.\\]", ".");
				}
				if(repeatCheck){
					boolean b = s.add(mm);
					if (b == false) {
						continue;
					}
				}
				ioc.add(mm);
			}
			iocs.put(key, ioc);
		}
		//匹配分析报告
		Pattern analysisReportPattern = Pattern.compile(".*《(.+)\u5206\u6790\u62a5\u544a》.*");
		String AnalysisReport="AnalysisReport";
		System.out.println("正在匹配的pattern为" + AnalysisReport + "... ...");
		Matcher matcher = analysisReportPattern.matcher(data);
		List<String> ioc = new ArrayList<String>();
		while (matcher.find()) {
			String result = matcher.group(1);
			result="《"+result+"分析报告》";
			ioc.add(result);
		}
		iocs.put(AnalysisReport, ioc);
		return iocs;
	}

	
	/**
	 * 将data匹配patterns.ini，输出匹配后的词性
	 * @return
	 * @throws IOException
	 */
	public String patternsMatchOneWord(String data) throws IOException {
		Iterator<?> it = patterns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Pattern value = (Pattern) entry.getValue();
			Matcher m = value.matcher(data);//匹配patterns.ini
			if(m.find()) {
				String match=m.group(1);
				String mm = key;
//				Term st=new Term(match, Nature.create(mm));
				return mm;
			}
		}
		return null;
	}
		
	/**
	 * ********输出接口*********
	 * @Description 调用匹配函数，控制台循环输出pattern(url/host...)和对应完全匹配结果
	 * @throws IOException
	 */
	public void printPatternResult() throws IOException {
		Map<String, List<String>> iocs = patternsMatch();
		Iterator<?> it = iocs.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			ArrayList<?> value = (ArrayList<?>) entry.getValue();
			for (int i = 0; i < value.size(); i++) {
				System.out.println(key + "相对应的数据: " + value.get(i));
			}
		}
	}
	
	public static void main(String[] args){
		String str="据外媒报道，国家刑事调查局 ( CIB ) 向通过网络安全知识测试的公众奖励了 250 个 U 盘，随后该主办方陆续接到报告称“累计 54 个 8GB U 盘中都带有恶意软件”，该测试是去年 12 月 11 日至 15 日期间由台湾总统办公室主办的信息安全活动的一部分。该恶意软件被标记为 XtbSeDuA.exe ，能够从32 位计算机中窃取个人信息。根据 CIB 的说法，若是数据窃取成功该恶意软件会试图将数据传递到一个位于波兰的 IP地址，随后该 IP 地址将数据转发给身份不明的服务器。研究人员透露该恶意程序被认为是欧洲刑警组织在 2015 年发现的网络诈骗团伙使用的。CIB 表示，这种感染来源于当地承包商员工所使用的 “ 将操作系统转移到驱动器并测试其存储容量 ” 的工作站，其中有些驱动器是在中国大陆生产的";
		try {
			System.out.println(new PatternsMatch().patternsMatchOneWord("IP 121.121.121.121"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("****end****");
//		PatternsMatch pm=new PatternsMatch("test",str);
//		try {
//			pm.printPatternResult();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
