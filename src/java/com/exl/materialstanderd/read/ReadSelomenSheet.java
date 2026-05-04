package com.exl.materialstanderd.read;

import java.util.ArrayList;

import com.example.demo.Simple;

public class ReadSelomenSheet {
	
	private FormulaId f = new FormulaId();
	public String read(ArrayList<Simple> simpleList) {
		String id = null;
		String num = null;
		String F=null;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < simpleList.size(); i++) {
			id = getLabelNumber(simpleList.get(i));
			num = simpleList.get(i).getTestItem();
			F=simpleList.get(i).getIfF();
			try {
				if(i%10==9) {
					sb.append(fomulaInfo(id, num, F)+'\r' + '\n');
				}else {
					if(i==simpleList.size()-1) {
						sb.append(fomulaInfo(id, num, F)+'\r' + '\n');
					}else {
						sb.append(fomulaInfo(id, num, F));
					}					
					//System.out.println(sb.toString());
				}
					
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < simpleList.size(); i++) {
			id = getLabelNumber(simpleList.get(i));
			num = simpleList.get(i).getTestItem();
			F=simpleList.get(i).getIfF();
			try {
				
					sb.append(fomulaPanelRInfo(id, num, F)+'\r' + '\n');
				
					
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < simpleList.size(); i++) {
			id = getLabelNumber(simpleList.get(i));
			num = simpleList.get(i).getTestItem();
			F=simpleList.get(i).getIfF();
			try {
				
					sb.append(fomulaPanelSInfo(id, num, F)+'\r' + '\n');
				
					
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	private String getLabelNumber(Simple simple) {
		return simple.getNumber();
	}
	private String fomulaInfo(String id, String num, String F) throws Exception {
		
			String sb = null;
			if (F == null) {
				F = "/";
			}
			
			if (id != null) {
				
				if(!F.contains("/")) {
					sb = f.modifyingTxt(id)+" F"+" ";
				}else {
					sb = f.modifyingTxt(id)+"   ";
				}
			} else {
				sb = "短号为空或已结束";
				
			
			}
			

			
			return sb;

		
	}
	private String fomulaPanelRInfo(String id, String num, String F) throws Exception {
		
		String sb = null;
		if (F == null) {
			F = "/";
		}
		
		if (id != null) {
			
			if(!F.contains("/")) {
				sb = f.modifyingTxt(id)+" RF";
			}else {
				sb = f.modifyingTxt(id)+" R";
			}
		} else {
			sb = "短号为空或已结束";
			
		
		}
		

		
		return sb;

	
}
private String fomulaPanelSInfo(String id, String num, String F) throws Exception {
		
		String sb = null;
		if (F == null) {
			F = "/";
		}
		
		if (id != null) {
			
			if(!F.contains("/")) {
				sb = f.modifyingTxt(id)+" SF";
			}else {
				sb = f.modifyingTxt(id)+" S";
			}
		} else {
			sb = "短号为空或已结束";
			
		
		}
		

		
		return sb;

	
}

}
