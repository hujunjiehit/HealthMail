package com.coinbene.manbiwang.spot.otc;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.manbiwang.model.http.AdModel;
import com.coinbene.manbiwang.model.http.QueryPswStateModel;
import com.coinbene.manbiwang.model.http.ReleaseAdInfo;
import com.coinbene.manbiwang.model.http.ReleaseAdInfoModel;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.app.FixedRecyclerView;
import com.coinbene.common.widget.app.ListWithCancelSheetDialog;
import com.coinbene.common.widget.dialog.AddAdConfirmDialog;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.otc.adapter.AdBindInfoItemBinder;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author mxd
 * 商家添加订单
 */
@SuppressLint("SetTextI18n")
public class AddOrderActivity extends CoinbeneBaseActivity implements ListWithCancelSheetDialog.ListSheetDialogListener {
	@BindView(R2.id.iv_close)
	ImageView ivClose;
	@BindView(R2.id.menu_right_tv)
	TextView menuRightTv;
	@BindView(R2.id.search_img)
	ImageView searchImg;
	@BindView(R2.id.menu_line_view)
	View menuLineView;
	@BindView(R2.id.arrow_asset_img)
	ImageView arrowAssetImg;
	@BindView(R2.id.tv_price_method)
	TextView tvPriceMethod;
	@BindView(R2.id.iv_price_method)
	ImageView ivPriceMethod;
	@BindView(R2.id.rl_price_method)
	RelativeLayout rlPriceMethod;
	@BindView(R2.id.tv_price_type)
	TextView tvPriceType;
	@BindView(R2.id.et_price_type)
	EditText etPriceType;
	@BindView(R2.id.tv_price_range)
	TextView tvPriceRange;
	@BindView(R2.id.tv_market_reference_price)
	TextView tvMarketReferencePrice;
	@BindView(R2.id.tv_price_type_tips)
	TextView tvPriceTypeTips;
	@BindView(R2.id.tv_trade_price)
	TextView tvTradePrice;
	@BindView(R2.id.et_trade_price)
	EditText etTradePrice;
	@BindView(R2.id.ad_able_balance_tips)
	TextView adAbleBalanceTips;
	@BindView(R2.id.tv_price_currency)
	TextView tvPriceCurrency;

	private HashMap<String, String> marketMap;
	private String currentRangePrice;

	@Override
	protected void onStop() {
		super.onStop();
	}

	public static final int DIALOG_TAG_AD = 1;//买入卖出
	public static final int DIALOG_TAG_ASSET = 2;//币种
	public static final int DIALOG_TAG_CURRENCY = 3;//法币
	public static final int DIALOG_TAG_PRICE_MOTHOD = 4;//定价方式

	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;
	@BindView(R2.id.menu_title_tv)
	TextView menuTitleTv;
	@BindView(R2.id.ad_select_type)
	RelativeLayout typeSelect;
	@BindView(R2.id.ad_insert_type)
	TextView adType;
	@BindView(R2.id.ad_input_vol)
	EditText inputVol;
	@BindView(R2.id.ad_input_price)
	EditText inputPrice;
	@BindView(R2.id.ad_recharge)
	TextView recharge;
	@BindView(R2.id.ad_limit_min)
	EditText limitMin;
	@BindView(R2.id.ad_limit_max)
	EditText limitMax;
	@BindView(R2.id.ad_able_balance)
	TextView ableBalanceTv;
	@BindView(R2.id.ad_remark)
	EditText remark;
	@BindView(R2.id.ad_release_btn)
	Button releaseBtn;
	@BindView(R2.id.arrow_legal_tender_img)
	View arrowLegalTenderImg;
	@BindView(R2.id.ad_legal_tender_type)
	View adLegalTypeLayout;
	@BindView(R2.id.ad_select_asset_type)
	View adCurrencyTypeLayout;

	@BindView(R2.id.ad_able_balance_layout)
	RelativeLayout ableBalanceLayout;
	@BindView(R2.id.ad_asset_tv)
	TextView ad_asset_tv;
	@BindView(R2.id.ad_legal_tender_tv)
	TextView ad_legal_tender_tv;
	@BindView(R2.id.bind_info_listview)
	FixedRecyclerView recyclerView;

