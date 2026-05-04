package com.exl.materialstanderd;

public class Enterobacteriaceae extends Bactetria {

	private String id = null;
	private String unit = null;

	public Enterobacteriaceae(String id, String unit) {
		this.unit = unit;
		this.id = id;
	}

	@Override
	public String getInfo() {
		if (unit.contains("CFU")) {
			return id + " VG";
		} else {
			return "BPW";
		}

	}

}
