package com.exl.materialstanderd.read;

import java.util.ArrayList;

import com.example.demo.Simple;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

public class ReadFSheetXML {
	private String temp = null;
	private FormulaId f = new FormulaId();

	public String read(ArrayList<Simple> simpleList,ArrayList<Standard> Standard) {
		ArrayList<TestItem> testItemList=null;
		ArrayList<Method> methodList=null;
		ArrayList<MethodProceed> methodProceedList=null;
		
		String id = null;
		String standard = null;
		String item = null;
		String tip = null;
		String name = null;
		String unit = null;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < simpleList.size(); i++) {
			standard = simpleList.get(i).getTestMethod();
			name = simpleList.get(i).getName();
			item=simpleList.get(i).getTestItem();
			id = getLabelNumber(simpleList.get(i));
			unit=simpleList.get(i).getUnit();
			tip = simpleList.get(i).getTip();
			try {
				
boolean isBreakTriggered = false; // 标志变量
				
				for(int StandardIndex=0;StandardIndex<Standard.size();StandardIndex++) {
					if(standard.contains(Standard.get(StandardIndex).getStandardNumber())) {
						testItemList=(ArrayList<TestItem>) Standard.get(StandardIndex).getTestItem();
						for(TestItem testitem:testItemList) {
							if (item.equals(testitem.getTestItem())) {
								
								methodList=(ArrayList<Method>) testitem.getMethod();
								boolean methodisFound = false;
								
								for(Method method: methodList) {
									if(method.getMethod().equals("/")) {
										methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
										int initialIndex=0;
										int loop=0;
										boolean isFound = false; // 标志是否找到匹配的 proceed
										for(MethodProceed proceed:methodProceedList) {
																						
											if(name.contains(proceed.getSearch())) {
												sb.append(fomulaInfo( name, id,tip,proceed.getSearch(),proceed.getHighlight()));
												isBreakTriggered=true;
												isFound=true;
												break;
											}
											
										}
										if (!isFound) {
											 MethodProceed defaultProceed = methodProceedList.get(initialIndex);
											if(defaultProceed.getUnit().equalsIgnoreCase("cfu")) {
												sb.append(fomulaInfo( name, id,tip,defaultProceed.getSearch(),defaultProceed.getHighlight()));
												
												isBreakTriggered=true;					
												break;
											}
										}
										
										
										
									}else if(standard.contains(method.getMethod())) {
										
										
										
											methodisFound=true;
											methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
											int initialIndex=0;
											int loop=0;
											boolean isFound = false; // 标志是否找到匹配的 proceed
											for(MethodProceed proceed:methodProceedList) {
												
//												if(proceed.getSearch().equals("/")) {
//													initialIndex=loop;
//												}
//												loop++;
												if(name.contains(proceed.getSearch())) {
													sb.append(fomulaInfo( name, id,tip,proceed.getSearch(),proceed.getHighlight()));
													isBreakTriggered=true;
													isFound=true;
													break;
												}
												
											}
											if (!isFound) {
												 MethodProceed defaultProceed = methodProceedList.get(initialIndex);
												if(defaultProceed.getUnit().equalsIgnoreCase("cfu")) {
													sb.append(fomulaInfo( name, id,tip,defaultProceed.getSearch(),defaultProceed.getHighlight()));
													isBreakTriggered=true;					
													break;
												}
											}
										
									}
																					
				
									
								}
				
							
				}
					}
				}
				

			}
				if(unit!=null&&unit.toLowerCase().contains("cfu")) {
					if(!isBreakTriggered) {
						sb.append(fomulaInfo(name, id, tip,"/","CFU未定义"));
						
					}
				}


				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	private String getLabelNumber(Simple simple) {
		if (simple.getDailySampleOrder() == null) {
			return simple.getNumber();
		}
		return "[" + simple.getDailySampleOrder() + "]" + simple.getNumber();
	}

	private String fomulaInfo(String name,String id, String tip, String search,String highlight) throws Exception {
		if (!tip.contains("T")) {
			Exception e = new Exception(f.modifyingTxt(id) + "没备注");
			return e.getMessage();

		} else {
			// 如果数字是 ASCII 数字，则使用如下代码：
			String result = tip.replaceAll("[^0-9~]", "");
			String[] resultSimble=result.split("~");
			StringBuilder sb = new StringBuilder();
			String T1 = resultSimble[0];
			String T2 =resultSimble[1];
			int t1 = Integer.valueOf(T1);
			int t2 = Integer.valueOf(T2);
			int tier = t2;
			String searchinfo=search;
			String con = null;
			if (id != null) {
				temp = f.modifyingTxt(id);
			} else {
				temp = null;
			}
			
			

				if (searchinfo!=null&&name.contains(searchinfo)) {
					
					
					for (int i = t1; i <=t2; i++) {
						
							con = temp + " F -" + tier+ " " +highlight+ " " + '\r' + '\n';
							tier = tier - 1;

							sb.append(con);
							con = null;
						
					}
				}    else {
					
					for (int i = t1; i <=t2; i++) {
						
						con = temp + " F -" + tier+ " " +highlight+ " " + '\r' + '\n';
						tier = tier - 1;

						sb.append(con);
						con = null;
					
				}

				}
			
			return sb.toString();

		}
	}

}
