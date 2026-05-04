package com.test.service;

import java.io.File;
import java.io.IOException;

import javax.swing.filechooser.FileSystemView;

import com.pcr.integration.starlims.dto.StarLimsSampleImportResult;
import com.pcr.integration.starlims.service.StarLimsSampleImportService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

	private final StarLimsSampleImportService sampleImportService;

	public UploadService(StarLimsSampleImportService sampleImportService) {
		this.sampleImportService = sampleImportService;
	}

	public StarLimsSampleImportResult uploadExcel(MultipartFile mutipartFile, String dir) throws IOException {
		FileSystemView fsv = FileSystemView.getFileSystemView();
		String desktop = fsv.getHomeDirectory().getPath();
		if (dir == null || dir.trim().isEmpty()) {
			dir = "avatar";
		}
		File targetFile = new File(desktop + "\\" + dir);
		System.out.println(targetFile);
		if (!targetFile.exists())
			targetFile.mkdirs();
		File targetFileName = new File(targetFile + "\\1.xlsx");
		mutipartFile.transferTo(targetFileName);
		System.out.println("OK");
		return sampleImportService.importExcel(targetFileName);
	}

}
