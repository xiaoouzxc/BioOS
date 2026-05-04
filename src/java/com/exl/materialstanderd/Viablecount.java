package com.exl.materialstanderd;

public class Viablecount extends Bactetria {

	private String id = null;

	public Viablecount(String id) {
		this.id = id;
	}

	@Override
	public String getInfo() {

		return id + " PCA";
	}

}
