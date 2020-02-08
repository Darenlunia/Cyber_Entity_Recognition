package org.cyber.dictTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.cyber.util.FreebaseList;
import org.cyber.util.ListLoader;


import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import edu.stanford.nlp.util.StringUtils;

public class StucExtract {
	static{
		Nature.create("nvendor");
		Nature.create("nproduct");
		Nature.create("nvulndesc");
	}
	private static String swInfoList = "dictionaries/software_info.json";
	private static String swDevList = "dictionaries/software_developers.json";
	private static String osList = "dictionaries/operating_systems.json";
	private static String relTermsList = "dictionaries/relevant_terms.txt";

	private String listFile;
	private FreebaseList swProductList;
	private FreebaseList swVendorList;
	private Set<String> relevantTermsList;

	/**
	 * 载入各个词典
	 * @param className
	 * @param config
	 * @return 
	 */
	public StucExtract() {
		Properties config=StringUtils.argsToProperties("-swProducts", swInfoList, "-swVendors", swDevList, "-swOS", osList, "-vulnDesc", relTermsList);
		listFile = config.getProperty("swProducts", swInfoList);
		swProductList = ListLoader.loadFreebaseList(listFile,"nproduct");
		listFile = config.getProperty("swVendors", swDevList);
		swVendorList = ListLoader.loadFreebaseList(listFile, "nvendor");
		listFile = config.getProperty("swOS", osList);
		FreebaseList temp = ListLoader.loadFreebaseList(listFile,"nproduct");
		if (temp != null) {
			swProductList.addEntries(temp);
		}
		
		listFile = config.getProperty("vulnDesc", relTermsList);
		relevantTermsList = ListLoader.loadTextList(listFile);
		
	}
	
	/**
	 * @param data 要分词和词性标注的的数据
	 */
	public void annotate(List<Term> tokens) {
		for (Term token : tokens) {
			if (swVendorList.contains(token.word)) {//直接匹配词典
				token.nature=Nature.fromString("nvendor");
			}
			else if (swProductList.contains(token.word)) {
				token.nature=Nature.fromString("nproduct");
			}
			else if (relevantTermsList.contains(token.word)) {
				token.nature=Nature.fromString("nvulndesc");
			}
		}	
	}

//	public static void main(String arg[]){
//		
//		PreEntityExtract et=new PreEntityExtract();
//		List<Term> d=et.getShortSegment2("Mac OS");//选择viterbi或CRF实体抽取
//		StucExtract test=new StucExtract();
//		test.annotate(d);
//		for (int i = 0; i < d.size(); i++) {
//			System.out.println(d.get(i));
//		}
//	}
}
