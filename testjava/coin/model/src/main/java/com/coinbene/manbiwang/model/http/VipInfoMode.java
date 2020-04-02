package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-09-07
 * com.coinbene.manbiwang.model.http
 */
public class VipInfoMode extends BaseRes {
	/**
	 * data : {"level":1,"name":"VIP 1","discount":"1","makerFeeRateDiscount":"1","takerFeeRateDiscount":"1","updateTime":null}
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
		 * level : 1
		 * name : VIP 1
		 * discount : 1
		 * makerFeeRateDiscount : 1
		 * takerFeeRateDiscount : 1
		 * updateTime : null
		 */

		private String level;
		private String name;
		private String discount;
		private String makerFeeRateDiscount;
		private String takerFeeRateDiscount;
		private Object updateTime;

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDiscount() {
			return discount;
		}

		public void setDiscount(String discount) {
			this.discount = discount;
		}

		public String getMakerFeeRateDiscount() {
			return makerFeeRateDiscount;
		}

		public void setMakerFeeRateDiscount(String makerFeeRateDiscount) {
			this.makerFeeRateDiscount = makerFeeRateDiscount;
		}

		public String getTakerFeeRateDiscount() {
			return takerFeeRateDiscount;
		}

		public void setTakerFeeRateDiscount(String takerFeeRateDiscount) {
			this.takerFeeRateDiscount = takerFeeRateDiscount;
		}

		public Object getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(Object updateTime) {
			this.updateTime = updateTime;
		}
	}
}
