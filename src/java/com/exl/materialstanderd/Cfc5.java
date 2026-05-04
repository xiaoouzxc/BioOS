package com.exl.materialstanderd;

public class Cfc5 extends Bactetria {

	private String item = null;

	public Cfc5(String item) {

		this.item = item;

	}

	@Override
	public String getInfo() {
		if (item.contains("大肠菌群")) {
			return "BG";
		} else {
			return "EC";
		}

	}

}
