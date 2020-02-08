package org.cyber.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

/** 
 *  
 * @Title: getTextFromPdf 
 * @Description: 读取pdf文件内容 
 * @param filePath 
 * @return: 读出的pdf的内容 
 * 
 */  
public class GetTextFromPdf {
	
	private String result;
	
	public String getResult() {
		return result;
	}
	
	public GetTextFromPdf(String filePath) {  
	   // String result = null;  
	    FileInputStream is = null;  
	    PDDocument document = null;  
	    try {  
	        is = new FileInputStream(filePath);  
	        PDFParser parser = new PDFParser(is);  
	        parser.parse();  
	        document = parser.getPDDocument();  
	        PDFTextStripper stripper = new PDFTextStripper(); 
	        result = stripper.getText(document);  
	    } catch (FileNotFoundException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    } finally {  
	        if (is != null) {  
	            try {  
	                is.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	        if (document != null) {  
	            try {  
	                document.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	    System.out.println(result);
	}  
	

}
