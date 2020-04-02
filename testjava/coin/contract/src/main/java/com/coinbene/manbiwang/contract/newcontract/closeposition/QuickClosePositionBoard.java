package com.coinbene.manbiwang.contract.newcontract.closeposition;

import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.dialog.ContractTradeDialog;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.http.ContractPlaceOrderModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.math.BigDecimal;

import butterknife.BindView;

/**
 * Created by june
 * on 2020-03-13
 *
 * 快速平仓弹窗面板
 */
public class QuickClosePositionBoard extends BaseBoard {

	@BindView(R2.id.tv_title)
	TextView mTvTitle;
	@BindView(R2.id.icon_describe)
	ImageView mIvDescribe;
	@BindView(R2.id.input_num)
	PlusSubInputView mInputNum;
	@BindView(R2.id.tv_available_number)
	TextView mTvAvailableNumber;
	@BindView(R2.id.seek_bar)
	BubbleSeekBar mSeekBar;
	@BindView(R2.id.tv_zero)
	TextView mTvZero;
	@BindView(R2.id.tv_cur_hold)
	TextView mTvCurHold;
	@BindView(R2.id.tv_hundred)
	TextView mTvHundred;
	@BindView(R2.id.layout_position)
	LinearLayout mLayoutPosition;
	@BindView(R2.id.btn_cancel)
	QMUIRoundButton mBtnCancel;
	@BindView(R2.id.btn_ok)
	QMUIRoundButton mBtnOk;

	private String mSymbol;
	private String mSide;
	private String mAvailableQuantity;

	private ContractType mContractType;

	private ContractUsdtInfoTable mUsdtTable;
	private ContractInfoTable mBtcTable;

	private BaseTextWatcher textWatcher;
	private boolean isChangeSeekBar;
	private ContractPlaceOrderParmsModel contractPlaceOrderModel;
	private ContractTradeDialog tradeDialog;

	public QuickClosePositionBoard(@NonNull Context context) {
		super(context);
	}

	@Override
	public int initLayout() {
		return R.layout.contract_close_position;
	}

	@Override
	public void initDialog() {
		setCancelable(false);
		setCanceledOnTouchOutside(true);
		getDelegate().findViewById(R.id.design_bottom_sheet).setBackgroundColor(
				getContext().getResources().getColor(R.color.transparent));

		mBtnCancel.setOnClickListener(v -> dismiss());

		mBtnOk.setOnClickListener(v -> checkParams());

		textWatcher = new BaseTextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (TradeUtils.isUsdtContract(mSymbol)) {
					if (TradeUtils.getContractUsdtPrecision(mUsdtTable) == 0) {
						mInputNum.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
					} else {
						mInputNum.getmEditText().setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
						PrecisionUtils.setPrecision(mInputNum.getmEditText(), s, TradeUtils.getContractUsdtPrecision(mUsdtTable));
					}
				}else {
					mInputNum.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
				}
				if (!isChangeSeekBar) {
					changeSeekBar();
				}
			}
		};

		mInputNum.addTextChangedListener(textWatcher);
