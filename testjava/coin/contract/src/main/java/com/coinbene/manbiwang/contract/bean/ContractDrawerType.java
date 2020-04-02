package com.coinbene.manbiwang.contract.bean;

public class ContractDrawerType {

	private  int type;
	private String contactName;

	public ContractDrawerType(int type, String contactName) {
		this.type = type;
		this.contactName = contactName;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
