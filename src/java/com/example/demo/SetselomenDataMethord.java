package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SetselomenDataMethord {
	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	public void setseldataofdhl(String simpleNum, String dhl, String table_name) {
		Connection conn;
		String Finfo = null;

		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			StringBuffer sb = new StringBuffer(Finfo);

			sb.replace(0, 1, dhl);
			String setinfoSql = "update `" + table_name + "` set `结果`='" + sb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			sb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setseldataofxld(String simpleNum, String xld, String table_name) {
		Connection conn;
		String Finfo = null;

		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			StringBuffer sb = new StringBuffer(Finfo);

			sb.replace(1, 2, xld);
			String setinfoSql = "update `" + table_name + "` set `结果`='" + sb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			sb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setseldataoftsiandGbun(String simpleNum, String tsi, String gbun, String table_name) {
		Connection conn;
		String Finfo = null;

		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			StringBuffer sb = new StringBuffer(Finfo);

			sb.replace(2, 3, tsi);
			sb.replace(3, 4, gbun);
			String setinfoSql = "update `" + table_name + "` set `结果`='" + sb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			sb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setseldataofOH(String simpleNum, String O, String H, String table_name) {
		Connection conn;
		String Finfo = null;

		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			StringBuffer sb = new StringBuffer(Finfo);
			sb.replace(4, 5, O);
			sb.replace(5, 6, H);
			String setinfoSql = "update `" + table_name + "` set `结果`='" + sb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			sb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setseldataoftubBox(String simpleNum, SelomenResult selomenres, String table_name) {
		Connection conn;
		String Finfo = null;
		String tubResult = null;
		StringBuffer Finfosb = null;
		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			Finfosb = new StringBuffer(Finfo);
			tubResult = selomenres.getLys() + selomenres.getKcn() + selomenres.getCas() + selomenres.getHbun()
					+ selomenres.getMan() + selomenres.getSor() + selomenres.getOnpg();
			Finfosb.replace(6, 13, tubResult);

			String setinfoSql = "update `" + table_name + "` set `结果`='" + Finfosb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			Finfosb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setseldataResult(String simpleNum, String result, String table_name) {
		Connection conn;
		String Finfo = null;

		String clearFinfoSql = "select `结果` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%';";
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = simpleInfo.getString("结果");
			}
			StringBuffer sb = new StringBuffer(Finfo);

			sb.replace(13, Finfo.length(), result);
			String setinfoSql = "update `" + table_name + "` set `结果`='" + sb.toString() + "' where `样品短号` like '"
					+ simpleNum + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setinfoSql);
			pstmt.executeUpdate();
			sb.delete(0, 13);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
