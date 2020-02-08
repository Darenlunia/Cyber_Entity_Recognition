package org.cyber.memmTag;

import java.io.IOException;
import java.util.List;

public class MemmTag {

	public MemmTag(){
		
	}
	
	public void pendingFunc(List<Word> sentences){
		//对sentences进行最后处理（对括号英文标注词性）
		if(sentences.size()>0)
			for(int i=0;i<sentences.size()-2;i++){
					if(sentences.get(i).word.equals("（")&&sentences.get(i+1).pos.equals("nx")&&sentences.get(i+2).word.equals("）")){//这个应该是空格，和后面的第二个英文单词
						sentences.get(i+1).pos=sentences.get(i-1).pos;
					}
			}
	}
	
	public static void main(String[] args) throws IOException{
		new Predict().getResult("YouTube遭黑客破坏并清除大量流行音乐视频；");
		System.out.println("*******end******");
	}
}
