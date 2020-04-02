package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-12-27
 */
public class LeverageLimitRes extends BaseRes {

	/**
	 * timezone : null
	 * data : [{"symbol":"BTCUSDT","levelMin":1,"levelMax":14,"quantityLimit":10000000},
	 * {"symbol":"BTCUSDT","levelMin":15,"levelMax":15,"quantityLimit":9500000},
	 * {"symbol":"BTCUSDT","levelMin":16,"levelMax":16,"quantityLimit":8500000},
	 * {"symbol":"BTCUSDT","levelMin":17,"levelMax":18,"quantityLimit":7500000},
	 * {"symbol":"BTCUSDT","levelMin":19,"levelMax":20,"quantityLimit":6500000},
	 * {"symbol":"BTCUSDT","levelMin":21,"levelMax":22,"quantityLimit":5500000},
	 * {"symbol":"BTCUSDT","levelMin":23,"levelMax":25,"quantityLimit":4500000},
	 * {"symbol":"BTCUSDT","levelMin":26,"levelMax":28,"quantityLimit":3500000},
	 * {"symbol":"BTCUSDT","levelMin":29,"levelMax":33,"quantityLimit":2500000},
	 * {"symbol":"BTCUSDT","levelMin":34,"levelMax":40,"quantityLimit":1500000},
	 * {"symbol":"BTCUSDT","levelMin":41,"levelMax":50,"quantityLimit":500000}]
	 */

	private String timezone;
	private List<DataBean> data;

	public Object getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<DataBean> getData() {
		return data;
	}

	public void setData(List<DataBean> data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * symbol : BTCUSDT
		 * levelMin : 1
		 * levelMax : 14
		 * quantityLimit : 10000000
		 */

		private String symbol;
		private int levelMin;
		private int levelMax;
		private int quantityLimit;

		public String getSymbol() {
			return symbol;
		}

		public void setSymbol(String symbol) {
			this.symbol = symbol;
		}

		public int getLevelMin() {
			return levelMin;
		}

		public void setLevelMin(int levelMin) {
			this.levelMin = levelMin;
		}

		public int getLevelMax() {
			return levelMax;
		}

		public void setLevelMax(int levelMax) {
			this.levelMax = levelMax;
		}

		public int getQuantityLimit() {
			return quantityLimit;
		}

		public void setQuantityLimit(int quantityLimit) {
			this.quantityLimit = quantityLimit;
		}
	}
}
