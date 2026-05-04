package com.exl.materialstanderd;

public class Enterococcus extends Bactetria {

	private String id = null;

	public Enterococcus(String id, String item) {
		this.id = id;

	}

	@Override
	public String getInfo() {
		// TODO Auto-generated method stub
		return id + " 肠";
	}

}
