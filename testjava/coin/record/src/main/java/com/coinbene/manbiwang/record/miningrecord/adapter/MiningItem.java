package com.coinbene.manbiwang.record.miningrecord.adapter;

import com.chad.library.adapter.base.entity.MultiItemEntity;

/**
 * Created by june
 * on 2019-08-06
 */
public class MiningItem implements MultiItemEntity {

	public static final int TYPE_SUMMARY = 1;
	public static final int TYPE_DETAIL = 2;

	private int itemType;

	private String frozenAmount;
	private String orderToken;
	private String paidAmount;
	private String sortToken;
	private String tokenAmount;
	private String tradeDayToken;
	private String tradeMinuteToken;
	private String yesterdayTokenAmount;

	private boolean showMiningRecords;

	//交易明细特有的字段
	private String date;

	public MiningItem(int itemType) {
		this.itemType = itemType;
	}

	@Override
	public int getItemType() {
		return itemType;
	}

	public String getFrozenAmount() {
		return frozenAmount;
	}

	public void setFrozenAmount(String frozenAmount) {
		this.frozenAmount = frozenAmount;
	}

	public String getOrderToken() {
		return orderToken;
	}

	public void setOrderToken(String orderToken) {
		this.orderToken = orderToken;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getSortToken() {
		return sortToken;
	}

	public void setSortToken(String sortToken) {
		this.sortToken = sortToken;
	}

	public String getTokenAmount() {
		return tokenAmount;
	}

	public void setTokenAmount(String tokenAmount) {
		this.tokenAmount = tokenAmount;
	}

	public String getTradeDayToken() {
		return tradeDayToken;
	}

	public void setTradeDayToken(String tradeDayToken) {
		this.tradeDayToken = tradeDayToken;
	}

	public String getTradeMinuteToken() {
		return tradeMinuteToken;
	}

	public void setTradeMinuteToken(String tradeMinuteToken) {
		this.tradeMinuteToken = tradeMinuteToken;
	}

	public String getYesterdayTokenAmount() {
		return yesterdayTokenAmount;
	}

	public void setYesterdayTokenAmount(String yesterdayTokenAmount) {
		this.yesterdayTokenAmount = yesterdayTokenAmount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public boolean isShowMiningRecords() {
		return showMiningRecords;
	}

	public void setShowMiningRecords(boolean showMiningRecords) {
		this.showMiningRecords = showMiningRecords;
	}
}
