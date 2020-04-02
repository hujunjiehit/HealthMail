package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * ding
 * 2019-08-19
 * com.coinbene.manbiwang.model.http
 */
public class BillingDetailsModel extends BaseRes {

	/**
	 * data : {"pageNum":1,"total":6,"pageSize":10,"pages":1,"list":[{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566184229000","bizType":"33","change":"-50","balance":"910.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566184170000","bizType":"33","change":"-50","balance":"960.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182848000","bizType":"32","change":"+1000","balance":"1010.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182327000","bizType":"33","change":"-199","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182323000","bizType":"32","change":"+199","balance":"209.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180581000","bizType":"33","change":"-65.1569","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180521000","bizType":"32","change":"+65.1569","balance":"75.907"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180445000","bizType":"33","change":"-65.1569","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180442000","bizType":"32","change":"+65.1569","balance":"75.907"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180323000","bizType":"33","change":"-65.1569","balance":"10.7501"}]}
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
		 * total : 6
		 * pageSize : 10
		 * pages : 1
		 * list : [{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566184229000","bizType":"33","change":"-50","balance":"910.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566184170000","bizType":"33","change":"-50","balance":"960.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182848000","bizType":"32","change":"+1000","balance":"1010.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182327000","bizType":"33","change":"-199","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566182323000","bizType":"32","change":"+199","balance":"209.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180581000","bizType":"33","change":"-65.1569","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180521000","bizType":"32","change":"+65.1569","balance":"75.907"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180445000","bizType":"33","change":"-65.1569","balance":"10.7501"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180442000","bizType":"32","change":"+65.1569","balance":"75.907"},{"symbol":"BTC/USDT","asset":"BTC","createTime":"1566180323000","bizType":"33","change":"-65.1569","balance":"10.7501"}]
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
			 * symbol : BTC/USDT
			 * asset : BTC
			 * createTime : 1566184229000
			 * bizType : 33
			 * change : -50
			 * balance : 910.7501
			 */

			private String symbol;
			private String asset;
			private String createTime;
			private String bizType;
			private String change;
			private String balance;

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

			public String getCreateTime() {
				return createTime;
			}

			public void setCreateTime(String createTime) {
				this.createTime = createTime;
			}

			public String getBizType() {
				return bizType;
			}

			public void setBizType(String bizType) {
				this.bizType = bizType;
			}

			public String getChange() {
				return change;
			}

			public void setChange(String change) {
				this.change = change;
			}

			public String getBalance() {
				return balance;
			}

			public void setBalance(String balance) {
				this.balance = balance;
			}
		}
	}
}
