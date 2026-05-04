package com.exl.materialstanderd;

public class Clostridiumperfringens extends Bactetria {

	private String id = null;

	public Clostridiumperfringens(String id) {
		this.id = id;
	}

	@Override
	public String getInfo() {

		return id + " 产荚";
	}

}
