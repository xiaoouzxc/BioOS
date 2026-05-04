package com.example.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadListSort {
	private final String[] list = { "样品短号", "样品名称", "报告抬头", "检测项目", "检测方法", "报告单位", "备注" };
	public ArrayList<String> read(String item) throws IOException, InvalidFormatException {
		ArrayList<String> list = new ArrayList<String>();
		int listContent = 0;
		int simpleNam=0;
		int simpleNum = 0;
		int simpletestItem=0;
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		String targetFile = desktop + "\\avatar\\1.xlsx";
		FileInputStream fis = new FileInputStream(targetFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("Sheet1");

		for (int n = 0; sheet.getRow(0) != null && sheet.getRow(0).getCell(n) != null; n++) {
			if (sheet.getRow(0).getCell(n).getStringCellValue().equals("检测项目")) {
				simpletestItem=n;
			}
		}
		for (int n = 0; sheet.getRow(0) != null && sheet.getRow(0).getCell(n) != null; n++) {
			
			
			if (sheet.getRow(0).getCell(n).getStringCellValue().equals(item)) {
				if (item.compareTo("样品名称") == 0) {
					simpleNam = n;
				}else if(item.compareTo("样品短号") == 0) {
					simpleNum=n;
				}
				listContent = n;
				System.out.println(n);
			}
		}

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
		

		for (int i = 1; i <columnNumber; i++) {
			String num=sheet.getRow(i).getCell(listContent).getStringCellValue();
			String testItem=sheet.getRow(i).getCell(simpletestItem).getStringCellValue();
			String nextchangeNum=null;
			
			if(i+1==columnNumber) {
					String nextnum=sheet.getRow(i).getCell(listContent).getStringCellValue();
					String nexttestItem=sheet.getRow(i).getCell(simpletestItem).getStringCellValue();
					nextchangeNum=nextnum+nexttestItem;
				}else  if(sheet.getRow(i+1) == null) {
				
					continue;
				}else{
					String nextnum=sheet.getRow(i+1).getCell(listContent).getStringCellValue();
					String nexttestItem=sheet.getRow(i+1).getCell(simpletestItem).getStringCellValue();
					nextchangeNum=nextnum+nexttestItem;
				}
			 
			
			String changeNum=num+testItem;
			
			boolean trueFivesimple=false;
			int repeatIndex=0;
			if (simpleNam == listContent) {
				if (sheet.getRow(i).getCell(listContent).getStringCellValue().length() > 99) {
					list.add("名称超范围");
				} else {
					list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
				}
			}else if(simpleNum == listContent&&changeNum.equals(nextchangeNum)&&i+1!=columnNumber) {
			                 
					
					while(changeNum.equals(nextchangeNum)) {
						repeatIndex++;
						 // 检查是否越界或下一行为空
				        if (i + 1 + repeatIndex >= columnNumber || sheet.getRow(i + 1 + repeatIndex) == null) {
				            break;
				        }
						
						
						nextchangeNum=sheet.getRow(i+1+repeatIndex).getCell(listContent).getStringCellValue()+sheet.getRow(i+1+repeatIndex).getCell(simpletestItem).getStringCellValue();
					}
				
					repeatIndex=repeatIndex+1;
					
				
				for(int g=0;g<repeatIndex;g++) {
					XSSFRow currentRow = sheet.getRow(i + g);
					if (currentRow != null && currentRow.getCell(listContent) != null) {
			            list.add(currentRow.getCell(listContent).getStringCellValue() + "." + (g + 1));
			        }
			    }
				
				i=i+repeatIndex-1;
				//System.out.println(i+"--"+sheet.getRow(i).getCell(simpleNam).getStringCellValue());
				
				
				
					
					
			} else {
				
					list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
				
				
			}

		}

		return list;

	}

}
