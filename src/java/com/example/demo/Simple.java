package com.example.demo;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonKey;
import com.google.gson.Gson;

public class Simple {

	private int id;
	private String number;
	private String name;
	private String company;
	private String testItem;
	private String testMethod;
	private String unit;
	private String tip;
	private int done;
	private String ifF;
	private String result;
	private String location;
	private Date date;
	private String selomenResult;
	private Integer dailySampleOrder;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	
	public Simple() {}

	public Simple(int id, String number, String name, String company, String testItem, String testMethod, String unit,
			String tip, int done, String ifF) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.company = company;
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.unit = unit;
		this.tip = tip;
		this.done = done;
		this.ifF = ifF;
	}

	public Simple(String number, String name, String testItem, String company, String ifF, String result,int done) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;
		this.company = company;
		this.ifF = ifF;
		this.result = result;
		this.done = done;
	}

	public Simple(int id,String number, String name,String company, String testItem,String unit, String testMethod,int done, String ifF,
			String result) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.company = company;
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.unit = unit;
		this.done = done;
		this.ifF = ifF;
		this.result = result;
	}

	public Simple(String number, String name, String testItem, String testMethod, String result) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.result = result;

	}

	public Simple(String number, String name, String testItem, String testMethod, String result, int id) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.result = result;
		this.id = id;

	}

	public Simple(String number, String name, String testItem, String testMethod, int done, String unit) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;
		this.testMethod = testMethod;
		this.unit = unit;
		this.done = done;

	}

	public Simple(int id, String number, String name, String testItem, String ifF, String location) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;
		this.location = location;
		this.ifF = ifF;
	}

	public Simple(int id, String number, String name, String company, String ifF, String selomenResult, Date date) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.company = company;
		this.ifF = ifF;
		this.selomenResult = selomenResult;
		this.date = date;
	}

	public Simple(int id, String number, String name, String testItem) {
		this.number = number;
		this.name = name;
		this.testItem = testItem;

	}

	public Simple(int id, String number, String name, String company, String testItem, String ifF, String result) {
		this.id = id;
		this.number = number;
		this.name = name;
		this.company = company;
		this.testItem = testItem;
		this.ifF = ifF;
		this.result = result;
	}
	public Simple(int id, String number, String ifF) {
		this.id = id;
		this.number = number;
		this.ifF = ifF;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
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

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public int getDone() {
		return done;
	}

	public void setDone(int done) {
		this.done = done;
	}

	public String getIfF() {
		return ifF;
	}

	public void setIfF(String ifF) {
		this.ifF = ifF;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@JsonIgnore
	public String getDate() {

		return sdf.format(date);
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSelomenResult() {
		return selomenResult;
	}

	public void setSelomenResult(String selomenResult) {
		this.selomenResult = selomenResult;
	}

	public Integer getDailySampleOrder() {
		return dailySampleOrder;
	}

	public void setDailySampleOrder(Integer dailySampleOrder) {
		this.dailySampleOrder = dailySampleOrder;
	}

}
