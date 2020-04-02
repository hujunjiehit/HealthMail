package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class LeverSymbolListModel extends BaseRes {


	private List<DataBean> data;

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * symbol : ETH/USDT
		 * base : ETH
		 * quote : USDT
		 * leverage : 5
		 * pricePrecision : 2
		 * makeFee : 0.001
		 * takeFee : 0.001
		 * sellDisabled : 0
		 * minVolume : 0.01
		 * volumePrecision : 2
		 * initialPrice : 328.85
		 * baseInterestRate : 0.0002
		 * quoteInterestRate : 0.0002
		 */

		private String symbol;
		private String base;
		private String quote;
		private String leverage;
		private int pricePrecision;
		private String makeFee;
		private String takeFee;
		private String sellDisabled;
		private String minVolume;
		private int volumePrecision;
		private String initialPrice;
		private String baseInterestRate;
		private String quoteInterestRate;

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getBase() {
			return base;
		}

		public void setBase(String base) {
			this.base = base;
		}

		public String getQuote() {
			return quote;
		}

		public void setQuote(String quote) {
			this.quote = quote;
		}

		public String getLeverage() {
			return leverage;
		}

		public void setLeverage(String leverage) {
			this.leverage = leverage;
		}

		public int getPricePrecision() {
			return pricePrecision;
		}

		public void setPricePrecision(int pricePrecision) {
			this.pricePrecision = pricePrecision;
		}

		public String getMakeFee() {
			return makeFee;
		}

		public void setMakeFee(String makeFee) {
			this.makeFee = makeFee;
		}

		public String getTakeFee() {
			return takeFee;
		}

		public void setTakeFee(String takeFee) {
			this.takeFee = takeFee;
		}

		public String getSellDisabled() {
			return sellDisabled;
		}

		public void setSellDisabled(String sellDisabled) {
			this.sellDisabled = sellDisabled;
		}

		public String getMinVolume() {
			return minVolume;
		}

		public void setMinVolume(String minVolume) {
			this.minVolume = minVolume;
		}

		public int getVolumePrecision() {
			return volumePrecision;
		}

		public void setVolumePrecision(int volumePrecision) {
			this.volumePrecision = volumePrecision;
		}

		public String getInitialPrice() {
			return initialPrice;
		}

		public void setInitialPrice(String initialPrice) {
			this.initialPrice = initialPrice;
		}

		public String getBaseInterestRate() {
			return baseInterestRate;
		}

		public void setBaseInterestRate(String baseInterestRate) {
			this.baseInterestRate = baseInterestRate;
		}

		public String getQuoteInterestRate() {
			return quoteInterestRate;
		}

		public void setQuoteInterestRate(String quoteInterestRate) {
			this.quoteInterestRate = quoteInterestRate;
		}
	}
}
