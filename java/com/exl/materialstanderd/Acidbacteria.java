package com.exl.materialstanderd;

public class Acidbacteria extends Bactetria {

	private String item = null;
	private String id = null;

	public Acidbacteria(String id, String item) {
		this.item = item;
		this.id = id;
	}

	@Override
	public String getInfo() {
		if (item.contains("双歧")) {
			return id + " MR+";
		} else if (item.contains("植物")) {
			return id + " 植";
		} else if (item.contains("戊糖片球")) {
			return id + " 戊";
		} else if (item.contains("嗜酸")) {
			return id + " MC";
		} else {
			return id + " MR";
		}

	}

}
