package com.example.demo;

public class TrListSimple {
	private String number;
	private String name;
	private String company;
	private String tip;

	public TrListSimple(String number, String name, String company, String tip) {
		this.number = number;
		this.name = name;
		this.company = company;
		this.tip = tip;

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

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

}
