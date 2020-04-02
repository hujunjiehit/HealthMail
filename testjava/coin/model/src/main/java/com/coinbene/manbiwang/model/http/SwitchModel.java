package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class SwitchModel extends BaseRes {


	/**
	 * data : {"contract":1,"otc":1}
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
		 * contract : 1
		 * otc : 1
		 */

		private int contract;
		private int otc;
		private int option;

		private APP app;//展示的逻辑变化：主站，分站，tab和资产(记录)页面的展示与否分开处理。

		public int getContract() {
			return contract;
		}

		public void setContract(int contract) {
			this.contract = contract;
		}

		public int getOtc() {
			return otc;
		}

		public void setOtc(int otc) {
			this.otc = otc;
		}

		public int getOption() {
			return option;
		}

		public void setOption(int option) {
			this.option = option;
		}

		public APP getApp() {
			return app;
		}

		public void setApp(APP app) {
			this.app = app;
		}
	}

	public static class APP {
		private InnerBean contract;
		private InnerBean option;
		private InnerBean otc;
		private InnerBean game;
		private InnerBean sickle;


		public InnerBean getOtc() {
			return otc;
		}

		public void setOtc(InnerBean otc) {
			this.otc = otc;
		}

		public InnerBean getOption() {
			return option;
		}

		public void setOption(InnerBean option) {
			this.option = option;
		}

		public InnerBean getContract() {
			return contract;
		}

		public void setContract(InnerBean contract) {
			this.contract = contract;
		}

		public InnerBean getGame() {
			return game;
		}

		public void setGame(InnerBean game) {
			this.game = game;
		}

		public InnerBean getSickle() {
			return sickle;
		}

		public void setSickle(InnerBean sickle) {
			this.sickle = sickle;
		}
	}

	public static class InnerBean {
		private int asset;//0不展示，1展示
		private int tab;

		public int getAsset() {
			return asset;
		}

		public void setAsset(int asset) {
			this.asset = asset;
		}

		public int getTab() {
			return tab;
		}

		public void setTab(int tab) {
			this.tab = tab;
		}
	}
}
