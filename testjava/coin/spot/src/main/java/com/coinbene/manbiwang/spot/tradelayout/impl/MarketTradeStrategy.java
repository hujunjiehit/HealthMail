package com.coinbene.manbiwang.spot.tradelayout.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.tradelayout.AbsTradeStrategy;

public class MarketTradeStrategy extends AbsTradeStrategy {
	@Override
	protected String calTotalPrice(String price,String unitPrice, String quantity) {
		return null;
	}

	@Override
	protected String calQuantityFormSeekBar(String price,String unitPrice, double percent) {
		String p =  String.valueOf(percent);
		if(BigDecimalUtils.isEmptyOrZero(p)){
			return "";
		}


		if (isBuy) {//买的时候计算
			if (!TextUtils.isEmpty(avlBuyBalance)) {
				return BigDecimalUtils.multiplyDown(avlBuyBalance, String.valueOf(percent), pricePrecision);
			}
		} else {//卖的时候计算
			if (!TextUtils.isEmpty(avlSellBalance)) {
				return BigDecimalUtils.multiplyDown(avlSellBalance, String.valueOf(percent), quantityPrecesion);
			}
		}
		return "";
	}

	@Override
	protected void calExpect(TextView mTvMarketFieldValue,String priceOrQuantity, String entrustPrice) {
		String marketExpect  =  CalculationUtils.calMarketExpect(isBuy,
				priceOrQuantity, lastPrice, buyPriceOne,sellPriceOne, pricePrecision, quantityPrecesion, takeFee, base, quote);
		if (!AppUtil.isMainThread()) {
			mTvMarketFieldValue.post(() -> mTvMarketFieldValue.setText(marketExpect));
		} else {
			mTvMarketFieldValue.setText(marketExpect);
		}
	}

	@Override
	protected void calPercentage(String totalAmount, String quantity, BubbleSeekBar mSeekBar, TextView tvPosition) {
		if (isBuy) {//买的时候计算
			if (!TextUtils.isEmpty(avlBuyBalance)) {
				int progress = BigDecimalUtils.divideHalfUp(quantity, avlBuyBalance, 2);
				if (progress > 100 || progress < 0) {
					progress = 100;
				}

				mSeekBar.setProgress(progress);
				tvPosition.setText(String.valueOf(progress));
			}
		} else {//卖的时候计算
			if (!TextUtils.isEmpty(avlSellBalance)) {
				int progress = BigDecimalUtils.divideHalfUp(quantity, avlSellBalance, 2);
				if (progress > 100 || progress < 0) {
					progress = 100;
				}
				mSeekBar.setProgress(progress);
				tvPosition.setText(String.valueOf(progress));
			}
		}
	}


	@Override
	protected String calLocalPrice(String price) {
		return null;
	}

	@Override
	protected void calQuantityFromTotalPrice(ClearEditText quantityEditText, String unitPrice, String amount) {

	}

	@Override
	public boolean checkParms(String touchPrice, String entrustPrice, String unitPrice, String quantity, String amount) {
//		if (BigDecimalUtils.isEmptyOrZero(unitPrice)) {
//				ToastUtil.show(R.string.please_input_coin_price_tip);
//			return false;
//		}


		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			if (isBuy)
				ToastUtil.show(R.string.please_buy_total_price);
			else
				ToastUtil.show(R.string.please_input_sell_number);
			return false;
		}
//		if (isBuy) {
//			if (BigDecimalUtils.isLessThan(avlBuyBalance, quantity)) {
//				ToastUtil.show(R.string.more_than_ablenum_tip);
//				return false;
//			}
//		} else {
//			if (BigDecimalUtils.isLessThan(avlSellBalance, quantity)) {
//				ToastUtil.show(R.string.more_than_ablenum_tip);
//				return false;
//			}
//		}


		return true;
	}

	@Override
	public void showDiscribe(Context context) {
		DialogManager.getMessageDialogBuilder(context)
				.setTitle(R.string.market_price)
				.setMessage(context.getString(R.string.market_discribe))
				.setPositiveButton(R.string.btn_confirm)
				.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {

					}
				}).showDialog();
	}
}
