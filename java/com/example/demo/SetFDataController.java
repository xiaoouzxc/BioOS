package com.example.demo;

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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class SetFDataController {

	@GetMapping("/setFData")
	public String result(Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());

		if (table_name == null) {
			return "getTestData/setFData";
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
					return "getTestData/setFData";
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {
					if (simpleInfo.getString("复测") != null && simpleInfo.getString("复测").contains("BF")) {
						simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("备注"),
								simpleInfo.getString("复测"), simpleInfo.getString("结果")));
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/setFData";
		}
	}

	@GetMapping("/deleteDuo")
	public String deleteDuo(String num, String nam, Model model) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String deletesql = "DELETE FROM `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '" + nam
				+ "%';";
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
		return result(model);

	}

	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	public void setFtieMethord(String simpleNum, String simpleName, String firstTie, String lastTie) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String Finfo = null;

		String clearFinfoSql = "select `备注` from `" + table_name + "` where `样品短号` like '" + simpleNum
				+ "%' AND `检测项目` like '" + simpleName + "%';";
		Connection conn;
		Statement stmt;
		try {
			conn = getConnection();
			stmt = conn.createStatement();

			ResultSet simpleInfo = stmt.executeQuery(clearFinfoSql);
			while (simpleInfo.next()) {
				Finfo = "";
			}
			String setFinfoSql = "update `" + table_name + "` set `备注`='" + Finfo + "T-" + firstTie + "~-" + lastTie
					+ "' where `样品短号` like '" + simpleNum + "%' AND `检测项目` like '" + simpleName + "%';";
			PreparedStatement pstmt = conn.prepareStatement(setFinfoSql);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setFtieMethord(String simpleNum, String simpleName, String Fmethod) {
		if (Fmethod != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
			String table_name = sdf.format(new Date());
			String setFmetSql = "update `" + table_name + "` set `检测方法`='" + Fmethod + "' where `样品短号` like '"
					+ simpleNum + "%' AND `检测项目` like '" + simpleName + "%';";

			Connection conn;

			try {
				conn = getConnection();

				PreparedStatement pstmt = conn.prepareStatement(setFmetSql);
				pstmt.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
