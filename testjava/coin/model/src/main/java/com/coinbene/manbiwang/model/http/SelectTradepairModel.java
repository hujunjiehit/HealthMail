package com.coinbene.manbiwang.model.http;

/**
 * Created by june
 * on 2019-09-15
 */
public class SelectTradepairModel {

	private String tradePairName;
	private boolean isChecked;

	public String getTradePairName() {
		return tradePairName;
	}

	public void setTradePairName(String tradePairName) {
		this.tradePairName = tradePairName;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean checked) {
		isChecked = checked;
	}
}
