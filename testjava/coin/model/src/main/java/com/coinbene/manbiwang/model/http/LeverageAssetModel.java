package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-08-16
 * com.coinbene.manbiwang.model.http
 */
public class LeverageAssetModel extends BaseRes {

	/**
	 * data : {"quantity":"13.8499166666"}
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
		 * quantity : 13.8499166666
		 */

		private String quantity;

		public String getQuantity() {
			return quantity;
		}

		public void setQuantity(String quantity) {
			this.quantity = quantity;
		}
	}
}
