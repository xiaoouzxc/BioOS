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
		int simpleNum = 0;
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		String targetFile = desktop + "\\avatar\\1.xlsx";
		FileInputStream fis = new FileInputStream(targetFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheet("Sheet1");

		for (int n = 0; sheet.getRow(0) != null && sheet.getRow(0).getCell(n) != null; n++) {

			if (sheet.getRow(0).getCell(n).getStringCellValue().equals(item)) {
				if (item.compareTo("样品名称") == 0) {
					simpleNum = n;
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

		for (int i = 1; i < columnNumber; i++) {

			if (simpleNum == listContent) {
				if (sheet.getRow(i).getCell(listContent).getStringCellValue().length() > 99) {
					list.add("名称超范围");
				} else {
					list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
				}
			} else {
				list.add(sheet.getRow(i).getCell(listContent).getStringCellValue());
			}

		}

		return list;

	}

}
