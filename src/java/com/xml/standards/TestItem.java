package com.xml.standards;

import java.util.ArrayList;
import java.util.List;

public class TestItem {
	private String testItem ;
	//private Method method=new Method();
	private List<Method> method=new ArrayList<Method>();
	public String getTestItem() {
		return testItem;
	}
	public void setTestItem(String testItem) {
		this.testItem = testItem;
	}
	
	public List<Method> getMethod() {
		return method;
	}
	public void setMethods(List<Method> asList) {
		this.method=asList;
		
	}
	
}
