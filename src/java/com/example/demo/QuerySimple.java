package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class QuerySimple {

	private static final String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
	private static final String name = "root";
	private static final String password = "1234";

	public ArrayList<Simple> getQuerySimple(String table_name, String simplenum) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0,"未找到", "未找到", "未找到", "未找到", "未找到", "未找到", 0,"未找到","未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";
			

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0,"未找到", "未找到", "未找到", "未找到", "未找到", "未找到", 0,"未找到","未找到"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

//				while (simpleInfo.next()) {
//
//					if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
//							&& !simpleInfo.getString("检测项目").contains("致泻")
//							&& !simpleInfo.getString("检测项目").contains("O157")
//							&& !simpleInfo.getString("检测项目").contains("商业无菌")
//							&& !simpleInfo.getString("检测项目").contains("副溶血")) {
//						if (simpleInfo.getString("样品短号").contains(simplenum)) {
//							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
//									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
//									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
//									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
//							System.out.println(simpleInfo.getInt("done"));
//						}
//
//					}
//				}
				while (simpleInfo.next()) {

					
						if (simpleInfo.getString("样品短号").contains(simplenum)) {
							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("报告抬头"),
									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
							
						}

				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromname(String table_name, String simpleNam) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0,"未找到", "未找到", "未找到", "未找到", "未找到", "未找到", 0,"未找到","未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0,"未找到", "未找到", "未找到", "未找到", "未找到", "未找到", 0,"未找到","未找到"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

//				while (simpleInfo.next()) {
//
//					if (!simpleInfo.getString("检测项目").contains("沙") && !simpleInfo.getString("检测项目").contains("志贺")
//							&& !simpleInfo.getString("检测项目").contains("致泻")
//							&& !simpleInfo.getString("检测项目").contains("O157")
//							&& !simpleInfo.getString("检测项目").contains("商业无菌")
//							&& !simpleInfo.getString("检测项目").contains("副溶血")) {
//						if (simpleInfo.getString("样品名称").contains(simpleNam)) {
//							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
//									simpleInfo.getString("检测项目"), simpleInfo.getString("检测方法"),
//									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
//									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
//						}
//
//					}
//				}
				while (simpleInfo.next()) {
					
					if(simpleNam.contains("co")) {
						String name=simpleNam.substring(0,simpleNam.length()-2);
						if (simpleInfo.getString("报告抬头").contains(name)) {
							simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
									simpleInfo.getString("检测项目"), simpleInfo.getString("报告抬头"),
									"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
									simpleInfo.getString("结果"), simpleInfo.getInt("done")));
							
						}
					}else{if (simpleInfo.getString("样品名称").contains(simpleNam)) {
						simpleList.add(new Simple(simpleInfo.getString("样品短号"), simpleInfo.getString("样品名称"),
								simpleInfo.getString("检测项目"), simpleInfo.getString("报告抬头"),
								"复测信息：" + simpleInfo.getString("复测") + "做样日期：" + table_name,
								simpleInfo.getString("结果"), simpleInfo.getInt("done")));
						
					}}
						
					}



			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromYeartable(String table_name, String simplenum) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";
			String sql2 = "SHOW FULL COLUMNS FROM `" + table_name + "`";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
					return simpleList;
				}
