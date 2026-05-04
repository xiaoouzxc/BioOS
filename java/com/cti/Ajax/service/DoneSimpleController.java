package com.cti.Ajax.service;

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

import com.test.service.SelmenQuery;

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
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String sql = "select * from `" + table_name + "`;";
		String quesSql = "select * from `" + table_name + "`where `样品短号` like '" + num + "';";
		String doneSql = "update `" + table_name + "` set `done`='1' where `样品短号` like '" + num + "';";
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
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(quesSql);
			while (simpleInfo.next()) {
				if(simpleInfo.getString("检测项目").contains("沙")&&simpleInfo.getInt("done")!=1) {
					if(simpleInfo.getString("复测")!=null) {
						SelmenQuery sq=new SelmenQuery();
						sq.create(num,computerName,"F");
					}else {
						SelmenQuery sq=new SelmenQuery();
						sq.create(num,computerName,"/");
					}
					
				}
			}
			
			SelmenQuery.delete(computerName);
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
