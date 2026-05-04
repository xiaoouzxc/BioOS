package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuerySimple {

	private static final String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static final String name = "root";
	private static final String password = "1234";

	public ArrayList<Simple> getQuerySimple(String table_name, String simplenum) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到", "0"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到","0"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {

					if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
							&& !simpleInfo.getString("检测项目").contains("致泻")
							&& !simpleInfo.getString("检测项目").contains("O157")
							&& !simpleInfo.getString("检测项目").contains("商业无菌")
							&& !simpleInfo.getString("检测项目").contains("副溶血")) {
						if (simpleInfo.getString("样品短号").contains(simplenum)) {
							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
						}

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromname(String table_name, String simpleNam) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到","0"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到","0"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {

					if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
							&& !simpleInfo.getString("检测项目").contains("致泻")
							&& !simpleInfo.getString("检测项目").contains("O157")
							&& !simpleInfo.getString("检测项目").contains("商业无菌")
							&& !simpleInfo.getString("检测项目").contains("副溶血")) {
						if (simpleInfo.getString("样品名称").contains(simpleNam)) {
							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
						}

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromYeartable(String table_name, String simplenum) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {

					if (simpleInfo.getString("样品短号").contains(simplenum)) {
						simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
								simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), "复测信息：" + simpleInfo.getString("复测"),
								simpleInfo.getString("结果")));

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromnamefromYeartable(String table_name, String simpleNam) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {

					if (simpleInfo.getString("样品名称").contains(simpleNam)) {
						simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
								simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), "复测信息：" + simpleInfo.getString("复测"),
								simpleInfo.getString("结果")));

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}
}
