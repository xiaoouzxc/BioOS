package com.example.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.service.XmlService;
import com.xml.standards.Method;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

@Controller
public class StandardController {

    @Autowired
    private XmlService xmlService;
   
    
    @GetMapping("/standard")
    public String showNewsdPage(Model model) {
    	ArrayList<Standard> standards=new ArrayList<Standard>();
    	 // 从 XML 文件读取数据
         standards = (ArrayList<Standard>) xmlService.readXmlData();
        
        // 将数据传递给前端页面
        model.addAttribute("standards", standards);
        // 返回 templates 下的 newsd.html 模板
        return "standards/newStandard";
    }
    @GetMapping("/getStandards")
    @ResponseBody
    public List<Standard> getStandards() {
        // 使用 XmlService 从 XML 文件读取数据
    	 List<Standard> standards = xmlService.readXmlData();
//    	 for (Standard standard : standards) {
//    	        if (!(standard.getTestItem() instanceof List)) {
//    	            // 如果 testItem 是单个对象，转为列表
//    	            standard.setTestItems(Arrays.asList(standard.getTestItem()));
//    	        }
//    	        if (!(standard.getTestItem().getMethod() instanceof List)) {
//    	            // 如果 testItem 是单个对象，转为列表
//    	            standard.getTestItem().setMethods(Arrays.asList(standard.getTestItem().getMethod()));
//    	        }
//    	        if (!(standard.getTestItem().getMethod().getMethodProceed() instanceof List)) {
//    	            // 如果 testItem 是单个对象，转为列表
//    	        	
//    	        	standard.getTestItem().getMethod().setMethodProceeds(Arrays.asList(standard.getTestItem().getMethod().getMethodProceed()));
//    	        }
//    	    }
        return standards;
    }

    @PostMapping("/addStandard")
    public String addStandard( @RequestParam("standardNumber") String standardNumber) {

        // 将新数据追加到 XML 文件中
        xmlService.appendstandardToXml(standardNumber);
        

        return "standards/newStandard";
    }
    @PostMapping("/removeStandard")
    public String removeStandard( @RequestParam("standardNumber") String standardNumber) {

        // 将新数据追加到 XML 文件中
        xmlService.removefromXML(standardNumber);
        

        return "standards/newStandard";
    }
    @PostMapping("/addTestItem")
    public String addTestItem(@RequestParam("standardNumber") String standardNumber) {

        // 将新数据追加到 XML 文件中
        xmlService.appendTestItemToXml(standardNumber);
        

        return "standards/newStandard";
    }
    @PostMapping("/removeTestItem")
    public String removeTestItem(@RequestParam("standardNumber") String standardNumber,@RequestParam("testItemsort") String testItemsort) {

        // 将新数据追加到 XML 文件中
        xmlService.removeTestItemfromXML(standardNumber,testItemsort);
        

        return "standards/newStandard";
    }
    @PostMapping("/addMethod")
    public String addMethod(@RequestParam("standardNumber") String standardNumber,@RequestParam("testItemsort") String testItemsort) {

        // 将新数据追加到 XML 文件中
        xmlService.appendMethodToXml(standardNumber,testItemsort);
        

        return "standards/newStandard";
    }
    @PostMapping("/removeMethod")
    public String removeMethod(@RequestParam("standardNumber") String standardNumber,@RequestParam("methodsort") String methodsort) {
 
        // 将新数据追加到 XML 文件中
        xmlService.removeMethodfromXML(standardNumber,methodsort);
        

        return "standards/newStandard";
    }
    
    @PostMapping("/addMethodProceed")
    public String addMethodProceed(@RequestParam("standardNumber") String standardNumber,@RequestParam("methodsort") String methodsort) {

        // 将新数据追加到 XML 文件中
        xmlService.appendMethodProceedToXml(standardNumber,methodsort);
        

        return "standards/newStandard";
    }
    @PostMapping("/removeMethodProceed")
    public String removeMethodProceed(@RequestParam("standardNumber") String standardNumber,@RequestParam("methodProceedsort") String methodProceedsort) {
    	
        // 将新数据追加到 XML 文件中
        xmlService.removeMethodProceedfromXML(standardNumber,methodProceedsort);
        

        return "standards/newStandard";
    }
    
    
    
    
    
    
    
    
    @PostMapping("/updateStandard")
    public ResponseEntity<String> updateStandard(
    		@RequestParam("standardNumber") String standardNumber,
    		@RequestParam("id") String id,                                    
    		@RequestParam("field") String field,                               
    		@RequestParam("value") String value) {
        try {
            // 更新 XML 文件中的数据
        	
        	xmlService.updateStandardField(standardNumber,id, field, value);
            return ResponseEntity.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("更新失败");
        }
    }
}