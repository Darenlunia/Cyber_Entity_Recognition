package org.cyber.memmTag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.cyber.dictTag.PreEntityExtract;
import org.cyber.util.GetTextFromPdf;

import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 生成分词结果
 * @author Administrator
 *
 */
public class HandTagTool {
	
	
	public static void excutedata(String data)throws IOException{
		PreEntityExtract ee=new PreEntityExtract();
		List<Term> list=ee.getShortSegment2(data);
		ee. allPretreatment(list);
	    for(Term tm:list) {
	    	System.out.println(tm.toString()+"	O");
	    }
        System.out.println("*******end*******");
	}
	

	public static void excute(String article,String data)throws IOException{
		PreEntityExtract ee=new PreEntityExtract();
		List<Term> list=ee.getShortSegment2(data);
		ee. allPretreatment(list);
//		String newPath=System.getProperty("user.dir") + "\\memmConfig\\Model\\手工数据处理\\"+article+".txt";
		String newPath="G:\\extraEclipseWork\\modeltest\\memmConfig\\trainningword\\"+article+".txt";
	    BufferedWriter out = new BufferedWriter(new FileWriter(new File(newPath),true)); 
	    for(Term tm:list)
	       out.write(tm.toString()+"	O"+"\r\n");
        out.flush(); // 缓存区内容压入文件  
        out.close(); 
        System.out.println("*******end*******");
	}
	

	public static void main(String[] arg) throws IOException {
//		String articlePath= System.getProperty("user.dir") +"\\memmConfig\\Model\\手工数据处理\\原始数据.txt";
//		BufferedReader br = new BufferedReader(new FileReader(new File(articlePath)));
//		String line = br.readLine();
//		String data="";
		
//		while (line != null){
//			data=data+line;
//			line = br.readLine(); 
//		}
		
		String data="此后利用这些服务器对SK Networks公司的电脑管理系统进行黑客攻击，并向企业电脑植入了“灵鼠”(ghost rat)病毒。";
//		HandTagTool.excute("未处理",data);//文章读取：source=“期-y页-篇”，每页从左到右，从上到下
		HandTagTool.excutedata(data);
		
	}
}
