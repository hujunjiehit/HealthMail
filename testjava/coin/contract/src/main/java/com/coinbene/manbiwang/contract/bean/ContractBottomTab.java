package com.coinbene.manbiwang.contract.bean;

public class ContractBottomTab {

	private String tabName;
	private ContractTabType tabType;


	public ContractBottomTab(String tabName, ContractTabType tabType) {
		this.tabName = tabName;
		this.tabType = tabType;
	}

	public String getTabName() {
		return tabName;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public ContractTabType getTabType() {
		return tabType;
	}

	public void setTabType(ContractTabType tabType) {
		this.tabType = tabType;
	}
}
