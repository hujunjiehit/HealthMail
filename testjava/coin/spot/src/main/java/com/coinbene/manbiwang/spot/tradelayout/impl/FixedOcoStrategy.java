package com.coinbene.manbiwang.spot.tradelayout.impl;

import android.content.Context;
import android.widget.TextView;

import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.app.ClearEditText;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.tradelayout.AbsTradeStrategy;

public class FixedOcoStrategy extends AbsTradeStrategy {

	@Override
	protected String calTotalPrice(String price, String unitPrice, String quantity) {
		return CalculationUtils.calTotalPrice(String.valueOf(Math.max(Tools.parseDouble(price, 0), Tools.parseDouble(unitPrice, 0)))
				, quantity
				, takeFee
				, pricePrecision
				, isBuy);
	}

	@Override
	protected String calQuantityFormSeekBar(String price, String unitPrice, double percent) {
		return CalculationUtils.calQuantityFromSeekBar(String.valueOf(Math.max(Tools.parseDouble(price, 0), Tools.parseDouble(unitPrice, 0))),
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
		return null;
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
	public boolean checkParms(String price, String touchPrice, String unitPrice, String quantity, String amount) {
		if (BigDecimalUtils.isEmptyOrZero(price)) {
			ToastUtil.show(R.string.please_input_fix_price);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(touchPrice)) {
			ToastUtil.show(R.string.please_input_touch_price);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(unitPrice)) {
			ToastUtil.show(R.string.please_input_entrust_price);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(quantity)) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return false;
		}
		return true;
	}

	@Override
	public void showDiscribe(Context context) {
		DialogManager.getMessageDialogBuilder(context)
				.setTitle(R.string.fixed_price_oco)
				.setMessage(R.string.fixed_oco_discribe)
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
