package com.xml.standards;

import java.util.ArrayList;
import java.util.List;

public class Standard {
    
	private String standardNumber;
	//private TestItem testItem=new TestItem() ;
	private List<TestItem> testItem=new ArrayList<TestItem>() ;
//    private String method;
//    private String unit;
//	private String dilution;
//	private String highlight;
//	private String quantity;
//	private String addition;
//	private String search;
//	private String medium;

    // Getters and Setters

    public String getStandardNumber() {
        return standardNumber;
    }

    public void setStandardNumber(String standardNumber) {
        this.standardNumber = standardNumber;
    }

	

	

	public List<TestItem> getTestItem() {
		return testItem;
	}

	public void setTestItems(List<TestItem> testItem) {
		this.testItem = testItem;
	}
 
}

