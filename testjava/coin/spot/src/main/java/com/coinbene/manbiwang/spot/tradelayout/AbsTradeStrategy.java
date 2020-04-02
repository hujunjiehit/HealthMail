package com.coinbene.manbiwang.spot.tradelayout;

import android.content.Context;
import android.widget.TextView;

import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;

public abstract class AbsTradeStrategy {


	protected String avlBuyBalance;
	protected String avlSellBalance;
	protected boolean isBuy;
	protected String takeFee;
	protected int pricePrecision;
	protected int quantityPrecesion;
	protected String base;
	protected String quote;
	protected String lastPrice;
	protected String buyPriceOne;
	protected String sellPriceOne;
	protected String rate;
	protected String minVolume;
	protected String priceChangeScale;



	protected abstract String calTotalPrice(String price,String unitPrice, String quantity);

	//
	protected abstract String calQuantityFormSeekBar(String price,String unitPrice, double percent);

	/**
	 * 计算市价下市场预估可买可卖
	 *
	 * @param priceOrQuantity 输入金额或者数量  买入的时候是金额  卖出是数量
	 */
	protected abstract void calExpect(TextView mTvMarketFieldValue,String priceOrQuantity, String touchPrice);


	/**
	 * 计算仓位百分比
	 */
	protected abstract void calPercentage(String totalAmount, String quantity, BubbleSeekBar mSeekBar, TextView tvPosition);

	/**
	 * 计算本地价格
	 *
	 * @param unitPrice
	 * @return
	 */
	protected abstract String calLocalPrice(String unitPrice);



	protected abstract void calQuantityFromTotalPrice(ClearEditText quantityEditText,String unitPrice, String amount);

	public abstract boolean checkParms(String touchPrice,String entrustPrice,String unitPrice,String quantity,String amount);

	public abstract void showDiscribe(Context context);



	void initStrategy(String takeFee, int pricePrecision, int quantityPrecesion, String base, String quote,String minVolume,String priceChangeScale) {
		this.takeFee = takeFee;
		this.pricePrecision = pricePrecision;
		this.quantityPrecesion = quantityPrecesion;
		this.base = base;
		this.quote = quote;
		this.minVolume = minVolume;
		this.priceChangeScale = priceChangeScale;
	}


	void setAvlBuyBalance(String avlBuyBalance) {
		this.avlBuyBalance = avlBuyBalance;
	}

	public void setAvlSellBalance(String avlSellBalance) {
		this.avlSellBalance = avlSellBalance;
	}

	void setBuy(boolean buy) {
		isBuy = buy;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public void setBuyPriceOne(String buyPriceOne) {
		this.buyPriceOne = buyPriceOne;
	}

	public void setSellPriceOne(String sellPriceOne) {
		this.sellPriceOne = sellPriceOne;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}




}
