package com.exl.materialstanderd;

public abstract class Bactetria {

	public Bactetria(String textItem, String standard, String unit) {
		super();
		getInfo();
	}

	public Bactetria() {
		// TODO Auto-generated constructor stub
	}

	private String id = null;
	private String name = null;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
