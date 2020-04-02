package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-09-11
 */
public class TransferResponse extends BaseRes {


	/**
	 * timezone : null
	 * data : true
	 */

	private boolean data;

	public boolean isData() {
		return data;
	}

	public void setData(boolean data) {
		this.data = data;
	}
}
