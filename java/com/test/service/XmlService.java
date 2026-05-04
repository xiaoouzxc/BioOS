package com.test.service;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


@Service
public class XmlService {
	@Value("${file.path}")
    private String filePath; // 从配置文件读取路径

	private XStream getNewStream() {
		 XStream xStream = new XStream();
         xStream.alias("standard", Standard.class); 
         xStream.alias("testItem", TestItem.class);
         xStream.alias("method", Method.class);// 配置别名
         xStream.alias("methodProceed", MethodProceed.class);
    
         xStream.addImplicitCollection(Standard.class, "testItem");
         xStream.addImplicitCollection(TestItem.class, "method");
         xStream.addImplicitCollection(Method.class, "methodProceed");
         xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
		return xStream;
		
		
	}
	 // 读取现有XML数据
    @SuppressWarnings("unchecked")
	public List<Standard> readXmlData() {
    	 try {
             File file = new File(filePath);
             if (!file.exists()) {
            	 String defaultXmlContent = "<list>\r\n"
                 		+ "    <standard>\r\n"
                 		+ "        <standardNumber>12345</standardNumber>\r\n"
                 		+ "        <testItem>\r\n"
                 		+ "            <testItem>检测项目1</testItem>\r\n"
                 		+ "            <method>\r\n"
                 		+ "                <method>方法1</method>\r\n"
                 		+ "                <methodProceed>\r\n"
                 		+ "                 <methodProceed>过程描述</methodProceed>\r\n"
                 		+ "                 <unit>单位1</unit>\r\n"
                 		+ "                 <dilution>稀释度1</dilution>\r\n"
                 		+ "                 <highlight>标识信息1</highlight>\r\n"
                 		+ "                 <quantity>10</quantity>\r\n"
                 		+ "                 <addition>添加剂1</addition>\r\n"
                 		+ "                 <search>筛选信息1</search>\r\n"
                 		+ "                 <medium>培养基1</medium>\r\n"
                 		+ "                </methodProceed>\r\n"
                 		+ "            </method>\r\n"
                 		+ "        </testItem>\r\n"
                 		+ "    </standard>\r\n"
                 		+ "</list>";
                 Files.write(file.toPath(), defaultXmlContent.getBytes(StandardCharsets.UTF_8));
             }

             XStream xStream = new XStream();
             
             xStream.setClassLoader(Standard.class.getClassLoader()); // 设置类加载器
             xStream.alias("standard", Standard.class); 
             xStream.alias("testItem", TestItem.class);
             xStream.alias("method", Method.class);// 配置别名
             xStream.alias("methodProceed", MethodProceed.class);
             //xStream.aliasField("testItem", Standard.class, "testItem");
             xStream.addImplicitCollection(Standard.class, "testItem");
             //xStream.aliasField("method", TestItem.class, "method");
              // 告诉 XStream testItem 是集合
             xStream.addImplicitCollection(TestItem.class, "method"); // 告诉 XStream testItem 是集合
             //xStream.aliasField("methodProceed", Method.class, "methodProceed");
             xStream.addImplicitCollection(Method.class, "methodProceed");
             xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
            
            // xStream.allowTypes(new Class[] { Standard.class });

             // 读取并解析XML数据，并将其转换为一个可变的 ArrayList
             return (ArrayList<Standard>) xStream.fromXML(new FileInputStream(file));
         } catch (Exception e) {
             e.printStackTrace();
             return new ArrayList<>();  // 读取失败时返回一个空的可变集合
         }
    }

