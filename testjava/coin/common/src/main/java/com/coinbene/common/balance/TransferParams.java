package com.coinbene.common.balance;

import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by june
 * on 2019-09-20
 *
 * 进入转账页面需要的参数
 */
public class TransferParams {

	public static final String EXTRA_ASSET = "asset";
	public static final String EXTRA_FROM_ACCOUNT = "from";
	public static final String EXTRA_TO_ACCOUNT = "to";
	public static final String EXTRA_FROM_SYMBOL = "fromsymbol";
	public static final String EXTRA_TO_SYMBOL = "tosymbol";

	private String asset; //默认的划转资产
	private String from;	//默认的转出账户
	private String to;	//默认的转入账户
	private String fromSymbol;  //杠杆账户转出币对
	private String toSymbol;	//杠杆账户转入币对

	public TransferParams() {
	}

	public Bundle toBundle() {
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_ASSET, TextUtils.isEmpty(asset) ? "" : asset);
		bundle.putString(EXTRA_FROM_ACCOUNT, TextUtils.isEmpty(from) ? "" : from);
		bundle.putString(EXTRA_TO_ACCOUNT, TextUtils.isEmpty(to) ? "" : to);
		bundle.putString(EXTRA_FROM_SYMBOL, TextUtils.isEmpty(fromSymbol) ? "" : fromSymbol);
		bundle.putString(EXTRA_TO_SYMBOL, TextUtils.isEmpty(toSymbol) ? "" : toSymbol);
		return bundle;
	}

	public String getAsset() {
		return asset;
	}

	public TransferParams setAsset(String asset) {
		this.asset = asset;
		return this;
	}

	public String getFrom() {
		return from;
	}

	public TransferParams setFrom(String fromAccount) {
		this.from = fromAccount;
		return this;
	}

	public String getTo() {
		return to;
	}

	public TransferParams setTo(String toToAccount) {
		this.to = toToAccount;
		return this;
	}

	public String getFromSymbol() {
		return fromSymbol;
	}

	public TransferParams setFromSymbol(String fromSymbol) {
		this.fromSymbol = fromSymbol;
		return this;
	}

	public String getToSymbol() {
		return toSymbol;
	}

	public TransferParams setToSymbol(String toSymbol) {
		this.toSymbol = toSymbol;
		return this;
	}
}
