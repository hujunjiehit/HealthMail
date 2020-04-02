package com.coinbene.manbiwang.contract.contractusdt.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.database.ContractUsdtConfigController;
import com.coinbene.common.database.ContractUsdtConfigTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.ProtocolDialog;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.contract.R;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.contract.adapter.EntrustTypeAdapter;
import com.coinbene.manbiwang.contract.adapter.LeverAdapter;
import com.coinbene.manbiwang.contract.dialog.ChooseLevelDialog;
import com.coinbene.manbiwang.contract.dialog.ContractTradeDialog;
import com.coinbene.manbiwang.contract.listener.CenterViewGroupListener;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.http.BottomSelectModel;
import com.coinbene.manbiwang.model.http.OrderModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.qmuiteam.qmui.widget.roundwidget.QMUIRoundButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 合约 中部 viewGroup
 */
@SuppressLint("DefaultLocale")
public class ContractUsdtCenterLayout extends LinearLayout implements View.OnClickListener {

	@BindView(R2.id.rb_open_long)
	RadioButton rbOpenLong;
	@BindView(R2.id.rb_open_short)
	RadioButton rbOpenShort;
	@BindView(R2.id.rg_open)
	RadioGroup rgOpen;
	@BindView(R2.id.tv_login)
	TextView tvLogin;

	@BindView(R2.id.rb_order_book)
	RadioButton mRbOrderBook;
	@BindView(R2.id.rb_trade_detail)
	RadioButton mRbTradeDetail;
	@BindView(R2.id.rg_tab)
	RadioGroup mRgTab;
	@BindView(R2.id.tv_order_type)
	QMUIRoundButton mTvOrderType;
	@BindView(R2.id.layout_order_type)
	ConstraintLayout mLayoutOrderType;

	@BindView(R2.id.ll_title)
	LinearLayout llTitle;
	@BindView(R2.id.ll_fixed_price)
	LinearLayout llFixedPrice;

	@BindView(R2.id.ll_place_order)
	LinearLayout llPlaceOrder;
	@BindView(R2.id.ll_no_login)
	LinearLayout llNoLogin;

	@BindView(R2.id.ll_lever)
	LinearLayout llLever;
	@BindView(R2.id.view_line5)
	View viewLine5;
	@BindView(R2.id.tv_order_list_price)
	TextView tvOrderListPrice;
	@BindView(R2.id.tv_avl_short)
	TextView tvAvlShort;
	@BindView(R2.id.tv_avl_short_number)
	TextView tvAvlShortNumber;
	@BindView(R2.id.tv_order_list_vol)
	TextView tvOrderListVol;
	@BindView(R2.id.rl_order_list_type)
	RelativeLayout rlOrderListType;
	@BindView(R2.id.rl_order_detail_type)
	RelativeLayout rlOrderDetailType;
	@BindView(R2.id.rl_high_lever)
	RelativeLayout rlHighLever;


	@BindView(R2.id.input_price)
	PlusSubInputView inputPrice;
	@BindView(R2.id.buy_coin_name)
	TextView buyCoinName;
	@BindView(R2.id.rl_price)
	RelativeLayout rlPrice;
	@BindView(R2.id.input_num)
	PlusSubInputView inputNum;
	@BindView(R2.id.rl_number)
	RelativeLayout rlNumber;
	@BindView(R2.id.seek_bar)
	BubbleSeekBar seekBar;
	@BindView(R2.id.et_percentage)
	EditText etPercentage;
	@BindView(R2.id.tv_percentage)
	TextView tvPercentage;
	@BindView(R2.id.tv_high_lever_type)
	TextView tvHighLeverType;

	@BindView(R2.id.buy_tv)
	TextView buyTv;
	@BindView(R2.id.tv_avl_long)
	TextView tvAvlLong;
	@BindView(R2.id.tv_avl_long_number)
	TextView tvAvlLongNumber;
	@BindView(R2.id.sell_tv)
	TextView sellTv;
	@BindView(R2.id.rl_order_list)
	RecyclerView rlOrderList;
	@BindView(R2.id.rl_order_detail)
	RecyclerView rlOrderDetail;
	@BindView(R2.id.root_layout)
	ContractUsdtCenterLayout rootLayout;
	@BindView(R2.id.tv_f8_value)
	TextView tvF8Value;
	@BindView(R2.id.tv_fixed_price)
	TextView tvFixedPrice;
	@BindView(R2.id.tv_close_short_number)
	TextView tvCloseShortNumber;
	@BindView(R2.id.tv_close_long_number)
	TextView tvCloseLongNumber;
	@BindView(R2.id.tv_trade_detail_vol)
	TextView tvTradeDetailVol;


	@BindView(R2.id.tv_fixed_price_right)
	ImageView tvFixedPriceRight;
	@BindView(R2.id.tv_lever)
	TextView tvLever;
	@BindView(R2.id.tv_lever_right)
	ImageView tvLeverRight;
	@BindView(R2.id.order_list_order_detail_list)
	public ContractUsdtOrderBookLayout contractUsdtOrderBookLayout;
	@BindView(R2.id.tv_quantity_unit)
	TextView tvQuantityUnit;
	@BindView(R2.id.tv_estimated_value)
	TextView tvEstimatedValue;
	@BindView(R2.id.tv_estimated_value_one_value)
	TextView tvEstimatedValueOneValue;
	//	@BindView(R2.id.iv_fee_discribe)
//	ImageView ivFeeFiscribe;
	@BindView(R2.id.tv_f8_settlement_value)
	TextView tvF8SettlementValue;
	@BindView(R2.id.tv_market_price_value)
	TextView tvMarketPriceValue;
	@BindView(R2.id.iv_order_discribe)
	ImageView ivOrderDiscribe;
	@BindView(R2.id.layout_usdt_contract_plan)
	LinearLayout layoutUsdtContractPlan;
	@BindView(R2.id.et_stop_profit)
	PlusSubInputView etStopProfit;
	@BindView(R2.id.et_stop_loss)
	PlusSubInputView etStopLoss;
	private boolean isRedRase = SwitchUtils.isRedRise();


