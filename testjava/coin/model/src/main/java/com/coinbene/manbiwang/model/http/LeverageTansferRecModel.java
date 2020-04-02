package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * ding
 * 2019-08-17
 * com.coinbene.manbiwang.model.http
 */
public class LeverageTansferRecModel extends BaseRes {

	/**
	 * data : {"pageNum":1,"total":43,"pageSize":10,"pages":5,"list":[{"time":"1565955258000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565955251000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565955114000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565955106000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565955010000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954937000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565954713000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954700000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565954662000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954656000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"}]}
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
		 * total : 43
		 * pageSize : 10
		 * pages : 5
		 * list : [{"time":"1565955258000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565955251000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565955114000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565955106000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565955010000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954937000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565954713000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954700000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"},{"time":"1565954662000","asset":"BTC","amount":"65.1569","from":"margin","to":"spot"},{"time":"1565954656000","asset":"BTC","amount":"65.1569","from":"spot","to":"margin"}]
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
			 * time : 1565955258000
			 * asset : BTC
			 * amount : 65.1569
			 * from : margin
			 * to : spot
			 */

			private String time;
			private String asset;
			private String amount;
			private String from;
			private String to;

			public String getTime() {
				return time;
			}

			public void setTime(String time) {
				this.time = time;
			}

			public String getAsset() {
				return asset;
			}

			public void setAsset(String asset) {
				this.asset = asset;
			}

			public String getAmount() {
				return amount;
			}

			public void setAmount(String amount) {
				this.amount = amount;
			}

			public String getFrom() {
				return from;
			}

			public void setFrom(String from) {
				this.from = from;
			}

			public String getTo() {
				return to;
			}

			public void setTo(String to) {
				this.to = to;
			}
		}
	}
}
