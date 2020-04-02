package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-25
 */
public class GameTotalInfoModel extends BaseRes {


	/**
	 * data : {"btcTotalPreestimate":"0.00000000","localTotalPreestimate":"0.00","currencySymbol":"$","currencyCode":"USD","list":[{"asset":"USDT","totalBalance":"0.00000000","availableBalance":"0.00000000","frozenBalance":"0.00000000","preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"asset":"CONI","totalBalance":"0.00000000","availableBalance":"0.00000000","frozenBalance":"0.00000000","preestimateBTC":"0.00000000","localPreestimate":"0.00"}]}
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
		 * btcTotalPreestimate : 0.00000000
		 * localTotalPreestimate : 0.00
		 * currencySymbol : $
		 * currencyCode : USD
		 * list : [{"asset":"USDT","totalBalance":"0.00000000","availableBalance":"0.00000000","frozenBalance":"0.00000000","preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"asset":"CONI","totalBalance":"0.00000000","availableBalance":"0.00000000","frozenBalance":"0.00000000","preestimateBTC":"0.00000000","localPreestimate":"0.00"}]
		 */

		private String btcTotalPreestimate;
		private String localTotalPreestimate;
		private String currencySymbol;
		private String currencyCode;
		private List<ListBean> list;

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

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * asset : USDT
			 * totalBalance : 0.00000000
			 * availableBalance : 0.00000000
			 * frozenBalance : 0.00000000
			 * preestimateBTC : 0.00000000
			 * localPreestimate : 0.00
			 */

			private String asset;
			private String totalBalance;
			private String availableBalance;
			private String frozenBalance;
			private String preestimateBTC;
			private String localPreestimate;

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getTotalBalance() {
				return totalBalance;
			}

			public void setTotalBalance(String totalBalance) {
				this.totalBalance = totalBalance;
			}

			public String getAvailableBalance() {
				return availableBalance;
			}

			public void setAvailableBalance(String availableBalance) {
				this.availableBalance = availableBalance;
			}

			public String getFrozenBalance() {
				return frozenBalance;
			}

			public void setFrozenBalance(String frozenBalance) {
				this.frozenBalance = frozenBalance;
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
