package com.exl.materialstanderd.read;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import com.example.demo.Simple;
import com.exl.materialstanderd.BacteriaFactory;
import com.exl.materialstanderd.Bactetria;

public class ReadSheet {

	private String temp = null;
	private FormulaId f = new FormulaId();

	public String read(ArrayList<Simple> simpleList) throws IOException, InvalidFormatException {

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
				name = simpleList.get(i + gcoli).getName();
				id = simpleList.get(i + gcoli).getNumber();
				unit = simpleList.get(i + gcoli).getUnit();

				item = simpleList.get(i + gcoli).getTestItem();

				Bactetria colibact = bf.creatBactetria(standard, unit, id, item);
				if (colibact == null) {
				} else if (unit.contains("MPN")) {
					coliNameNote = coliInfo(colibact.getInfo(), unit);

				}

			}
			colipsout = coliNameNote;

			for (int gcoli = 0; gcoli < IDsimilar; gcoli++) {

				if (i + gcoli >= simpleList.size()) {
					break;
				}
				standard = simpleList.get(i + gcoli).getTestMethod();
				name = simpleList.get(i + gcoli).getName();
				id = simpleList.get(i + gcoli).getNumber();
				unit = simpleList.get(i + gcoli).getUnit();
				item = simpleList.get(i + gcoli).getTestItem();

				if (unit.contains("cfu") || unit.contains("CFU")) {

					Bactetria bact = bf.creatBactetria(standard, unit, id, item);
					if (bact == null) {
						info = id + "木方法";
						sb.append(fomulaInfo(standard, name, info, unit, colipsout, item));

					} else {
						info = bact.getInfo();
						sb.append(fomulaInfo(standard, name, info, unit, colipsout, item));
					}

				}

			}

			if (IDsimilar != 1) {
				i = (i - 1) + IDsimilar;
			}

		}
		return sb.toString();
	}

	private String coliInfo(String info, String unit) {
		String coliInfo = null;
		if (unit.contains("MPN")) {
			coliInfo = info;
		}
		return coliInfo;

	}

	private String fomulaInfo(String standard, String name, String info, String unit, String coliInfo, String item) {
		StringBuilder sb = new StringBuilder();
		String con = null;
		if (info != null) {
			temp = info = f.modifyingTxt(info);
		} else {
			temp = null;
		}
		if (coliInfo != null) {

		}
		if (unit.compareToIgnoreCase("cfu/g") == 0) {

			if (name.contains("猫条") || name.contains("罐") || name.contains("SPF") || name.contains("spf")
					|| name.contains("盒")) {
				if (coliInfo != null) {
					con = temp + " " + "-1" + coliInfo + '\r' + '\n';
					sb.append(con);
					con = null;
				} else {
					con = temp + " " + "-1" + '\r' + '\n';
					sb.append(con);
					con = null;
				}

			} else if (name.contains("PD") || name.contains("pd")) {
				for (int i = 1; i < 4; i++) {
					if (i == 3 && coliInfo != null) {
						con = temp + " PD " + (i - 4) + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " PD " + (i - 4) + '\r' + '\n';

						sb.append(con);
						con = null;
					}
				}
			} else if (item.contains("金")) {
				if (coliInfo != null) {
					con = temp + " " + "-1" + coliInfo + '\r' + '\n';
					sb.append(con);
					con = null;
				} else {
					con = temp + " " + "-1" + '\r' + '\n';
					sb.append(con);
					con = null;
				}
			} else if (item.contains("蜡样")) {
				if (coliInfo != null) {
					con = temp + " " + "-1" + coliInfo + '\r' + '\n';
					sb.append(con);
					con = null;
				} else {
					con = temp + " " + "-1" + '\r' + '\n';
					sb.append(con);
					con = null;
				}
			} else if (item.contains("肠球菌") || item.contains("丁酸") || item.contains("酵母活细胞") || item.contains("枯草")
					|| item.contains("地衣") || item.contains("芽孢") || item.contains("乳酸菌") || item.contains("双歧")
					|| item.contains("嗜酸") || item.contains("植物") || item.contains("乳杆") || item.contains("戊糖片球")) {
				for (int i = 1; i < 9; i++) {
					if (i == 8 && coliInfo != null) {
						con = temp + " " + (i - 9) + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - 9) + '\r' + '\n';

						sb.append(con);
						con = null;
					}

				}
			} else if (standard.contains("GB/T 5750.12-2006")) {

				for (int i = 0; i <= 2; i++) {
					if (i == 2 && coliInfo != null) {
						con = temp + " " + (i - 2) + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - 2) + '\r' + '\n';

						sb.append(con);
						con = null;
					}

				}
			} else {
				for (int i = 1; i < 4; i++) {
					if (i == 3 && coliInfo != null) {
						con = temp + " " + (i - 4) + coliInfo + '\r' + '\n';
						sb.append(con);

						con = null;
					} else {
						con = temp + " " + (i - 4) + '\r' + '\n';

						sb.append(con);
						con = null;
					}
				}

			}
		}
		return sb.toString();

	}

	public String write(ArrayList<Simple> simpleList) {
		String content = null;
		try {
			content = read(simpleList);

		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return content;
	}

}
