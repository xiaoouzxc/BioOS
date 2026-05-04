package com.test.service;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

	public void uploadExcel(MultipartFile mutipartFile, String dir) {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		File targetFile = new File(desktop + "\\" + dir);
		System.out.println(targetFile);
		try {
			if (!targetFile.exists())
				targetFile.mkdirs();
			File targetFileName = new File(targetFile + "\\1.xlsx");
			mutipartFile.transferTo(targetFileName);
			System.out.println("OK");
			//return "1";
		} catch (IOException e) {
			e.printStackTrace();
			//return "2";
		}

	}

}
