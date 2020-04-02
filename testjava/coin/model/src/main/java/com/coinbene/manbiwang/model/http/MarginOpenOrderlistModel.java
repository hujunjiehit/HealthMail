package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-08-18
 */
public class MarginOpenOrderlistModel extends BaseRes {


	/**
	 * data : {"pageNum":1,"total":3,"pageSize":20,"pages":1,"list":[{"orderId":"226","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116500000","lastInterestTime":"1566116500000","borrowQuantity":"1","repayQuantity":"0","interestRate":"0.0002","interest":"0.00000834","repayInterest":"0"},{"orderId":"225","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116482000","lastInterestTime":"1566116482000","borrowQuantity":"0.8","repayQuantity":"0","interestRate":"0.0002","interest":"0.00000667","repayInterest":"0"},{"orderId":"224","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116386000","lastInterestTime":"1566116386000","borrowQuantity":"0.1","repayQuantity":"0","interestRate":"0.0002","interest":"840E-9","repayInterest":"0"}]}
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
		 * total : 3
		 * pageSize : 20
		 * pages : 1
		 * list : [{"orderId":"226","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116500000","lastInterestTime":"1566116500000","borrowQuantity":"1","repayQuantity":"0","interestRate":"0.0002","interest":"0.00000834","repayInterest":"0"},{"orderId":"225","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116482000","lastInterestTime":"1566116482000","borrowQuantity":"0.8","repayQuantity":"0","interestRate":"0.0002","interest":"0.00000667","repayInterest":"0"},{"orderId":"224","symbol":"BTC/USDT","asset":"BTC","createTime":"1566116386000","lastInterestTime":"1566116386000","borrowQuantity":"0.1","repayQuantity":"0","interestRate":"0.0002","interest":"840E-9","repayInterest":"0"}]
		 */

		private int pageNum;
		private int total;
		private int pageSize;
		private int pages;
		private List<ListBean> list;

		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNum) {
			this.pageNum = pageNum;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
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
			 * orderId : 226
			 * symbol : BTC/USDT
			 * asset : BTC
			 * createTime : 1566116500000
			 * lastInterestTime : 1566116500000
			 * borrowQuantity : 1
			 * repayQuantity : 0
			 * interestRate : 0.0002
			 * interest : 0.00000834
			 * repayInterest : 0
			 */

			private String orderId;
			private String symbol;
			private String asset;
			private String createTime;
			private String lastInterestTime;
			private String borrowQuantity;		//借币数量
			private String repayQuantity;		//已还数量
			private String interestRate;		//计息利率
			private String interest;			//借币利息
			private String repayInterest;		//已还利息

			private String unRepayQuantity; 	//未还数量
			private String unRepayInterest;		//未还利息

			public String getOrderId() {
				return orderId;
			}

			public void setOrderId(String orderId) {
				this.orderId = orderId;
			}

			public String getSymbol() {
				return symbol;
			}

			public void setSymbol(String symbol) {
				this.symbol = symbol;
			}

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getCreateTime() {
				return createTime;
			}

			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}

			public String getLastInterestTime() {
				return lastInterestTime;
			}

			public void setLastInterestTime(String lastInterestTime) {
				this.lastInterestTime = lastInterestTime;
			}

			public String getBorrowQuantity() {
				return borrowQuantity;
			}

			public void setBorrowQuantity(String borrowQuantity) {
				this.borrowQuantity = borrowQuantity;
			}

			public String getRepayQuantity() {
				return repayQuantity;
			}

			public void setRepayQuantity(String repayQuantity) {
				this.repayQuantity = repayQuantity;
			}

			public String getInterestRate() {
				return interestRate;
			}

			public void setInterestRate(String interestRate) {
				this.interestRate = interestRate;
			}

			public String getInterest() {
				return interest;
			}

			public void setInterest(String interest) {
				this.interest = interest;
			}

			public String getRepayInterest() {
				return repayInterest;
			}

			public void setRepayInterest(String repayInterest) {
				this.repayInterest = repayInterest;
			}

			public String getUnRepayQuantity() {
				return unRepayQuantity;
			}

			public void setUnRepayQuantity(String unRepayQuantity) {
				this.unRepayQuantity = unRepayQuantity;
			}

			public String getUnRepayInterest() {
				return unRepayInterest;
			}

			public void setUnRepayInterest(String unRepayInterest) {
				this.unRepayInterest = unRepayInterest;
			}
		}
	}
}
