package com.coinbene.common.websocket.model;

import com.coinbene.common.utils.StringUtils;

/**
 * Created by june
 * on 2020-01-14
 */
public class WsExchangeRate {

	/**
	 * langId : zh_CN
	 * langName : 简体中文 - CN
	 * name : CNY
	 * symbol : ￥
	 * rate : 6.9985
	 */

	private String langId;
	private String langName;
	private String name;
	private String symbol;
	private String rate;

	public String getLangId() {
		return langId;
	}

	public void setLangId(String langId) {
		this.langId = langId;
	}

	public String getLangName() {
		return langName;
	}

	public void setLangName(String langName) {
		this.langName = langName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return StringUtils.getCnyReplace(symbol);
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}
}
