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
 * @Title  ģʽƥ��
 * @Description  pdf����ƥ��ini�ļ�������ʽ
 * @param ���캯������ΪҪ�����������ʽ�����ļ�
 * @�ռ��ӿ�  printPatternResult(fpath)������Ϊpdf��path
 * @author Administrator
 *
 */

public class PatternsMatch {

	private boolean repeatCheck = false;//Ĭ�ϲ����أ�������Ҫ����repeat=true
	
	private Map<String, Pattern> patterns=new HashMap<String, Pattern>();
	// keyΪpattern���ƣ�value-PatternΪ�������ִ�
	private Map<String, Map<String, String>> temp=new HashMap<String, Map<String, String>>();// �ȼ���ConfigReader������ֵkey,value;
	// ���ڹ���ConfigReader�����patterns��patterns����Ӧ����ConfigReaderֱ�ӱ�ʾ�ģ��޸��鷳���Բ�����һ��tempֵ��
	private Map<String, Boolean> defang= new HashMap<String, Boolean>();// keyΪpattern���ƣ�valueΪ�л���
	// private HashMap<String, Pattern> whiteList;
	private WhiteList whiteList= new WhiteList();
	// ������
	private String dir ="G:\\extraEclipseWork\\ioc-parse\\wlini\\patterns.ini";
	//Ĭ�������ļ�
	private String data;
	//pdf���ݣ�ƥ�䴦���data���޳���ƥ�������
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data=data;
	}
	/**
	 * ������ʽ  ���ش���true
	 * @param repeat
	 */
	public void setRepeatCheck(boolean repeat) {//���������Ҫ���� trueֵ
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
	 * @param fpath  Ҫ��ȡ��pdf����Ŀ¼
	 */
	public PatternsMatch(String fpath) {//pattern.ini����Ŀ¼
		// TODO Auto-generated constructor stub
		this.loadPatterns();
		GetTextFromPdf gtfp = new GetTextFromPdf(fpath);
		this.data = gtfp.getResult();
	}
	
	/**
	 * ��һ���������������캯�����������롰test��
	 * @param teststring 
	 */
	public PatternsMatch(String str,String teststring) {//Ĭ��ʹ����pattern.ini����Ŀ¼
		// TODO Auto-generated constructor stub
		this.data = teststring;
		this.loadPatterns();	
	}
	

	/**
	 * @Description  ���캯��ʽʱ���á�
	 * �������ļ�����ÿ��key��������ʽ���б��벢����(Map)patterns��(Map)defang��
	 * ���Ұ�cfr��key��value��������temp
	 * @throws IOException
	 */
	public void loadPatterns() {
		//��ȡ�����ļ�
		ConfigReader cfr = new ConfigReader(dir);
		Iterator<?> it = cfr.get().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Map<String, String> value = (Map<String, String>) entry.getValue();
			temp.put(key, value);
			// ��ÿ��key��pattern���Խ���compile����������pattern
			Pattern pattern = Pattern.compile(value.get("pattern"));
			patterns.put(key, pattern);
			if (value.containsKey("defang")) {
				defang.put(key, true);
			}
		}
		// System.out.println(patterns);
	}

	/**
	 * û����
	 * ƥ�������
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
	 * ƥ��patterns.ini��whiteList�µ�ini��ע���ˣ�;ƥ������������ʽƥ����ı�
	 * @return
	 * @throws IOException
	 */
	public Map<String, List<String>> patternsMatch() throws IOException {
		System.out.println("���ڽ���������ʽƥ��... ...");
		Map<String, List<String>> iocs = new HashMap<String, List<String>>();// ��¼����ƥ���ȫ�����
		// ��data��ƥ�����
//		System.out.println("ƥ�����ݣ�"+data);
		Iterator<?> it = patterns.entrySet().iterator();
		while (it.hasNext()) {
			List<String> ioc = new ArrayList<String>();
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Pattern value = (Pattern) entry.getValue();
			System.out.println("����ƥ���patternΪ" + key + "... ...");
			Matcher m = value.matcher(data);//ƥ��patterns.ini	
			String rec=temp.get(key).get("pattern").replace("\b", "");
			data=data.replaceAll(rec, " ** ");//��ƥ������������ʽ��**��ʾ
			Set<String> s = new HashSet<String>();//����
			while (m.find()) {
				String mm = m.group(1);
//				if (is_whitelisted(m.group(1),key)) {//ƥ��whitelist/ini
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
		//ƥ���������
		Pattern analysisReportPattern = Pattern.compile(".*��(.+)\u5206\u6790\u62a5\u544a��.*");
		String AnalysisReport="AnalysisReport";
		System.out.println("����ƥ���patternΪ" + AnalysisReport + "... ...");
		Matcher matcher = analysisReportPattern.matcher(data);
		List<String> ioc = new ArrayList<String>();
		while (matcher.find()) {
			String result = matcher.group(1);
			result="��"+result+"�������桷";
			ioc.add(result);
		}
		iocs.put(AnalysisReport, ioc);
		return iocs;
	}

	
	/**
	 * ��dataƥ��patterns.ini�����ƥ���Ĵ���
	 * @return
	 * @throws IOException
	 */
	public String patternsMatchOneWord(String data) throws IOException {
		Iterator<?> it = patterns.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String key = (String) entry.getKey();
			Pattern value = (Pattern) entry.getValue();
			Matcher m = value.matcher(data);//ƥ��patterns.ini
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
	 * ********����ӿ�*********
	 * @Description ����ƥ�亯��������̨ѭ�����pattern(url/host...)�Ͷ�Ӧ��ȫƥ����
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
				System.out.println(key + "���Ӧ������: " + value.get(i));
			}
		}
	}
	
	public static void main(String[] args){
		String str="����ý�������������µ���� ( CIB ) ��ͨ�����簲ȫ֪ʶ���ԵĹ��ڽ����� 250 �� U �̣��������췽½���ӵ�����ơ��ۼ� 54 �� 8GB U ���ж����ж�����������ò�����ȥ�� 12 �� 11 ���� 15 ���ڼ���̨����ͳ�칫���������Ϣ��ȫ���һ���֡��ö�����������Ϊ XtbSeDuA.exe ���ܹ���32 λ���������ȡ������Ϣ������ CIB ��˵��������������ȡ�ɹ��ö����������ͼ�����ݴ��ݵ�һ��λ�ڲ����� IP��ַ������ IP ��ַ������ת������ݲ����ķ��������о���Ա͸¶�ö��������Ϊ��ŷ���̾���֯�� 2015 �귢�ֵ�����թƭ�Ż�ʹ�õġ�CIB ��ʾ�����ָ�Ⱦ��Դ�ڵ��سа���Ա����ʹ�õ� �� ������ϵͳת�Ƶ���������������洢���� �� �Ĺ���վ��������Щ�����������й���½������";
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
