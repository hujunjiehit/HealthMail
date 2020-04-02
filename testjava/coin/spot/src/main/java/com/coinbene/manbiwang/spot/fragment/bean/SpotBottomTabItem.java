package com.coinbene.manbiwang.spot.fragment.bean;

/**
 * Created by june
 * on 2019-11-20
 */
public class SpotBottomTabItem {

	private SpotBottomTab type;
	private String title;
	private AccountType accountType;

	public SpotBottomTabItem(SpotBottomTab type, String title, AccountType accountType) {
		this.type = type;
		this.title = title;
		this.accountType = accountType;
	}


	public SpotBottomTab getType() {
		return type;
	}

	public void setType(SpotBottomTab type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}
}
