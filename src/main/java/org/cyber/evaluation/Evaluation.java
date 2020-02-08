package org.cyber.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cyber.crfTag.CrfTag;
import org.cyber.dictTag.PreEntityExtract;
import org.cyber.regexpTag.PatternsMatch;

import com.hankcs.hanlp.seg.common.Term;

/**
 * MEM性能评估 以及规则+词典+统计 总体评估
 * 
 * @author Administrator
 *
 */
public class Evaluation {

	// 再加一条评估最大熵模型速度

	public enum EnumNature { //
		SHA1, CNNVD, CNVD, Email, IPPORT, IP, Filename, Registry, Filepath, SHA256, CAN, AS, CVE, CNCVE, URL, Cert, Domain, PhoneNumeber, MD5, AnalysisReport, nr, nt, ns, nthreat, nhack, nreport, nevent, nvendor, nproduct, nvlundesc,;
	}

	/**
	 * 精确率
	 */
	public float precision;

	/**
	 * 召回率
	 */
	public float recall;

	/**
	 * F1
	 */
	public float f1;

	/**
	 * 正类被判别为正类
	 */
	public float tp;
	/**
	 * 正类被判别为负类为fp，此为tp+fp
	 */
	public float tpfp;
	/**
	 * 负类被判别为正类为fn,此为tp+fn
	 */
	public float tpfn;
	
	
	public float nrtp;
	public float nrtpfp;
	public float nrtpfn;
	public float nrprecision;
	public float nrrecall;
	public float nrf1;
	
	public float nttp=0;
	public float nttpfp=0;
	public float nttpfn=0;
	public float ntprecision=0;
	public float ntrecall=0;
	public float ntf1=0;
	
	public float IPtp=0;
	public float IPtpfp=0;
	public float IPtpfn=0;
	public float IPprecision=0;
	public float IPrecall=0;
	public float IPf1=0;

	public Evaluation() {
		this.tp = 0;
		this.tpfp = 0;
		this.tpfn = 0;
		this.precision= 0;
		this.recall = 0;
		this.f1 = 0;
		this.nrtp=0;
		this.nrtpfp=0;
		this.nrtpfn=0;
		this.nrrecall=0;
		this.nrprecision=0;
		this.nrf1=0;
	}

	/**
	 * 精确率=模型正确数/模型测出来的总数
	 * @return
	 */
	public float getPrecision() {
		this.precision = tp / tpfp;
		return this.precision;
	}

	/**
	 *  召回率=模型正确数/实际正确数
	 * @return
	 */
	public float getRecall() {
		this.recall = this.tp / this.tpfn;
		return this.recall;
	}

	public float getF1() {
		this.f1 = (2 * this.precision * this.recall) / (this.precision+ this.recall);
		return this.f1;
	}

	/**
	 * @param map map 用规则、词典、最大熵完成后得到的结果，key为词性，value为词性对应词汇列表
	 * @Describe 计算tpfp。精确率分母
	 * @throws IOException
	 */
	public void computeTPFP(Map<String, List<String>> map,boolean setRepeat) throws IOException {
	
//		String str1 = "对应 202.113.112.30 CVE-2018-7600 Microsoft Windows 360威胁情报中心 WannaCry《针对答复哈回复沙发大厦的分析报告》";
		// 通过模型后 输出的总实体值计数，tpfp
		if(setRepeat){
			for (Map.Entry entry : map.entrySet()) {
				HashSet<String> set =new HashSet<String>();
				List<String> ioc = (List) entry.getValue();
				for(int i=0;i<ioc.size();i++){
					if(set.add(ioc.get(i))){
						tpfp++;
					}
				}
			}	
		}
		else{
			for (Map.Entry entry : map.entrySet()) {
				List<String> ioc = (List) entry.getValue();
				tpfp += ioc.size();
			}
		}
		
		System.out.println(tpfp+"个模型识别出来的实体");
	}

