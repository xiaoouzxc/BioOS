package com.exl.materialstanderd.read;

public class FormulaId {

	public String modifyingTxt(String txt) {
		StringBuilder sb = new StringBuilder(txt);
		if (txt.contains("-")) {
			return sb.toString();
		} else {
			sb.delete(0, 3);
			sb.replace(5, 6, "-");
			if (sb.codePointAt(0) == 0) {
				sb.delete(0, 0);
			}

			return sb.toString();
		}

	}

}
