package com.exl.materialstanderd;

public class BacteriaFactory {

	public Bactetria creatBactetria(String standard, String unit, String id, String item) {
		if (standard.contains("GB/T 13093-2006") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 13093-2006") && unit.contains("cfu")
				|| standard.contains("GB/T 13093-2006") && unit.contains("CFU")) {
			return new bacount(id);
		} else if (standard.contains("GB/T 13093-2023") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 13093-2023") && unit.contains("cfu")
				|| standard.contains("GB/T 13093-2023") && unit.contains("CFU")) {
			return new NewBacount(id);
		} else if (standard.contains("GB/T 18869-2019") && unit.contains("MPN")
				|| standard.contains("GB/T 4789.3-2003") && unit.contains("MPN")) {
			return new coliCount(id);
		} else if (standard.contains("GB 4789.10-2016") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB 4789.10-2016") && unit.contains("cfu")
				|| standard.contains("GB 4789.10-2016") && unit.contains("CFU")) {
			return new Saureus(id, item);
		} else if (item.contains("蜡样") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 26427-2010") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 26427-2010") && unit.contains("cfu")
				|| standard.contains("GB/T 26427-2010") && unit.contains("CFU")) {
			return new BacillusCereus(id, item);
		} else if (standard.contains("GB 4789.41-2016") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 40850-2021") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("SN/T 0738-1997") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Enterobacteriaceae(id, unit);
		} else if (standard.contains("GB 4789.41-2016") && unit.contains("MPN")
				|| standard.contains("GB/T 40850-2021") && unit.contains("MPN")
				|| standard.contains("21528") && unit.contains("MPN")) {
			return new Enterobacteriaceae(id, unit);
		} else if (standard.contains("GB/T 13092-2006")
				|| standard.contains("T/SDPIA 05-2022") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("团") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new MoldsCount(id, standard);
		} else if (item.contains("肠球菌") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Enterococcus(id, item);
		} else if (standard.contains("4789.15-2016") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new YeastMold(id, item);
		} else if (item.contains("酵母活细胞") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new YeastMold(id, item);
		} else if (standard.contains("GB/T 26425-2010") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB 4789.13-2012") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Clostridiumperfringens(id);
		} else if (standard.contains("GB/T 5750.12-2006") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB/T 5750.12-2006") && unit.contains("MPN")) {
			return new WaterB(id, unit);
		} else if (standard.contains("GB 4789.3-2016") && unit.compareToIgnoreCase("cfu/g") == 0
				|| standard.contains("GB 4789.3-2016") && unit.contains("MPN")) {
			return new Coliforms(id, unit);
		} else if (standard.contains("GB 4789.38-2012") && unit.contains("MPN")
				|| standard.contains("GB 4789.38-2012") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Escherichiacoli(id, unit);
		} else if (standard.contains("GB 4789.2-2022") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Viablecount(id);
		} else if (item.contains("枯草") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("芽孢") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("地衣") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new BacillusSubitilus(id, item);
		} else if (standard.contains("7251:2005") && unit.contains("MPN")) {
			return new elicoISO();
		} else if (standard.contains("NY/T 555-2002") && unit.contains("MPN")) {
			return new Cfc5(item);
		} else if (item.contains("乳酸菌") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("双歧") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("乳杆") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("戊糖片球") && unit.compareToIgnoreCase("cfu/g") == 0
				|| item.contains("嗜酸") && unit.compareToIgnoreCase("cfu/g") == 0) {
			return new Acidbacteria(id, item);
		}
		return null;
	}

}