	/**
	 * @param path
	 * @return 返回一个文件夹中的所有txt文本链接起来的String数据
	 * @throws IOException
	 */
	public static String getAllFileData(String path) throws IOException {
		String data = "";
		File file = new File(path);
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile() && f.exists()) {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					data = data + lineTxt;
					System.out.println(lineTxt);
				}
				br.close();
			} else
				System.out.println("文件不存在!");
			data = data + "\n\r";
		}
		return data;
	}

	/**
	 * --old--
	 * 
	 * @param map1
	 * @param path
	 * @return 处理人工标注的数据，并对比计算出正确数tp_iocs，计算出文本正确数tpfp_iocs
	 * @throws IOException
	 */
	public void computeTPFN(Map<String, List<String>> map, String path) throws IOException {
		// 是否对标注好的文本去重：setRepeat 默认去重
		boolean setRepeat = true;
		Set<String> set = new HashSet<String>();
		File file = new File(path);
		File[] files = file.listFiles();
		for (File f : files) {
			if (f.isFile() && f.exists()) {
				InputStreamReader isr = new InputStreamReader(new FileInputStream(f), "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					String[] temp = lineTxt.split("	");
					if (setRepeat) {// 如果去重
						if (set.add(temp[0])) {// 如果不重复
							System.out.println(temp[0] + "   " + temp[1]);
							if (map.get(temp[1]) != null) {
								List<String> ls = map.get(temp[1]);
								for (String str : ls) {
									if (str.equals(temp[0])) {
										this.tp++;
									}
								}
							}
							this.tpfn++;
						}
					} else {
						if (map.get(temp[1]) != null) {
							List<String> ls = map.get(temp[1]);
							for (String str : ls) {
								if (str.equals(temp[0])) {
									this.tp++;
								}
							}
						}
						this.tpfn++;
					}
				}
				System.out.println(tp + "是正确匹配的值");
				br.close();
			} else
				System.out.println("文件不存在!");
		}
	}

	/**
	 * --newest--
	 * @Describe 计算tpfn 召回率分母
	 * @param map 用规则、词典、最大熵完成后得到的结果，key为词性，value为词性对应词汇列表
	 * @param path bart标注的ann文件位置
	 * @param setRepeat 是否查重
	 * @return 处理brat的ann数据，计算出匹配正确数tp，计算出实际正确数tpfp
	 * @throws IOException
	 */
	public void computeTPFN(Map<String, List<String>> map, String path, boolean setRepeat) throws IOException {
		// 是否对标注好的文本去重：setRepeat 默认去重
		Map<String, List<String>> tempmap=new HashMap<String, List<String>>();
		tempmap.putAll(map);//这个temp并没有起到作用，也就是说修改了temp的值，map还是跟着被修改了。所以还是先需要计算精确率再计算召回率。因为在这里计算召回率的时候会修改map值，如果修改后再计算精确率就会产生错误。
		Set<String> set = new HashSet<String>();
		List<String> rightlist = new ArrayList<String>();
		File[] files = new File(path).listFiles();
		System.out.println(path);
		
		
		//预处理
		List<String> nt =new ArrayList<String>();
		nt.addAll(map.get("nt"));
		nt.addAll(map.get("nvendor"));
		List<String> ip = new ArrayList<String>();
		ip.addAll(map.get("IP"));
		ip.addAll(map.get("IPPORT"));
		List<String> vul = new ArrayList<String>();
		vul.addAll(map.get("CAN"));
		vul.addAll(map.get("CNVD"));
		vul.addAll(map.get("CNNVD"));
		vul.addAll(map.get("CNCVE"));
		
		this.nrtpfp=map.get("nr").size();
		this.nttpfp=nt.size();
		this.IPtpfp=map.get("IP").size();
		
		for (File f : files) {
			if (f.isFile() && f.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					if(lineTxt== null) break;
					String[] word = lineTxt.split("	");//word[2]是词面
					String[] pos = word[1].split(" ");//pos[0]是词性
//					System.out.println("f.toString():"+f.toString()+"-word[2]"+word[2]+"-pos+"+pos[0]);
					if (setRepeat) {// 如果去重
						if (set.add(word[2])) {
							if (pos[0].equals("Organization")) {
								this.nttpfn++;
								for (String str : nt) 
									if (str.equals(word[2])) {
										this.tp++;
										this.nttp++;
									}	
							}else if (pos[0].equals("CVE")||pos[0].equals("AS")||pos[0].equals("Cert")||pos[0].equals("Host")||pos[0].equals("Domain")||pos[0].equals("Email")||pos[0].equals("MD5")||pos[0].equals("Registry")||pos[0].equals("SHA1")||pos[0].equals("SHA256")||pos[0].equals("URL")) {
								System.out.println("f.toString():"+f.toString()+"	word:"+word[0]);
								for (String str : map.get(pos[0])) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if (pos[0].equals("IP")) {
								this.IPtpfn++;
								for (String str : ip) 
									if (str.equals(word[2])) {
										this.tp++;
										this.IPtp++;
									}
							}else if(pos[0].equals("File_Name")){
								for (String str : map.get("Filename")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("File_Path")){
								for (String str : map.get("Filepath")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Operating_System")||pos[0].equals("Software")||pos[0].equals("Tool")||pos[0].equals("Program")){
								for (String str : map.get("nproduct")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Person")){
								this.nrtpfn++;
								for (String str : map.get("nr")) 
									if (str.equals(word[2])) {
										this.nrtp++;
										this.tp++;
									}
							}else if(pos[0].equals("Place")){
								for (String str : map.get("ns")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Report")){
								for (String str : map.get("nreport")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Malware")||pos[0].equals("Virus")||pos[0].equals("Vulnerability")){
								map.get("nthreat").addAll(vul);
								for (String str : map.get("nthreat")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Event")){
								for (String str : map.get("nevent")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Hacker_Group")){
								for (String str : map.get("nhack")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Attack")){
								for (String str : map.get("nvulndesc")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Hardware")){
								for (String str : map.get("nhardware")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Conference")){
								for (String str : map.get("nconference")) 
									if (str.equals(word[2])) 
										this.tp++;
							}else if(pos[0].equals("Protocol")){
								for (String str : map.get("nprotocol")) 
									if (str.equals(word[2])) 
										this.tp++;
							}
							this.tpfn++;
						}
					} else {//不去重
						if (pos[0].equals("Organization")) {
							this.nttpfn++;
							for(int j=0;j<nt.size();j++){
								if (nt.get(j).equals(word[2])) {
									this.nttp++;
									this.tp++;
									rightlist.add(word[2]);
									nt.remove(j);
									break;
								}
							}
						}else if (pos[0].equals("Place")) {
							for(int j=0;j<tempmap.get("ns").size();j++){
								if (tempmap.get("ns").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("ns").remove(j);
									break;
								}
							}
						}else if (pos[0].equals("CVE")||pos[0].equals("AS")||pos[0].equals("Cert")||pos[0].equals("Host")||pos[0].equals("Domain")||pos[0].equals("Email")||pos[0].equals("MD5")||pos[0].equals("Registry")||pos[0].equals("SHA1")||pos[0].equals("SHA256")||pos[0].equals("URL")) {
							for(int j=0;j<tempmap.get(pos[0]).size();j++){
									if (tempmap.get(pos[0]).get(j).equals(word[2])) {
										this.tp++;
										rightlist.add(word[2]);
										tempmap.get(pos[0]).remove(j);
										break;
									}
							}
						}else if (pos[0].equals("IP")) {	
							this.IPtpfn++;
							System.out.println(word[2]+""+word[0]);
							for(int j=0;j<ip.size();j++){
								if (ip.get(j).equals(word[2])) {
									this.tp++;
									this.IPtp++;
									rightlist.add(word[2]);
									ip.remove(j);
									break;
								}
							}
						}else if(pos[0].equals("File_Name")){
							for(int j=0;j<tempmap.get("Filename").size();j++){
								if (tempmap.get("Filename").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("Filename").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("File_Path")){
							for(int j=0;j<tempmap.get("Filepath").size();j++){
								if (tempmap.get("Filepath").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("Filepath").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Virus")||pos[0].equals("Vulnerability")){
							tempmap.get("nthreat").addAll(vul);
							for(int j=0;j<tempmap.get("nthreat").size();j++){
								if (tempmap.get("nthreat").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nthreat").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Operating_System")||pos[0].equals("Software")||pos[0].equals("Tool")||pos[0].equals("Program")){
							for(int j=0;j<tempmap.get("nproduct").size();j++){
								if (tempmap.get("nproduct").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nproduct").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Person")){
							this.nrtpfn++;
							for(int j=0;j<tempmap.get("nr").size();j++){
								if (tempmap.get("nr").get(j).equals(word[2])) {
									this.nrtp++;
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nr").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Report")){
							for(int j=0;j<tempmap.get("nreport").size();j++){
								if (tempmap.get("nreport").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nreport").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Malware")||pos[0].equals("Virus")||pos[0].equals("Vulnerability")){
							for(int j=0;j<tempmap.get("nthreat").size();j++){
								if (tempmap.get("nthreat").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nthreat").remove(j);
									break;
								}
							}
							
						}else if(pos[0].equals("Event")){
							for(int j=0;j<tempmap.get("nevent").size();j++){
								if (tempmap.get("nevent").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nevent").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Hacker_Group")){
							for(int j=0;j<tempmap.get("nhack").size();j++){
								if (tempmap.get("nhack").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nhack").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Attack")){
							for(int j=0;j<tempmap.get("nvulndesc").size();j++){
								if (tempmap.get("nvulndesc").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nvulndesc").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Hardware")){
							for(int j=0;j<tempmap.get("nhardware").size();j++){
								if (tempmap.get("nhardware").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nhardware").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Protocol")){
							for(int j=0;j<tempmap.get("nprotocol").size();j++){
								if (tempmap.get("nprotocol").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nprotocol").remove(j);
									break;
								}
							}
						}else if(pos[0].equals("Conference")){
							for(int j=0;j<tempmap.get("nconference").size();j++){
								if (tempmap.get("nconference").get(j).equals(word[2])) {
									this.tp++;
									rightlist.add(word[2]);
									tempmap.get("nconference").remove(j);
									break;
								}
							}
						}
						this.tpfn++;
					}	
				}
//				for(String str:rightlist){
//					System.out.println("正确的值有："+str);
//				}
				System.out.println(this.tp + "个模型正确匹配的值");
				System.out.println(this.tpfn+"个brat标注的正确值");
				br.close();
				System.out.println(f.getName());
			}else
				System.out.println("文件不存在!");
		}
	}
	
	public void nrCompute(Map<String, List<String>> map){
		System.out.println("nrtp:"+this.nrtp);
		System.out.println("nrtpfn:"+this.nrtpfn);
		System.out.println("nrtpfp:"+this.nrtpfp);
		if(this.nrtp==this.nrtpfn){
			this.nrrecall=1;
		}else{ this.nrrecall=this.nrtp/this.nrtpfn;}
		
		if(this.nrtp==this.nrtpfp){
			this.nrprecision=1;
		}else{ this.nrprecision=this.nrtp/this.nrtpfp;}
		
		if(this.nrprecision * this.nrrecall==this.nrprecision+ this.nrrecall){
			this.nrf1=1;
		}else{ this.nrf1 = (2 * this.nrprecision * this.nrrecall) / (this.nrprecision+ this.nrrecall);}
		System.out.println("nr精确率:"+this.nrprecision);
		System.out.println("nr召回率:"+this.nrrecall);
		System.out.println("nrf1:"+this.nrf1);
		System.out.println("**********");
		System.out.println("nttp:"+this.nttp);
		System.out.println("nttpfn:"+this.nttpfn);
		System.out.println("nttpfp:"+this.nttpfp);
		if(this.nttp==this.nttpfn){
			this.ntrecall=1;
		}else{ this.ntrecall=this.nttp/this.nttpfn;}
		
		if(this.nttp==this.nttpfp){
			this.ntprecision=1;
		}else{ this.ntprecision=this.nttp/this.nttpfp;}
		
		if(this.ntprecision * this.ntrecall==this.ntprecision+ this.ntrecall){
			this.ntf1=1;
		}else{ this.ntf1 = (2 * this.ntprecision * this.ntrecall) / (this.ntprecision+ this.ntrecall);}
		System.out.println("nt精确率:"+this.ntprecision);
		System.out.println("nt召回率:"+this.ntrecall);
		System.out.println("ntf1:"+this.ntf1);
		System.out.println("**********");
		System.out.println("IPtp:"+this.IPtp);
		System.out.println("IPtpfn:"+this.IPtpfn);
		System.out.println("IPtpfp:"+this.IPtpfp);
		if(this.IPtp==this.IPtpfn){
			this.IPrecall=1;
		}else{ this.IPrecall=this.IPtp/this.IPtpfn;}
		
		if(this.IPtp==this.IPtpfp){
			this.IPprecision=1;
		}else{ this.IPprecision=this.IPtp/this.IPtpfp;}
		
		if(this.IPprecision * this.IPrecall==this.IPprecision+ this.IPrecall){
			this.IPf1=1;
		}else{ this.IPf1 = (2 * this.IPprecision * this.IPrecall) / (this.IPprecision+ this.IPrecall);}
		System.out.println("IP精确率:"+this.IPprecision);
		System.out.println("IP召回率:"+this.IPrecall);
		System.out.println("IPf1:"+this.IPf1);
		System.out.println("**********");
	}

	/**
	 * 不分实体类别情况下的精度计算（包括tp、tpfp、tpfn的计算）
	 * @throws IOException 
	 */
	public void notClassified(Map<String, List<String>> map, String path, boolean setRepeat) throws IOException{
		//初始化
		Set<String> set = new HashSet<String>();
		File[] files = new File(path).listFiles();
		List<String> modelEntity =new ArrayList<String>();
		//实体放入modelEntity，同时计数tpfp
		if(setRepeat){
			for (Map.Entry entry : map.entrySet()) {
				List<String> ioc = (List) entry.getValue();
				for(int i=0;i<ioc.size();i++){
					if(set.add(ioc.get(i))){
						modelEntity.add(ioc.get(i));
						tpfp++;
					}
				}
			}	
		}else{
			for (Map.Entry entry : map.entrySet()) {
				List<String> ioc = (List) entry.getValue();
				modelEntity.addAll(ioc);
				tpfp += ioc.size();
			}
		}
		System.out.println("查重为"+setRepeat+"的情况下，"+tpfp+"个模型识别出来的实体");
		System.out.println("如下：");
		for (String str :modelEntity) {
			System.out.println(str);
		}
		//计算tpfn以及tp
		set.clear();
		for (File f : files) {
			if (f.isFile() && f.exists()) {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "utf-8"));
				String lineTxt = null;
				while ((lineTxt = br.readLine()) != null) {
					String[] word = lineTxt.split("	");//word[2]是词面
					if (setRepeat) {// 如果去重
						if (set.add(word[2])) {
							for (String str :modelEntity) 
									if (str.equals(word[2])) 
										this.tp++;
							this.tpfn++;
						}
					}else{
						for (String str :modelEntity) 
							if (str.equals(word[2])) {
								this.tp++;
								break;
							}
						this.tpfn++;
					}
				}
				System.out.println(this.tp + "个模型正确匹配的值");
				System.out.println(this.tpfn+"个brat标注的正确值");
				br.close();
			}else{
				System.out.println("文件不存在!");
			}
		}
	}
	
	/**
	 * 
	 *  分实体类别情况下的精度计算（包括tp、tpfp、tpfn的计算）
	 * @throws IOException
	 */
	public void classified(Map<String, List<String>> map, String path, boolean setRepeat) throws IOException{
		this.computeTPFP(map,setRepeat);
		this.computeTPFN(map,path,setRepeat);
		
	}
	
	public static void main(String[] arg) throws IOException {
		/*测试getAllFileData函数*/
		String str = getAllFileData("G:\\extraEclipseWork\\AnnSecurityWeekly\\TxtIntegrated");
//		String str="自从上个月21号美国DNS服务提供商Dyn遭遇大规模DDoS攻击以来，关于幕后黑手的身份引发各界猜测。Flashpoint发布的最新报告认为，这次超大型的DDoS攻击很有可能就是一名脚本小子将索尼游戏网站(PSN)作为目标，利用Mirai恶意软件感染大量IoT僵尸网络而发动的，Dyn由于向PSN提供域名解析服务而惨遭中枪。与之前所怀疑的、黑客活动组织、恐怖分子或国家黑客攻击无关。但是来自ThousandEyes的网络故障分析师Nick Kephart对此事持有不同的意见，他表示发起如此规模的DDoS攻击只是为了攻击Dyn的一个客户的说法是站不住脚的。与此同时，Akamai公司的Martin McKeay更是直言，Flashpoint的分析就是为了掩盖真正的攻击目标。";
//		String str1="网络安全公司RecordedFuture的威胁研究小组Insikt Group在上周四发表的一篇博文中给我们带来了关于Mirai僵尸网络的最新动态。在今年1月下旬，Mirai僵尸网络的一个变种针对金融业企业发起了一系列DDoS攻击。Insikt Group表示，攻击事件中的僵尸网络可能与Mirai僵尸网络的变种IoTroop存在密切联系。到目前为止，至少已经有三家欧洲金融机构成为了攻击事件的受害者。在2017年10月29日，以色列网络安全公司Check Point在其发布的技术报告中表示，他们的安全团队发现了一个代号为IoTroop的新型僵尸网络。据Insikt Group称，在这个僵尸网络中有80%是受感染的MikroTik路由器，其余20%由其他多种物联网设备组成，包括易受攻击的Apache和IIS网络服务器以及由Ubiquity、Cisco和ZyXEL生产的路由器。";
//		String str2="上周，clearbit.com的创始人Alex MacCaw在Twitter公布了他刚刚收到的一条短信的截图。一名未知的黑客向MacCaw发送了一条自称来自谷歌的短信，内容如下：“(Google通知)我们最近发现了一次可疑的登录尝试，有人试图在IP地址136.91.38.203登录您的帐户jschnei4@gmail.com。如果您没有在这个位置尝试登录，或者想暂时锁定您的帐户，请回复您收到的6位数验证码。如果您已授权此次登录，请忽略这个警告。”事实上，受害者将会收到的是2FA验证代码，因为攻击者正在尝试非法登录他们的帐户。, 2FA即双因素认证，许多在线服务都支持这种双层认证，例如Facebook、谷歌、银行和政府机构。2FA的工作原理是，当用户登录一个受双因素认证保护的帐户时，手机会收到一条短信，随后用户将会被要求输入短信中的验证码。如果用户不能及时输入代码，此次登录将被视为非法登录，即使他们输入了正确的密码也无法访问帐户。这些骗子试图访问MacCaw的帐户,当2FA系统发挥作用时， MacCaw可能会采取行动,通过发送所谓的“谷歌验证码”来锁定他的账户，事实上，这些2FA验证码将会被发送给骗子，而骗子会拿着这些验证码进入并访问他的帐户。";

		PreEntityExtract ee = new PreEntityExtract();
		List<Term> list = ee.getShortSegment2(str);
		ee.repeatCheck=false;//是否查重
		ee.allPretreatment(list);
		Map<String,List<String>> predictMap=ee.getPredictMap3(list);
//		Map<String,List<String>> predictMap=ee.getPredictMap4(list);

		//在这里输出一波最后算出的实体：
		for (Map.Entry entry : predictMap.entrySet()) {
			List<String> ioc = (List) entry.getValue();
			System.out.println("*********"+entry.getKey()+"如下*********");
			for(String string:ioc){
				System.out.println(string);
			}
		}
		String path="G:\\extraEclipseWork\\AnnSecurityWeekly\\AnnIntegrated";//brat的ann文件位置
		System.out.println("*********");
		Evaluation eval = new Evaluation();
		eval.classified(predictMap, path, ee.repeatCheck);//计算过程中，map值有所损失
//		eval.notClassified(predictMap, path, ee.repeatCheck);
		System.out.println("*********");
		System.out.println("精确率:" + eval.getPrecision());
		System.out.println("召回率:" + eval.getRecall());
		System.out.println("F1:" + eval.getF1());
		System.out.println("*********");
//		eval.nrCompute(predictMap);
		
//		System.out.println("***end***");
	}
}
