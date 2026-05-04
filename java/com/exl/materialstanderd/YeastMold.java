package com.exl.materialstanderd;

public class YeastMold extends Bactetria {
	private String id = null;
	private String item = null;

	public YeastMold(String id, String item) {
		this.id = id;
		this.item = item;
	}

	@Override
	public String getInfo() {
		if (item.contains("酵母活细胞")) {
			return id + " J";
		} else {
			return id + " MJ";
		}
	}

}
