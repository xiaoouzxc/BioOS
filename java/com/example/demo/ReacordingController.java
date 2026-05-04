package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.google.gson.Gson;

@Controller
public class ReacordingController {
	String haveRecordingtable_name;
	private ArrayList<String> tableNameList = new ArrayList<String>();

	@GetMapping("/recording")
	public String result(String date, Model model) {
		tableNameList = new getDBtestName().getSevendayTestName();
		model.addAttribute("nameList", tableNameList);

		String table_name = date;
		if (table_name == null) {
			return "getTestData/recording";
		} else {
			String sql = "select * from `" + table_name + "`;";
			List<Simple> simpleList = new ArrayList<Simple>();
			List<Simple> SMsimpleList = new ArrayList<Simple>();

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

				// simpleInfo.getString("复测")==null&&
				while (simpleInfo.next()) {
					if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
							&& !simpleInfo.getString("检测项目").contains("致泻")
							&& !simpleInfo.getString("检测项目").contains("O157")
							&& !simpleInfo.getString("检测项目").contains("商业无菌")
							&& !simpleInfo.getString("检测项目").contains("微生物")
							&& !simpleInfo.getString("检测项目").contains("副溶血")) {
						if (simpleInfo.getString("结果") == null
								|| simpleInfo.getString("复测") == null && simpleInfo.getString("结果").isBlank()) {
							if (simpleInfo.getString("检测项目").contains("霉")
									|| simpleInfo.getString("检测项目").contains("酵母")) {
								SMsimpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
										simpleInfo.getString("复测"), simpleInfo.getString("结果"), simpleInfo.getInt("done")));

							} else {
								simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
										simpleInfo.getString("复测"), simpleInfo.getString("结果"), simpleInfo.getInt("done")));
							}
						}
					} else if (simpleInfo.getString("检测项目").contains("微生物")) {
						// simpleInfo.getString("复测")==null&&
						if (simpleInfo.getString("结果") == null || simpleInfo.getString("结果").isBlank()) {
							if (simpleInfo.getString("检测项目").contains("霉")
									|| simpleInfo.getString("检测项目").contains("酵母")) {
								SMsimpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
										simpleInfo.getString("复测"), simpleInfo.getString("结果"), simpleInfo.getInt("done")));

							}
						}
					}
				}
				for (Simple s : SMsimpleList) {
					simpleList.add(s);
				}
			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);
			haveRecordingtable_name = table_name;
			System.out.println(haveRecordingtable_name);

			return "getTestData/recording";
		}
	}

	@GetMapping("/haverecorded")
	public String haveResulted(String date,Model model) {
		String table_name =date; //haveRecordingtable_name
		System.out.println(table_name);
		if (table_name == null) {
			return "getTestData/recording";
		} else {
			String sql = "select * from `" + table_name + "`;";
			List<Simple> simpleList = new ArrayList<Simple>();
			List<Simple> SMsimpleList = new ArrayList<Simple>();

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
					// simpleInfo.getString("复测")==null&&
					if (simpleInfo.getString("结果") != null && !simpleInfo.getString("结果").isBlank()) {
						if (simpleInfo.getString("检测项目").contains("霉") || simpleInfo.getString("检测项目").contains("酵母")) {
							SMsimpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
									simpleInfo.getString("复测"), simpleInfo.getString("结果"), simpleInfo.getInt("done")));

						} else {
							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
									simpleInfo.getString("复测"), simpleInfo.getString("结果"), simpleInfo.getInt("done")));

						}
					} else if (simpleInfo.getString("报告单位").contains("MPN") && simpleInfo.getString("结果") != null
							&& !simpleInfo.getString("结果").isBlank()) {
						simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("复测"),
								simpleInfo.getString("结果"), simpleInfo.getInt("done")));
					}

				}
				for (Simple s : SMsimpleList) {
					simpleList.add(s);
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/haveRecorded";
		}
	}

	@GetMapping("/Frecording")
	public String resultF(String date, Model model) {
		String table_name = date;
		if (table_name == null) {
			return "getTestData/recording";
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
					if (!simpleInfo.getString("检测项目").contains("沙")) {
						if (simpleInfo.getString("复测") != null && simpleInfo.getString("复测").contains("BF")) {
							if (simpleInfo.getInt("done") == 0) {
								simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), "没做"));
							} else {
								simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
										simpleInfo.getString("结果")));
							}

						}
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/recording";
		}
	}

	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	private Connection getSelomenConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	public void setDataMethord(String simpleNum, String simpleName, String data, String table_name) {

		String setFinfoSql = "update `" + table_name + "` set `结果`='" + data + "' where `样品短号` like '" + simpleNum
				+ "%' AND `检测项目` like '" + simpleName + "%';";

		Connection conn;

		try {
			conn = getConnection();

			PreparedStatement pstmt = conn.prepareStatement(setFinfoSql);

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	@GetMapping("/manualAddData")
	public String manualAdd(String simpleNum, String simpleName, String simpleItem, String testMethord,
			String table_name, Model model) {
		if (table_name == null) {
			return "noListError.html";
		}
		int maxID = 0;
		String equalID = null;
		boolean wetherAdd = true;
		String insertSql = "insert into `" + table_name
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`done`,`复测`,`结果`) values(?,?,?,?,?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + table_name
				+ "`);";
		String IDquery = "SELECT `样品短号` FROM `" + table_name + "`;";
		Connection conn;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			getMaxidstmt = conn.createStatement();
			pstmt = conn.prepareStatement(insertSql);
			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);

			if (tables.next()) {
				ResultSet getEqualid = getMaxidstmt.executeQuery(IDquery);
				while (getEqualid.next()) {
					equalID = getEqualid.getString("样品短号");
					if (equalID.compareTo(simpleNum) == 0) {
						wetherAdd = false;
						break;
					}
				}
				if (wetherAdd == false) {
					return resultF(table_name, model);
				} else {
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
					pstmt.setString(9, "1");
					pstmt.setString(10, "BF");
					pstmt.setString(11, "");
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return resultF(table_name, model);

	}

	@GetMapping("/recordingSelomen")
	public String selomenResult(String date, Model model) {

		String table_name = date;
		if (table_name == null) {
			return "getTestData/recording";
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
					if (simpleInfo.getString("检测项目").contains("沙")) {

						simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
								simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("复测"), simpleInfo.getString("位置")));
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/SelomenRecording.html";
		}

	}

	@GetMapping("/recordingSuspectionSelomen")
	public String reselomenResult(Model model) {
		tableNameList = new getDBtestName().getSevendayTestName();
		model.addAttribute("nameList", tableNameList);
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		Date todaydate = new Date();
		long d3agotime = todaydate.getTime() - 259200000l;
		Date threedayagodate = new Date(d3agotime);
		String table_name = sdf.format(threedayagodate);
		if (table_name == null) {
			return "getTestData/recording";
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
					return "getTestData/SelomenRecording.html";
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {
					if (simpleInfo.getString("检测项目").contains("沙")) {

						simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
								simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("复测"), simpleInfo.getString("位置")));
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			model.addAttribute("simpleList", simpleList);
			model.addAttribute("tablename", table_name);

			return "getTestData/SelomenRecording.html";
		}

	}

	public void addSelomen(String num, String simpleName, String table_name) {
		System.out.println(num + "," + simpleName);
		int maxID = 0;
		ArrayList<Object> list = new ArrayList<Object>();
		String sql = "select * from `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";
		String insertSql = "insert into `selomenResult`(`id`,`样品短号`,`样品名称`,`报告抬头`,`复测`,`结果`,`日期`) values(?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `selomenResult` WHERE `id` = (SELECT MAX(`id`) FROM `selomenResult`);";
		Connection conn;
		Connection selomenconn;
		Statement stmt;
		Statement simpleInfostmt;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			selomenconn = getSelomenConnection();
			simpleInfostmt = conn.createStatement();
			getMaxidstmt = selomenconn.createStatement();
			stmt = selomenconn.createStatement();
			pstmt = selomenconn.prepareStatement(insertSql);
			ResultSet tables = selomenconn.getMetaData().getTables(null, null, "selomenResult", null);

			ResultSet simpleInfo = simpleInfostmt.executeQuery(sql);

			if (tables.next()) {
				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
				while (getMaxid.next()) {
					maxID = getMaxid.getInt("id");
				}

				pstmt.setInt(1, maxID + 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, null);
					pstmt.setString(6, "nnnnnnnnnnnnnn");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					table_name = table_name.replace("/", "-");
					System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
					java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
					pstmt.setDate(7, sqlDate);
					pstmt.executeUpdate();
				}
			} else {
				String creattablesql = "CREATE TABLE `selomen`.`selomenResult` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `复测` VARCHAR(20) NULL,\r\n"
						+ "  `结果` VARCHAR(20) NULL,\r\n" + "  `位置` VARCHAR(20) NULL,\r\n" + "  `日期`  DATE NOT NULL,\r\n"
						+ "  PRIMARY KEY (`id`));";
				stmt.execute(creattablesql);
				pstmt.setInt(1, 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, null);
					pstmt.setString(6, "nnnnnnnnnnnnnn");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					table_name = table_name.replace("/", "-");
					System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
					java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
					pstmt.setDate(7, sqlDate);
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

	}

	public void addFSelomen(String num, String simpleName, String table_name) {
		System.out.println(num + "," + simpleName);
		int maxID = 0;
		ArrayList<Object> list = new ArrayList<Object>();
		String sql = "select * from `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";
		String selomenFResultsql = "select * from `" + "selomenFResult" + "` where `样品短号` like '" + num + "%';";
		String insertSql = "insert into `selomenFResult`(`id`,`样品短号`,`样品名称`,`报告抬头`,`复测`,`结果`,`日期`) values(?,?,?,?,?,?,?);";
		String maxIDquery = "SELECT `id` FROM `selomenFResult` WHERE `id` = (SELECT MAX(`id`) FROM `selomenFResult`);";
		Connection conn;
		Connection selomenconn;
		Statement stmt;
		Statement simpleInfostmt;
		Statement getMaxidstmt;
		Statement equalFselomen;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			selomenconn = getSelomenConnection();
			simpleInfostmt = conn.createStatement();
			getMaxidstmt = selomenconn.createStatement();
			equalFselomen = selomenconn.createStatement();
			PreparedStatement preequalFselomen = equalFselomen.getConnection().prepareStatement(selomenFResultsql,
					ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			stmt = selomenconn.createStatement();
			pstmt = selomenconn.prepareStatement(insertSql);
			ResultSet tables = selomenconn.getMetaData().getTables(null, null, "selomenFResult", null);

			ResultSet simpleInfo = simpleInfostmt.executeQuery(sql);

			if (tables.next()) {
				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
				while (getMaxid.next()) {
					maxID = getMaxid.getInt("id");
				}

				ResultSet getequalid = preequalFselomen.executeQuery(selomenFResultsql);

				String equalIndex = null;
				if (getequalid.first() == false) {

					pstmt.setInt(1, maxID + 1);
					while (simpleInfo.next()) {

						pstmt.setString(2, simpleInfo.getString("样品短号"));
						pstmt.setString(3, simpleInfo.getString("样品名称"));
						pstmt.setString(4, simpleInfo.getString("报告抬头"));
						pstmt.setString(5, null);
						pstmt.setString(6, "nnnnnnnnnnnnnn");
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						table_name = table_name.replace("/", "-");
						System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
						java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
						pstmt.setDate(7, sqlDate);
						pstmt.executeUpdate();

					}
				} else {
					System.out.println("aaa");

					if (getequalid.last()) {
						equalIndex = getequalid.getString("样品短号");
						if (equalIndex.contains("第")) {
							char index = equalIndex.charAt(equalIndex.length() - 2);
							int lastNum = Character.getNumericValue(index);
							pstmt.setInt(1, maxID + 1);
							while (simpleInfo.next()) {
								String[] str = simpleInfo.getString("样品短号").split("第");

								pstmt.setString(2, str[0] + "第" + (lastNum + 1) + "遍");
								pstmt.setString(3, simpleInfo.getString("样品名称"));
								pstmt.setString(4, simpleInfo.getString("报告抬头"));
								pstmt.setString(5, null);
								pstmt.setString(6, "nnnnnnnnnnnnnn");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								table_name = table_name.replace("/", "-");
								System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
								java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
								pstmt.setDate(7, sqlDate);
								pstmt.executeUpdate();

							}
						} else {
							pstmt.setInt(1, maxID + 1);

							while (simpleInfo.next()) {

								pstmt.setString(2, simpleInfo.getString("样品短号") + "第2遍");
								pstmt.setString(3, simpleInfo.getString("样品名称"));
								pstmt.setString(4, simpleInfo.getString("报告抬头"));
								pstmt.setString(5, null);
								pstmt.setString(6, "nnnnnnnnnnnnnn");
								SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
								table_name = table_name.replace("/", "-");
								System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
								java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
								pstmt.setDate(7, sqlDate);
								pstmt.executeUpdate();
							}
						}

					}

				}

			} else {
				String creattablesql = "CREATE TABLE `selomen`.`selomenFResult` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `复测` VARCHAR(20) NULL,\r\n"
						+ "  `结果` VARCHAR(20) NULL,\r\n" + "  `位置` VARCHAR(20) NULL,\r\n" + "  `日期`  DATE NOT NULL,\r\n"
						+ "  PRIMARY KEY (`id`));";
				stmt.execute(creattablesql);
				pstmt.setInt(1, 1);

				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, null);
					pstmt.setString(6, "nnnnnnnnnnnnnn");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					table_name = table_name.replace("/", "-");
					System.out.println(table_name + "-----" + sdf.format(sdf.parse(table_name)));
					java.sql.Date sqlDate = new java.sql.Date(sdf.parse(table_name).getTime());
					pstmt.setDate(7, sqlDate);
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException | ParseException e) {
			e.printStackTrace();
		}

	}
}
