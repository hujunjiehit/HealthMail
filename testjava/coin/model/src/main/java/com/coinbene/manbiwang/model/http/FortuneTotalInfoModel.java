package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-18
 */
public class FortuneTotalInfoModel extends BaseRes {

	/**
	 * data : {"btcTotalPreestimate":"341.141191","localTotalPreestimate":"2764692.6720176","currencySymbol":"$","currenyCode":"USD","currentPreestimate":{"btcTotalPreestimate":"341.141191","localTotalPreestimate":"2740591.8720176","currencySymbol":"$","currenyCode":"USD","currentAccountList":[{"asset":"BTC","availableBalance":"340.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"19.177900","yesterdayDividend":"0.000000","historyDividend":"0.000160","preestimateBTC":"340","localPreestimate":"2731424"},{"asset":"ETH","availableBalance":"23.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"0.000000","preestimateBTC":"1.141191","localPreestimate":"9167.87"}]},"financialAccountList":[{"asset":"BTC","availableBalance":"3.00000000","preestimateBTC":"3","localPreestimate":"24100.8"},{"asset":"ETH","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]}
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
		 * btcTotalPreestimate : 341.141191
		 * localTotalPreestimate : 2764692.6720176
		 * currencySymbol : $
		 * currenyCode : USD
		 * currentPreestimate : {"btcTotalPreestimate":"341.141191","localTotalPreestimate":"2740591.8720176","currencySymbol":"$","currenyCode":"USD","currentAccountList":[{"asset":"BTC","availableBalance":"340.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"19.177900","yesterdayDividend":"0.000000","historyDividend":"0.000160","preestimateBTC":"340","localPreestimate":"2731424"},{"asset":"ETH","availableBalance":"23.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"0.000000","preestimateBTC":"1.141191","localPreestimate":"9167.87"}]}
		 * financialAccountList : [{"asset":"BTC","availableBalance":"3.00000000","preestimateBTC":"3","localPreestimate":"24100.8"},{"asset":"ETH","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]
		 */

		private String btcTotalPreestimate;
		private String localTotalPreestimate;
		private String currencySymbol;
		private String currenyCode;
		private CurrentPreestimateBean currentPreestimate;
		private List<FinancialAccountListBean> financialAccountList;

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

		public String getCurrenyCode() {
			return currenyCode;
		}

		public void setCurrenyCode(String currenyCode) {
			this.currenyCode = currenyCode;
		}

		public CurrentPreestimateBean getCurrentPreestimate() {
			return currentPreestimate;
		}

		public void setCurrentPreestimate(CurrentPreestimateBean currentPreestimate) {
			this.currentPreestimate = currentPreestimate;
		}

		public List<FinancialAccountListBean> getFinancialAccountList() {
			return financialAccountList;
		}

		public void setFinancialAccountList(List<FinancialAccountListBean> financialAccountList) {
			this.financialAccountList = financialAccountList;
		}

		public static class CurrentPreestimateBean {
			/**
			 * btcTotalPreestimate : 341.141191
			 * localTotalPreestimate : 2740591.8720176
			 * currencySymbol : $
			 * currenyCode : USD
			 * currentAccountList : [{"asset":"BTC","availableBalance":"340.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"19.177900","yesterdayDividend":"0.000000","historyDividend":"0.000160","preestimateBTC":"340","localPreestimate":"2731424"},{"asset":"ETH","availableBalance":"23.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"0.000000","preestimateBTC":"1.141191","localPreestimate":"9167.87"}]
			 */

			private String btcTotalPreestimate;
			private String localTotalPreestimate;
			private String currencySymbol;
			private String currenyCode;
			private List<CurrentAccountListBean> currentAccountList;

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

			public String getCurrenyCode() {
				return currenyCode;
			}

			public void setCurrenyCode(String currenyCode) {
				this.currenyCode = currenyCode;
			}

			public List<CurrentAccountListBean> getCurrentAccountList() {
				return currentAccountList;
			}

			public void setCurrentAccountList(List<CurrentAccountListBean> currentAccountList) {
				this.currentAccountList = currentAccountList;
			}

			public static class CurrentAccountListBean {
				/**
				 * asset : BTC
				 * availableBalance : 340.00000000
				 * totalProfit : 0.00000000
				 * yesterdayProfit : 0.00000000
				 * lastSevenDayAnnual : 19.177900
				 * yesterdayDividend : 0.000000
				 * historyDividend : 0.000160
				 * preestimateBTC : 340
				 * localPreestimate : 2731424
				 */

				private String asset;
				private String availableBalance;
				private String totalProfit;
				private String yesterdayProfit;
				private String lastSevenDayAnnual;
				private String yesterdayDividend;
				private String historyDividend;
				private String preestimateBTC;
				private String localPreestimate;

				public String getAsset() {
					return asset;
				}

				public void setAsset(String asset) {
					this.asset = asset;
				}

				public String getAvailableBalance() {
					return availableBalance;
				}

				public void setAvailableBalance(String availableBalance) {
					this.availableBalance = availableBalance;
				}

				public String getTotalProfit() {
					return totalProfit;
				}

				public void setTotalProfit(String totalProfit) {
					this.totalProfit = totalProfit;
				}

				public String getYesterdayProfit() {
					return yesterdayProfit;
				}

				public void setYesterdayProfit(String yesterdayProfit) {
					this.yesterdayProfit = yesterdayProfit;
				}

				public String getLastSevenDayAnnual() {
					return lastSevenDayAnnual;
				}

				public void setLastSevenDayAnnual(String lastSevenDayAnnual) {
					this.lastSevenDayAnnual = lastSevenDayAnnual;
				}

				public String getYesterdayDividend() {
					return yesterdayDividend;
				}

				public void setYesterdayDividend(String yesterdayDividend) {
					this.yesterdayDividend = yesterdayDividend;
				}

				public String getHistoryDividend() {
					return historyDividend;
				}

				public void setHistoryDividend(String historyDividend) {
					this.historyDividend = historyDividend;
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

		public static class FinancialAccountListBean {
			/**
			 * asset : BTC
			 * availableBalance : 3.00000000
			 * preestimateBTC : 3
			 * localPreestimate : 24100.8
			 */

			private String asset;
			private String availableBalance;
			private String preestimateBTC;
			private String localPreestimate;

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getAvailableBalance() {
				return availableBalance;
			}

			public void setAvailableBalance(String availableBalance) {
				this.availableBalance = availableBalance;
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
