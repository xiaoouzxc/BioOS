package com.exl.materialstanderd.read;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.example.demo.Simple;
import com.exl.materialstanderd.BacteriaFactory;
import com.exl.materialstanderd.Bactetria;
import com.xml.standards.Method;
import com.xml.standards.MethodProceed;
import com.xml.standards.Standard;
import com.xml.standards.TestItem;

public class ReadSheetXML {

	private String temp = null;
	private FormulaId f = new FormulaId();

	public String read(ArrayList<Simple> simpleList,ArrayList<Standard> Standard,int dupliction) throws IOException, InvalidFormatException {
		ArrayList<TestItem> testItemList=null;
		ArrayList<Method> methodList=null;
		ArrayList<MethodProceed> methodProceedList=null;
		for (int n = 0; n < simpleList.size(); n++) {

			if (simpleList.get(n).getUnit() == null || simpleList.get(n).getUnit().isBlank()
					|| simpleList.get(n).getUnit().compareTo("/") == 0
					|| simpleList.get(n).getUnit().compareTo("/25g") == 0
					|| simpleList.get(n).getUnit().compareTo("/g") == 0) {

				if (simpleList.get(n).getTestItem().contains("数") || simpleList.get(n).getTestItem().contains("霉菌")
						|| simpleList.get(n).getTestItem().contains("酵母")) {
					simpleList.get(n).setUnit("CFU/g");

				} else {

					simpleList.get(n).setUnit("/");

				}
			}
		}
		BacteriaFactory bf = new BacteriaFactory();
		String name = null;
		String item = null;
		String standard = null;
		String unit = null;
		String id = null;
		String info = null;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < simpleList.size(); i++) {

			String cellContent = simpleList.get(i).getNumber();
			int IDsimilar = 1;
			int index = 0;
			String nextID = null;
			String coliNameNote = null;
			String colipsout = null;

			if (i + 1 > simpleList.size()) {
				break;
			} else if (i + 1 == simpleList.size()) {
				IDsimilar = 1;
			} else {
				nextID = simpleList.get(i + 1).getNumber();
			}
			while (cellContent.equals(nextID)) {
				IDsimilar++;
				index = IDsimilar;
				if (i + index < simpleList.size()) {

					nextID = simpleList.get(i + index).getNumber();
				} else {
					nextID = null;
				}
			}

			for (int gcoli = 0; gcoli < IDsimilar; gcoli++) {
				if (i + gcoli >= simpleList.size()) {
					break;
				}
				standard = simpleList.get(i + gcoli).getTestMethod();
				item=simpleList.get(i + gcoli).getTestItem();
				unit=simpleList.get(i + gcoli).getUnit();
				
				
					
				 

				for(int StandardIndex=0;StandardIndex<Standard.size();StandardIndex++) {
					
					if(standard.contains(Standard.get(StandardIndex).getStandardNumber())) {
						testItemList=(ArrayList<TestItem>) Standard.get(StandardIndex).getTestItem();
						for(TestItem testitem:testItemList) {
							if (item.contains(testitem.getTestItem())) {	
								methodList=(ArrayList<Method>) testitem.getMethod();
								for(Method method: methodList) {
									if(method.getMethod().equals("/")) {
									methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
									for(MethodProceed proceed:methodProceedList) {
										if(proceed.getUnit().equalsIgnoreCase("mpn")) {
											
											if(Standard.get(StandardIndex).getStandardNumber().contains(method.getMethod())) {		
												//添加筛选条件，防止5变重复显示管子
												if(coliNameNote!=null&&!coliNameNote.contains(proceed.getHighlight())) {
													coliNameNote=coliNameNote+" "+proceed.getHighlight();
												}else {
													coliNameNote=proceed.getHighlight();
												}
												
												break;
											}else {
												if(coliNameNote!=null&&!coliNameNote.contains(proceed.getHighlight())) {
													coliNameNote=coliNameNote+" "+proceed.getHighlight();
												}else {
													coliNameNote=proceed.getHighlight();
												}
												
												
												break;
											}
											
										}
										
										//System.out.println(id+"---"+(i + gcoli));
											
										//isBreakTriggered=true;
										
									}
									}else if(standard.contains(method.getMethod())) {
										methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
										for(MethodProceed proceed:methodProceedList) {
											if(proceed.getUnit().equalsIgnoreCase("mpn")) {
												
												if(Standard.get(StandardIndex).getStandardNumber().contains(method.getMethod())) {		
													//添加筛选条件，防止5变重复显示管子
													if(coliNameNote!=null&&!coliNameNote.contains(proceed.getHighlight())) {
														coliNameNote=coliNameNote+" "+proceed.getHighlight();
													}else {
														coliNameNote=proceed.getHighlight();
													}
													
													break;
												}else {
													if(coliNameNote!=null&&!coliNameNote.contains(proceed.getHighlight())) {
														coliNameNote=coliNameNote+" "+proceed.getHighlight();
													}else {
														coliNameNote=proceed.getHighlight();
													}
													
													
													break;
												}
												
											}
											
											//System.out.println(id+"---"+(i + gcoli));
												
											//isBreakTriggered=true;
											
										}
									}
//									if(!method.getMethod().contains("/")) {
//										if(Standard.get(StandardIndex).getStandardNumber().contains(method.getMethod())){
//											methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
//											for(MethodProceed proceed:methodProceedList) {
//												coliNameNote=proceed.getHighlight();
//												System.out.println(standard+"---"+coliNameNote);
//												break;
//											}
//										}
//									}
									
								}
								
								
								}
						}
						
					}
				}
				if(unit.toLowerCase().contains("mpn")&& coliNameNote==null) {
					coliNameNote="mpn未定义";
					
				}
				

			}
			
			colipsout = coliNameNote;
			String colipsoutReplace=null;
			if(colipsout!=null) {
				
				colipsoutReplace=colipsout.replace("null", "");
				
			}
			
			int countForSuffix = 0;
			for (int gcoli = 0; gcoli <IDsimilar; gcoli++) {
				if (i + gcoli >= simpleList.size()) {
					break;
				}
				standard = simpleList.get(i + gcoli).getTestMethod();
				name = simpleList.get(i + gcoli).getName();
				item=simpleList.get(i + gcoli).getTestItem();
				id = getLabelNumber(simpleList.get(i + gcoli));
				unit=simpleList.get(i + gcoli).getUnit();
				
				boolean isBreakTriggered = false; // 标志变量
				 if(simpleList.get(i + gcoli).getTestItem().contains("*5")) {
					 if(dupliction!=0) {
						 int suffix = dupliction - (countForSuffix % dupliction);
					        id = id + "#" + suffix;
					        countForSuffix++;
					        if(countForSuffix>dupliction) {
					        	continue;
					        }
					 }else {
						 int suffix = 5 - (countForSuffix % 5);
						 
					        id = id + "#" + suffix;
					        countForSuffix++;
					 }
				        
				    }
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
											
//											if(proceed.getSearch().equals("/")) {
//												initialIndex=loop;
//											}
//											loop++;
											if(name.contains(proceed.getSearch())) {
												//2024.12.26添加修改，因缺少CFU判定导致fomulaInfo方法传递了非int值
												if(proceed.getUnit().equalsIgnoreCase("cfu")) {
													sb.append(fomulaInfo( name, id, colipsoutReplace,proceed.getDilution(),proceed.getSearch(),proceed.getHighlight()));
													isBreakTriggered=true;
													isFound=true;
													break;
												}
												
											}
											
										}
										if (!isFound) {
											 MethodProceed defaultProceed = methodProceedList.get(initialIndex);
											if(defaultProceed.getUnit().equalsIgnoreCase("cfu")) {
												sb.append(fomulaInfo( name, id, colipsoutReplace,defaultProceed.getDilution(),defaultProceed.getSearch(),defaultProceed.getHighlight()));
												
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
													if(proceed.getUnit().equalsIgnoreCase("cfu")) {
														sb.append(fomulaInfo( name, id, colipsoutReplace,proceed.getDilution(),proceed.getSearch(),proceed.getHighlight()));
														isBreakTriggered=true;
														isFound=true;
														break;
													}
													
												}
												
											}
											if (!isFound) {
												 MethodProceed defaultProceed = methodProceedList.get(initialIndex);
												if(defaultProceed.getUnit().equalsIgnoreCase("cfu")) {
													sb.append(fomulaInfo( name, id, colipsoutReplace,defaultProceed.getDilution(),defaultProceed.getSearch(),defaultProceed.getHighlight()));
													isBreakTriggered=true;					
													break;
												}
											}
										
									}
										
//											methodProceedList=(ArrayList<MethodProceed>) method.getMethodProceed();
//											for(MethodProceed proceed:methodProceedList) {
//												if(proceed.getUnit().equalsIgnoreCase("cfu")) {
//													
//													if(Standard.get(StandardIndex).getStandardNumber().contains(method.getMethod())) {
//														sb.append(fomulaInfo( name, id, colipsoutReplace,proceed.getDilution(),proceed.getSearch(),proceed.getHighlight()));
//														isBreakTriggered=true;
//														break;
//													}else {
//														sb.append(fomulaInfo( name, id, colipsoutReplace,proceed.getDilution(),proceed.getSearch(),proceed.getHighlight()));
//														isBreakTriggered=true;
//														break;
//													}
//													
//												}
//												
//												//System.out.println(id+"---"+(i + gcoli));
//													
//												//
//												
//											}
										
									
									
								}
				
							
				}
					}
				}
				
