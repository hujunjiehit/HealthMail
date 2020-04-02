package com.coinbene.manbiwang.contract.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ContractTradeDialog extends Dialog implements View.OnClickListener {

	@BindView(R2.id.tv_lever)
	TextView tvLever;
	@BindView(R2.id.tv_lever_value)
	TextView tvLeverValue;
	@BindView(R2.id.tv_price_value)
	TextView tvPriceValue;
	@BindView(R2.id.tv_number_value)
	TextView tvNumberValue;
	@BindView(R2.id.tv_cancel)
	TextView tvCancer;
	@BindView(R2.id.tv_sure)
	TextView tvSure;
	@BindView(R2.id.tv_open_close)
	TextView tvOpenClose;
	@BindView(R2.id.tv_contrack_name)
	TextView tvContrackName;
	@BindView(R2.id.tv_price)
	TextView tv_price;
	@BindView(R2.id.tb_not_hint)
	ToggleButton tbNotHint;
	@BindView(R2.id.ll_not_hint)
	LinearLayout llNotHint;
	@BindView(R2.id.ll_profit)
	LinearLayout llProfit;
	@BindView(R2.id.ll_loss)
	LinearLayout llLoss;
	@BindView(R2.id.tv_profit_price_value)
	TextView tvProfitPriceValue;
	@BindView(R2.id.tv_loss_price_value)
	TextView tvLossPriceValue;
	private boolean isRedRase;
	private int direction;
	private OnClickListener onClickListener;

	private ContractPlaceOrderParmsModel contractPlaceOrderParmsModel;

	public ContractTradeDialog(Context context) {
		super(context);
		getWindow().setBackgroundDrawable(new BitmapDrawable());
	}

	public ContractTradeDialog(Context context, int themeResId) {
		super(context, themeResId);
	}

	protected ContractTradeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog_contrack_trade);
		setCanceledOnTouchOutside(true);
		ButterKnife.bind(this);

	}

	@Override
	protected void onStart() {
		super.onStart();
		tvCancer.setOnClickListener(this);
		tvSure.setOnClickListener(this);
		llNotHint.setOnClickListener(this);
		isRedRase = SwitchUtils.isRedRise();
		if (contractPlaceOrderParmsModel != null) {
			if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_OPEN_LONG) {
				tvOpenClose.setText(R.string.buy_open_long_zh);
			} else if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_OPEN_SHORT) {
				tvOpenClose.setText(R.string.sell_open_short_zh);
			} else if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_CLOSE_SHORT) {
				tvOpenClose.setText(R.string.buy_close_short);
			} else if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_CLOSE_LONG) {
				tvOpenClose.setText(R.string.sell_close_long);
			}

			tvContrackName.setText(String.format(getContext().getString(R.string.forever_no_delivery),
					TradeUtils.isUsdtContract(contractPlaceOrderParmsModel.getSymbol()) ?
							TradeUtils.getUsdtContractBase(contractPlaceOrderParmsModel.getSymbol()) :
							contractPlaceOrderParmsModel.getSymbol()
			));

			if (TextUtils.isEmpty(contractPlaceOrderParmsModel.getEstimatedValue())) {
				tvNumberValue.setText(String.format("%s%s", contractPlaceOrderParmsModel.getNumber(), contractPlaceOrderParmsModel.getUnit()));
			} else {
				String text = String.format("%s%s", contractPlaceOrderParmsModel.getNumber() + contractPlaceOrderParmsModel.getUnit(), contractPlaceOrderParmsModel.getEstimatedValue());
				tvNumberValue.setText(text);
			}

			//市价下不显示价格
			if (contractPlaceOrderParmsModel.getOrderType() == Constants.MARKET_PRICE && !contractPlaceOrderParmsModel.isQuickClose()) {//市价
				tvPriceValue.setVisibility(View.GONE);
				tv_price.setVisibility(View.GONE);
			} else {
				tv_price.setVisibility(View.VISIBLE);
				tvPriceValue.setVisibility(View.VISIBLE);
				if (contractPlaceOrderParmsModel.isQuickClose()) {
					tvPriceValue.setText(R.string.res_quick_close);
				} else {
					tvPriceValue.setText(contractPlaceOrderParmsModel.getPrice());
				}

			}


			//平仓下不显示杠杆
			if (isClosePosition(contractPlaceOrderParmsModel.getTradeType())) {
				tvLeverValue.setVisibility(View.GONE);
				tvLever.setVisibility(View.GONE);
			} else {
				tvLeverValue.setVisibility(View.VISIBLE);
				tvLever.setVisibility(View.VISIBLE);
				tvLeverValue.setText(String.format("%dX", contractPlaceOrderParmsModel.getLever()));
			}

			//高级委托或者平仓下不显示止盈止损
			if (isTradeHighLever(contractPlaceOrderParmsModel.getOrderType()) || isClosePosition(contractPlaceOrderParmsModel.getTradeType())) {
				llProfit.setVisibility(View.GONE);
				llLoss.setVisibility(View.GONE);
			} else {
				llProfit.setVisibility(View.VISIBLE);
				llLoss.setVisibility(View.VISIBLE);
				tvProfitPriceValue.setText(BigDecimalUtils.isEmptyOrZero(contractPlaceOrderParmsModel.getProfitPrice()) ? "--" : contractPlaceOrderParmsModel.getProfitPrice());
				tvLossPriceValue.setText(BigDecimalUtils.isEmptyOrZero(contractPlaceOrderParmsModel.getLossPrice()) ? "--" : contractPlaceOrderParmsModel.getLossPrice());
			}


			if (isRedRase) {
				if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_OPEN_LONG || contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_CLOSE_SHORT) {
					tvOpenClose.setTextColor(getContext().getResources().getColor(R.color.res_red));
				} else {
					tvOpenClose.setTextColor(getContext().getResources().getColor(R.color.res_green));
				}
			} else {
				if (contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_OPEN_LONG || contractPlaceOrderParmsModel.getTradeType() == Constants.TRADE_CLOSE_SHORT) {
					tvOpenClose.setTextColor(getContext().getResources().getColor(R.color.res_green));
				} else {
					tvOpenClose.setTextColor(getContext().getResources().getColor(R.color.res_red));
				}
			}

		}
	}


	private boolean isClosePosition(int tradeType) {
		return tradeType == Constants.TRADE_CLOSE_LONG || tradeType == Constants.TRADE_CLOSE_SHORT;
	}

	private boolean isTradeHighLever(int orderType) {
		return orderType == Constants.FIXED_PRICE_HIGH_LEVER;
	}


	public ContractTradeDialog setRedRase(boolean redRase) {
		isRedRase = redRase;
		return this;
	}

	public ContractTradeDialog setDirection(int direction) {
		this.direction = direction;
		return this;
	}


	public ContractTradeDialog buildPrams(ContractPlaceOrderParmsModel contractPlaceOrderParmsModel) {
		this.contractPlaceOrderParmsModel = contractPlaceOrderParmsModel;
		return this;
	}


	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.tv_cancel) {
			if (onClickListener != null) {
				onClickListener.clickCancel();
			}
			dismiss();
		} else if (id == R.id.tv_sure) {
			if (onClickListener != null) {
				onClickListener.placeOrder(contractPlaceOrderParmsModel);
			}
			dismiss();
			if (tbNotHint.isChecked()) {
				SpUtil.setContractSureDialogIsShow(true);
			}
		} else if (id == R.id.ll_not_hint) {
			if (tbNotHint.isChecked()) {
				tbNotHint.setChecked(false);
			} else {
				tbNotHint.setChecked(true);
			}
		}
	}


	public interface OnClickListener {
		void clickCancel();


		void placeOrder(ContractPlaceOrderParmsModel orderModel);
	}
}
