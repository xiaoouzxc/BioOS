package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.test.service.SampleTaskTable;

public class GetSimpleList {
	private ArrayList<Simple> simpleList = new ArrayList<Simple>();

	public ArrayList<Simple> getLabelList() {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String sql = "select * from `" + table_name + "` where `done`=0;";

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return null;
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getString("复测") == null) {
					simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
							simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getInt("done"),
							simpleInfo.getString("报告单位")));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}

	public ArrayList<Simple> getFLabelList() {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String table_name = sdf.format(new Date());
		String sql = "select * from `" + table_name + "` where `done`=0;";

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return null;
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getString("复测") != null) {
					simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
							simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("备注"),
							simpleInfo.getString("复测"), simpleInfo.getString("结果")));
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}
	public ArrayList<Simple> getSelomenLabelList(String table_name) {
		
		String sql = "select * from `" + table_name + "`;";

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				
					simpleList.add(new Simple(simpleInfo.getInt("id"),simpleInfo.getString("样品短号"),simpleInfo.getString("复测")));
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}

	public ArrayList<Simple> getList(String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return null;
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
						&& !simpleInfo.getString("检测项目").contains("致泻")
						&& !simpleInfo.getString("检测项目").contains("O157")
						&& !simpleInfo.getString("检测项目").contains("商业无菌")
						&& !simpleInfo.getString("检测项目").contains("微生物")
						&& !simpleInfo.getString("检测项目").contains("副溶血")) {
					simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
							simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
							simpleInfo.getInt("id")));

				} else if (simpleInfo.getString("检测项目").contains("微生物") && simpleInfo.getString("检测项目").contains("霉")) {
					simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
							simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
							simpleInfo.getInt("id")));

				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}
	public ArrayList<Simple> getSZList(String table_name) {
		simpleList.clear();

		String sampleDate = table_name == null || table_name.trim().isEmpty()
				? LocalDate.now(ZoneId.of("Asia/Shanghai")).toString()
				: table_name.trim().replace("/", "-");
		String sampleTableName = sampleDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}")
				? "total_samples_" + sampleDate.substring(0, 4)
				: SampleTaskTable.currentYearTableName();
		String sql = "select * from `" + sampleTableName
				+ "` where DATE(`传入时间`) = ? and `检测项目` is not null and `报告单位` is not null and `结果` is not null;";

		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet tables = conn.getMetaData().getTables(null, null, sampleTableName, null);
			if (!tables.next()) {
				
				return simpleList;
			}
			stmt.setString(1, sampleDate);
			ResultSet simpleInfo = stmt.executeQuery();
			while (simpleInfo.next()) {
				 if(simpleInfo.getString("检测项目").contains("肠")) {
					 System.out.println("dachang");
				 }else {
					 if (simpleInfo.getString("报告单位").contains("CFU")||simpleInfo.getString("报告单位").contains("cfu")) {
							
							if(simpleInfo.getString("结果")!=null&&simpleInfo.getString("结果").length()>20) {
								simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
										simpleInfo.getInt("id")));
							}	

						} 
				 }
				
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}

	public ArrayList<Simple> getSelmonList() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String table_name = "selomenResult";
		String sql = "select * from `" + table_name + "`;";

		ArrayList<Simple> selomenList = new ArrayList<Simple>();

		Date todaydate = new Date();
		// long monthagotime=todaydate.getTime()-2626560000L;
		long monthagotime = todaydate.getTime() - 1209600000L;
		Date monthagodate = new Date(monthagotime);
		String date = sdf.format(monthagodate);
		String deletesql = "delete from `" + table_name + "`where `日期`<'" + date + "';";
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			Statement deleteste = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return null;
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getDate("日期").before(monthagodate)) {
					selomenList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));
				}

			}
			// deleteste.execute(deletesql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return selomenList;

	}

	public ArrayList<Simple> getSelmonFList() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String table_name = "selomenfresult";
		String sql = "select * from `" + table_name + "`;";

		ArrayList<Simple> selomenList = new ArrayList<Simple>();

		Date todaydate = new Date();
		// long monthagotime=todaydate.getTime()-2626560000L;
		long monthagotime = todaydate.getTime() - 1209600000L;
		Date monthagodate = new Date(monthagotime);
		String date = sdf.format(monthagodate);
		String deletesql = "delete from `" + table_name + "`where `日期`<'" + date + "';";
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn;

		try {
			conn = DriverManager.getConnection(url, name, password);
			Statement stmt = conn.createStatement();
			Statement deleteste = conn.createStatement();
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
			if (!tables.next()) {
				return null;
			}
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				if (simpleInfo.getDate("日期").before(monthagodate)) {
					selomenList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("复测"),
							simpleInfo.getString("结果"), simpleInfo.getDate("日期")));
				}

			}
			// deleteste.execute(deletesql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return selomenList;

	}

	public void close() {
		simpleList.clear();
	}

}
