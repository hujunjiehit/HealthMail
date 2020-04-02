package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-25
 */
public class OptionsTotalInfoModel extends BaseRes {


	/**
	 * data : {"btcTotalPreestimate":"0.00000000","localTotalPreestimate":"0.00","currencySymbol":"$","currencyCode":"USD","list":[{"brokerId":"14","assetId":2,"assetName":"BTC","amount":"0.00000000","sort":0,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":13,"assetName":"CONI","amount":"0.0000","sort":1,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":3,"assetName":"ETH","amount":"0.0000","sort":2,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":15,"assetName":"USDT","amount":"0.0000","sort":3,"preestimateBTC":"0.00000000","localPreestimate":"0.00"}]}
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
		 * list : [{"brokerId":"14","assetId":2,"assetName":"BTC","amount":"0.00000000","sort":0,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":13,"assetName":"CONI","amount":"0.0000","sort":1,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":3,"assetName":"ETH","amount":"0.0000","sort":2,"preestimateBTC":"0.00000000","localPreestimate":"0.00"},{"brokerId":"14","assetId":15,"assetName":"USDT","amount":"0.0000","sort":3,"preestimateBTC":"0.00000000","localPreestimate":"0.00"}]
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
			 * brokerId : 14
			 * assetId : 2
			 * assetName : BTC
			 * amount : 0.00000000
			 * sort : 0
			 * preestimateBTC : 0.00000000
			 * localPreestimate : 0.00
			 */

			private String brokerId;
			private int assetId;
			private String assetName;
			private String amount;
			private int sort;
			private String preestimateBTC;
			private String localPreestimate;

			public String getBrokerId() {
				return brokerId;
			}

			public void setBrokerId(String brokerId) {
				this.brokerId = brokerId;
			}

			public int getAssetId() {
				return assetId;
			}

			public void setAssetId(int assetId) {
				this.assetId = assetId;
			}

			public String getAssetName() {
				return assetName;
			}

			public void setAssetName(String assetName) {
				this.assetName = assetName;
			}

			public String getAmount() {
				return amount;
			}

			public void setAmount(String amount) {
				this.amount = amount;
			}

			public int getSort() {
				return sort;
			}

			public void setSort(int sort) {
				this.sort = sort;
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
