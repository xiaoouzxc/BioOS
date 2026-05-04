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
import java.util.HashSet;

import com.test.service.SampleTaskTable;

public class GetSimpleList {
	private ArrayList<Simple> simpleList = new ArrayList<Simple>();

	public ArrayList<Simple> getLabelList(Integer batch) {
		simpleList.clear();
		String table_name = SampleTaskTable.currentYearTableName();
		//String sql = "select * from `" + table_name + "` where `done`=0;";
		String sql = "select * from `" + table_name + "` where DATE(`传入时间`) = CURDATE();";

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
				if (simpleInfo.getString("复测") == null && (batch == null || simpleInfo.getInt("顺序") == batch)) {
					Simple simple = new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
							simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getInt("done"),
							simpleInfo.getString("报告单位"));
					simple.setDailySampleOrder(getNullableInt(simpleInfo, "当天样品序号"));
					simpleList.add(simple);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}

	public ArrayList<Simple> getFLabelList() {
		simpleList.clear();
		String table_name = SampleTaskTable.currentYearTableName();
		String sql = "select * from `" + table_name + "` where DATE(`传入时间`) = CURDATE();";

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
					Simple simple = new Simple( simpleInfo.getInt("id"),
							simpleInfo.getString("样品短号"), 
							simpleInfo.getString("样品名称"),
							simpleInfo.getString("报告抬头"),							
							simpleInfo.getString("检测项目"),					
							simpleInfo.getString("检测方法"), 
							simpleInfo.getString("报告单位"),
							simpleInfo.getString("备注"),
							simpleInfo.getInt("done"),
							simpleInfo.getString("复测") 
							);
					simple.setDailySampleOrder(getNullableInt(simpleInfo, "当天样品序号"));
					simpleList.add(simple);
				}
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}
	public ArrayList<Simple> getSelomenLabelList(String table_name, String checkItem) {
		return getSelomenLabelList(table_name, checkItem, null);
	}

	public ArrayList<Simple> getSelomenLabelList(String table_name, String checkItem, String position) {
	    String sampleTableName = SampleTaskTable.currentYearTableName();
	    String dateCondition = "CURDATE()";
	    String positionCondition = position == null || position.isBlank() ? "" : " and `位置` = '" + position + "'";
	    if (table_name != null && table_name.contains(" ")) {
	    	String[] tableNameParts = table_name.split(" ");
	    	if (tableNameParts.length > 1) {
	    		if (!"选择日期".equals(tableNameParts[0])) {
	    			positionCondition = " and `位置` = '" + tableNameParts[0] + "'";
	    		}
	    		dateCondition = "'" + tableNameParts[1].replace("/", "-") + "'";
	    	}
	    } else if (table_name != null && table_name.matches("\\d{4}/\\d{2}/\\d{2}")) {
	    	dateCondition = "'" + table_name.replace("/", "-") + "'";
	    } else if (table_name != null && table_name.matches("\\d{4}-\\d{2}-\\d{2}")) {
	    	dateCondition = "'" + table_name + "'";
	    }
	    String sql = "select `样品短号`, `复测`, `当天样品序号`, `做样顺序`, min(`id`) as first_id from `" + sampleTableName + "` "
	    		+ "where DATE(`传入时间`) = " + dateCondition + positionCondition + " and `done`=1 and `检测项目` like '%" + checkItem + "%' "
	    		+ "group by `样品短号`, `复测`, `当天样品序号`, `做样顺序` order by `做样顺序` IS NULL, `做样顺序`, first_id;";
	    
	    String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	    String name = "root";
	    String password = "1234";
	    
	    ArrayList<Simple> simpleList = new ArrayList<>(); // 请确保在方法中初始化 simpleList

	    try {
	        Connection conn = DriverManager.getConnection(url, name, password);
	        Statement stmt = conn.createStatement();
	        
	        ResultSet simpleInfo = stmt.executeQuery(sql);
	        while (simpleInfo.next()) {
                Simple simple = new Simple(
                    simpleInfo.getInt("first_id"),
                    simpleInfo.getString("样品短号"),
                    simpleInfo.getString("复测")
                );
                simple.setDailySampleOrder(getNullableInt(simpleInfo, "当天样品序号"));
                simpleList.add(simple);
	        }
	        
	        // 关闭资源
	        simpleInfo.close();
	        stmt.close();
	        conn.close();
	        
	    } catch (SQLException e) {
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
//			while (simpleInfo.next()) {
//			if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
//					&& !simpleInfo.getString("检测项目").contains("致泻")
//					&& !simpleInfo.getString("检测项目").contains("O157")
//					&& !simpleInfo.getString("检测项目").contains("商业无菌")
//					&& !simpleInfo.getString("检测项目").contains("微生物")
//					&& !simpleInfo.getString("检测项目").contains("副溶血")) {
//				simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
//						simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
//						simpleInfo.getInt("id")));
//
//			} else if (simpleInfo.getString("检测项目").contains("微生物") && simpleInfo.getString("检测项目").contains("霉")) {
//				simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
//						simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
//						simpleInfo.getInt("id")));
//
//			}
//		}
		while (simpleInfo.next()) {				
				simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
						simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
						simpleInfo.getInt("id")));

				
			 
		}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return simpleList;

	}
