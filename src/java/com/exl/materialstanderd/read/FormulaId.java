package com.exl.materialstanderd.read;

public class FormulaId {

	public String modifyingTxt(String txt) {
	    StringBuilder sb = new StringBuilder();
	    String prefix = "";
	    if (txt != null && txt.matches("^\\[\\d+\\].*")) {
	    	int endIndex = txt.indexOf("]") + 1;
	    	prefix = txt.substring(0, endIndex);
	    	txt = txt.substring(endIndex);
	    }

	    if (txt.contains("-")) {
	    	
	    	return prefix + txt;// 如果包含 "-", 直接返回txt字符串 修改日期2025.4.9
	        //return sb.toString(); // 如果包含 "-", 直接返回空字符串
	    } else {
	        // 只移除前缀字母，保留数字和 `#`
	        String result = txt.replaceAll("^[A-Za-z]+", ""); 
	        sb.append(result);

	        // 确保字符串长度足够再插入 `-`
	        if (sb.length() > 5) {  
	        	sb.replace(5, 6, "-");
	        }

	        // 如果首字符是 '0'，则删除
	        if (sb.length() > 0 && sb.charAt(0) == '0') { 
	            sb.deleteCharAt(0);
	        }

	        return prefix + sb.toString();
	    }
	}

}
