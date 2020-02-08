package org.cyber.memmTag;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cyber.memmTag.GetFeatures;
import org.cyber.memmTag.Word;

/**
 * 转换数据，从原始手工标注的数据再到获取特征后的数据
 * 
 * @author Administrator
 *
 */

public class TransferCorpus {

	private String pathAfterTransfer;
	private String labelPredictBeforeTransfer;

	public String getPathAfterTransfer() {
		return pathAfterTransfer;
	}

	public String getLabelPredictBeforeTransfer() {
		return labelPredictBeforeTransfer;
	}

	/**
	 * 
	 * @throws IOException
	 */
	public TransferCorpus() throws IOException {
		String pathBeforeTransfer = System.getProperty("user.dir") + "\\memmConfig\\Model\\featureBeforeTransfer.dat"; 
		List<Word> sentences = this.loadCorpus(pathBeforeTransfer,false);
		pathAfterTransfer = System.getProperty("user.dir") + "\\memmConfig\\Model\\featureAfterTransfer.dat";
		this.getTransferCorpus(pathAfterTransfer, sentences);
	}

	/**	 
	 * 读取原始数据放入sentences
	 * rich为是否将测试结果中发现的新词加入用户自定义词典
	 * @throws IOException
	 */
	public List<Word> loadCorpus(String path,Boolean rich) throws IOException {
		List<Word> sentences = new ArrayList<Word>();
		
		String newPath1 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\专有名词.txt";//未分类库
		String newPath2 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\黑客组织.txt";
		String newPath3 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\组织机构.txt";
		String newPath4 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\威胁.txt";
		String newPath5 = System.getProperty("user.dir") + "\\data\\dictionary\\custom\\事件.txt";
		File writename1 = new File(newPath1);
		File writename2 = new File(newPath2);
		File writename3 = new File(newPath3);
		File writename4 = new File(newPath4);
		File writename5 = new File(newPath5);
		BufferedWriter out1 = new BufferedWriter(new FileWriter(writename1, true));
		BufferedWriter out2 = new BufferedWriter(new FileWriter(writename2, true));
		BufferedWriter out3 = new BufferedWriter(new FileWriter(writename3, true));
		BufferedWriter out4 = new BufferedWriter(new FileWriter(writename4, true));
		BufferedWriter out5 = new BufferedWriter(new FileWriter(writename5, true));
		
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));  
		String line=null;  
		while((line=br.readLine())!=null)  
		{  
			String segs[] = line.split("	");
			String[] term = segs[0].split("/");
			System.out.println(term[0]+term[1]+segs[1]);
			if(rich==true){
				if (segs[1].equals("S-nz")) {
					out1.write(term[0] + "\r\n");
					segs[1] = "O";
				} else if (segs[1].equals("B-nz")) {
					String res = term[0];
					segs[1] = "O";
					sentences.add(new Word(term[0], term[1], segs[1]));
					while (true) {
						line = br.readLine();
						String segss[] = line.split("	");
						String[] terms = segss[0].split("/");
						if (segss[1].equals("E-nz")) {
							segs[1] = "O";
							res = res + terms[0];
							out1.write(res + "\r\n");
							break;
						}
						segs[1] = "O";
						sentences.add(new Word(term[0], term[1], segs[1]));
						res = res + terms[0];
					}
				} else if (segs[1].equals("S-nhack")) {
					out2.write(term[0] + "\r\n");
				} else if (segs[1].equals("B-nhack")) {
					String res = term[0];
					sentences.add(new Word(term[0], term[1], segs[1]));
					while (true) {
						line = br.readLine();
						String segss[] = line.split("	");
						String[] terms = segss[0].split("/");
						if (segss[1].equals("E-nhack")) {
							res = res + terms[0];
							out2.write(res + "\r\n");
							break;
						}
						sentences.add(new Word(term[0], term[1], segs[1]));
						res = res + terms[0];
					}
				} else if (segs[1].equals("S-nthreat")) {
					out3.write(term[0] + "\r\n");
				} else if (segs[1].equals("B-nthreat")) {
					String res = term[0];
					sentences.add(new Word(term[0], term[1], segs[1]));
					while (true) {
						line = br.readLine();
						String segss[] = line.split("	");
						String[] terms = segss[0].split("/");
						if (segss[1].equals("E-nthreat")) {
							res = res + terms[0];
							out3.write(res + "\r\n");
							break;
						}
						sentences.add(new Word(term[0], term[1], segs[1]));
						res = res + terms[0];
					}
				} else if (segs[1].equals("S-norg")) {
					out4.write(term[0] + "\r\n");
				} else if (segs[1].equals("B-norg")) {
					String res = term[0];
					sentences.add(new Word(term[0], term[1], segs[1]));
					while (true) {
						line = br.readLine();
						String segss[] = line.split("	");
						String[] terms = segss[0].split("/");
						if (segss[1].equals("E-norg")) {
							res = res + terms[0];
							out4.write(res + "\r\n");
							break;
						}
						sentences.add(new Word(term[0], term[1], segs[1]));
						res = res + terms[0];
					}
				}else if (segs[1].equals("S-nevent")) {
					out5.write(term[0] + "\r\n");
				} else if (segs[1].equals("B-nevent")) {
					String res = term[0];
					sentences.add(new Word(term[0], term[1], segs[1]));
					while (true) {
						line = br.readLine();
						String segss[] = line.split("	");
						String[] terms = segss[0].split("/");
						if (segss[1].equals("E-nevent")) {
							res = res + terms[0];
							out5.write(res + "\r\n");
							break;
						}
						sentences.add(new Word(term[0], term[1], segs[1]));
						res = res + terms[0];
					}
				}
			}
			sentences.add(new Word(term[0], term[1], segs[1]));
		}
		out1.close();
		out2.close();
		out3.close();
		out4.close();
		out5.close();
		br.close();
		return sentences;
	}

	public void getTransferCorpus(String newPath, List<Word> sentences) throws IOException {
		GetFeatures w = new GetFeatures(sentences);
		File writename = new File(newPath); 
		if (!writename.exists())
			writename.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(writename));
		for (int i = 0; i < sentences.size(); i++) {
			String featureStr = "";
			for (String str : w.getFeatureList().get(i)) {
				featureStr = featureStr + str + " ";
			}
			out.write(featureStr + sentences.get(i).tag + "\r\n");
		}
		out.flush(); 
		out.close(); 
	}

	
	/**
	 * 特征选择
	 * @throws IOException 
	 */
		public void selectFeaturelist(String newPath, List<Word> sentences) throws IOException{
			GetFeatures w = new GetFeatures(sentences);
			File writename = new File(newPath); 
			if (!writename.exists())
				writename.createNewFile();
			BufferedWriter out = new BufferedWriter(new FileWriter(writename));
			String featureStr = "";
			
			//下面这段是新加的
			HashMap<String,Integer> map =new HashMap<String,Integer>();
			for (int i = 0; i < sentences.size(); i++) {
				for (String str : w.getFeatureList().get(i)) {
					if(map.containsKey(str))
						map.put(str, map.get(str)+1);
					else map.put(str, 1);
				}
			}
			
			for (int i = 0; i < sentences.size(); i++) {
				for (String str : w.getFeatureList().get(i)) {
					if(map.get(str)>1)//（可以在此设置阈值，先设置为2，即>1）
						featureStr = featureStr + str + " ";
				}
				out.write(featureStr + sentences.get(i).tag + "\r\n");
			}
			
			out.flush(); 
			out.close(); 
		}
		
	
	/**
	 * 
	 * 对path路径的文件去重
	 */
	public void deleteRepeatFromFile(String path) throws IOException{
		BufferedReader br1=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));  
		Set<String> setStr = new HashSet<String>();
		String line1=null;  
		while((line1=br1.readLine())!=null)  
		{  
			setStr.add(line1);
		}  
		br1.close(); 
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));   
		for(String str : setStr){
			bw.write(str);  
		}
		bw.close();  
	}
	
	public static void main(String arg[]) {
		try {
			new TransferCorpus();
			System.out.println("***end***");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