//				ResultSet rsCols = stmt.executeQuery(sql2);
//
//				System.out.println("---- SHOW FULL COLUMNS ----");
//				while (rsCols.next()) {
//				    String field = rsCols.getString("Field");
//				    System.out.println("Field=[" + field + "] len=" + field.length());
//				}
//				System.out.println("---------------------------");
//				rsCols.close();
				
				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {

					if (simpleInfo.getString("样品短号").contains(simplenum)) {
						simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
								simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
								simpleInfo.getString("检测项目"), "复测信息：" + simpleInfo.getString("复测"),
								simpleInfo.getString("结果")));

					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}

	public ArrayList<Simple> getQuerySimplefromnamefromYeartable(String table_name, String simpleNam) {
		ArrayList<Simple> simpleList = new ArrayList<Simple>();
		if (table_name == null) {
			simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));

			return simpleList;
		} else {
			String sql = "select * from `" + table_name + "`  ;";

			Connection conn;
			try {
				conn = DriverManager.getConnection(url, name, password);
				Statement stmt = conn.createStatement();
				ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null);
				if (!tables.next()) {
					simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
					return simpleList;
				}

				ResultSet simpleInfo = stmt.executeQuery(sql);

				while (simpleInfo.next()) {
					
					if(simpleNam.contains("co")) {
						String name=simpleNam.substring(0,simpleNam.length()-2);
						//System.out.println(simpleInfo.getString("报告抬头")+"---"+name);
						if (simpleInfo.getString("报告抬头").contains(name)) {
							simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
									simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
									simpleInfo.getString("检测项目"), "复测信息：" + simpleInfo.getString("复测"),
									simpleInfo.getString("结果")));
					}

					

					}else {
						if (simpleInfo.getString("样品名称").contains(simpleNam)) {
							simpleList.add(new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
									simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"),
									simpleInfo.getString("检测项目"), "复测信息：" + simpleInfo.getString("复测"),
									simpleInfo.getString("结果")));
					}
					}
				}

			} catch (SQLException e) {

				e.printStackTrace();
			}

			return simpleList;
		}

	}
	
	public ArrayList<Simple> getQuerySimpletoComparefromYeartable(
	        String table_name,
	        String simpleNam,
	        String company,
	        String testItem
	) {
	    ArrayList<Simple> simpleList = new ArrayList<>();

	    if (table_name == null || table_name.trim().isEmpty()) {
	        simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
	        return simpleList;
	    }

	    // 参数保护
	    simpleNam = (simpleNam == null) ? "" : simpleNam.trim();
	    company  = (company  == null) ? "" : company.trim();
	    testItem = (testItem == null) ? "" : testItem.trim();

	    

	    // ⚠️ 表名不能用 PreparedStatement 参数化，只能白名单/校验
	    // 最少要限制格式，避免 SQL 注入：只允许字母数字下划线
	    if (!table_name.matches("^[a-zA-Z0-9_]+$")) {
	        simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
	        return simpleList;
	    }

	    String sql;
	   
	        // 样品名称 + 报告抬头 + 检测项目 三条件
	        sql = "SELECT id, `样品短号`, `样品名称`, `报告抬头`, `检测项目`, `复测`, `结果` " +
	              "FROM `" + table_name + "` " +
	              "WHERE `样品名称` LIKE ? AND `报告抬头` LIKE ? AND `检测项目` LIKE ? ";
	    

	    try (Connection conn = DriverManager.getConnection(url, name, password)) {

	        // 表存在性检查（可保留）
	        try (ResultSet tables = conn.getMetaData().getTables(null, null, table_name, null)) {
	            if (!tables.next()) {
	                simpleList.add(new Simple(0, "未找到", "未找到", "未找到", "未找到", "未找到", "未找到"));
	                return simpleList;
	            }
	        }

	        try (PreparedStatement ps = conn.prepareStatement(sql)) {
	           
	                ps.setString(1, "%" + simpleNam + "%");
	                ps.setString(2, "%" + company + "%");
	                ps.setString(3, "%" + testItem + "%");
	            

	            try (ResultSet rs = ps.executeQuery()) {
	                while (rs.next()) {
	                    simpleList.add(new Simple(
	                            rs.getInt("id"),
	                            rs.getString("样品短号"),
	                            rs.getString("样品名称"),
	                            rs.getString("报告抬头"),
	                            rs.getString("检测项目"),
	                            "复测信息：" + rs.getString("复测"),
	                            rs.getString("结果")
	                    ));
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    // 没有结果也返回“未找到”让前端好处理（可选）
	    if (simpleList.isEmpty()) {
	    	String validindex=table_name+"未找到";
	        simpleList.add(new Simple(0, validindex, validindex, validindex, validindex, validindex, validindex));
	    }
	    return simpleList;
	}
}
