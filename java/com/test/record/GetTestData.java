package com.test.record;

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

import com.example.demo.Simple;

@Controller
public class GetTestData {
	@GetMapping("/getData")
	public String result(String date, Model model) {
		String table_name = date;
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
				return "没有单子";
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);

			while (simpleInfo.next()) {

				simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
						simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("复测"),
						simpleInfo.getString("结果"), simpleInfo.getInt("done")));

			}
			simpleList.add(new Simple("/", "/", "/", "/", "/", "/",0));

		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);

		return "getData";

	}

}
