package com.example.demo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.boot.context.properties.PropertyMapper.Source;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
@RestController
public class PDFRecord {
	private  File[] numList= {
			
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/0.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/1.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/2.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/3.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/4.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/5.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/6.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/7.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/8.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/9.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/point.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/-.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/name.jpg").getFile()),
			new File(getClass().getClassLoader().getResource("static/image/randomePhoto/f.jpg").getFile())
			};
	
	@PostMapping("generate-pdf")
	public ResponseEntity<byte[]> creator(@RequestBody Map<String,Object> request) throws IOException {
		List<Integer> selectedDays =null;
		String name=null;
		
			selectedDays = (List<Integer>) request.get("selectedDays");
			 if (request.get("selectedDays") instanceof List<?>) {
		            List<?> rawSelectedDays = (List<?>) request.get("selectedDays");

		            // 将 rawSelectedDays 转换为 List<Integer>
		            selectedDays = rawSelectedDays.stream()
		                .map(day -> {
		                    if (day instanceof String) {
		                        return Integer.parseInt((String) day);
		                    } else if (day instanceof Integer) {
		                        return (Integer) day;
		                    } else {
		                        throw new IllegalArgumentException("Invalid day value");
		                    }
		                })
		                .collect(Collectors.toList());
		        }
			
		
		
			 name=(String) request.get("filename");
		
		URL url=null;
		if(name.isEmpty()) {
			url=getClass().getClassLoader().getResource("static/record/nullname.pdf");
		}else {
			System.out.println(name);
			url=getClass().getClassLoader().getResource("static/record/"+name);
		}

URL urlimg=getClass().getClassLoader().getResource("static/image/randomePhoto/0.png");
URL urlsave=getClass().getClassLoader().getResource("static/record/");

		// 创建一个空白的PDF文档
	   PDDocument document = PDDocument.load(new File(url.getFile()));
	    	
	        PDPage page = document.getPage(0); // 创建一页
	        PDImageXObject[] imangeObject= {
	        		PDImageXObject.createFromFileByContent(numList[0], document),
	        		PDImageXObject.createFromFileByContent(numList[1], document),
	        		PDImageXObject.createFromFileByContent(numList[2], document),
	        		PDImageXObject.createFromFileByContent(numList[3], document),
	        		PDImageXObject.createFromFileByContent(numList[4], document),
	        		PDImageXObject.createFromFileByContent(numList[5], document),
	        		PDImageXObject.createFromFileByContent(numList[6], document),
	        		PDImageXObject.createFromFileByContent(numList[7], document),
	        		PDImageXObject.createFromFileByContent(numList[8], document),
	        		PDImageXObject.createFromFileByContent(numList[9], document),
	        		PDImageXObject.createFromFileByContent(numList[10], document),
	        		PDImageXObject.createFromFileByContent(numList[11], document),
	        		PDImageXObject.createFromFileByContent(numList[12], document),
	        		PDImageXObject.createFromFileByContent(numList[13], document)
	        		};   
	       
	     // 添加图片
	       // File imageFile = new File(urlimg.getFile()); // 图片文件路径
	        float x = 100; // 图片的X坐标（单位是像素）
	        float y = 712; // 图片的Y坐标（单位是像素）
	        float width = 7; // 图片的宽度（单位是像素）
	        float height = 10; // 图片的高度（单位是像素）	       
	        // 生成 [min, max] 范围的随机整数
	        
	     PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);
	        	for(int i=0;i<31;i++) {
	        		if(selectedDays.contains(i+1)) {
	        			System.out.println(selectedDays.size());
	        			continue;
	        		}
	        		contentStream.drawImage(imangeObject[ThreadLocalRandom.current().nextInt(3, 6 + 1)], x, y-i*21.7f, width, height);		            
		            contentStream.drawImage(imangeObject[10], x+6f, y-i*21.7f, 3, 3);
		            contentStream.drawImage(imangeObject[ThreadLocalRandom.current().nextInt(0, 9 + 1)], x+9, y-i*21.7f, width, height);
		            contentStream.drawImage(imangeObject[12], 474, y-i*21.7f, 25, 10);
	        		
	        	}
	        	contentStream.close();
	        
	        // 保存PDF文件
	        	System.out.println("src/main/resources/static/record/"+name);
	        document.save("src/main/resources/static/record/output.pdf");
	        System.out.println();	        
	        
	        
	        // 将PDF内容保存为字节流
	        ByteArrayOutputStream output = new ByteArrayOutputStream();
	        document.save(output);
	        
	        document.close();
	        System.out.println("wanchengle");
	        // 返回生成的 PDF 文件
	        HttpHeaders headers = new HttpHeaders();
	        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=output.pdf");
	        headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");
	        return  new ResponseEntity<>(output.toByteArray(), headers, HttpStatus.OK);
			
	     
	    
		//return "noListError.html";
		
	    
	}
	
	 @GetMapping("list-files")
	    public List<String> listFiles() {
	        // 获取 static/record 目录
	        String directoryPath = new File("src/main/resources/static/record").getAbsolutePath();
	        File directory = new File(directoryPath);

	        // 返回文件和文件夹的列表
	        List<String> fileNames = new ArrayList<>();
	        if (directory.exists() && directory.isDirectory()) {
	        	
	            for (File file : directory.listFiles()) {
	                if (file.isFile()) {
	                    fileNames.add(file.getName());
	                }
	            }
	        }
	        return fileNames;
	    }
	
}
	 

        

       

       
       