    // 将新数据追加到 XML 中
    public void appendstandardToXml(String standardNumber) {
    	try {
            File file = new File(filePath);

            // 如果文件不存在，创建文件
            if (!file.exists()) {
                String defaultXmlContent = "<list>\r\n"
                		+ "    <standard>\r\n"
                		+ "        <standardNumber>12345</standardNumber>\r\n"
                		+ "        <testItem>\r\n"
                		+ "            <testItem>检测项目1</testItem>\r\n"
                		+ "            <method>\r\n"
                		+ "                <method>方法1</method>\r\n"
                		+ "                <methodProceed>\r\n"
                		+ "                 <methodProceed>过程描述</methodProceed>\r\n"
                		+ "                 <unit>单位1</unit>\r\n"
                		+ "                 <dilution>稀释度1</dilution>\r\n"
                		+ "                 <highlight>标识信息1</highlight>\r\n"
                		+ "                 <quantity>10</quantity>\r\n"
                		+ "                 <addition>添加剂1</addition>\r\n"
                		+ "                 <search>筛选信息1</search>\r\n"
                		+ "                 <medium>培养基1</medium>\r\n"
                		+ "                </methodProceed>\r\n"
                		+ "            </method>\r\n"
                		+ "        </testItem>\r\n"
                		+ "    </standard>\r\n"
                		+ "</list>";
                Files.write(file.toPath(), defaultXmlContent.getBytes(StandardCharsets.UTF_8));
            }

            XStream xStream = new XStream();
            xStream.alias("standard", Standard.class); 
            xStream.alias("testItem", TestItem.class);
            xStream.alias("method", Method.class);// 配置别名
            xStream.alias("methodProceed", MethodProceed.class);
           // xStream.aliasField("testItem", Standard.class, "testItem");
           // xStream.aliasField("method", TestItem.class, "method");
            //xStream.aliasField("methodProceed", MethodProceed.class, "methodProceed");
            xStream.addImplicitCollection(Standard.class, "testItem");
            xStream.addImplicitCollection(TestItem.class, "method");
            xStream.addImplicitCollection(Method.class, "methodProceed");
            xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
            // 读取现有数据并转换为可变集合
            List<Standard> existingData = readXmlData();
            //预设MethodProceed对象
            List<Standard> newStandards = new ArrayList<>();
            boolean isDuplicate = false;

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                    isDuplicate = true; // 标准号已存在
                    break;
                }
            }
           
            	
            	if(!isDuplicate) {
            		MethodProceed mp=new MethodProceed();
                    mp.setAddition("添加剂");
                    mp.setDilution("稀释度");
                    mp.setHighlight("标识");
                    mp.setMedium("培养基");
                    mp.setMethodProceed("过程描述");
                    mp.setQuantity("培养基添加量");
                    mp.setSearch("筛选信息");
                    mp.setUnit("单位");
                    ArrayList<MethodProceed> MethodProceedList= new ArrayList<MethodProceed>();
                    MethodProceedList.add(mp);
                
                
                //预设Method对象
                	 Method method=new Method();
                	 method.setMethod("方法名称");
                	 method.setMethodProceeds(MethodProceedList);
                	 ArrayList<Method> MethodList= new ArrayList<Method>();
                	 MethodList.add(method);
                //预设TestItem对象
                	 TestItem testItem=new TestItem();
                	 testItem.setTestItem("检测项目");
                	 testItem.setMethods(MethodList);
                	 ArrayList<TestItem> testItemList= new ArrayList<TestItem>();
                	 testItemList.add(testItem);
                //预设Standard对象
                	 Standard newstandard= new Standard();
                     newstandard.setStandardNumber(standardNumber);
                     newstandard.setTestItems(testItemList);
                     newStandards.add(newstandard);


                
            	}
            	// 将新数据追加到现有数据中
                existingData.addAll(newStandards);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 使用可变集合写回XML文件
                    
                
            	
            }
            
            	

            // 将新的数据写入文件
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removefromXML(String standardNumber) {
    	File file = new File(filePath);
    	 XStream xStream = new XStream();
         xStream.alias("standard", Standard.class); 
         xStream.alias("testItem", TestItem.class);
         xStream.alias("method", Method.class);// 配置别名
         xStream.alias("methodProceed", MethodProceed.class);
        
         xStream.addImplicitCollection(Standard.class, "testItem");
         xStream.addImplicitCollection(TestItem.class, "method");
         xStream.addImplicitCollection(Method.class, "methodProceed");
         xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
      // 读取现有数据并转换为可变集合
         List<Standard> existingData = readXmlData();
         boolean isDuplicate = false;
         
         int removedstdindex=0;

         for (Standard std : existingData) {
        	 
             if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
            	 
                 isDuplicate = true; // 标准号已存在
                
                 break;
             }
             removedstdindex++;
         }
         if(isDuplicate) {
        	 
        	 existingData.remove(removedstdindex);
        	 try (FileOutputStream fos = new FileOutputStream(file)) {
                 xStream.toXML(existingData, fos);  // 使用可变集合写回XML文件
 	
         } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         }
    }
    
    
 // 将新数据追加到 XML 中
    public void appendTestItemToXml(String standardNumber) {
    	try {
            File file = new File(filePath);

           

            XStream xStream=getNewStream();
            // 读取现有数据并转换为可变集合
            List<Standard> existingData = readXmlData();
            //预设MethodProceed对象
           
            boolean isDuplicate = false;
            int standardIndex=0;

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                	
                		
                			isDuplicate = true; // 标准号已存在
                			break;        
            }
                standardIndex++;
            }
            	System.out.println(isDuplicate);
            	if(isDuplicate) {
            		MethodProceed mp=new MethodProceed();
                    mp.setAddition("添加剂");
                    mp.setDilution("稀释度");
                    mp.setHighlight("标识");
                    mp.setMedium("培养基");
                    mp.setMethodProceed("过程描述");
                    mp.setQuantity("培养基添加量");
                    mp.setSearch("筛选信息");
                    mp.setUnit("单位");
                    ArrayList<MethodProceed> MethodProceedList= new ArrayList<MethodProceed>();
                    MethodProceedList.add(mp);
                
                
                //预设Method对象
                	 Method method=new Method();
                	 method.setMethod("方法名称");
                	 method.setMethodProceeds(MethodProceedList);
                	 ArrayList<Method> MethodList= new ArrayList<Method>();
                	 MethodList.add(method);
                //预设TestItem对象
                	 TestItem testItem=new TestItem();
                	 testItem.setTestItem("检测项目");
                	 testItem.setMethods(MethodList);
                	 
                	 
                	// 将新数据追加到现有standard.testItem数据中
                	 existingData.get(standardIndex).getTestItem().add(testItem);
                	 System.out.println("tianjiale");


                
            	}
            	// 将新数据追加到现有数据中
               
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 使用可变集合写回XML文件
                    
                
            	
            }
            
            	

            // 将新的数据写入文件
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    
    
    
    
    
    
    
    
    
    public void updateStandardField(String standardNumber,String id, String field, String value) throws Exception {
    	File file = new File(filePath);
    	 XStream xStream = getNewStream();
         xStream.alias("standard", Standard.class);  // 配置别名
        List<Standard> standards = readXmlData();
       
        
        // 更新对应的字段值
        for (Standard standard : standards) {
        	
            if (String.valueOf(standard.getStandardNumber()).equals(standardNumber)) {
            	 switch (field) {
                 
                 case "testItem":
                 	
                     for(TestItem testItem:standard.getTestItem()) {
                    	 if(String.valueOf(testItem.getTestItem()).equals(id)) {
                    		 testItem.setTestItem(value);
                    	 }
                     }
                     break;
//                 case "method":
//                     standard.setMethod(value);
//                     break;
//                 case "unit":
//                     standard.setUnit(value);
//                     break;
//                 case "dilution":
//                     standard.setDilution(value);
//                     break;
//                 case "highlight":
//                     standard.setHighlight(value);
//                     break;
//                 case "medium":
//                     standard.setMedium(value);
//                     break;
//                 case "quantity":
//                     standard.setQuantity(value);
//                     break;
//                 case "addition":
//                     standard.setAddition(value);
//                     break;
//                 case "search":
//                     standard.setSearch(value);
//                     break;
                 default:
                     throw new IllegalArgumentException("未知字段: " + field);
             }
            	
            		
            	}
            	
            	
            	
            	
            	
            	
            	
               
            }
        

        // 将修改后的对象写回 XML 文件
        try (FileOutputStream fos = new FileOutputStream(file)) {
            xStream.toXML(standards, fos);  // 使用可变集合写回XML文件
            
        }
    }
}
