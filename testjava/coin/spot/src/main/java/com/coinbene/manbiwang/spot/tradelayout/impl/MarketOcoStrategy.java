package com.coinbene.manbiwang.spot.tradelayout.impl;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.tradelayout.AbsTradeStrategy;

public class MarketOcoStrategy extends AbsTradeStrategy {
	@Override
	protected String calTotalPrice(String price, String unitPrice, String quantity) {
		return CalculationUtils.calTotalPrice(price
				, quantity
				, takeFee
				, pricePrecision
				, isBuy);
	}

	@Override
	protected String calQuantityFormSeekBar(String price, String unitPrice, double percent) {
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
	protected void calExpect(TextView mTvMarketFieldValue, String priceOrQuantity, String entrustPrice) {
//		String marketExpect = CalculationUtils.calMarketLossExpect(isBuy,
//				priceOrQuantity,
//				entrustPrice,
//				base,
//				quote,
//				pricePrecision);
//		if (!AppUtil.isMainThread()) {
//			mTvMarketFieldValue.post(() -> mTvMarketFieldValue.setText(marketExpect));
//		} else {
//			mTvMarketFieldValue.setText(marketExpect);
//		}
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
	public boolean checkParms(String price, String touchPrice, String unitPrice, String quantity, String amount) {
		if (BigDecimalUtils.isEmptyOrZero(price)) {
			ToastUtil.show(R.string.please_input_fix_price);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(touchPrice)) {
			ToastUtil.show(R.string.please_input_touch_price);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			if (isBuy)
				ToastUtil.show(R.string.please_buy_total_price);
			else
				ToastUtil.show(R.string.please_input_sell_number);
			return false;
		}
		return true;
	}

	@Override
	public void showDiscribe(Context context) {
		DialogManager.getMessageDialogBuilder(context)
				.setTitle(R.string.market_price_oco)
				.setMessage(R.string.market_oco_discribe)
				.setPositiveButton(R.string.read_more)
				.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						UIBusService.getInstance().openUri(context, UrlUtil.getZendeskSpotOrderTypeUrl(), null);
					}
				}).showDialog();
	}
}
