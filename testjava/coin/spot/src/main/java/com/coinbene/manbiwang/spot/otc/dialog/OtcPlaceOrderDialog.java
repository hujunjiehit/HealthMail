package com.coinbene.manbiwang.spot.otc.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.database.OtcPayTypeTable;
import com.coinbene.manbiwang.model.http.OtcAdListModel;
import com.coinbene.manbiwang.model.http.ReleaseAdInfo;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.OtcPayDialogAdapter;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("SetTextI18n")
public class OtcPlaceOrderDialog extends BottomSheetDialog {

	@BindView(R2.id.tv_buy_or_sell)
	TextView tvBuyOrSell;
	@BindView(R2.id.tv_unit_price)
	TextView tvUnitPrice;
	@BindView(R2.id.tv_amount)
	TextView tvAmount;
	@BindView(R2.id.tv_limit_price)
	TextView tvLimitPrice;
	@BindView(R2.id.tv_currency)
	TextView tvCurrency;
	@BindView(R2.id.tv_order_amount_asset)
	TextView tvOrderAmountAsset;
	@BindView(R2.id.tv_order_price_cny)
	TextView tvOrderPriceCny;

	@BindView(R2.id.view_amout_line)
	View viewAmoutLine;
	@BindView(R2.id.tv_price)
	TextView tvPrice;
	@BindView(R2.id.view_price_line)
	View viewPriceLine;
	@BindView(R2.id.view_line)
	View viewLine;
	@BindView(R2.id.et_usdt)
	EditText etUsdt;
	@BindView(R2.id.tv_all_usdt)
	TextView tvAllUsdt;
	@BindView(R2.id.tv_please_pay_type)
	TextView tvPleasePayType;
	@BindView(R2.id.tv_order_amount)
	TextView tvOrderAmount;
	@BindView(R2.id.tv_order_amount_value)
	TextView tvOrderAmountValue;
	@BindView(R2.id.tv_order_price)
	TextView tvOrderPrice;
	@BindView(R2.id.tv_order_price_value)
	TextView tvOrderPriceValue;

	@BindView(R2.id.tv_place_order)
	TextView tvPlaceOrder;
	@BindView(R2.id.tv_cancel)
	TextView tvCancel;
	@BindView(R2.id.rl_pay_type)
	RecyclerView rlPayType;

	private int curInputType = 0;//0按数量  1按金额
	private int firstLocation;
	private Activity mAcitivity;

	private OtcAdListModel.DataBean.ListBean adItem;
	private TextWatcher etTextWatcher;
	private OtcPayDialogAdapter otcPayAdapter;
	private PayOrderLisenter payOrderLisenter;

	public void setPayOrderLisenter(PayOrderLisenter payOrderLisenter) {
		this.payOrderLisenter = payOrderLisenter;
	}

	private static int getScreenHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public OtcPlaceOrderDialog(@NonNull Context context) {
		super(context, R.style.CoinBene_BottomSheet);
		mAcitivity = (Activity) context;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setContentView(R.layout.spot_dialog_otc_place_order);
		View content = getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
		content.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		BottomSheetBehavior.from(content).setState(BottomSheetBehavior.STATE_EXPANDED);
	}

	public OtcPlaceOrderDialog(@NonNull Context context, int theme) {
		super(context, theme);
	}

	protected OtcPlaceOrderDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ButterKnife.bind(this);


		tvAmount.setOnClickListener(v -> {
			if (curInputType == 0) {
				return;
			}
			initHeight();
			tvCurrency.setText(adItem.getAsset());
			if (adItem.getAdType() == 2) {
				etUsdt.setHint(R.string.please_input_number);
			} else {
				etUsdt.setHint(R.string.please_input_sell_number);
			}

			tvAmount.setTextColor(getContext().getResources().getColor(R.color.res_blue));
			tvPrice.setTextColor(getContext().getResources().getColor(R.color.res_textColor_2));
			viewAmoutLine.setVisibility(View.VISIBLE);
			viewPriceLine.setVisibility(View.INVISIBLE);
			curInputType = 0;
			initInputPrecision(6);
			etUsdt.setText("");


		});
		etUsdt.setOnFocusChangeListener((v, hasFocus) -> {
			initHeight();
		});
		tvPrice.setOnClickListener(v -> {
			if (curInputType == 1) {
				return;
			}
			initHeight();
			tvCurrency.setText(adItem.getCurrency());
			if (adItem.getAdType() == 2) {
				etUsdt.setHint(R.string.please_input_buy_cny);
			} else {
				etUsdt.setHint(R.string.please_input_sell_price);
			}
			tvAmount.setTextColor(getContext().getResources().getColor(R.color.res_textColor_2));
			tvPrice.setTextColor(getContext().getResources().getColor(R.color.res_blue));
			viewAmoutLine.setVisibility(View.INVISIBLE);
			viewPriceLine.setVisibility(View.VISIBLE);
			curInputType = 1;
			initInputPrecision(2);
			etUsdt.setText("");
		});

