package com.test.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.GetSimpleList;
import com.exl.materialstanderd.read.ReadFSheet;
import com.exl.materialstanderd.read.ReadFSheetXML;
import com.exl.materialstanderd.read.ReadSelomenSheet;
import com.exl.materialstanderd.read.ReadSheet;
import com.exl.materialstanderd.read.ReadSheetXML;
import com.test.record.ExportRecordData;
import com.xml.standards.Standard;

import jakarta.servlet.http.HttpServletResponse;

@Service
public class FileDownload {
	@Autowired
    private XmlService xmlService;
	public void fileload(HttpServletResponse response) throws IOException, InvalidFormatException {
		String txtFile = null;
		ReadSheet rs = new ReadSheet();
		GetSimpleList getList = new GetSimpleList();
		//txtFile = rs.read(getList.getLabelList());

		response.setContentType("application/octet-stream; charset=UTF-8");
		// ServletOutputStream ops = response.getOutputStream();
		PrintWriter out = response.getWriter();
		out.println(txtFile);
		out.close();
		getList.close();
	}
	public void fileloadBETA(HttpServletResponse response,Integer batch,int dup) throws IOException, InvalidFormatException {
		String txtFile = null;
		ReadSheetXML rs = new ReadSheetXML();
		GetSimpleList getList = new GetSimpleList();
		ArrayList<Standard> standards=(ArrayList<Standard>) xmlService.readXmlData();
		txtFile = rs.read(getList.getLabelList(batch),standards,dup);

		response.setContentType("application/octet-stream; charset=UTF-8");
		  // 设置文件名和扩展名（例如 .txt）
		 // 获取当前时间
	        LocalDateTime now = LocalDateTime.now();

	        // 定义时间格式，例如：2023-12-21_14-30-00
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss");

	        // 格式化时间为字符串
	        String formattedTime = now.format(formatter);

	        // 设置文件名，添加扩展名
	        String batchName = batch == null ? "全部" : String.valueOf(batch);
	        String fileName = formattedTime+"第"+batchName + "批.txt";
	        // 进行 UTF-8 URL 编码
		    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
		    // 设置 Content-Disposition 头，告诉浏览器这是一个文件下载，并指定文件名
		    response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");
		PrintWriter out = response.getWriter();
		out.println(txtFile);
		out.close();
		getList.close();
	}

	public void fileFload(HttpServletResponse response) throws IOException, InvalidFormatException {
		String txtFile = null;
		ReadFSheet rs = new ReadFSheet();
		GetSimpleList getList = new GetSimpleList();
		txtFile = rs.read(getList.getFLabelList());
		

		response.setContentType("application/octet-stream; charset=UTF-8");
		// ServletOutputStream ops = response.getOutputStream();
		PrintWriter out = response.getWriter();
		out.println(txtFile);
		out.close();
		getList.close();
	}
	public void fileFloadXML(HttpServletResponse response) throws IOException, InvalidFormatException {
		String txtFile = null;
		ReadFSheetXML rs = new ReadFSheetXML();
		GetSimpleList getList = new GetSimpleList();
		ArrayList<Standard> standards=(ArrayList<Standard>) xmlService.readXmlData();
		txtFile = rs.read(getList.getFLabelList(),standards);

		 // 设置响应内容类型为二进制流
	    response.setContentType("application/octet-stream; charset=UTF-8");

	    // 设置文件名和扩展名（例如 .txt）
	 // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 定义时间格式，例如：2023-12-21_14-30-00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd_HH-mm-ss");

        // 格式化时间为字符串
        String formattedTime = now.format(formatter);

        // 设置文件名，添加扩展名
        String fileName = formattedTime + "F.txt";
	    
	    // 设置 Content-Disposition 头，告诉浏览器这是一个文件下载，并指定文件名
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		PrintWriter out = response.getWriter();
		out.println(txtFile);
		out.close();
		getList.close();
	}
//	public void fileSelomenLabalload(HttpServletResponse response,String table_name) throws IOException, InvalidFormatException {
//		String txtFile = null;
//		ReadSelomenSheet rs = new ReadSelomenSheet();
//		GetSimpleList getList = new GetSimpleList();
//		txtFile = rs.read(getList.getSelomenLabelList(table_name));
//
//		response.setContentType("application/octet-stream; charset=UTF-8");
//		// ServletOutputStream ops = response.getOutputStream();
//		PrintWriter out = response.getWriter();
//		out.println(txtFile);
//		out.close();
//		getList.close();
//	}
	public void fileSelomenLabalload(HttpServletResponse response, String table_name,String checkItem,String position) throws IOException, InvalidFormatException, SQLException, ParseException {
	    String txtFile = null;
	    ReadSelomenSheet rs = new ReadSelomenSheet();
	    GetSimpleList getList = new GetSimpleList();
	    txtFile = rs.read(getList.getSelomenLabelList(table_name,checkItem,position));

	    // 设置响应内容类型为二进制流
	    response.setContentType("application/octet-stream; charset=UTF-8");

	    // 设置文件名和扩展名（例如 .txt）
	    String fileName = "S" + table_name + ".txt";  // 动态生成文件名
	 // 进行 UTF-8 URL 编码
	    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());
	    // 设置 Content-Disposition 头，告诉浏览器这是一个文件下载，并指定文件名
	    response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + "\"");

	    // 获取输出流，并将文件内容写入输出流
	    PrintWriter out = response.getWriter();
	    out.println(txtFile);
	    out.close();

	    // 关闭资源
	    getList.close();
	}
	public XSSFWorkbook recordDataload(String table_name) throws FileNotFoundException {
		XSSFWorkbook workbook=null;
		ExportRecordData erd=new ExportRecordData();
		
		try {
			erd.write(table_name);
			//PrintWriter out = response.getWriter();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return workbook;
	}

}
