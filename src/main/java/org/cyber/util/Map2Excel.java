package org.cyber.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class Map2Excel {

	private String fileName = "���ɵ�ʵ���б�.xls";        // �����ļ���
//	String headString = "ʵ����Ϣ��";          // ���������https://blog.csdn.net/myCSDN_sy_yl/article/details/78469454
	private String sheetName = "������һ";                  // ���幤�������
	private String filePath = "G:\\predictResult\\";             // �ļ����ر���·��
	private String[] thead = {" ","ʵ������","ʵ������"};                    // �����ͷ����
	int[] sheetWidth = {1,4000,18000};   // ����ÿһ�п��

	public Map2Excel(Map<String,List<String>> result) throws IOException{
	HSSFWorkbook wb = new HSSFWorkbook();           // ����Excel�ĵ�����
	HSSFSheet sheet = wb.createSheet(sheetName);    // ����������
	// ������ͷ
	ExcelUtil.createThead(wb, sheet, thead, sheetWidth);
	ExcelUtil.createTable2(wb, sheet, result);
	
	//���Excel������
	FileOutputStream fos = new FileOutputStream(new File(filePath+fileName));
	// filePath,fileName�����϶�����ļ�����·�����ļ���
	wb.write(fos);
	fos.close();
	wb.close();
	}
}
