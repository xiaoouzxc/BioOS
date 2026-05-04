package com.newOS.RecordData;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.Simple;
import com.example.demo.getDBtestName;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.service.XmlService;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;
@Controller
public class RecordController {
	 @Autowired
	    private XmlService xmlService;
	 private static final DateTimeFormatter DATE_PARAM_FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");
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
	@GetMapping("/recordingbeta")
	public String htmlInitial( Model model) {
		tableNameList = new getDBtestName().getSevendayTestName();
		model.addAttribute("nameList", tableNameList);


			return "recordDataHTML/recordingbeta";
		}
	
	@RequestMapping("/questData")
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

	        String returnUnit = getUnitFromIndex(simpleTest,method,StandardNumber, index);
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

	@RequestMapping("/record/api/samples")
	@ResponseBody
	public CombinedResponseQuee samplesByUnit(@RequestParam("date") String date,
			@RequestParam("id") String questid,
			@RequestParam(value = "unit", defaultValue = "CFU") String selectedUnit) throws SQLException {
	    if (date == null || date.isEmpty()) {
	        return emptyCombinedResponse();
	    }
	    System.out.println(date+"---"+questid);

	    int idAcount = Integer.valueOf(questid);
	    int offset = idAcount == 500 ? 0 : idAcount - 500;

	    List<Simple> selectedSimpleList = new ArrayList<>();
	    List<List<MethodProceed>> selectedMethodProceedList = new ArrayList<>();

	    try (Connection conn = getConnection()) {
	        QuerySource source = resolveQuerySource(conn, date);
	        if (source == null) {
	            return emptyCombinedResponse();
	        }

	        String sql = buildPagedSelectSql(source, offset);
	        ArrayList<Standard> standards = (ArrayList<Standard>) xmlService.readXmlData();
	        Map<String, Map<String, List<Method>>> index = buildStandardIndex(standards);

	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            bindDateRange(stmt, source);
	            try (ResultSet simpleInfo = stmt.executeQuery()) {
	        while (simpleInfo.next()) {
	            String simpleTest = simpleInfo.getString(testItem);
	            String method = simpleInfo.getString(testMethod);
	            String standardNumber = null;
	            for (Standard std : standards) {
	                if (method != null && std.getStandardNumber() != null && method.contains(std.getStandardNumber())) {
	                    standardNumber = std.getStandardNumber();
	                }
	            }

	            String returnUnit = getUnitFromIndex(simpleTest, method, standardNumber, index);
	            if (!selectedUnit.equals(returnUnit)) {
	                continue;
	            }

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
	            selectedSimpleList.add(simple);
	            selectedMethodProceedList = SelectedData(selectedMethodProceedList, simpleTest, standardNumber, method);
	        }
	            }
	        }
	    }

