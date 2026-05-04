package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadList {

	public ArrayList<String> read(String item) throws IOException, InvalidFormatException {
		ArrayList<String> list = new ArrayList<String>();
		int listContent = 0;
		int simpleNam = 0;
		int unit=0;
		int min=0;
		int max=0;
		int literaLimite=0;
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		String targetFile = desktop + "\\avatar\\1.xlsx";
		FileInputStream fis = new FileInputStream(targetFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("Sheet1");
				

		for (int n = 0; sheet.getRow(0) != null && sheet.getRow(0).getCell(n) != null; n++) {
			if(sheet.getRow(0).getCell(n).getStringCellValue().compareTo("低限") == 0) {
				min=n;
			}
			if(sheet.getRow(0).getCell(n).getStringCellValue().compareTo("高限") == 0) {
				max=n;
			}
			if(sheet.getRow(0).getCell(n).getStringCellValue().compareTo("文本限值") == 0) {
				literaLimite=n;
			}
			

			if (sheet.getRow(0).getCell(n).getStringCellValue().equals(item)) {
				if (item.compareTo("样品名称") == 0) {
					simpleNam = n;
				}
				if(item.compareTo("报告单位") == 0) {
					unit=n;
				}
				listContent = n;
				System.out.println(n);
			}
		}
		
		String minLimit=null;
		String maxLimit=null;
		String literaLimits=null;

		int columnNumber = 0;
		for (int n = 0; sheet.getRow(n) != null && sheet.getRow(n).getCell(listContent) != null; n++) {

			columnNumber++;

		}
		for (int n = 0; sheet.getRow(n) != null; n++) {
			if (sheet.getRow(n).getCell(listContent).getCellType() != Cell.CELL_TYPE_NUMERIC
					&& sheet.getRow(n).getCell(listContent).getStringCellValue().isEmpty()) {
				sheet.getRow(n).getCell(listContent).setCellValue("/");
			}
			if (sheet.getRow(n).getCell(listContent).getCellType() == Cell.CELL_TYPE_NUMERIC) {
				sheet.getRow(n).getCell(listContent).setCellValue("num");
			}
		}

		for (int i = 1; i < columnNumber; i++) {

			if (simpleNam == listContent) {
				if (sheet.getRow(i).getCell(listContent).getStringCellValue().length() > 99) {
					list.add("名称超范围");
				} else {
					list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
				}
			} else if(unit==listContent){
				sheet.getRow(i).getCell(literaLimite).setCellType(Cell.CELL_TYPE_STRING);
				sheet.getRow(i).getCell(min).setCellType(Cell.CELL_TYPE_STRING);
				sheet.getRow(i).getCell(max).setCellType(Cell.CELL_TYPE_STRING);
				
				
				if(!sheet.getRow(i).getCell(min).getStringCellValue().isEmpty()) {
					
					minLimit=" min:"+sheet.getRow(i).getCell(min).getStringCellValue();
					//System.out.println(literaLimits);
				}else {minLimit="";}
				if(!sheet.getRow(i).getCell(max).getStringCellValue().isEmpty()) {
	
					maxLimit=" max:"+sheet.getRow(i).getCell(max).getStringCellValue();	
					//System.out.println(literaLimits);
					}else {maxLimit="";}
				if(!sheet.getRow(i).getCell(literaLimite).getStringCellValue().isEmpty()) {
					
					literaLimits=" text:"+sheet.getRow(i).getCell(literaLimite).getStringCellValue();	
					//System.out.println(literaLimits);
				}else {literaLimits="";}
				
				String limit=minLimit+maxLimit+literaLimits;
				
					list.add(sheet.getRow(i).getCell(listContent).getStringCellValue()+limit);
				
				
			} 
			else {
				list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
			}

		}

		return list;

	}

}
