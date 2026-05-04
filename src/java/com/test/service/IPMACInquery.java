package com.test.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jakarta.servlet.http.HttpServletRequest;

public class IPMACInquery {
	
	public String getMAC(HttpServletRequest request) {
		
		 String clientIP = request.getRemoteAddr();  // 获取客户端的IP地址
		 System.out.println("Client IP: " + clientIP);
		 String macAddress = getMacAddressFromIp(clientIP);
		    if (macAddress != null) {
		    	return macAddress;
		    } else {
		    	return "Unknown ";
		    }
		
	}

	 private String getMacAddressFromIp(String ip) {
	    try {
	        // 执行系统命令获取 ARP 信息
	        String[] command = {"arp -a " + ip};
	        
			Process process = Runtime.getRuntime().exec(command);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

	        String line;
	        while ((line = reader.readLine()) != null) {
	            if (line.contains(ip)) {
	                String[] parts = line.split(" ");
	                // 获取 MAC 地址
	                return parts[3]; // MAC 地址通常在第4列
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return null;
	}

}
