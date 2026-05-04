package com.cti.Ajax.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.ui.Model;

public class FindFsimpleOperation {

	private Connection getConnection() throws SQLException {

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	public void insertLoc(String simpleNum, String simpleName, String loccon) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		System.out.println(simpleNum + "," + simpleName + "," + loccon);
		String sql = "update `" + table_name + "` set `位置`='" + loccon + "' where `样品短号` like '" + simpleNum
				+ "%' AND `检测项目` like '" + simpleName + "%';";
		Connection conn;
		try {
			conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkFinded(String simpleNum, String simpleName, Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());

		String number = simpleNum;

		if (isFsimple(simpleNum, simpleName, table_name)) {
			deleteMethord(number, simpleName, table_name);
		} else {
			setMethord(number, simpleName, table_name);
		}
	}

	private void deleteMethord(String num, String simpleName, String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String deleteFSql = "update `" + table_name + "` set `复测`='BF' where `样品短号` like '" + num
				+ "%' AND `检测项目` like '" + simpleName + "%';";

		Statement stmt;
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(deleteFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setMethord(String num, String simpleName, String table_name) {

		String sql = "select * from `" + table_name + "`;";
		String setFSql = "update `" + table_name + "` set `复测`='RBF' where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";

		Statement stmt;
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(setFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean isFsimple(String simpleNum, String simpleName, String table_name) {
		String sql = "select `复测` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%' AND `检测项目` like '"
				+ simpleName + "%';";
		Connection conn;
		String isF = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				isF = simpleInfo.getString("复测");
			}
			if (isF.compareTo("RBF") == 0) {
				return true;
			}
			simpleInfo.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
