package com.exl.materialstanderd;

public class Coliforms extends Bactetria {

	private String unit = null;
	private String id = null;

	public Coliforms(String id, String unit) {
		this.id = id;
		this.unit = unit;
	}

	@Override
	public String getInfo() {
		if (unit.contains("CFU")) {
			return id + " V";
		} else {
			return "D";
		}
	}

}
