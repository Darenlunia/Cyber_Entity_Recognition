package org.cyber.dictTag;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cyber.util.GetTextFromPdf;
import org.cyber.util.GetTextFromTxt;
import org.cyber.util.Map2Excel;
import org.cyber.crfTag.CrfTag;
import org.cyber.evaluation.TestCrfTest;
import org.cyber.memmTag.Predict;
import org.cyber.regexpTag.PatternsMatch;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.corpus.util.CustomNatureUtility;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 分词预处理类-包括Hanlp分词、分词数据预处理
 * 
 * @author Administrator
 *
 */
public class PreEntityExtract {

	
	static{
		CustomNatureUtility.addNature("nvulndesc");
		CustomNatureUtility.addNature("nprotocol");
		CustomNatureUtility.addNature("prehack");
		CustomNatureUtility.addNature("nproduct");
		CustomNatureUtility.addNature("nhardware");
		CustomNatureUtility.addNature("nvuldescribe");
//		CustomNatureUtility.addNature("nthreat");
//		CustomNatureUtility.addNature("nreport");
		CustomNatureUtility.registerSwitchClass(PreEntityExtract.class);
	}
	/**
	 * 放的是分词匹配或词典匹配后的map
	 */
	private Map<String,List<String>> iocs =new HashMap<String,List<String>>();
	
	/**
	 * 是否查重过滤再输出，默认 查重
	 */
	public boolean repeatCheck =true;
	
	/**
	 * 对输入的文件进行viterbi分词
	 * 
	 * @param 要分词的文档
	 * @return 分词后Term的列表
	 */
	public List<Term> getShortSegment(String fpath) {

		GetTextFromPdf gtfp = new GetTextFromPdf(fpath);
		String data = gtfp.getResult();
		Segment shortestSegment = new ViterbiSegment().enablePlaceRecognize(true).enableOrganizationRecognize(true)
				.enableNameRecognize(false);
		List<Term> list = shortestSegment.seg(data);
		// 数据预处理 英文空格相连，去掉前后空格
		for (int i = 0; i < list.size() - 1; i++) {
			// 处理英文前空格
			if(i>0){
				if(list.get(i).word.equals(" ")&& list.get(i - 1).nature.equals(Nature.nx)){
					i--;
				}
			}
			// 处理英文前空格
			if (list.get(i).word.equals(" ") && list.get(i + 1).nature.equals(Nature.nx)) {
				list.remove(i);
			}
			// 英文与英文空格或.连接
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx)
					&& (list.get(i + 1).word.equals(" ") || list.get(i + 1).word.equals("."))
					&& list.get(i + 2).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word + list.get(i + 2).word, Nature.nx));
				list.remove(i + 1);
				list.remove(i + 1);
			}
			// 数字与英文直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.m)
					&& list.get(i + 1).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
				// i--;
			}
			// 英文与数字直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.nx)
					&& list.get(i + 1).nature.equals(Nature.m)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
				// i--;
			}
			// 英文与英文直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.nx)
					&& list.get(i + 1).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
				// i--;
			}
			// 英文数字-连接
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx) && list.get(i + 1).word.equals("-")
					&& list.get(i + 2).nature.equals(Nature.m)) {
				list.set(i, new Term(list.get(i).word + "-" + list.get(i + 2).word, Nature.nx));
				list.remove(i + 1);
				list.remove(i + 1);
				// i--;
			}
			// 去掉最后的空格
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx) && list.get(i + 1).word.equals(" ")
					&& !list.get(i + 2).nature.equals(Nature.nx)) {
				list.remove(i + 1);
				i++;
			}
			//数字与年连接
			if(i< list.size() - 1&&list.get(i).nature.equals(Nature.m)&&list.get(i + 1).word.equals("年")){
				list.set(i, new Term(list.get(i).word + "年", Nature.create("Date")));
				list.remove(i + 1);
			}
		}
		// CoreStopWordDictionary.apply(list);//过滤分词结果
		return list;
	}

	/**
	 * 对输入的句子进行viterbi分词，以及预处理
	 * 
	 * @param data
	 *            要分词的字符串
	 * 
	 */
	public List<Term> getShortSegment2(String data) {
		Segment shortestSegment = new ViterbiSegment().enablePlaceRecognize(true).enableOrganizationRecognize(true)
				.enableNameRecognize(true);
		List<Term> list = shortestSegment.seg(data);
		for (int i = 0; i < list.size() - 1; i++) {
			if(i>0){
				if(list.get(i).word.equals(" ")&& list.get(i - 1).nature.equals(Nature.nx)){
					i--;
				}
			}
			// 处理英文前空格
			if (list.get(i).word.equals(" ") && list.get(i + 1).nature.equals(Nature.nx)) {
				list.remove(i);
			}
			// 英文与:连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.nx)
					&& (list.get(i + 1).word.equals(":"))
					) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
			}
			// 英文与英文/连接
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx)
					&& (list.get(i + 1).word.equals(" ") || list.get(i + 1).word.equals("\\")||list.get(i + 1).word.equals("/"))
					&& list.get(i + 2).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word + list.get(i + 2).word, Nature.nx));
				list.remove(i + 1);
				list.remove(i + 1);
				i--;
			}
			// 英文与英文空格或.连接
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx)
					&& (list.get(i + 1).word.equals("."))
					&& list.get(i + 2).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word + list.get(i + 2).word, Nature.nx));
				list.remove(i + 1);
				list.remove(i + 1);
				i--;
			}
			// 数字与英文直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.m)
					&& list.get(i + 1).nature.equals(Nature.nx)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
