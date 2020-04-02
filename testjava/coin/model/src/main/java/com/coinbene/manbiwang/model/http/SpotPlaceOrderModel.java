package com.coinbene.manbiwang.model.http;


/**
 * 现货下单实体类
 */
public class SpotPlaceOrderModel {

	private int orderType;
	private boolean isBuy;
	private String symbol;
	private String price;
	private String quantity;
	private String touchPrice;
	private String ocoPrice;

	public String getOcoPrice() {
		return ocoPrice;
	}

	public void setOcoPrice(String ocoPrice) {
		this.ocoPrice = ocoPrice;
	}

	public String getTouchPrice() {
		return touchPrice;
	}

	public void setTouchPrice(String touchPrice) {
		this.touchPrice = touchPrice;
	}

	public SpotPlaceOrderModel() {
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public boolean isBuy() {
		return isBuy;
	}

	public void setBuy(boolean buy) {
		isBuy = buy;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
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

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
}
