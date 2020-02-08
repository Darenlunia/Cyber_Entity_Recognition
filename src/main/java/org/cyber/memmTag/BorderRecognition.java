package org.cyber.memmTag;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.cyber.dictTag.PreEntityExtract;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 将某词的前指示词和后指示词识别出来放入config的指示词文件
 * @author Administrator
 *
 */
public class BorderRecognition {
	
	private String label;
	private String keyWord;
	
	/**
	 * 要取的是keyWord词的边界
	 * @param keyWord
	 */
	public BorderRecognition(String label,String keyWord){
		this.label=label;
		this.keyWord=keyWord;
	}

	public void getPreBorder() {
		try {
			PreEntityExtract ee = new PreEntityExtract();
			Map<String, Integer> map = new HashMap<String, Integer>();
			List<Term> list = ee.getShortSegment("G:\\extraEclipseWork\\CyberEntity\\wlpdf\\WhiteElephant.pdf");
			//***************这里最好是自己分标注的含命名实体标注的分词序列***************//但省事使用了hanks的
			PrintWriter pw = new PrintWriter(new FileWriter("G:\\extraEclipseWork\\CyberEntity\\memmConfig\\指示词\\preBorder\\"+label+"前指示词.txt"));
			//↑前指示词的输出位置
			for (int i = 0; i < list.size(); i++) {
				if (i == 0) {
					continue;
				}
				if (list.get(i).word.equals(keyWord)) {
					if(i==1){
						if (map.containsKey(list.get(i - 2).word))
							map.put(list.get(i - 2).word, map.get(list.get(i - 2).word) + 1);
						else
							map.put(list.get(i - 2).word, 0);
					}
					if (map.containsKey(list.get(i - 1).word))
						map.put(list.get(i - 1).word, map.get(list.get(i - 1).word) + 1);
					else
						map.put(list.get(i - 1).word, 0);
				}
			}
			 for (Map.Entry<String, Integer> entry : map.entrySet()) {  
				   if(entry.getValue()>=0){
					   pw.println(entry.getKey().toString());
				   } 
				  }  
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void getRearBorder() {
		try {
			PreEntityExtract ee = new PreEntityExtract();
			Map<String, Integer> map = new HashMap<String, Integer>();
			List<Term> list = ee.getShortSegment("G:\\extraEclipseWork\\CyberEntity\\wlpdf\\WhiteElephant.pdf");
			//***************这里最好是自己分标注的含命名实体标注的分词序列***************//但省事使用了hanks的
			PrintWriter pw = new PrintWriter(new FileWriter("G:\\extraEclipseWork\\CyberEntity\\memmConfig\\preBorder\\"+label+"后指示词.txt"));
			//↑后指示词的输出位置
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).word.equals(keyWord)) {
					if(i<list.size()-2){
						if (map.containsKey(list.get(i + 2).word))
							map.put(list.get(i + 2).word, map.get(list.get(i + 2).word) + 1);
						else
							map.put(list.get(i + 2).word, 0);
					}
					if(i<list.size()-1){
						if (map.containsKey(list.get(i + 1).word))
							map.put(list.get(i + 1).word, map.get(list.get(i + 1).word) + 1);
						else
							map.put(list.get(i + 1).word, 0);
					}
				}
			}
			 for (Map.Entry<String, Integer> entry : map.entrySet()) {  
				   if(entry.getValue()>=0){
					   pw.println(entry.getKey().toString());
				   } 
				  }  
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public static void main(String arg[]) throws IOException {
//		BorderRecognition br = new BorderRecognition("安全事件","白象的舞步");
//		br.getPreBorder();
//		br.getRearBorder();
		
		
	//下面是测试一波文件输入，设置为true为不覆盖的追加输入
//		String newPath1 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\我就试试.txt";// 转换后语料
//		File writename1 = new File(newPath1); // 相对路径，如果没有则要建立一个新的output.txt文件
//		BufferedWriter out1 = new BufferedWriter(new FileWriter(writename1,true));
//		out1.write("\r\n"+"我也试试把");
//		out1.write("\r\n"+"fdfasf");
//		out1.close();
//		System.out.println("***end****");
//	}
}
