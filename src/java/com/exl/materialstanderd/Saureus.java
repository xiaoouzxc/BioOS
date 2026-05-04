package com.exl.materialstanderd;

public class Saureus extends Bactetria {

	private String id = null;
	private String item = null;

	public Saureus(String id, String item) {
		this.id = id;
		this.item = item;
	}

	@Override
	public String getInfo() {

		return id + " 金";
	}

}