//		mInputNum.setPlusSubWirth(QMUIDisplayHelper.dpToPx(44));
		mSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
			@Override
			public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
				mTvCurHold.setText(String.format("%s%d%%", getContext().getString(R.string.cur_position), progress));
				if (fromUser) {
					isChangeSeekBar = true;
					changeInputText(progress);
					isChangeSeekBar = false;
				}
			}

			@Override
			public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

			}

			@Override
			public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

			}
		});

		mIvDescribe.setOnClickListener(v ->
			DialogManager.getMessageDialogBuilder(v.getContext())
					.setMessage(v.getContext().getString(R.string.res_quick_close_tips))
					.showDialog()
		);
	}

	private void changeSeekBar() {
		if (TextUtils.isEmpty(mAvailableQuantity)) {
			mSeekBar.setProgress(0);
			return;
		}

		int persent = BigDecimalUtils.divideHalfUp(mInputNum.getText(), mAvailableQuantity, 2);
		if (persent < 100) {
			mSeekBar.setProgress(persent);
		} else {
			mSeekBar.setProgress(100);
		}
	}

	private void changeInputText(int progress) {
		if (TextUtils.isEmpty(mAvailableQuantity)) {
			return;
		}
		BigDecimal bdAvl = new BigDecimal(mAvailableQuantity);
		BigDecimal bdPg = new BigDecimal(progress);
		BigDecimal onHundred = new BigDecimal(100);
		BigDecimal multiply = bdPg.divide(onHundred).multiply(bdAvl);
		if (TradeUtils.isUsdtContract(mSymbol)) {
			mInputNum.setText(multiply.setScale(TradeUtils.getContractUsdtPrecision(mUsdtTable), BigDecimal.ROUND_DOWN).toPlainString());
		} else {
			mInputNum.setText(multiply.setScale(0, BigDecimal.ROUND_DOWN).toPlainString());
		}
		mInputNum.setSelection(mInputNum.getText().length());
	}

	public void initParams(String symbol, String side, String availableQuantity) {
		this.mSymbol = symbol;
		this.mSide = side;
		this.mAvailableQuantity = availableQuantity;

		mSeekBar.setProgress(100);
		mContractType = TradeUtils.getContractType(mSymbol);
		switch (mContractType) {
			case USDT:
				mUsdtTable = ContractUsdtInfoController.getInstance().queryContrackByName(mSymbol);
				mInputNum.setHint(String.format("%s(%s)", getContext().getString(R.string.input_num_hint), TradeUtils.getContractUsdtUnit(mUsdtTable)));
				mInputNum.setMinPriceChange(TradeUtils.getContractUsdtMultiplier(mUsdtTable));
				mAvailableQuantity = TradeUtils.getContractUsdtUnitValue(mAvailableQuantity, mUsdtTable);
				mInputNum.setMax(mAvailableQuantity);
				mInputNum.setText(mAvailableQuantity);

				mTvAvailableNumber.setText(String.format("%s%s(%s)",
						getString(R.string.avl_close),
						mAvailableQuantity,
						TradeUtils.getContractUsdtUnit(mUsdtTable)
					));
				break;
			case BTC:
				mBtcTable = ContractInfoController.getInstance().queryContrackByName(mSymbol);
				mInputNum.setHint(String.format("%s(%s)",getContext().getString(R.string.input_num_hint), getContext().getString(R.string.number)));
				mInputNum.setMinPriceChange(TradeUtils.getContractBtcMultiplier(mBtcTable));
				mInputNum.setMax(mAvailableQuantity);
				mInputNum.setText(mAvailableQuantity);
				mTvAvailableNumber.setText(String.format("%s%s(%s)",
						getString(R.string.avl_close),
						mAvailableQuantity,
						getString(R.string.number)
						));
				break;
		}

		mInputNum.getmEditText().setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				KeyboardUtils.hideKeyboard(mInputNum);
				return true;
			}
			return false;
		});

		int greenColor = getContext().getResources().getColor(R.color.res_green);
		int redColor = getContext().getResources().getColor(R.color.res_red);
		if (mSide.equals("long")) {
			//平多
			mSeekBar.setThumbColor(SwitchUtils.isRedRise() ? greenColor : redColor);
			mSeekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? greenColor : redColor);
		} else {
			//平空
			mSeekBar.setThumbColor(SwitchUtils.isRedRise() ? redColor : greenColor);
			mSeekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? redColor : greenColor);
		}
 	}

	private String getString(@StringRes int strId) {
		return getContext().getResources().getString(strId);
	}

	private void checkParams() {
		if (BigDecimalUtils.isEmptyOrZero(mInputNum.getText())) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return;
		}
		if (!TextUtils.isEmpty(mAvailableQuantity) && BigDecimalUtils.isGreaterThan(mInputNum.getText(), mAvailableQuantity)) {
			ToastUtil.show(R.string.close_amount_insufficient_tip);
			return;
		}

		if (TradeUtils.isUsdtContract(mSymbol)) {
			if (!TradeUtils.checkContractUsdtInput(mInputNum.getText(), mUsdtTable)) {
				ToastUtil.show(String.format("%s%s", getContext().getString(R.string.usdt_contract_min_place_order), mUsdtTable.multiplier));
				return;
			}
		}

		if (contractPlaceOrderModel == null) {
			contractPlaceOrderModel = new ContractPlaceOrderParmsModel();
		}
		if (mContractType == ContractType.USDT) {
			contractPlaceOrderModel.setNumber(TradeUtils.getContractUsdtDialogShowValue(mInputNum.getText(), mUsdtTable));
		} else {
			contractPlaceOrderModel.setNumber(mInputNum.getText());
		}
		contractPlaceOrderModel.setTradeType(mSide.equals("long") ? Constants.TRADE_CLOSE_LONG : Constants.TRADE_CLOSE_SHORT);
		contractPlaceOrderModel.setSymbol(mSymbol);
		contractPlaceOrderModel.setUnit(mContractType == ContractType.USDT ? TradeUtils.getContractUsdtUnit(mSymbol):getContext().getString(R.string.number));
		contractPlaceOrderModel.setOrderType(Constants.MARKET_PRICE);
		contractPlaceOrderModel.setIsQuickClose(true);
		if (mContractType == ContractType.USDT) {
			if (SpUtil.getContractUsdtUnitSwitch() == 0) {
				if (!BigDecimalUtils.isEmptyOrZero(mInputNum.getText())){
					contractPlaceOrderModel.setEstimatedValue(String.format("=%s %s", TradeUtils.getContractUsdtEstimatedValue(mInputNum.getText(), mUsdtTable), mUsdtTable.baseAsset));
				} else {
					contractPlaceOrderModel.setEstimatedValue("");
				}
			} else {
				contractPlaceOrderModel.setEstimatedValue("");
			}
		} else {
			if (!BigDecimalUtils.isEmptyOrZero(mInputNum.getText()) ) {
				if ("BTCUSDT".equals(mSymbol)) {
					contractPlaceOrderModel.setEstimatedValue(
							String.format("=%s %s", CalculationUtils.getBtcContractEstimated(mBtcTable,
									mSymbol,
									mInputNum.getText()), "USDT")
					);
				} else {
					contractPlaceOrderModel.setEstimatedValue(
							String.format("≈%s %s", CalculationUtils.getBtcContractEstimated(mBtcTable,
									mSymbol,
									mInputNum.getText()), "BTC")
					);
				}

			} else {
				contractPlaceOrderModel.setEstimatedValue("");
			}
		}
		if (SpUtil.isContractSureDialogNotShow()) {
			closeOrder(contractPlaceOrderModel);
			return;
		}

		if (tradeDialog == null) {
			tradeDialog = new ContractTradeDialog(getContext());
			tradeDialog.setOnClickListener(new ContractTradeDialog.OnClickListener() {
				@Override
				public void clickCancel() {

				}

				@Override
				public void placeOrder(ContractPlaceOrderParmsModel orderModel) {
					closeOrder(contractPlaceOrderModel);
				}

			});
		}
		tradeDialog.buildPrams(contractPlaceOrderModel).show();
	}

	/**
	 * 闪电平仓 -> 市价平仓
	 * @param parmsModel
	 */
	private void closeOrder(ContractPlaceOrderParmsModel parmsModel) {
		HttpParams params = new HttpParams();
		params.put("symbol", parmsModel.getSymbol());

		//订单类型，limit，限价；market，市价
		params.put("orderType", parmsModel.getOrderType() == Constants.MARKET_PRICE ? "market" : "limit");

		if (TradeUtils.isUsdtContract(parmsModel.getSymbol())) {
			params.put("quantity", TradeUtils.getContractUsdtPlaceOrderValue(mInputNum.getText(), mUsdtTable));
		} else {
			params.put("quantity", parmsModel.getNumber());
		}
		params.put("source", "android");
		//方向closeLong(平多),closeShort(平空)
		params.put("direction", parmsModel.getTradeType() == Constants.TRADE_CLOSE_LONG ? "closeLong" : "closeShort");

		String url = mContractType == ContractType.USDT ? Constants.TRADE_CLOSE_ORDER_USDT : Constants.TRADE_CLOSE_ORDER;
		OkGo.<ContractPlaceOrderModel>post(url).params(params).tag(this).execute(new DialogCallback<ContractPlaceOrderModel>(getOwnerActivity()) {
			@Override
			public void onSuc(Response<ContractPlaceOrderModel> response) {
				ToastUtil.show(R.string.buyorsell_success);
				dismiss();
			}

			@Override
			public void onE(Response<ContractPlaceOrderModel> response) {

			}
		});
	}

	@Override
	public void show() {
		super.show();

		mInputNum.clearFocus();
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (tradeDialog != null && tradeDialog.isShowing()) {
			tradeDialog.dismiss();
		}
	}
}
