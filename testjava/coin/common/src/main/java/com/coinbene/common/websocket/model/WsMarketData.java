package com.coinbene.common.websocket.model;

import android.text.TextUtils;

import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.database.TagsTable;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.TradeUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2020-01-13
 */
public class WsMarketData implements Serializable, Cloneable {

	/**
	 * symbol : LTCBRL
	 * lastPrice : 229.31
	 * bestAskPrice : 0.00
	 * bestBidPrice : 0.00
	 * high24h : 195.16
	 * open24h : 195.16
	 * low24h : 195.16
	 * volume24h : 0.00
	 * timestamp : 1577964818641
	 */

	private String symbol;
	private String lastPrice;
	private String bestAskPrice;
	private String bestBidPrice;
	private String high24h;
	private String open24h;
	private String openPrice;
	private String low24h;
	private String volume24h;
	private long timestamp;

	//合约用到的字段
	private String openInterest;    //持仓量
	private String markPrice;        //标记价
	private String indexPrice;        //指数价
	private String fundingRate;        //资金费率

	//新增字段
	private String tradePairName;
	private String quoteAsset;        //分母资产
	private String baseAsset;        //分子资产
	private String localPrice;
	private String upsAndDowns;    //涨跌幅
	private String changeValue;    //涨跌值
	private String Mp;    //标记价
	private String F8;    //资金费率

	private String chineseName;
	private String englishName;
	private int precision;
	private int sort;
	public boolean isOptional;//是否是自选
	public boolean isMargin;
	public String leverage;

	public boolean isLatest;
	public boolean isHot;

	private List<TagsTable> tags;
	private boolean tagRefresh;

	private ContractType contractType;
	private int riseType;

	public boolean isMargin() {
		return isMargin;
	}

	public void setMargin(boolean margin) {
		isMargin = margin;
	}

	public String getLeverage() {
		return leverage;
	}

	public void setLeverage(String leverage) {
		this.leverage = leverage;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		if (this.symbol != null && !this.symbol.equals(symbol)) {
			//symbol变化，清空tradePairName
			setTradePairName("");
			setBaseAsset("");
			setQuoteAsset("");
		}
		this.symbol = symbol;
	}

