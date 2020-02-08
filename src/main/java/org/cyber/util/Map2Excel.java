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

	private String fileName = "生成的实体列表.xls";        // 定义文件名
//	String headString = "实体信息表";          // 定义表格标题https://blog.csdn.net/myCSDN_sy_yl/article/details/78469454
	private String sheetName = "工作表一";                  // 定义工作表表名
	private String filePath = "G:\\predictResult\\";             // 文件本地保存路径
	private String[] thead = {" ","实体类型","实体内容"};                    // 定义表头内容
	int[] sheetWidth = {1,4000,18000};   // 定义每一列宽度

	public Map2Excel(Map<String,List<String>> result) throws IOException{
	HSSFWorkbook wb = new HSSFWorkbook();           // 创建Excel文档对象
	HSSFSheet sheet = wb.createSheet(sheetName);    // 创建工作表
	// 创建表头
	ExcelUtil.createThead(wb, sheet, thead, sheetWidth);
	ExcelUtil.createTable2(wb, sheet, result);
	
	//输出Excel到本地
	FileOutputStream fos = new FileOutputStream(new File(filePath+fileName));
	// filePath,fileName是如上定义的文件保存路径及文件名
	wb.write(fos);
	fos.close();
	wb.close();
	}
}
