package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-08-18
 */
public class SingleAccountModel extends BaseRes {

	/**
	 * data : {"block":"0","forceClosePrice":"0","riskRate":"2.5789","symbol":"BTC/USDT","balanceList":[{"available":"4.90000000","borrow":"1.90000000","asset":"BTC","frozen":"0.00000000","interest":"0.00001584","interestRate":"0.0002","minBorrow":"0.001"},{"available":"0.00","borrow":"0.00","asset":"USDT","frozen":"0.00","interest":"0.00","interestRate":"0.0002","minBorrow":"1"}]}
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
		 * block : 0
		 * forceClosePrice : 0
		 * riskRate : 2.5789
		 * symbol : BTC/USDT
		 * balanceList : [{"available":"4.90000000","borrow":"1.90000000","asset":"BTC","frozen":"0.00000000","interest":"0.00001584","interestRate":"0.0002","minBorrow":"0.001"},{"available":"0.00","borrow":"0.00","asset":"USDT","frozen":"0.00","interest":"0.00","interestRate":"0.0002","minBorrow":"1"}]
		 */

		private String block;
		private String forceClosePrice;
		private String riskRate;
		private String symbol;
		private List<BalanceListBean> balanceList;

		public String getBlock() {
			return block;
		}

		public void setBlock(String block) {
			this.block = block;
		}

		public String getForceClosePrice() {
			return forceClosePrice;
		}

		public void setForceClosePrice(String forceClosePrice) {
			this.forceClosePrice = forceClosePrice;
		}

		public String getRiskRate() {
			return riskRate;
		}

		public void setRiskRate(String riskRate) {
			this.riskRate = riskRate;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public List<BalanceListBean> getBalanceList() {
			return balanceList;
		}

		public void setBalanceList(List<BalanceListBean> balanceList) {
			this.balanceList = balanceList;
		}

		public static class BalanceListBean {
			/**
			 * available : 4.90000000
			 * borrow : 1.90000000
			 * asset : BTC
			 * frozen : 0.00000000
			 * interest : 0.00001584
			 * interestRate : 0.0002
			 * minBorrow : 0.001
			 */

			private String available;
			private String borrow;
			private String asset;
			private String frozen;
			private String interest;
			private String interestRate;
			private String minBorrow;

			public String getAvailable() {
				return available;
			}

			public void setAvailable(String available) {
				this.available = available;
			}

			public String getBorrow() {
				return borrow;
			}

			public void setBorrow(String borrow) {
				this.borrow = borrow;
			}

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getFrozen() {
				return frozen;
			}

			public void setFrozen(String frozen) {
				this.frozen = frozen;
			}

			public String getInterest() {
				return interest;
			}

			public void setInterest(String interest) {
				this.interest = interest;
			}

			public String getInterestRate() {
				return interestRate;
			}

			public void setInterestRate(String interestRate) {
				this.interestRate = interestRate;
			}

			public String getMinBorrow() {
				return minBorrow;
			}

			public void setMinBorrow(String minBorrow) {
				this.minBorrow = minBorrow;
			}
		}
	}
}
