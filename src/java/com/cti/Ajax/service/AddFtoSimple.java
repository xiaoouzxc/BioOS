package com.cti.Ajax.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.test.service.SampleTaskTable;

public class AddFtoSimple {
	final String selomenTable = "selomenResult";

	public void getNumber(String simpleNum,String id, String simpleName, String table_name, Model model) {

		System.out.println(table_name);
		String number = simpleNum;
		System.out.println(simpleNum);
		System.out.println(number);
		addFtoTodayDate(number,id, simpleName, table_name);

	}

	// 获取数据库
	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

	// 判断是否是复测
	private boolean isFsimple(String simpleNum,String id, String simpleName, String table_name) {
		String sql = "select `复测` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%' AND `检测项目` like '"+simpleName+"%' AND `id` = '" +id+ "';";
		Connection conn;
		String isF = null;
		try {
			conn = getConnection();
			Statement stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				isF = simpleInfo.getString("复测");
			}
			if (isF != null) {
				return true;
			}
			simpleInfo.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

	// 设置为复测
	private void setMethord(String num,String id, String simpleName, String table_name) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		String Fdata = sdf.format(new Date());

		String sql = "select * from `" + table_name + "`;";
		String setFSql = "update `" + table_name + "` set `复测`='F" + Fdata + "' where `样品短号` like '" + num
				+ "%' AND `检测项目` like '" + simpleName + "%' AND `id`= '"+id+"';";

		Statement stmt;
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(setFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 取消设置为复测
	private void deleteMethord(String num,String id, String simpleName, String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String deleteFSql = "update `" + table_name + "` set `复测`=null where `样品短号` like '" + num
				+ "%' AND  `检测项目` like '" + simpleName + "%' AND `id`= '"+id+"';";

		Statement stmt;
		try {
			Connection conn = getConnection();
			PreparedStatement pstmt = conn.prepareStatement(deleteFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	private void addFtoTodayDate(String num,String id, String simpleName, String table_name_beforeday) {
//		String table_name = SampleTaskTable.currentYearTableName();
//		if (id != null && !id.isBlank()) {
//			String sourceTable = (table_name_beforeday == null || table_name_beforeday.isBlank()) ? table_name : table_name_beforeday;
//			String selectSql = "select * from `" + sourceTable + "` where `id` = ?;";
//			String maxIdSql = "select COALESCE(MAX(`id`), 0) from `" + table_name + "`;";
//			String maxDailyOrderSql = "select COALESCE(MAX(`当天样品序号`), 0) from `" + table_name
//					+ "` where DATE(`传入时间`) = CURDATE();";
//			String insertSql = "insert into `" + table_name
//					+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`done`,`复测`,`结果`,`位置`,`顺序`,`传入时间`,`当天样品序号`,`做样顺序`,`做样时间`) "
//					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?);";
//			try (Connection conn = getConnection();
//					PreparedStatement selectStmt = conn.prepareStatement(selectSql);
//					Statement maxIdStmt = conn.createStatement();
//					Statement maxDailyOrderStmt = conn.createStatement();
//					PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
//				selectStmt.setString(1, id);
//				ResultSet simpleInfo = selectStmt.executeQuery();
//				if (!simpleInfo.next()) {
//					return;
//				}
//
//				int maxID = 0;
//				ResultSet maxId = maxIdStmt.executeQuery(maxIdSql);
//				if (maxId.next()) {
//					maxID = maxId.getInt(1);
//				}
//
//				int dailyOrder = 0;
//				ResultSet maxDailyOrder = maxDailyOrderStmt.executeQuery(maxDailyOrderSql);
//				if (maxDailyOrder.next()) {
//					dailyOrder = maxDailyOrder.getInt(1);
//				}
//
//				insertStmt.setInt(1, maxID + 1);
//				insertStmt.setString(2, simpleInfo.getString("样品短号"));
//				insertStmt.setString(3, simpleInfo.getString("样品名称"));
//				insertStmt.setString(4, simpleInfo.getString("报告抬头"));
//				insertStmt.setString(5, simpleInfo.getString("检测项目"));
//				insertStmt.setString(6, simpleInfo.getString("检测方法"));
//				insertStmt.setString(7, simpleInfo.getString("报告单位"));
//				insertStmt.setString(8, simpleInfo.getString("备注"));
//				insertStmt.setInt(9, 0);
//				insertStmt.setString(10, "BF");
//				insertStmt.setString(11, null);
//				insertStmt.setString(12, null);
//				insertStmt.setInt(13, 0);
//				insertStmt.setInt(14, dailyOrder + 1);
//				insertStmt.setObject(15, null);
//				insertStmt.setObject(16, null);
//				insertStmt.executeUpdate();
//				markSourceAsRetestCreated(conn, sourceTable, id);
//				return;
//			} catch (SQLException e) {
//				e.printStackTrace();
//				return;
//			}
//		}
//		int maxID = 0;
//		ArrayList<Object> list = new ArrayList<Object>();
//		String sql = "select * from `" + table_name_beforeday + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
//				+ simpleName + "%' AND `id` = '"+id+"';";
//		String insertSql = "insert into `" + table_name
//				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`复测`,`顺序`,`传入时间`) values(?,?,?,?,?,?,?,?,?,?,NOW());";
//		String maxIDquery = "SELECT `id` FROM `" + table_name + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + table_name
//				+ "`);";
//		Connection conn;
//		Statement stmt;
//		Statement simpleInfostmt;
//		Statement getMaxidstmt;
//		PreparedStatement pstmt;
//		try {
//			conn = getConnection();
//			simpleInfostmt = conn.createStatement();
//			getMaxidstmt = conn.createStatement();
//			stmt = conn.createStatement();
//			pstmt = conn.prepareStatement(insertSql);
//			ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
//
//			ResultSet simpleInfo = simpleInfostmt.executeQuery(sql);
//
//			if (tables.next()) {
//				ResultSet getMaxid = getMaxidstmt.executeQuery(maxIDquery);
//				while (getMaxid.next()) {
//					maxID = getMaxid.getInt("id");
//				}
//
//				pstmt.setInt(1, maxID + 1);
//				while (simpleInfo.next()) {
//					pstmt.setString(2, simpleInfo.getString("样品短号"));
//					pstmt.setString(3, simpleInfo.getString("样品名称"));
//					pstmt.setString(4, simpleInfo.getString("报告抬头"));
//					pstmt.setString(5, simpleInfo.getString("检测项目"));
//					pstmt.setString(6, simpleInfo.getString("检测方法"));
//					pstmt.setString(7, simpleInfo.getString("报告单位"));
//					pstmt.setString(8, simpleInfo.getString("备注"));
//					pstmt.setString(9, "BF");
//					pstmt.setInt(10, 0);
//					pstmt.executeUpdate();
//				}
//			} else {
//				String creattablesql = "CREATE TABLE `test`.`" + table_name + "` (\r\n" + "  `id` int NOT NULL,\r\n"
//						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
//						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
//						+ "  `检测方法` TEXT NULL,\r\n" + "  `报告单位` VARCHAR(100) NULL,\r\n"
//						+ "  `备注` TEXT NULL,\r\n" + "  `done` int NOT NULL DEFAULT 0,\r\n"
//						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n"
//						+ "  `位置` VARCHAR(20) NULL,\r\n" + "  `顺序` int NOT NULL DEFAULT 0,\r\n"
//						+ "  PRIMARY KEY (`id`));";
//				stmt.execute(creattablesql);
//				pstmt.setInt(1, 1);
//				while (simpleInfo.next()) {
//					pstmt.setString(2, simpleInfo.getString("样品短号"));
//					pstmt.setString(3, simpleInfo.getString("样品名称"));
//					pstmt.setString(4, simpleInfo.getString("报告抬头"));
//					pstmt.setString(5, simpleInfo.getString("检测项目"));
//					pstmt.setString(6, simpleInfo.getString("检测方法"));
//					pstmt.setString(7, simpleInfo.getString("报告单位"));
//					pstmt.setString(8, simpleInfo.getString("备注"));
//					pstmt.setString(9, "BF");
//					pstmt.setInt(10, 0);
//					pstmt.executeUpdate();
//				}
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//	}
	
//	private void addFtoTodayDate(String num, String id, String simpleName, String table_name_beforeday) {
//	    String table_name = SampleTaskTable.currentYearTableName();
//
//	    if (id == null || id.isBlank()) {
//	        return;
//	    }
//
//	    LocalDate queryDate = parseDateOrNull(table_name_beforeday);
//
//	    // 如果 table_name_beforeday 是 2026/04/28，就查 total_samples_2026
//	    // 如果为空，就默认查当前年度 total_samples_2026 / total_samples_2027
//	    String sourceTable = getTotalSamplesTableName(queryDate);
//
//	    String selectSql;
//	    if (queryDate != null) {
//	        selectSql = "select * from `" + sourceTable + "` where `id` = ? and DATE(`传入时间`) = ?;";
//	    } else {
//	        selectSql = "select * from `" + sourceTable + "` where `id` = ?;";
//	    }
//
//	    String maxIdSql = "select COALESCE(MAX(`id`), 0) from `" + table_name + "`;";
//
//	    String maxDailyOrderSql = "select COALESCE(MAX(`当天样品序号`), 0) from `" + table_name
//	            + "` where DATE(`传入时间`) = CURDATE();";
//
//	    String insertSql = "insert into `" + table_name
//	            + "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`done`,`复测`,`结果`,`位置`,`顺序`,`传入时间`,`当天样品序号`,`做样顺序`,`做样时间`) "
//	            + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?);";
//
//	    try (Connection conn = getConnection();
//	         PreparedStatement selectStmt = conn.prepareStatement(selectSql);
//	         Statement maxIdStmt = conn.createStatement();
//	         Statement maxDailyOrderStmt = conn.createStatement();
//	         PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
//
//	        selectStmt.setString(1, id);
//
//	        if (queryDate != null) {
//	            selectStmt.setDate(2, java.sql.Date.valueOf(queryDate));
//	        }
//
//	        ResultSet simpleInfo = selectStmt.executeQuery();
//
//	        if (!simpleInfo.next()) {
//	            System.out.println("未找到复测来源数据，sourceTable=" + sourceTable
//	                    + ", id=" + id
//	                    + ", queryDate=" + queryDate);
//	            return;
//	        }
//
//	        int maxID = 0;
//	        ResultSet maxId = maxIdStmt.executeQuery(maxIdSql);
//	        if (maxId.next()) {
//	            maxID = maxId.getInt(1);
//	        }
//
//	        int dailyOrder = 0;
//	        ResultSet maxDailyOrder = maxDailyOrderStmt.executeQuery(maxDailyOrderSql);
//	        if (maxDailyOrder.next()) {
//	            dailyOrder = maxDailyOrder.getInt(1);
//	        }
//
//	        insertStmt.setInt(1, maxID + 1);
//	        insertStmt.setString(2, simpleInfo.getString("样品短号"));
//	        insertStmt.setString(3, simpleInfo.getString("样品名称"));
//	        insertStmt.setString(4, simpleInfo.getString("报告抬头"));
//	        insertStmt.setString(5, simpleInfo.getString("检测项目"));
//	        insertStmt.setString(6, simpleInfo.getString("检测方法"));
//	        insertStmt.setString(7, simpleInfo.getString("报告单位"));
//	        insertStmt.setString(8, simpleInfo.getString("备注"));
//	        insertStmt.setInt(9, 0);
//	        insertStmt.setString(10, "BF");
//	        insertStmt.setString(11, null);
//	        insertStmt.setString(12, null);
//	        insertStmt.setInt(13, 0);
//	        insertStmt.setInt(14, dailyOrder + 1);
//	        insertStmt.setObject(15, null);
//	        insertStmt.setObject(16, null);
//
//	        insertStmt.executeUpdate();
//
//	        markSourceAsRetestCreated(conn, sourceTable, id);
//
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	    }
//	}
	
	private void addFtoTodayDate(String num, String id, String simpleName, String table_name_beforeday) {
	    String targetTable = SampleTaskTable.currentYearTableName();

	    if (id == null || id.isBlank()) {
	        System.out.println("添加复测失败：id 为空");
	        return;
	    }

	    LocalDate queryDate = parseDateOrNull(table_name_beforeday);
	    String sourceTable = getTotalSamplesTableName(queryDate);

	    String selectSql;
	    if (queryDate != null) {
	        selectSql = "select * from `" + sourceTable + "` where `id` = ? and DATE(`传入时间`) = ?;";
	    } else {
	        selectSql = "select * from `" + sourceTable + "` where `id` = ?;";
	    }

	    String maxIdSql = "select COALESCE(MAX(`id`), 0) from `" + targetTable + "`;";

	    String maxDailyOrderSql = "select COALESCE(MAX(`当天样品序号`), 0) from `" + targetTable
	            + "` where DATE(`传入时间`) = CURDATE();";

	    String insertSql = "insert into `" + targetTable
	            + "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`done`,`复测`,`结果`,`位置`,`顺序`,`传入时间`,`当天样品序号`,`做样顺序`,`做样时间`) "
	            + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,NOW(),?,?,?);";

	    try (Connection conn = getConnection();
	         Statement maxIdStmt = conn.createStatement();
	         Statement maxDailyOrderStmt = conn.createStatement();
	         PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

	        ResultSet simpleInfo = null;
	        PreparedStatement selectStmt = null;
	        String realSourceTable = sourceTable;

	        try {
	            selectStmt = conn.prepareStatement(selectSql);
	            selectStmt.setString(1, id);

	            if (queryDate != null) {
	                selectStmt.setDate(2, java.sql.Date.valueOf(queryDate));
	            }

	            simpleInfo = selectStmt.executeQuery();

	            // 1. 先查 total_samples_2026
	            if (!simpleInfo.next()) {
	                closeQuietly(simpleInfo);
	                closeQuietly(selectStmt);

	                // 2. 如果年度总表查不到，再回退查旧表，例如 `2026/04/27`
	                if (table_name_beforeday == null || table_name_beforeday.isBlank()) {
	                    System.out.println("年度总表未找到复测来源数据，sourceTable=" + sourceTable
	                            + ", id=" + id
	                            + ", queryDate=" + queryDate);
	                    return;
	                }

	                String oldTable = table_name_beforeday.trim();

	                String oldSelectSql = "select * from `" + oldTable + "` where `id` = ?;";

	                selectStmt = conn.prepareStatement(oldSelectSql);
	                selectStmt.setString(1, id);
	                simpleInfo = selectStmt.executeQuery();

	                if (!simpleInfo.next()) {
	                    System.out.println("年度总表和旧日期表都未找到复测来源数据，newTable=" + sourceTable
	                            + ", oldTable=" + oldTable
	                            + ", id=" + id
	                            + ", queryDate=" + queryDate);
	                    return;
	                }

	                realSourceTable = oldTable;

	                System.out.println("年度总表未找到，已从旧日期表找到复测来源数据：oldTable=" + oldTable
	                        + ", id=" + id);
	            }

	            int maxID = 0;
	            ResultSet maxId = maxIdStmt.executeQuery(maxIdSql);
	            if (maxId.next()) {
	                maxID = maxId.getInt(1);
	            }
	            closeQuietly(maxId);

	            int dailyOrder = 0;
	            ResultSet maxDailyOrder = maxDailyOrderStmt.executeQuery(maxDailyOrderSql);
	            if (maxDailyOrder.next()) {
	                dailyOrder = maxDailyOrder.getInt(1);
	            }
	            closeQuietly(maxDailyOrder);

	            insertStmt.setInt(1, maxID + 1);
	            insertStmt.setString(2, simpleInfo.getString("样品短号"));
	            insertStmt.setString(3, simpleInfo.getString("样品名称"));
	            insertStmt.setString(4, simpleInfo.getString("报告抬头"));
	            insertStmt.setString(5, simpleInfo.getString("检测项目"));
	            insertStmt.setString(6, simpleInfo.getString("检测方法"));
	            insertStmt.setString(7, simpleInfo.getString("报告单位"));
	            insertStmt.setString(8, simpleInfo.getString("备注"));
	            insertStmt.setInt(9, 0);
	            insertStmt.setString(10, "BF");
	            insertStmt.setString(11, null);
	            insertStmt.setString(12, null);
	            insertStmt.setInt(13, 0);
	            insertStmt.setInt(14, dailyOrder + 1);
	            insertStmt.setObject(15, null);
	            insertStmt.setObject(16, null);

	            insertStmt.executeUpdate();

	            markSourceAsRetestCreated(conn, realSourceTable, id);

	            System.out.println("添加复测成功，targetTable=" + targetTable
	                    + ", sourceTable=" + realSourceTable
	                    + ", id=" + id
	                    + ", 样品短号=" + simpleInfo.getString("样品短号"));

	        } finally {
	            closeQuietly(simpleInfo);
	            closeQuietly(selectStmt);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	private void closeQuietly(ResultSet rs) {
	    if (rs != null) {
	        try {
	            rs.close();
	        } catch (SQLException ignored) {
	        }
	    }
	}

	private void closeQuietly(Statement stmt) {
	    if (stmt != null) {
	        try {
	            stmt.close();
	        } catch (SQLException ignored) {
	        }
	    }
	}
	
	private LocalDate parseDateOrNull(String value) {
	    if (value == null || value.isBlank()) {
	        return null;
	    }

	    String text = value.trim();

	    List<DateTimeFormatter> formatters = List.of(
	            DateTimeFormatter.ofPattern("yyyy/MM/dd"),
	            DateTimeFormatter.ofPattern("yyyy/M/d"),
	            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
	            DateTimeFormatter.ofPattern("yyyy-M-d"),
	            DateTimeFormatter.ofPattern("yyyy.MM.dd"),
	            DateTimeFormatter.ofPattern("yyyy.M.d")
	    );

	    for (DateTimeFormatter formatter : formatters) {
	        try {
	            return LocalDate.parse(text, formatter);
	        } catch (Exception ignored) {
	        }
	    }

	    return null;
	}

	private String getTotalSamplesTableName(LocalDate date) {
	    int year = date == null ? LocalDate.now().getYear() : date.getYear();
	    return "total_samples_" + year;
	}

	private void deleteFFromeTodayDate(String num, String simpleName) {
		String table_name = SampleTaskTable.currentYearTableName();
		String deletesql = "DELETE FROM `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ simpleName + "%';";
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
	}

	private void markSourceAsRetestCreated(Connection conn, String sourceTable, String sourceId) throws SQLException {
		if (sourceTable == null || sourceTable.isBlank() || sourceId == null || sourceId.isBlank()) {
			return;
		}
		String updateSql = "update `" + sourceTable + "` set `复测` = CASE "
				+ "WHEN `复测` IS NULL OR TRIM(`复测`) = '' THEN 'C' "
				+ "WHEN FIND_IN_SET('C', REPLACE(`复测`, '，', ',')) > 0 THEN `复测` "
				+ "ELSE CONCAT(`复测`, ',C') END where `id` = ?;";
		try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
			updateStmt.setString(1, sourceId);
			updateStmt.executeUpdate();
		}
	}

	public void setSelomenToFlabel(String simpleNum, String id, String table_name, Model model) {

		addFtoTodayDate(simpleNum, id, "", table_name);

	}

//获取数据库
	private Connection getselomenConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/selomen?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}

//判断是否是复测
	private boolean isFselomensimple(String simpleNum, String table_name) {
		String sql = "select `复测` from `" + table_name + "` where `样品短号` like '" + simpleNum + "%' ;";
		Connection conn;
		String isF = null;
		try {
			conn = getselomenConnection();
			Statement stmt = conn.createStatement();
			ResultSet simpleInfo = stmt.executeQuery(sql);
			while (simpleInfo.next()) {
				isF = simpleInfo.getString("复测");
			}
			if (isF != null) {
				return true;
			}
			simpleInfo.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;

	}

//设置为复测
	private void setselomenMethord(String num, String table_name) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String Fdata = sdf.format(new Date());

		String sql = "select * from `" + table_name + "`;";
		String setFSql = "update `" + table_name + "` set `复测`='F" + Fdata + "' where `样品短号` like '" + num + "%';";

		Statement stmt;
		try {
			Connection conn = getselomenConnection();
			PreparedStatement pstmt = conn.prepareStatement(setFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//取消设置为复测
	private void deleteselomenMethord(String num, String table_name) {

		String sql = "select * from `" + table_name + "`;";

		String deleteFSql = "update `" + table_name + "` set `复测`=null where `样品短号` like '" + num + "%';";

		Statement stmt;
		try {
			Connection conn = getselomenConnection();
			PreparedStatement pstmt = conn.prepareStatement(deleteFSql);
			stmt = conn.createStatement();
			stmt.getConnection().prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addFselomenToTable(String num, String table_name) {
		String todayDate = SampleTaskTable.currentYearTableName();
		int maxID = 0;
		ArrayList<Object> list = new ArrayList<Object>();
		String sql = "select * from `" + table_name + "` where `样品短号` like '" + num + "%';";
		String insertSql = "insert into `" + todayDate
				+ "`(`id`,`样品短号`,`样品名称`,`报告抬头`,`检测项目`,`检测方法`,`报告单位`,`备注`,`复测`,`顺序`,`传入时间`) values(?,?,?,?,?,?,?,?,?,?,NOW());";
		String maxIDquery = "SELECT `id` FROM `" + todayDate + "` WHERE `id` = (SELECT MAX(`id`) FROM `" + todayDate
				+ "`);";
		Connection conn;
		Connection selomenconn;
		Statement stmt;
		Statement simpleInfostmt;
		Statement getMaxidstmt;
		PreparedStatement pstmt;
		try {
			conn = getConnection();
			selomenconn = getselomenConnection();
			simpleInfostmt = selomenconn.createStatement();
			getMaxidstmt = conn.createStatement();
			stmt = conn.createStatement();
			pstmt = conn.prepareStatement(insertSql);
			ResultSet tables = conn.getMetaData().getTables(null, null, todayDate, null);

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
					pstmt.setString(5, "沙门氏菌");
					pstmt.setString(6, "/");
					pstmt.setString(7, "/");
					pstmt.setString(8, "/");
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			} else {
				String creattablesql = "CREATE TABLE `test`.`" + todayDate + "` (\r\n" + "  `id` int NOT NULL,\r\n"
						+ "  `样品短号` VARCHAR(100) NOT NULL,\r\n" + "  `样品名称` VARCHAR(100) NULL,\r\n"
						+ "  `报告抬头` VARCHAR(100) NULL,\r\n" + "  `检测项目` VARCHAR(100) NULL,\r\n"
						+ "  `检测方法` VARCHAR(100) NULL,\r\n" + "  `报告单位` VARCHAR(100) NULL,\r\n"
						+ "  `备注` TEXT NULL,\r\n" + "  `done` int NOT NULL DEFAULT 0,\r\n"
						+ "  `复测` VARCHAR(20) NULL,\r\n" + "  `结果` VARCHAR(100) NULL,\r\n"
						+ "  `位置` VARCHAR(20) NULL,\r\n" + "  `顺序` int NOT NULL,\r\n" + "  PRIMARY KEY (`id`));";
				stmt.execute(creattablesql);
				pstmt.setInt(1, 1);
				while (simpleInfo.next()) {
					pstmt.setString(2, simpleInfo.getString("样品短号"));
					pstmt.setString(3, simpleInfo.getString("样品名称"));
					pstmt.setString(4, simpleInfo.getString("报告抬头"));
					pstmt.setString(5, "沙门氏菌");
					pstmt.setString(6, "/");
					pstmt.setString(7, "/");
					pstmt.setString(8, "/");
					pstmt.setString(9, "BF");
					pstmt.setInt(10, 0);
					pstmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void deleteFSelomen(String num) {
		String table_name = SampleTaskTable.currentYearTableName();
		String deletesql = "DELETE FROM `" + table_name + "` where `样品短号` like '" + num + "%' AND `检测项目` like '"
				+ "沙门氏菌" + "%';";
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
	}
}
