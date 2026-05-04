package com.example.demo;

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

import com.google.gson.Gson;

@Controller
public class RecordSelomenData {
	final String table_name = "selomenResult";
	final String Ftable_name = "selomenFResult";

	@GetMapping("/recordselData")
	public String selomenResult(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		Date todaydate = new Date();
		long sevenagotime = todaydate.getTime() - 1209600000L;
		Date sevenagodate = new Date(sevenagotime);

		String sql = "select * from `" + table_name + "`;";
		List<Simple> simpleList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
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
				if (simpleInfo.getDate("日期").after(sevenagodate)) {

					simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);
		model.addAttribute("tablename", table_name);

		return "getTestData/RecordSelomenData.html";

	}

	@GetMapping("/recordFselData")
	public String selomenFResult(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		Date todaydate = new Date();
		long sevenagotime = todaydate.getTime() - 1209600000L;
		Date sevenagodate = new Date(sevenagotime);

		String sql = "select * from `" + Ftable_name + "`;";
		List<Simple> simpleList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, Ftable_name, null);
			if (!tables.next()) {
				return "noListError.html";
			}

			ResultSet simpleInfo = stmt.executeQuery(sql);

			while (simpleInfo.next()) {
				if (simpleInfo.getDate("日期").after(sevenagodate)) {

					simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));
				}
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("simpleList", simpleList);
		model.addAttribute("tablename", Ftable_name);

		return "getTestData/RecordSelomenData.html";

	}

}
