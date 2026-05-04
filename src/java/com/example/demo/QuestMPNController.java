package com.example.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cti.sqlCreat.CreatConnection;
import com.test.service.SampleTaskTable;
import com.test.service.XmlService;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class QuestMPNController {
	@Autowired
	private XmlService xmlService;
	 
	
	@GetMapping("questMPN")
	public String result(Model model,HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		 String position=(String) session.getAttribute("username");
		 String table_name = SampleTaskTable.currentYearTableName();
		 String todayCondition = "DATE(`传入时间`) = CURDATE()";
		 boolean canOperate = false;
		 try {
			 canOperate = position != null && hasTodayClaimedSamples(table_name, position);
		 } catch (SQLException e) {
			 e.printStackTrace();
		 }
		
		String scopeSql = canOperate ? "`位置` = '" + position + "' and " : "";
		String sql = "SELECT * FROM `" + table_name + "` where " + scopeSql + todayCondition + ";";
		String doneSql = "update `" + table_name + "` set `done`='T';";
		List<Simple> simpleList = new ArrayList<Simple>();
		ArrayList<Simple> presentList = new ArrayList<Simple>();
		List<Standard> standards = xmlService.readXmlData();

		
		Connection conn;
		try {
			conn = getConnection();
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
				if (isMpnByStandard(simpleInfo.getString("检测方法"), simpleInfo.getString("检测项目"), standards)) {
					// if(simpleInfo.getInt("done")==0) {
					Simple simple = new Simple(simpleInfo.getInt("id"), simpleInfo.getString("样品短号"),
							simpleInfo.getString("样品名称"), simpleInfo.getString("报告抬头"), simpleInfo.getString("检测项目"),
							simpleInfo.getString("检测方法"), simpleInfo.getString("报告单位"), simpleInfo.getString("备注"),
							simpleInfo.getInt("done"), simpleInfo.getString("复测"));
					simple.setDailySampleOrder(simpleInfo.getInt("当天样品序号"));
					simpleList.add(simple);
					// }
				}
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

		return "questMPN";

	}
	
	// 返回可用批次数量或列表
    @GetMapping("batches")
    @ResponseBody
    public List<Integer> getBatchList() {
    	String table_name = SampleTaskTable.currentYearTableName();
        // 假设你从数据库里查询 distinct 顺序 列表
        List<Integer> batchList = new ArrayList<>();
        String table =table_name /* 你当前的 table_name */;
        String sql = "SELECT DISTINCT `顺序` FROM `" + table + "` WHERE DATE(`传入时间`) = CURDATE() ORDER BY `顺序`";
        try (Connection c = getConnection();
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            while (rs.next()) {
                batchList.add(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return batchList;
    }

    // 根据批次返回 simpleList
    @GetMapping("simples")
    @ResponseBody
    public List<Simple> getSimpleList(@RequestParam int batch) {
    	String table_name = SampleTaskTable.currentYearTableName();
        List<Simple> list = new ArrayList<>();
        List<Standard> standards = xmlService.readXmlData();
        String table =table_name /* 你当前的 table_name */;
        String sql = "SELECT * FROM `" + table + "` WHERE `顺序` = ? AND DATE(`传入时间`) = CURDATE()";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, batch);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Simple s = new Simple();
                    if (isMpnByStandard(rs.getString("检测方法"), rs.getString("检测项目"), standards)) {
                    	 s.setId(rs.getInt("id"));
                         s.setNumber(rs.getString("样品短号"));
                         s.setCompany(rs.getString("报告抬头"));
                         s.setName(rs.getString("样品名称"));
                         s.setUnit(rs.getString("报告单位"));
                         s.setTestItem(rs.getString("检测项目"));
                         s.setTestMethod(rs.getString("检测方法")); 
                         s.setTip(rs.getString("备注"));
                         s.setDone(rs.getInt("done"));
                         s.setIfF(rs.getString("复测"));
                         s.setDailySampleOrder(rs.getInt("当天样品序号"));
                         list.add(s);
    				}
                   
                    
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private Connection getConnection() throws SQLException {
    	String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
    	
		return DriverManager.getConnection(url, name, password);
    	
    }

    private boolean hasTodayClaimedSamples(String tableName, String position) throws SQLException {
    	String sql = "select 1 from `" + tableName + "` where `位置`='" + position + "' and DATE(`传入时间`) = CURDATE() limit 1;";
    	try (Connection c = getConnection();
    		 Statement s = c.createStatement();
    		 ResultSet rs = s.executeQuery(sql)) {
    		return rs.next();
    	}
    }

    private boolean isMpnByStandard(String testMethod, String testItem, List<Standard> standards) {
    	if (testMethod == null || testItem == null || standards == null) {
    		return false;
    	}
    	for (Standard standard : standards) {
    		if (standard == null || standard.getStandardNumber() == null || !testMethod.contains(standard.getStandardNumber())) {
    			continue;
    		}
    		for (TestItem standardItem : standard.getTestItem()) {
    			if (standardItem == null || standardItem.getTestItem() == null) {
    				continue;
    			}
    			if (!testItem.contains(standardItem.getTestItem()) && !standardItem.getTestItem().contains(testItem)) {
    				continue;
    			}
    			for (Method method : standardItem.getMethod()) {
    				if (method == null || method.getMethod() == null) {
    					continue;
    				}
    				if (!"/".equals(method.getMethod()) && !testMethod.contains(method.getMethod())) {
    					continue;
    				}
    				for (MethodProceed proceed : method.getMethodProceed()) {
    					if (proceed != null && proceed.getUnit() != null && proceed.getUnit().equalsIgnoreCase("mpn")) {
    						return true;
    					}
    				}
    			}
    		}
    	}
    	return false;
    }

}