	public static int INDEX_OPEN = 0;//开仓
	public static int INDEX_CLOSE = 1;//平仓
	private int currentIndex = INDEX_OPEN;
	public static int DISPLAY_ORDER_LIST = 0;
	public static int DISPLAY_ORDER_DETAIL = 1;
	private int currentDisplay = DISPLAY_ORDER_LIST;

	private CenterViewGroupListener centerViewGroupListener;
	private CountDownTimer timer;
	private BottomSheetDialog fixedPriceDialog, highEntrustDialog;
	private BottomSheetDialog leverDialog;
	private ContractTradeDialog tradeDialog;
	private int currentTradeDirection;
	private int curFixPriceType = Constants.FIXED_PRICE;//限价 0   市价  1；
	private int curHighLeverEntrust = Constants.ONLY_MAKER;//高级限价委托  默认 只做maker
	private long defaultCountDownTime = 8 * 60 * 60 * 1000;//默认距费用结算  8小时
	private TextWatcher priceTextWatcher, stopProfitTextWatcher, stopLossTextWatcher;
	private boolean isNeedChangePrice = true;//是否需要显示价格输入框的价格   主要是在websocket回调里 不用每次去显示  第一次需要显示
	private ContractUsdtConfigTable contractConfigTable;
	private ContractUsdtInfoTable contractInfoTable;


	private RecyclerView mRecyclerView;
	private LeverAdapter leverAdapter = null;
	private EntrustTypeAdapter entrustTypeAdapter = null;
	private EntrustTypeAdapter highLeverAdapter = null;

	/**
	 * 计算可开张数 变量
	 */
	private int currentLever = 10;//杠杆默认是10x
	private boolean isHaveLever = false;//用户之前当前合约是否有使用的杠杆
	private String[] firstAndNewPrice = new String[4];//买一 卖一  最新价  变量随时更新，计算可开张数的时候需要拿来价算
	private String availableBalance;
	private boolean isGetUserUseLever;
	private String[] avlCloseAccount = {"0", "0"}; //可平多 可平空
	private String[] avlOpenAccount = {"0", "0"};
	private String maxOrderAccount = "10000000";

	/**
	 * isNeedFillAccount 是否需要填充可平张数
	 * fillCloseDirection 填充方向
	 */

	private boolean isNeedFillAccount;
	private String fillCloseDirection;

	//当前委托订单数量
	private int currentOrderNumber = 0;
	private ChooseLevelDialog chooseLevelDialog;
	private long foundTime;
	private BaseTextWatcher numTextWatcher;

	private SelectorDialog<String> mTypeSelectorDialog;
	private int tradeMarketPosition = Constants.TRADE_MARKET_POP_DEFAULT;

	public void setGetUserUseLever(boolean getUserUseLever) {
		isGetUserUseLever = getUserUseLever;
	}


	public void setContractInfoTable(ContractUsdtInfoTable contractInfoTable) {
		this.contractInfoTable = contractInfoTable;
		setCurrentLever();
		updataContractUnit();
	}


	//最新价
	public String getlastPrice() {
		return firstAndNewPrice[2];
	}

	public void setFirstAndNewPrice(String[] firstAndNewPrice) {
		this.firstAndNewPrice = firstAndNewPrice;
	}

	private void setCurrentLever() {
		if (contractInfoTable != null && contractInfoTable.curLever != 0) {
			this.currentLever = contractInfoTable.curLever;
		} else {
			this.currentLever = 10;
		}
	}


	public int getCurrentLever() {
		return currentLever;
	}

	public void setNeedChangePrice(boolean needChangePrice) {
		isNeedChangePrice = needChangePrice;
	}

	public ContractUsdtCenterLayout(Context context) {
		super(context);
	}

	public ContractUsdtCenterLayout(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	public ContractUsdtCenterLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setCenterViewGroupListener(CenterViewGroupListener centerViewGroupListener) {
		this.centerViewGroupListener = centerViewGroupListener;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ButterKnife.bind(this);
		initView();
		initListener();
		contractConfigTable = ContractUsdtConfigController.getInstance().queryContrackConfig();
	}

	public void setF8(String f8) {
		if (tvF8Value != null)
			tvF8Value.setText(TextUtils.isEmpty(f8) ? "--" : BigDecimalUtils.toPercentage(f8, "0.0000%"));
	}


	private void initView() {

		contractUsdtOrderBookLayout.setTvMarkPrice(tvMarketPriceValue);
		tvLever.setText(String.format("%dX", currentLever));
		tvAvlLongNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvAvlShortNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvQuantityUnit.setText(TradeUtils.getContractUsdtUnit(contractInfoTable));
		rbOpenLong.setChecked(true);
		rgOpen.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_open_long) {
				currentIndex = INDEX_OPEN;
				calculationAvlOpen();
				tvCloseShortNumber.setVisibility(GONE);
				tvCloseLongNumber.setVisibility(GONE);
				tvAvlLongNumber.setVisibility(VISIBLE);
				tvAvlShortNumber.setVisibility(VISIBLE);

			} else {
				currentIndex = INDEX_CLOSE;
				setAvlClose();

			}
			changeTab(true);

		});
		initBackColor();
		changeTab(false);

	}


	/**
	 * 更新交易单位
	 */
	public void updataContractUnit() {
		tvAvlLongNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvAvlShortNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
//		tvCloseShortNumber.setText(String.format("%s%s", avlCloseAccount[1], TradeUtils.getContractUsdtUnit(contractInfoTable)));
//		tvCloseLongNumber.setText(String.format("%s%s", avlCloseAccount[0], TradeUtils.getContractUsdtUnit(contractInfoTable)));
		inputPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.price), contractInfoTable.quoteAsset));
		inputPrice.setMinPriceChange(contractInfoTable.minPriceChange);
		inputNum.setHint(String.format("%s(%s)", getResources().getString(R.string.input_num_hint), TradeUtils.getContractUsdtUnit(contractInfoTable)));
		inputNum.setMinPriceChange(TradeUtils.getContractUsdtMultiplier(contractInfoTable));
		tvQuantityUnit.setText(TradeUtils.getContractUsdtUnit(contractInfoTable));
		inputNum.setText("");
		calculationAvlOpen();
		tvTradeDetailVol.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvOrderListVol.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvEstimatedValueOneValue.setText(String.format("%s%s", TradeUtils.getContractUsdtEstimatedValue("1", contractInfoTable), contractInfoTable.baseAsset));
		tvEstimatedValue.setText("");
		etStopProfit.setHint(String.format("%s(%s)", getContext().getString(R.string.stop_profit_price), contractInfoTable.quoteAsset));
		etStopLoss.setHint(String.format("%s(%s)", getContext().getString(R.string.stop_loss_price), contractInfoTable.quoteAsset));
		etStopProfit.setMinPriceChange(contractInfoTable.minPriceChange);
		etStopLoss.setMinPriceChange(contractInfoTable.minPriceChange);
		etStopLoss.setText("");
		etStopProfit.setText("");
	}

