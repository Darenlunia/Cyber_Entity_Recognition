package org.cyber.memmTag;

import java.io.IOException;
import java.util.List;

public class MemmTag {

	public MemmTag(){
		
	}
	
	public void pendingFunc(List<Word> sentences){
		//��sentences���������������Ӣ�ı�ע���ԣ�
		if(sentences.size()>0)
			for(int i=0;i<sentences.size()-2;i++){
					if(sentences.get(i).word.equals("��")&&sentences.get(i+1).pos.equals("nx")&&sentences.get(i+2).word.equals("��")){//���Ӧ���ǿո񣬺ͺ���ĵڶ���Ӣ�ĵ���
						sentences.get(i+1).pos=sentences.get(i-1).pos;
					}
			}
	}
	
	public static void main(String[] args) throws IOException{
		new Predict().getResult("YouTube��ڿ��ƻ��������������������Ƶ��");
		System.out.println("*******end******");
	}
}