		tvAllUsdt.setOnClickListener(v -> {
			if (adItem != null) {
				if (adItem.getAdType() == 2) {//买入
					String[] strings = CalculationUtils.calculateOtcBuyAll(adItem.getStock(), adItem.getMinOrder(), adItem.getMaxOrder(), adItem.getPrice());
					if (strings != null && !TextUtils.isEmpty(strings[0]) && !TextUtils.isEmpty(strings[1])) {
						tvOrderAmountValue.setText(strings[0]);
						tvOrderAmountAsset.setText(adItem.getAsset());
						tvOrderPriceValue.setText(strings[1]);
						tvOrderPriceCny.setText(adItem.getCurrency());
						if (curInputType == 0) {
							etUsdt.setText(strings[0]);
						} else {
							etUsdt.setText(strings[1]);
						}
					}
				} else {
					getMaxTradeAccount();
				}
			}
		});
		initInputPrecision(6);
		tvCancel.setOnClickListener(v -> dismiss());
		tvPlaceOrder.setOnClickListener(v -> {
			if (BigDecimalUtils.isEmptyOrZero(tvOrderPriceValue.getText().toString())) {
				ToastUtil.show(R.string.cant_zero);
				return;
			}
			if (otcPayAdapter == null || otcPayAdapter.getSelectPosition() < 0) {
				if (adItem != null && adItem.getAdType() == 2) {
					ToastUtil.show(R.string.please_pay_order_type);
				} else {
					ToastUtil.show(R.string.please_choose_pay_type);
				}

				return;
			}

			if (payOrderLisenter != null) {
				payOrderLisenter.payOrder(adItem.getAdId(), tvOrderPriceValue.getText().toString(), adItem.getAdType(), otcPayAdapter.getSelectPayType());
			}

		});

	}

	@Override
	public void show() {
		super.show();
		initHeight();
	}

	private void initHeight() {
		View content = getWindow().getDecorView().findViewById(R.id.design_bottom_sheet);
		content.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
		BottomSheetBehavior.from(content).setState(BottomSheetBehavior.STATE_EXPANDED);
	}

	public void initInputPrecision(int number) {
		etUsdt.removeTextChangedListener(etTextWatcher);
		etTextWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(etUsdt, s, number);
			}

			@Override
			public void afterTextChanged(Editable s) {
				calculationInputChange(etUsdt.getText().toString());
//				etUsdt.setSelection(etUsdt.getText().toString().length());
			}
		};
		etUsdt.addTextChangedListener(etTextWatcher);
	}

	public void setPriceChange(OtcAdListModel.DataBean.ListBean adItem) {
		if (adItem == null) {
			return;

		}
		this.adItem = adItem;
		tvUnitPrice.setText(String.format("%s%s", adItem.getCurrencySymbol(), adItem.getPrice()));
		calculationInputChange(etUsdt.getText().toString());
	}


	public OtcAdListModel.DataBean.ListBean getAdItem() {
		return adItem;
	}

	private void calculationInputChange(String input) {

		if (BigDecimalUtils.isEmptyOrZero(input) || input.equals(".") || input.equals(",")) {
			tvOrderPriceValue.setText("0.00");
			tvOrderAmountValue.setText("0.000000");
			return;
		}

		//当输入是数量的时候
		if (curInputType == 0) {
			tvOrderAmountValue.setText(PrecisionUtils.appendZero(input, 6));
			if (!TextUtils.isEmpty(input) && !TextUtils.isEmpty(adItem.getPrice())) {
				String cny = PrecisionUtils.getRoundDown(BigDecimalUtils.multiplyToStr(input, adItem.getPrice()), Constants.cnyPrecision);
				tvOrderPriceValue.setText(cny);
			}
		} else { //当输入是金额的时候
			tvOrderPriceValue.setText(PrecisionUtils.appendZero(input, 2));
			if (!TextUtils.isEmpty(adItem.getPrice())) {
				String usdt = PrecisionUtils.getRoundDown(BigDecimalUtils.divideToStr(input, adItem.getPrice(), 6), Constants.usdtPrecision);
				tvOrderAmountValue.setText(usdt);
			}
		}
	}

	public void setData(OtcAdListModel.DataBean.ListBean data, List<OtcPayTypeTable> otcPayTypeTables) {
		tvAmount.performClick();
		etUsdt.setText("");
		this.adItem = data;
		if (data.getAdType() == 2) {
			etUsdt.setHint(R.string.please_input_number);
			tvPleasePayType.setText(R.string.please_pay_order_type);
			tvBuyOrSell.setText(String.format("%s  %s", getContext().getResources().getString(R.string.trade_buy), data.getAsset()));
		} else {
			etUsdt.setHint(R.string.please_input_sell_number);
			tvPleasePayType.setText(R.string.please_choose_pay_type);
			tvBuyOrSell.setText(String.format("%s  %s", getContext().getResources().getString(R.string.trade_sell), data.getAsset()));
		}

		tvUnitPrice.setText(String.format("%s%s", data.getCurrencySymbol(), data.getPrice()));
		tvLimitPrice.setText(String.format("%s%s-%s", data.getCurrencySymbol(), data.getMinOrder(), data.getMaxOrder()));
		tvCurrency.setText(data.getCurrency());
		tvOrderAmountAsset.setText(data.getAsset());
		tvOrderPriceCny.setText(data.getCurrency());
		tvCurrency.setText(data.getAsset());
		tvOrderPriceValue.setText("0.00");
		tvOrderAmountValue.setText("0.000000");
		initPayTypeData(data.getPayTypes(), otcPayTypeTables);

	}

	private void initPayTypeData(List<OtcAdListModel.DataBean.ListBean.PayType> payTypes, List<OtcPayTypeTable> datas) {
		GridLayoutManager linearLayoutManager = new GridLayoutManager(getContext(), 3);
		rlPayType.setLayoutManager(linearLayoutManager);

		otcPayAdapter = new OtcPayDialogAdapter();
		rlPayType.setAdapter(otcPayAdapter);

		if (datas != null && datas.size() > 0) {//卖出的时候
			otcPayAdapter.setLists(datas, -1);
		} else {//买入的时候
			List<OtcPayTypeTable> otcPayTypeTables = new ArrayList<>();
			if (payTypes != null && payTypes.size() > 0) {
				for (OtcAdListModel.DataBean.ListBean.PayType pay : payTypes) {
					OtcPayTypeTable table = new OtcPayTypeTable();
					table.payTypeId = pay.getPayTypeId();
					table.payTypeName = pay.getPayTypeName();
					table.payId = pay.getPayId();
					table.bankName = pay.getBankName();
					otcPayTypeTables.add(table);
				}
			}
			otcPayAdapter.setLists(otcPayTypeTables, -1);
		}
		otcPayAdapter.setOnItemClickLisenter((coinName, position) -> otcPayAdapter.setSelectPosition(position));
	}


	private void getMaxTradeAccount() {
		HttpParams params = new HttpParams();
		params.put("currency", adItem.getCurrency());
		params.put("accountType", 1);
		params.put("asset", adItem.getAsset());
		params.put("requestType", 2);
		OkGo.<ReleaseAdInfo>get(Constants.OTC_MAX_TRADE_ACCPUNT).tag(this).params(params).execute(new NewDialogJsonCallback<ReleaseAdInfo>(mAcitivity) {
			@Override
			public void onSuc(Response<ReleaseAdInfo> response) {
				if (response.body().getData() != null
						&& !TextUtils.isEmpty(response.body().getData().getAmount())) {

					String[] strings = CalculationUtils.calculateOtcSellAll(adItem.getStock(),
							adItem.getMinOrder(),
							adItem.getMaxOrder(),
							adItem.getPrice(),
							response.body().getData().getAmount());
					if (strings != null && !TextUtils.isEmpty(strings[0]) && !TextUtils.isEmpty(strings[1]) && isShowing()) {
						tvOrderAmountValue.setText(strings[0]);
						tvOrderAmountAsset.setText(adItem.getAsset());
						tvOrderPriceValue.setText(strings[1]);
						tvOrderPriceCny.setText(adItem.getCurrency());
						if (curInputType == 0) {
							etUsdt.setText(strings[0]);
						} else {
							etUsdt.setText(strings[1]);
						}
					}

				}
			}

			@Override
			public void onE(Response<ReleaseAdInfo> response) {
			}

		});
	}


	public interface PayOrderLisenter {

		void payOrder(String adId, String price, int buyorsell, int payType);

	}

}
