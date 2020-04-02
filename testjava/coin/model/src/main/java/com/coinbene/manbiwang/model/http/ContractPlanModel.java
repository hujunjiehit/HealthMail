package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class ContractPlanModel extends BaseRes {


	/**
	 * message : null
	 * timezone : null
	 * data : {"pageNum":1,"pageSize":10,"total":-1,"pages":1,"list":[{"id":7,"symbol":"BTCUSDT","direction":"closeLong","planType":"stopLoss","orderType":"planLimit","triggerPrice":"2888.0","orderPrice":"12.0","quantity":2,"status":"trigger","createTime":1555659394000}]}
	 */

	private Object timezone;
	private DataBean data;


	public Object getTimezone() {
		return timezone;
	}

	public void setTimezone(Object timezone) {
		this.timezone = timezone;
	}

	public DataBean getData() {
		return data;
	}

	public void setData(DataBean data) {
		this.data = data;
	}

	public static class DataBean {
		/**
		 * pageNum : 1
		 * pageSize : 10
		 * total : -1
		 * pages : 1
		 * list : [{"id":7,"symbol":"BTCUSDT","direction":"closeLong","planType":"stopLoss","orderType":"planLimit","triggerPrice":"2888.0","orderPrice":"12.0","quantity":2,"status":"trigger","createTime":1555659394000}]
		 */

		private int pageNum;
		private int pageSize;
		private int total;
		private int pages;
		private List<ListBean> list;
		private int waitTriggerCount;

		public int getWaitTriggerCount() {
			return waitTriggerCount;
		}

		public void setWaitTriggerCount(int waitTriggerCount) {
			this.waitTriggerCount = waitTriggerCount;
		}

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

		public int getTotal() {
			return total;
		}

		public void setTotal(int total) {
			this.total = total;
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
			 * id : 7
			 * symbol : BTCUSDT
			 * direction : closeLong
			 * planType : stopLoss
			 * orderType : planLimit
			 * triggerPrice : 2888.0
			 * orderPrice : 12.0
			 * quantity : 2
			 * status : trigger
			 * createTime : 1555659394000
			 */

			private String planId;
			private String symbol;
			private String direction;
			private String planType;
			private String orderType;
			private String triggerPrice;
			private String orderPrice;
			private String quantity;
			private String status;
			private long createTime;
			private long updateTime;
			private String triggerQuantity;

			public String getTriggerQuantity() {
				return triggerQuantity;
			}

			public void setTriggerQuantity(String triggerQuantity) {
				this.triggerQuantity = triggerQuantity;
			}

			public String getId() {
				return planId;
			}

			public String getPlanId() {
				return planId;
			}

			public void setPlanId(String planId) {
				this.planId = planId;
			}


			public long getUpdateTime() {
				return updateTime;
			}

			public void setUpdateTime(long updateTime) {
				this.updateTime = updateTime;
			}

			public void setId(String id) {
				this.planId = id;
			}

			public String getSymbol() {
				return symbol;
			}

			public void setSymbol(String symbol) {
				this.symbol = symbol;
			}

			public String getDirection() {
				return direction;
			}

			public void setDirection(String direction) {
				this.direction = direction;
			}

			public String getPlanType() {
				return planType;
			}

			public void setPlanType(String planType) {
				this.planType = planType;
			}

			public String getOrderType() {
				return orderType;
			}

			public void setOrderType(String orderType) {
				this.orderType = orderType;
			}

			public String getTriggerPrice() {
				return triggerPrice;
			}

			public void setTriggerPrice(String triggerPrice) {
				this.triggerPrice = triggerPrice;
			}

			public String getOrderPrice() {
				return orderPrice;
			}

			public void setOrderPrice(String orderPrice) {
				this.orderPrice = orderPrice;
			}

			public String getQuantity() {
				return quantity;
			}

			public void setQuantity(String quantity) {
				this.quantity = quantity;
			}

			public String getStatus() {
				return status;
			}

			public void setStatus(String status) {
				this.status = status;
			}

			public long getCreateTime() {
				return createTime;
			}

			public void setCreateTime(long createTime) {
				this.createTime = createTime;
			}
		}
	}
}
