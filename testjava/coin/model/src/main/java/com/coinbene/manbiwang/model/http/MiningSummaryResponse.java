package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-08-06
 */
public class MiningSummaryResponse extends BaseRes {

	/**
	 * data : {"frozenAmount":"string","orderToken":"string","paidAmount":"string","sortToken":"string","tokenAmount":"string","tradeDayToken":"string","tradeMinuteToken":"string","yesterdayTokenAmount":"string"}
	 * timezone : 0
	 */

	private DataBean data;
	private int timezone;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public static class DataBean {
		/**
		 * frozenAmount : string
		 * orderToken : string
		 * paidAmount : string
		 * sortToken : string
		 * tokenAmount : string
		 * tradeDayToken : string
		 * tradeMinuteToken : string
		 * yesterdayTokenAmount : string
		 */

		private String frozenAmount;
		private String orderToken;
		private String paidAmount;
		private String sortToken;
		private String tokenAmount;
		private String tradeDayToken;
		private String tradeMinuteToken;
		private String yesterdayTokenAmount;

		public String getFrozenAmount() {
			return frozenAmount;
		}

		public void setFrozenAmount(String frozenAmount) {
			this.frozenAmount = frozenAmount;
		}

		public String getOrderToken() {
			return orderToken;
		}

		public void setOrderToken(String orderToken) {
			this.orderToken = orderToken;
		}

		public String getPaidAmount() {
			return paidAmount;
		}

		public void setPaidAmount(String paidAmount) {
			this.paidAmount = paidAmount;
		}

		public String getSortToken() {
			return sortToken;
		}

		public void setSortToken(String sortToken) {
			this.sortToken = sortToken;
		}

		public String getTokenAmount() {
			return tokenAmount;
		}

		public void setTokenAmount(String tokenAmount) {
			this.tokenAmount = tokenAmount;
		}

		public String getTradeDayToken() {
			return tradeDayToken;
		}

		public void setTradeDayToken(String tradeDayToken) {
			this.tradeDayToken = tradeDayToken;
		}

		public String getTradeMinuteToken() {
			return tradeMinuteToken;
		}

		public void setTradeMinuteToken(String tradeMinuteToken) {
			this.tradeMinuteToken = tradeMinuteToken;
		}

		public String getYesterdayTokenAmount() {
			return yesterdayTokenAmount;
		}

		public void setYesterdayTokenAmount(String yesterdayTokenAmount) {
			this.yesterdayTokenAmount = yesterdayTokenAmount;
		}
	}
}
