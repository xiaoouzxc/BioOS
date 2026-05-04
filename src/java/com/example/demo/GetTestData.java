package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Simple;

@Controller
@RestController
public class GetTestData {
	final String table_name = "selomenResult";
	final String Ftable_name = "selomenFResult";
	private ArrayList<String> tableNameList = new ArrayList<String>();

	//2025.2.6旧版作废
	@GetMapping("/getData")
	
	public String result(String date, Model model) {
		tableNameList = new getDBtestName().getTestName();
		model.addAttribute("nameList", tableNameList);
		String table_name = date;
		if (table_name == null) {
			return "2025.2.6旧版作废";
		} else {
			String sql = "select * from `" + table_name + "`;";
			List<Simple> simpleList = new ArrayList<Simple>();
			List<Simple> SMsimpleList = new ArrayList<Simple>();
			List<Simple> FsimpleList = new ArrayList<Simple>();
			List<Simple> BFsimpleList = new ArrayList<Simple>();
			String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
			String name = "root";
			String password = "1234";
			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					System.out.println(table_name.toString() + "null");
					return "2025.2.6旧版作废";
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {
					if (simpleInfo.getString("复测") == null) {
						if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
								&& !simpleInfo.getString("检测项目").contains("致泻")
								&& !simpleInfo.getString("检测项目").contains("O157")
								&& !simpleInfo.getString("检测项目").contains("商业无菌")
								&& !simpleInfo.getString("检测项目").contains("微生物")
								&& !simpleInfo.getString("检测项目").contains("副溶血")) {
							if (simpleInfo.getString("检测项目").contains("霉")
									|| simpleInfo.getString("检测项目").contains("酵母")) {
								SMsimpleList.add(new Simple(simpleInfo.getInt("id"),
										simpleInfo.getString("样品短号"), 
										simpleInfo.getString("样品名称"),
										simpleInfo.getString("报告抬头"),
										simpleInfo.getString("检测项目"), 
										simpleInfo.getString("报告单位"),
										simpleInfo.getString("检测方法"),										
										simpleInfo.getInt("done"),
										simpleInfo.getString("复测"), 
										simpleInfo.getString("结果")
										
										));
							} else {
								simpleList.add(new Simple(simpleInfo.getInt("id"),
										simpleInfo.getString("样品短号"), 
										simpleInfo.getString("样品名称"),
										simpleInfo.getString("报告抬头"),
										simpleInfo.getString("检测项目"), 
										simpleInfo.getString("报告单位"),
										simpleInfo.getString("检测方法"),										
										simpleInfo.getInt("done"),
										simpleInfo.getString("复测"), 
										simpleInfo.getString("结果")
										
										));
							}
						} else if (simpleInfo.getString("检测项目").contains("微生物")) {
							if (simpleInfo.getString("检测项目").contains("霉")
									|| simpleInfo.getString("检测项目").contains("酵母")) {
								SMsimpleList.add(new Simple(simpleInfo.getInt("id"),
										simpleInfo.getString("样品短号"), 
										simpleInfo.getString("样品名称"),
										simpleInfo.getString("报告抬头"),
										simpleInfo.getString("检测项目"), 
										simpleInfo.getString("报告单位"),
										simpleInfo.getString("检测方法"),										
										simpleInfo.getInt("done"),
										simpleInfo.getString("复测"), 
										simpleInfo.getString("结果")
										
										));

							}
						}
					} else if (simpleInfo.getString("复测").contains("BF")
							&& !simpleInfo.getString("检测项目").contains("沙")) {
						BFsimpleList.add(new Simple(simpleInfo.getInt("id"),
								simpleInfo.getString("样品短号"), 
								simpleInfo.getString("样品名称"),
								simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), 
								simpleInfo.getString("报告单位"),
								simpleInfo.getString("检测方法"),										
								simpleInfo.getInt("done"),
								simpleInfo.getString("复测"), 
								simpleInfo.getString("结果")
								
								));
					} else {
						if (!simpleInfo.getString("检测项目").contains("沙")) {
							FsimpleList.add(new Simple(simpleInfo.getInt("id"),
									simpleInfo.getString("样品短号"), 
									simpleInfo.getString("样品名称"),
									simpleInfo.getString("报告抬头"),
									simpleInfo.getString("检测项目"), 
									simpleInfo.getString("报告单位"),
									simpleInfo.getString("检测方法"),										
									simpleInfo.getInt("done"),
									simpleInfo.getString("复测"), 
									simpleInfo.getString("结果")
									
									));
						}
					}
				}
				for (Simple s : SMsimpleList) {
					simpleList.add(s);
				}
				for (Simple s : FsimpleList) {
					simpleList.add(s);
				}
				for (Simple s : BFsimpleList) {
					simpleList.add(s);
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			tableNameList = new getDBtestName().getTestName();
			return "2025.2.6旧版作废";
		}
	}

	@GetMapping("/getselomenData")
	public String selomenResult(Model model) {
		String table_name = "selomenResult";
		Date todaydate = new Date();
		long sevenagotime = todaydate.getTime() - 1209600000;
		Date sevenagodate = new Date(sevenagotime);
		String sql = "select * from `" + table_name + "`;";
		List<Simple> simpleList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return "noListError.html";
			}

			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getDate("日期").after(sevenagodate)) {

					simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));

				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);
		model.addAttribute("tablename", table_name);

		return "getTestData/getSelomenData";

	}

	@GetMapping("/getselomenFData")
	public String selomenFResult(Model model) {

		Date todaydate = new Date();
		long sevenagotime = todaydate.getTime() - 1209600000;
		Date sevenagodate = new Date(sevenagotime);
		String sql = "select * from `" + Ftable_name + "`;";
		List<Simple> simpleList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, Ftable_name, null);
			if (!tables.next()) {
				return "noListError.html";
			}

			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getDate("日期").after(sevenagodate)) {

					simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));

				}
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);
		model.addAttribute("tablename", Ftable_name);

		return "getTestData/getSelomenData";

	}

	@GetMapping("/writeselomenData")
	public String write() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		Date todaydate = new Date();
		GetSimpleList list = new GetSimpleList();
		String log = null;
		ArrayList<Simple> writeList = list.getSelmonList();

		if (writeList == null) {
			log = "-----------complete";
			System.out.println(log);
		} else {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			String desktop = fsv.getHomeDirectory().getPath();
			String tableName = sdf.format(todaydate).replace("/", "-");
			String filePath = desktop + "/沙门数据备份/" + tableName + ".xlsx";
			File folder = new File(desktop + "/沙门数据备份");
			File savefile = null;
			if (!folder.exists()) {
				folder.mkdir();
				savefile = new File(filePath);
			} else {
				savefile = new File(filePath);
			}

			OutputStream outputStream = new FileOutputStream(savefile);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Sheet1");
			CellStyle style = workbook.createCellStyle();
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			// style.setAlignment((short)0.1);
			// style.setVerticalAlignment((short)0.1);
			style.setWrapText(true);

			for (int l = 0; l < writeList.size(); l++) {
				// System.out.println(list.indexOf(index));

				// System.out.println("leak!!!"+list.size());
				sheet.createRow(l);

			}

			for (int l = 0; l < writeList.size(); l++) {

				sheet.getRow(l).createCell(0).setCellValue(writeList.get(l).getNumber());
				sheet.getRow(l).getCell(0).setCellStyle(style);
				sheet.getRow(l).createCell(1).setCellValue(writeList.get(l).getName());
				sheet.getRow(l).getCell(1).setCellStyle(style);
				sheet.getRow(l).createCell(2).setCellValue(writeList.get(l).getIfF());
				sheet.getRow(l).getCell(2).setCellStyle(style);
				sheet.getRow(l).createCell(3).setCellValue(formatSelresult(writeList.get(l).getSelomenResult()));
				// formatSelresult(writeList.get(l).getResult())
				sheet.getRow(l).getCell(3).setCellStyle(style);
				sheet.getRow(l).createCell(4).setCellValue(writeList.get(l).getDate());

				sheet.getRow(l).getCell(4).setCellStyle(style);

			}

			sheet.setDefaultRowHeight((short) (255.86 * 1.50 + 184.27));
			sheet.setColumnWidth(0, (int) (255.86 * 11.75 + 184.27));
			sheet.setColumnWidth(1, (int) (255.86 * 11.75 + 184.27));
			sheet.setColumnWidth(2, (int) (255.86 * 8.50 + 184.27));
			sheet.setColumnWidth(3, (int) (255.86 * 28.50 + 184.27));
			sheet.setColumnWidth(4, (int) (255.86 * 10.50 + 184.27));

			workbook.setActiveSheet(0);
			workbook.write(outputStream);
			outputStream.close();
			log = "-----------文件已生成";
			// System.out.println(log);
		}
		return "getTestData/writeSelomenReturn.html";

	}

	@GetMapping("/writeselomenFData")
	public String writeF() throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		Date todaydate = new Date();
		GetSimpleList list = new GetSimpleList();
		String log = null;
		ArrayList<Simple> writeList = list.getSelmonFList();

		if (writeList == null) {
			log = "-----------complete";
			System.out.println(log);
		} else {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			String desktop = fsv.getHomeDirectory().getPath();
			String tableName = sdf.format(todaydate).replace("/", "-");
			String filePath = desktop + "/沙门数据备份/" + tableName + "复测.xlsx";
			File folder = new File(desktop + "/沙门数据备份");
			File savefile = null;
			if (!folder.exists()) {
				folder.mkdir();
				savefile = new File(filePath);
			} else {
				savefile = new File(filePath);
			}

			OutputStream outputStream = new FileOutputStream(savefile);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("Sheet1");
			CellStyle style = workbook.createCellStyle();
			style.setBorderTop(BorderStyle.THIN);
			style.setBorderBottom(BorderStyle.THIN);
			style.setBorderLeft(BorderStyle.THIN);
			style.setBorderRight(BorderStyle.THIN);
			// style.setAlignment((short)0.1);
			// style.setVerticalAlignment((short)0.1);
			style.setWrapText(true);

			for (int l = 0; l < writeList.size(); l++) {
				// System.out.println(list.indexOf(index));

				// System.out.println("leak!!!"+list.size());
				sheet.createRow(l);

			}

			for (int l = 0; l < writeList.size(); l++) {

				sheet.getRow(l).createCell(0).setCellValue(writeList.get(l).getNumber());
				sheet.getRow(l).getCell(0).setCellStyle(style);
				sheet.getRow(l).createCell(1).setCellValue(writeList.get(l).getName());
				sheet.getRow(l).getCell(1).setCellStyle(style);
				sheet.getRow(l).createCell(2).setCellValue(writeList.get(l).getIfF());
				sheet.getRow(l).getCell(2).setCellStyle(style);
				sheet.getRow(l).createCell(3).setCellValue(formatSelresult(writeList.get(l).getSelomenResult()));
				// formatSelresult(writeList.get(l).getResult())
				sheet.getRow(l).getCell(3).setCellStyle(style);
				sheet.getRow(l).createCell(4).setCellValue(writeList.get(l).getDate());

				sheet.getRow(l).getCell(4).setCellStyle(style);

			}

			sheet.setDefaultRowHeight((short) (255.86 * 1.50 + 184.27));
			sheet.setColumnWidth(0, (int) (255.86 * 11.75 + 184.27));
			sheet.setColumnWidth(1, (int) (255.86 * 11.75 + 184.27));
			sheet.setColumnWidth(2, (int) (255.86 * 8.50 + 184.27));
			sheet.setColumnWidth(3, (int) (255.86 * 28.50 + 184.27));
			sheet.setColumnWidth(4, (int) (255.86 * 10.50 + 184.27));

			workbook.setActiveSheet(0);
			workbook.write(outputStream);
			outputStream.close();
			log = "-----------文件已生成";
			// System.out.println(log);
		}
		return "getTestData/writeSelomenReturn.html";

	}

	private String formatSelresult(String result) {
		String[] format = { "DHL:", "XLD:", "TSI:", "BUN:", "O:", "H:", "LYS:", "KCN:", "靛基质:", "BUN:", "甘露醇:", "山梨醇:",
				"ONPG:", "结果:" };
		StringBuilder sbuilder = new StringBuilder();

		for (int i = 0; i < result.length(); i++) {
			if ((i == 0 || i == 1) && result.charAt(i) == 'a') {
				sbuilder.append(format[i] + "RV,SC ");
			} else if ((i == 0 || i == 1) && result.charAt(i) == 'b') {
				sbuilder.append(format[i] + "RV ");
			} else if ((i == 0 || i == 1) && result.charAt(i) == 'c') {
				sbuilder.append(format[i] + "SC ");
			} else if (i == 2 && result.charAt(i) == 'a') {
				sbuilder.append(format[i] + "K,A,H2S,GAS ");
			} else if (i == 2 && result.charAt(i) == 'b') {
				sbuilder.append(format[i] + "K,H2S,GAS ");
			} else if (i == 2 && result.charAt(i) == 'c') {
				sbuilder.append(format[i] + "A,H2S,GAS ");
			} else {
				sbuilder.append(format[i] + result.charAt(i));
			}

		}

		
		return sbuilder.toString();

	}

}
