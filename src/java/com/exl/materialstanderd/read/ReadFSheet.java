package com.exl.materialstanderd.read;

import java.util.ArrayList;

import com.example.demo.Simple;

public class ReadFSheet {
	private String temp = null;
	private FormulaId f = new FormulaId();

	public String read(ArrayList<Simple> simpleList) {
		String id = null;
		String item = null;
		String tip = null;
		String name = null;
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < simpleList.size(); i++) {
			id = simpleList.get(i).getNumber();
			item = simpleList.get(i).getTestItem();
			tip = simpleList.get(i).getTip();
			name = simpleList.get(i).getName();
			try {
				if (!item.contains("沙") && !item.contains("志贺") && !item.contains("致泻") && !item.contains("O157")
						&& !item.contains("商业无菌") && !item.contains("副溶血") && !item.contains("肠")) {
					sb.append(fomulaInfo(id, item, tip, name));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}

	private String fomulaInfo(String id, String item, String tip, String name) throws Exception {
		if (!tip.contains("T")) {
			Exception e = new Exception(f.modifyingTxt(id) + "没备注");
			return e.getMessage();

		} else {
			StringBuilder sb = new StringBuilder();
			String con = null;
			if (id != null) {
				temp = f.modifyingTxt(id);
			} else {
				temp = null;
			}

			if (name.contains("猫条") || name.contains("罐") || name.contains("SPF") || name.contains("spf")
					|| name.contains("盒")) {

				con = temp + " F " + "-1" + '\r' + '\n';
				sb.append(con);
				con = null;

			} else if (item.contains("金")) {

				con = temp + " F " + "-1" + '\r' + '\n';
				sb.append(con);
				con = null;

			} else if (item.contains("肠球菌") || item.contains("丁酸") || item.contains("酵母活细胞") || item.contains("枯草")
					|| item.contains("地衣") || item.contains("芽孢") || item.contains("乳酸菌") || item.contains("双歧")
					|| item.contains("嗜酸") || item.contains("植物") || item.contains("乳杆") || item.contains("戊糖片球")) {
				char T1 = tip.charAt(2);
				char T2 = tip.charAt(5);
				int t1 = Character.getNumericValue(T1);
				int t2 = Character.getNumericValue(T2);
				int tier = t2;
				for (int i = t1; i <= t2; i++) {
					con = temp + " F -" + tier + '\r' + '\n';
					tier = tier - 1;
					sb.append(con);
					con = null;

				}
			} else {
				char T1 = tip.charAt(2);
				char T2 = tip.charAt(5);
				int t1 = Character.getNumericValue(T1);
				int t2 = Character.getNumericValue(T2);
				int tier = t2;
				for (int i = t1; i <= t2; i++) {

					con = temp + " F -" + tier + '\r' + '\n';
					tier = tier - 1;
					sb.append(con);
					con = null;

				}

			}
			return sb.toString();

		}
	}

}
