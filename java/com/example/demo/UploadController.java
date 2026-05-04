package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.test.service.UploadService;
import com.test.service.updateTodaySimpleList;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@ComponentScan("com.test.service")
public class UploadController {

	@Autowired
	private UploadService uploadService;
	@Autowired
	private updateTodaySimpleList uts;

	@GetMapping("/upload")
	public String toupload() {
		return "upload";
	}

//	@PostMapping("/upload/file")
//	@ResponseBody
//	public String upload(@RequestParam("file") MultipartFile multipartFile, HttpServletRequest request) {
//		if (multipartFile.isEmpty()) {
//			return "文件为空";
//		}
//		multipartFile.getSize();
//		multipartFile.getOriginalFilename();
//		String contentType = multipartFile.getContentType();
//
//		if (!"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType)) {
//			return "文件格式不符";
//		}
//		String dir = request.getParameter("temp");
//		uploadService.uploadExcel(multipartFile, dir);
//		// HelloControrller hct=new HelloControrller();
//		uts.index();
//		return "上传成功";
//	}
	@PostMapping("/upload/file")
	    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile,HttpServletRequest request) {
	        try {
	        	String name=multipartFile.getOriginalFilename();
	        	String dir = request.getParameter("temp");
	        	uploadService.uploadExcel(multipartFile, dir);
	        	
	        	uts.index();
	        	System.out.println("OK");
	            return ResponseEntity.ok("文件上传成功: " + name);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("文件上传失败");
	        }
	    }
	}


