package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;

/**
 * Created by june
 * on 2019-07-19
 */
public class FeeRateInfoModel extends BaseRes {


	/**
	 * data : {"amount":"string","level":"string","levelId":0,"makerFeeRate":"string","neededAmount":"string","nextLevel":"string","takerFeeRate":"string","useDefault":0}
	 * timezone : 0
	 */

	private DataBean data;
	private int timezone;

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public int getTimezone() {
		return timezone;
	}

	public void setTimezone(int timezone) {
		this.timezone = timezone;
	}

	public static class DataBean implements Serializable {
		/**
		 * amount : string
		 * level : string
		 * levelId : 0
		 * makerFeeRate : string
		 * neededAmount : string
		 * nextLevel : string
		 * takerFeeRate : string
		 * useDefault : 0
		 */

		private String amount;
		private String level;
		private int levelId;
		private String makerFeeRate;
		private String neededAmount;
		private String nextLevel;
		private String takerFeeRate;
		private int useDefault;

		public String getAmount() {
			return amount;
		}

		public void setAmount(String amount) {
			this.amount = amount;
		}

		public String getLevel() {
			return level;
		}

		public void setLevel(String level) {
			this.level = level;
		}

		public int getLevelId() {
			return levelId;
		}

		public void setLevelId(int levelId) {
			this.levelId = levelId;
		}

		public String getMakerFeeRate() {
			return makerFeeRate;
		}

		public void setMakerFeeRate(String makerFeeRate) {
			this.makerFeeRate = makerFeeRate;
		}

		public String getNeededAmount() {
			return neededAmount;
		}

		public void setNeededAmount(String neededAmount) {
			this.neededAmount = neededAmount;
		}

		public String getNextLevel() {
			return nextLevel;
		}

		public void setNextLevel(String nextLevel) {
			this.nextLevel = nextLevel;
		}

		public String getTakerFeeRate() {
			return takerFeeRate;
		}

		public void setTakerFeeRate(String takerFeeRate) {
			this.takerFeeRate = takerFeeRate;
		}

		public int getUseDefault() {
			return useDefault;
		}

		public void setUseDefault(int useDefault) {
			this.useDefault = useDefault;
		}
	}
}
