package com.cti.sqlCreat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

@Repository
public class CreatConnection {

	private Connection connection;

	public static Connection getConnection() throws SQLException {

		Connection conn = DriverManager.getConnection(UrlMap.testurl, UrlMap.name, UrlMap.password);
		return conn;
	}

	public ResultSet testSet(String sql, String table_name) throws SQLException {

		connection = DriverManager.getConnection(UrlMap.testurl, UrlMap.name, UrlMap.password);

		if (connection.getMetaData().getTables(null, null, table_name, null).next() == false) {
			return null;
		} else {
			ResultSet simpleInfo = connection.createStatement().executeQuery(sql);
			return simpleInfo;
		}

	}

	public ResultSet selomenSet(String sql, String table_name) throws SQLException {

		connection = DriverManager.getConnection(UrlMap.selomenurl, UrlMap.name, UrlMap.password);

		if (connection.getMetaData().getTables(null, null, table_name, null).next() == false) {
			return null;
		} else {
			ResultSet simpleInfo = connection.createStatement().executeQuery(sql);

			return simpleInfo;
		}

	}

	public void close() {
		try {
			connection.createStatement().close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

}
