package com.exl.materialstanderd;

public class Escherichiacoli extends Bactetria {

	private String id = null;
	private String unit = null;

	public Escherichiacoli(String id, String unit) {
		this.id = id;
		this.unit = unit;
	}

	@Override
	public String getInfo() {
		if (unit.contains("MPN")) {
			return "LST";
		} else {
			return id + " VM";
		}

	}

}
