package org.cyber.memmTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.cyber.memmTag.Word;

/**
 * 
 * @author Administrator
 *
 */
public class GetFeatures {


	private String[] paths={
			"\\memmConfig\\CUR_ORG_SUFFIX.txt",
			"\\memmConfig\\CUR_VIRUS_PREFIX.txt",
			"\\memmConfig\\FRONT_HACK.txt",
			"\\memmConfig\\FRONT_ORG.txt",
			"\\memmConfig\\CUR_EVENT_SUFFIX.txt",
			"\\memmConfig\\NEXT_HACK.txt",
			"\\memmConfig\\FRONT_VIRUS.txt",
			"\\memmConfig\\NEXT_VIRUS.txt",
	};
	
	private List<HashSet<String>> featureList = new ArrayList<HashSet<String>>();


	/**
	 * @return
	 */
	public List<HashSet<String>> getFeatureList() {
		return featureList;
	}

	public String toString(List<HashSet<String>> list) {
		String string = "";
		for (HashSet<String> hs : list) {
			string += "(";
			for (String str : hs)
				string += str;
			string += ")";
		}
		return string;
	}

	public GetFeatures() {

	}

	/**
	 * 
	 * 
	 * @param sentences
	 * @throws IOException
	 */
	public GetFeatures(List<Word> sentences) throws IOException {
		Map<Integer, String> context = new HashMap<Integer, String>();
		Map<Integer, String> pos_context = new HashMap<Integer, String>();
		int l = sentences.size();
		context.put(-2, "_START_");
		context.put(-1, "_START_");
		context.put(l, "_END_");
		context.put(l + 1, "_END_");
		pos_context.put(-2, "_PSTART_");
		pos_context.put(-1, "_PSTART_");
		pos_context.put(l, "_PEND_");
		pos_context.put(l + 1, "_PEND_");
		for (int i = 0; i < sentences.size(); i++) {
			context.put(i, sentences.get(i).word);
			pos_context.put(i, sentences.get(i).pos);
			// pos_context[i]=pos_tags[i];
		}
		String prev = "n_START_";
		String prev2 = "n_START_";

		for (int i = 0; i < sentences.size(); i++) {
			featureList.add(this.getFeatures(i, context, pos_context, prev, prev2));
			prev2 = prev;
			prev = sentences.get(i).tag;
		}
	}

	public HashSet<String> getFeatures(int i, Map<Integer, String> context, Map<Integer, String> pos_context,
			String prev, String prev2) throws IOException {
		
		HashSet<String> features = new HashSet<String>();
		add(features, "(i-1)tag=", prev);
		add(features, "(i-2)tag=", prev2);
		add(features, "(i-1)(i-2)tag=", prev, prev2);
		add(features, "(i-1)tag(i-1)word=", prev,context.get(i - 1));
		
		
		add(features, "(i-1)word=", context.get(i - 1));
		add(features, "(i-2)word=", context.get(i - 2));
		add(features, "(i)word=", context.get(i));
		add(features, "(i+1)word=", context.get(i + 1));
		add(features, "(i+2)word=", context.get(i + 2));

		
		add(features, "(i-1)pos=", pos_context.get(i - 1));
		add(features, "(i-1)pos(i-1)word=", pos_context.get(i-1),context.get(i-1));
		add(features, "(i-2)pos(i-2)word=", pos_context.get(i-2),context.get(i-2));
		add(features, "(i-2)pos=", pos_context.get(i - 2));
//		add(features, "(i-1)(i+1)pos=", pos_context.get(i - 1),pos_context.get(i + 1));
//		add(features, "(i-1)(i+2)pos=", pos_context.get(i - 1),pos_context.get(i + 2));
//		add(features, "(i-1)pos(i-2)word=", pos_context.get(i - 1),context.get(i - 2));
//		add(features, "(i-1)word(i-2)pos=", context.get(i - 1),pos_context.get(i - 2));
//		add(features, "(i)(i+2)pos=", pos_context.get(i),pos_context.get(i + 2));
		add(features, "(i)pos=", pos_context.get(i));
		add(features, "(i)pos(i)word=", pos_context.get(i),context.get(i));
		add(features, "(i+1)pos=", pos_context.get(i + 1));
		add(features, "(i+1)pos(i+1)word=", pos_context.get(i+1),context.get(i+1));
		add(features, "(i+2)pos=", pos_context.get(i + 2));
		add(features, "(i+1)(i+2)pos=", pos_context.get(i + 1),pos_context.get(i + 2));
		add(features, "(i-1)(i-2)pos=", pos_context.get(i - 1),pos_context.get(i - 2));
		add(features, "(i)(i-1)pos=", pos_context.get(i), pos_context.get(i - 1));
		add(features, "(i+1)(i)pos=", pos_context.get(i + 1), pos_context.get(i));
//		add(features, "(i)(i+1)(i+2)pos=",pos_context.get(i), pos_context.get(i + 1),pos_context.get(i + 2));
//		add(features, "(i)(i-1)(i-2)pos=",pos_context.get(i), pos_context.get(i - 1),pos_context.get(i - 2));
		
		for (int j=0;j<paths.length;j++) {
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(System.getProperty("user.dir") +paths[j]),"UTF-8"));  
			String s=null;  
			while(j==0&&(s=br.readLine())!=null) 
			{  
			    if(s.equals(context.get(i))){
			    	add(features,"CUR_ORG_SUFFIX=", "true");
			    	break;
			    }
			}
			while(j==1&&(s=br.readLine())!=null){
		    	if(s.equals(context.get(i))){
			    	add(features,"CUR_VIRUS_PREFIX=", "true");
			    	break;
			    }
			}
			while(j==2&&(s=br.readLine())!=null){
		    	if(i>0&&s.equals(context.get(i-1))){
			    	add(features,"FRONT_HACK=", "true");
			    	break;
			    }
			}
			while(j==3&&(s=br.readLine())!=null){
		    	if(i>0&&s.equals(context.get(i-1))){
			    	add(features,"FRONT_ORG=", "true");
			    	break;
			    }
			}
			while(j==4&&(s=br.readLine())!=null){
		    	if(s.equals(context.get(i))){
			    	add(features,"CUR_EVENT_SUFFIX=", "true");
			    	break;
			    }
			}
			while(j==5&&(s=br.readLine())!=null){
		    	if(i<context.size()&&s.equals(context.get(i+1))){
			    	add(features,"NEXT_HACK=", "true");
			    	break;
			    }
			}
			while(j==6&&(s=br.readLine())!=null){
		    	if(i>0&&s.equals(context.get(i-1))){
			    	add(features,"FRONT_VIRUS=", "true");
			    	break;
			    }
			}
			while(j==7&&(s=br.readLine())!=null){
		    	if(i<context.size()&&s.equals(context.get(i+1))){
			    	add(features,"NEXT_VIRUS=", "true");
			    	break;
			    }
			}
			br.close();
		}
		return features;
	}
	private void add(HashSet<String> features, String name, String st1) {
		features.add(name + st1);
	}

	private void add(HashSet<String> features, String name, String st1, String st2) {
		features.add(name + st1 + "+" + st2);
	}

	private void add(HashSet<String> features, String name, Integer b) {
			features.add(name +b.toString());
	}

//	 private void add(HashSet<String> features,String name, String st1, String st2,String st3) {
//	 features.add(name+"+"+st1+"+"+st2+"+"+st3);
//	 }
}
