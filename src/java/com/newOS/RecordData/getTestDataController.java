package com.newOS.RecordData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cti.Ajax.service.AddFtoSimple;
import com.example.demo.Simple;
import com.example.demo.getDBtestName;
import com.test.service.XmlService;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

import jakarta.servlet.http.HttpServletResponse;
@Controller
public class getTestDataController {
	 @Autowired
	    private XmlService xmlService;
	 private static String id="id";
	 private static String num="样品短号";
	 private static String name="样品名称";
	 private static String unit="报告单位";
	 private static String company="报告抬头";
	 private static String testItem="检测项目";
	 private static String testMethod="检测方法";
	 private static String done="done";
	 private static String ifF="复测";
	 private static String result="结果";
	private ArrayList<String> tableNameList = new ArrayList<String>();
	@GetMapping("/getTestDatabeta")
	public String htmlInitial( Model model) {
		tableNameList = new getDBtestName().getSevendayTestName();
		model.addAttribute("nameList", tableNameList);


			return "getDataHTML/getDataBeta";
		}
	
	@RequestMapping("/getData")
	@ResponseBody
	public CombinedResponseQuee result(@RequestParam("date") String date,@RequestParam("id") String questid) throws SQLException {
	    if (date == null || date.isEmpty()) {
	        return null;
	    }
//System.out.println(date+"---"+questid);
	    String tableName = date;
	    int idAcount=Integer.valueOf(questid);
	    String sql=null;
	    if(idAcount==500) {
	    	sql = "SELECT * FROM `" + tableName + "` ORDER BY id LIMIT 500 OFFSET 0;";
	    }else {
	    	 sql = "SELECT * FROM `" + tableName + "` ORDER BY id LIMIT 500 OFFSET "+(idAcount-500)+";";
	    }
	   

	    Connection conn = null;
	    conn = getConnection();
	    Statement stmt = conn.createStatement();

	    // 检查表是否存在
	    ResultSet tables = conn.getMetaData().getTables(null, null, tableName, null);
	    if (!tables.next()) {
	        return null;
	    }

	    ResultSet simpleInfo = stmt.executeQuery(sql);

	    // 构建索引
	    ArrayList<Standard> standards = (ArrayList<Standard>) xmlService.readXmlData();
	    Map<String, Map<String, List<Method>>> index = buildStandardIndex(standards);

	    // 分类数据
	    List<Simple> cfusimpleList = new ArrayList<>();
	    List<Simple> mpnsimpleList = new ArrayList<>();
	    List<Simple> attrsimpleList = new ArrayList<>();
	    List<Simple> nonesimpleList = new ArrayList<>();

	    List<List<MethodProceed>> cfumethodProceedList = new ArrayList<>();
	    List<List<MethodProceed>> mpnmethodProceedList = new ArrayList<>();
	    List<List<MethodProceed>> attrmethodProceedList = new ArrayList<>();
	    List<List<MethodProceed>> nonemethodProceedList = new ArrayList<>();

	    while (simpleInfo.next()) {
	    	
	        String simpleTest = simpleInfo.getString(testItem);
	        String method = simpleInfo.getString(testMethod);
	        String StandardNumber=null;
	        for(Standard std :standards) {
	        	if(method.contains(std.getStandardNumber())) {
	        		StandardNumber=std.getStandardNumber();
	        	}
	        }

	        String returnUnit = getUnitFromIndex(simpleTest, StandardNumber, index);
	       // System.out.println(returnUnit);
	        Simple simple = new Simple(
	            simpleInfo.getInt(id),
	            simpleInfo.getString(num),
	            simpleInfo.getString(name),
	            simpleInfo.getString(company),
	            simpleTest,
	            simpleInfo.getString(unit),
	            method,
	            simpleInfo.getInt(done),
	            simpleInfo.getString(ifF),
	            simpleInfo.getString(result)
	        );
	        

	        switch (returnUnit) {
	            case "CFU":
	                cfusimpleList.add(simple);
	                cfumethodProceedList = SelectedData(cfumethodProceedList, simpleTest,StandardNumber, method);
	                break;
	            case "MPN":
	                mpnsimpleList.add(simple);
	                mpnmethodProceedList = SelectedData(mpnmethodProceedList, simpleTest,StandardNumber,method);
	                
	                break;
	            case "/":
	                attrsimpleList.add(simple);
	                attrmethodProceedList = SelectedData(attrmethodProceedList, simpleTest,StandardNumber, method);
	                break;
	            default:
	                nonesimpleList.add(simple);
	                nonemethodProceedList = SelectedData(nonemethodProceedList, simpleTest,StandardNumber, method);
	                break;
	        }
	    }
	    
	    
	    conn.close();

	    return new CombinedResponseQuee(
	        cfusimpleList, mpnsimpleList, attrsimpleList, nonesimpleList,
	        cfumethodProceedList, mpnmethodProceedList, attrmethodProceedList, nonemethodProceedList
	    );
	}