//	public void setVolUnit() {
//
//	}

	/**
	 * 设置可平仓张数
	 */
	private void setAvlClose() {
		tvCloseShortNumber.setVisibility(VISIBLE);
		tvCloseLongNumber.setVisibility(VISIBLE);
		tvAvlLongNumber.setVisibility(GONE);
		tvAvlShortNumber.setVisibility(GONE);
		tvCloseShortNumber.setText(String.format("%s%s", avlCloseAccount[1], TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvCloseLongNumber.setText(String.format("%s%s", avlCloseAccount[0], TradeUtils.getContractUsdtUnit(contractInfoTable)));
	}

	public void setInputPrecision(int precision, String minPriceChange) {
		/**
		 * 输入价格变化监听
		 */
		inputPrice.setText("");
		//每次初始化precision的时候先remove掉上一次的   不然有多个 textWatcher 会崩溃
		inputPrice.removeTextChangedListener(priceTextWatcher);

		priceTextWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				inputPrice.removeTextChangedListener(this);
				if (!TextUtils.isEmpty(minPriceChange)) {
					PrecisionUtils.setPrecisionMinPriceChange(inputPrice.getmEditText(), s, precision, minPriceChange);
				}
				calculationAvlOpen();
				inputPrice.addTextChangedListener(this);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
		inputPrice.addTextChangedListener(priceTextWatcher);

		etStopProfit.removeTextChangedListener(stopProfitTextWatcher);
		stopProfitTextWatcher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etStopProfit.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etStopProfit.getmEditText(), s, precision, minPriceChange);
				etStopProfit.addTextChangedListener(this);
			}
		};
		etStopProfit.addTextChangedListener(stopProfitTextWatcher);

		etStopLoss.removeTextChangedListener(stopLossTextWatcher);
		stopLossTextWatcher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etStopLoss.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etStopLoss.getmEditText(), s, precision, minPriceChange);
				etStopLoss.addTextChangedListener(this);
			}
		};
		etStopLoss.addTextChangedListener(stopLossTextWatcher);


	}

	public void setAmountUnit(String baseAsset) {
		tvOrderListPrice.setText(TextUtils.isEmpty(baseAsset) ? getResources().getString(R.string.price) :
				String.format("%s(%s)", getResources().getString(R.string.price), baseAsset));

		tvOrderListVol.setText(
				String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvTradeDetailVol.setText(String.format("%s(%s)", getResources().getString(R.string.trade_vol), TradeUtils.getContractUsdtUnit(contractInfoTable)));
	}

	private void initListener() {
		ivOrderDiscribe.setOnClickListener(v -> showContractPlanDiscribe());
		rlHighLever.setOnClickListener(this);
		tvCloseLongNumber.setOnClickListener(this);
		tvCloseShortNumber.setOnClickListener(this);
		tvLogin.setOnClickListener(this);
		sellTv.setOnClickListener(this);
		buyTv.setOnClickListener(this);
		llFixedPrice.setOnClickListener(this);
		llLever.setOnClickListener(this);
		contractUsdtOrderBookLayout.setOrderItemClick(price -> {
			if (curFixPriceType != Constants.MARKET_PRICE) {
				inputPrice.setText(price);
				inputPrice.setSelection(inputPrice.getText().length());
//                calculationAvlOpen();
			}
		});

		etStopProfit.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				etStopProfit.setText(firstAndNewPrice[2]);
			}

			@Override
			public void onSub() {

			}
		});
		etStopLoss.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				etStopLoss.setText(firstAndNewPrice[2]);
			}

			@Override
			public void onSub() {

			}
		});


		numTextWatcher = new BaseTextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				super.beforeTextChanged(s, start, count, after);
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				super.onTextChanged(s, start, before, count);


				if (contractInfoTable != null) {
					if (TradeUtils.getContractUsdtPrecision(contractInfoTable) == 0) {
						inputNum.getmEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
						if (SpUtil.getContractUsdtUnitSwitch() == 0) {
							if (!BigDecimalUtils.isEmptyOrZero(inputNum.getText()))
								tvEstimatedValue.setText(String.format("=%s %s", TradeUtils.getContractUsdtEstimatedValue(inputNum.getText(), contractInfoTable), contractInfoTable.baseAsset));
							else {
								tvEstimatedValue.setText("");
							}
						}
					} else {
						inputNum.getmEditText().setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
						PrecisionUtils.setPrecision(inputNum.getmEditText(), s, TradeUtils.getContractUsdtPrecision(contractInfoTable));
						tvEstimatedValue.setText("");
					}
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
//				inputNum.setSeparatorText(numTextWatcher);
			}
		};
		inputNum.addTextChangedListener(numTextWatcher);

