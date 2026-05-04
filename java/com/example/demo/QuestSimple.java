package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
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

import com.test.service.StartTestingController;

@Controller
public class QuestSimple {
	@GetMapping("quest")
	public String result(String questSimpleNum, Model model) {
		String num = questSimpleNum;
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String sql = "select * from `" + table_name + "`;";

		List<Simple> questList = new ArrayList<Simple>();
		ArrayList<Simple> presentQuestList = new ArrayList<Simple>();

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;
		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);

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
				if (simpleInfo.getString("样品短号").contains(num) && simpleInfo.getInt("done") != 1) {
					questList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("检测项目"),
							simpleInfo.getString("检测方法"), simpleInfo.getString("报告单位"), simpleInfo.getString("备注"),
							simpleInfo.getInt("done"), simpleInfo.getString("复测")));
				}
			}
			questList.add(new Simple(0, "/", "/", "/", "/", "/", "/", "/", 0, "/"));
			for (int i = 0; i <= questList.size(); i++) {
				if (i + 1 == questList.size()) {

					break;
				}
				if (questList.get(i).getNumber().compareTo(questList.get(i + 1).getNumber()) == 0) {
					testItem = testItem + questList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
					testMethord = testMethord + questList.get(i).getTestMethod() + "\r\n" + "------------------------"
							+ "\r\n";
					unit = unit + questList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
				} else {
					id++;
					testItem = testItem + questList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
					testMethord = testMethord + questList.get(i).getTestMethod() + "\r\n" + "------------------------"
							+ "\r\n";
					unit = unit + questList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
					presentNum = questList.get(i).getNumber();
					presentName = questList.get(i).getName();
					presentCo = questList.get(i).getCompany();
					presentInfo = questList.get(i).getTip();
					done = questList.get(i).getDone();
					isF = questList.get(i).getIfF();

					presentQuestList.add(new Simple(id, presentNum, presentName, presentCo, testItem, testMethord, unit,
							presentInfo, done, isF));
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

		model.addAttribute("presentQuestList", presentQuestList);
		StartTestingController stc = new StartTestingController();
		return stc.result(model);

	}

}
