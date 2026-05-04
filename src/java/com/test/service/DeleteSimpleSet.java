package com.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Service
public class DeleteSimpleSet {

	public int delete() {
		return 0;
/*
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String table_name = sdf.format(new Date());
		String lastSql = "SELECT `顺序` FROM `" + table_name + "` WHERE `顺序`!=0;";
		int arr = 0;

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return 0;
			}

			ResultSet simpleinfo = stmt.executeQuery(lastSql);
			while (simpleinfo.next()) {

				arr = simpleinfo.getInt("顺序");
			}
			System.out.println(arr);
			if (arr != 0) {
				String delSql = "delete from `" + table_name + "` where `顺序`=" + arr + ";";
				stmt.execute(delSql);
				stmt.close();
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return arr;
*/

	}

}
