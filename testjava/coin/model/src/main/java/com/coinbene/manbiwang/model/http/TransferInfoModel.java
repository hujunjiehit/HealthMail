package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-09-10
 */
public class TransferInfoModel extends BaseRes {


	/**
	 * timezone :
	 * data : {"products":[{"id":2,"transferAvailable":3},{"id":3,"transferAvailable":3},{"id":4,"transferAvailable":0},{"id":5,"transferAvailable":3},{"id":6,"transferAvailable":1},{"id":9,"transferAvailable":3}],"marginProductId":5,"marginSymbols":["BTC/USDT"],"availableBalance":"99998.15620169"}
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
		 * products : [{"id":2,"transferAvailable":3},{"id":3,"transferAvailable":3},{"id":4,"transferAvailable":0},{"id":5,"transferAvailable":3},{"id":6,"transferAvailable":1},{"id":9,"transferAvailable":3}]
		 * marginProductId : 5
		 * marginSymbols : ["BTC/USDT"]
		 * availableBalance : 99998.15620169
		 */

		private int marginProductId;
		private String availableBalance;
		private List<ProductsBean> products;
		private List<String> marginSymbols;

		public int getMarginProductId() {
			return marginProductId;
		}

		public void setMarginProductId(int marginProductId) {
			this.marginProductId = marginProductId;
		}

		public String getAvailableBalance() {
			return availableBalance;
		}

		public void setAvailableBalance(String availableBalance) {
			this.availableBalance = availableBalance;
		}

		public List<ProductsBean> getProducts() {
			return products;
		}

		public void setProducts(List<ProductsBean> products) {
			this.products = products;
		}

		public List<String> getMarginSymbols() {
			return marginSymbols;
		}

		public void setMarginSymbols(List<String> marginSymbols) {
			this.marginSymbols = marginSymbols;
		}

		public static class ProductsBean {
			/**
			 * id : 2
			 * transferAvailable : 3
			 */

			private int id;
			private int transferAvailable;

			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public int getTransferAvailable() {
				return transferAvailable;
			}

			public void setTransferAvailable(int transferAvailable) {
				this.transferAvailable = transferAvailable;
			}
		}
	}
}
