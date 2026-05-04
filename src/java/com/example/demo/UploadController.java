package com.example.demo;

import com.pcr.integration.starlims.dto.StarLimsSampleImportResult;
import com.test.service.UploadService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@ComponentScan("com.test.service")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @GetMapping("/upload")
    public String toupload() {
        return "upload";
    }

    @PostMapping("/upload/file")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile multipartFile,
                                             HttpServletRequest request) {
        try {
            String name = multipartFile.getOriginalFilename();
            String dir = request.getParameter("temp");
            StarLimsSampleImportResult importResult = uploadService.uploadExcel(multipartFile, dir);

            return ResponseEntity.ok("文件上传成功: " + name
                    + "，导入表: " + importResult.getTableName()
                    + "，导入数量: " + importResult.getImportedCount()
                    + "，顺序: " + importResult.getSequence());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("文件上传失败");
        }
    }
}
