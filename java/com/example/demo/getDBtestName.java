package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class getDBtestName {
	private static final String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static final String name = "root";
	private static final String password = "1234";
	private static final String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'test';";

	public ArrayList<String> getTestName() {

		ArrayList<String> list = new ArrayList<String>();

		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			// ResultSet tables=conn.getMetaData().getTables(null, null, null,new String[]
			// {"TABLE"});
			ResultSet tables = stmt.executeQuery(sql);
			while (tables.next()) {
				String table_name = tables.getString("table_name");
				if (table_name.contains("/")&&table_name.length()<11) {
					list.add(table_name);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}
	public ArrayList<String> getTestSelomenLableName() {

		ArrayList<String> list = new ArrayList<String>();

		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			// ResultSet tables=conn.getMetaData().getTables(null, null, null,new String[]
			// {"TABLE"});
			ResultSet tables = stmt.executeQuery(sql);
			while (tables.next()) {
				String table_name = tables.getString("table_name");
				if (table_name.contains("/")&&table_name.length()>10) {
					list.add(table_name);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

	public ArrayList<String> getSevendayTestName() {
		ArrayList<String> list = new ArrayList<String>();
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			// ResultSet tables=conn.getMetaData().getTables(null, null, null,new String[]
			// {"TABLE"});
			ResultSet tables = stmt.executeQuery(sql);
			int heap = 14;
			while (tables.next()) {

				if (heap < 15) {
					String table_name = tables.getString("table_name");

					if (table_name.contains("/")&&table_name.length()<11) {
						list.add(table_name);

					}
				}
				heap--;

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	public ArrayList<String> getYearTestName() {

		ArrayList<String> list = new ArrayList<String>();

		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			// ResultSet tables=conn.getMetaData().getTables(null, null, null,new String[]
			// {"TABLE"});
			ResultSet tables = stmt.executeQuery(sql);
			while (tables.next()) {
				String table_name = tables.getString("table_name");
				if (table_name.contains("t")) {
					list.add(table_name);
				}

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;

	}

}
