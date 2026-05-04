package com.cti.Ajax.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;

import com.test.service.SampleTaskTable;

public class DoneSimpleController {
//	//@RequestMapping("/done")
//	//@ResponseBody
//	@GetMapping("/done")
//	public void getNumber(String simpleNum,Model model){
//		String number=simpleNum;
//		System.out.println(number);
//		doneMethord(number);
//			
//	}
//	@GetMapping("/done2")
//	public String getNumber2(String simpleNum,Model model){
//		String number=simpleNum;
//		System.out.println(number);
//		doneMethord(number);
//		return "testing";
//		
//		
//		
//		
//	}
public ArrayList<String> al=new ArrayList<String>();
	public void doneMethord(String num,String computerName) throws ParseException {
		String table_name = SampleTaskTable.currentYearTableName();
		String quesSql = "select * from `" + table_name + "` where `样品短号` like ? AND `位置` = ? AND `done`=0 AND DATE(`传入时间`) = CURDATE();";
		String orderSql = "select COALESCE(MAX(sample_order), 0) + 1 from (select `样品短号`, MAX(`做样顺序`) sample_order from `" + table_name + "` where `位置` = ? AND DATE(`传入时间`) = CURDATE() group by `样品短号`) t;";
		String doneSql = "update `" + table_name + "` set `done`='1', `做样顺序`=?, `做样时间`=NOW() where `样品短号` like ? AND `位置` = ? AND `done`=0 AND DATE(`传入时间`) = CURDATE();";
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		Connection selomenconn;
		

		Statement stmt;
		Statement selomenstmt;
		try {
			conn = DriverManager.getConnection(url, name, password);
			PreparedStatement pstmt = conn.prepareStatement(doneSql);
			PreparedStatement queryStmt = conn.prepareStatement(quesSql);
			PreparedStatement orderStmt = conn.prepareStatement(orderSql);
			queryStmt.setString(1, num);
			queryStmt.setString(2, computerName.trim());
			ResultSet simpleInfo = queryStmt.executeQuery();
			boolean matched = simpleInfo.next();
			
			if (matched) {
				orderStmt.setString(1, computerName.trim());
				ResultSet orderRs = orderStmt.executeQuery();
				int sampleOrder = 1;
				if (orderRs.next()) {
					sampleOrder = orderRs.getInt(1);
				}
				pstmt.setInt(1, sampleOrder);
				pstmt.setString(2, num);
				pstmt.setString(3, computerName.trim());
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
