package com.coinbene.manbiwang.contract.newcontract;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.DLog;
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
import com.coinbene.manbiwang.contract.adapter.EntrustTypeAdapter;
import com.coinbene.manbiwang.contract.dialog.ChooseLevelDialog;
import com.coinbene.manbiwang.contract.dialog.ContractTradeDialog;
import com.coinbene.manbiwang.contract.newcontract.presenter.NewContractInterface;
import com.coinbene.manbiwang.contract.newcontract.presenter.NewContractPresenter;
import com.coinbene.manbiwang.model.contract.CalAvlPositionModel;
import com.coinbene.manbiwang.model.contract.ContractPlaceOrderParmsModel;
import com.coinbene.manbiwang.model.contract.PriceParamsModel;
import com.coinbene.manbiwang.model.http.BottomSelectModel;
import com.coinbene.manbiwang.model.http.ContractAccountInfoModel;
import com.coinbene.manbiwang.model.http.ContractLeverModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContractTradeLayout extends ConstraintLayout implements NewContractInterface.View, View.OnClickListener {
	@BindView(R2.id.rb_open_long)
	RadioButton rbOpenLong;
	@BindView(R2.id.rb_open_short)
	RadioButton rbOpenShort;
	@BindView(R2.id.rg_open)
	RadioGroup rgOpen;
	@BindView(R2.id.tv_fixed_price)
	TextView tvFixedPrice;
	@BindView(R2.id.tv_fixed_price_right)
	ImageView tvFixedPriceRight;
	@BindView(R2.id.ll_fixed_price)
	LinearLayout llFixedPrice;
	@BindView(R2.id.tv_lever)
	TextView tvLever;
	@BindView(R2.id.tv_lever_right)
	ImageView tvLeverRight;
	@BindView(R2.id.ll_lever)
	LinearLayout llLever;
	@BindView(R2.id.ll_parms)
	LinearLayout llParms;
	@BindView(R2.id.tv_high_lever_type)
	TextView tvHighLeverType;
	@BindView(R2.id.iv_hight_lever_right)
	ImageView ivHightLeverRight;
	@BindView(R2.id.rl_high_lever)
	RelativeLayout rlHighLever;
	@BindView(R2.id.edit_price)
	PlusSubInputView editPrice;
	@BindView(R2.id.edit_quantity)
	PlusSubInputView editQuantity;
	@BindView(R2.id.tv_estimated_value)
	TextView tvEstimatedValue;
	@BindView(R2.id.tv_usdt_estimated_value)
	TextView tvUsdtEstimatedValue;

	@BindView(R2.id.tv_plan)
	TextView tvPlan;
	@BindView(R2.id.iv_order_discribe)
	ImageView ivOrderDiscribe;
	@BindView(R2.id.iv_show_hide_plan)
	ImageView ivShowHidePlan;
	@BindView(R2.id.rl_contract_plan_discribe)
	RelativeLayout rlContractPlanDiscribe;
	@BindView(R2.id.et_stop_profit)
	PlusSubInputView etStopProfit;
	@BindView(R2.id.et_stop_loss)
	PlusSubInputView etStopLoss;
	@BindView(R2.id.tv_login)
	TextView tvLogin;
	@BindView(R2.id.ll_no_login)
	LinearLayout llNoLogin;
	@BindView(R2.id.rl_avl)
	RelativeLayout rlAvl;
	@BindView(R2.id.tv_buy)
	TextView tvBuy;
	@BindView(R2.id.tv_avl_long)
	TextView tvAvlLong;
	@BindView(R2.id.tv_sell)
	TextView tvSell;
	@BindView(R2.id.tv_avl_short)
	TextView tvAvlShort;
	@BindView(R2.id.include_trade_place_order)
	ConstraintLayout tradePlaceOrder;
	@BindView(R2.id.include_plan)
	ConstraintLayout includePlan;
	@BindView(R2.id.tv_avl_balance)
	TextView tvAvlBalance;
	@BindView(R2.id.tv_avl_long_number)
	TextView tvAvlLongNumber;
	@BindView(R2.id.tv_avl_short_number)
	TextView tvAvlShortNumber;
	@BindView(R2.id.tv_close_short_number)
	TextView tvCloseShortNumber;
	@BindView(R2.id.tv_close_long_number)
	TextView tvCloseLongNumber;
	@BindView(R2.id.tv_occupy_buy_value)
	TextView tvOccupyBuyValue;
	@BindView(R2.id.tv_occupy_sell_value)
	TextView tvOccupySellValue;

	@BindView(R2.id.rg_percent)
	PercentTradeLayout layoutPercent;
	@BindView(R2.id.ll_bond_sell)
	View llBondSell;
	@BindView(R2.id.ll_bond_buy)
	View llBondBuy;


	private Context mContext;
	public static int INDEX_OPEN = 0;//开仓
	public static int INDEX_CLOSE = 1;//平仓
	private int currentDirection = INDEX_OPEN;
	private TextWatcher priceTextWatcher;
	private NewContractPresenter curPresenter, usdtPresenter, btcPresenter;
	private int curOrderNumber;
	private ChooseLevelDialog chooseLevelDialog;
	private String symbol;
	private int currentLever;
	private BottomSheetDialog fixedPriceDialog;
	private RecyclerView mRecyclerView;
	private EntrustTypeAdapter entrustTypeAdapter;
	private int curFixPriceType = Constants.FIXED_PRICE;//限价 0   市价  1；
	private int curHighLeverEntrust = Constants.ONLY_MAKER;//高级限价委托  默认 只做maker
	private BottomSheetDialog highEntrustDialog;
	private EntrustTypeAdapter highLeverAdapter;
	private PriceParamsModel priceParamsModel = new PriceParamsModel();
	private CalAvlPositionModel calAvlPositionModel = new CalAvlPositionModel();
	private BaseTextWatcher stopProfitTextWatcher, stopLossTextWatcher, numTextWatcher;
	private String[] avlCloseAccount = new String[2];
	private int tradeType;
	private String maxOrderAccount = "10000000";
	private String[] avlOpenAccount = new String[2];
	private ContractTradeDialog tradeDialog;
	private ContractPlaceOrderParmsModel contractPlaceOrderParmsModel;
	private String positionMode = Constants.MODE_FIXED;

	private TradeLayoutLisenter tradeLayoutLisenter;
	private ContractType contractType;
	private BubbleSeekBar.OnProgressChangedListener onProgressChangedListener;


	public void setTradeLayoutLisenter(TradeLayoutLisenter tradeLayoutLisenter) {
		this.tradeLayoutLisenter = tradeLayoutLisenter;
	}

	public ContractTradeLayout(Context context) {
		super(context);
		init(context);
	}


	public ContractTradeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public ContractTradeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {

		initLayout(context);
		initView();
		initLisenter();
	}

	private void initLayout(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_contract_trade, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}
	}

	private void initView() {
		ivOrderDiscribe.setOnClickListener(v -> showContractPlanDiscribe());
		if (SpUtil.getShowContractPlan()) {
			ivShowHidePlan.setBackgroundResource(R.drawable.res_fortune_top);
			etStopLoss.setVisibility(VISIBLE);
			etStopProfit.setVisibility(VISIBLE);
		} else {
			ivShowHidePlan.setBackgroundResource(R.drawable.res_fortune_down);
			etStopLoss.setVisibility(GONE);
			etStopProfit.setVisibility(GONE);
		}
		rgOpen.setOnCheckedChangeListener((group, checkedId) -> {
			if (checkedId == R.id.rb_open_long) {
				currentDirection = INDEX_OPEN;
			} else {
				currentDirection = INDEX_CLOSE;
			}
			calAvlPositionModel.setCurrentDirection(currentDirection);
			changeDirection(currentDirection);
		});
		rbOpenLong.setChecked(true);
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

	public void initInput() {
		tvAvlLongNumber.setText(String.format("%d%s", 0, usdtPresenter.getContractUnit()));
		tvAvlShortNumber.setText(String.format("%d%s", 0, usdtPresenter.getContractUnit()));
		editPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.price), curPresenter.getQouteAsset()));
		editPrice.setMinPriceChange(curPresenter.getMinPriceChange());
		editPrice.setTag(null);
		editQuantity.setHint(String.format("%s(%s)", getResources().getString(R.string.input_num_hint), curPresenter.getContractUnit()));
		editQuantity.setMinPriceChange(curPresenter.getContractMinQuantityChange());
		editQuantity.setText("");
		layoutPercent.setSeekBarStatus(currentDirection);
		etStopProfit.setHint(String.format("%s(%s)", getContext().getString(R.string.stop_profit_price), curPresenter.getQouteAsset()));
		etStopLoss.setHint(String.format("%s(%s)", getContext().getString(R.string.stop_loss_price), curPresenter.getQouteAsset()));
		etStopLoss.setText("");
		etStopProfit.setText("");
		etStopProfit.setMinPriceChange(curPresenter.getMinPriceChange());
		etStopLoss.setMinPriceChange(curPresenter.getMinPriceChange());
		setAvlAccountData();
	}

	private void changeDirection(int currentDirection) {
		if (currentDirection == INDEX_OPEN) {//开仓
			etStopProfit.setEnabled(true);
			etStopLoss.setEnabled(true);
			llLever.setVisibility(VISIBLE);
			tvBuy.setText(R.string.buy_open_long_zh);
			tvSell.setText(R.string.sell_open_short_zh);
			tvAvlLong.setText(R.string.avl_open_long);
			tvAvlShort.setText(R.string.avl_open_short);
			tvCloseShortNumber.setVisibility(GONE);
			tvCloseLongNumber.setVisibility(GONE);
			tvAvlLongNumber.setVisibility(VISIBLE);
			tvAvlShortNumber.setVisibility(VISIBLE);
			llBondBuy.setVisibility(VISIBLE);
			llBondSell.setVisibility(VISIBLE);
			if (curPresenter != null)
				curPresenter.calculationSingerAvlOpen(priceParamsModel, calAvlPositionModel);
			if (SwitchUtils.isRedRise()) {//红涨绿跌
				tvBuy.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
				tvSell.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
			} else {
				tvBuy.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
				tvSell.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
			}
		} else {//平仓
			etStopProfit.setEnabled(false);
			etStopLoss.setEnabled(false);
			llLever.setVisibility(GONE);
			tvBuy.setText(R.string.buy_close_short);
			tvSell.setText(R.string.sell_close_long);
			tvAvlLong.setText(R.string.avl_close);
			tvAvlShort.setText(R.string.avl_close);
			tvCloseShortNumber.setVisibility(VISIBLE);
			tvCloseLongNumber.setVisibility(VISIBLE);
			tvAvlLongNumber.setVisibility(GONE);
			tvAvlShortNumber.setVisibility(GONE);
			llBondBuy.setVisibility(INVISIBLE);
			llBondSell.setVisibility(INVISIBLE);
			if (SwitchUtils.isRedRise()) {//绿涨红跌
				tvBuy.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
				tvSell.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
			} else {
				tvBuy.setBackground(getResources().getDrawable(R.drawable.res_selector_button_green));
				tvSell.setBackground(getResources().getDrawable(R.drawable.res_selector_button_red));
			}
		}
		initTabBg();
		editQuantity.setText("");
		layoutPercent.setSeekBarStatus(currentDirection);
		setLastPrice(priceParamsModel);
		setAvlAccountData();
	}

	private void initTabBg() {
		if (SwitchUtils.isRedRise()) {
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

	private void initLisenter() {
		tvCloseLongNumber.setOnClickListener(this);
		tvCloseShortNumber.setOnClickListener(this);
		tvBuy.setOnClickListener(this);
		tvSell.setOnClickListener(this);
		rlAvl.setOnClickListener(this);
		rlHighLever.setOnClickListener(this);
		llFixedPrice.setOnClickListener(this);
		llLever.setOnClickListener(this);
		rlContractPlanDiscribe.setOnClickListener(this);
		tvLogin.setOnClickListener(this);
		editPrice.removeTextChangedListener(priceTextWatcher);

		priceTextWatcher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				editPrice.removeTextChangedListener(this);
				if (!TextUtils.isEmpty(curPresenter.getMinPriceChange())) {
					PrecisionUtils.setPrecisionMinPriceChange(editPrice.getmEditText(), s, curPresenter.getPricePresition(), curPresenter.getMinPriceChange());
				}
				calAvlPositionModel.setInputPrice(editPrice.getText());

				curPresenter.calculationSingerAvlOpen(priceParamsModel, calAvlPositionModel);
				editPrice.addTextChangedListener(this);
			}

		};
		editPrice.addTextChangedListener(priceTextWatcher);

		etStopProfit.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				etStopProfit.setText(priceParamsModel.getLastPrice());
			}

			@Override
			public void onSub() {
				etStopProfit.setText(priceParamsModel.getLastPrice());

			}
		});
		etStopLoss.setOnPlusSubListener(new PlusSubInputView.OnPlusSubListener() {
			@Override
			public void onPlus() {
				etStopLoss.setText(priceParamsModel.getLastPrice());
			}

			@Override
			public void onSub() {
				etStopLoss.setText(priceParamsModel.getLastPrice());
			}
		});


		etStopProfit.removeTextChangedListener(stopProfitTextWatcher);
		stopProfitTextWatcher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etStopProfit.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etStopProfit.getmEditText(), s, curPresenter.getPricePresition(), curPresenter.getMinPriceChange());
				etStopProfit.addTextChangedListener(this);
			}
		};
		etStopProfit.addTextChangedListener(stopProfitTextWatcher);

		etStopLoss.removeTextChangedListener(stopLossTextWatcher);
		stopLossTextWatcher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				etStopLoss.removeTextChangedListener(this);
				PrecisionUtils.setPrecisionMinPriceChange(etStopLoss.getmEditText(), s, curPresenter.getPricePresition(), curPresenter.getMinPriceChange());
				etStopLoss.addTextChangedListener(this);
			}
		};
		etStopLoss.addTextChangedListener(stopLossTextWatcher);


		onProgressChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
			@Override
			public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
				if (fromUser) {
					layoutPercent.getTvPosition().setText(String.valueOf(progress));
				}
				removeChangeLisenter(null);
				String persent = BigDecimalUtils.divideToStr(String.valueOf(progress), "100", 2);
				if (ServiceRepo.getUserService().isLogin()) {
					editQuantity.setText(currentDirection == Constants.INDEX_OPEN ?
							CalculationUtils.calAvlOpenPersent(persent, curPresenter.getContractUsdtUnitValue(avlOpenAccount[0]), curPresenter.getContractUsdtUnitValue(avlOpenAccount[1]), contractType == ContractType.USDT ? TradeUtils.getContractUsdtPrecision(symbol) : 0) :
							CalculationUtils.calAvlClosePersent(persent, curPresenter.getContractUsdtUnitValue(avlCloseAccount[0]), curPresenter.getContractUsdtUnitValue(avlCloseAccount[1]), contractType == ContractType.USDT ? TradeUtils.getContractUsdtPrecision(symbol) : 0));
				} else {
					editQuantity.setText("");
				}
				curPresenter.calSingerBond(priceParamsModel, calAvlPositionModel, curPresenter.getRealNumber(editQuantity.getText()));
				String[] estimatedValue = curPresenter.getEstimatedValue(priceParamsModel.getLastPrice(), editQuantity);
				tvEstimatedValue.setText(String.format("%s%s%s", estimatedValue[0], estimatedValue[1], estimatedValue[2]));
				if (!TextUtils.isEmpty(estimatedValue[1]) && !TextUtils.isEmpty(estimatedValue[2]) && !"USDT".equals(estimatedValue[2])) {
					tvUsdtEstimatedValue.setText(String.format("(%sUSDT)", TradeUtils.getUsdtEstimated(symbol, estimatedValue[1])));
				} else {
					tvUsdtEstimatedValue.setText("");
				}
				addChangeLisenter(null);
			}

			@Override
			public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

			}

			@Override
			public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

			}
		};
		layoutPercent.getSeekBar().setOnProgressChangedListener(onProgressChangedListener);


		numTextWatcher = new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				super.onTextChanged(s, start, before, count);
				removeChangeLisenter(numTextWatcher);
				String[] estimatedValue = curPresenter.getEstimatedValue(priceParamsModel.getLastPrice(), editQuantity);
				tvEstimatedValue.setText(String.format("%s%s%s", estimatedValue[0], estimatedValue[1], estimatedValue[2]));
				if (!TextUtils.isEmpty(estimatedValue[1]) && !TextUtils.isEmpty(estimatedValue[2]) && !"USDT".equals(estimatedValue[2])) {
					tvUsdtEstimatedValue.setText(String.format("(%sUSDT)", TradeUtils.getUsdtEstimated(symbol, estimatedValue[1])));
				} else {
					tvUsdtEstimatedValue.setText("");
				}
				curPresenter.calSingerBond(priceParamsModel, calAvlPositionModel, curPresenter.getRealNumber(editQuantity.getText()));
				layoutPercent.setSeekBarValue(currentDirection == Constants.INDEX_OPEN ?
						CalculationUtils.calPersentOpenFromQutity(editQuantity.getText(), curPresenter.getContractUsdtUnitValue(avlOpenAccount[0]), curPresenter.getContractUsdtUnitValue(avlOpenAccount[1])) :
						CalculationUtils.calPersentCloseFromQutity(editQuantity.getText(), curPresenter.getContractUsdtUnitValue(avlCloseAccount[0]), curPresenter.getContractUsdtUnitValue(avlCloseAccount[1])));
				addChangeLisenter(numTextWatcher);
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				curPresenter.setQuantityPresition(editQuantity);

				DLog.e("editQuantity", "afterTextChanged" + editQuantity.getText());
			}
		};
		editQuantity.addTextChangedListener(numTextWatcher);
	}

	private void addChangeLisenter(BaseTextWatcher textWatcher) {
		if (textWatcher == numTextWatcher) {
			layoutPercent.getSeekBar().setOnProgressChangedListener(onProgressChangedListener);
		} else {
			editQuantity.addTextChangedListener(numTextWatcher);
		}

	}

	private void removeChangeLisenter(BaseTextWatcher textWatcher) {
		if (textWatcher == numTextWatcher) {
			layoutPercent.getSeekBar().setOnProgressChangedListener(null);
		} else {
			editQuantity.clearTextChangedListeners();
		}
	}


	public void initPresenter() {
		if (usdtPresenter == null) {
			usdtPresenter = new NewContractPresenter(this, (Activity) getContext());
		}
		if (btcPresenter == null) {
			btcPresenter = new NewContractPresenter(this, (Activity) getContext());
		}
	}

	public void setSymbol(String currentSymbol) {
		clearData();
		this.symbol = currentSymbol;
		unSubAll();

		contractType = TradeUtils.getContractType(symbol);
		if (contractType == ContractType.USDT) {
			curPresenter = usdtPresenter;
		} else {
			curPresenter = btcPresenter;
		}
		curPresenter.setSymbol(currentSymbol);
		subAll();

		setCurrentLever();
		initInput();

	}

	private void setCurrentLever() {
		this.currentLever = curPresenter.getCecheLever();
		setCurLeverData(currentLever);
	}


	public int getCurrentLever() {
		return currentLever;
	}

	private void clearData() {
		curOrderNumber = 0;
		calAvlPositionModel.setAvlBalance("");
		calAvlPositionModel.setInputPrice("");
		priceParamsModel.setLastPrice("");
		priceParamsModel.setSymbol("");
		priceParamsModel.setBuyOnePrice("");
		priceParamsModel.setSellOnePrice("");
		priceParamsModel.setMarkPrice("");
		avlCloseAccount[0] = "0";
		avlCloseAccount[1] = "0";
		avlOpenAccount[0] = "0";
		avlOpenAccount[1] = "0";
		if (curPresenter != null)
			curPresenter.cancelAll();
	}


	@Override
	public void setPresenter(NewContractInterface.Presenter presenter) {
	}


	@Override
	public void setMarginMode(ContractLeverModel.DataBean dataBean) {
		if (tradeLayoutLisenter != null) {
			tradeLayoutLisenter.onMarginModeChange(dataBean);
		}
		this.positionMode = dataBean.getMarginModeSetting();
	}

	@Override
	public void setContractAccountInfo(ContractAccountInfoModel.DataBean data) {
		calAvlPositionModel.setAvlBalance(data.getAvailableBalance());
		this.avlCloseAccount[0] = data.getLongQuantity();
		this.avlCloseAccount[1] = data.getShortQuantity();
		setAvlAccountData();
		curPresenter.calculationSingerAvlOpen(priceParamsModel, calAvlPositionModel);
		curPresenter.calSingerBond(priceParamsModel,calAvlPositionModel,editQuantity.getText());
	}

	private void setAvlAccountData() {
		if (curPresenter == null) {
			return;
		}
		if (ServiceRepo.getUserService().isLogin()) {
			tvAvlBalance.setText(String.format("%s %s %s", getResources().getString(R.string.able_label), calAvlPositionModel.getAvlBalance(), TradeUtils.isUsdtContract(symbol) ? "USDT" : "BTC"));
			if (currentDirection == Constants.INDEX_OPEN) {
				tvAvlLongNumber.setText(String.format("%s%s", curPresenter.getContractUsdtUnitValue(avlOpenAccount[0]), curPresenter.getContractUnit()));
				tvAvlShortNumber.setText(String.format("%s%s", curPresenter.getContractUsdtUnitValue(avlOpenAccount[1]), curPresenter.getContractUnit()));
			} else {
				tvCloseShortNumber.setText(String.format("%s%s", curPresenter.getContractUsdtUnitValue(avlCloseAccount[1]), curPresenter.getContractUnit()));
				tvCloseLongNumber.setText(String.format("%s%s", curPresenter.getContractUsdtUnitValue(avlCloseAccount[0]), curPresenter.getContractUnit()));
			}
		} else {
			if (currentDirection == Constants.INDEX_OPEN) {
				tvAvlLongNumber.setText(String.format("%d%s", 0, curPresenter.getContractUnit()));
				tvAvlShortNumber.setText(String.format("%d%s", 0, curPresenter.getContractUnit()));
			} else {
				tvCloseShortNumber.setText(String.format("%d%s", 0, curPresenter.getContractUnit()));
				tvCloseLongNumber.setText(String.format("%d%s", 0, curPresenter.getContractUnit()));
			}
			tvAvlBalance.setText(String.format("%s %s %s", getResources().getString(R.string.able_label), "0", TradeUtils.isUsdtContract(symbol) ? "USDT" : "BTC"));

		}
	}

	@Override
	public void setCurrentOrderData(int total) {
		this.curOrderNumber = total;
		if (tradeLayoutLisenter != null) {
			tradeLayoutLisenter.setCurOrderListSize(total);
		}

	}

	@Override
	public void setAvlOpenAccout(String[] avlOpenAccount) {
		tvAvlLong.post(() -> {
			this.avlOpenAccount = avlOpenAccount;
			setAvlAccountData();
		});
	}

	@Override
	public void setPisitionNumber(int size) {
		if (tradeLayoutLisenter != null) {
			tradeLayoutLisenter.holdPositionChange(size);
		}
	}

	@Override
	public void placeOrderSucces() {
		editQuantity.setText("");
		etStopLoss.setText("");
		etStopProfit.setText("");

	}

	@Override
	public void updateModeSuccess(String mode) {
		if (tradeLayoutLisenter != null) {
			tradeLayoutLisenter.onMarginModeUpdate(mode);
		}
		this.positionMode = mode;

	}

	@Override
	public void setBond(String[] strings) {
		post(() -> {
			tvOccupyBuyValue.setText(strings[0]);
			tvOccupySellValue.setText(strings[1]);
		});


	}


	public void setUserState() {
		if (ServiceRepo.getUserService().isLogin()) {
			llNoLogin.setVisibility(GONE);
			tradePlaceOrder.setVisibility(VISIBLE);
		} else {
			llNoLogin.setVisibility(VISIBLE);
			tradePlaceOrder.setVisibility(GONE);
		}
		/**
		 * 重新刷新红涨绿跌
		 */
		changeDirection(currentDirection);

	}

	public void getAllData() {
		if (ServiceRepo.getUserService().isLogin()) {
			curPresenter.getAllData();
		}
	}


	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.tv_buy) {
			if (currentDirection == INDEX_OPEN) {// 买入开多
				tradeType = Constants.TRADE_OPEN_LONG;
			} else {//买入平空
				tradeType = Constants.TRADE_CLOSE_SHORT;
			}

			checkPlaceOrder();
		} else if (v.getId() == R.id.tv_sell) {
			if (currentDirection == INDEX_OPEN) {
				tradeType = Constants.TRADE_OPEN_SHORT;
			} else {
				tradeType = Constants.TRADE_CLOSE_LONG;
			}
			checkPlaceOrder();
		} else if (v.getId() == R.id.tv_login) {
			gotoLoginOrLock();
		} else if (v.getId() == R.id.rl_contract_plan_discribe) {
			if (etStopProfit.getVisibility() == VISIBLE) {
				etStopProfit.setVisibility(GONE);
				etStopLoss.setVisibility(GONE);
				ivShowHidePlan.setBackgroundResource(R.drawable.res_fortune_down);
				if (tradeLayoutLisenter != null) {
					tradeLayoutLisenter.showHideContractPlan(false);
				}
			} else {
				ivShowHidePlan.setBackgroundResource(R.drawable.res_fortune_top);
				etStopLoss.setVisibility(VISIBLE);
				etStopProfit.setVisibility(VISIBLE);
				if (tradeLayoutLisenter != null) {
					tradeLayoutLisenter.showHideContractPlan(true);
				}
			}

		} else if (v.getId() == R.id.ll_lever) {
//			if (curOrderNumber > 0) {
//				ToastUtil.show(R.string.res_not_support_change_level);
//				return;
//			}
			showLeverDialog(tvLever.getText().toString());
		} else if (v.getId() == R.id.ll_fixed_price) {
			showFixPriceDialog();
		} else if (v.getId() == R.id.rl_high_lever) {
			showHighEntrustDialog();
		} else if (v.getId() == R.id.rl_avl) {
			doTransFer(rlAvl);
		} else if (v.getId() == R.id.tv_close_long_number) {
			if (!BigDecimalUtils.isEmptyOrZero(avlCloseAccount[0]))
				editQuantity.setText(curPresenter.getContractUsdtUnitValue(avlCloseAccount[0]));
		} else if (v.getId() == R.id.tv_close_short_number) {
			if (!BigDecimalUtils.isEmptyOrZero(avlCloseAccount[1]))
				editQuantity.setText(curPresenter.getContractUsdtUnitValue(avlCloseAccount[1]));

		}
	}


	private void checkPlaceOrder() {


		if (!curPresenter.checkProtocalStatus(tvBuy)) {
			return;
		}
		//限价的时候需要判断输入的价格是不是空或者是不是0
		if (this.curFixPriceType == Constants.FIXED_PRICE && BigDecimalUtils.isEmptyOrZero(editPrice.getText())) {
			ToastUtil.show(R.string.please_input_coin_price_tip);
			return;
		}

		if (BigDecimalUtils.isEmptyOrZero(editQuantity.getText())) {
			ToastUtil.show(R.string.please_input_coin_num_tip);
			return;
		}
		String realNumber = curPresenter.getRealNumber(editQuantity.getText());


		//小于最小下单量
		if (BigDecimalUtils.isEmptyOrZero(realNumber)) {
			ToastUtil.show(String.format("%s%s", getResources().getString(R.string.usdt_contract_min_place_order), curPresenter.getContractMinQuantityChange()));
			return;
		}

		//如果是开多  需要检查一下 可开张数够不够
		if (tradeType == Constants.TRADE_OPEN_LONG) {

			if (BigDecimalUtils.isGreaterThan(realNumber, maxOrderAccount)) {
				ToastUtil.show(R.string.max_order_account);
				return;

			}
			if (BigDecimalUtils.isEmptyOrZero(avlOpenAccount[0]) || BigDecimalUtils.isGreaterThan(realNumber, String.valueOf(avlOpenAccount[0]))) {
				ToastUtil.show(R.string.available_balance_Insufficient);
				return;
			}
			if (curFixPriceType == Constants.FIXED_PRICE) {
				if (!BigDecimalUtils.isEmptyOrZero(etStopProfit.getText()) && BigDecimalUtils.isLessThan(etStopProfit.getText(), editPrice.getText())) {
					ToastUtil.show(R.string.profit_not_less_price);
					return;
				}
				if (!BigDecimalUtils.isEmptyOrZero(etStopLoss.getText()) && BigDecimalUtils.isGreaterThan(etStopLoss.getText(), editPrice.getText())) {
					ToastUtil.show(R.string.less_not_than_price);
					return;
				}
			}
		}
		//如果是开空  需要检查一下 可开张数够不够
		if (tradeType == Constants.TRADE_OPEN_SHORT) {
			if (BigDecimalUtils.isGreaterThan(realNumber, maxOrderAccount)) {
				ToastUtil.show(R.string.max_order_account);
				return;

			}
			if (BigDecimalUtils.isEmptyOrZero(avlOpenAccount[1]) || BigDecimalUtils.isGreaterThan(realNumber, String.valueOf(avlOpenAccount[1]))) {
				ToastUtil.show(R.string.available_balance_Insufficient);
				return;
			}
			if (curFixPriceType == Constants.FIXED_PRICE) {
				if (!BigDecimalUtils.isEmptyOrZero(etStopProfit.getText()) && BigDecimalUtils.isGreaterThan(etStopProfit.getText(), editPrice.getText())) {
					ToastUtil.show(R.string.progit_not_than_price);
					return;
				}
				if (!BigDecimalUtils.isEmptyOrZero(etStopLoss.getText()) && BigDecimalUtils.isLessThan(etStopLoss.getText(), editPrice.getText())) {
					ToastUtil.show(R.string.loss_not_less_price);
					return;
				}
			}

		}
		//如果是平多  需要检查一下 可平张数够不够
		if (tradeType == Constants.TRADE_CLOSE_LONG) {
			if (BigDecimalUtils.isEmptyOrZero(avlCloseAccount[0]) || BigDecimalUtils.isGreaterThan(realNumber, avlCloseAccount[0])) {
				ToastUtil.show(R.string.close_amount_insufficient_tip);
				return;
			}
		}
		//如果是平空 需要检查一下 可平张数够不够
		if (tradeType == Constants.TRADE_CLOSE_SHORT) {
			if (BigDecimalUtils.isEmptyOrZero(avlCloseAccount[1]) || BigDecimalUtils.isGreaterThan(realNumber, avlCloseAccount[1])) {
				ToastUtil.show(R.string.close_amount_insufficient_tip);
				return;
			}
		}


		if (contractPlaceOrderParmsModel == null) {
			contractPlaceOrderParmsModel = new ContractPlaceOrderParmsModel();
		}
		contractPlaceOrderParmsModel.setEstimatedValue((contractType == ContractType.USDT && SpUtil.getContractUsdtUnitSwitch() == 1) ? "" : tvEstimatedValue.getText().toString());
		contractPlaceOrderParmsModel.setLever(currentLever);
		contractPlaceOrderParmsModel.setLossPrice(etStopLoss.getText());
		contractPlaceOrderParmsModel.setRealNumber(realNumber);
		contractPlaceOrderParmsModel.setNumber(curPresenter.getPlaceOrderQuantity(editQuantity.getText()));
		contractPlaceOrderParmsModel.setTradeType(tradeType);
		contractPlaceOrderParmsModel.setPrice(editPrice.getText());
		contractPlaceOrderParmsModel.setProfitPrice(etStopProfit.getText());
		contractPlaceOrderParmsModel.setSymbol(symbol);
		contractPlaceOrderParmsModel.setOrderType(curFixPriceType);
		contractPlaceOrderParmsModel.setHighLeverOrderType(curHighLeverEntrust);
		contractPlaceOrderParmsModel.setMarginMode(positionMode);
		contractPlaceOrderParmsModel.setUnit(contractType == ContractType.USDT ? TradeUtils.getContractUsdtUnit(symbol) : getContext().getString(R.string.number));
		if (SpUtil.isContractSureDialogNotShow()) {
			curPresenter.doPlaceOrder(contractPlaceOrderParmsModel);
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

					curPresenter.doPlaceOrder(orderModel);
				}
			});
		}
		tradeDialog.buildPrams(contractPlaceOrderParmsModel).show();
	}


	@NeedLogin(jump = true)
	private void doTransFer(View v) {
		curPresenter.doTransFer(v);
	}

	/**
	 * 高级委托切换弹窗
	 */
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
	 * 点击委托类型切换弹窗
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
				calAvlPositionModel.setCurOrderType(curFixPriceType);
				calAvlPositionModel.setInputPrice(editPrice.getText());
				curPresenter.calculationAvlOpen(priceParamsModel, calAvlPositionModel);
				curPresenter.calSingerBond(priceParamsModel, calAvlPositionModel, editQuantity.getText());
			});

			tvCancel.setOnClickListener(v -> fixedPriceDialog.cancel());
		} else {
			entrustTypeAdapter.setCurrentSelect(tvFixedPrice.getText().toString());
			entrustTypeAdapter.notifyDataSetChanged();
		}
		fixedPriceDialog.show();

	}

	/**
	 * 不同委托类型设置
	 */
	private void setPriceTypeView() {
		if (curFixPriceType == Constants.FIXED_PRICE) {
			if (tradeLayoutLisenter != null) {
				tradeLayoutLisenter.showHideContractHighLever(false);
			}
			includePlan.setVisibility(VISIBLE);
			rlHighLever.setVisibility(GONE);
			editPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.price), curPresenter.getQouteAsset()));
			editPrice.setEnablePlusSub(true);
			editPrice.setEnabled(true);
			setLastPrice(priceParamsModel);
		} else if (curFixPriceType == Constants.MARKET_PRICE) {
			if (tradeLayoutLisenter != null) {
				tradeLayoutLisenter.showHideContractHighLever(false);
			}
			includePlan.setVisibility(VISIBLE);
			editPrice.setEnablePlusSub(false);
			rlHighLever.setVisibility(GONE);
			editPrice.setEnabled(false);
			editPrice.setText("");
			editPrice.setHint(R.string.market_price);
		} else if (curFixPriceType == Constants.FIXED_PRICE_HIGH_LEVER) {
			includePlan.setVisibility(GONE);
			if (tradeLayoutLisenter != null) {
				tradeLayoutLisenter.showHideContractHighLever(true);
			}
			editPrice.setEnablePlusSub(true);
			rlHighLever.setVisibility(VISIBLE);
			editPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.price), curPresenter.getQouteAsset()));
			editPrice.setEnabled(true);
			setLastPrice(priceParamsModel);
		}
	}

	/**
	 * 点击杠杆选择框
	 */
	private void showLeverDialog(String lever) {
		if (chooseLevelDialog == null) {
			chooseLevelDialog = new ChooseLevelDialog(getContext());
		}
		chooseLevelDialog.initDialog(symbol, lever);
		chooseLevelDialog.showWithCallBack(leverage -> {
			if (currentLever == leverage) {
				return;
			}
			currentLever = leverage;
			curPresenter.updateContractLever(leverage);
//			ContractUsdtInfoController.getInstance().updataContractLever(symbol, leverage);
			tvLever.setText(String.format("%dX", currentLever));
			calAvlPositionModel.setCurLever(currentLever);
			calAvlPositionModel.setInputPrice(editPrice.getText());
			curPresenter.calculationAvlOpen(priceParamsModel, calAvlPositionModel);
			curPresenter.calSingerBond(priceParamsModel, calAvlPositionModel, editQuantity.getText());
		});
	}

	@Override
	public void setCurLeverData(int lever) {
		if (lever == 0) {
			currentLever = curPresenter.getCecheLever();
			tvLever.setText(String.format("%dX", currentLever));
		} else {
			currentLever = lever;
			curPresenter.updateContractLever(lever);
			tvLever.setText(String.format("%dX", lever));
		}
		calAvlPositionModel.setCurLever(currentLever);
		calAvlPositionModel.setInputPrice(editPrice.getText());
		curPresenter.calculationAvlOpen(priceParamsModel, calAvlPositionModel);
		curPresenter.calSingerBond(priceParamsModel,calAvlPositionModel,editQuantity.getText());
	}

	public void setPriceParams(PriceParamsModel priceParamsModel) {
		this.priceParamsModel = priceParamsModel;
		curPresenter.getAccountInfo();
		curPresenter.getPositionlist();
		calAvlPositionModel.setInputPrice(editPrice.getText());
		curPresenter.calculationAvlOpen(priceParamsModel, calAvlPositionModel);
		curPresenter.calBond(priceParamsModel,calAvlPositionModel,editQuantity.getText());
		setInputLastPrice(priceParamsModel);
	}


	/**
	 * 判断是否需要显示最新价
	 *
	 * @param priceParamsModel
	 */
	public void setInputLastPrice(PriceParamsModel priceParamsModel) {
		if (TextUtils.isEmpty(priceParamsModel.getLastPrice())) {
			return;
		}
		if (TextUtils.isEmpty((String) editPrice.getTag()) || !(editPrice.getTag()).equals(priceParamsModel.getSymbol())) {
			setLastPrice(priceParamsModel);
		}
	}

	/**
	 * 显示最新价
	 *
	 * @return
	 */
	public void setLastPrice(PriceParamsModel priceParamsModel) {
		if (!isMarketOrderType()) {
			editPrice.setTag(priceParamsModel.getSymbol());
			editPrice.setText(priceParamsModel.getLastPrice());
		}
	}

	private boolean isMarketOrderType() {
		return curFixPriceType == Constants.MARKET_PRICE;
	}


	public void subAll() {
		if (curPresenter != null) {
			if (curPresenter == usdtPresenter) {
				btcPresenter.unSubAll();
			} else {
				usdtPresenter.unSubAll();
			}
			curPresenter.subAll();
		}
	}

	public void unSubAll() {
		if (curPresenter != null)
			curPresenter.unSubAll();
	}

	public void setClickPrice(String price) {
		if (!isMarketOrderType()) {
			editPrice.setText(price);
		}
	}

	public void onDestory() {
		if (curPresenter != null)
			curPresenter.onDestory();
		if (usdtPresenter != null)
			usdtPresenter.onDestory();
		if (btcPresenter != null)
			btcPresenter.onDestory();

		mContext = null;
	}

	public void updatePositionMode(String mode) {
		if (curPresenter != null) {
			curPresenter.updateMarginMode(mode);
		}
	}


	public interface TradeLayoutLisenter {
		void showHideContractPlan(boolean showContractType);

		void showHideContractHighLever(boolean showHide);

		void holdPositionChange(int number);

		void setCurOrderListSize(int total);

		void onMarginModeChange(ContractLeverModel.DataBean dataBean);

		void onMarginModeUpdate(String mode);
	}
}
