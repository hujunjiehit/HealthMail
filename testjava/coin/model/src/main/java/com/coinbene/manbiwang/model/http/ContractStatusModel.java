package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-11-04
 * com.coinbene.manbiwang.model.http
 */
public class ContractStatusModel extends BaseRes {
	private DataBean data;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * flag : 0
		 */

		private String flag;

		public String getFlag() {
			return flag;
		}

		public void setFlag(String flag) {
			this.flag = flag;
		}
	}
}