//				 if(isBreakTriggered) {
//					
//					sb.append(fomulaInfo(name, id, colipsout,"1","/","/"));
//				}


			}

				if(unit.toLowerCase().contains("cfu")&&!isBreakTriggered) {
					sb.append(fomulaInfo(name, id, colipsout,"1","/","CFU未定义"));
					
				}

		}
			if (IDsimilar != 1) {
				i = (i - 1) + IDsimilar;
			}
		}
		return sb.toString();
	}



	private String fomulaInfo( String name, String info, String coliInfo,String dilution,String search,String highlight) {
		StringBuilder sb = new StringBuilder();
		int[] dn = {1, 2, 3, 4, 5};
		ArrayList<Integer> dN = Arrays.stream(dn).boxed()
		                              .collect(Collectors.toCollection(ArrayList::new));
		
		//System.out.println(info+dilution);
		int dilutiontransfer=Integer.parseInt(dilution);
		int d=Integer.parseInt(dilution)+1;
		String searchinfo=search;
		String con = null;
		if (info != null) {
			temp = info = f.modifyingTxt(info);
		} else {
			temp = null;
		}
		if (coliInfo != null) {

		}
		//添加d=1的情况；

			if (searchinfo!=null&&name.contains(searchinfo)) {
				
				
				for (int i = 1; i <d; i++) {
					if (i == dilutiontransfer && coliInfo != null) {
						con = temp + " " + (i - d)+ " " +highlight+ " " + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - d)+ " " +highlight+ " " + '\r' + '\n';

						sb.append(con);
						con = null;
					}
				}
			}    else {
				
				for (int i = 1; i <d; i++) {
					if (i == dilutiontransfer && coliInfo != null) {
						con = temp + " " + (i - d)+ " " +highlight + " " + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - d)+ " " +highlight + '\r' + '\n';

						sb.append(con);
						con = null;
					}
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

	public String write(ArrayList<Simple> simpleList,ArrayList<Standard> Standard,int dup) {
		String content = null;
		//try {
			//content = read(simpleList,Standard);

		//} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}

		return content;
	}

}
