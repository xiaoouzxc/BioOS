package com.exl.materialstanderd.read;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.example.demo.Simple;
import com.exl.materialstanderd.BacteriaFactory;
import com.exl.materialstanderd.Bactetria;
import com.xml.standards.Standard;

public class ReadSheetXML {

	private String temp = null;
	private FormulaId f = new FormulaId();

	public String read(ArrayList<Simple> simpleList,ArrayList<Standard> Standard) throws IOException, InvalidFormatException {

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
				

				for(int StandardIndex=0;StandardIndex<Standard.size();StandardIndex++) {
					if(standard.contains(Standard.get(StandardIndex).getStandardNumber())) {
						if (Standard.get(StandardIndex).getUnit().contains("mpn")) {
						coliNameNote =Standard.get(StandardIndex).getHighlight() ;
						System.out.println(standard);
						}
					}
				}
				

			}
			colipsout = coliNameNote;

			for (int gcoli = 0; gcoli < IDsimilar; gcoli++) {
				standard = simpleList.get(i + gcoli).getTestMethod();
				name = simpleList.get(i + gcoli).getName();
				id = simpleList.get(i + gcoli).getNumber();
				if (i + gcoli >= simpleList.size()) {
					break;
				}
				
				boolean isBreakTriggered = false; // 标志变量
				
				for(int StandardIndex=0;StandardIndex<Standard.size();StandardIndex++) {
					if(standard.contains(Standard.get(StandardIndex).getStandardNumber())) {
						unit =Standard.get(StandardIndex).getUnit() ;
						if (unit.contains("cfu")) {
				
							sb.append(fomulaInfo( name, id, colipsout,Standard.get(StandardIndex).getDilution(),Standard.get(StandardIndex).getSearch(),Standard.get(StandardIndex).getHighlight()));	
							isBreakTriggered=true;
							break;
				}
					}
				}
				
				 if(isBreakTriggered) {
					
					sb.append(fomulaInfo(name, id, colipsout,"1","/","/"));
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
		

			if (searchinfo!=null&&name.contains(searchinfo)) {
				for (int i = 1; i <d; i++) {
					if (i == 3 && coliInfo != null) {
						con = temp + " " + (i - d)+highlight+ coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - d)+highlight+ '\r' + '\n';

						sb.append(con);
						con = null;
					}
				}
			}    else {
				
				for (int i = 1; i <d; i++) {
					if (i == 3 && coliInfo != null) {
						con = temp + " " + (i - d)+highlight + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - d)+highlight + '\r' + '\n';

						sb.append(con);
						con = null;
					}
				}

			}
		
		return sb.toString();

	}

	public String write(ArrayList<Simple> simpleList,ArrayList<Standard> Standard) {
		String content = null;
		try {
			content = read(simpleList,Standard);

		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

}
