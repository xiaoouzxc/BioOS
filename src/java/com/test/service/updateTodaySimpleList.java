package com.test.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.filechooser.FileSystemView;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.GetSimpleList;
import com.example.demo.ReadList;
import com.example.demo.ReadListSort;
import com.example.demo.Simple;
import com.example.demo.getDBtestName;

@Service
public class updateTodaySimpleList {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//	private Date todaydate = new Date();
//	private String year = sdf.format(todaydate).substring(0, 4);
//	private long monthagotime = todaydate.getTime() - 1209600000L;
//	private Date monthagodate = new Date(monthagotime);
//	private String monthagotable_name = sdf.format(monthagodate);
	private final String[] list = { "样品短号", "样品名称", "报告抬头", "检测项目", "检测方法", "报告单位", "备注" };

	public void index() {
		Date todaydate = new Date();
		
		String year = sdf.format(todaydate).substring(0, 4);
		long monthagotime = todaydate.getTime() - 1209600000L;
		Date monthagodate = new Date(monthagotime);

		String table_name = sdf.format(new Date());

		try {
			String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
			String name = "root";
			String password = "1234";
			Connection conn = DriverManager.getConnection(url, name, password);

			Statement stmt = conn.createStatement();

			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (tables.next()) {
				try {
					insertData(conn, table_name);
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				String sql = "CREATE TABLE `test`.`" + table_name + "` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
						+ "  `检测方法` TEXT NULL,\r\n" + "  `报告单位` VARCHAR(100) NULL,\r\n"
						+ "  `备注` TEXT NULL,\r\n" + "  `done` int NOT NULL DEFAULT 0,\r\n"
						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n"
						+ "  `位置` VARCHAR(20) NULL,\r\n" + "  `顺序` int NOT NULL DEFAULT 0,\r\n"
						+ "  PRIMARY KEY (`id`));";
				stmt.execute(sql);

				try {
					insertDataforFirst(conn, table_name);
				} catch (InvalidFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stmt.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//String sql = "SELECT name FROM new_table WHERE id = ?";     
//String sql2 ="CREATE TABLE `test`.`new_table2` (\r\n"
//		+ "  `id` INT NOT NULL,\r\n"
//		+ "  `name` VARCHAR(45) NULL,\r\n"
//		+ "  PRIMARY KEY (`id`));";
// // 通过jdbcTemplate查询数据库        
//@SuppressWarnings("deprecation")
//String name = (String)jdbcTemplate.queryForObject(sql, new Object[] { 1 }, String.class);  
//jdbcTemplate.execute(sql2);
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		String targetFile = desktop + "\\avatar\\1.xlsx";
		File file = new File(targetFile);
		if (file.exists()) {
			file.delete();
		}
		SimpleDateFormat uninDatetool = new SimpleDateFormat("yyyy-MM-dd");
		getDBtestName gtablename = new getDBtestName();
		ArrayList<String> tablenamelist = gtablename.getTestName();
		for (String name : tablenamelist) {

			try {
				String tablename = name.replace("/", "-");
				Date date = uninDatetool.parse(tablename);

				if (date.before(monthagodate)) {

					write(name);
					uninTableMethord(name,year);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {

				e.printStackTrace();
			}

		}

	}

	private void insertData(Connection conn, String table_name)
			throws SQLException, InvalidFormatException, IOException {
		String insertSql = "insert  into `" + table_name
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`顺序`) values(?,?,?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id`,`顺序` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `"
				+ table_name + "`);";
		String findMAXarrSql = "SELECT `顺序` FROM `" + table_name + "` WHERE `顺序` != 0;";
//	   String selectdoublictSql="CREATE TEMPORARY TABLE tmp_table AS SELECT `样品短号`,`检测项目` FROM "+table_name+ " GROUP BY `样品短号`,`检测项目` HAVING COUNT(*) > 1; ";
//	   String removedoublictSql= "DELETE FROM "+ table_name+ " WHERE `样品短号`,`检测项目` IN ( SELECT `样品短号`,`检测项目` FROM tmp_table );";
//    		   
		int maxID = 0;
		int arr = 0;
		Statement simpleInfostm1 = conn.createStatement();

		ResultSet simpleInfo = simpleInfostm1.executeQuery(maxIDquery);

		while (simpleInfo.next()) {
			maxID = simpleInfo.getInt("id");
			arr = simpleInfo.getInt("顺序");
		}

//	   ResultSet  lastSqlarr=stm.executeQuery(lastSql);
//	   while(lastSqlarr.next()) {
//		   arr=lastSqlarr.getInt("顺序");			
//		   }

		PreparedStatement pstmt = conn.prepareStatement(insertSql);

		ReadList rl = new ReadList();
		ArrayList<String> List1 = new ArrayList<String>();
		ArrayList<String> List2 = new ArrayList<String>();
		ArrayList<String> List3 = new ArrayList<String>();
		ArrayList<String> List4 = new ArrayList<String>();
		ArrayList<String> List5 = new ArrayList<String>();
		ArrayList<String> List6 = new ArrayList<String>();
		ArrayList<String> List7 = new ArrayList<String>();
		List1 = rl.read(list[0]);
		List2 = rl.read(list[1]);
		List3 = rl.read(list[2]);
		List4 = rl.read(list[3]);
		List5 = rl.read(list[4]);
		List6 = rl.read(list[5]);
		List7 = rl.read(list[6]);
		System.out.println(List1.size()+"-"+List2.size()+"-"+List3.size()+"-"+List4.size()+"-"+List5.size()+"-"+List6.size()+"-"+List7.size()+"-");

		// int maxID=Integer.parseInt(jdbcTemplate.queryForObject(maxIDquery,
		// String.class));
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection findLastArrconn = DriverManager.getConnection(url, name, password);
		Statement findLastArrconnstm = findLastArrconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
		if (arr == 0) {
			ResultSet simpleInfoNOF = findLastArrconnstm.executeQuery(findMAXarrSql);

			while (simpleInfoNOF.next()) {

				arr = simpleInfoNOF.getInt("顺序");
			}
			arr++;
		} else {
			arr++;
		}

		for (int i = 0; i <List1.size(); i++) {
			pstmt.setInt(1, maxID + (i + 1));
			pstmt.setString(2, List1.get(i));
			pstmt.setString(3, List2.get(i));
			pstmt.setString(4, List3.get(i));
			pstmt.setString(5, List4.get(i));
			pstmt.setString(6, List5.get(i));
			pstmt.setString(7, List6.get(i));
			pstmt.setString(8, List7.get(i));
			pstmt.setInt(9, arr);

			pstmt.executeUpdate();

		}
		;
		// stm.execute(selectdoublictSql);
		// stm.execute(removedoublictSql);
		pstmt.close();

	}

	private void insertDataforFirst(Connection conn, String table_name)
			throws SQLException, InvalidFormatException, IOException {
		String insertSql = "insert into `" + table_name
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`顺序`) values(?,?,?,?,?,?,?,?,?);";

		PreparedStatement pstmt = conn.prepareStatement(insertSql);
		ReadList rl = new ReadList();
		ArrayList<String> List1 = new ArrayList<String>();
		ArrayList<String> List2 = new ArrayList<String>();
		ArrayList<String> List3 = new ArrayList<String>();
		ArrayList<String> List4 = new ArrayList<String>();
		ArrayList<String> List5 = new ArrayList<String>();
		ArrayList<String> List6 = new ArrayList<String>();
		ArrayList<String> List7 = new ArrayList<String>();
		List1 = rl.read(list[0]);
		List2 = rl.read(list[1]);
		List3 = rl.read(list[2]);
		List4 = rl.read(list[3]);
		List5 = rl.read(list[4]);
		List6 = rl.read(list[5]);
		List7 = rl.read(list[6]);

		for (int i = 0; i < List1.size(); i++) {
			pstmt.setInt(1, i + 1);
			pstmt.setString(2, List1.get(i));
			pstmt.setString(3, List2.get(i));
			pstmt.setString(4, List3.get(i));
			pstmt.setString(5, List4.get(i));
			pstmt.setString(6, List5.get(i));
			pstmt.setString(7, List6.get(i));
			pstmt.setString(8, List7.get(i));
			pstmt.setString(9, "1");
			pstmt.executeUpdate();

		}
		;

		pstmt.close();

	}

	private String uninTableMethord(String monthagoTables,String year) {
		
		
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		String year_table = "t" + year;
//		String sql = "insert into " + year_table
//				+ "(`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`复测`,`结果`) select `样品短号`,`样品名称`,`报告抬头`,`检测项目`,`复测`,`结果` from `"
//				+ monthagoTables + "`where `检测项目` not like '沙门%';";
		String sql = "insert into " + year_table
				+ "(`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`复测`,`结果`) select `样品短号`,`样品名称`,`报告抬头`,`检测项目`,`复测`,`结果` from `"
				+ monthagoTables + "`;";
		String deletesql = "drop table `" + monthagoTables + "`;";
		try {
			Connection conn = DriverManager.getConnection(url, name, password);
			ResultSet tables = conn.getMetaData().getTables(null, null, year_table, null);
			ResultSet deletetables = conn.getMetaData().getTables(null, null, monthagoTables, null);
			if (tables.next() && deletetables.next()) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				PreparedStatement deletepstmt = conn.prepareStatement(deletesql);
				pstmt.execute();
				System.out.println(year_table+"--"+monthagoTables);
				deletepstmt.execute();
				return "-----------已合并";
			} else if (tables.next() == false && deletetables.next()) {
				Statement stmt = conn.createStatement();
				String creatsql = "CREATE TABLE `test`.`" + year_table + "` (\r\n" + "  `id` int auto_increment,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n" + "  PRIMARY KEY (`id`));";
				stmt.execute(creatsql);
				PreparedStatement pstmt = conn.prepareStatement(sql);
				PreparedStatement deletepstmt = conn.prepareStatement(deletesql);
				pstmt.execute();
				deletepstmt.execute();
				return "-----------已合并";
			} else {

				return "-----------需合并当天没有单子";
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-----------已合并";
	}

	public void write(String monthagoTables) throws IOException {
		GetSimpleList list = new GetSimpleList();
		String log = null;
		ArrayList<Simple> writeList = list.getList(monthagoTables);

		if (writeList == null) {
			log = "-----------complete";
			System.out.println(log);
		} else {
			FileSystemView fsv = FileSystemView.getFileSystemView();
			String desktop = fsv.getHomeDirectory().getPath();
			String tableName = monthagoTables.replace("/", "-");
			String filePath = desktop + "/数据备份/" + tableName + ".xlsx";
			File folder = new File(desktop + "/数据备份");
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
				sheet.getRow(l).createCell(2).setCellValue(writeList.get(l).getTestItem());
				sheet.getRow(l).getCell(2).setCellStyle(style);
				sheet.getRow(l).createCell(3).setCellValue(writeList.get(l).getTestMethod());
				sheet.getRow(l).getCell(3).setCellStyle(style);
				sheet.getRow(l).createCell(4).setCellValue(writeList.get(l).getResult());

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

	}
}
