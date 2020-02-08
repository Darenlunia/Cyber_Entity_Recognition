package org.cyber.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 在本地加载Freebase文件，载入集�?
 * @author Administrator
 *
 */
public class ListLoader {
	private static ObjectMapper mapper = new ObjectMapper();

	public static FreebaseList loadFreebaseList(String listFile, String listType) {
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		mapper.configure(DeserializationFeature.USE_JAVA_ARRAY_FOR_JSON_ARRAY, true);
		
		FreebaseList freebaseList = null;
		try {
			InputStream inputStream = ListLoader.class.getClassLoader().getResourceAsStream(listFile);
			freebaseList = mapper.readValue(inputStream, FreebaseList.class);
			freebaseList.setListType(listType);
		} catch (Exception ex) {
			try {
				InputStream inputStream = new FileInputStream(new File(listFile));
				freebaseList = mapper.readValue(inputStream, FreebaseList.class);
				freebaseList.setListType(listType);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return freebaseList;
	}
	
	/**
	 * 从文档中读取数据
	 * @param textFile
	 * @return
	 */
	public static Set<String> loadTextList(String textFile) {
		Set<String> textList = new HashSet<String>();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(ListLoader.class.getClassLoader().getResourceAsStream(textFile)));
			String term = reader.readLine();
			while (term != null) {
				textList.add(term);
				term = reader.readLine();
			}
			reader.close();
		} catch (Exception ex) {
			try {
				BufferedReader reader = new BufferedReader(new FileReader(new File(textFile)));
				String term = reader.readLine();
				while (term != null) {
					textList.add(term);
					term = reader.readLine();
				}
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
		return textList;
	}

	
//	public static void main(String[] args) {
////		FreebaseList freebaseList = ListLoader.loadFreebaseList("src/main/resources/lists/software_info.json","software");
////		System.out.println(freebaseList.toString());
//		Set<String> relTerms = ListLoader.loadTextList("src/main/resources/lists/relevant_terms.txt");
//		System.out.println(relTerms);
//	}

}
