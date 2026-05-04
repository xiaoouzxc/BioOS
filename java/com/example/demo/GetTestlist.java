package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class GetTestlist {
	SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");

	public GetTestlist() {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@GetMapping("/getList")
	public String getList(MultipartFile multipartfile, HttpServletRequest request) {
		String realPath = request.getSession().getServletContext().getRealPath("/update/");
		String format = sdf.format(new Date());
		String forderPath = realPath + format;
		Path forder = new File(forderPath).toPath();
		List<Object> filelist = new ArrayList<>();
		ArrayList<Object> fileList = new ArrayList<>();
		try {
			Files.walkFileTree(forder, new SimpleFileVisitor() {
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
					fileList.add(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file, IOException exc) {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filename = " ";
		for (int i = 0; i < fileList.size(); i++) {

			filename = filename + fileList.get(i);
		}
		return filename;

	}

}
