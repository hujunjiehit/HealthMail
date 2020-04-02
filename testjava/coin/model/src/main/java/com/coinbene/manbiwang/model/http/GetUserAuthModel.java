package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2020-02-16
 */
public class GetUserAuthModel extends BaseRes {

	/**
	 * data : {"bindPhone":1,"bindGoogle":0}
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
		 * bindPhone : 1
		 * bindGoogle : 0
		 */

		private int bindPhone;
		private int bindGoogle;

		public int getBindPhone() {
			return bindPhone;
		}

		public void setBindPhone(int bindPhone) {
			this.bindPhone = bindPhone;
		}

		public int getBindGoogle() {
			return bindGoogle;
		}

		public void setBindGoogle(int bindGoogle) {
			this.bindGoogle = bindGoogle;
		}
	}
}
