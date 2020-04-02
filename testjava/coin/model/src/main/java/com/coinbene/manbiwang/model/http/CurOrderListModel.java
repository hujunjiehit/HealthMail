package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangle on 2018/4/8.
 */

public class CurOrderListModel extends BaseRes {


	/**
	 * data : {"pageNum":1,"pageSize":10,"total":7,"pages":1,"list":[{"orderId":"201804091554020000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:54:02"},{"orderId":"201804091553220000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:53:22"},{"orderId":"201804091552230000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:52:23"},{"orderId":"201804091546180000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:46:18"},{"orderId":"201804091536210000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:36:21"},{"orderId":"201804091511520000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:11:52"},{"orderId":"201804091501430000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"28","filledQuantity":"0","amount":"808.64","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"28","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:01:44"}]}
	 */

	public DataBean data;

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
		 * total : 7
		 * pages : 1
		 * list : [{"orderId":"201804091554020000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:54:02"},{"orderId":"201804091553220000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:53:22"},{"orderId":"201804091552230000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:52:23"},{"orderId":"201804091546180000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:46:18"},{"orderId":"201804091536210000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:36:21"},{"orderId":"201804091511520000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"20","filledQuantity":"0","amount":"577.6","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"20","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:11:52"},{"orderId":"201804091501430000012","accountType":1,"tradePair":"BTCUSDT","baseAsset":"BTC","quoteAsset":"USDT","orderType":1,"orderDirection":2,"quantity":"28","filledQuantity":"0","amount":"808.64","filledAmount":"0","orderPrice":"28.88","takerFeeRate":"0","makerFeeRate":"0","frozenAmount":"28","avgPrice":"0","orderStatus":0,"bank":"MainBank","orderTime":"2018-04-09 15:01:44"}]
		 */

		public int pageNum;
		public int pageSize;
		public int total;
		public int pages;
		public List<ListBean> list;

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

		public static class ListBean implements Serializable {
			/**
			 * orderId : 201804091554020000012
			 * accountType : 1
			 * tradePair : BTCUSDT
			 * baseAsset : BTC
			 * quoteAsset : USDT
			 * orderType : 1
			 * orderDirection : 2
			 * quantity : 20
			 * filledQuantity : 0
			 * amount : 577.6
			 * filledAmount : 0
			 * orderPrice : 28.88
			 * takerFeeRate : 0
			 * makerFeeRate : 0
			 * frozenAmount : 20
			 * avgPrice : 0
			 * orderStatus : 0
			 * bank : MainBank
			 * orderTime : 2018-04-09 15:54:02
			 */

			public String orderId;
			public int accountType;
			public String tradePair;
			public String baseAsset;// 基础资产
			public String quoteAsset;
			public int orderType;
			public int orderDirection;
			public String quantity;// 委托数量
			public String filledQuantity;// 成交数量
			public String amount;
			public String filledAmount;  //成交金额
			public String orderPrice;
			public String takerFeeRate;
			public String makerFeeRate;
			public String frozenAmount;
			public String avgPrice;
			public int orderStatus;
			public String bank;
			public String orderTime;
			public String totalFee;//手续费

			public String totalFee_usdt;
			public String totalFee_coni;

			public String getOrderId() {
				return orderId;
			}

			public void setOrderId(String orderId) {
				this.orderId = orderId;
			}

			public int getAccountType() {
				return accountType;
			}

			public void setAccountType(int accountType) {
				this.accountType = accountType;
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

			public int getOrderType() {
				return orderType;
			}

			public void setOrderType(int orderType) {
				this.orderType = orderType;
			}

			public int getOrderDirection() {
				return orderDirection;
			}

			public void setOrderDirection(int orderDirection) {
				this.orderDirection = orderDirection;
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

			public String getOrderPrice() {
				return orderPrice;
			}

			public void setOrderPrice(String orderPrice) {
				this.orderPrice = orderPrice;
			}

			public String getTakerFeeRate() {
				return takerFeeRate;
			}

			public void setTakerFeeRate(String takerFeeRate) {
				this.takerFeeRate = takerFeeRate;
			}

			public String getMakerFeeRate() {
				return makerFeeRate;
			}

			public void setMakerFeeRate(String makerFeeRate) {
				this.makerFeeRate = makerFeeRate;
			}

			public String getFrozenAmount() {
				return frozenAmount;
			}

			public void setFrozenAmount(String frozenAmount) {
				this.frozenAmount = frozenAmount;
			}

			public String getAvgPrice() {
				return avgPrice;
			}

			public void setAvgPrice(String avgPrice) {
				this.avgPrice = avgPrice;
			}

			public int getOrderStatus() {
				return orderStatus;
			}

			public void setOrderStatus(int orderStatus) {
				this.orderStatus = orderStatus;
			}

			public String getBank() {
				return bank;
			}

			public void setBank(String bank) {
				this.bank = bank;
			}

			public String getOrderTime() {
				return orderTime;
			}

			public void setOrderTime(String orderTime) {
				this.orderTime = orderTime;
			}

			public String getTotalFee() {
				return totalFee;
			}

			public void setTotalFee(String totalFee) {
				this.totalFee = totalFee;
			}
		}
	}
}