	    if ("MPN".equals(selectedUnit)) {
	        return new CombinedResponseQuee(new ArrayList<>(), selectedSimpleList, new ArrayList<>(), new ArrayList<>(),
	            new ArrayList<>(), selectedMethodProceedList, new ArrayList<>(), new ArrayList<>());
	    } else if ("/".equals(selectedUnit)) {
	        return new CombinedResponseQuee(new ArrayList<>(), new ArrayList<>(), selectedSimpleList, new ArrayList<>(),
	            new ArrayList<>(), new ArrayList<>(), selectedMethodProceedList, new ArrayList<>());
	    } else if ("none".equals(selectedUnit)) {
	        return new CombinedResponseQuee(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), selectedSimpleList,
	            new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), selectedMethodProceedList);
	    }
	    return new CombinedResponseQuee(selectedSimpleList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
	        selectedMethodProceedList, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	private CombinedResponseQuee emptyCombinedResponse() {
	    return new CombinedResponseQuee(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
	        new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}

	@RequestMapping("/record/api/test-item-filters")
	@ResponseBody
	public List<TestItemFilterOption> testItemFilters(@RequestParam("date") String date,
			@RequestParam(value = "unit", defaultValue = "CFU") String selectedUnit) throws SQLException {
	    if (date == null || date.trim().isEmpty()) {
	        return new ArrayList<>();
	    }

	    List<Standard> standards = xmlService.readXmlData();
	    Map<String, TestItemFilterOption> groupedOptions = new HashMap<>();

	    try (Connection conn = getConnection()) {
	        QuerySource source = resolveQuerySource(conn, date);
	        if (source == null) {
	            return new ArrayList<>();
	        }

	        String sql = buildFilterSelectSql(source);
	        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            bindDateRange(stmt, source);
	        try (ResultSet rows = stmt.executeQuery()) {
	            while (rows.next()) {
	                String rawTestItem = rows.getString(testItem);
	                String rawMethod = rows.getString(testMethod);
	                TestItemFilterOption option = resolveTestItemFilterOption(rawTestItem, rawMethod, standards);
	                if (!selectedUnit.equals(option.getUnit())) {
	                    continue;
	                }

	                TestItemFilterOption grouped = groupedOptions.get(option.getKey());
	                if (grouped == null) {
	                    grouped = option;
	                    groupedOptions.put(option.getKey(), grouped);
	                }
	                grouped.incrementCount();
	                grouped.addAlias(rawTestItem);
	            }
	        }
	        }
	    }

	    return new ArrayList<>(groupedOptions.values());
	}

	@RequestMapping("/record/api/query-samples")
	@ResponseBody
	public ArrayList<Simple> querySamples(@RequestParam("keyword") String keyword) throws SQLException {
	    ArrayList<Simple> resultList = new ArrayList<>();
	    if (keyword == null || keyword.trim().isEmpty()) {
	        return resultList;
	    }

	    String query = keyword.trim();
	    boolean numeric = query.matches("^[0-9]+$");
	    boolean companyQuery = !numeric && query.endsWith("co");
	    String searchText = companyQuery ? query.substring(0, query.length() - 2) : query;

	    try (Connection conn = getConnection()) {
	        List<String> tables = findCompatibleQueryTables(conn);
	        for (String table : tables) {
	            queryCompatibleTable(conn, table, searchText, numeric, companyQuery, resultList);
	        }
	    }

	    if (resultList.isEmpty()) {
	        resultList.add(new Simple("未找到", "未找到", "未找到", "未找到", "未找到", "未找到", 0));
	    }
	    return resultList;
	}

	private List<String> findCompatibleQueryTables(Connection conn) throws SQLException {
	    List<String> tables = new ArrayList<>();
	    try (ResultSet rs = conn.getMetaData().getTables(null, null, null, new String[] {"TABLE"})) {
	        while (rs.next()) {
	            String tableName = rs.getString("TABLE_NAME");
	            if (isDailyTableName(tableName) || isLegacyYearTableName(tableName) || isNewYearTableName(tableName)) {
	                tables.add(tableName);
	            }
	        }
	    }
	    return tables;
	}

	private boolean isDailyTableName(String tableName) {
	    return tableName != null && tableName.contains("/") && tableName.length() < 11;
	}

	private boolean isLegacyYearTableName(String tableName) {
	    return tableName != null && tableName.matches("^t\\d{4}$");
	}

	private boolean isNewYearTableName(String tableName) {
	    return tableName != null && tableName.matches("^total_samples_\\d{4}$");
	}

	private void queryCompatibleTable(Connection conn, String tableName, String searchText, boolean numeric,
			boolean companyQuery, ArrayList<Simple> resultList) throws SQLException {
	    String column = numeric ? num : (companyQuery ? company : name);
	    boolean hasDoneColumn = columnExists(conn, tableName, done);
	    boolean hasTestMethodColumn = columnExists(conn, tableName, testMethod);
	    String sql = "SELECT `id`, `" + num + "`, `" + name + "`, `" + company + "`, `" + testItem
	            + "`, `" + ifF + "`, `" + result + "`"
	            + (hasTestMethodColumn ? ", `" + testMethod + "`" : "")
	            + (hasDoneColumn ? ", `" + done + "`" : "")
	            + (isNewYearTableName(tableName) ? ", `传入时间`" : "")
	            + " FROM `" + tableName + "` WHERE `" + column + "` LIKE ? LIMIT 200";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, "%" + searchText + "%");
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String dateInfo = isNewYearTableName(tableName) && rs.getTimestamp("传入时间") != null
	                        ? rs.getTimestamp("传入时间").toLocalDateTime().toLocalDate().toString()
	                        : tableName;
	                Simple simple = new Simple();
	                simple.setNumber(rs.getString(num));
	                simple.setName(rs.getString(name));
	                simple.setTestItem(rs.getString(testItem));
	                simple.setCompany(rs.getString(company));
	                simple.setTestMethod(hasTestMethodColumn ? rs.getString(testMethod) : "");
	                simple.setIfF("复测信息：" + rs.getString(ifF) + " 做样日期：" + dateInfo);
	                simple.setResult(rs.getString(result));
	                simple.setDone(hasDoneColumn ? rs.getInt(done) : -1);
	                resultList.add(simple);
	            }
	        }
	    } catch (SQLException e) {
	        // 兼容旧表结构差异：单表异常不影响其它表继续查询。
	        System.out.println("跳过不兼容查询表: " + tableName + " - " + e.getMessage());
	    }
	}

	private boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
	    try (ResultSet columns = conn.getMetaData().getColumns(null, null, tableName, columnName)) {
	        return columns.next();
	    }
	}

	private TestItemFilterOption resolveTestItemFilterOption(String rawTestItem, String rawMethod,
			List<Standard> standards) {
	    String standardNumber = findStandardNumber(rawMethod, standards);
	    Standard matchedStandard = findStandard(standardNumber, standards);
	    TestItem matchedTestItem = findCanonicalTestItem(rawTestItem, matchedStandard);

	    if (matchedStandard == null || matchedTestItem == null) {
	        String key = "none|" + rawTestItem;
	        return new TestItemFilterOption(key, rawTestItem, "none", "none");
	    }

	    String matchedUnit = findUnitForMethod(rawMethod, matchedTestItem);
	    String canonicalName = matchedTestItem.getTestItem();
	    String key = matchedStandard.getStandardNumber() + "|" + canonicalName + "|" + matchedUnit;
	    return new TestItemFilterOption(key, canonicalName, matchedUnit, matchedStandard.getStandardNumber());
	}

	private String findStandardNumber(String rawMethod, List<Standard> standards) {
	    if (rawMethod == null) {
	        return null;
	    }
	    for (Standard standard : standards) {
	        if (standard.getStandardNumber() != null && rawMethod.contains(standard.getStandardNumber())) {
	            return standard.getStandardNumber();
	        }
	    }
	    return null;
	}

	private Standard findStandard(String standardNumber, List<Standard> standards) {
	    if (standardNumber == null) {
	        return null;
	    }
	    for (Standard standard : standards) {
	        if (standardNumber.equals(standard.getStandardNumber())) {
	            return standard;
	        }
	    }
	    return null;
	}

	private TestItem findCanonicalTestItem(String rawTestItem, Standard standard) {
	    if (rawTestItem == null || standard == null || standard.getTestItem() == null) {
	        return null;
	    }
	    for (TestItem item : standard.getTestItem()) {
	        if (rawTestItem.equals(item.getTestItem())) {
	            return item;
	        }
	    }
	    for (TestItem item : standard.getTestItem()) {
	        String canonicalName = item.getTestItem();
	        if (canonicalName != null && (rawTestItem.contains(canonicalName) || canonicalName.contains(rawTestItem))) {
	            return item;
	        }
	    }
	    return null;
	}

	private String findUnitForMethod(String rawMethod, TestItem matchedTestItem) {
	    if (matchedTestItem.getMethod() == null) {
	        return "none";
	    }
	    for (Method method : matchedTestItem.getMethod()) {
	        if (rawMethod != null && method.getMethod() != null && rawMethod.contains(method.getMethod())) {
	            return firstUsefulUnit(method);
	        }
	    }
	    for (Method method : matchedTestItem.getMethod()) {
	        if ("/".equals(method.getMethod())) {
	            return firstUsefulUnit(method);
	        }
	    }
	    return "none";
	}

	private String firstUsefulUnit(Method method) {
	    if (method.getMethodProceed() == null || method.getMethodProceed().isEmpty()) {
	        return "none";
	    }
	    for (MethodProceed proceed : method.getMethodProceed()) {
	        if (proceed.getUnit() != null && !proceed.getUnit().isEmpty()) {
	            return proceed.getUnit();
	        }
	    }
	    return "none";
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
	private String getUnitFromIndex(String simpleTest,String testmethod, String standardNumber, Map<String, Map<String, List<Method>>> index) {
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
	        
	        if (methodProceeds != null && !methodProceeds.isEmpty()&&testmethod.contains(method.getMethod())) {
	        	//只筛选第一个方法内的unit，导致第一个为MPN接下来的所有都筛选为MPN。
	        	//对大肠类友好，对混合型不友好，具体情况需要观察，此处待改进。
	        	//2025/1/5 14：28
	        	
	        		return methodProceeds.get(0).getUnit();

	        }
	    }
	    for (Method method : methods) {
	        List<MethodProceed> methodProceeds = method.getMethodProceed();
	        
	        if (methodProceeds != null && !methodProceeds.isEmpty()&&method.getMethod().equals("/")) {
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
	
	@RequestMapping("/AutoupdateData")
	@ResponseBody
	public void setZeroDataAutomatllyMethord(@RequestBody AutoUpdateRequest request) {
		
		 String table_name = request.getTable_name();
		    List<Simple> simpleList = request.getList();
		 
		   

	    String resultStr = "-1(0,0)-2(0,0)-3(0,0)"; // 固定更新内容

	    // 将 JSON 格式的 list 字符串转换成 Java List<Simple> 对象
	    
	    
	    try {
	       
	    } catch (Exception e) {
	        e.printStackTrace();
	        return;
	    }

	    try (Connection conn = getConnection()) {
	    	UpdateTarget target = resolveUpdateTarget(conn, table_name);
	        for (Simple simple : simpleList) {
	            
	                String checkSql = buildSelectForUpdateSql(target);
	                String updateSql = buildUpdateSql(target, "结果", false);

	                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
	                	bindUpdateWhere(checkStmt, target, 1, simple.getNumber(), String.valueOf(simple.getId()), simple.getTestItem());

	                    ResultSet rs = checkStmt.executeQuery();
	                    if (rs.next()) {
	                        String result = rs.getString("结果");
	                        if (result != null && !result.trim().isEmpty()) {
	                            continue; // 已有结果，跳过
	                        }
	                    }

	                    // 执行更新
	                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
	                        updateStmt.setString(1, resultStr);
	                        bindUpdateWhere(updateStmt, target, 2, simple.getNumber(), String.valueOf(simple.getId()), simple.getTestItem());

	                        updateStmt.executeUpdate();
	                        
	                    }
	                
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	@RequestMapping("/updateData")
	@ResponseBody
	public void setDataMethord(@RequestParam("simpleNum") String simpleNum,
			@RequestParam("simpleID") String simpleID,
			@RequestParam("simpleName") String simpleName,
			@RequestParam("data") String data,
			@RequestParam("table_name") String table_name) {
		System.out.println(simpleID+"--"+simpleNum+"--"+simpleName+"-"+table_name);
		try (Connection conn = getConnection()) {
			UpdateTarget target = resolveUpdateTarget(conn, table_name);
			String setFinfoSql = buildUpdateSql(target, "结果", false);
			try (PreparedStatement pstmt = conn.prepareStatement(setFinfoSql)) {
				pstmt.setString(1, data);
				bindUpdateWhere(pstmt, target, 2, simpleNum, simpleID, simpleName);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("/updateMPNData")
	@ResponseBody
	public String setMPNDataMethord(@RequestParam("simpleNum") String simpleNum,
			@RequestParam("simpleID") String simpleID,
			@RequestParam("simpleName") String simpleName,
			@RequestParam("proceedName") String proceedName, 
			@RequestParam("data") String data,
			@RequestParam("table_name") String table_name,
			@RequestParam("step") String step,
			@RequestParam("proceedLength") String proceedLength) {
		System.out.println(simpleID+"--"+simpleNum+"--"+simpleName+"-"+table_name);
		String returnValue=null;

		try (Connection conn = getConnection()) {
			UpdateTarget target = resolveUpdateTarget(conn, table_name);
			String selectinfoSql = buildSelectForUpdateSql(target);
			try (PreparedStatement selectStmt = conn.prepareStatement(selectinfoSql)) {
				bindUpdateWhere(selectStmt, target, 1, simpleNum, simpleID, simpleName);
		     ResultSet rs = selectStmt.executeQuery();
		     // 遍历查询结果
		     while (rs.next()) {
		    	 
		         String result = rs.getString("结果");
		         if(result!=null) {
		        	 String[] temp=result.split(">");
		        	 int stepIndex = Integer.parseInt(step);
		        	 int pLength=Integer.parseInt(proceedLength);
			            if (temp.length==pLength) {
			            
			                // 替换 temp 数组中的指定位置文本
			            	System.out.println(data);
			            String[] qData=data.split(" ",2);
			            data=qData[1];
//			            for(String s:qData) {
//			            	System.out.println(s);
//			            }
			            
			                temp[stepIndex] = proceedName+" "+data; // 用新的 data 替换指定位置的文本
			                returnValue= temp[stepIndex];
			                // 生成新的 result
			               String newresult = String.join(">", temp);
			               System.out.println(newresult);
			                data=newresult;
			                
			            }else  {
			            	//String[] qData=data.split(" ",2);
				            //data=qData[1];
//				            for(String s:qData) {
//				            	System.out.println(s);
//				            }
				            if(stepIndex==temp.length) {
				            	returnValue= proceedName+" "+data;
				            	data=result+proceedName+" "+data+">";
				            	
				            }else {
				            	temp[stepIndex] = data; // 用新的 data 替换指定位置的文本
				            	returnValue= temp[stepIndex];
				                // 生成新的 result
				               String newresult = String.join(">", temp);
				               System.out.println(newresult);
				                data=newresult+">";
				                
				            }
				                
				        }
			            
		         }else {
		        	 returnValue=proceedName+" "+data;
		        	 data=proceedName+" "+data+">";
		        	 
		         } 
		         
		        
		         
		         
		         
		     }
			}
		     String setinfoSql = buildUpdateSql(target, "结果", false);
			try (PreparedStatement pstmt = conn.prepareStatement(setinfoSql)) {
				pstmt.setString(1, data);
				bindUpdateWhere(pstmt, target, 2, simpleNum, simpleID, simpleName);
				pstmt.executeUpdate();
			}
			 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return returnValue;
		

	}
	@RequestMapping("/updateAttrData")
	@ResponseBody
	public void setDataAttrMethord(@RequestParam("simpleNum") String simpleNum,
			@RequestParam("simpleID") String simpleID,
			@RequestParam("simpleName") String simpleName,
			@RequestParam("table_name") String table_name) {

		System.out.println(simpleID+"--"+simpleNum+"--"+simpleName+"-"+table_name);

		try (Connection conn = getConnection()) {
			UpdateTarget target = resolveUpdateTarget(conn, table_name);
			String setFinfoSql = buildUpdateSql(target, "报告单位", false);
			try (PreparedStatement pstmt = conn.prepareStatement(setFinfoSql)) {
				pstmt.setString(1, "selected");
				bindUpdateWhere(pstmt, target, 2, simpleNum, simpleID, simpleName);
				pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private QuerySource resolveQuerySource(Connection conn, String date) throws SQLException {
	    if (tableExists(conn, date)) {
	        return QuerySource.daily(date);
	    }
	    LocalDate localDate = parseDateParam(date);
	    String yearlyTable = "total_samples_" + localDate.getYear();
	    if (tableExists(conn, yearlyTable)) {
	        return QuerySource.yearly(yearlyTable, localDate);
	    }
	    return null;
	}

	private UpdateTarget resolveUpdateTarget(Connection conn, String dateOrTable) throws SQLException {
	    if (tableExists(conn, dateOrTable)) {
	        return UpdateTarget.daily(dateOrTable);
	    }
	    LocalDate localDate = parseDateParam(dateOrTable);
	    String yearlyTable = "total_samples_" + localDate.getYear();
	    if (!tableExists(conn, yearlyTable)) {
	        throw new SQLException("找不到结果记录数据表: " + dateOrTable + " / " + yearlyTable);
	    }
	    return UpdateTarget.yearly(yearlyTable);
	}

	private boolean tableExists(Connection conn, String tableName) throws SQLException {
	    if (tableName == null || tableName.trim().isEmpty()) {
	        return false;
	    }
	    try (ResultSet tables = conn.getMetaData().getTables(null, null, tableName, null)) {
	        return tables.next();
	    }
	}

	private LocalDate parseDateParam(String date) {
	    String normalized = date.replace("-", "/");
	    return LocalDate.parse(normalized, DATE_PARAM_FORMATTER);
	}

	private String buildPagedSelectSql(QuerySource source, int offset) {
	    String sql = "SELECT * FROM `" + source.tableName + "`";
	    if (!source.dailyTable) {
	        sql += " WHERE `传入时间` >= ? AND `传入时间` < ?";
	    }
	    return sql + " ORDER BY id LIMIT 500 OFFSET " + offset + ";";
	}

	private String buildFilterSelectSql(QuerySource source) {
	    String sql = "SELECT `" + testItem + "`, `" + testMethod + "` FROM `" + source.tableName + "`";
	    if (!source.dailyTable) {
	        sql += " WHERE `传入时间` >= ? AND `传入时间` < ?";
	    }
	    return sql + " ORDER BY id;";
	}

	private void bindDateRange(PreparedStatement stmt, QuerySource source) throws SQLException {
	    if (source.dailyTable) {
	        return;
	    }
	    stmt.setString(1, source.date.toString() + " 00:00:00");
	    stmt.setString(2, source.date.plusDays(1).toString() + " 00:00:00");
	}

	private String buildSelectForUpdateSql(UpdateTarget target) {
	    if (target.dailyTable) {
	        return "SELECT `结果` FROM `" + target.tableName + "` WHERE `样品短号` LIKE ? AND `id` = ? AND `检测项目` LIKE ?";
	    }
	    return "SELECT `结果` FROM `" + target.tableName + "` WHERE `id` = ?";
	}

	private String buildUpdateSql(UpdateTarget target, String columnName, boolean appendDateRange) {
	    if (target.dailyTable) {
	        return "UPDATE `" + target.tableName + "` SET `" + columnName + "`=? WHERE `样品短号` LIKE ? AND `id` = ? AND `检测项目` LIKE ?";
	    }
	    return "UPDATE `" + target.tableName + "` SET `" + columnName + "`=? WHERE `id` = ?";
	}

	private void bindUpdateWhere(PreparedStatement stmt, UpdateTarget target, int startIndex,
			String simpleNum, String simpleID, String simpleName) throws SQLException {
	    if (target.dailyTable) {
	        stmt.setString(startIndex, simpleNum + "%");
	        stmt.setInt(startIndex + 1, Integer.parseInt(simpleID));
	        stmt.setString(startIndex + 2, simpleName + "%");
	        return;
	    }
	    stmt.setInt(startIndex, Integer.parseInt(simpleID));
	}

	private static class QuerySource {
	    private final String tableName;
	    private final boolean dailyTable;
	    private final LocalDate date;

	    private QuerySource(String tableName, boolean dailyTable, LocalDate date) {
	        this.tableName = tableName;
	        this.dailyTable = dailyTable;
	        this.date = date;
	    }

	    private static QuerySource daily(String tableName) {
	        return new QuerySource(tableName, true, null);
	    }

	    private static QuerySource yearly(String tableName, LocalDate date) {
	        return new QuerySource(tableName, false, date);
	    }
	}

	private static class UpdateTarget {
	    private final String tableName;
	    private final boolean dailyTable;

	    private UpdateTarget(String tableName, boolean dailyTable) {
	        this.tableName = tableName;
	        this.dailyTable = dailyTable;
	    }

	    private static UpdateTarget daily(String tableName) {
	        return new UpdateTarget(tableName, true);
	    }

	    private static UpdateTarget yearly(String tableName) {
	        return new UpdateTarget(tableName, false);
	    }
	}

	private Connection getConnection() throws SQLException {
		String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
		String name = "root";
		String password = "1234";
		Connection conn = DriverManager.getConnection(url, name, password);
		return conn;
	}
}


