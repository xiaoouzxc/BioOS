package com.test.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.cti.sqlCreat.CreatConnection;

@Service
public class MannualaddFsimple {
	public String manualAddF(String simpleNum, String simpleName, String simpleItem, String testMethord) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		int maxID = 0;
		String insertSql = "insert into `" + table_name
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`复测`,`顺序`) values(?,?,?,?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + table_name
				+ "`);";
		Connection conn;
		Statement stmt;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = CreatConnection.getConnection();
			getMaxidstmt = conn.createStatement();
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(insertSql);
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);

			if (tables.next()) {
				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
				while (getMaxid.next()) {
					maxID = getMaxid.getInt("id");
				}

				pstmt.setInt(1, maxID + 1);
				pstmt.setString(2, simpleNum);
				pstmt.setString(3, simpleName);
				pstmt.setString(4, "");
				pstmt.setString(5, simpleItem);
				pstmt.setString(6, testMethord);
				pstmt.setString(7, "");
				pstmt.setString(8, "");
				pstmt.setString(9, "BF");
				pstmt.setInt(10, 0);
				pstmt.executeUpdate();

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

				pstmt.setInt(1, maxID + 1);
				pstmt.setString(2, simpleNum);
				pstmt.setString(3, simpleName);
				pstmt.setString(4, "");
				pstmt.setString(5, simpleItem);
				pstmt.setString(6, testMethord);
				pstmt.setString(7, "");
				pstmt.setString(8, "");
				pstmt.setString(9, "BF");
				pstmt.setInt(10, 0);
				pstmt.executeUpdate();

			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "index2.html";

	}
}
