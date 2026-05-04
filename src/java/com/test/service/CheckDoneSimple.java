package com.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.example.demo.Simple;

@Service
public class CheckDoneSimple {
	public String result(Model model,String position) {

		String table_name = SampleTaskTable.currentYearTableName();
		String todayCondition = "DATE(`传入时间`) = CURDATE()";
		boolean canOperate = false;
		try {
			canOperate = position != null && hasTodayClaimedSamples(table_name, position);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		String scopeSql = canOperate ? "`位置` = '" + position + "' and " : "";
		String sql = "SELECT * FROM `" + table_name + "` where " + scopeSql + "`done`=1 and " + todayCondition + ";";
		String doneSql = "update `" + table_name + "` set `done`='T';";
		List<Simple> simpleList = new ArrayList<Simple>();
		ArrayList<Simple> presentList = new ArrayList<Simple>();

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

			int id = 0;
			String presentNum = null;
			String testItem = "";
			String testMethord = "";
			String unit = "";

			String presentName = null;
			String presentCo = null;
			String presentInfo = null;
			int done = 0;
			String isF = null;

			while (simpleInfo.next()) {
				Simple simple = new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
						simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("检测项目"),
						simpleInfo.getString("检测方法"), simpleInfo.getString("报告单位"), simpleInfo.getString("备注"),
						simpleInfo.getInt("done"), simpleInfo.getString("复测"));
				simple.setDailySampleOrder(simpleInfo.getInt("当天样品序号"));
				simpleList.add(simple);
			}
			simpleList.add(new Simple(0, "/", "/", "/", "/", "/", "/", "/", 0, "/"));
			for (int i = 0; i <= simpleList.size(); i++) {
				if (i + 1 == simpleList.size()) {

					break;
				}
				if (simpleList.get(i).getNumber().compareTo(simpleList.get(i + 1).getNumber()) == 0) {
					testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
					testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------"
							+ "\r\n";
					unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
				} else {
					id++;
					testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
					testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------"
							+ "\r\n";
					unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
					presentNum = simpleList.get(i).getNumber();
					presentName = simpleList.get(i).getName();
					presentCo = simpleList.get(i).getCompany();
					presentInfo = simpleList.get(i).getTip();
					done = simpleList.get(i).getDone();
					isF = simpleList.get(i).getIfF();

					Simple presentSimple = new Simple(id, presentNum, presentName, presentCo, testItem, testMethord, unit,
							presentInfo, done, isF);
					presentSimple.setDailySampleOrder(simpleList.get(i).getDailySampleOrder());
					presentList.add(presentSimple);
					testItem = "";
					testMethord = "";
					unit = "";
				}
			}
//			for(Simple s:presentList) {
//				System.out.println(s.getId()+","+s.getNumber()+","+s.getName()+","+s.getCompany()+","+s.getTestItem()+","+s.getTestMethod()+","+s.getUnit()+","+s.getTip());
//			}
			// stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE,
			// ResultSet.CONCUR_UPDATABLE);
			// PreparedStatement pstmt=conn.prepareStatement(doneSql);
			// pstmt.executeUpdate();
		} catch (SQLException e) {

			e.printStackTrace();
		}

		model.addAttribute("presentList", presentList);

		return "checkDoneSimple";

	}

	private boolean hasTodayClaimedSamples(String tableName, String position) throws SQLException {
		String sql = "select 1 from `" + tableName + "` where `位置`='" + position + "' and DATE(`传入时间`) = CURDATE() limit 1;";
		try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8", "root", "1234");
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			return rs.next();
		}
	}
}