	/**
	 * 构建索引
	 */
	private Map<String, Map<String, List<Method>>> buildStandardIndex(List<Standard> standards) {
	    Map<String, Map<String, List<Method>>> index = new HashMap<>();
	    for (Standard standard : standards) {
	        Map<String, List<Method>> testItemMap = new HashMap<>();
	        for (TestItem testItem : standard.getTestItem()) {
	            testItemMap.put(testItem.getTestItem(), testItem.getMethod());
	        }
	        index.put(standard.getStandardNumber(), testItemMap);
	        
	    }
	    return index;
	}

	/**
	 * 从索引中获取单位
	 */
	private String getUnitFromIndex(String simpleTest, String standardNumber, Map<String, Map<String, List<Method>>> index) {
	    Map<String, List<Method>> testItemMap = index.get(standardNumber);
	    if (testItemMap == null) {
	    	
	        return "none";
	    }

	    List<Method> methods = testItemMap.get(simpleTest);
	    if (methods == null || methods.isEmpty()) {
	    	
	        return "none";
	    }

	    for (Method method : methods) {
	        List<MethodProceed> methodProceeds = method.getMethodProceed();
	        
	        if (methodProceeds != null && !methodProceeds.isEmpty()) {
	        	//只筛选第一个方法内的unit，导致第一个为MPN接下来的所有都筛选为MPN。
	        	//对大肠类友好，对混合型不友好，具体情况需要观察，此处待改进。
	        	//2025/1/5 14：28
	        	
	            return methodProceeds.get(0).getUnit();
	        }
	    }

	    return "none";
	}
	private List<List<MethodProceed>> SelectedData(
		    List<List<MethodProceed>> methodProceedList,
		    String simpleTest,
		    String standardNumber,
		    String simpleMethod
		) throws SQLException {
		    // 定义自定义 MethodProceed 数据
		    ArrayList<MethodProceed> customMethodProceedList = new ArrayList<>();
		    MethodProceed customMethodProceed = new MethodProceed();
		    customMethodProceed.setMethodProceed("方法未定义！");
		    customMethodProceed.setAddition("/");
		    customMethodProceed.setDilution("/");
		    customMethodProceed.setHighlight("/");
		    customMethodProceed.setMedium("/");
		    customMethodProceed.setQuantity("/");
		    customMethodProceed.setSearch("/");
		    customMethodProceed.setUnit("none");
		    customMethodProceedList.add(customMethodProceed);

		    // 构建索引
		    Map<String, Map<String, List<Method>>> standardIndex = buildStandardIndex(xmlService.readXmlData());

		    // 检查是否存在对应的标准
		    Map<String, List<Method>> testItemMap = standardIndex.get(standardNumber);
		    if (testItemMap == null) {
		        methodProceedList.add(customMethodProceedList);
		        return methodProceedList;
		    }

		    // 检查是否存在对应的检测项目
		    List<Method> methods = testItemMap.get(simpleTest);
		    if (methods == null || methods.isEmpty()) {
		        methodProceedList.add(customMethodProceedList);
		        return methodProceedList;
		    }

		    // 查找对应的检测方法
		    boolean added = false;
		    for (Method method : methods) {
		    	
		        if (simpleMethod.contains(method.getMethod()) || "/".equals(method.getMethod())) {
		        	//int proceedIndex=0;
		        	List<MethodProceed> MethodProceed= method.getMethodProceed();
		        	
		        	// 使用显式迭代器安全地移除元素
		            Iterator<MethodProceed> iterator = MethodProceed.iterator();
		            while (iterator.hasNext()) {
		                MethodProceed proceed = iterator.next();
		                if (proceed.getUnit().equals("/")) {
		                    iterator.remove(); // 安全删除元素
		                }
		            }
		        	
		        	
		        	methodProceedList.add(MethodProceed);
		            added = true;
		            break;
		        }
		    }

		    // 如果没有找到匹配的检测方法，添加自定义数据
		    if (!added) {
		        methodProceedList.add(customMethodProceedList);
		    }

		    return methodProceedList;
		}
	@RequestMapping("/b")
	@ResponseBody
	public void ajax2(@RequestParam("simpleNum") String simpleNum,
			@RequestParam("simpleID") String simpleID,
			@RequestParam("simpleName") String simpleName,
			@RequestParam("table_name") String table_name,Model model) {
		AddFtoSimple ats = new AddFtoSimple();
		ats.getNumber(simpleNum,simpleID, simpleName, table_name, model);
	}
	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}
}
