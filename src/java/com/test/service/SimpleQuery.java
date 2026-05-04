package com.test.service;

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

import com.example.demo.getDBtestName;

public class SimpleQuery {
	private static String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static String name = "root";
	private static String password = "1234";
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	private static SimpleDateFormat uninDatetool = new SimpleDateFormat("yyyy-MM-dd");
	
	public void create(String num,String computerName,String F) throws SQLException {
		ArrayList<String> al=new ArrayList<String>();
		Connection selomenconn= DriverManager.getConnection(url, name, password);
		Statement stmt = selomenconn.createStatement();
		
		String table_name = computerName+sdf.format(new Date());
		
		//ResultSet tables = selomenconn.getMetaData().getTables(null, null, table_name, null);
		getDBtestName getTablesName=new getDBtestName();
		

		
		for(String s:getTablesName.getTestSelomenLableName()) {
			
				al.add(s);
			
		}
		
		if (al.contains(table_name)) {
			insert(table_name,num,F,selomenconn);
		}else {
			String sql = "CREATE TABLE `test`.`" + table_name + "` (\r\n" + "  `id` int NOT NULL,\r\n"
					+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" 					
					+ "  `复测` VARCHAR(20) NULL,\r\n" +  "  PRIMARY KEY (`id`));";
			stmt.execute(sql);
			insert(table_name,num,F,selomenconn);
		}
		//String sql = "select * from `" + table_name + "`;";
		
	}
	
	private void insert(String table_name,String num,String F,Connection selomenconn) throws SQLException {
		String maxIDquery = "SELECT `id`,`样品短号`,`复测` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `"
				+ table_name + "`);";
		String insertSql = "insert  into `" + table_name
				+ "`(`id`,`样品短号`,`复测`) values(?,?,?);";
		PreparedStatement pstmt = selomenconn.prepareStatement(insertSql);
		int maxID = 0;
		
		Statement simpleInfostm1 = selomenconn.createStatement();

		ResultSet simpleInfo = simpleInfostm1.executeQuery(maxIDquery);
		boolean duplicateNum=false;

		while (simpleInfo.next()) {
			
			maxID = simpleInfo.getInt("id");
			String number=simpleInfo.getString("样品短号");
			if(number.compareTo(num)==0) {
				duplicateNum=true;
			}
			//System.out.println(maxID);
		}
		if(duplicateNum==false) {
			pstmt.setInt(1, maxID  + 1);
			pstmt.setString(2, num);
			pstmt.setString(3, F);
			
			pstmt.executeUpdate();
		}
		
	}
	
	public static void delete(String computerName) throws SQLException, ParseException {
		Date todaydate = new Date();
		long threeagotime = todaydate.getTime() - 259200000L;
		Date threeagodate = new Date(threeagotime);
		Connection selomenconn= DriverManager.getConnection(url, name, password);
		Statement stmt = selomenconn.createStatement();
		getDBtestName gtablename = new getDBtestName();
		ArrayList<String> tablenamelist = gtablename.getTestSelomenLableName();
		for (String name : tablenamelist) {

			System.out.println(name);
				
				String[] tablename = name.split(" ");
				
				String dateofstring=tablename[1].replace("/", "-");
				
				Date date =uninDatetool.parse(dateofstring);

				if (date.before(threeagodate)) {
					
				
					String sql = "drop TABLE `test`.`" + name+"`";
					stmt.execute(sql);

		}
				}
		}

}
