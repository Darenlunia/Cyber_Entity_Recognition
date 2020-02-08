package org.cyber.memmTag;

import java.util.HashSet;

public class Word {
	/**
	 * ����
	 */
	public String word;
	/**
	 * ����n
	 */
	public String pos;
	/**
	 * �����ձ�ǩ--O B I (E)
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
