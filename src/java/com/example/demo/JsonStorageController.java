package com.example.demo;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.service.JsonInfoService;
import com.xml.standards.CustomeRecordinfoInstant;




@RestController
@RequestMapping("/api/json")
public class JsonStorageController {

    @Autowired
    private JsonInfoService jsonStorageService;

    // 保存数据
    @PostMapping("/save")
    public String saveData(@RequestBody List<CustomeRecordinfoInstant> userDataList) {
        try {
            jsonStorageService.saveData(userDataList);
            return "数据保存成功！";
        } catch (IOException e) {
            e.printStackTrace();
            return "数据保存失败：" + e.getMessage();
        }
    }

    // 获取数据
    @GetMapping("/read")
    public List<CustomeRecordinfoInstant> readData() {
        try {
            return jsonStorageService.readData();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
