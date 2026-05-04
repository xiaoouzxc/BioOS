package com.example.demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class ClientInfoController {
    private static final int SESSION_TIMEOUT_SECONDS = 8 * 60 * 60;
	
		
        
    
	private Map<String, String> saveSessionInfo(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        HttpSession session=request.getSession();
        if(session.getAttribute("username")==null&&session.getAttribute("passord")==null) {
        	 result.put("status", "未登录");             
             return result;
        }else {
        	

                            // 根据你自己的逻辑设置登录状态
                            result.put("status", "已登录");                           
                            result.put("username", (String) session.getAttribute("username"));
                            return result;
                        }
 
    }
	
	
	
	 //@GetMapping("/login")
//		public Map<String, String> login(HttpServletRequest request) {
//	        Map<String, String> result = new HashMap<>();
//	        HttpSession session=request.getSession();
//	        if(session.getAttribute("name")==null&&session.getAttribute("passord")==null) {
//	        	 result.put("status", "未登录");             
//	             return result;
//	        }else {
//	        	try {
//	                String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
//	                String username = "root";
//	                String password = "1234";
//	                Connection conn = DriverManager.getConnection(url, username, password);
//	                Statement stmt = conn.createStatement();
//	               
//
//	                // 判断数据表是否存在
//	                ResultSet tables = conn.getMetaData().getTables(null, null, "testIPTable", null);
//	                if (tables.next()) {
//	                    // 数据表存在，查询所有记录
//	                    String sql = "SELECT * FROM testIPTable;";
//	                    ResultSet simpleInfo = stmt.executeQuery(sql);
//	                    boolean found = false;
//	                    while (simpleInfo.next()) {
//	                        String clientpassword = simpleInfo.getString("IP");
//	                        String name = simpleInfo.getString("buildingName");
//	                        if (clientpassword.compareTo((String) session.getAttribute("password")) == 0&&name.compareTo((String) session.getAttribute("name")) == 0) {
//	                            found = true;
//
//	                            // 根据你自己的逻辑设置登录状态
//	                            result.put("status", "已登录");
//	                            result.put("password", clientpassword);
//	                            result.put("name", name);
//	                            return result;
//	                        }
//	                    }
//	                    if (!found) {
//	                        // 没有找到记录，提示是否添加设备
//	                    	
//	                        // 根据你自己的逻辑设置登录状态
//	                        result.put("status", "是否添加此设备为做样设备");
//	                        result.put("ip", null);
//	                        result.put("name", null);
//	                        return result;
//	                    }
//	                } else {
//	                    // 数据表不存在，先创建表（此处仅创建表，后续操作在新增接口中处理）
//	                    String createSql = "CREATE TABLE test.testIPTable ("
//	                            + " id int NOT NULL, "
//	                            + " IP VARCHAR(100) NULL, "
//	                            + " buildingName VARCHAR(100) NULL, "
//	                            + " PRIMARY KEY (id));";
//	                    stmt.execute(createSql);
//	                    stmt.close();
//	                }
//	                conn.close();
//	            } catch (SQLException e) {
//	                e.printStackTrace();
//	            }
//	        }
//			
//	        
//	    }
    @GetMapping("/getClientIp")
    public Map<String, String> getClientIp(HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Map<String, String> result = new HashMap<>();
        try {
            String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
            String username = "root";
            String password = "1234";
            Connection conn = DriverManager.getConnection(url, username, password);
            Statement stmt = conn.createStatement();
           

            // 判断数据表是否存在
            ResultSet tables = conn.getMetaData().getTables(null, null, "testIPTable", null);
            if (tables.next()) {
                // 数据表存在，查询所有记录
                String sql = "SELECT * FROM testIPTable;";
                ResultSet simpleInfo = stmt.executeQuery(sql);
                boolean found = false;
                while (simpleInfo.next()) {
                    String ip = simpleInfo.getString("IP");
                    String name = simpleInfo.getString("buildingName");
                    if (clientIp.compareTo(ip) == 0) {
                        found = true;
                        // 检查 Session
                        HttpSession session = request.getSession(false);
                        if (session == null || session.getAttribute("clientIp") == null) {
                            session =  request.getSession();
                            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
                            session.setAttribute("clientIp", clientIp);
                            session.setAttribute("name", name);
                            
                        }
                        
                        // 根据你自己的逻辑设置登录状态
                        result.put("status", "已登录");
                        result.put("ip", clientIp);
                        result.put("name", name);
                        return result;
                    }
                }
                if (!found) {
                    // 没有找到记录，提示是否添加设备
                	
                    // 根据你自己的逻辑设置登录状态
                    result.put("status", "是否添加此设备为做样设备");
                    result.put("ip", null);
                    result.put("name", null);
                    return result;
                }
            } else {
                // 数据表不存在，先创建表（此处仅创建表，后续操作在新增接口中处理）
                String createSql = "CREATE TABLE test.testIPTable ("
                        + " id int NOT NULL, "
                        + " IP VARCHAR(100) NULL, "
                        + " buildingName VARCHAR(100) NULL, "
                        + " PRIMARY KEY (id));";
                stmt.execute(createSql);
                stmt.close();
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 新增接口：添加 IP 并创建登录 Session
    @PostMapping("/addClientIp")
    public Map<String, String> addClientIp(HttpServletRequest request,String name) {
    	Map<String, String> result = new HashMap<>();
    	if(name.isEmpty()||name.isBlank()) {
    		result.put("status", "实验室名不能为空");
            result.put("ip", null);
            result.put("name", null);
            return result;
    	}
        String clientIp = request.getRemoteAddr();
        
        try {
            String url = "jdbc:mysql://localhost:3306/test?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
            String username = "root";
            String password = "1234";
            Connection conn = DriverManager.getConnection(url, username, password);

            // 计算新记录的 id，假设 id 为递增值
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM testIPTable;");
            int newId = 1;
            if (rs.next()) {
                newId = rs.getInt(1) + 1;
            }
            rs.close();

            // 插入新 IP 记录（这里 buildingName 随便设置为 "做样设备"，可根据实际需求修改）
            String insertSql = "INSERT INTO testIPTable (id, IP, buildingName) VALUES (?, ?, ?);";
            PreparedStatement pstmt = conn.prepareStatement(insertSql);
            pstmt.setInt(1, newId);
            pstmt.setString(2, clientIp);
            pstmt.setString(3, name);
            pstmt.executeUpdate();
            pstmt.close();
            stmt.close();
            conn.close();

            // 创建 Session 并保存 IP
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute("clientIp", clientIp);
            session.setAttribute("name", name);
         // 根据你自己的逻辑设置登录状态
            result.put("status", "已登录");
            result.put("ip", clientIp);
            result.put("name", name);
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("status", "添加失败");
            result.put("ip", null);
            result.put("name", null);
            return result;
            
        }
        
    }
    @GetMapping("/checkLogin")
    public Map<String, String> checkLogin(HttpServletRequest request) {
    	 Map<String, String> result = new HashMap<>();
        HttpSession session = request.getSession();
        
        if (session != null &&session.getAttribute("username")!=null) {
        	session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
        //	 if(Calendar.getInstance().getTimeInMillis()-session.getLastAccessedTime()<session.getMaxInactiveInterval()*1000l) {
        		// System.out.println(session.getAttribute("username")+"---"+session.getAttribute("password"));
        		 System.out.println(session.getAttribute("username")+"--session有效");
        		 result.put("status", "已登录");
                 result.put("username", (String) session.getAttribute("username"));
                 return result;
        		 
        	 //}else {
        		// System.out.println(session.getAttribute("username")+"--session失效");
        		 //result.put("status", "未登录");                           
                 
                // return result;
        	 //}
        	 
            
        }else {
        	System.out.println("--session失效");
        	result.put("status", "未登录");                           
            
            return result;
        }
        
    }
    @GetMapping("/quitLogin")
    public String checkSessionTimeout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
        	UserController.removeLoggedInUser((String) session.getAttribute("username"), session.getId());
        	session.invalidate();
            return "已退出" ;
        }
        return "No session found!";
    }
}
