package com.exl.materialstanderd;

public class BacillusSubitilus extends Bactetria {

	private String id = null;
	private String item = null;

	public BacillusSubitilus(String id, String item) {
		this.id = id;
		this.item = item;

	}

	@Override
	public String getInfo() {
		if (item.contains("枯草")) {
			return id + " 枯";
		} else if (item.contains("地衣")) {
			return id + " 地";
		} else {
			return id + " 芽";
		}

	}

}
