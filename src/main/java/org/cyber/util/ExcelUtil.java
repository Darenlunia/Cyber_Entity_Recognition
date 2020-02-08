package org.cyber.util;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExcelUtil {

    /**
     * ����������
     * @param wb            Excel�ĵ�����
     * @param sheet         ���������
     * @param headString    ��������
     * @param col           ����ռ������
     */
    public static void createHeadTittle(HSSFWorkbook wb,HSSFSheet sheet,String headString,int col){
        HSSFRow row = sheet.createRow(0);           // ����Excel���������
        HSSFCell cell = row.createCell(0);          // ����Excel������ָ���еĵ�Ԫ��
        row.setHeight((short) 1000);                // ���ø߶�

        cell.setCellType(HSSFCell.ENCODING_UTF_16); // ���嵥Ԫ��Ϊ�ַ�������
        cell.setCellValue(new HSSFRichTextString(headString));

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, col));  // ָ������ϲ�����

        // ���嵥Ԫ���ʽ����ӵ�Ԫ�����ʽ������ӵ�������
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);             // ָ����Ԫ����ж���
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);  // ָ����Ԫ��ֱ���и�����
        cellStyle.setWrapText(true);                                    // ָ����Ԫ���Զ�����

        // ���õ�Ԫ������
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("΢���ź�");
        font.setFontHeightInPoints((short) 16); // �����С

        cellStyle.setFont(font);
        cell.setCellStyle(cellStyle);
    }

    /**
     * ������ͷ
     * @param wb            Excel�ĵ�����
     * @param sheet         ���������
     * @param thead         ��ͷ����
     * @param sheetWidth    ÿһ�п��
     */
    public static void createThead(HSSFWorkbook wb,HSSFSheet sheet,String[] thead,int[] sheetWidth){
        HSSFRow row1 = sheet.createRow(1);
        row1.setHeight((short) 600);
        // ���嵥Ԫ���ʽ����ӵ�Ԫ�����ʽ������ӵ�������
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setWrapText(true);
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);  // ���ñ���ɫ
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);                // �����ұ߿�����
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);               // �����ұ߿���ɫ

        // ���õ�Ԫ������
        HSSFFont font = wb.createFont();
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        font.setFontName("����");
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);

        // ���ñ�ͷ����
        for(int i=0;i<thead.length;i++){
            HSSFCell cell1 = row1.createCell(i);
            cell1.setCellType(HSSFCell.ENCODING_UTF_16);
            cell1.setCellValue(new HSSFRichTextString(thead[i]));
            cell1.setCellStyle(cellStyle);
        }

        // ����ÿһ�п��
        for(int i=0;i<sheetWidth.length;i++){
            sheet.setColumnWidth(i, sheetWidth[i]);
        }
    }

    /**
     * ��������
     * @param wb        // Excel�ĵ�����
     * @param sheet     // ���������
     * @param result    // ������
     */
    public static void createTable(HSSFWorkbook wb,HSSFSheet sheet,List<Map<String, String>> result){
        // ���嵥Ԫ���ʽ����ӵ�Ԫ�����ʽ������ӵ�������
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setWrapText(true);

        // ��Ԫ������
        HSSFFont font = wb.createFont();
        font.setFontName("����");
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);

        // ѭ����������
        for(int i = 0; i < result.size(); i++ ){
            HSSFRow row = sheet.createRow(i+2);
            row.setHeight((short) 400); // ���ø߶�
            HSSFCell cell = null;
            int j = 0;
            for (String key : (result.get(i).keySet())) {
                cell = row.createCell(j);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(new HSSFRichTextString(result.get(i).get(key)));
                j++;
            }
        }
    }
    


    /**
     * ��������
     * @param wb        // Excel�ĵ�����
     * @param sheet     // ���������
     * @param result    // ������
     */
    public static void createTable2(HSSFWorkbook wb,HSSFSheet sheet,Map<String,List<String>> result){
        // ���嵥Ԫ���ʽ����ӵ�Ԫ�����ʽ������ӵ�������
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        cellStyle.setWrapText(true);

        // ��Ԫ������
        HSSFFont font = wb.createFont();
        font.setFontName("����");
        font.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font);

        // ѭ����������
        int i=0;
        for (Map.Entry entry : result.entrySet()) {
			List<String> ioc = (List) entry.getValue();
			String key=(String) entry.getKey();
//			System.out.println("*********" + entry.getKey() + "����*********");			
			for (String string : ioc) {
				HSSFRow row = sheet.createRow(i+2);
	            row.setHeight((short) 300); // ���ø߶�
	            HSSFCell cell = null;       
	            cell = row.createCell(1);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(new HSSFRichTextString(key));
                cell = row.createCell(2);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(new HSSFRichTextString(string));
//				System.out.println(string);
				i++;

			}
		}
        

    }
    
}