//	public ArrayList<Simple> getSZList(String table_name) {
//		simpleList.clear();
//
//		String sampleDate = table_name == null || table_name.trim().isEmpty()
//				? LocalDate.now(ZoneId.of("Asia/Shanghai")).toString()
//				: table_name.trim().replace("/", "-");
//		String sampleTableName = sampleDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}")
//				? "total_samples_" + sampleDate.substring(0, 4)
//				: SampleTaskTable.currentYearTableName();
//		String sql = "select * from `" + sampleTableName
//				+ "` where DATE(`传入时间`) = ? and `检测项目` is not null and `报告单位` is not null and `结果` is not null;";
//
//		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
//		String name = "root";
//		String password = "1234";
//		Connection conn;
//
//		try {
//			conn = DriverManager.getConnection(url, name, password);
//			PreparedStatement stmt = conn.prepareStatement(sql);
//			ResultSet tables = conn.getMetaData().getTables(null, null, sampleTableName, null);
//			if (!tables.next()) {
//				
//				return simpleList;
//			}
//			stmt.setString(1, sampleDate);
//			ResultSet simpleInfo = stmt.executeQuery();
//			while (simpleInfo.next()) {
//				 if(simpleInfo.getString("检测项目").contains("肠")) {
//					 System.out.println("dachang");
//				 }else {
//					 if (simpleInfo.getString("报告单位").contains("CFU")||simpleInfo.getString("报告单位").contains("cfu")) {
//							
//							if(simpleInfo.getString("结果")!=null&&simpleInfo.getString("结果").length()>20) {
//								simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
//										simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"), simpleInfo.getString("结果"),
//										simpleInfo.getInt("id")));
//							}	
//
//						} 
//				 }
//				
//			}
//
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return simpleList;
//
//	}
	
	public ArrayList<Simple> getSZList(String table_name) {
	    simpleList.clear();

	    String sampleDate = normalizeDateToDash(table_name);
	    String oldTableName = normalizeDateToSlash(sampleDate);

	    String sampleTableName = sampleDate.matches("\\d{4}-\\d{1,2}-\\d{1,2}")
	            ? "total_samples_" + sampleDate.substring(0, 4)
	            : SampleTaskTable.currentYearTableName();

	    String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	    String name = "root";
	    String password = "1234";

	    try (Connection conn = DriverManager.getConnection(url, name, password)) {

	        // 1. 先查新年度总表 total_samples_2026
	        if (tableExists(conn, sampleTableName)) {
	            String newSql = "select * from `" + sampleTableName + "` " +
	                    "where DATE(`传入时间`) = ? " +
	                    "and `检测项目` is not null " +
	                    "and `报告单位` is not null " +
	                    "and `结果` is not null;";

	            try (PreparedStatement stmt = conn.prepareStatement(newSql)) {
	                stmt.setString(1, sampleDate);

	                try (ResultSet simpleInfo = stmt.executeQuery()) {
	                    while (simpleInfo.next()) {
	                        addSimpleIfMatch(simpleInfo);
	                    }
	                }
	            }

	            // 关键：如果新总表已经查到数据，就直接返回
	            if (!simpleList.isEmpty()) {
	                return simpleList;
	            }
	        }

	        // 2. 新年度总表不存在，或者当天没有数据，再查旧日表 2026/04/20
	        if (tableExists(conn, oldTableName)) {
	            String oldSql = "select * from `" + oldTableName + "` " +
	                    "where `检测项目` is not null " +
	                    "and `报告单位` is not null " +
	                    "and `结果` is not null;";

	            try (PreparedStatement stmt = conn.prepareStatement(oldSql);
	                 ResultSet simpleInfo = stmt.executeQuery()) {

	                while (simpleInfo.next()) {
	                    addSimpleIfMatch(simpleInfo);
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return simpleList;
	}
	
	private String normalizeDateToDash(String tableName) {
	    String raw = tableName == null || tableName.trim().isEmpty()
	            ? LocalDate.now(ZoneId.of("Asia/Shanghai")).toString()
	            : tableName.trim();

	    return raw.replace("/", "-");
	}
	
	
	
	private String normalizeDateToSlash(String tableName) {
	    String raw = tableName == null || tableName.trim().isEmpty()
	            ? LocalDate.now(ZoneId.of("Asia/Shanghai")).toString()
	            : tableName.trim();

	    return raw.replace("-", "/");
	}
	
	private boolean tableExists(Connection conn, String tableName) throws SQLException {
	    try (ResultSet tables = conn.getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
	        return tables.next();
	    }
	}
	
	private void addSimpleIfMatch(ResultSet simpleInfo) throws SQLException {
	    String testItem = simpleInfo.getString("检测项目");
	    String reportUnit = simpleInfo.getString("报告单位");
	    String result = simpleInfo.getString("结果");

	    if (testItem == null || reportUnit == null || result == null) {
	        return;
	    }

	    // 排除“肠”
	    if (testItem.contains("肠")) {
	        System.out.println("dachang");
	        return;
	    }

	    // 只要 CFU
	    if (!reportUnit.contains("CFU") && !reportUnit.contains("cfu")) {
	        return;
	    }

	    // 结果长度判断
	    if (result.length() <= 20) {
	        return;
	    }

	    simpleList.add(new Simple(
	            simpleInfo.getString("样品短号"),
	            simpleInfo.getString("样品名称"),
	            testItem,
	            simpleInfo.getString("检测方法"),
	            result,
	            simpleInfo.getInt("id")
	    ));
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

	private Integer getNullableInt(ResultSet rs, String columnName) throws SQLException {
		int value = rs.getInt(columnName);
		return rs.wasNull() ? null : value;
	}

}
