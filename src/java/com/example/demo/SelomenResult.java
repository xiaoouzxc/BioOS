package com.example.demo;

public class SelomenResult {
	private String dhl;
	private String xld;
	private String tsi;
	private String gbun;
	private String lys;
	private String kcn;
	private String cas;
	private String hbun;
	private String man;
	private String sor;
	private String onpg;

	public SelomenResult(String dhl, String xld, String tsi, String gbun, String lys, String kcn, String cas,
			String hbun, String man, String sor, String onpg) {
		super();
		this.dhl = dhl;
		this.xld = xld;
		this.tsi = tsi;
		this.gbun = gbun;
		this.lys = lys;
		this.kcn = kcn;
		this.cas = cas;
		this.hbun = hbun;
		this.man = man;
		this.sor = sor;
		this.onpg = onpg;
	}

	public SelomenResult(String lys, String kcn, String cas, String hbun, String man, String sor, String onpg) {
		this.lys = lys;
		this.kcn = kcn;
		this.cas = cas;
		this.hbun = hbun;
		this.man = man;
		this.sor = sor;
		this.onpg = onpg;
	}

	public String getDhl() {
		return dhl;
	}

	public void setDhl(String dhl) {
		this.dhl = dhl;
	}

	public String getXld() {
		return xld;
	}

	public void setXld(String xld) {
		this.xld = xld;
	}

	public String getTsi() {
		return tsi;
	}

	public void setTsi(String tsi) {
		this.tsi = tsi;
	}

	public String getGbun() {
		return gbun;
	}

	public void setGbun(String gbun) {
		this.gbun = gbun;
	}

	public String getLys() {
		return lys;
	}

	public void setLys(String lys) {
		this.lys = lys;
	}

	public String getKcn() {
		return kcn;
	}

	public void setKcn(String kcn) {
		this.kcn = kcn;
	}

	public String getCas() {
		return cas;
	}

	public void setCas(String cas) {
		this.cas = cas;
	}

	public String getHbun() {
		return hbun;
	}

	public void setHbun(String hbun) {
		this.hbun = hbun;
	}

	public String getMan() {
		return man;
	}

	public void setMan(String man) {
		this.man = man;
	}

	public String getSor() {
		return sor;
	}

	public void setSor(String sor) {
		this.sor = sor;
	}

	public String getOnpg() {
		return onpg;
	}

	public void setOnpg(String onpg) {
		this.onpg = onpg;
	}

}
