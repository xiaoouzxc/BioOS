package com.test.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xml.standards.CustomeRecordinfoInstant;
@Service
public class JsonInfoService {
	@Value("${file_path_secondary}")
    private String filePath; // 从配置文件读取路径
	    private final ObjectMapper objectMapper = new ObjectMapper();

	    // 保存数据到本地文件
	    public void saveData(List<CustomeRecordinfoInstant> userDataList) throws IOException {
	    	 // 读取现有数据
	        List<CustomeRecordinfoInstant> existingData = readData();

	        // 合并新数据
	        existingData.addAll(userDataList);

	        // 保存到文件
	        objectMapper.writeValue(new File(filePath), existingData);
	    }

	    // 从本地文件读取数据
	    public List<CustomeRecordinfoInstant> readData() throws IOException {
	        File file = new File(filePath);
	        if (file.exists()) {
	            return objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, CustomeRecordinfoInstant.class));
	        }
	        return new ArrayList<>(); // 如果文件不存在，返回空列表
	    }

}