	@BindView(R2.id.checkbox_layout)
	LinearLayout checkBoxLayout;
	@BindView(R2.id.checkBox_imgview)
	ImageView checkBoxImgView;
	@BindView(R2.id.ad_vol_unit_tv)
	TextView ad_vol_unit_tv;
	@BindView(R2.id.left_amount_unit_tv)
	TextView left_amount_unit_tv;
	@BindView(R2.id.right_amount_unit_tv)
	TextView right_amount_unit_tv;
	@BindView(R2.id.payment_tip_tv)
	TextView payment_tip_tv;
	@BindView(R2.id.tv_trade_currency)
	TextView tvTradeCurrency;

	private String currentAmount = "0";//卖出时，最大的可卖数量
	private String currentPrice = "0";//卖出时，最大可卖的价格
	private String[] adTypeArray = null;
	private String[] assetArray = null;
	private String[] currentArray = null;
	private String[] priceMothodArray = null;
	private int dialogSelectedAdIndex = 1;//0,买，1卖出
	private int dialogSelectedAssetIndex = 0;//币种的下标
	private int dialogSelectedCurrencyIndex = 0;//法币的下标
	private int dialogSelectedPriceIndex = 0;//定价方式的下标

	private MultiTypeAdapter mContentAdapter;
	private boolean isGetTradeFirst = false;
	ListWithCancelSheetDialog sheetDialog;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, AddOrderActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.spot_activity_add_order;
	}

	@Override
	public void initView() {
		isGetTradeFirst = true;
		initDataAndView();
	}

	@Override
	public void setListener() {
		initListener();
	}

	@Override
	public void initData() {
		getReleaseAdInfo();
	}

	@Override
	public boolean needLock() {
		return true;
	}

	public void initDataAndView() {
		checkBoxImgView.setClickable(false);
		adTypeArray = new String[]{getString(R.string.trade_buy), getString(R.string.trade_sell)};
		priceMothodArray = new String[]{getString(R.string.fixed_amount), getString(R.string.from_floating_proportionally)};
		menuTitleTv.setText(R.string.insert_ad_text);
		inputVol.setHint(R.string.please_input_buy_number);
		limitMin.setText("200");
		initAdTypeDialogDialog();

		mContentAdapter = new MultiTypeAdapter();
		AdBindInfoItemBinder adBindInfoItemBinder = new AdBindInfoItemBinder();
		mContentAdapter.register(ReleaseAdInfoModel.OtcPaymentWay.class, adBindInfoItemBinder);
		recyclerView.setLayoutManager(new LinearLayoutManager(AddOrderActivity.this));
		recyclerView.setAdapter(mContentAdapter);
	}


	private void changeAdType() {
		etPriceType.setText("");
		if (dialogSelectedAdIndex == 0) {

			adType.setText(R.string.trade_buy);
			arrowLegalTenderImg.setVisibility(View.VISIBLE);
			adLegalTypeLayout.setOnClickListener(this);
			ableBalanceLayout.setVisibility(View.GONE);
			inputVol.setHint(R.string.please_input_buy_number);
			payment_tip_tv.setText(R.string.ad_payment_buy_label);
		} else {

			adType.setText(R.string.trade_sell);
			arrowLegalTenderImg.setVisibility(View.GONE);
			adLegalTypeLayout.setOnClickListener(null);
			ableBalanceLayout.setVisibility(View.VISIBLE);
			inputVol.setHint(R.string.please_input_sell_number);
			payment_tip_tv.setText(R.string.ad_payment_label);
			//类型选择卖出时，法币固定为商家kyc对应的法币
			if (sellCurrency != null) {
				ad_legal_tender_tv.setText(sellCurrency);
				left_amount_unit_tv.setText(sellCurrency);
				right_amount_unit_tv.setText(sellCurrency);
			} else {
				ad_legal_tender_tv.setText("--");
			}
		}
		if (assetArray != null) {
			String asset = assetArray[dialogSelectedAssetIndex];
			ad_vol_unit_tv.setText(asset);
		}
	}

	/**
	 * 全部的类型的选择框，都统一处理
	 */
	private void initAdTypeDialogDialog() {
		sheetDialog = new ListWithCancelSheetDialog(this);
		sheetDialog.setListSheetDialogListener(this);
	}

	/**
	 * 选择类型，买入，卖出
	 */
	private void showAdTypeDialog() {
		sheetDialog.setTag(DIALOG_TAG_AD);
		sheetDialog.setSelectedPosition(dialogSelectedAdIndex);
		sheetDialog.setDataAndNotifyData(adTypeArray);
		sheetDialog.show();
	}

	/**
	 * 选择币种
	 */
	private void showAssetDialog() {
		sheetDialog.setTag(DIALOG_TAG_ASSET);
		sheetDialog.setSelectedPosition(dialogSelectedAssetIndex);
		sheetDialog.setDataAndNotifyData(assetArray);
		sheetDialog.show();
	}

	/**
	 * 选择法币
	 */
	private void showCurrencyDialog() {
		sheetDialog.setTag(DIALOG_TAG_CURRENCY);
		sheetDialog.setSelectedPosition(dialogSelectedCurrencyIndex);
		sheetDialog.setDataAndNotifyData(currentArray);
		sheetDialog.show();
	}

	/**
	 * 选择定价方式
	 */
	private void showPriceMothodDialog() {
		sheetDialog.setTag(DIALOG_TAG_PRICE_MOTHOD);
		sheetDialog.setSelectedPosition(dialogSelectedPriceIndex);
		sheetDialog.setDataAndNotifyData(priceMothodArray);
		sheetDialog.show();
	}

	public void initListener() {
		checkBoxLayout.setOnClickListener(this);
		menuBack.setOnClickListener(this);
		typeSelect.setOnClickListener(this);
		recharge.setOnClickListener(this);
		releaseBtn.setOnClickListener(this);
		adCurrencyTypeLayout.setOnClickListener(this);
		rlPriceMethod.setOnClickListener(this);
		inputVol.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(inputVol, s, 6);
			}

			@Override
			public void afterTextChanged(Editable s) {


			}
		});
		etPriceType.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(etPriceType, s, 2);
				if (currentArray == null || currentArray.length == 0 || assetArray == null || assetArray.length == 0) {
					return;
				}

				if (!TextUtils.isEmpty(etPriceType.getText().toString()) && !etPriceType.getText().toString().equals("-") && !etPriceType.getText().toString().equals("-.") && !etPriceType.getText().toString().equals("+") && !etPriceType.getText().toString().equals("+.")) {
					if (dialogSelectedPriceIndex == 0) {//固定金额
						etTradePrice.setText(etPriceType.getText().toString());
						tvTradeCurrency.setText(String.format("%s/%s", currentArray[dialogSelectedCurrencyIndex], assetArray[dialogSelectedAssetIndex]));
					} else {//浮动比例
						if (!TextUtils.isEmpty(currentRangePrice)) {

							if (BigDecimalUtils.isLessThan(etPriceType.getText().toString(), "-10") || BigDecimalUtils.isGreaterThan(etPriceType.getText().toString(), "10")) {
								ToastUtil.show(R.string.price_range_tips);
							}
							String currentTradePrice;
							if (dialogSelectedAdIndex == 1) {//卖出时交易价格=参考价格（1+比例），精度为小数点后两位，向上取整
								currentTradePrice = BigDecimalUtils.multiplyUp(currentRangePrice, BigDecimalUtils.add("1", BigDecimalUtils.divideToStr(etPriceType.getText().toString(), "100", 4)), 2);
							} else {
								currentTradePrice = BigDecimalUtils.multiplyDown(currentRangePrice, BigDecimalUtils.subtract("1", BigDecimalUtils.divideToStr(etPriceType.getText().toString(), "100", 4)), 2);
							}
							etTradePrice.setText(currentTradePrice);
							tvTradeCurrency.setText(String.format("%s/%s", currentArray[dialogSelectedCurrencyIndex], assetArray[dialogSelectedAssetIndex]));

						}
					}
				} else {
					etTradePrice.setText("");
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		limitMax.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(limitMax, s, 2);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		limitMin.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(limitMin, s, 2);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


	}

	/**
	 * 弹窗选择后，回调接口
	 *
	 * @param tag
	 * @param position
	 */
	@Override
	public void onSelectedItem(Object tag, int position) {
		int tagInt = (int) tag;
		switch (tagInt) {
			case DIALOG_TAG_AD:
				if (position == dialogSelectedAdIndex) {
					return;
				}
				dialogSelectedAdIndex = position;
				changeAdType();
				//卖出，刷新资产信息
//                if (dialogSelectedAdIndex == 1) {
				getMaxTradeAccount();
//                }
				break;
			case DIALOG_TAG_ASSET:
				if (position == dialogSelectedAssetIndex) {
					return;
				}
				dialogSelectedAssetIndex = position;
				String asset = assetArray[dialogSelectedAssetIndex];
				ad_asset_tv.setText(asset);
				ad_vol_unit_tv.setText(asset);

				getMaxTradeAccount();
//                if (marketPrice != null) {
//                    getMarketPrice(currentArray[dialogSelectedCurrencyIndex]);
//                } else
//                    setPriceRange();
				break;
			case DIALOG_TAG_CURRENCY:
				if (position == dialogSelectedCurrencyIndex) {
					return;
				}
				dialogSelectedCurrencyIndex = position;
				String current = currentArray[dialogSelectedCurrencyIndex];
				ad_legal_tender_tv.setText(current);

				left_amount_unit_tv.setText(current);
				right_amount_unit_tv.setText(current);

				getMaxTradeAccount();
//                getMarketPrice(current);
				break;
			case DIALOG_TAG_PRICE_MOTHOD:
				if (position == dialogSelectedPriceIndex) {
					return;
				}
				dialogSelectedPriceIndex = position;
				String currentPriceType = priceMothodArray[dialogSelectedPriceIndex];
				tvPriceMethod.setText(currentPriceType);
				if (dialogSelectedPriceIndex == 0) {//固定金额
					tvPriceType.setText(R.string.fixed_amount);
					tvPriceRange.setVisibility(View.GONE);
					if (!TextUtils.isEmpty(currentPrice))
						etPriceType.setText(currentPrice);
					etPriceType.setHint(R.string.please_input_coin_price_tip);
					tvPriceCurrency.setText(currentArray[dialogSelectedCurrencyIndex]);
					tvPriceTypeTips.setText(R.string.price_type_tips);
				} else {//按比例浮动
					etPriceType.setHint(R.string.please_input_ratio);
					tvPriceType.setText(R.string.floating_range);
					tvPriceRange.setVisibility(View.VISIBLE);
					etPriceType.setText("");
					etTradePrice.setText(currentRangePrice);
					tvTradeCurrency.setText(String.format("%s/%s", currentArray[dialogSelectedCurrencyIndex], assetArray[dialogSelectedAssetIndex]));
					tvPriceCurrency.setText("%");
					tvPriceTypeTips.setText(R.string.price_type_tips2);
				}

				break;
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.menu_back) {
			finish();
		} else if (id == R.id.ad_select_type) {
			showAdTypeDialog();
		} else if (id == R.id.ad_recharge) {
			if (assetArray == null || assetArray.length == 0) {
				return;
			}
			String asset = assetArray[dialogSelectedAssetIndex];
			BalanceInfoTable infoTable = BalanceController.getInstance().findByAsset(asset);
			if (infoTable == null) {
				return;
			}
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (userTable == null) {
				return;
			}
			if (TextUtils.isEmpty(userTable.phone) && !String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage(getString(R.string.dialog_recharge_content));
				dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> {
					ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(this);
					dialog1.dismiss();
				});
				dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog12, which) -> dialog12.dismiss());
				dialog.show();
				return;
			} else if (TextUtils.isEmpty(userTable.phone) && !userTable.deposit) {
				//充值被限制
				AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setMessage(R.string.restricted_deposit);
				dialog.setPositiveButton(getString(R.string.btn_ok), (dialog1, which) -> dialog1.dismiss());
				dialog.show();
				return;
			}

			if (!infoTable.deposit) {
				String str = "";
				if (TextUtils.isEmpty(infoTable.banDepositReason)) {
					str = this.getResources().getString(R.string.cannot_deposit_tips);
					str = String.format(str, infoTable.asset);
				} else {
					str = infoTable.banDepositReason + getString(R.string.cannot_deposit_tips_new);
				}
				ToastUtil.show(str);
				return;
			}
			ARouter.getInstance().build(RouteHub.Balance.depositActivity).withString("model", asset).navigation(v.getContext());
		} else if (id == R.id.ad_release_btn) {
			adRelease();
		} else if (id == R.id.ad_legal_tender_type) {
			showCurrencyDialog();
		} else if (id == R.id.ad_select_asset_type) {
			showAssetDialog();
		} else if (id == R.id.rl_price_method) {
			showPriceMothodDialog();
		} else if (id == R.id.checkbox_layout) {
			boolean isSelected = checkBoxImgView.isSelected();
			checkBoxImgView.setSelected(!isSelected);
		}
	}


	/**
	 * @return 返回检验通过
	 */
	public boolean check() {


		if (paymentWayList == null || paymentWayList.size() == 0) {
			ToastUtil.show(R.string.ad_tips_pay_bind_info);
			return false;
		}
		if (BigDecimalUtils.isEmptyOrZero(inputVol.getText().toString())) {
			if (dialogSelectedAdIndex == 0) {
				ToastUtil.show(R.string.please_input_buy_number);
			} else {
				ToastUtil.show(R.string.please_input_sell_number);
			}
			return false;
		}


		if (BigDecimalUtils.isEmptyOrZero(etTradePrice.getText().toString())) {
			ToastUtil.show(R.string.please_input_coin_price_tip);
			return false;
		}

		if (BigDecimalUtils.isEmptyOrZero(limitMin.getText().toString())
				|| BigDecimalUtils.isEmptyOrZero(limitMax.getText().toString())) {
			ToastUtil.show(R.string.ad_input_limit);
			return false;
		}


		if (!TextUtils.isEmpty(remark.getText().toString()) && remark.getText().toString().length() > 500) {
			ToastUtil.show(R.string.otc_ad_memo_max_length);
			return false;
		}
		BigDecimal minLimit = new BigDecimal(limitMin.getText().toString());
		BigDecimal vol = new BigDecimal(inputVol.getText().toString());
//        if (minLimit.compareTo(new BigDecimal("200")) == -1) {//<200
//            ToastUtil.show("最小值必须大于等于200");
//            return false;
//        }
		if (dialogSelectedAdIndex == 1) {
			if (vol.compareTo(new BigDecimal(currentAmount)) == 1) {
				ToastUtil.show(R.string.otc_ad_sell_max_num);
				return false;
			}
		}
		BigDecimal price = new BigDecimal(etTradePrice.getText().toString());
		BigDecimal maxLimit = new BigDecimal(limitMax.getText().toString());

		if (vol.multiply(price).compareTo(minLimit) == -1) {
			ToastUtil.show(getString(R.string.ad_minlimit_tip) + BigDecimalUtils.multiplyDown(price.toPlainString(), inputVol.getText().toString(), 2));
			return false;
		}

		if (maxLimit.compareTo(minLimit) == -1) {
			ToastUtil.show(R.string.ad_maxlimit_tip);
			return false;
		}

		return true;
	}


	private String sellCurrency;

	/**
	 * 新增广告页面的初始化数据获取
	 */
	public void getReleaseAdInfo() {
		OkGo.<ReleaseAdInfoModel>post(Constants.OTC_GET_RELEASE_AD_INFO).tag(this).execute(new NewDialogJsonCallback<ReleaseAdInfoModel>(this) {
			@Override
			public void onSuc(Response<ReleaseAdInfoModel> response) {
				ReleaseAdInfoModel adInfoModel = response.body();
				if (adInfoModel == null || adInfoModel.getData() == null) {
					return;
				}
				ReleaseAdInfoModel.DataBean adInfoBean = adInfoModel.getData();

				initViewInfo(adInfoBean);
				//获取对应的可卖数量和价格
				getMaxTradeAccount();
			}

			@Override
			public void onE(Response<ReleaseAdInfoModel> response) {
			}
		});

	}


	private void setPriceRange(HashMap<String, String> marketPrice) {
		this.marketMap = marketPrice;
		if (marketMap == null || marketMap.size() == 0) {
			return;
		}
		currentRangePrice = marketMap.get(assetArray[dialogSelectedAssetIndex]);
		tvMarketReferencePrice.setText(String.format("%s%s/%s", String.format("%s: %s",
				getString(R.string.market_reference_price),
				marketMap.get(assetArray[dialogSelectedAssetIndex])),
				currentArray[dialogSelectedCurrencyIndex],
				assetArray[dialogSelectedAssetIndex]));
	}

	private List<ReleaseAdInfoModel.OtcPaymentWay> paymentWayList;

	/**
	 * 解析后端返回的数据,币种和法币
	 *
	 * @param adInfoBean
	 */
	private void initViewInfo(ReleaseAdInfoModel.DataBean adInfoBean) {
		sellCurrency = adInfoBean.getSellCurrency();
		if (TextUtils.isEmpty(sellCurrency)) {
			return;
		}

		List<String> assetList = adInfoBean.getAssetList();
		assetArray = new String[assetList.size()];
		assetList.toArray(assetArray);

		List<String> currencyList = adInfoBean.getCurrencyList();

		currentArray = new String[currencyList.size()];
		currencyList.toArray(currentArray);
		//选择买入时，法币默认为商家kyc对应的法币，可以选择法币种类
		for (int i = 0; i < currentArray.length; i++) {
			if (currentArray[i].equals(sellCurrency)) {
				dialogSelectedCurrencyIndex = i;
				break;
			}
		}


		String asset = assetArray[dialogSelectedAssetIndex];
		ad_asset_tv.setText(asset);
		String current = currentArray[dialogSelectedCurrencyIndex];
		ad_legal_tender_tv.setText(current);


		changeAdType();

//        getMarketPrice(current);

		paymentWayList = adInfoBean.getPaymentWayList();
		if (paymentWayList == null || paymentWayList.size() == 0) {
			//临时创建一个数据
			List<ReleaseAdInfoModel.OtcPaymentWay> tempWayList = new ArrayList<>();
			ReleaseAdInfoModel.OtcPaymentWay otcPaymentWay = new ReleaseAdInfoModel.OtcPaymentWay();
			otcPaymentWay.setType(AdBindInfoItemBinder.TYPE_BANK);
			otcPaymentWay.setBankName("--");
			otcPaymentWay.setBankAccount("--");
			tempWayList.add(otcPaymentWay);
			mContentAdapter.setItems(tempWayList);
		} else {
			mContentAdapter.setItems(paymentWayList);
		}
		mContentAdapter.notifyDataSetChanged();
	}

	/**
	 * 保存广告
	 */
	private void adRelease() {
		if (check()) {
			queryPswState();
		}
	}

	AddAdConfirmDialog.AdDialogClickListener adDialogClickListener = new AddAdConfirmDialog.AdDialogClickListener() {
		@Override
		public void onCancel() {
		}

		@Override
		public void onAdDialogOk(String identifyCode, String assetCode) {
			identifyCodeAd = identifyCode;
			assetCodeAd = assetCode;
			addReleaseHttp();
		}
	};

	/**
	 * 请求是否需要输入密码状态
	 */
	private void queryPswState() {
		HttpParams params = new HttpParams();
		OkGo.<QueryPswStateModel>post(Constants.OTC_QUERY_PSW_STATE).tag(this).params(params).execute(new NewDialogJsonCallback<QueryPswStateModel>(AddOrderActivity.this) {
			@Override
			public void onSuc(Response<QueryPswStateModel> response) {
				if (response.body().getData()) {
					AddAdConfirmDialog confirmDialog = new AddAdConfirmDialog(AddOrderActivity.this);
					confirmDialog.setClickLisenter(adDialogClickListener);
					confirmDialog.show();
				} else {
					addReleaseHttp();
				}
			}

			@Override
			public void onE(Response<QueryPswStateModel> response) {
			}

		});
	}

	private String identifyCodeAd, assetCodeAd;

	private void addReleaseHttp() {
		HttpParams httpParams = new HttpParams();

		httpParams.put("adType", dialogSelectedAdIndex == 0 ? 1 : 2);//1买入，2卖出

		String asset = assetArray[dialogSelectedAssetIndex];

		httpParams.put("asset", asset);// 数字货币

		String current = currentArray[dialogSelectedCurrencyIndex];

		httpParams.put("moneyType", current);// 法币

		httpParams.put("price", etTradePrice.getText().toString());

		httpParams.put("minOrder", limitMin.getText().toString().trim());

		httpParams.put("maxOrder", limitMax.getText().toString().trim());

		httpParams.put("stock", inputVol.getText().toString().trim());//数量

		httpParams.put("releaseType", 1);//0:没上架 1:已经上架 2:下架 3删除

		httpParams.put("adId", 0);//新增广告没有adId

		httpParams.put("adPriceMode", dialogSelectedPriceIndex);
		if (dialogSelectedPriceIndex == 1) {
			httpParams.put("floatingRatio", etPriceType.getText().toString());
		}
		if (!TextUtils.isEmpty(remark.getText().toString())) {
			httpParams.put("remark", remark.getText().toString());
		}
		httpParams.put("showReferenceNo", !checkBoxImgView.isSelected());//是否展示参考号,不展示传false

		if (!TextUtils.isEmpty(assetCodeAd)) {
			httpParams.put("assetPassword", MD5Util.MD5(assetCodeAd));//资金密码
		}
		if (!TextUtils.isEmpty(identifyCodeAd)) {
			httpParams.put("identifyCode", identifyCodeAd);//验证码
		}
		httpParams.put("accountType", 1);


		OkGo.<AdModel>post(Constants.AD_REALEASE).params(httpParams).tag(this).execute(new NewDialogJsonCallback<AdModel>(AddOrderActivity.this) {
			@Override
			public void onSuc(Response<AdModel> response) {
				ToastUtil.show(R.string.ad_order_release_success);
				finish();
			}

			@Override
			public void onE(Response<AdModel> response) {

			}
		});
	}

	/**
	 * 广告页请求可以出售最大数量和最大价格
	 */
	private void getMaxTradeAccount() {
		if (assetArray == null || currentArray == null) {
			return;
		}
		String asset = assetArray[dialogSelectedAssetIndex];
		String currency = currentArray[dialogSelectedCurrencyIndex];

		HttpParams params = new HttpParams();
		params.put("currency", currency);
//        params.put("accountType", 1);
		params.put("asset", asset);
		params.put("requestType", 1);//发广告传1   下单的时候传2
		OkGo.<ReleaseAdInfo>get(Constants.OTC_GET_PRICE_DATA).tag(this).params(params).execute(new NewDialogJsonCallback<ReleaseAdInfo>(AddOrderActivity.this) {
			@Override
			public void onSuc(Response<ReleaseAdInfo> response) {
				if (response.body().getData() != null) {
					currentAmount = response.body().getData().getAmount();
					if (dialogSelectedAdIndex == 1) {
						ableBalanceTv.setText(TextUtils.isEmpty(currentAmount) ? "--" : currentAmount);
						inputVol.setText(TextUtils.isEmpty(currentAmount) ? "" : currentAmount);
					} else {
						ableBalanceTv.setText("--");//切换到买入时，隐藏
						inputVol.setText("");//切换到买入时，数量清空
					}
					setPriceRange(response.body().getData().getMarketPrice());
					currentPrice = response.body().getData().getMaxPrice();
//                    inputPrice.setText(TextUtils.isEmpty(currentPrice) ? "" : currentPrice);

					etTradePrice.setText(TextUtils.isEmpty(currentPrice) ? "" : currentPrice);
					if (dialogSelectedPriceIndex == 0) {
						etPriceType.setText(TextUtils.isEmpty(currentPrice) ? "" : currentPrice);
						tvPriceCurrency.setText(currentArray[dialogSelectedCurrencyIndex]);
					}
					// 只设置一次
					if (isGetTradeFirst && !TextUtils.isEmpty(currentAmount) && !TextUtils.isEmpty(currentPrice)) {
						isGetTradeFirst = false;
						limitMax.setText(BigDecimalUtils.multiplyDown(currentAmount, currentPrice, 2));
					}
				}
			}

			@Override
			public void onE(Response<ReleaseAdInfo> response) {
			}

		});
	}
}