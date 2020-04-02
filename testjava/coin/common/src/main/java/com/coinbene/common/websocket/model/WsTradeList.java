package com.coinbene.common.websocket.model;

/**
 * Created by june
 * on 2020-01-15
 */
public class WsTradeList {
	private String price;
	private String type;
	private String amount;
	private String time;

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
