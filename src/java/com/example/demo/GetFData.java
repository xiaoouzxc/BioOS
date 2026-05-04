package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GetFData {
	@GetMapping("/getFData")
	public String result(String date, Model model) {
		String table_name = date;
		if (table_name == null) {
			return "getTestData/getFData";
		} else {
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
						simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("复测"),
								simpleInfo.getString("结果"), simpleInfo.getInt("done")));
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/getFData";
		}
	}

}