//		tvEstimatedValueOneValue.setOnClickListener(v -> showEstimatedPopWindow());

		if (mTypeSelectorDialog == null) {
			mTypeSelectorDialog = new SelectorDialog<>(getContext());
			List<String> typeList = new ArrayList<>();
			typeList.add(getResources().getString(R.string.market_default));
			typeList.add(getResources().getString(R.string.show_buy_market));
			typeList.add(getResources().getString(R.string.show_sell_merket));
			mTypeSelectorDialog.setDatas(typeList);
		}

		mTypeSelectorDialog.setSelectListener((data, positon) -> {
			mTvOrderType.setText(data);
			tradeMarketPosition = positon;
			contractUsdtOrderBookLayout.setCurrentDisplay(positon);
			NewContractUsdtWebsocket.getInstance().pullOrderListData();
		});

		mTvOrderType.setOnClickListener(v -> {
			mTypeSelectorDialog.setDefaultPosition(tradeMarketPosition);
			mTypeSelectorDialog.show();
		});

		mRgTab.check(R.id.rb_order_book);
		mRgTab.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_order_book) {
				//挂单
				mLayoutOrderType.setVisibility(VISIBLE);
				currentDisplay = DISPLAY_ORDER_LIST;
				rlOrderList.setVisibility(VISIBLE);
				rlOrderDetail.setVisibility(GONE);
				rlOrderListType.setVisibility(View.VISIBLE);
				rlOrderDetailType.setVisibility(View.GONE);

				if (centerViewGroupListener != null) {
					centerViewGroupListener.clickOrderBook();
				}
			} else if (checkedId == R.id.rb_trade_detail) {
				//成交
				mLayoutOrderType.setVisibility(GONE);
				currentDisplay = DISPLAY_ORDER_DETAIL;
				rlOrderList.setVisibility(GONE);
				rlOrderDetail.setVisibility(VISIBLE);
				rlOrderListType.setVisibility(View.GONE);
				rlOrderDetailType.setVisibility(View.VISIBLE);
				if (centerViewGroupListener != null) {
					centerViewGroupListener.clickOrderDetail();
				}
			}
		});
	}

	private void showContractPlanDiscribe() {
		DialogManager.getMessageDialogBuilder(getContext())
				.setTitle(R.string.contract_plan)
				.setMessage(getContext().getString(R.string.contract_plan_discribe))
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

//	/**
//	 * 资金费率说明
//	 */
//	private void showEstimatedPopWindow() {
//		PopWindow qmuiPopup = new PopWindow(getContext(), ivFeeFiscribe);
//		qmuiPopup.getTv1().setText(String.format(getResources().getString(R.string.next_found_time), TimeUtils.getMDHMS(foundTime)));
//		qmuiPopup.getTv2().setText("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
//		qmuiPopup.show(ivFeeFiscribe);
//	}

	/**
	 * 红涨绿跌 开仓平仓  tab 文字颜色  及背景
	 */
	private void initBackColor() {
		if (isRedRase) {
			rbOpenLong.setBackground(getResources().getDrawable(R.drawable.open_red_right_selector));
			rbOpenLong.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_check_white));
			rbOpenShort.setBackground(getResources().getDrawable(R.drawable.open_green_left_selector));
			rbOpenShort.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_check_white));
		} else {
			rbOpenLong.setBackground(getResources().getDrawable(R.drawable.open_green_right_selector));
			rbOpenLong.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_check_white));
			rbOpenShort.setBackground(getResources().getDrawable(R.drawable.open_red_left_selector));
			rbOpenShort.setTextColor(getResources().getColorStateList(R.color.res_selector_tv2_check_white));
		}
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	/**
	 * 当前持仓点击平仓
	 * 切换到平仓状态
	 *
	 * @param currentIndex
	 */
	public void changeTabToClosePosition(int currentIndex) {
		this.currentIndex = currentIndex;
		if (currentIndex == INDEX_OPEN) {
			rbOpenLong.setChecked(true);
		} else
			rbOpenShort.setChecked(true);
//        initBackColor();
		changeTab(true);
	}

	/**
	 * 点击开仓平仓切换  改变view颜色及文字
	 * isChangeFixPrice  切换tab是否需要改为默认限价
	 */
	private void changeTab(boolean isChangeFixPrice) {

//新增高级限价委托后切换委托的方式去掉了
//        if (isChangeFixPrice) {
//            curFixPriceType = FIXED_PRICE;
//            tvFixedPrice.setText(R.string.fixed_price_entrust);
//            inputPrice.setEnabled(true);
//            inputPrice.setHint(R.string.price);
//            rlPrice.setBackground(getResources().getDrawable(R.drawable.res_shape_frame_normal));
//        }
		if (curFixPriceType != Constants.MARKET_PRICE && !TextUtils.isEmpty(firstAndNewPrice[2])) {
			inputPrice.setText(firstAndNewPrice[2]);
		}
		inputNum.setText("");
		if (currentIndex == INDEX_OPEN) {//开仓
			etStopProfit.setEnabled(true);
			etStopLoss.setEnabled(true);
			llLever.setVisibility(VISIBLE);
			buyTv.setText(R.string.buy_open_long_zh);
			sellTv.setText(R.string.sell_open_short_zh);
			tvAvlLong.setText(R.string.avl_open_long);
			tvAvlShort.setText(R.string.avl_open_short);
			if (isRedRase) {//红涨绿跌
				buyTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
				sellTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
			} else {
				buyTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
				sellTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
			}
		} else {//平仓
			etStopProfit.setEnabled(false);
			etStopLoss.setEnabled(false);
			llLever.setVisibility(GONE);
			buyTv.setText(R.string.buy_close_short);
			sellTv.setText(R.string.sell_close_long);
			tvAvlLong.setText(R.string.avl_close);
			tvAvlShort.setText(R.string.avl_close);
			if (isRedRase) {//绿涨红跌
//                seekBar.setThumbColor(getContext().getResources().getColor(R.color.res_green));
//                seekBar.setSecondTrackColor(getContext().getResources().getColor(R.color.res_green));
				buyTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
				sellTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
			} else {
//                seekBar.setThumbColor(getContext().getResources().getColor(R.color.ma5));
//                seekBar.setSecondTrackColor(getContext().getResources().getColor(R.color.ma5));
				buyTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
				sellTv.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
			}
		}
	}


	//红涨跌跌相关view颜色变化
	public void setRedRase(boolean redRase) {
		if (isRedRase == redRase) {
			return;
		}
		isRedRase = redRase;
		initBackColor();
		changeTab(false);
	}


	/**
	 * 挂单数据填充
	 *
	 * @param buyList
	 * @param sellList
	 * @param tickerData
	 */
	public void setOrderListData(List<OrderModel> buyList, List<OrderModel> sellList, WsMarketData tickerData, String contractName) {

		contractUsdtOrderBookLayout.setOrderListData(buyList, sellList, tickerData, contractName);

		if (tickerData != null) {
			//买一价
			firstAndNewPrice[0] = tickerData.getBestBidPrice();
			//卖一价
			firstAndNewPrice[1] = tickerData.getBestAskPrice();
		}

		if (tickerData != null && !TextUtils.isEmpty(tickerData.getLastPrice())) {
			firstAndNewPrice[2] = tickerData.getLastPrice();
			firstAndNewPrice[3] = tickerData.getMarkPrice();
			if (isNeedChangePrice) {
				post(() -> {
					if (curFixPriceType != Constants.MARKET_PRICE) {
						inputPrice.setText(tickerData.getLastPrice());
						isNeedChangePrice = false;
					}
				});
			}
		}
	}


	/**
	 * orderDetail数据填充
	 *
	 * @param orderDetailList
	 */
	public void setOrderDetailData(List<WsTradeList> orderDetailList, String contractName) {
		contractUsdtOrderBookLayout.setOrderDetailData(orderDetailList, contractName);
	}


	/**
	 * 费用结算倒计时
	 *
	 * @param time
	 */
	public void initTimer(long time) {
//		foundTime = time;
		if (time <= 0) {

			time = defaultCountDownTime;
		} else {
			time = time * 1000;
		}
		destroyTimer();
		timer = new CountDownTimer(time, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				tvF8SettlementValue.setText(TimeUtils.secondToHourMinSecond(millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
//				tvF8SettlementValue.setText("0");
				destroyTimer();
				//小于0  执行默认的开始时间
				initTimer(-1);


			}
		};
		timer.start();
	}

	/**
	 * 销毁倒计时
	 */
	private void destroyTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	@Override
	public void onClick(View v) {
		//当前交易方向
		int id = v.getId();
		if (id == R.id.buy_tv) {
			if (currentIndex == INDEX_OPEN) {// 买入开多
				currentTradeDirection = Constants.TRADE_OPEN_LONG;
			} else {//买入平空
				currentTradeDirection = Constants.TRADE_CLOSE_SHORT;
			}

			checkAndShowTradeDialog(currentTradeDirection, tvFixedPrice.getText().toString()
					, buyTv.getText().toString()
					, inputPrice.getText()
					, inputNum.getText().toString()
					, contractInfoTable.name
					, contractInfoTable.precision
					, curFixPriceType
					, true);
		} else if (id == R.id.sell_tv) {//卖出

			if (currentIndex == INDEX_OPEN) {
				currentTradeDirection = Constants.TRADE_OPEN_SHORT;
			} else {
				currentTradeDirection = Constants.TRADE_CLOSE_LONG;

			}
			checkAndShowTradeDialog(currentTradeDirection, tvFixedPrice.getText().toString()
					, sellTv.getText().toString()
					, inputPrice.getText()
					, inputNum.getText().toString()
					, contractInfoTable.name
					, contractInfoTable.precision
					, curFixPriceType, true);
		} else if (id == R.id.ll_fixed_price) {
			showFixPriceDialog();
		} else if (id == R.id.tv_login) {
			gotoLoginOrLock();
		} else if (id == R.id.tv_close_long_number) {//
			if (!BigDecimalUtils.isEmptyOrZero(tvCloseLongNumber.getText().toString()))
				inputNum.setText(tvCloseLongNumber.getText().toString());
//                }
		} else if (id == R.id.tv_close_short_number) {//                if(avlCloseAccount[1]!=0){
			inputNum.setText(avlCloseAccount[1]);
//                }
		} else if (id == R.id.ll_lever) {//如果用户已经有杠杆  则不允许切换
			//如果用户当前已经有委托  则不允许切换
			if (currentOrderNumber > 0) {
				ToastUtil.show(R.string.res_not_support_change_level);
				return;
			}

			if (contractInfoTable != null && !TextUtils.isEmpty(contractInfoTable.leverages)) {
				showLeverDialog(tvLever.getText().toString());
			}
		} else if (id == R.id.rl_high_lever) {
			showHighEntrustDialog();
		}
	}

	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}


	/**
	 * 点击开仓或者平仓的弹窗 及 弹窗之前的校验
	 *
	 * @param buyOrSell
	 */
