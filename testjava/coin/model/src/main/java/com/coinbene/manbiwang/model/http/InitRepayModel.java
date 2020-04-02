package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-08-19
 */
public class InitRepayModel extends BaseRes {


	/**
	 * data : {"actualQuantity":"4.00039751","availableQuantity":"4.00039751","symbol":"BTC/USDT","asset":"BTC","interestRate":"0.0002"}
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
		 * actualQuantity : 4.00039751
		 * availableQuantity : 4.00039751
		 * symbol : BTC/USDT
		 * asset : BTC
		 * interestRate : 0.0002
		 */

		private String actualQuantity;		//应还数量
		private String availableQuantity;	//最多可还，跟随用户当前可用资产而变化
		private String symbol;
		private String asset;
		private String interestRate;

		public String getActualQuantity() {
			return actualQuantity;
		}

		public void setActualQuantity(String actualQuantity) {
			this.actualQuantity = actualQuantity;
		}

		public String getAvailableQuantity() {
			return availableQuantity;
		}

		public void setAvailableQuantity(String availableQuantity) {
			this.availableQuantity = availableQuantity;
		}

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public String getAsset() {
			return asset;
		}

		public void setAsset(String asset) {
			this.asset = asset;
		}

		public String getInterestRate() {
			return interestRate;
		}

		public void setInterestRate(String interestRate) {
			this.interestRate = interestRate;
		}
	}
}
