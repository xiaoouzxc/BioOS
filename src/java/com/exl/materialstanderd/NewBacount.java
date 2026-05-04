package com.exl.materialstanderd;

public class NewBacount extends Bactetria {
	private String id = null;

	public NewBacount(String id) {
		super();
		this.id = id;

		// TODO Auto-generated constructor stub
	}

	@Override
	public String getInfo() {
		return id + " P";

	}
}
