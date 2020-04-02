package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-25
 */
public class MarginTotalInfoModel extends BaseRes {


	/**
	 * data : {"btcTotalPreestimate":"0","localTotalPreestimate":"0","currencySymbol":"$","currencyCode":"USD","accountList":[{"block":"0","forceClosePrice":"0","riskRate":"0","symbol":"BTC/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"BTC","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]},{"block":"0","forceClosePrice":"0","riskRate":"0","symbol":"ETH/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"ETH","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]}]}
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
		 * btcTotalPreestimate : 0
		 * localTotalPreestimate : 0
		 * currencySymbol : $
		 * currencyCode : USD
		 * accountList : [{"block":"0","forceClosePrice":"0","riskRate":"0","symbol":"BTC/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"BTC","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]},{"block":"0","forceClosePrice":"0","riskRate":"0","symbol":"ETH/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"ETH","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]}]
		 */

		private String btcTotalPreestimate;
		private String localTotalPreestimate;
		private String currencySymbol;
		private String currencyCode;
		private List<AccountListBean> accountList;

		public String getBtcTotalPreestimate() {
			return btcTotalPreestimate;
		}

		public void setBtcTotalPreestimate(String btcTotalPreestimate) {
			this.btcTotalPreestimate = btcTotalPreestimate;
		}

		public String getLocalTotalPreestimate() {
			return localTotalPreestimate;
		}

		public void setLocalTotalPreestimate(String localTotalPreestimate) {
			this.localTotalPreestimate = localTotalPreestimate;
		}

		public String getCurrencySymbol() {
			return currencySymbol;
		}

		public void setCurrencySymbol(String currencySymbol) {
			this.currencySymbol = currencySymbol;
		}

		public String getCurrencyCode() {
			return currencyCode;
		}

		public void setCurrencyCode(String currencyCode) {
			this.currencyCode = currencyCode;
		}

		public List<AccountListBean> getAccountList() {
			return accountList;
		}

		public void setAccountList(List<AccountListBean> accountList) {
			this.accountList = accountList;
		}

		public static class AccountListBean {
			/**
			 * block : 0
			 * forceClosePrice : 0
			 * riskRate : 0
			 * symbol : BTC/USDT
			 * balanceList : [{"available":"0.00000000","borrow":"0.00000000","asset":"BTC","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000","interest":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]
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
				 * available : 0.00000000
				 * borrow : 0.00000000
				 * asset : BTC
				 * frozen : 0.00000000
				 * interest : 0.00000000
				 * preestimateBTC : 0
				 * localPreestimate : 0
				 */

				private String available;
				private String borrow;
				private String asset;
				private String frozen;
				private String interest;
				private String preestimateBTC;
				private String localPreestimate;

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

				public String getPreestimateBTC() {
					return preestimateBTC;
				}

				public void setPreestimateBTC(String preestimateBTC) {
					this.preestimateBTC = preestimateBTC;
				}

				public String getLocalPreestimate() {
					return localPreestimate;
				}

				public void setLocalPreestimate(String localPreestimate) {
					this.localPreestimate = localPreestimate;
				}
			}
		}
	}
}
