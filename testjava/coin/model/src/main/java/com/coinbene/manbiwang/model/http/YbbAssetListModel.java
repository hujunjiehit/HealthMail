package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-17
 */
public class YbbAssetListModel extends BaseRes {

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
		 */

		private String asset;

		public String getAsset() {
			return asset;
		}

		public void setAsset(String asset) {
			this.asset = asset;
		}
	}
}