	public String getLastPrice() {
		return lastPrice == null ? "" : lastPrice;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public String getBestAskPrice() {
		return bestAskPrice;
	}

	public void setBestAskPrice(String bestAskPrice) {
		this.bestAskPrice = bestAskPrice;
	}

	public String getBestBidPrice() {
		return bestBidPrice;
	}

	public void setBestBidPrice(String bestBidPrice) {
		this.bestBidPrice = bestBidPrice;
	}

	public String getHigh24h() {
		return high24h;
	}

	public void setHigh24h(String high24h) {
		this.high24h = high24h;
	}

	public String getOpen24h() {
		return open24h;
	}

	public void setOpen24h(String open24h) {
		this.open24h = open24h;
	}

	public String getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(String openPrice) {
		this.openPrice = openPrice;
	}

	public String getLow24h() {
		return low24h;
	}

	public void setLow24h(String low24h) {
		this.low24h = low24h;
	}

	public String getVolume24h() {
		return volume24h == null ? "" : volume24h;
	}

	public void setVolume24h(String volume24h) {
		this.volume24h = volume24h;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTradePairName() {
		if (TextUtils.isEmpty(tradePairName)) {
			TradePairInfoTable table = TradePairInfoController.getInstance().queryDataById(symbol);
			setTradePairName(table == null ? "" : table.tradePairName);
		}
		return tradePairName;
	}

	public void setTradePairName(String tradePairName) {
		this.tradePairName = tradePairName;
	}

	public String getLocalPrice() {
		return TradeUtils.getLocalPrice(this);
	}

	public void setLocalPrice(String localPrice) {
		this.localPrice = localPrice;
	}

	public String getUpsAndDowns() {
		if (TextUtils.isEmpty(upsAndDowns)) {
			setUpsAndDowns(TradeUtils.getUpsAndDowns(this));
		}
		return upsAndDowns;
	}

	public void setUpsAndDowns(String upsAndDowns) {
		this.upsAndDowns = upsAndDowns;
	}

	public String getEnglishName() {
		return englishName;
	}

	public void setEnglishName(String englishName) {
		this.englishName = englishName;
	}

	public String getChineseName() {
		return chineseName;
	}

	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean optional) {
		isOptional = optional;
	}

	public String getChangeValue() {
		if (TextUtils.isEmpty(changeValue)) {
			setChangeValue(TradeUtils.getChangeValue(this));
		}
		return changeValue;
	}

	public void setChangeValue(String changeValue) {
		this.changeValue = changeValue;
	}

	public String getMp() {
		return Mp;
	}

	public void setMp(String mp) {
		Mp = mp;
	}

	public String getF8() {
		return F8;
	}

	public void setF8(String f8) {
		F8 = f8;
	}

	public boolean isLatest() {
		return isLatest;
	}

	public void setLatest(boolean latest) {
		isLatest = latest;
	}

	public boolean isHot() {
		return isHot;
	}

	public void setHot(boolean hot) {
		isHot = hot;
	}

	public String getQuoteAsset() {
		switch (getContractType()) {
			case NONE:
				if (TextUtils.isEmpty(getTradePairName()) || !getTradePairName().contains("/")) {
					return "";
				}
				if (TextUtils.isEmpty(quoteAsset)) {
					setQuoteAsset(getTradePairName().split("/")[1]);
				}
				break;
			case USDT:
				if (TextUtils.isEmpty(quoteAsset)) {
					ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
					if (table != null) {
						setBaseAsset(table.baseAsset);
						setQuoteAsset(table.quoteAsset);
					}
				}
				break;
			case BTC:
				if (TextUtils.isEmpty(quoteAsset)) {
					ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(symbol);
					if (table != null) {
						setBaseAsset(table.baseAsset);
						setQuoteAsset(table.quoteAsset);
					}
				}
				break;
		}

		return quoteAsset;
	}

	public String getBaseAsset() {
		switch (getContractType()) {
			case NONE:
				if (TextUtils.isEmpty(getTradePairName()) || !getTradePairName().contains("/")) {
					return "";
				}
				if (TextUtils.isEmpty(baseAsset)) {
					setBaseAsset(getTradePairName().split("/")[0]);
				}
				break;
			case USDT:
				if (TextUtils.isEmpty(baseAsset)) {
					ContractUsdtInfoTable table = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
					if (table != null) {
						setBaseAsset(table.baseAsset);
						setQuoteAsset(table.quoteAsset);
					}
				}
				break;
			case BTC:
				if (TextUtils.isEmpty(baseAsset)) {
					ContractInfoTable table = ContractInfoController.getInstance().queryContrackByName(symbol);
					if (table != null) {
						setBaseAsset(table.baseAsset);
						setQuoteAsset(table.quoteAsset);
					}
				}
				break;
		}
		return baseAsset;
	}

	public void setQuoteAsset(String quoteAsset) {
		this.quoteAsset = quoteAsset;
	}

	public void setBaseAsset(String baseAsset) {
		this.baseAsset = baseAsset;
	}

	public String getOpenInterest() {
		return openInterest;
	}

	public void setOpenInterest(String openInterest) {
		this.openInterest = openInterest;
	}

	public String getMarkPrice() {
		return markPrice;
	}

	public void setMarkPrice(String markPrice) {
		this.markPrice = markPrice;
	}

	public String getIndexPrice() {
		return indexPrice;
	}

	public void setIndexPrice(String indexPrice) {
		this.indexPrice = indexPrice;
	}

	public String getFundingRate() {
		return fundingRate;
	}

	public void setFundingRate(String fundingRate) {
		this.fundingRate = fundingRate;
	}

	public List<TagsTable> getTags() {
		return tags == null ? new ArrayList<>() : tags;
	}

	public void setTags(List<TagsTable> tags) {
		this.tags = tags;
		this.tagRefresh = true;
	}

	public boolean isTagRefresh() {
		return tagRefresh;
	}

	public void setTagRefresh(boolean tagRefresh) {
		this.tagRefresh = tagRefresh;
	}

	public String getTagString() {
		StringBuilder sb = new StringBuilder();
		for (TagsTable tag : getTags()) {
			sb.append(tag.iconUrl);
		}
		return sb.toString();
	}

	public ContractType getContractType() {
		if(contractType == null){
			contractType = ContractType.NONE;
		}
		return contractType;
	}

	public int getRiseType() {
		return riseType;
	}

	public void setRiseType(int riseType) {
		this.riseType = riseType;
	}

	public void setContractType(ContractType contractType) {
		this.contractType = contractType;
	}

	@Override
	public Object clone() {
		WsMarketData dataBean = null;
		try {
			dataBean = (WsMarketData) super.clone();
		} catch (Exception CloneNotSupportedException) {

		}
		return dataBean;
	}
}
