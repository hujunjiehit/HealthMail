package com.coinbene.manbiwang.model.http;


/**
 * 杠杆下单参数
 */
public class MarginPlaceOrderParms {

	private boolean isBuy;
	private String price;
	private String quantity;
	private String symbol;

	public boolean isBuy() {
		return isBuy;
	}

	public void setBuy(boolean buy) {
		isBuy = buy;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String vol) {
		this.quantity = vol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
