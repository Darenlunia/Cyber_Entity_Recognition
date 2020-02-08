package org.cyber.memmTag;

import java.util.HashSet;

public class Word {
	/**
	 * 词面
	 */
	public String word;
	/**
	 * 词性n
	 */
	public String pos;
	/**
	 * 词最终标签--O B I (E)
	 */
	public String tag;
	
	public HashSet<String> feature=new HashSet<String>();

	public Word( String word,String pos,String tag){
		this.word=word;
		this.pos=pos;
		this.tag=tag;
	}
	
	public Word( String word,String pos){
		this.word=word;
		this.pos=pos;
	}
	
	public void setFeature(HashSet<String> features) {
		this.feature=features;
	}
}
