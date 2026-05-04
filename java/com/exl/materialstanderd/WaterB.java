package com.exl.materialstanderd;

public class WaterB extends Bactetria {

	private String id = null;
	private String unit = null;

	public WaterB(String id, String unit) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.unit = unit;
	}

	@Override
	public String getInfo() {
		if (unit.contains("MPN")) {
			return "蛋";
		} else {
			return id + " 水";
		}
	}

}
