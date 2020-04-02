package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-08-18
 */
public class InitBorrowModel extends BaseRes {


	/**
	 * data : {"maxBorrow":"10","rate":"0.0002","minBorrow":"0.001"}
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
		 * maxBorrow : 10
		 * rate : 0.0002
		 * minBorrow : 0.001
		 */

		private String maxBorrow;
		private String rate;  //日利率，除以24得小时利率
		private String minBorrow;

		public String getMaxBorrow() {
			return maxBorrow;
		}

		public void setMaxBorrow(String maxBorrow) {
			this.maxBorrow = maxBorrow;
		}

		public String getRate() {
			return rate;
		}

		public void setRate(String rate) {
			this.rate = rate;
		}

		public String getMinBorrow() {
			return minBorrow;
		}

		public void setMinBorrow(String minBorrow) {
			this.minBorrow = minBorrow;
		}
	}
}
