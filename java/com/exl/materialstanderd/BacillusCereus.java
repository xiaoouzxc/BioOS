package com.exl.materialstanderd;

public class BacillusCereus extends Bactetria {
	private String id = null;
	private String item = null;

	public BacillusCereus(String id, String item) {
		this.id = id;
		this.item = item;

	}

	@Override
	public String getInfo() {

		return id + " MYP";
	}

}
