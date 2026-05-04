package com.test.record;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.demo.GetSimpleList;
import com.example.demo.Simple;

public class ExportRecordData {
	GetSimpleList list = new GetSimpleList();
	public void write(String table_name) throws IOException {
		ArrayList<Simple> writeList = list.getSZList(table_name);
		

		 String resultData=null;
		 String[] s=null;
			FileSystemView fsv = FileSystemView.getFileSystemView();
			//String desktop = fsv.getHomeDirectory().getPath();
			String tableName = table_name.replace("/", "-");
			String filePath =  "./result";
			String fileName=tableName+".xlsx";
			File folder = new File(filePath);
			File savefile = null;
			if (!folder.exists()) {
				folder.mkdir();
				savefile = new File(filePath,fileName);
			} else {
			savefile = new File(filePath,fileName);
			}

			OutputStream outputStream = new FileOutputStream(savefile);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Sheet1");
			CellStyle style = workbook.createCellStyle();
						
			int totalRow=writeList.size()*2;

			for (int l = 0; l < totalRow; l++) {
				
				sheet.createRow(l);

			}
			
				int rowIndex=0;
			for (int l = 0; l < writeList.size(); l++) {
				resultData=writeList.get(l).getResult();
				 s=resultData.split("-");
				
				for(int line=rowIndex;line<totalRow;line++) {
					
//						sheet.getRow(l+1).createCell(0).setCellValue(writeList.get(l).getNumber());					
//						
//						sheet.getRow(l+1).createCell(1).setCellValue(writeList.get(l).getTestItem());
//						
//						sheet.getRow(l+1).createCell(2).setCellValue(writeList.get(l).getTestMethod());
						//System.out.println(String.valueOf(s[1].charAt(0)));
						if(line%2==0) {
                            sheet.getRow(line).createCell(1).setCellValue(writeList.get(l).getTestItem());
							
							sheet.getRow(line).createCell(2).setCellValue(writeList.get(l).getTestMethod());
							
							
							sheet.getRow(line).createCell(3).setCellValue(parttn(String.valueOf(s[1].charAt(0)))[0]);
							sheet.getRow(line).createCell(4).setCellValue(":");
							    sheet.getRow(line).createCell(5).setCellValue(parttn(String.valueOf(s[1].charAt(0)))[1]);
							 sheet.getRow(line).createCell(6).setCellValue(parttn(String.valueOf(s[2].charAt(0)))[0]);
							sheet.getRow(line).createCell(7).setCellValue(":");
						    sheet.getRow(line).createCell(8).setCellValue(parttn(String.valueOf(s[2].charAt(0)))[1]);
						    sheet.getRow(line).createCell(9).setCellValue(parttn(String.valueOf(s[3].charAt(0)))[0]);
							sheet.getRow(line).createCell(10).setCellValue(":");
							    sheet.getRow(line).createCell(11).setCellValue(parttn(String.valueOf(s[3].charAt(0)))[1]);
						}else if(line%2!=0) {
							sheet.getRow(line).createCell(0).setCellValue(writeList.get(l).getNumber());					
							
							sheet.getRow(line).createCell(1).setCellValue(writeList.get(l).getTestItem());
							
							sheet.getRow(line).createCell(2).setCellValue(writeList.get(l).getTestMethod());
							
							sheet.getRow(line).createCell(3).setCellValue(parttnResult(s[1])[0]);
							sheet.getRow(line).createCell(4).setCellValue(",");
							    sheet.getRow(line).createCell(5).setCellValue(parttnResult(s[1])[1]);
							 sheet.getRow(line).createCell(6).setCellValue(parttnResult(s[2])[0]);
							sheet.getRow(line).createCell(7).setCellValue(",");
						    sheet.getRow(line).createCell(8).setCellValue(parttnResult(s[2])[1]);
						    sheet.getRow(line).createCell(9).setCellValue(parttnResult(s[3])[0]);
							sheet.getRow(line).createCell(10).setCellValue(",");
							    sheet.getRow(line).createCell(11).setCellValue(parttnResult(s[3])[1]);
							rowIndex=line+1;
							line=rowIndex;
							break;
						}
					
						
						
					
				}

				
				
//				
//				sheet.getRow(l).createCell(6).setCellValue();
//				sheet.getRow(l).createCell(7).setCellValue();

				
			}
			

			sheet.setDefaultRowHeight((short) (255.86 * 1.50 + 184.27));
//			sheet.setColumnWidth(0, (int) (255.86 * 11.75 + 184.27));
//			sheet.setColumnWidth(1, (int) (255.86 * 11.75 + 184.27));
//			sheet.setColumnWidth(2, (int) (255.86 * 8.50 + 184.27));
//			sheet.setColumnWidth(3, (int) (255.86 * 28.50 + 184.27));
//			sheet.setColumnWidth(4, (int) (255.86 * 10.50 + 184.27));

			workbook.setActiveSheet(0);
			workbook.write(outputStream);
			outputStream.close();
			
		
	}
	private String[] parttn(String s) {
	
		String[] dilution={"1:1","1:10","1:100","1:1000","1:10000","1:100000","1:1000000","1:10000000","1:100000000","1:1000000000","1:1000000000"};
		for(int i=0;i<=dilution.length;i++) {
			if(Integer.valueOf(s)==i) {
				return dilution[i].split(":");
			}
		}
		return null;
		
		
	}
	private String[] parttnResult(String s) {
		
		String result=s.substring(2, s.length()-1);
		String[] requary=result.split(",");
		//System.out.println(s);
		return requary;
		
	}

}
