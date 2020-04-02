package com.coinbene.manbiwang.spot.tradelayout.impl;

import android.content.Context;
import android.widget.TextView;

import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.tradelayout.AbsTradeStrategy;

public class FixedTradeStrategy extends AbsTradeStrategy {


	private WsMarketData marketData;

	@Override
	protected String calTotalPrice(String price, String unitPrice, String quantity) {
		return CalculationUtils.calTotalPrice(unitPrice
				, quantity
				, takeFee
				, pricePrecision
				, isBuy);
	}

	@Override
	protected String calQuantityFormSeekBar(String price, String unitPrice, double percent) {
		return CalculationUtils.calQuantityFromSeekBar(unitPrice,
				String.valueOf(percent),
				isBuy ? avlBuyBalance : avlSellBalance,
				takeFee,
				quantityPrecesion,
				isBuy);
	}

	@Override
	protected void calExpect(TextView mTvMarketFieldValue, String priceOrQuantity, String touchPrice) {
	}


	@Override
	protected void calPercentage(String totalAmount, String quantity, BubbleSeekBar mSeekBar, TextView tvPosition) {
		if (BigDecimalUtils.isEmptyOrZero(totalAmount) || (BigDecimalUtils.isEmptyOrZero(quantity))) {
			mSeekBar.setProgress(0);
			tvPosition.setText("0");
		}
		long percentage = isBuy ?
				BigDecimalUtils.divideHalfUp(totalAmount, avlBuyBalance, 2) :
				BigDecimalUtils.divideHalfUp(quantity, avlSellBalance, 2);
		//小于0是为了 让算出来的数据大于integer的最大值的时候会变成负数
		if (percentage > 100 || percentage < 0) {
			percentage = 100;

		}
		mSeekBar.setProgress(percentage);
		tvPosition.setText(String.valueOf(percentage));
	}

	@Override
	protected String calLocalPrice(String price) {
		if (base == null || quote == null) {
			return "";
		}
		if (marketData == null) {
			marketData = new WsMarketData();
		}
		marketData.setSymbol(base + quote);
		marketData.setLastPrice(price);
		return TradeUtils.getLocalPrice(marketData);
	}

	@Override
	protected void calQuantityFromTotalPrice(ClearEditText quantityEditText, String unitPrice, String amount) {
		quantityEditText.setText(CalculationUtils.calQuantityFromTotalPrice(unitPrice,
				amount,
				takeFee,
				quantityPrecesion,
				isBuy));
	}

	@Override
	public boolean checkParms(String touchPrice, String entrustPrice, String unitPrice, String quantity, String amount) {
		if (BigDecimalUtils.isEmptyOrZero(unitPrice)) {
			ToastUtil.show(R.string.please_input_coin_price_tip);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return false;
		}
//		//检查是否小于最小交易数量
//		if (BigDecimalUtils.isGreaterThan(minVolume, "0")) {
//			if (BigDecimalUtils.isLessThan(quantity, minVolume)) {
//				ToastUtil.show(String.format(CBRepository.getContext().getString(R.string.mincnt_tips), minVolume));
//				return false;
//			}
//		}
//		if (isBuy) {
//			//买入的时候检查是否大于交易百分比
//			if (!TextUtils.isEmpty(lastPrice) && !TextUtils.isEmpty(priceChangeScale)) {
//				if (BigDecimalUtils.isGreaterThan(unitPrice, BigDecimalUtils.multiplyToStr(lastPrice, BigDecimalUtils.add("1", priceChangeScale)))) {
//					ToastUtil.show(String.format(CBRepository.getContext().getString(R.string.max_input_price_tips), BigDecimalUtils.multiply(BigDecimalUtils.add("1",priceChangeScale), "100").intValue() + "%"));
//					return false;
//				}
//			}
//			//检查余额是否充足
////				if (BigDecimalUtils.isGreaterThan(mEtAmount.getText().toString(), buyBalance)) {
////					ToastUtil.show(R.string.more_than_ablenum_tip);
////					return;
////				}
//		} else {
//			//卖出的时候检查是否小于交易百分比
//			if (!TextUtils.isEmpty(lastPrice) && !TextUtils.isEmpty(priceChangeScale)) {
//				if (BigDecimalUtils.isLessThan(unitPrice, BigDecimalUtils.multiplyToStr(lastPrice, BigDecimalUtils.subtract("1", priceChangeScale)))) {
//					ToastUtil.show(String.format(CBRepository.getContext().getString(R.string.min_input_price_tips), BigDecimalUtils.multiply(BigDecimalUtils.subtract("1", priceChangeScale), "100").intValue() + "%"));
//					return false;
//				}
//			}
//			if (!TextUtils.isEmpty(lastPrice)) {
//				if (BigDecimalUtils.isGreaterThan(unitPrice, BigDecimalUtils.multiplyToStr(lastPrice, "10"))) {
//					ToastUtil.show(R.string.sell_max_input_price_tips);
//					return false;
//				}
//			}
////				//检查余额是否充足
////				if (BigDecimalUtils.isGreaterThan(mEtQuantity.getText().toString(), sellBalance)) {
////					ToastUtil.show(R.string.more_than_ablenum_tip);
////					return;
////				}
//		}


		return true;
	}

	@Override
	public void showDiscribe(Context context) {
		DialogManager.getMessageDialogBuilder(context)
				.setTitle(R.string.fixed_price)
				.setMessage(R.string.fixed_discribe)
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