//				 i--;
			}
			// 英文与数字直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.nx)
					&& list.get(i + 1).nature.equals(Nature.m)) {
				list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
				list.remove(i + 1);
				 i--;
			}
			// 英文与英文直接连接
			if (i < list.size() - 1 && list.get(i).nature.equals(Nature.nx)
								&& list.get(i + 1).nature.equals(Nature.nx)) {
							list.set(i, new Term(list.get(i).word + list.get(i + 1).word, Nature.nx));
							list.remove(i + 1);
							 i--;
			}
			// 英文数字-连接
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx) && list.get(i + 1).word.equals("-")
					&& list.get(i + 2).nature.equals(Nature.m)) {
				list.set(i, new Term(list.get(i).word + "-" + list.get(i + 2).word, Nature.nx));
				list.remove(i + 1);
				list.remove(i + 1);
				// i--;
			}
			// 去掉最后的空格
			if (i < list.size() - 2 && list.get(i).nature.equals(Nature.nx) && list.get(i + 1).word.equals(" ")
					&& !list.get(i + 2).nature.equals(Nature.nx)) {
				list.remove(i + 1);
				i++;
			}
			//数字与年连接
			if(i< list.size() - 1&&list.get(i).nature.equals(Nature.m)&&list.get(i + 1).word.equals("年")){
				list.set(i, new Term(list.get(i).word + "年", Nature.create("Date")));
				list.remove(i + 1);
			}
		}
		// CoreStopWordDictionary.apply(list);//过滤分词结果
		return list;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public void regexp(List<Term> list) throws IOException {
		for (int i = 0; i < list.size() - 1; i++) {
			String pos = "";
//			Term tt= new PatternsMatch().patternsMatchOneWord(list.get(i).word);
			pos= new PatternsMatch().patternsMatchOneWord(list.get(i).word);
			if (pos!= null) {//			if (tt!= null) {
//				pos=tt.nature.toString();
//				list.get(i).word=tt.word;
				list.get(i).nature = Nature.create(pos);
				// 下面为email处理
				if (i > 0 && pos.equals("Domain") && list.get(i - 1).nature.equals(Nature.nx)) {
					list.set(i, new Term(list.get(i - 1).word + list.get(i).word, Nature.create("Email")));
					list.remove(i - 1);
					i--;
				}
				// 下面为URL处理
				if (i > 3 && pos.equals("Domain") && list.get(i - 3).word.equals("http")
						&& list.get(i - 2).word.equals(":") && list.get(i - 1).word.equals("//")) {
					list.set(i, new Term(
							list.get(i - 3).word + list.get(i - 2).word + list.get(i - 1).word + list.get(i).word,
							Nature.create("URL")));
					list.remove(i - 3);
					list.remove(i - 3);
					list.remove(i - 3);
					i = i - 3;
					if(i < list.size() - 2 && (list.get(i + 1).word.equals("/") ||list.get(i + 1).word.equals(".") )
							&& list.get(i + 2).nature.equals(Nature.nx)){
						list.set(i, new Term(list.get(i).word + list.get(i + 1).word + list.get(i + 2).word, Nature.create("URL")));
						list.remove(i + 1);
						list.remove(i + 1);
						
					}
				}
			}
		}
	}
	
	/**
	 *对分词结果进行stucco词典与regex规则匹配 
	 * @param list
	 * @throws IOException
	 */
	public void allPretreatment(List<Term> list) throws IOException{
		this.regexp(list);//正则词性标注
		new StucExtract().annotate(list);//stcco词典词性标注
	}

	//***************************以下用map存储分词结果*****************************//
	/**
	 * 输出中国地名大类ns
	 * @param list
	 * @return
	 * 
	 */
	public String getNsEntity(List<Term> list){
		System.out.println("正在收集地名ns大类... ...");
		List<String> ioc =new ArrayList<String>();
		HashSet<String> set = new  HashSet<String>(); 
		for(Term tm : list) {
			if(tm.nature.startsWith("ns")/*toString().equals("ns")*/)
				if(repeatCheck){
					if(set.add(tm.word)){	
						System.out.println(tm.word);
						ioc.add(tm.word);
					}
				}
				else{
					System.out.println(tm.word);
					ioc.add(tm.word);
				}
			}
		iocs.put("ns", ioc);
		return null;
	}
	
	/**
	 * 输出词性匹配“nt-机构大类”的结果
	 * @param list
	 * @return
	 */
	public String getNtEntity(List<Term> list){		
		System.out.println("正在收集机构nt大类... ...");
		List<String> ioc =new ArrayList<String>();
		HashSet<String> set = new  HashSet<String>(); 
		for(Term tm : list) {
			if(tm.nature.startsWith("nt"))	{
				tm.word=tm.word.trim();
				if(repeatCheck){
					if(!tm.word.isEmpty()&&set.add(tm.word)){
						System.out.println(tm.word);
						ioc.add(tm.word);
					}
				}
				else {
					System.out.println(tm.word);
					ioc.add(tm.word);
				}
			}
		}
		iocs.put("nt", ioc);
		return null;
	}
	
	/**
	 * 输出词性匹配人名nr（普通人名，蒙古名 复姓等）的结果
	 * @param list
	 * @return
	 * 
	 */
	public String getNrEntity(List<Term> list){
		System.out.println("正在收集人名nr大类... ...");
		List<String> ioc =new ArrayList<String>();
		HashSet<String> set = new  HashSet<String>(); 
		for(Term tm : list) {
			if(tm.nature.startsWith("nr")/*.toString().equals("nr")*/)
				if(repeatCheck){
					if(set.add(tm.word))	{
						System.out.println(tm.word);
						ioc.add(tm.word);
					}
				}
				else{
					System.out.println(tm.word);
					ioc.add(tm.word);
				}
			}
		iocs.put("nr", ioc);
		return null;
	}
	
	
	/**
	 * —— old ——
	 * 装入过程——将词典与规则识别的结果放入iocs
	 * @param list
	 * @return
	 * 
	 */
	public Map<String,List<String>> getEntity(List<Term> list){
		List<String> nr =new ArrayList<String>();
		List<String> ns =new ArrayList<String>();
		List<String> nt =new ArrayList<String>();
		List<String> Cert =new ArrayList<String>();
		List<String> AS =new ArrayList<String>();
		List<String> Email =new ArrayList<String>();
		List<String> URL =new ArrayList<String>();
		List<String> Domain =new ArrayList<String>();
		List<String> IPPORT =new ArrayList<String>();
		List<String> IP =new ArrayList<String>();
		List<String> MD5 =new ArrayList<String>();
		List<String> SHA1 =new ArrayList<String>();
		List<String> SHA256 =new ArrayList<String>();
		List<String> CVE =new ArrayList<String>();
		List<String> CAN =new ArrayList<String>();
		List<String> CNVD =new ArrayList<String>();
		List<String> CNNVD =new ArrayList<String>();
		List<String> CNCVE =new ArrayList<String>();
		List<String> Registry =new ArrayList<String>();
		List<String> Filename =new ArrayList<String>();
		List<String> Filepath =new ArrayList<String>();
		List<String> nvendor =new ArrayList<String>();
		List<String> nproduct =new ArrayList<String>();
		List<String> nvulndesc =new ArrayList<String>();
		List<String> nprotocol =new ArrayList<String>();
		List<String> nhardware =new ArrayList<String>();
		iocs.put("nr", nr);
		iocs.put("ns", ns);
		iocs.put("nt", nt);
		iocs.put("Cert", Cert);
		iocs.put("AS", AS);
		iocs.put("Email", Email);
		iocs.put("URL", URL);
		iocs.put("Domain", Domain);
		iocs.put("IPPORT", IPPORT);
		iocs.put("IP", IP);
		iocs.put("MD5", MD5);
		iocs.put("SHA1", SHA1);
		iocs.put("SHA256", SHA256);
		iocs.put("CVE", CVE);
		iocs.put("CAN", CAN);
		iocs.put("CNVD", CNVD);
		iocs.put("CNNVD", CNNVD);
		iocs.put("CNCVE", CNCVE);
		iocs.put("Registry", Registry);
		iocs.put("Filename", Filename);
		iocs.put("Filepath", Filepath );
		iocs.put("nvendor", nvendor);
		iocs.put("nproduct", nproduct);
		iocs.put("nvulndesc", nvulndesc);
		iocs.put("nprotocol", nprotocol);
		iocs.put("nhardware", nhardware);
		
		HashSet<String> set = new  HashSet<String>(); 
		if(repeatCheck){
			for(Term tm : list) {
				if(set.add(tm.word+tm.nature.toString())){
					if(tm.nature.startsWith("nr")){
						nr.add(tm.word);
					}else if(tm.nature.startsWith("ns")){
						ns.add(tm.word);
					}else if(tm.nature.startsWith("nt")){
						nt.add(tm.word);
					}else if(tm.nature.toString().equals("Cert")){
						Cert.add(tm.word);
					}else if(tm.nature.toString().equals("AS")){
						AS.add(tm.word);
					}else if(tm.nature.toString().equals("Email")){
						Email.add(tm.word);
					}else if(tm.nature.toString().equals("URL")){
						URL.add(tm.word);
					}else if(tm.nature.toString().equals("Domain")){
						Domain.add(tm.word);
					}else if(tm.nature.toString().equals("IPPROT")){
						IPPORT.add(tm.word);
					}else if(tm.nature.toString().equals("IP")){
						IP.add(tm.word);
					}else if(tm.nature.toString().equals("MD5")){
						MD5.add(tm.word);
					}else if(tm.nature.toString().equals("SHA1")){
						SHA1.add(tm.word);
					}else if(tm.nature.toString().equals("SHA256")){
						SHA256.add(tm.word);
					}else if(tm.nature.toString().equals("CVE")){
						CVE.add(tm.word);
					}else if(tm.nature.toString().equals("CAN")){
						CAN.add(tm.word);
					}else if(tm.nature.toString().equals("CNVD")){
						CNVD.add(tm.word);
					}else if(tm.nature.toString().equals("CNNVD")){
						CNNVD.add(tm.word);
					}else if(tm.nature.toString().equals("CNCVE")){
						CNCVE.add(tm.word);
					}else if(tm.nature.toString().equals("Registry")){
						Registry.add(tm.word);
					}else if(tm.nature.toString().equals("Filename")){
						Filename.add(tm.word);
					}else if(tm.nature.toString().equals("Filepath")){
						Filepath.add(tm.word);
					}else if(tm.nature.toString().equals("nvendor")){
						nvendor.add(tm.word);
					}else if(tm.nature.toString().equals("nproduct")){
						nproduct.add(tm.word);
					}else if(tm.nature.toString().equals("nvulndesc")){
						nvulndesc.add(tm.word);
					}else if(tm.nature.toString().equals("nprotocol")){
						nprotocol.add(tm.word);
					}else if(tm.nature.toString().equals("nhardware")){
						nhardware.add(tm.word);
					}
				}
			}
		}else{
			for(Term tm : list) {
				if(tm.nature.startsWith("nr")){
					nr.add(tm.word);
				}else if(tm.nature.startsWith("ns")){
					ns.add(tm.word);
				}else if(tm.nature.startsWith("nt")){
					nt.add(tm.word);
				}else if(tm.nature.toString().equals("Cert")){
					Cert.add(tm.word);
				}else if(tm.nature.toString().equals("AS")){
					AS.add(tm.word);
				}else if(tm.nature.toString().equals("Email")){
					Email.add(tm.word);
				}else if(tm.nature.toString().equals("URL")){
					URL.add(tm.word);
				}else if(tm.nature.toString().equals("Domain")){
					Domain.add(tm.word);
				}else if(tm.nature.toString().equals("IPPROT")){
					IPPORT.add(tm.word);
				}else if(tm.nature.toString().equals("IP")){
					IP.add(tm.word);
				}else if(tm.nature.toString().equals("MD5")){
					MD5.add(tm.word);
				}else if(tm.nature.toString().equals("SHA1")){
					SHA1.add(tm.word);
				}else if(tm.nature.toString().equals("SHA256")){
					SHA256.add(tm.word);
				}else if(tm.nature.toString().equals("CVE")){
					CVE.add(tm.word);
				}else if(tm.nature.toString().equals("CAN")){
					CAN.add(tm.word);
				}else if(tm.nature.toString().equals("CNVD")){
					CNVD.add(tm.word);
				}else if(tm.nature.toString().equals("CNNVD")){
					CNNVD.add(tm.word);
				}else if(tm.nature.toString().equals("CNCVE")){
					CNCVE.add(tm.word);
				}else if(tm.nature.toString().equals("Registry")){
					Registry.add(tm.word);
				}else if(tm.nature.toString().equals("Filename")){
					Filename.add(tm.word);
				}else if(tm.nature.toString().equals("Filepath")){
					Filepath.add(tm.word);
				}else if(tm.nature.toString().equals("nvendor")){
					nvendor.add(tm.word);
				}else if(tm.nature.toString().equals("nproduct")){
					nproduct.add(tm.word);
				}else if(tm.nature.toString().equals("nvulndesc")){
					nvulndesc.add(tm.word);
				}else if(tm.nature.toString().equals("nprotocol")){
					nprotocol.add(tm.word);
				}else if(tm.nature.toString().equals("nhardware")){
					nhardware.add(tm.word);
				}
			}
		}
		return iocs;
	} 

	/**
	 * —— newest ——(没有getEntity2)
	 * 装入过程——将词典与规则与最大熵识别的结果放入iocs
	 * @param list
	 * @return
	 * 
	 */
	public Map<String,List<String>> getEntity3(List<Term> list){
		for (int i = 0; i < list.size(); i++)  System.out.println(list.get(i));
		List<String> nr =new ArrayList<String>();
		List<String> ns =new ArrayList<String>();
		List<String> nt =new ArrayList<String>();
		List<String> Cert =new ArrayList<String>();
		List<String> AS =new ArrayList<String>();
		List<String> Email =new ArrayList<String>();
		List<String> URL =new ArrayList<String>();
		List<String> Domain =new ArrayList<String>();
		List<String> IPPORT =new ArrayList<String>();
		List<String> IP =new ArrayList<String>();
		List<String> MD5 =new ArrayList<String>();
		List<String> SHA1 =new ArrayList<String>();
		List<String> SHA256 =new ArrayList<String>();
		List<String> CVE =new ArrayList<String>();
		List<String> CAN =new ArrayList<String>();
		List<String> CNVD =new ArrayList<String>();
		List<String> CNNVD =new ArrayList<String>();
		List<String> CNCVE =new ArrayList<String>();
		List<String> Registry =new ArrayList<String>();
		List<String> Filename =new ArrayList<String>();
		List<String> Filepath =new ArrayList<String>();
		List<String> nvendor =new ArrayList<String>();
		List<String> nproduct =new ArrayList<String>();
		List<String> nvulndesc =new ArrayList<String>();
		List<String> nprotocol =new ArrayList<String>();
		List<String> nhardware =new ArrayList<String>();
		List<String> nreport =new ArrayList<String>();
		List<String> nevent =new ArrayList<String>();
		List<String> nhack =new ArrayList<String>();
		List<String> nthreat =new ArrayList<String>();
		List<String> nconference =new ArrayList<String>();
		iocs.put("nr", nr);
		iocs.put("ns", ns);
		iocs.put("nt", nt);
		iocs.put("Cert", Cert);
		iocs.put("AS", AS);
		iocs.put("Email", Email);
		iocs.put("URL", URL);
		iocs.put("Domain", Domain);
		iocs.put("IPPORT", IPPORT);
		iocs.put("IP", IP);
		iocs.put("MD5", MD5);
		iocs.put("SHA1", SHA1);
		iocs.put("SHA256", SHA256);
		iocs.put("CVE", CVE);
		iocs.put("CAN", CAN);
		iocs.put("CNVD", CNVD);
		iocs.put("CNNVD", CNNVD);
		iocs.put("CNCVE", CNCVE);
		iocs.put("Registry", Registry);
		iocs.put("Filename", Filename);
		iocs.put("Filepath", Filepath );
		iocs.put("nvendor", nvendor);
		iocs.put("nproduct", nproduct);
		iocs.put("nvulndesc", nvulndesc);
		iocs.put("nprotocol", nprotocol);
		iocs.put("nhardware", nhardware);
		iocs.put("nreport", nreport);
		iocs.put("nevent", nevent);
		iocs.put("nhack", nhack);
		iocs.put("nthreat", nthreat);
		iocs.put("nconference", nconference);
		HashSet<String> set = new  HashSet<String>(); 
		if(repeatCheck){
			for(Term tm : list) {
				if(set.add(tm.word+tm.nature.toString())){
					if(tm.nature.toString().equals("nthreat")){
							nthreat.add(tm.word);
					}else if(tm.nature.startsWith("nreport")){
						nreport.add(tm.word);
					}else if(tm.nature.startsWith("nr")){
						nr.add(tm.word);
					}else if(tm.nature.startsWith("ns")){
						ns.add(tm.word);
					}else if(tm.nature.startsWith("nt")){
						nt.add(tm.word);
					}else if(tm.nature.toString().equals("Cert")){
						Cert.add(tm.word);
					}else if(tm.nature.toString().equals("AS")){
						AS.add(tm.word);
					}else if(tm.nature.toString().equals("Email")){
						Email.add(tm.word);
					}else if(tm.nature.toString().equals("URL")){
						URL.add(tm.word);
					}else if(tm.nature.toString().equals("Domain")){
						Domain.add(tm.word);
					}else if(tm.nature.toString().equals("IPPROT")){
						IPPORT.add(tm.word);
					}else if(tm.nature.toString().equals("IP")){
						IP.add(tm.word);
					}else if(tm.nature.toString().equals("MD5")){
						MD5.add(tm.word);
					}else if(tm.nature.toString().equals("SHA1")){
						SHA1.add(tm.word);
					}else if(tm.nature.toString().equals("SHA256")){
						SHA256.add(tm.word);
					}else if(tm.nature.toString().equals("CVE")){
						CVE.add(tm.word);
					}else if(tm.nature.toString().equals("CAN")){
						CAN.add(tm.word);
					}else if(tm.nature.toString().equals("CNVD")){
						CNVD.add(tm.word);
					}else if(tm.nature.toString().equals("CNNVD")){
						CNNVD.add(tm.word);
					}else if(tm.nature.toString().equals("CNCVE")){
						CNCVE.add(tm.word);
					}else if(tm.nature.toString().equals("Registry")){
						Registry.add(tm.word);
					}else if(tm.nature.toString().equals("Filename")){
						Filename.add(tm.word);
					}else if(tm.nature.toString().equals("Filepath")){
						Filepath.add(tm.word);
					}else if(tm.nature.toString().equals("nvendor")){
						nvendor.add(tm.word);
					}else if(tm.nature.toString().equals("nproduct")){
						nproduct.add(tm.word);
					}else if(tm.nature.toString().equals("nvulndesc")){
						nvulndesc.add(tm.word);
					}else if(tm.nature.toString().equals("nprotocol")){
						nprotocol.add(tm.word);
					}else if(tm.nature.toString().equals("nhardware")){
						nhardware.add(tm.word);
					}else if(tm.nature.toString().equals("nevent")){
						nevent.add(tm.word);
					}else if(tm.nature.toString().equals("nhack")){
						nhack.add(tm.word);
					}else if(tm.nature.toString().equals("nconference")){
						nconference.add(tm.word);
					}
				}
			}
		}else{
			for(Term tm : list) {
				if(tm.nature.toString().equals("nthreat")){
					nthreat.add(tm.word);
				}else if(tm.nature.toString().equals("nreport")){
					nreport.add(tm.word);
				}else if(tm.nature.toString().equals("nvulndesc")){
					nvulndesc.add(tm.word);
				}else if(tm.nature.startsWith("nr")){
					nr.add(tm.word);
				}else if(tm.nature.startsWith("ns")){
					ns.add(tm.word);
				}else if(tm.nature.startsWith("nt")){
					nt.add(tm.word);
				}else if(tm.nature.toString().equals("Cert")){
					Cert.add(tm.word);
				}else if(tm.nature.toString().equals("AS")){
					AS.add(tm.word);
				}else if(tm.nature.toString().equals("Email")){
					Email.add(tm.word);
				}else if(tm.nature.toString().equals("URL")){
					URL.add(tm.word);
				}else if(tm.nature.toString().equals("Domain")){
					Domain.add(tm.word);
				}else if(tm.nature.toString().equals("IPPROT")){
					IPPORT.add(tm.word);
				}else if(tm.nature.toString().equals("IP")){
					IP.add(tm.word);
				}else if(tm.nature.toString().equals("MD5")){
					MD5.add(tm.word);
				}else if(tm.nature.toString().equals("SHA1")){
					SHA1.add(tm.word);
				}else if(tm.nature.toString().equals("SHA256")){
					SHA256.add(tm.word);
				}else if(tm.nature.toString().equals("CVE")){
					CVE.add(tm.word);
				}else if(tm.nature.toString().equals("CAN")){
					CAN.add(tm.word);
				}else if(tm.nature.toString().equals("CNVD")){
					CNVD.add(tm.word);
				}else if(tm.nature.toString().equals("CNNVD")){
					CNNVD.add(tm.word);
				}else if(tm.nature.toString().equals("CNCVE")){
					CNCVE.add(tm.word);
				}else if(tm.nature.toString().equals("Registry")){
					Registry.add(tm.word);
				}else if(tm.nature.toString().equals("Filename")){
					Filename.add(tm.word);
				}else if(tm.nature.toString().equals("Filepath")){
					Filepath.add(tm.word);
				}else if(tm.nature.toString().equals("nvendor")){
					nvendor.add(tm.word);
				}else if(tm.nature.toString().equals("nproduct")){
					nproduct.add(tm.word);
				}else if(tm.nature.toString().equals("nprotocol")){
					nprotocol.add(tm.word);
				}else if(tm.nature.toString().equals("nhardware")){
					nhardware.add(tm.word);
				}else if(tm.nature.toString().equals("nevent")){
					nevent.add(tm.word);
				}else if(tm.nature.toString().equals("nhack")){
					nhack.add(tm.word);
				}else if(tm.nature.toString().equals("nconference")){
					nconference.add(tm.word);
				}
			}
		}
		return iocs;
	} 
	
	
	/**
	 * —— old ——
	 * 最大熵与词典规则标注相结合——待修改，将两者合并当做predict中的一个函数来做。好像还应该添加上去重
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getPredictMap(List<Term> list) throws IOException{
		Map<String,List<String>> predictMap=this.getEntity(list);
		Map<String,List<String>> predictMap2=new Predict().getResult(list);
		predictMap.get("nr").addAll(predictMap2.get("nr"));
		predictMap.get("nt").addAll(predictMap2.get("norg"));
		predictMap.put("nthreat", predictMap2.get("nthreat"));
		predictMap.put("nhack", predictMap2.get("nhack"));
		predictMap.put("nevent", predictMap2.get("nevent"));
		predictMap.put("nreport", predictMap2.get("nreport"));
		predictMap.put("nconference", predictMap2.get("nconference"));
		predictMap.get("nproduct").addAll(predictMap2.get("nproduct"));
		return predictMap;
	}
	
	/**
	 * —— new ——
	 * 最大熵与词典规则标注相结合——待修改，将两者合并当做predict中的一个函数来做。好像还应该添加上
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getPredictMap2(List<Term> list) throws IOException{
		Map<String,List<String>> predictMap=this.getEntity(list);
		Map<String,List<String>> predictMap2=new Predict().getResult2(list,predictMap);
		return predictMap2;
	}
	
	/**
	 * —— newest 面向memm——
	 * 向词典规则的term中加入最大熵预测的值，全部放入list，最后转换为map
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getPredictMap3(List<Term> list) throws IOException{
		new Predict().getResult3(list);
		CoreStopWordDictionary.apply(list);
		Map<String,List<String>> predictMap=this.getEntity3(list);
		return predictMap;
	}
	
	/**
	 * —— newest 面向crf ——
	 * 向词典规则的term中加入crf预测的值，全部放入list，最后转换为map
	 * 后续应该将有规律的病毒名称归为正则表达式，举例Trojan/Win32.Uploader 
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public Map<String,List<String>> getPredictMap4(List<Term> list) throws IOException{
		new CrfTag().getResult4(list, "basenp", 0.7, "test.data", false, null, null);//false 不重新训练模型
		CoreStopWordDictionary.apply(list);
		Map<String,List<String>> predictMap=this.getEntity3(list);//与最大熵相同
		return predictMap;
	}
	public static void main(String[] args) throws IOException {
		PreEntityExtract ee = new PreEntityExtract();
//		从pdf文本读取实体
		String dataPDF=new GetTextFromPdf("G:\\extraEclipseWork\\AnnSecurityWeekly\\WhiteElephant.pdf").getResult();
//		从txt文本读取实体
//		String dataTXT=new GetTextFromTxt("G:\\extraEclipseWork\\AnnSecurityWeekly\\test.txt").getResult();
		
//		String str="自从上个月21号美国DNS服务提供商Dyn遭遇大规模DDoS攻击以来，关于幕后黑手的身份引发各界猜测。Flashpoint发布的最新报告认为，这次超大型的DDoS攻击很有可能就是一名脚本小子将索尼游戏网站(PSN)作为目标，利用Mirai恶意软件感染大量IoT僵尸网络而发动的，Dyn由于向PSN提供域名解析服务而惨遭中枪。与之前所怀疑的、黑客活动组织、恐怖分子或国家黑客攻击无关。但是来自ThousandEyes的网络故障分析师Nick Kephart对此事持有不同的意见，他表示发起如此规模的DDoS攻击只是为了攻击Dyn的一个客户的说法是站不住脚的。与此同时，Akamai公司的Martin McKeay更是直言，Flashpoint的分析就是为了掩盖真正的攻击目标。";
		String str1="360威胁情报中心发布了对应IP为 202.113.112.30的 Microsoft Windows 漏洞CVE-2018-7600,该漏洞由WannaCry病毒系列导致";
//		String str2="在Linux平台下我们使用不同的浏览器访问该页面都会提示下载名为“install_flashplayer.exe”(md5为 7e68371ba3a988ff88e0fb54e2507f0d )的更新文件，当操作系统为Mac OS时，水坑则向Safari浏览器推送能在Mac OS环境中运行的恶意更新程序“install_flashplayer_mac.zip”";
//		String str3="Customer Insights and Analysis公司项目总监伊林·史密斯表示，“银行、分立制造，特别是高科技领域及政府机构方面，均在安全方面作了大量投资以应对大规模网络攻击。随着物联网（IoT）设备与基础设施保护需求的日益提升，电信、各政府机构的投入将进一步推动全球安全支出的增长。”";
//		String str4="安天在 2012 年获取导向相关的载荷最早的投放行为曾淹没于其他海量的安全事件中，并未将相关事件判定为 APT 攻击。因此需要感谢安全厂商 Norman 在 2013 年 7 月所发布的报告《OPERATION HANGOVER |Executive Summary——Unveiling an Indian Cyberattack Infrastructure》，Norman 在上述报告根据在分析中发现的原始工程名“HangOve”，将此事件命名为“HangOver”。这组事件即是安天称为“白象一代”的行动。这让安天反思过去在发现和追踪 APT 攻击中，过度考虑攻击技巧和漏洞利用的问题，并开始针对周边国家对中国攻击检测有了新的方法和视角。 ";
//		String data="释放的 VBScript 脚本，脚本执行后连接远程服务器 zolipas.info。";
//		String data2="此后利用这些服务器对SK Networks公司的电脑管理系统进行黑客攻击，并向企业电脑植入了“灵鼠”(ghost rat)病毒。";

		System.out.println("...分词结果...");
		List<Term> list = ee.getShortSegment2( str1);
		for(Term ss:list){
			System.out.println(ss.toString());
		}
		System.out.println("...正则与词典匹配...");
		ee.repeatCheck=false;//是否查重
		ee.allPretreatment(list);
		for(Term ss:list){
			System.out.println(ss.toString());
		}
		System.out.println("...统计模型结果...");
		Map<String,List<String>> predictMap=ee.getPredictMap3(list);
//		Map<String,List<String>> predictMap=ee.getPredictMap4(list);
		System.out.println("\n\n\n============最终分析结果============");
		//在这里输出一波最后算出的实体到控制台：
		for (Map.Entry entry : predictMap.entrySet()) {
			List<String> ioc = (List) entry.getValue();
			System.out.println("********" + entry.getKey() + "如下*********");
			for (String string : ioc) {
				System.out.println(string);
			}
		}
		//在这里输出一波实体到excel表格
		new Map2Excel(predictMap);
	}
}
