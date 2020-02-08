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
 * ��ĳ�ʵ�ǰָʾ�ʺͺ�ָʾ��ʶ���������config��ָʾ���ļ�
 * @author Administrator
 *
 */
public class BorderRecognition {
	
	private String label;
	private String keyWord;
	
	/**
	 * Ҫȡ����keyWord�ʵı߽�
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
			//***************����������Լ��ֱ�ע�ĺ�����ʵ���ע�ķִ�����***************//��ʡ��ʹ����hanks��
			PrintWriter pw = new PrintWriter(new FileWriter("G:\\extraEclipseWork\\CyberEntity\\memmConfig\\ָʾ��\\preBorder\\"+label+"ǰָʾ��.txt"));
			//��ǰָʾ�ʵ����λ��
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
			//***************����������Լ��ֱ�ע�ĺ�����ʵ���ע�ķִ�����***************//��ʡ��ʹ����hanks��
			PrintWriter pw = new PrintWriter(new FileWriter("G:\\extraEclipseWork\\CyberEntity\\memmConfig\\preBorder\\"+label+"��ָʾ��.txt"));
			//����ָʾ�ʵ����λ��
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
//		BorderRecognition br = new BorderRecognition("��ȫ�¼�","������貽");
//		br.getPreBorder();
//		br.getRearBorder();
		
		
	//�����ǲ���һ���ļ����룬����ΪtrueΪ�����ǵ�׷������
//		String newPath1 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\�Ҿ�����.txt";// ת��������
//		File writename1 = new File(newPath1); // ���·�������û����Ҫ����һ���µ�output.txt�ļ�
//		BufferedWriter out1 = new BufferedWriter(new FileWriter(writename1,true));
//		out1.write("\r\n"+"��Ҳ���԰�");
//		out1.write("\r\n"+"fdfasf");
//		out1.close();
//		System.out.println("***end****");
//	}
}
