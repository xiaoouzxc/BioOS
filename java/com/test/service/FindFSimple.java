package com.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.Simple;

@Service
public class FindFSimple {

	public String result(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());

		String sql = "select * from `" + table_name + "`;";
		List<Simple> simpleList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return "noListError.html";
			}

			ResultSet simpleInfo = stmt.executeQuery(sql);

			while (simpleInfo.next()) {
				if (simpleInfo.getString("复测") != null && simpleInfo.getString("复测").contains("BF")) {
					simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("检测项目"), simpleInfo.getString("复测"),
							simpleInfo.getString("位置")));
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);

		return "getTestData/findSimple";

	}

}
