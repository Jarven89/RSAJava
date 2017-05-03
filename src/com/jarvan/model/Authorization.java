package com.jarvan.model;

public class Authorization {
	private int appl = 1; //app限制
	private long ucl = 1000;//用户限制
	private String cid = "";//用户 ID
	private String vs = "V1.4";//版本号
	private long vli = 0;//过期时间
	private boolean ic = false;//是否验证

	public int getAppl() {
		return appl;
	}

	public void setAppl(int appl) {
		this.appl = appl;
	}

	public long getUcl() {
		return ucl;
	}

	public void setUcl(long ucl) {
		this.ucl = ucl;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getVs() {
		return vs;
	}

	public void setVs(String vs) {
		this.vs = vs;
	}

	public long getVli() {
		return vli;
	}

	public void setVli(long vli) {
		this.vli = vli;
	}

	public boolean isIc() {
		return ic;
	}

	public void setIc(boolean ic) {
		this.ic = ic;
	}
}
