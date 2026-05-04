package com.exl.materialstanderd;

public class MoldsCount extends Bactetria {
	private String id = null;
	private String standard = null;

	public MoldsCount(String id, String standard) {
		this.id = id;
		this.standard = standard;

	}

	@Override
	public String getInfo() {
		if (standard.contains("T/SDPIA 05-2022") || standard.contains("团")||standard.contains("GB/T 43839-2024")) {
			return id + " M";
		} else {
			return id + " SM";
		}

	}

}
