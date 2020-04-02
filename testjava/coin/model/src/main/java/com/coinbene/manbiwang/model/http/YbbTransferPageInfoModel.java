package com.coinbene.manbiwang.model.http;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-10-17
 */
public class YbbTransferPageInfoModel extends BaseRes {


	/**
	 * data : {"availableBalance":"string","userMaxTotal":"string","userTotalLeft":"string"}
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
		 * availableBalance : string
		 * userMaxTotal : string
		 * userTotalLeft : string
		 */

		private String availableBalance;
		private String userMaxTotal;
		private String userTotalLeft;

		public String getAvailableBalance() {
			if (TextUtils.isEmpty(availableBalance)) {
				return "--";
			}
			return availableBalance;
		}

		public void setAvailableBalance(String availableBalance) {
			this.availableBalance = availableBalance;
		}

		public String getUserMaxTotal() {
			if (TextUtils.isEmpty(userMaxTotal)) {
				return "--";
			}
			return userMaxTotal;
		}

		public void setUserMaxTotal(String userMaxTotal) {
			this.userMaxTotal = userMaxTotal;
		}

		public String getUserTotalLeft() {
			if (TextUtils.isEmpty(userTotalLeft)) {
				return "--";
			}
			return userTotalLeft;
		}

		public void setUserTotalLeft(String userTotalLeft) {
			this.userTotalLeft = userTotalLeft;
		}
	}
}
