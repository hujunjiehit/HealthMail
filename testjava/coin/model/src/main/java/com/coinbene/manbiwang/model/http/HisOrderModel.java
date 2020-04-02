package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class HisOrderModel extends BaseRes {


	/**
	 * data : {"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":2,"planTriggeredCondition":null,"triggeredTime":null,"quantity":"0.0001","filledQuantity":"0.0001","avgPrice":"13513","orderPrice":"0","amount":"0","filledAmount":"1.3513","fee":"0.0013513","direction":2,"orderStatus":1,"feeByConi":"0","ocoLimitTriggerCondition":null,"ocoTriggerCondition":null,"ocoTriggeredCondition":null,"ocoTriggeredOrderId":null,"ocoTriggeredOrderPrice":null,"ocoTriggeredQuantity":null,"ocoTriggeredAmount":null,"ocoCancelledOrderId":null,"ocoCancelledOrderPrice":null,"ocoCancelledQuantity":null,"ocoCancelledAmount":null,"tradeRecordVo":[{"price":"13513","quantity":"0.0001","amount":"1.3513","fee":"0.0013513","direction":2,"tradeTime":"2019-12-30 12:07:57","feeByConi":"-1"}]}
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
		 * tradePair : BTCUSDT
		 * baseAsset : BTC
		 * quoteAsset : USDT
		 * orderType : 2
		 * planTriggeredCondition : null
		 * triggeredTime : null
		 * quantity : 0.0001
		 * filledQuantity : 0.0001
		 * avgPrice : 13513
		 * orderPrice : 0
		 * amount : 0
		 * filledAmount : 1.3513
		 * fee : 0.0013513
		 * direction : 2
		 * orderStatus : 1
		 * feeByConi : 0
		 * ocoLimitTriggerCondition : null
		 * ocoTriggerCondition : null
		 * ocoTriggeredCondition : null
		 * ocoTriggeredOrderId : null
		 * ocoTriggeredOrderPrice : null
		 * ocoTriggeredQuantity : null
		 * ocoTriggeredAmount : null
		 * ocoCancelledOrderId : null
		 * ocoCancelledOrderPrice : null
		 * ocoCancelledQuantity : null
		 * ocoCancelledAmount : null
		 * tradeRecordVo : [{"price":"13513","quantity":"0.0001","amount":"1.3513","fee":"0.0013513","direction":2,"tradeTime":"2019-12-30 12:07:57","feeByConi":"-1"}]
		 */

		private String tradePair;
		private String baseAsset;
		private String quoteAsset;
		private int orderType;
		private String planTriggeredCondition;
		private String triggeredTime;
		private String quantity;
		private String filledQuantity;
		private String avgPrice;
		private String orderPrice;
		private String amount;
		private String filledAmount;
		private String fee;
		private int direction;
		private int orderStatus;
		private String feeByConi;
		private String ocoLimitTriggerCondition;
		private String ocoTriggerCondition;
		private String ocoTriggeredCondition;
		private String ocoTriggeredOrderId;
		private String ocoTriggeredOrderPrice;
		private String ocoTriggeredQuantity;
		private String ocoTriggeredAmount;
		private String ocoCancelledOrderId;
		private String ocoCancelledOrderPrice;
		private String ocoCancelledQuantity;
		private String ocoCancelledAmount;
		private List<TradeRecordVoBean> tradeRecordVo;

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

		public int getOrderType() {
			return orderType;
		}

		public void setOrderType(int orderType) {
			this.orderType = orderType;
		}

		public String getPlanTriggeredCondition() {
			return planTriggeredCondition;
		}

		public void setPlanTriggeredCondition(String planTriggeredCondition) {
			this.planTriggeredCondition = planTriggeredCondition;
		}

		public String getTriggeredTime() {
			return triggeredTime;
		}

		public void setTriggeredTime(String triggeredTime) {
			this.triggeredTime = triggeredTime;
		}

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}

		public String getFilledQuantity() {
			return filledQuantity;
		}

		public void setFilledQuantity(String filledQuantity) {
			this.filledQuantity = filledQuantity;
		}

		public String getAvgPrice() {
			return avgPrice;
		}

		public void setAvgPrice(String avgPrice) {
			this.avgPrice = avgPrice;
		}

		public String getOrderPrice() {
			return orderPrice;
		}

		public void setOrderPrice(String orderPrice) {
			this.orderPrice = orderPrice;
		}

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getFilledAmount() {
			return filledAmount;
		}

		public void setFilledAmount(String filledAmount) {
			this.filledAmount = filledAmount;
		}

		public String getFee() {
			return fee;
		}

		public void setFee(String fee) {
			this.fee = fee;
		}

		public int getDirection() {
			return direction;
		}

		public void setDirection(int direction) {
			this.direction = direction;
		}

		public int getOrderStatus() {
			return orderStatus;
		}

		public void setOrderStatus(int orderStatus) {
			this.orderStatus = orderStatus;
		}

		public String getFeeByConi() {
			return feeByConi;
		}

		public void setFeeByConi(String feeByConi) {
			this.feeByConi = feeByConi;
		}

		public String getOcoLimitTriggerCondition() {
			return ocoLimitTriggerCondition;
		}

		public void setOcoLimitTriggerCondition(String ocoLimitTriggerCondition) {
			this.ocoLimitTriggerCondition = ocoLimitTriggerCondition;
		}

		public String getOcoTriggerCondition() {
			return ocoTriggerCondition;
		}

		public void setOcoTriggerCondition(String ocoTriggerCondition) {
			this.ocoTriggerCondition = ocoTriggerCondition;
		}

		public String getOcoTriggeredCondition() {
			return ocoTriggeredCondition;
		}

		public void setOcoTriggeredCondition(String ocoTriggeredCondition) {
			this.ocoTriggeredCondition = ocoTriggeredCondition;
		}

		public String getOcoTriggeredOrderId() {
			return ocoTriggeredOrderId;
		}

		public void setOcoTriggeredOrderId(String ocoTriggeredOrderId) {
			this.ocoTriggeredOrderId = ocoTriggeredOrderId;
		}

		public String getOcoTriggeredOrderPrice() {
			return ocoTriggeredOrderPrice;
		}

		public void setOcoTriggeredOrderPrice(String ocoTriggeredOrderPrice) {
			this.ocoTriggeredOrderPrice = ocoTriggeredOrderPrice;
		}

		public String getOcoTriggeredQuantity() {
			return ocoTriggeredQuantity;
		}

		public void setOcoTriggeredQuantity(String ocoTriggeredQuantity) {
			this.ocoTriggeredQuantity = ocoTriggeredQuantity;
		}

		public String getOcoTriggeredAmount() {
			return ocoTriggeredAmount;
		}

		public void setOcoTriggeredAmount(String ocoTriggeredAmount) {
			this.ocoTriggeredAmount = ocoTriggeredAmount;
		}

		public String getOcoCancelledOrderId() {
			return ocoCancelledOrderId;
		}

		public void setOcoCancelledOrderId(String ocoCancelledOrderId) {
			this.ocoCancelledOrderId = ocoCancelledOrderId;
		}

		public String getOcoCancelledOrderPrice() {
			return ocoCancelledOrderPrice;
		}

		public void setOcoCancelledOrderPrice(String ocoCancelledOrderPrice) {
			this.ocoCancelledOrderPrice = ocoCancelledOrderPrice;
		}

		public String getOcoCancelledQuantity() {
			return ocoCancelledQuantity;
		}

		public void setOcoCancelledQuantity(String ocoCancelledQuantity) {
			this.ocoCancelledQuantity = ocoCancelledQuantity;
		}

		public String getOcoCancelledAmount() {
			return ocoCancelledAmount;
		}

		public void setOcoCancelledAmount(String ocoCancelledAmount) {
			this.ocoCancelledAmount = ocoCancelledAmount;
		}

		public List<TradeRecordVoBean> getTradeRecordVo() {
			return tradeRecordVo;
		}

		public void setTradeRecordVo(List<TradeRecordVoBean> tradeRecordVo) {
			this.tradeRecordVo = tradeRecordVo;
		}

		public static class TradeRecordVoBean {
			/**
			 * price : 13513
			 * quantity : 0.0001
			 * amount : 1.3513
			 * fee : 0.0013513
			 * direction : 2
			 * tradeTime : 2019-12-30 12:07:57
			 * feeByConi : -1
			 */

			private String price;
			private String quantity;
			private String amount;
			private String fee;
			private int direction;
			private String tradeTime;
			private String feeByConi;

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

			public String getAmount() {
				return amount;
			}

			public void setAmount(String amount) {
				this.amount = amount;
			}

			public String getFee() {
				return fee;
			}

			public void setFee(String fee) {
				this.fee = fee;
			}

			public int getDirection() {
				return direction;
			}

			public void setDirection(int direction) {
				this.direction = direction;
			}

			public String getTradeTime() {
				return tradeTime;
			}

			public void setTradeTime(String tradeTime) {
				this.tradeTime = tradeTime;
			}

			public String getFeeByConi() {
				return feeByConi;
			}

			public void setFeeByConi(String feeByConi) {
				this.feeByConi = feeByConi;
			}
		}
	}
}
