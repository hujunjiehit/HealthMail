package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class HighLeverOrderModel extends BaseRes {


	/**
	 * data : {"pageNum":1,"pageSize":10,"total":1,"pages":1,"list":[{"planOrderId":"1983245869297905664","orderId":null,"createTime":"2019-12-26 17:11:09","orderType":4,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderDirection":1,"planTriggerCondition":"≤10000","planOrderPrice":"10000","ocoLimitTriggerCondition":null,"ocoLimitPrice":null,"ocoTriggerCondition":null,"ocoOrderPrice":null,"ocoTriggeredCondition":null,"quantity":"1","amount":"10000","status":0}]}
	 */

	private DataBean data;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * pageNum : 1
		 * pageSize : 10
		 * total : 1
		 * pages : 1
		 * list : [{"planOrderId":"1983245869297905664","orderId":null,"createTime":"2019-12-26 17:11:09","orderType":4,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderDirection":1,"planTriggerCondition":"≤10000","planOrderPrice":"10000","ocoLimitTriggerCondition":null,"ocoLimitPrice":null,"ocoTriggerCondition":null,"ocoOrderPrice":null,"ocoTriggeredCondition":null,"quantity":"1","amount":"10000","status":0}]
		 */

		private int pageNum;
		private int pageSize;
		private int total;
		private int pages;
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

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getPages() {
			return pages;
		}

		public void setPages(int pages) {
			this.pages = pages;
		}

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * planOrderId : 1983245869297905664
			 * orderId : null
			 * createTime : 2019-12-26 17:11:09
			 * orderType : 4
			 * tradePair : BTCUSDT
			 * baseAsset : BTC
			 * quoteAsset : USDT
			 * orderDirection : 1
			 * planTriggerCondition : ≤10000
			 * planOrderPrice : 10000
			 * ocoLimitTriggerCondition : null
			 * ocoLimitPrice : null
			 * ocoTriggerCondition : null
			 * ocoOrderPrice : null
			 * ocoTriggeredCondition : null
			 * quantity : 1
			 * amount : 10000
			 * status : 0
			 */

			private String planOrderId;
			private long orderId;
			private String createTime;
			private int orderType;
			private String tradePair;
			private String baseAsset;
			private String quoteAsset;
			private int orderDirection;
			private String planTriggerCondition;
			private String planOrderPrice;
			private String ocoLimitTriggerCondition;
			private String ocoLimitPrice;
			private String ocoTriggerCondition;
			private String ocoOrderPrice;
			private String ocoTriggeredCondition;
			private String quantity;
			private String amount;
			private int status;
			private String ocoTriggeredOrderPrice;

			public String getOcoTriggeredOrderPrice() {
				return ocoTriggeredOrderPrice;
			}

			public void setOcoTriggeredOrderPrice(String ocoTriggeredOrderPrice) {
				this.ocoTriggeredOrderPrice = ocoTriggeredOrderPrice;
			}

			public String getPlanOrderId() {
				return planOrderId;
			}

			public void setPlanOrderId(String planOrderId) {
				this.planOrderId = planOrderId;
			}

			public long getOrderId() {
				return orderId;
			}

			public void setOrderId(long orderId) {
				this.orderId = orderId;
			}

			public String getCreateTime() {
				return createTime;
			}

			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}

			public int getOrderType() {
				return orderType;
			}

			public void setOrderType(int orderType) {
				this.orderType = orderType;
			}

			public String getTradePair() {
				return tradePair;
			}

			public void setTradePair(String tradePair) {
				this.tradePair = tradePair;
			}

			public String getBaseAsset() {
				return baseAsset;
			}

			public void setBaseAsset(String baseAsset) {
				this.baseAsset = baseAsset;
			}

			public String getQuoteAsset() {
				return quoteAsset;
			}

			public void setQuoteAsset(String quoteAsset) {
				this.quoteAsset = quoteAsset;
			}

			public int getOrderDirection() {
				return orderDirection;
			}

			public void setOrderDirection(int orderDirection) {
				this.orderDirection = orderDirection;
			}

			public String getPlanTriggerCondition() {
				return planTriggerCondition;
			}

			public void setPlanTriggerCondition(String planTriggerCondition) {
				this.planTriggerCondition = planTriggerCondition;
			}

			public String getPlanOrderPrice() {
				return planOrderPrice;
			}

			public void setPlanOrderPrice(String planOrderPrice) {
				this.planOrderPrice = planOrderPrice;
			}

			public String getOcoLimitTriggerCondition() {
				return ocoLimitTriggerCondition;
			}

			public void setOcoLimitTriggerCondition(String ocoLimitTriggerCondition) {
				this.ocoLimitTriggerCondition = ocoLimitTriggerCondition;
			}

			public String getOcoLimitPrice() {
				return ocoLimitPrice;
			}

			public void setOcoLimitPrice(String ocoLimitPrice) {
				this.ocoLimitPrice = ocoLimitPrice;
			}

			public String getOcoTriggerCondition() {
				return ocoTriggerCondition;
			}

			public void setOcoTriggerCondition(String ocoTriggerCondition) {
				this.ocoTriggerCondition = ocoTriggerCondition;
			}

			public String getOcoOrderPrice() {
				return ocoOrderPrice;
			}

			public void setOcoOrderPrice(String ocoOrderPrice) {
				this.ocoOrderPrice = ocoOrderPrice;
			}

			public String getOcoTriggeredCondition() {
				return ocoTriggeredCondition;
			}

			public void setOcoTriggeredCondition(String ocoTriggeredCondition) {
				this.ocoTriggeredCondition = ocoTriggeredCondition;
			}

			public String getQuantity() {
				return quantity;
			}

			public void setQuantity(String quantity) {
				this.quantity = quantity;
			}

			public String getAmount() {
				return amount;
			}

			public void setAmount(String amount) {
				this.amount = amount;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}
		}
	}
}
