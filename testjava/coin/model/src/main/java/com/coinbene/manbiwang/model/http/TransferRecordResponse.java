package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-09-12
 */
public class TransferRecordResponse extends BaseRes {


	/**
	 * data : {"list":[{"amount":"string","asset":"string","formSymbol":"string","fromProduct":0,"id":0,"remark":"string","status":0,"toProduct":0,"toSubProduct":"string"}],"pageNum":0,"pageSize":0}
	 * timezone : 0
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
		 * list : [{"amount":"string","asset":"string","formSymbol":"string","fromProduct":0,"id":0,"remark":"string","status":0,"toProduct":0,"toSubProduct":"string"}]
		 * pageNum : 0
		 * pageSize : 0
		 */

		private int pageNum;
		private int pageSize;
		private List<ListBean> list;

		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNum) {
			this.pageNum = pageNum;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * amount : string
			 * asset : string
			 * formSymbol : string
			 * fromProduct : 0
			 * id : 0
			 * remark : string
			 * status : 0
			 * toProduct : 0
			 * toSubProduct : string
			 */

			private String amount;
			private String asset;
			private String fromSubProduct;
			private int fromProduct;
			private int id;
			private String remark;
			private int status;
			private int toProduct;
			private String toSubProduct;
			private long createTime;

			public String getAmount() {
				return amount;
			}

			public void setAmount(String amount) {
				this.amount = amount;
			}

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getFromSubProduct() {
				return fromSubProduct;
			}

			public void setFromSubProduct(String formSymbol) {
				this.fromSubProduct = formSymbol;
			}

			public int getFromProduct() {
				return fromProduct;
			}

			public void setFromProduct(int fromProduct) {
				this.fromProduct = fromProduct;
			}

			public int getId() {
				return id;
			}

			public void setId(int id) {
				this.id = id;
			}

			public String getRemark() {
				return remark;
			}

			public void setRemark(String remark) {
				this.remark = remark;
			}

			public int getStatus() {
				return status;
			}

			public void setStatus(int status) {
				this.status = status;
			}

			public int getToProduct() {
				return toProduct;
			}

			public void setToProduct(int toProduct) {
				this.toProduct = toProduct;
			}

			public String getToSubProduct() {
				return toSubProduct;
			}

			public void setToSubProduct(String toSubProduct) {
				this.toSubProduct = toSubProduct;
			}

			public long getCreateTime() {
				return createTime;
			}

			public void setCreateTime(long time) {
				this.createTime = time;
			}
		}
	}
}
