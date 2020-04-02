package com.coinbene.manbiwang.model.contract;

public class ContractPlaceOrderParmsModel {


	private int tradeType;//交易类型    买入 开多  卖出开空等
	private int orderType;//订单类型   限价  市价等
	private String symbol;
	private int lever;
	private String price;
	private String number;
	private String estimatedValue;
	private String profitPrice;
	private String lossPrice;
	private int highLeverOrderType;
	private String marginMode;
	private String realNumber;
	private String unit;
	private boolean isQuickClose;

	public boolean isQuickClose() {
		return isQuickClose;
	}

	public void setQuickClose(boolean quickClose) {
		isQuickClose = quickClose;
	}

	public String getUnit() {
		return unit;
	}

	public String getRealNumber() {
		return realNumber;
	}

	public String getMarginMode() {
		return marginMode;
	}

	public void setMarginMode(String marginMode) {
		this.marginMode = marginMode;
	}

	public int getHighLeverOrderType() {
		return highLeverOrderType;
	}

	public void setHighLeverOrderType(int highLeverOrderType) {
		this.highLeverOrderType = highLeverOrderType;
	}

	public int getOrderType() {
		return orderType;
	}

	public void setOrderType(int orderType) {
		this.orderType = orderType;
	}

	public ContractPlaceOrderParmsModel() {
	}



	public int getTradeType() {
		return tradeType;
	}

	public void setTradeType(int tradeType) {
		this.tradeType = tradeType;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public int getLever() {
		return lever;
	}

	public void setLever(int lever) {
		this.lever = lever;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getEstimatedValue() {
		return estimatedValue;
	}

	public void setEstimatedValue(String estimatedValue) {
		this.estimatedValue = estimatedValue;
	}

	public String getProfitPrice() {
		return profitPrice;
	}

	public void setProfitPrice(String profitPrice) {
		this.profitPrice = profitPrice;
	}

	public String getLossPrice() {
		return lossPrice;
	}

	public void setLossPrice(String lossPrice) {
		this.lossPrice = lossPrice;
	}

	public void setRealNumber(String placeOrderQuantity) {
		this.realNumber = placeOrderQuantity;
	}

	public void setUnit(String s) {
		this.unit = s;
	}

	public void setIsQuickClose(boolean b) {
		this.isQuickClose = b;
	}
}
