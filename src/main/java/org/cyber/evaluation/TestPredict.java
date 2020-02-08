package org.cyber.evaluation;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.cyber.memmTag.Predict;
import org.junit.Before;
import org.junit.Test;

public class TestPredict {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws IOException {

		String str="自由之家总裁Michael Abramowitz表示：�?�使用评论�?�水军�?�与政治机器人来传播政府宣传意见的作法最先是由俄罗斯等国采用，但现在已经在世界范围内普及�?来�?�这类迅速传播的�?术手段会对民主及民众行动产生破坏性的潜在影响。�??";
		String str2="维基揭秘�?新曝光的中情�?网络间谍活动的秘密文件代号为�?8号保险库”�?�文件内容包括：为了隐瞒电脑病毒的来源，中情�?研制出一款名叫�?�蜂巢�?�的非法源代码，";
		String str3="网络安全公司vira研究人员近期发现勒索软件 Locky出现新型变种，不仅可以�?�过僵尸网络Necurs�?展大规模钓鱼攻击活动�?";
		String str4="（箭头体现了恶意软件与上个月相比的变化趋势）↔RoughTed、↔Locky、↑Seamless、↑Zeus-银行木马、↑CoinHive - Crypto Miner、↑Ramnit -银行木马、↓Fireball、↓Pushdo、↑Andromeda 、Triada、LeakerLocker 、Hiddad�?";
		String str5="360网络安全响应中心�?360Cert）报�?1个漏洞�?�本月微软安全公告重点涉及Internet Explorer浏览器�?�Windows系统组件、Office软件中存在的多个安全漏洞�?";
		new Predict().getResult(str4);
		System.out.println("*************");
		
		/*无用的测试代码*/
//		String str="Arbor Network/nx	O";
//		String[] segs=str.split("	");
//		for(String i:segs)
//			System.out.println("i="+i);
		
		/*无用的测试代码*/
//		ArrayList<String> list =new ArrayList<String>();
//		list.add("asdddddddddddd");
//		list.add("asdddddddddddd");
//		list.add("qqqq");
//		list.add("asdddddddddddd");
//		list.add("sqq");
//		list.add("qdsfqq");
//		for(String str:list){
//			System.out.println(str);
//		}
//		System.out.println("     ddd ");
//		list.remove("asdddddddddddd");
//		for(String str:list){
//			System.out.println(str);
//		}
//		
		/*无用的测试代码*/
//		HashMap<String,String> map=new HashMap<String,String>();
//		map.put("璐璐", "可爱");
//		map.put("裤裤", "酷酷");
//		map.put("小狗", "哈哈");
//		HashMap<String,String> map1=new HashMap<String,String>();
//		map1.putAll(map);
//		map1.remove("小狗");
//		System.out.println(map.get("小狗"));
//		System.out.println(map1.get("小狗"));

		
 	}

}
