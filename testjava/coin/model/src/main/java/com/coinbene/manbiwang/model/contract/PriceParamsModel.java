package com.coinbene.manbiwang.model.contract;

/**
 * Created by june
 * on 2020-03-09
 */
public class PriceParamsModel {
	private String buyOnePrice;		//买一价
	private String sellOnePrice;	//卖一价
	private String lastPrice;		//最新价
	private String markPrice;		//标记价
	private String symbol;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getBuyOnePrice() {
		return buyOnePrice;
	}

	public void setBuyOnePrice(String buyOnePrice) {
		this.buyOnePrice = buyOnePrice;
	}

	public String getSellOnePrice() {
		return sellOnePrice;
	}

	public void setSellOnePrice(String sellOnePrice) {
		this.sellOnePrice = sellOnePrice;
	}

	public String getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public String getMarkPrice() {
		return markPrice;
	}

	public void setMarkPrice(String markPrice) {
		this.markPrice = markPrice;
	}

	@Override
	public String toString() {
		return "PriceParamsModel{" +
				"buyOnePrice='" + buyOnePrice + '\'' +
				", sellOnePrice='" + sellOnePrice + '\'' +
				", lastPrice='" + lastPrice + '\'' +
				", markPrice='" + markPrice + '\'' +
				'}';
	}
}
