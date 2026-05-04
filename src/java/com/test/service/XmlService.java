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
    private String filePath; // 浠庨厤缃枃浠惰鍙栬矾寰?

	private XStream getNewStream() {
		 XStream xStream = new XStream();
         xStream.alias("standard", Standard.class); 
         xStream.alias("testItem", TestItem.class);
         xStream.alias("method", Method.class);// 閰嶇疆鍒悕
         xStream.alias("methodProceed", MethodProceed.class);
    
         xStream.addImplicitCollection(Standard.class, "testItem");
         xStream.addImplicitCollection(TestItem.class, "method");
         xStream.addImplicitCollection(Method.class, "methodProceed");
         xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
		return xStream;
		
		
	}
	 // 璇诲彇鐜版湁XML鏁版嵁
    @SuppressWarnings("unchecked")
	public List<Standard> readXmlData() {
    	 try {
             File file = new File(filePath);
             if (!file.exists()) {
            	 String defaultXmlContent = "<list>\r\n"
                 		+ "    <standard>\r\n"
                 		+ "        <standardNumber>12345</standardNumber>\r\n"
                 		+ "        <testItem>\r\n"
                 		+ "            <testItem>妫€娴嬮」鐩?</testItem>\r\n"
                 		+ "            <method>\r\n"
                 		+ "                <method>鏂规硶1</method>\r\n"
                 		+ "                <methodProceed>\r\n"
                 		+ "                 <methodProceed>杩囩▼鎻忚堪</methodProceed>\r\n"
                 		+ "                 <unit>鍗曚綅1</unit>\r\n"
                 		+ "                 <dilution>绋€閲婂害1</dilution>\r\n"
                 		+ "                 <highlight>鏍囪瘑淇℃伅1</highlight>\r\n"
                 		+ "                 <quantity>10</quantity>\r\n"
                 		+ "                 <addition>娣诲姞鍓?</addition>\r\n"
                 		+ "                 <search>绛涢€変俊鎭?</search>\r\n"
                 		+ "                 <medium>鍩瑰吇鍩?</medium>\r\n"
						+ "                 <cultureTime>/</cultureTime>\r\n"
                 		+ "                </methodProceed>\r\n"
                 		+ "            </method>\r\n"
                 		+ "        </testItem>\r\n"
                 		+ "    </standard>\r\n"
                 		+ "</list>";
                 Files.write(file.toPath(), defaultXmlContent.getBytes(StandardCharsets.UTF_8));
             }

             XStream xStream = new XStream();
             
             xStream.setClassLoader(Standard.class.getClassLoader()); // 璁剧疆绫诲姞杞藉櫒
             xStream.alias("standard", Standard.class); 
             xStream.alias("testItem", TestItem.class);
             xStream.alias("method", Method.class);// 閰嶇疆鍒悕
             xStream.alias("methodProceed", MethodProceed.class);
             //xStream.aliasField("testItem", Standard.class, "testItem");
             xStream.addImplicitCollection(Standard.class, "testItem");
             //xStream.aliasField("method", TestItem.class, "method");
              // 鍛婅瘔 XStream testItem 鏄泦鍚?
             xStream.addImplicitCollection(TestItem.class, "method"); // 鍛婅瘔 XStream testItem 鏄泦鍚?
             //xStream.aliasField("methodProceed", Method.class, "methodProceed");
             xStream.addImplicitCollection(Method.class, "methodProceed");
             xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
            
            // xStream.allowTypes(new Class[] { Standard.class });

             // 璇诲彇骞惰В鏋怷ML鏁版嵁锛屽苟灏嗗叾杞崲涓轰竴涓彲鍙樼殑 ArrayList
             return (ArrayList<Standard>) xStream.fromXML(new FileInputStream(file));
         } catch (Exception e) {
             e.printStackTrace();
             return new ArrayList<>();  // 璇诲彇澶辫触鏃惰繑鍥炰竴涓┖鐨勫彲鍙橀泦鍚?
         }
    }

    // 灏嗘柊鏁版嵁杩藉姞鍒?XML 涓?
    public void appendstandardToXml(String standardNumber) {
    	try {
            File file = new File(filePath);

            // 濡傛灉鏂囦欢涓嶅瓨鍦紝鍒涘缓鏂囦欢
            if (!file.exists()) {
                String defaultXmlContent = "<list>\r\n"
                		+ "    <standard>\r\n"
                		+ "        <standardNumber>12345</standardNumber>\r\n"
                		+ "        <testItem>\r\n"
                		+ "            <testItem>妫€娴嬮」鐩?</testItem>\r\n"
                		+ "            <method>\r\n"
                		+ "                <method>鏂规硶1</method>\r\n"
                		+ "                <methodProceed>\r\n"
                		+ "                 <methodProceed>杩囩▼鎻忚堪</methodProceed>\r\n"
                		+ "                 <unit>鍗曚綅1</unit>\r\n"
                		+ "                 <dilution>绋€閲婂害1</dilution>\r\n"
                		+ "                 <highlight>鏍囪瘑淇℃伅1</highlight>\r\n"
                		+ "                 <quantity>10</quantity>\r\n"
                		+ "                 <addition>娣诲姞鍓?</addition>\r\n"
                		+ "                 <search>绛涢€変俊鎭?</search>\r\n"
                		+ "                 <medium>鍩瑰吇鍩?</medium>\r\n"
						+ "                 <cultureTime>/</cultureTime>\r\n"
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
            xStream.alias("method", Method.class);// 閰嶇疆鍒悕
            xStream.alias("methodProceed", MethodProceed.class);
           // xStream.aliasField("testItem", Standard.class, "testItem");
           // xStream.aliasField("method", TestItem.class, "method");
            //xStream.aliasField("methodProceed", MethodProceed.class, "methodProceed");
            xStream.addImplicitCollection(Standard.class, "testItem");
            xStream.addImplicitCollection(TestItem.class, "method");
            xStream.addImplicitCollection(Method.class, "methodProceed");
            xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
            // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
            List<Standard> existingData = readXmlData();
            //棰勮MethodProceed瀵硅薄
            List<Standard> newStandards = new ArrayList<>();
            boolean isDuplicate = false;

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                    isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                    break;
                }
            }
           
            	
            	if(!isDuplicate) {
            		MethodProceed mp=new MethodProceed();
                    mp.setAddition("/");
                    mp.setDilution("/");
                    mp.setHighlight("/");
                    mp.setMedium("/");
                    mp.setCultureTime("/");
                    mp.setMethodProceed("/");
                    mp.setQuantity("/");
                    mp.setSearch("/");
                    mp.setUnit("/");
                    ArrayList<MethodProceed> MethodProceedList= new ArrayList<MethodProceed>();
                    MethodProceedList.add(mp);
                
                
                //棰勮Method瀵硅薄
                	 Method method=new Method();
                	 method.setMethod("鏂规硶鍚嶇О");
                	 method.setMethodProceeds(MethodProceedList);
                	 ArrayList<Method> MethodList= new ArrayList<Method>();
                	 MethodList.add(method);
                //棰勮TestItem瀵硅薄
                	 TestItem testItem=new TestItem();
                	 testItem.setTestItem("检测项目");
                	 testItem.setMethods(MethodList);
                	 ArrayList<TestItem> testItemList= new ArrayList<TestItem>();
                	 testItemList.add(testItem);
                //棰勮Standard瀵硅薄
                	 Standard newstandard= new Standard();
                     newstandard.setStandardNumber(standardNumber);
                     newstandard.setTestItems(testItemList);
                     newStandards.add(newstandard);


                
            	}
            	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈夋暟鎹腑
                existingData.addAll(newStandards);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
                    
                
            	
            }
            
            	

            // 灏嗘柊鐨勬暟鎹啓鍏ユ枃浠?
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void removefromXML(String standardNumber) {
    	File file = new File(filePath);
    	 XStream xStream = new XStream();
         xStream.alias("standard", Standard.class); 
         xStream.alias("testItem", TestItem.class);
         xStream.alias("method", Method.class);// 閰嶇疆鍒悕
         xStream.alias("methodProceed", MethodProceed.class);
        
         xStream.addImplicitCollection(Standard.class, "testItem");
         xStream.addImplicitCollection(TestItem.class, "method");
         xStream.addImplicitCollection(Method.class, "methodProceed");
         xStream.allowTypesByWildcard(new String[] { "com.xml.standards.**" });
      // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
         List<Standard> existingData = readXmlData();
         boolean isDuplicate = false;
         
         int removedstdindex=0;

         for (Standard std : existingData) {
        	 
             if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
            	 
                 isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                
                 break;
             }
             removedstdindex++;
         }
         if(isDuplicate) {
        	 
        	 existingData.remove(removedstdindex);
        	 try (FileOutputStream fos = new FileOutputStream(file)) {
                 xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
 	
         } catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         }
    }
    
    
 // 灏嗘柊鏁版嵁TestItem杩藉姞鍒?XML 涓?
    public void appendTestItemToXml(String standardNumber) {
    	try {
            File file = new File(filePath);

           

            XStream xStream=getNewStream();
            // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
            List<Standard> existingData = readXmlData();
            //棰勮MethodProceed瀵硅薄
           
            boolean isDuplicate = false;
            int standardIndex=0;

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                	
                		
                			isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                			break;        
            }
                standardIndex++;
            }
            	System.out.println(isDuplicate);
            	if(isDuplicate) {
            		MethodProceed mp=new MethodProceed();
                    mp.setAddition("/");
                    mp.setDilution("/");
                    mp.setHighlight("/");
                    mp.setMedium("/");
                    mp.setCultureTime("/");
                    mp.setMethodProceed("/");
                    mp.setQuantity("/");
                    mp.setSearch("/");
                    mp.setUnit("/");
                    ArrayList<MethodProceed> MethodProceedList= new ArrayList<MethodProceed>();
                    MethodProceedList.add(mp);
                
                
                //棰勮Method瀵硅薄
                	 Method method=new Method();
                	 method.setMethod("鏂规硶鍚嶇О");
                	 method.setMethodProceeds(MethodProceedList);
                	 ArrayList<Method> MethodList= new ArrayList<Method>();
                	 MethodList.add(method);
                //棰勮TestItem瀵硅薄
                	 TestItem testItem=new TestItem();
                	 testItem.setTestItem("检测项目");
                	 testItem.setMethods(MethodList);
                	 
                	 
                	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈塻tandard.testItem鏁版嵁涓?
                	 existingData.get(standardIndex).getTestItem().add(testItem);
                	 System.out.println("tianjiale");


                
            	}
            	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈夋暟鎹腑
               
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
                    
                
            	
            }
            
            	

            // 灏嗘柊鐨勬暟鎹啓鍏ユ枃浠?
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // 灏嗘柊鏁版嵁TestItem浠?XML 涓垹闄?
    public void removeTestItemfromXML(String standardNumber,String testItemsort) {
    	File file = new File(filePath);
    	XStream xStream=getNewStream();
      // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
         List<Standard> existingData = readXmlData();
         boolean isDuplicate = false;
         
         int removedstdindex=0;

         for (Standard std : existingData) {
        	 
             if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
            	 
                 isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                
                 break;
             }
             removedstdindex++;
         }
         if(isDuplicate) {
        	 ArrayList<TestItem> testItems=(ArrayList<TestItem>) existingData.get(removedstdindex).getTestItem();
        	 if(testItems.size()>1) {
        		 existingData.get(removedstdindex).getTestItem().remove(Integer.parseInt(testItemsort));
        		 try (FileOutputStream fos = new FileOutputStream(file)) {
                     xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
     	
             } catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	 }
        	 
        	
         
         }
    }
 // 灏嗘柊鏁版嵁Method杩藉姞鍒?XML 涓?
    public void appendMethodToXml(String standardNumber,String testItemsort) {
    	try {
            File file = new File(filePath);

           

            XStream xStream=getNewStream();
            // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
            List<Standard> existingData = readXmlData();
            //棰勮MethodProceed瀵硅薄
           
            boolean isDuplicate = false;
            int standardIndex=0;

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                	
                		
                			isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                			break;        
            }
                standardIndex++;
            }
            	System.out.println(isDuplicate);
            	if(isDuplicate) {
            		MethodProceed mp=new MethodProceed();
            		mp.setAddition("/");
                    mp.setDilution("/");
                    mp.setHighlight("/");
                    mp.setMedium("/");
                    mp.setCultureTime("/");
                    mp.setMethodProceed("/");
                    mp.setQuantity("/");
                    mp.setSearch("/");
                    mp.setUnit("/");
                    ArrayList<MethodProceed> MethodProceedList= new ArrayList<MethodProceed>();
                    MethodProceedList.add(mp);
                
                
                //棰勮Method瀵硅薄
                	 Method method=new Method();
                	 method.setMethod("鏂规硶鍚嶇О");
                	 method.setMethodProceeds(MethodProceedList);
                	 ArrayList<Method> MethodList= new ArrayList<Method>();
                	 MethodList.add(method);
                
     	 
                	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈塻tandard.testItem鏁版嵁涓?
                	 existingData.get(standardIndex).getTestItem().get(Integer.parseInt(testItemsort)).getMethod().add(method);
                	 System.out.println("tianjiale");


                
            	}
            	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈夋暟鎹腑
               
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
                    
                
            	
            }
            
            	

            // 灏嗘柊鐨勬暟鎹啓鍏ユ枃浠?
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 // 灏嗘柊鏁版嵁Method浠?XML 涓垹闄?
    public void removeMethodfromXML(String standardNumber,String methodsort) {
    	File file = new File(filePath);
    	XStream xStream=getNewStream();
    	String[] sMethodID=methodsort.split("-");
      // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
         List<Standard> existingData = readXmlData();
         boolean isDuplicate = false;
         
         int removedstdindex=0;

         for (Standard std : existingData) {
        	 
             if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
            	 
                 isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                
                 break;
             }
             removedstdindex++;
         }
         if(isDuplicate) {
           ArrayList<Method> MethodList= (ArrayList<Method>) existingData.get(removedstdindex).getTestItem().get(Integer.parseInt(sMethodID[0])).getMethod();
        	 if(MethodList.size()>1) {
        		 existingData.get(removedstdindex).getTestItem().get(Integer.parseInt(sMethodID[0])).getMethod().remove(Integer.parseInt(sMethodID[0]));
        		 try (FileOutputStream fos = new FileOutputStream(file)) {
                     xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
     	
             } catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	 }
        	 
        	
         
         }
    }
 // 灏嗘柊鏁版嵁MethodProceed杩藉姞鍒?XML 涓?
    public void appendMethodProceedToXml(String standardNumber,String methodsort) {
    	try {
            File file = new File(filePath);

           

            XStream xStream=getNewStream();
            // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
            List<Standard> existingData = readXmlData();
            //棰勮MethodProceed瀵硅薄
           
            boolean isDuplicate = false;
            int standardIndex=0;
            String[] sMethodID=methodsort.split("-");

            for (Standard std : existingData) {
                if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
                	
                		
                			isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                			break;        
            }
                standardIndex++;
            }
            	System.out.println(isDuplicate);
            	if(isDuplicate) {
            		MethodProceed mp=new MethodProceed();
            		mp.setAddition("/");
                    mp.setDilution("/");
                    mp.setHighlight("/");
                    mp.setMedium("/");
                    mp.setCultureTime("/");
                    mp.setMethodProceed("/");
                    mp.setQuantity("/");
                    mp.setSearch("/");
                    mp.setUnit("/");
  //                  ArrayList<MethodProceed> MethodProceedList= (ArrayList<MethodProceed>) existingData.get(standardIndex).getTestItem().get(Integer.parseInt(sMethodID[0])).getMethod().get(Integer.parseInt(sMethodID[1])).getMethodProceed()
                    
                

                	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈塻tandard.testItem鏁版嵁涓?
                	 existingData.get(standardIndex).getTestItem().get(Integer.parseInt(sMethodID[0])).getMethod().get(Integer.parseInt(sMethodID[1])).getMethodProceed().add(mp);
                	 System.out.println("tianjiale");


                
            	}
            	// 灏嗘柊鏁版嵁杩藉姞鍒扮幇鏈夋暟鎹腑
               
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
                    
                
            	
            }
            
            	

            // 灏嗘柊鐨勬暟鎹啓鍏ユ枃浠?
            

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
 // 灏嗘柊鏁版嵁MethodProceed浠?XML 涓垹闄?
    public void removeMethodProceedfromXML(String standardNumber,String methodProceedsort) {
    	File file = new File(filePath);
    	XStream xStream=getNewStream();
    	String[] sMethodProceedID=methodProceedsort.split("-");
      // 璇诲彇鐜版湁鏁版嵁骞惰浆鎹负鍙彉闆嗗悎
         List<Standard> existingData = readXmlData();
         boolean isDuplicate = false;
         
         int removedstdindex=0;

         for (Standard std : existingData) {
        	 
             if (standardNumber.equalsIgnoreCase(std.getStandardNumber())) {
            	 
                 isDuplicate = true; // 鏍囧噯鍙峰凡瀛樺湪
                
                 break;
             }
             removedstdindex++;
         }
         if(isDuplicate) {
           ArrayList<MethodProceed> methodProceedList= (ArrayList<MethodProceed>) existingData.get(removedstdindex).getTestItem().get(Integer.parseInt(sMethodProceedID[0])).getMethod().get(Integer.parseInt(sMethodProceedID[1])).getMethodProceed();
        	 if(methodProceedList.size()>1) {
        		 existingData.get(removedstdindex).getTestItem().get(Integer.parseInt(sMethodProceedID[0])).getMethod().get(Integer.parseInt(sMethodProceedID[1])).getMethodProceed().remove(Integer.parseInt(sMethodProceedID[2]));
        		 try (FileOutputStream fos = new FileOutputStream(file)) {
                     xStream.toXML(existingData, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
     	
             } catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	 }
        	 
        	
         
         }
    }
    
    
    
    public void updateStandardField(String standardNumber,String id, String field, String value) throws Exception {
    	File file = new File(filePath);
    	 XStream xStream = getNewStream();
         xStream.alias("standard", Standard.class);  // 閰嶇疆鍒悕
        List<Standard> standards = readXmlData();
     
        
        // 鏇存柊瀵瑰簲鐨勫瓧娈靛€?
        for (Standard standard : standards) {
        	
            if (String.valueOf(standard.getStandardNumber()).equals(standardNumber)) {
            	 switch (field) {
                 
                 case "testItem":
                	
                	 standard.getTestItem().get(Integer.parseInt(id)).setTestItem(value);;

                     break;
                 case "method":
                	 String[] sMethod=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(sMethod[0])).getMethod().get(Integer.parseInt(sMethod[1])).setMethod(value);   	 
                     break;
                 case "methodProceed":
                	 System.out.println(id);
                	 String[] sMethodProceed=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(sMethodProceed[0])).getMethod().get(Integer.parseInt(sMethodProceed[1])).getMethodProceed().get(Integer.parseInt(sMethodProceed[2])).setMethodProceed(value);   	 
                      
                      break;
                 case "unit":
                	 String[] sunit=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(sunit[0])).getMethod().get(Integer.parseInt(sunit[1])).getMethodProceed().get(Integer.parseInt(sunit[2])).setUnit(value); 
                       
                      break;
                 case "dilution":
                	 String[] sdilution=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(sdilution[0])).getMethod().get(Integer.parseInt(sdilution[1])).getMethodProceed().get(Integer.parseInt(sdilution[2])).setDilution(value); 
                       
                     break;
                 case "highlight":
                	 String[] shighlight=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(shighlight[0])).getMethod().get(Integer.parseInt(shighlight[1])).getMethodProceed().get(Integer.parseInt(shighlight[2])).setHighlight(value); 
                     break;
                 case "medium":
                	 String[] smedium=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(smedium[0])).getMethod().get(Integer.parseInt(smedium[1])).getMethodProceed().get(Integer.parseInt(smedium[2])).setMedium(value); 
                	
                     break;
                 case "quantity":
                	 String[] squantity=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(squantity[0])).getMethod().get(Integer.parseInt(squantity[1])).getMethodProceed().get(Integer.parseInt(squantity[2])).setQuantity(value); 
                	 
                     break;
                 case "addition":
                	 String[] saddition=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(saddition[0])).getMethod().get(Integer.parseInt(saddition[1])).getMethodProceed().get(Integer.parseInt(saddition[2])).setAddition(value); 
                	 
                     break;
                 case "search":
                	 String[] ssearch=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(ssearch[0])).getMethod().get(Integer.parseInt(ssearch[1])).getMethodProceed().get(Integer.parseInt(ssearch[2])).setSearch(value);                 	 
                     break;
                 case "cultureTime":
                	 String[] scultureTime=id.split("-");
                	 standard.getTestItem().get(Integer.parseInt(scultureTime[0])).getMethod().get(Integer.parseInt(scultureTime[1])).getMethodProceed().get(Integer.parseInt(scultureTime[2])).setCultureTime(value);
                     break;
                 default:
                     throw new IllegalArgumentException("鏈煡瀛楁: " + field);
             }
            	
            		
            	}
            	
            }

        // 灏嗕慨鏀瑰悗鐨勫璞″啓鍥?XML 鏂囦欢
        try (FileOutputStream fos = new FileOutputStream(file)) {
            xStream.toXML(standards, fos);  // 浣跨敤鍙彉闆嗗悎鍐欏洖XML鏂囦欢
            
        }
    }
}
