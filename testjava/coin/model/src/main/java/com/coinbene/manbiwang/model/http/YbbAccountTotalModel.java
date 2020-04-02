package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by june
 * on 2019-10-17
 */
public class YbbAccountTotalModel extends BaseRes {

	/**
	 * data : {"currentValuation":"0.00000000","profitValuation":"0.00000000","currencySymbol":"￥","localPreestimate":"0.00000000","preestimateType":"CNY","profitTime":"64519","yesterdayValuation":"0"}
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
		 * currentValuation : 0.00000000
		 * profitValuation : 0.00000000
		 * currencySymbol : ￥
		 * localPreestimate : 0.00000000
		 * preestimateType : CNY
		 * profitTime : 64519
		 * yesterdayValuation : 0
		 */

		private String currentValuation;
		private String profitValuation;
		private String currencySymbol;
		private String localPreestimate;
		private String preestimateType;
		private String profitTime;
		private String yesterdayValuation;

		public String getCurrentValuation() {
			return currentValuation;
		}

		public void setCurrentValuation(String currentValuation) {
			this.currentValuation = currentValuation;
		}

		public String getProfitValuation() {
			return profitValuation;
		}

		public void setProfitValuation(String profitValuation) {
			this.profitValuation = profitValuation;
		}

		public String getCurrencySymbol() {
			return currencySymbol;
		}

		public void setCurrencySymbol(String currencySymbol) {
			this.currencySymbol = currencySymbol;
		}

		public String getLocalPreestimate() {
			return localPreestimate;
		}

		public void setLocalPreestimate(String localPreestimate) {
			this.localPreestimate = localPreestimate;
		}

		public String getPreestimateType() {
			return preestimateType;
		}

		public void setPreestimateType(String preestimateType) {
			this.preestimateType = preestimateType;
		}

		public String getProfitTime() {
			return profitTime;
		}

		public void setProfitTime(String profitTime) {
			this.profitTime = profitTime;
		}

		public String getYesterdayValuation() {
			return yesterdayValuation;
		}

		public void setYesterdayValuation(String yesterdayValuation) {
			this.yesterdayValuation = yesterdayValuation;
		}
	}
}
