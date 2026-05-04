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
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

public class AddFtoSimple {
	final String selomenTable = "selomenResult";

	public void getNumber(String simpleNum, String simpleName, String table_name, Model model) {

		System.out.println(table_name);
		String number = simpleNum;
		System.out.println(simpleNum);
		System.out.println(number);
		if (isFsimple(simpleNum, simpleName, table_name)) {
			deleteMethord(number, simpleName, table_name);
			deleteFFromeTodayDate(number, simpleName);
		} else {
			setMethord(number, simpleName, table_name);
			addFtoTodayDate(number, simpleName, table_name);
		}

	}

	// 获取数据库
	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	// 判断是否是复测
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
			if (isF != null) {
				return true;
			}
			simpleInfo.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	// 设置为复测
	private void setMethord(String num, String simpleName, String table_name) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String Fdata = sdf.format(new Date());

		String sql = "select * from `" + table_name + "`;";
		String setFSql = "update `" + table_name + "` set `复测`='F" + Fdata + "' where `样品短号` like '" + num
				+ "%' AND `检测项目` like '" + simpleName + "%';";

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

	// 取消设置为复测
	private void deleteMethord(String num, String simpleName, String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String deleteFSql = "update `" + table_name + "` set `复测`=null where `样品短号` like '" + num
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

	private void addFtoTodayDate(String num, String simpleName, String table_name_beforeday) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		int maxID = 0;
		ArrayList<Object> list = new ArrayList<Object>();
		String sql = "select * from `" + table_name_beforeday + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";
		String insertSql = "insert into `" + table_name
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`复测`,`顺序`) values(?,?,?,?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + table_name
				+ "`);";
		Connection conn;
		Statement stmt;
		Statement simpleInfostmt;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			simpleInfostmt = conn.createStatement();
			getMaxidstmt = conn.createStatement();
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(insertSql);
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);

			ResultSet simpleInfo = simpleInfostmt.executeQuery(sql);

			if (tables.next()) {
				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
				while (getMaxid.next()) {
					maxID = getMaxid.getInt("id");
				}

				pstmt.setInt(1, maxID + 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, simpleInfo.getString("检测项目"));
					pstmt.setString(6, simpleInfo.getString("检测方法"));
					pstmt.setString(7, simpleInfo.getString("报告单位"));
					pstmt.setString(8, simpleInfo.getString("备注"));
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			} else {
				String creattablesql = "CREATE TABLE `test`.`" + table_name + "` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
						+ "  `检测方法` VARCHAR(100) NULL,\r\n" + "  `报告单位` VARCHAR(100) NULL,\r\n"
						+ "  `备注` TEXT NULL,\r\n" + "  `done` int NOT NULL DEFAULT 0,\r\n"
						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n"
						+ "  `位置` VARCHAR(20) NULL,\r\n" + "  `顺序` int NOT NULL DEFAULT 0,\r\n"
						+ "  PRIMARY KEY (`id`));";
				stmt.execute(creattablesql);
				pstmt.setInt(1, 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, simpleInfo.getString("检测项目"));
					pstmt.setString(6, simpleInfo.getString("检测方法"));
					pstmt.setString(7, simpleInfo.getString("报告单位"));
					pstmt.setString(8, simpleInfo.getString("备注"));
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void deleteFFromeTodayDate(String num, String simpleName) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String deletesql = "DELETE FROM `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";
		Connection conn;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(deletesql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setSelomenToFlabel(String simpleNum, String table_name, Model model) {

		if (isFselomensimple(simpleNum, table_name)) {
			deleteselomenMethord(simpleNum, table_name);
			deleteFSelomen(simpleNum);
		} else {
			setselomenMethord(simpleNum, table_name);
			addFselomenToTable(simpleNum, table_name);
		}

	}

//获取数据库
	private Connection getselomenConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

//判断是否是复测
	private boolean isFselomensimple(String simpleNum, String table_name) {
		String sql = "select `复测` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%' ;";
		Connection conn;
		String isF = null;
		try {
			conn = getselomenConnection();
			Statement stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				isF = simpleInfo.getString("复测");
			}
			if (isF != null) {
				return true;
			}
			simpleInfo.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

//设置为复测
	private void setselomenMethord(String num, String table_name) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String Fdata = sdf.format(new Date());

		String sql = "select * from `" + table_name + "`;";
		String setFSql = "update `" + table_name + "` set `复测`='F" + Fdata + "' where `样品短号` like '" + num + "%';";

		Statement stmt;
		try {
			Connection conn = getselomenConnection();
			PreparedStatement pstmt = conn.prepareStatement(setFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//取消设置为复测
	private void deleteselomenMethord(String num, String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String deleteFSql = "update `" + table_name + "` set `复测`=null where `样品短号` like '" + num + "%';";

		Statement stmt;
		try {
			Connection conn = getselomenConnection();
			PreparedStatement pstmt = conn.prepareStatement(deleteFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addFselomenToTable(String num, String table_name) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String todayDate = sdf.format(new Date());
		int maxID = 0;
		ArrayList<Object> list = new ArrayList<Object>();
		String sql = "select * from `" + table_name + "` where `样品短号` like '" + num + "%';";
		String insertSql = "insert into `" + todayDate
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`复测`,`顺序`) values(?,?,?,?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `" + todayDate + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + todayDate
				+ "`);";
		Connection conn;
		Connection selomenconn;
		Statement stmt;
		Statement simpleInfostmt;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			selomenconn = getselomenConnection();
			simpleInfostmt = selomenconn.createStatement();
			getMaxidstmt = conn.createStatement();
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(insertSql);
			ResultSet tables = conn.getMetaData().getTables(null, null, todayDate, null);

			ResultSet simpleInfo = simpleInfostmt.executeQuery(sql);

			if (tables.next()) {
				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
				while (getMaxid.next()) {
					maxID = getMaxid.getInt("id");
				}

				pstmt.setInt(1, maxID + 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, "沙门氏菌");
					pstmt.setString(6, "/");
					pstmt.setString(7, "/");
					pstmt.setString(8, "/");
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			} else {
				String creattablesql = "CREATE TABLE `test`.`" + todayDate + "` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
						+ "  `检测方法` VARCHAR(100) NULL,\r\n" + "  `报告单位` VARCHAR(100) NULL,\r\n"
						+ "  `备注` TEXT NULL,\r\n" + "  `done` int NOT NULL DEFAULT 0,\r\n"
						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n"
						+ "  `位置` VARCHAR(20) NULL,\r\n" + "  `顺序` int NOT NULL,\r\n" + "  PRIMARY KEY (`id`));";
				stmt.execute(creattablesql);
				pstmt.setInt(1, 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, "沙门氏菌");
					pstmt.setString(6, "/");
					pstmt.setString(7, "/");
					pstmt.setString(8, "/");
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteFSelomen(String num) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String deletesql = "DELETE FROM `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ "沙门氏菌" + "%';";
		Connection conn;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(deletesql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
