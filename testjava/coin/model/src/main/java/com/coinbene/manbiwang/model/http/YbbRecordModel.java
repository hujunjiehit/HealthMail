package com.coinbene.manbiwang.model.http;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by june
 * on 2019-10-18
 */
public class YbbRecordModel extends BaseRes {

	/**
	 * data : {"pageNum":1,"total":2,"pageSize":10,"pages":1,"list":[{"asset":"ETH","bizType":3,"change":"+23.00000000","balance":"23.00000000","createTime":"1571370881000"},{"asset":"BTC","bizType":3,"change":"+250.00000000","balance":"250.00000000","createTime":"1571370855000"}]}
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
		 * pageNum : 1
		 * total : 2
		 * pageSize : 10
		 * pages : 1
		 * list : [{"asset":"ETH","bizType":3,"change":"+23.00000000","balance":"23.00000000","createTime":"1571370881000"},{"asset":"BTC","bizType":3,"change":"+250.00000000","balance":"250.00000000","createTime":"1571370855000"}]
		 */

		private int pageNum;
		private int total;
		private int pageSize;
		private int pages;
		private List<ListBean> list;

		public int getPageNum() {
			return pageNum;
		}

		public void setPageNum(int pageNum) {
			this.pageNum = pageNum;
		}

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
		}

		public int getPageSize() {
			return pageSize;
		}

		public void setPageSize(int pageSize) {
			this.pageSize = pageSize;
		}

		public int getPages() {
			return pages;
		}

		public void setPages(int pages) {
			this.pages = pages;
		}

		public List<ListBean> getList() {
			return list;
		}

		public void setList(List<ListBean> list) {
			this.list = list;
		}

		public static class ListBean {
			/**
			 * asset : ETH
			 * bizType : 3
			 * change : +23.00000000
			 * balance : 23.00000000
			 * createTime : 1571370881000
			 */

			private String asset;
			private int bizType;
			private String change;
			private String balance;
			private String createTime;

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public int getBizType() {
				return bizType;
			}

			public void setBizType(int bizType) {
				this.bizType = bizType;
			}

			public String getChange() {
				if (TextUtils.isEmpty(change)) {
					return "--";
				}
				return change;
			}

			public void setChange(String change) {
				this.change = change;
			}

			public String getBalance() {
				if (TextUtils.isEmpty(balance)) {
					return "--";
				}
				return balance;
			}

			public void setBalance(String balance) {
				this.balance = balance;
			}

			public String getCreateTime() {
				return createTime;
			}

			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}
		}
	}
}
