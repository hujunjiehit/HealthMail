package com.coinbene.manbiwang.service.spot.message;

/**
 * Created by june
 * on 2019-12-09
 */
public class BuyOrSellPriceOneMessage {
	private String buyPriceOne;
	private String sellPriceOne;
	private String symbol;

	public BuyOrSellPriceOneMessage(String buyPriceOne, String sellPriceOne, String symbol) {
		this.buyPriceOne = buyPriceOne;
		this.sellPriceOne = sellPriceOne;
		this.symbol = symbol;
	}

	public String getBuyPriceOne() {
		return buyPriceOne;
	}

	public void setBuyPriceOne(String buyPriceOne) {
		this.buyPriceOne = buyPriceOne;
	}

	public String getSellPriceOne() {
		return sellPriceOne;
	}

	public void setSellPriceOne(String sellPriceOne) {
		this.sellPriceOne = sellPriceOne;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
}
