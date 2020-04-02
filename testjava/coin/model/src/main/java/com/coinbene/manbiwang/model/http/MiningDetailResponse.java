package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-08-06
 */
public class MiningDetailResponse extends BaseRes {


	/**
	 * data : {"list":[{"date":"2019-08-06","frozenAmount":"string","orderToken":"string","paidAmount":"string","sortToken":"string","tokenAmount":"string","tradeDayToken":"string","tradeMinuteToken":"string"}],"pageNum":0,"pageSize":0}
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
		 * list : [{"date":"2019-08-06","frozenAmount":"string","orderToken":"string","paidAmount":"string","sortToken":"string","tokenAmount":"string","tradeDayToken":"string","tradeMinuteToken":"string"}]
		 * pageNum : 0
		 * pageSize : 0
		 */

		private int pageNum;
		private int pageSize;
		private List<ListBean> list;

		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNum) {
			this.pageNum = pageNum;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * date : 2019-08-06
			 * frozenAmount : string
			 * orderToken : string
			 * paidAmount : string
			 * sortToken : string
			 * tokenAmount : string
			 * tradeDayToken : string
			 * tradeMinuteToken : string
			 */

			private String date;
			private String frozenAmount;
			private String orderToken;
			private String paidAmount;
			private String sortToken;
			private String tokenAmount;
			private String tradeDayToken;
			private String tradeMinuteToken;

			public String getDate() {
				return date;
			}

			public void setDate(String date) {
				this.date = date;
			}

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
		}
	}
}