//	@NeedLogin(jump = true)
	public void checkAndShowTradeDialog(int tradeDirection, String fixedStr, String buyOrSell, String price, String number, String symble, int precision, int curFixPriceType, boolean needCheck) {

		//未开启显示
		if (!SpUtil.getProtocolStatusOfContract("usdt")) {
			ProtocolDialog dialog = new ProtocolDialog(getContext());
			dialog.setTitle(getContext().getResources().getString(R.string.usdt_protocol_title));
			dialog.setContent(getContext().getResources().getString(R.string.usdt_protocol_content));
			dialog.setPositiveText(getContext().getResources().getString(R.string.go_trading));
			dialog.setNegativeText(getContext().getResources().getString(R.string.not_trading));
			dialog.setProtocolText(getContext().getResources().getString(R.string.usdt_protocol_text));
			dialog.setProtocolUrl(UrlUtil.getUsdtProtocol());
			dialog.show();

			dialog.setDialogListener(new DialogListener() {
				@Override
				public void clickNegative() {

				}

				@Override
				public void clickPositive() {
					centerViewGroupListener.agreeProtocol();
				}
			});
			return;
		}

		if (needCheck) {

			//限价的时候需要判断输入的价格是不是空或者是不是0
			if (this.curFixPriceType == Constants.FIXED_PRICE && BigDecimalUtils.isEmptyOrZero(inputPrice.getText().toString())) {
				ToastUtil.show(R.string.please_input_coin_price_tip);
				return;
			}

			if (BigDecimalUtils.isEmptyOrZero(inputNum.getText())) {
				ToastUtil.show(R.string.please_input_coin_num_tip);
				return;
			}


			//如果是开多  需要检查一下 可开张数够不够
			if (tradeDirection == Constants.TRADE_OPEN_LONG) {

				if (BigDecimalUtils.isGreaterThan(TradeUtils.getContractUsdtUnitValue(inputNum.getText(), contractInfoTable), maxOrderAccount)) {
					ToastUtil.show(R.string.max_order_account);
					return;

				}

				if (BigDecimalUtils.isEmptyOrZero(avlOpenAccount[0]) || BigDecimalUtils.isGreaterThan(inputNum.getText(), String.valueOf(avlOpenAccount[0]))) {
					ToastUtil.show(R.string.available_balance_Insufficient);
					return;
				}
				if (curFixPriceType == Constants.FIXED_PRICE) {
					if (!BigDecimalUtils.isEmptyOrZero(etStopProfit.getText()) && BigDecimalUtils.isLessThan(etStopProfit.getText(), inputPrice.getText())) {
						ToastUtil.show(R.string.profit_not_less_price);
						return;
					}
					if (!BigDecimalUtils.isEmptyOrZero(etStopLoss.getText()) && BigDecimalUtils.isGreaterThan(etStopLoss.getText(), inputPrice.getText())) {
						ToastUtil.show(R.string.less_not_than_price);
						return;
					}
				}

			}
			//如果是开空  需要检查一下 可开张数够不够
			if (tradeDirection == Constants.TRADE_OPEN_SHORT) {
				if (BigDecimalUtils.isGreaterThan(TradeUtils.getContractUsdtUnitValue(inputNum.getText(), contractInfoTable), maxOrderAccount)) {
					ToastUtil.show(R.string.max_order_account);
					return;

				}

				if (BigDecimalUtils.isEmptyOrZero(avlOpenAccount[1]) || BigDecimalUtils.isGreaterThan(inputNum.getText().toString(), avlOpenAccount[1])) {
					ToastUtil.show(R.string.available_balance_Insufficient);
					return;
				}
				if (curFixPriceType == Constants.FIXED_PRICE) {
					if (!BigDecimalUtils.isEmptyOrZero(etStopProfit.getText()) && BigDecimalUtils.isGreaterThan(etStopProfit.getText(), inputPrice.getText())) {
						ToastUtil.show(R.string.progit_not_than_price);
						return;
					}
					if (!BigDecimalUtils.isEmptyOrZero(etStopLoss.getText()) && BigDecimalUtils.isLessThan(etStopLoss.getText(), inputPrice.getText())) {
						ToastUtil.show(R.string.loss_not_less_price);
						return;
					}
				}

			}
			//如果是平多  需要检查一下 可平张数够不够
			if (tradeDirection == Constants.TRADE_CLOSE_LONG) {
				if (BigDecimalUtils.isEmptyOrZero(avlCloseAccount[0]) || BigDecimalUtils.isGreaterThan(inputNum.getText().toString(), avlCloseAccount[0])) {
					ToastUtil.show(R.string.close_amount_insufficient_tip);
					return;
				}
			}
			//如果是平空 需要检查一下 可平张数够不够
			if (tradeDirection == Constants.TRADE_CLOSE_SHORT) {
				if (BigDecimalUtils.isEmptyOrZero(avlCloseAccount[1]) || BigDecimalUtils.isGreaterThan(inputNum.getText().toString(), avlCloseAccount[1])) {
					ToastUtil.show(R.string.close_amount_insufficient_tip);
					return;
				}
			}
			if (!TradeUtils.checkContractUsdtInput(inputNum.getText().toString(), contractInfoTable)) {
				ToastUtil.show(String.format("%s%s", getContext().getString(R.string.usdt_contract_min_place_order), contractInfoTable.multiplier));
				return;
			}

		}
//		if (SpUtil.isContractSureDialogNotShow()) {
//			placeOrder(tradeDirection, curFixPriceType, curHighLeverEntrust, price, number, symble);
//			return;
//		}
//		if (tradeDialog == null) {
//			tradeDialog = new ContractTradeDialog(getContext());
//			tradeDialog.setOnClickListener(new ContractTradeDialog.OnClickListener() {
//				@Override
//				public void clickCancel() {
//
//				}
//
//				@Override
//				public void placeOrder(ContractPlaceOrderParmsModel orderModel) {
////					placeOrder(orderModel.getTradeType(), priceType, curHighLeverEntrust, price, number, contractName);
//				}
//
////				@Override
////				public void clickSure(int tradeDirection, int priceType, int curHighLeverEntrust, String price, String number, String contractName) {
//////					placeOrder(tradeDirection, priceType, curHighLeverEntrust, price, number, contractName);
////				}
//			});
//		}
//		tradeDialog.buildTitle(fixedStr, buyOrSell, symble)
//				.buildLeverRatio(tvLever.getText().toString())
//				.buildPrice(PrecisionUtils.appendZero(price, precision))
//				.buildNumber(String.format("%s %s", TradeUtils.getContractUsdtDialogShowValue(inputNum.getText(), contractInfoTable), TradeUtils.getContractUsdtUnit(contractInfoTable)))
//				.buildEstimatedValue(tvEstimatedValue.getText().toString())
//				.setFixPriceType(curFixPriceType, curHighLeverEntrust)
//				.buildProfitPrice(etStopProfit.getText())
//				.buildLossPrice(etStopLoss.getText())
//				.setRedRase(isRedRase)
//				.setDirection(tradeDirection)
//				.show();
	}

	/**
	 * 下单的回调
	 */
	private void placeOrder(int tradeDirection, int priceType, int curHighLeverEntrust, String price, String number, String contractName) {
		if (centerViewGroupListener != null) {
			centerViewGroupListener.clickPlaceOrder(tradeDirection, priceType, curHighLeverEntrust, currentLever,
					price, TradeUtils.getContractUsdtPlaceOrderValue(inputNum.getText(), contractInfoTable), contractName, etStopProfit.getText(), etStopLoss.getText());
		}
	}


	public void setLever(int lever) {
		if (lever == 0) {
			isHaveLever = false;
			currentLever = getCurrentLever();
			tvLever.setText(String.format("%dX", currentLever));
		} else {
			isHaveLever = true;
			currentLever = lever;
			ContractUsdtInfoController.getInstance().updataContractLever(contractInfoTable.name, currentLever);
			tvLever.setText(String.format("%dX", lever));
		}
		isGetUserUseLever = true;
		calculationAvlOpen();

	}

	private void showHighEntrustDialog() {

		if (highEntrustDialog == null) {
			highEntrustDialog = new BottomSheetDialog(getContext(), R.style.CoinBene_BottomSheet);
			highEntrustDialog.setContentView(R.layout.dialog_contract_mode_entrust);
			mRecyclerView = highEntrustDialog.findViewById(R.id.rlv_item_list);
			highLeverAdapter = new EntrustTypeAdapter(tvHighLeverType.getText().toString());
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
			mRecyclerView.setAdapter(highLeverAdapter);
			TextView tvCancel = highEntrustDialog.findViewById(R.id.ad_dialog_cancel);
			List<BottomSelectModel> datas = new ArrayList<>();
			datas.add(new BottomSelectModel(getContext().getString(R.string.only_marker), Constants.ONLY_MAKER));
			datas.add(new BottomSelectModel(getContext().getString(R.string.all_deal_or_cancel), Constants.ALL_DEAL_OR_ALL_CANCEL));
			datas.add(new BottomSelectModel(getContext().getString(R.string.deal_cancel_surplus), Constants.DEAL_CANCEL_SURPLUS));

			highLeverAdapter.setItem(datas);

			highLeverAdapter.setListener(item -> {
				highEntrustDialog.cancel();
				if (curHighLeverEntrust == item.getType()) {
					return;
				}
				curHighLeverEntrust = item.getType();
				tvHighLeverType.setText(item.getTypeName());
			});

			tvCancel.setOnClickListener(v -> highEntrustDialog.cancel());
		} else {
			highLeverAdapter.setCurrentSelect(tvHighLeverType.getText().toString());
			highLeverAdapter.notifyDataSetChanged();
		}
		highEntrustDialog.show();

	}


	/**
	 * 点击限价市价选择框
	 */
	private void showFixPriceDialog() {

		if (fixedPriceDialog == null) {
			fixedPriceDialog = new BottomSheetDialog(getContext(), R.style.CoinBene_BottomSheet);
			fixedPriceDialog.setContentView(R.layout.dialog_contract_mode_entrust);
			mRecyclerView = fixedPriceDialog.findViewById(R.id.rlv_item_list);
			entrustTypeAdapter = new EntrustTypeAdapter(tvFixedPrice.getText().toString());
			mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
			mRecyclerView.setAdapter(entrustTypeAdapter);
			TextView tvCancel = fixedPriceDialog.findViewById(R.id.ad_dialog_cancel);
			List<BottomSelectModel> datas = new ArrayList<>();
			datas.add(new BottomSelectModel(getContext().getString(R.string.fixed_price_entrust), Constants.FIXED_PRICE));
			datas.add(new BottomSelectModel(getContext().getString(R.string.market_price_entrust), Constants.MARKET_PRICE));
			datas.add(new BottomSelectModel(getContext().getString(R.string.high_lever_entrust), Constants.FIXED_PRICE_HIGH_LEVER));

			entrustTypeAdapter.setItem(datas);

			entrustTypeAdapter.setListener(item -> {
				fixedPriceDialog.cancel();
				if (curFixPriceType == item.getType()) {
					return;
				}
				curFixPriceType = item.getType();
				tvFixedPrice.setText(item.getTypeName());
				setPriceTypeView();
			});

			tvCancel.setOnClickListener(v -> fixedPriceDialog.cancel());
		} else {
			entrustTypeAdapter.setCurrentSelect(tvFixedPrice.getText().toString());
			entrustTypeAdapter.notifyDataSetChanged();
		}
		fixedPriceDialog.show();

	}

	private void setPriceTypeView() {
		if (curFixPriceType == Constants.FIXED_PRICE) {
			layoutUsdtContractPlan.setVisibility(VISIBLE);
			rlHighLever.setVisibility(GONE);
			rlPrice.setBackground(getResources().getDrawable(R.drawable.shape_edit_normal_background));
			inputPrice.setHint(R.string.price);
			if (!TextUtils.isEmpty(firstAndNewPrice[2])) {
				inputPrice.setText(firstAndNewPrice[2]);
			}
			inputPrice.setEnablePlusSub(true);
			inputPrice.setEnabled(true);
//            calculationAvlOpen();
		} else if (curFixPriceType == Constants.MARKET_PRICE) {
			layoutUsdtContractPlan.setVisibility(VISIBLE);
			inputPrice.setEnablePlusSub(false);
			rlHighLever.setVisibility(GONE);
			inputPrice.setEnabled(false);
			inputPrice.setText("");
			inputPrice.setHint(R.string.market_price);
			rlPrice.setBackground(getResources().getDrawable(R.drawable.shape_edit_disable_background));
//            calculationAvlOpen();
		} else if (curFixPriceType == Constants.FIXED_PRICE_HIGH_LEVER) {
			layoutUsdtContractPlan.setVisibility(GONE);
			inputPrice.setEnablePlusSub(true);
			rlHighLever.setVisibility(VISIBLE);
			rlPrice.setBackground(getResources().getDrawable(R.drawable.shape_edit_focused_background));
			inputPrice.setHint(R.string.price);
			if (!TextUtils.isEmpty(firstAndNewPrice[2])) {
				inputPrice.setText(firstAndNewPrice[2]);
			}
			inputPrice.setEnabled(true);
//            calculationAvlOpen();
		}
	}


	/**
	 * 点击杠杆选择框
	 */
	private void showLeverDialog(String lever) {
		if (contractInfoTable == null) {
			return;
		}
		if (chooseLevelDialog == null) {
			chooseLevelDialog = new ChooseLevelDialog(getContext());
		}
		chooseLevelDialog.initDialog(contractInfoTable.name, lever);
		chooseLevelDialog.showWithCallBack(leverage -> {
			if (currentLever == leverage) {
				return;
			}
			currentLever = leverage;
			ContractUsdtInfoController.getInstance().updataContractLever(contractInfoTable.name, leverage);
			tvLever.setText(String.format("%dX", currentLever));
			calculationAvlOpen();
		});
	}


	/**
	 * 计算可开多开空张数   登录并且解锁状态   并且拿到用户使用的杠杆之后再计算可开张数 并且在开仓tab下
	 * 用户资产变化 、切换杠杆、切换限价或者市价的时候都需要重新计算可开张数
	 */
	public void calculationAvlOpen() {
		if (contractInfoTable != null && contractConfigTable != null && CommonUtil.isLoginAndUnLocked() && isGetUserUseLever && currentIndex == INDEX_OPEN) {
			avlOpenAccount = CalculationUtils.calculationOther(curFixPriceType, inputPrice.getText().toString(),
					firstAndNewPrice[0],
					firstAndNewPrice[1],
					firstAndNewPrice[2],
					firstAndNewPrice[3],
					contractInfoTable.costPriceMultiplier,
					availableBalance,
					currentLever,
					contractConfigTable.takerFeeRate,
					contractInfoTable.multiplier,
					contractInfoTable.precision,
					contractInfoTable.minPriceChange);

			avlOpenAccount[0] = TradeUtils.getContractUsdtUnitValue(avlOpenAccount[0], contractInfoTable);
			avlOpenAccount[1] = TradeUtils.getContractUsdtUnitValue(avlOpenAccount[1], contractInfoTable);
			//大于等于 999999999则显示加号
			if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[0], CalculationUtils.maxAvlOpenAmount)) {
				avlOpenAccount[0] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				tvAvlLongNumber.setText(String.format("%s%s%s", avlOpenAccount[0], "+", TradeUtils.getContractUsdtUnit(contractInfoTable)));
			} else {
				tvAvlLongNumber.setText(String.format("%s%s", avlOpenAccount[0], TradeUtils.getContractUsdtUnit(contractInfoTable)));
			}
			if (BigDecimalUtils.isThanOrEqual(avlOpenAccount[1], CalculationUtils.maxAvlOpenAmount)) {
				avlOpenAccount[1] = CalculationUtils.maxAvlOpenAmount.toPlainString();
				tvAvlShortNumber.setText(String.format("%s%s%s", avlOpenAccount[1], "+", TradeUtils.getContractUsdtUnit(contractInfoTable)));
			} else {
				tvAvlShortNumber.setText(String.format("%s%s", avlOpenAccount[1], TradeUtils.getContractUsdtUnit(contractInfoTable)));
			}
		}
	}

	//对应activity的 onDestory
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		destroyTimer();
	}


	//对应activity的 onResume
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}


	/**
	 * @param availableBalance 可用资产   算 可开张数的时候需要用
	 * @param longQuantity     可平多张数
	 * @param shortQuantity    可平空张数
	 */
	public void setUserData(String availableBalance, String longQuantity, String shortQuantity) {
		this.availableBalance = availableBalance;
		this.avlCloseAccount[0] = TradeUtils.getContractUsdtUnitValue(longQuantity, contractInfoTable);
		this.avlCloseAccount[1] = TradeUtils.getContractUsdtUnitValue(shortQuantity, contractInfoTable);

		if (currentIndex == INDEX_CLOSE) {
			setAvlClose();
		}

		if (isNeedFillAccount) {
			if (!TextUtils.isEmpty(fillCloseDirection)) {
				inputNum.setText(fillCloseDirection.equals("long") ? avlCloseAccount[0] : avlCloseAccount[1]);
				inputNum.setSelection(inputNum.getText().toString().length());
			}
			isNeedFillAccount = false;
		}

	}

	public void clearInputText() {
//		inputPrice.setText("");
		inputNum.setText("");
		etStopLoss.setText("");
		etStopProfit.setText("");
	}

	/**
	 * 当点击持仓列表平仓的时候  需要填充可平张数
	 *
	 * @param direction
	 */
	public void setAvlCloseAccount(String direction) {
		if (!TextUtils.isEmpty(direction)) {
			inputNum.setText(direction.equals("long") ? String.valueOf(avlCloseAccount[0]) : String.valueOf(avlCloseAccount[1]));
			inputNum.setSelection(inputNum.getText().toString().length());
		}

	}

	public void setNeedFillAvlCloseAccount(boolean isNeedFillAccount, String direction) {
		this.isNeedFillAccount = isNeedFillAccount;
		this.fillCloseDirection = direction;
	}

	public void clearFirstAndNewPrice() {
		if (firstAndNewPrice != null) {
			firstAndNewPrice[0] = "";
			firstAndNewPrice[1] = "";
			firstAndNewPrice[2] = "";
			firstAndNewPrice[3] = "";
		}
	}

	public void setUserLogin() {
		if (CommonUtil.isLoginAndUnLocked()) {
			llNoLogin.setVisibility(GONE);
			llPlaceOrder.setVisibility(VISIBLE);
		} else {
			llNoLogin.setVisibility(VISIBLE);
			llPlaceOrder.setVisibility(GONE);
		}
	}


	public void setNoUser() {
		avlCloseAccount = new String[]{"0", "0"};
		avlOpenAccount = new String[]{"0", "0"};
		tvAvlLongNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
		tvAvlShortNumber.setText(String.format("%d%s", 0, TradeUtils.getContractUsdtUnit(contractInfoTable)));
		isHaveLever = false;
		currentLever = getCurrentLever();
		isGetUserUseLever = false;
		tvLever.setText(String.format("%dX", currentLever));
		llNoLogin.setVisibility(VISIBLE);
		llPlaceOrder.setVisibility(GONE);

		//未登录时弹窗仍然展示，并且造成可下单的BUG
		if (tradeDialog != null && tradeDialog.isShowing()) {
			tradeDialog.dismiss();
		}

	}


	public void setOrderType(int curDisplayType) {
		contractUsdtOrderBookLayout.setCurDisplayType(curDisplayType);
	}

	public void setCurrentOrderNumber(int total) {
		this.currentOrderNumber = total;
	}
}
