package com.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cti.sqlCreat.CreatConnection;
import com.example.demo.Simple;
import com.example.demo.getDBtestName;

@Service
public class StartTestingController {

    private CreatConnection cct = new CreatConnection();
    private CreatConnection cct2 = new CreatConnection();
  
    private ArrayList<String> tableNameList = new ArrayList<String>();
    

    // 修改方法签名，接受可选的批次参数
    public String result( Integer batch, Model model,String position) throws SQLException {
        // 获取批次任务表名称列表
        tableNameList = getTotalSampleDateList();
        model.addAttribute("nameList", tableNameList);

        String table_name = SampleTaskTable.currentYearTableName();
        String todayCondition = "DATE(`传入时间`) = CURDATE()";
        boolean canOperate = position != null && hasTodayClaimedSamples(table_name, position);
        //ResultSet simpleInfo=null;
        
        // 根据是否有 batch 参数决定查询哪条SQL
        String sql= null;
        String setTestlocate = null;
         if (batch != null&&position!=null) {
        	
        	setTestlocate="update `" + table_name + "` set `位置`='"+position+"'  where `顺序`=" + batch + " and `位置` IS null and " + todayCondition + ";";
        	Connection conn=CreatConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(setTestlocate);
			
			pstmt.executeUpdate();
			conn.close();
            canOperate = hasTodayClaimedSamples(table_name, position);
            sql = "select * from `" + table_name + "` where `位置`='" + position + "' and `done`=0 and " + todayCondition + ";";
        } else {
            if (canOperate) {
                sql = "select * from `" + table_name + "` where `位置`='" + position + "' and `done`=0 and " + todayCondition + ";";
            } else {
                sql = "select * from `" + table_name + "` where `done`=0 and " + todayCondition + ";";
            }
        }
        String doneSql = "update `" + table_name + "` set `done`='T';";
        List<Simple> simpleList = new ArrayList<Simple>();
        ArrayList<Simple> presentList = new ArrayList<Simple>();

        try {
           
            
			 ResultSet simpleInfo = cct.testSet(sql, table_name);
            if (simpleInfo == null) {
                return "noListError.html";
            }

            // 原有逻辑：遍历 simpleInfo，将待处理的任务添加到 simpleList
            while (simpleInfo.next()) {
                Simple simple = new Simple(
                    simpleInfo.getInt("id"),
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
                simple.setDailySampleOrder(simpleInfo.getInt("当天样品序号"));
                simpleList.add(simple);
                
            }
            // 作为分组终止标记，便于后续分组处理
            simpleList.add(new Simple(0, "/", "/", "/", "/", "/", "/", "/", 0, "/"));
            
            // 根据 simpleList 生成 presentList（原有逻辑）
            int id = 0;
            String testItem = "";
            String testMethord = "";
            String unit = "";
            for (int i = 0; i < simpleList.size(); i++) {
                if (i + 1 == simpleList.size()) {
                    break;
                }
                if (simpleList.get(i).getIfF() == null) {
                    if (simpleList.get(i).getNumber().compareTo(simpleList.get(i + 1).getNumber()) == 0) {
                        if(simpleList.get(i).getTestItem().compareTo(simpleList.get(i + 1).getTestItem()) != 0){
                            testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
                            testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------" + "\r\n";
                            unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
                        } 
                    } else {
                        id++;
                        testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
                        testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------" + "\r\n";
                        unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
                        Simple presentSimple = new Simple(id,
                                simpleList.get(i).getNumber(),
                                simpleList.get(i).getName(),
                                simpleList.get(i).getCompany(),
                                testItem,
                                testMethord,
                                unit,
                                simpleList.get(i).getTip(),
                                simpleList.get(i).getDone(),
                                simpleList.get(i).getIfF());
                        presentSimple.setDailySampleOrder(simpleList.get(i).getDailySampleOrder());
                        presentList.add(presentSimple);
                        testItem = "";
                        testMethord = "";
                        unit = "";
                    }
                } else {
                    id++;
                    Simple presentSimple = new Simple(id,
                            simpleList.get(i).getNumber(),
                            simpleList.get(i).getName(),
                            simpleList.get(i).getCompany(),
                            simpleList.get(i).getTestItem(),
                            simpleList.get(i).getTestMethod(),
                            simpleList.get(i).getUnit(),
                            simpleList.get(i).getTip(),
                            simpleList.get(i).getDone(),
                            simpleList.get(i).getIfF());
                    presentSimple.setDailySampleOrder(simpleList.get(i).getDailySampleOrder());
                    presentList.add(presentSimple);
                }
            }

            cct.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("presentList", presentList);
        model.addAttribute("canOperate", canOperate);
        model.addAttribute("currentUser", position == null ? "" : position);

        // 查询可领取的批次列表：例如查询当前表中 distinct 顺序值
        List<Integer> batchList = new ArrayList<>();
        try {
            String batchSql = "select distinct `顺序` from `" + table_name + "` where `位置` IS null and " + todayCondition + " order by `顺序` asc;";
            Statement batchStmt = CreatConnection.getConnection().createStatement();
            ResultSet batchRS = batchStmt.executeQuery(batchSql);
            while (batchRS.next()) {
                batchList.add(batchRS.getInt("顺序"));
            }
            batchRS.close();
            batchStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("batchList", batchList);
        
     // 查询可领取的批次列表：例如查询当前表中 distinct 顺序值
        List<Integer> allbatchList = new ArrayList<>();
        try {
            //String allbatchSql = "select distinct `顺序` from `" + table_name + "` where `位置`='" + position + "'  order by `顺序` asc;";
        	String allbatchSql = "select distinct `顺序` from `" + table_name + "` where " + todayCondition + " order by `顺序` asc;";
            Statement allbatchStmt = CreatConnection.getConnection().createStatement();
            ResultSet allbatchRS = allbatchStmt.executeQuery(allbatchSql);
            while (allbatchRS.next()) {
            	allbatchList.add(allbatchRS.getInt("顺序"));
            }
            allbatchRS.close();
            allbatchStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("recordPanel", allbatchList);
        
        int allSimple = 0;
        try {
            String scopeSql = canOperate ? "`位置` ='"+position+"' and " : "";
            String allSimpleSql = "select distinct `样品短号` from `" + table_name + "` where " + scopeSql + todayCondition + " order by `样品短号` asc;";
            Statement allSimpleStmt = CreatConnection.getConnection().createStatement();
            ResultSet allSimpleRS = allSimpleStmt.executeQuery(allSimpleSql);
            while (allSimpleRS.next()) {
            	
            	allSimple++;
            }
            allSimpleRS.close();
            allSimpleStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("allSimpleNum", allSimple);
        
        int doneSimple = 0;
        try {
            String scopeSql = canOperate ? "`位置` ='"+position+"' and " : "";
            String doneSimpleSql = "select distinct `样品短号` from `" + table_name + "` where " + scopeSql + "`done`='1' and " + todayCondition + " order by `样品短号` asc;";
            Statement doneSimpleStmt = CreatConnection.getConnection().createStatement();
            ResultSet doneSimpleRS = doneSimpleStmt.executeQuery(doneSimpleSql);
            while (doneSimpleRS.next()) {
            	
            	doneSimple++;
            }
            doneSimpleRS.close();
            doneSimpleStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        model.addAttribute("doneSimpleNum", doneSimple);

        return "testing";
    }

    private boolean hasTodayClaimedSamples(String tableName, String position) throws SQLException {
        String sql = "select 1 from `" + tableName + "` where `位置`='" + position + "' and DATE(`传入时间`) = CURDATE() limit 1;";
        try (Connection conn = CreatConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next();
        }
    }

    private ArrayList<String> getTotalSampleDateList() {
        ArrayList<String> dateList = new ArrayList<String>();
        String tableName = SampleTaskTable.currentYearTableName();
        String sql = "select distinct DATE_FORMAT(`传入时间`, '%Y/%m/%d') as `日期` from `" + tableName + "` order by `日期` desc;";
        try (Connection conn = CreatConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dateList.add(rs.getString("日期"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dateList;
    }
    
    public String questResult( Integer batch, Model model,String position) throws SQLException {
        // 获取批次任务表名称列表
        tableNameList = getTotalSampleDateList();
        model.addAttribute("nameList", tableNameList);

        String table_name = SampleTaskTable.currentYearTableName();
        String todayCondition = "DATE(`传入时间`) = CURDATE()";
        //ResultSet simpleInfo=null;
        
        // 根据是否有 batch 参数决定查询哪条SQL
        String sql= null;
        String setTestlocate = null;
         if (batch != null) {
        	
        	
            sql = "select * from `" + table_name + "` where `顺序`=" + batch + " and " + todayCondition + ";";
        } 
        String doneSql = "update `" + table_name + "` set `done`='T';";
        List<Simple> simpleList = new ArrayList<Simple>();
        ArrayList<Simple> presentList = new ArrayList<Simple>();

        try {
           
            
			 ResultSet simpleInfo = cct.testSet(sql, table_name);
            if (simpleInfo == null) {
                return "noListError.html";
            }

            // 原有逻辑：遍历 simpleInfo，将待处理的任务添加到 simpleList
            while (simpleInfo.next()) {
            	
            		
                        Simple simple = new Simple(
                            simpleInfo.getInt("id"),
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
                        simple.setDailySampleOrder(simpleInfo.getInt("当天样品序号"));
                        simpleList.add(simple);
                    
            	
                
            }
            // 作为分组终止标记，便于后续分组处理
            simpleList.add(new Simple(0, "/", "/", "/", "/", "/", "/", "/", 0, "/"));
            
            // 根据 simpleList 生成 presentList（原有逻辑）
            int id = 0;
            String testItem = "";
            String testMethord = "";
            String unit = "";
            for (int i = 0; i < simpleList.size(); i++) {
                if (i + 1 == simpleList.size()) {
                    break;
                }
                if (simpleList.get(i).getIfF() == null) {
                    if (simpleList.get(i).getNumber().compareTo(simpleList.get(i + 1).getNumber()) == 0) {
                        if(simpleList.get(i).getTestItem().compareTo(simpleList.get(i + 1).getTestItem()) != 0){
                            testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
                            testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------" + "\r\n";
                            unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
                        } 
                    } else {
                        id++;
                        testItem = testItem + simpleList.get(i).getTestItem() + "\r\n" + "-----" + "\r\n";
                        testMethord = testMethord + simpleList.get(i).getTestMethod() + "\r\n" + "------------------------" + "\r\n";
                        unit = unit + simpleList.get(i).getUnit() + "\r\n" + "---" + "\r\n";
                        Simple presentSimple = new Simple(id,
                                simpleList.get(i).getNumber(),
                                simpleList.get(i).getName(),
                                simpleList.get(i).getCompany(),
                                testItem,
                                testMethord,
                                unit,
                                simpleList.get(i).getTip(),
                                simpleList.get(i).getDone(),
                                simpleList.get(i).getIfF());
                        presentSimple.setDailySampleOrder(simpleList.get(i).getDailySampleOrder());
                        presentList.add(presentSimple);
                        testItem = "";
                        testMethord = "";
                        unit = "";
                    }
                } else {
                    id++;
                    Simple presentSimple = new Simple(id,
                            simpleList.get(i).getNumber(),
                            simpleList.get(i).getName(),
                            simpleList.get(i).getCompany(),
                            simpleList.get(i).getTestItem(),
                            simpleList.get(i).getTestMethod(),
                            simpleList.get(i).getUnit(),
                            simpleList.get(i).getTip(),
                            simpleList.get(i).getDone(),
                            simpleList.get(i).getIfF());
                    presentSimple.setDailySampleOrder(simpleList.get(i).getDailySampleOrder());
                    presentList.add(presentSimple);
                }
            }

            cct.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        model.addAttribute("presentList", presentList);

        

        return "testing";
    }
}
