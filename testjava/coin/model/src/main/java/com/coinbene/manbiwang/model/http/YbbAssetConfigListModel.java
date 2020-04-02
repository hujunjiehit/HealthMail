package com.coinbene.manbiwang.model.http;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-17
 */
public class YbbAssetConfigListModel extends BaseRes {

	private List<DataBean> data;

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * asset : BTC
		 * minTransferIn : 1
		 * maxTransferOut : 200020
		 * lastSevenDayAnnual : 0.000000
		 * yesterdayDividend : 0.000000
		 * historyDividend : 0.000000
		 * status : 1
		 */

		private String asset;
		private String minTransferIn;
		private String maxTransferOut;
		private String lastSevenDayAnnual;
		private String yesterdayDividend;
		private String historyDividend;
		private String status;

		public String getAsset() {
			if (TextUtils.isEmpty(asset)) {
				return "--";
			}
			return asset;
		}

		public void setAsset(String asset) {
			this.asset = asset;
		}

		public String getMinTransferIn() {
			return minTransferIn;
		}

		public void setMinTransferIn(String minTransferIn) {
			this.minTransferIn = minTransferIn;
		}

		public String getMaxTransferOut() {
			return maxTransferOut;
		}

		public void setMaxTransferOut(String maxTransferOut) {
			this.maxTransferOut = maxTransferOut;
		}

		public String getLastSevenDayAnnual() {
			return lastSevenDayAnnual;
		}

		public void setLastSevenDayAnnual(String lastSevenDayAnnual) {
			this.lastSevenDayAnnual = lastSevenDayAnnual;
		}

		public String getYesterdayDividend() {
			if (TextUtils.isEmpty(yesterdayDividend)) {
				return "--";
			}
			return yesterdayDividend;
		}

		public void setYesterdayDividend(String yesterdayDividend) {
			this.yesterdayDividend = yesterdayDividend;
		}

		public String getHistoryDividend() {
			if (TextUtils.isEmpty(historyDividend)) {
				return "--";
			}
			return historyDividend;
		}

		public void setHistoryDividend(String historyDividend) {
			this.historyDividend = historyDividend;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}
	}
}
