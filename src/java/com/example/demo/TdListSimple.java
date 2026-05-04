package com.example.demo;

public class TdListSimple {

	private String testItem;
	private String testMethod;
	private String unit;

	public TdListSimple(String testItem, String testMethod, String unit) {
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.unit = unit;

	}

	public String getTestItem() {
		return testItem;
	}

	public void setTestItem(String testItem) {
		this.testItem = testItem;
	}

	public String getTestMethod() {
		return testMethod;
	}

	public void setTestMethod(String testMethod) {
		this.testMethod = testMethod;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
