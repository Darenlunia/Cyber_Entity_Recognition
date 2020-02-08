package org.cyber.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GetTextFromTxt {

	private String data;
	
	public GetTextFromTxt(String path) throws IOException{
		BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));  
		String line=null;  
		this.data="";
		while((line=br.readLine())!=null)  {
			data+=line;
		}
	}
	
	public String getResult(){
		return this.data;
	}
	
}
