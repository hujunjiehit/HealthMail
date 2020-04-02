package com.coinbene.manbiwang.spot.tradelayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.BalanceInfoTable_;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.SelectorDialog;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.CalculationUtils;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.userevent.BaseUserEventListener;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.common.widget.BaseTextWatcher;
import com.coinbene.common.widget.input.PlusSubInputView;
import com.coinbene.common.widget.seekbar.BubbleSeekBar;
import com.coinbene.manbiwang.model.http.BottomSelectModel;
import com.coinbene.manbiwang.model.http.MarginSingleAccountModel;
import com.coinbene.manbiwang.model.http.SpotPlaceOrderModel;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.spot.message.BuyOrSellPriceOneMessage;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.margin.MarginFragment;
import com.coinbene.manbiwang.spot.margin.MarginTradeLayoutPresenter;
import com.coinbene.manbiwang.spot.orderlayout.listener.OrderBookListener;
import com.coinbene.manbiwang.spot.spot.SpotTradeLayoutPresenter;
import com.coinbene.manbiwang.spot.tradelayout.impl.FixedLossStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.FixedOcoStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.FixedTradeStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.MarketLossStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.MarketOcoStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.MarketTradeStrategy;
import com.coinbene.manbiwang.spot.tradelayout.impl.TradeLayoutInterface;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.android.AndroidScheduler;
import io.objectbox.reactive.DataObserver;
import io.objectbox.reactive.DataSubscriptionList;

import static com.coinbene.common.Constants.FIXED_PRICE_OCO;
import static com.coinbene.common.Constants.MARKET_PRICE_OCO;
import static com.coinbene.common.Constants.TAB_BUY;
import static com.coinbene.common.Constants.TAB_SELL;

/**
 * Created by june
 * on 2019-12-02
 */
public class TradeLayout extends LinearLayout implements TradeLayoutInterface.View, OrderBookListener {

	@BindView(R2.id.tv_tab_buy)
	TextView mTvTabBuy;
	@BindView(R2.id.tv_tab_sell)
	TextView mTvTabSell;
	@BindView(R2.id.tv_fixed_price)
	TextView mTvFixedPrice;
	@BindView(R2.id.tv_fixed_price_right)
	ImageView mTvFixedPriceRight;
	@BindView(R2.id.ll_root)
	LinearLayout llRoot;
	@BindView(R2.id.ll_fixed_price)
	RelativeLayout mLlFixedPrice;
	@BindView(R2.id.et_unit_price)
	PlusSubInputView mEtUnitPrice;
	@BindView(R2.id.tv_price_coin)
	TextView mTvPriceCoin;
	@BindView(R2.id.rl_unit_price)
	RelativeLayout mRlUnitPrice;
	@BindView(R2.id.tv_local_price)
	TextView mTvLocalPrice;
	@BindView(R2.id.et_quantity)
	PlusSubInputView mEtQuantity;
	@BindView(R2.id.tv_quantity_coin)
	TextView mTvQuantityCoin;
	@BindView(R2.id.rl_number)
	RelativeLayout mRlNumber;
	@BindView(R2.id.tv_avl)
	TextView mTvAvl;
	@BindView(R2.id.tv_avl_value)
	TextView mTvAvlValue;
	@BindView(R2.id.ll_market_avl)
	LinearLayout mLlMarketAvl;
	@BindView(R2.id.seek_bar)
	BubbleSeekBar mSeekBar;
	@BindView(R2.id.tv_position)
	TextView mTvPosition;
	@BindView(R2.id.et_amount)
	PlusSubInputView mEtAmount;
	@BindView(R2.id.tv_amount_coin)
	TextView mTvAmountCoin;
	@BindView(R2.id.rl_amount)
	RelativeLayout mRlAmount;
	@BindView(R2.id.tv_market_field)
	TextView mTvMarketField;
	@BindView(R2.id.tv_market_field_value)
	TextView mTvMarketFieldValue;
	@BindView(R2.id.ll_market_field)
	LinearLayout mLlMarketField;
	@BindView(R2.id.tv_place_order)
	TextView mTvPlaceOrder;
	@BindView(R2.id.rl_price)
	RelativeLayout mRlPrice;
	@BindView(R2.id.tv_coin)
	TextView mTvCoin;
	@BindView(R2.id.et_price)
	PlusSubInputView mEtPrice;

	@BindView(R2.id.rl_touch_price)
	RelativeLayout mRlTouchPrice;
	@BindView(R2.id.tv_touch_coin)
	TextView mTvTouchCoin;
	@BindView(R2.id.et_touch_price)
	PlusSubInputView mEtTouchPrice;
	@BindView(R2.id.iv_order_type)
	ImageView mIvOrderType;
	@BindView(R2.id.ll_order_type)
	LinearLayout llOrderType;


	private Context mContext;
	private DataSubscriptionList subscriptions;

	private int curTab = TAB_BUY;

	private int curFixPriceType = Constants.FIXED_PRICE;

	private boolean isMargin = false;
	private TradeLayoutInterface.Presenter mPresenter;

	private String symbol;

	private String qouteAsset;
	private String baseAsset;

	private String buyBalance = "0";
	private String sellBalance = "0";

	private String lastPrice;
	private String rate;
	private String currency;

	private String sellPriceOne = "";
	private String buyPriceOne = "";

	private TextWatcher quantityTextWatcher, unitPriceTextWacher, amountTextWacher, touchPriceTextWacher, priceTextWacher;
	private BubbleSeekBar.OnProgressChangedListener progressChangedListener;
	private SelectorDialog selectorDialog;

	private Fragment mParentFragment;
	private AbsTradeStrategy tradeStrategy;

	private FixedTradeStrategy fixedTradeStrategy;
	private MarketTradeStrategy marketTradeStrategy;
	private FixedLossStrategy fixedLossStrategy;
	private MarketLossStrategy marketLossStrategy;
	private FixedOcoStrategy fixedOcoStrategy;
	private MarketOcoStrategy marketOcoStrategy;

	private BaseUserEventListener userEventListener;

	public TradeLayout(Context context) {
		super(context);
		initView(context);
	}

	public TradeLayout(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TradeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.spot_new_trade_layout, this, true);
		ButterKnife.bind(this);

		if (isInEditMode()) {
			return;
		}

		if (subscriptions == null) {
			subscriptions = new DataSubscriptionList();
		}

		initViewByDirection(TAB_BUY);

		if (fixedTradeStrategy == null) {
			fixedTradeStrategy = new FixedTradeStrategy();
		}
		tradeStrategy = fixedTradeStrategy;

		initListener();
	}


	private void initListener() {
		mIvOrderType.setOnClickListener(v -> showOrderDiscribe());

		mTvPlaceOrder.setOnClickListener(v -> placeOrder());

		mTvTabBuy.setOnClickListener(v -> changeTab(TAB_BUY));

		tradeStrategy.setBuy(isBuyTab());

		mTvTabSell.setOnClickListener(v -> changeTab(TAB_SELL));

		initTextChangeListener();

		llOrderType.setOnClickListener(v -> {
			if (selectorDialog == null) {
				List<BottomSelectModel> datas = new ArrayList<>();
				datas.add(new BottomSelectModel(getContext().getString(R.string.fixed_price_entrust), Constants.FIXED_PRICE));
				datas.add(new BottomSelectModel(getContext().getString(R.string.market_price_entrust), Constants.MARKET_PRICE));
				datas.add(new BottomSelectModel(getContext().getString(R.string.fixed_price_stop_loss_and_stop_loss), Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS));
				datas.add(new BottomSelectModel(getContext().getString(R.string.market_price_stop_loss_and_sop_loss), Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS));
				datas.add(new BottomSelectModel(getContext().getString(R.string.fixed_price_oco), Constants.FIXED_PRICE_OCO));
				datas.add(new BottomSelectModel(getContext().getString(R.string.market_price_oco), Constants.MARKET_PRICE_OCO));

				selectorDialog = DialogManager.getSelectorDialog(getContext());
				selectorDialog.setDatas(datas);
				selectorDialog.setSelectListener(new SelectorDialog.SelectListener() {
					@Override
					public void onItemSelected(Object data, int positon) {
						if (data instanceof BottomSelectModel) {
							BottomSelectModel item = (BottomSelectModel) data;
							selectorDialog.cancel();
							if (curFixPriceType == item.getType()) {
								return;
							}
							curFixPriceType = item.getType();
							mTvFixedPrice.setText(item.getTypeName());
							setPriceTypeView();
						}
					}
				});
			}

			selectorDialog.setDefaultData(mTvFixedPrice.getText().toString());
			selectorDialog.show();
		});
	}

	private void showOrderDiscribe() {
		tradeStrategy.showDiscribe(getContext());
	}


	/**
	 * 设置当前币对对应数据
	 *
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		if (!symbol.contains("/")) {
			TradePairInfoTable table = TradePairInfoController.getInstance().queryDataByTradePair(symbol);
			symbol = table.tradePairName;
		}

		mPresenter.setSymbol(symbol);

		this.symbol = symbol;

		parseTradePairName();

		initData();

		if (!isMargin) {
			if (subscriptions != null && !subscriptions.isCanceled()) {
				subscriptions.cancel();
			}
			setBalanceObserver();
		}
		setPriceTypeView();

		setPlusAddMinChange();

		mEtTouchPrice.setText("");
		mEtPrice.setText("");

		if (ServiceRepo.getUserService().isLogin() && mPresenter != null) {
			mPresenter.getBanlance();
		}
	}

	private void setPlusAddMinChange() {
		mEtPrice.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getPricePrecision()));
		mEtTouchPrice.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getPricePrecision()));
		mEtUnitPrice.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getPricePrecision()));
		mEtAmount.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getPricePrecision()));
	}

	private void initStrategy() {
		tradeStrategy.initStrategy(mPresenter.getTakeFee(), mPresenter.getPricePrecision(), mPresenter.getVolumePrecision(), baseAsset, qouteAsset, mPresenter.getMinVolume(), mPresenter.getPriceChangeScale());
		tradeStrategy.setAvlBuyBalance(buyBalance);
		tradeStrategy.setAvlSellBalance(sellBalance);
		tradeStrategy.setBuy(isBuyTab());
		tradeStrategy.setBuyPriceOne(buyPriceOne);
		tradeStrategy.setSellPriceOne(sellPriceOne);
		tradeStrategy.setLastPrice(lastPrice);
		tradeStrategy.setRate(rate);
	}


	/**
	 * 选择市价或者限价后变化
	 */
	private void setPriceTypeView() {
		if (curFixPriceType == Constants.FIXED_PRICE) {
			if (fixedTradeStrategy == null) {
				fixedTradeStrategy = new FixedTradeStrategy();
			}
			tradeStrategy = fixedTradeStrategy;
			mRlTouchPrice.setVisibility(GONE);
			mTvLocalPrice.setVisibility(VISIBLE);
			mEtUnitPrice.setEnablePlusSub(true);
			mEtUnitPrice.setEnabled(true);
			mEtUnitPrice.setHint(R.string.price);
			mRlPrice.setVisibility(GONE);
			setFirstPrice();
			mEtUnitPrice.setEnabled(true);
			mRlAmount.setVisibility(VISIBLE);
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.price), qouteAsset));
			mEtQuantity.setText("");
		} else if (curFixPriceType == Constants.MARKET_PRICE) {
			if (marketTradeStrategy == null) {
				marketTradeStrategy = new MarketTradeStrategy();
			}
			tradeStrategy = marketTradeStrategy;
			mRlTouchPrice.setVisibility(GONE);
			mTvLocalPrice.setVisibility(GONE);
			mEtUnitPrice.setEnablePlusSub(false);
			mEtUnitPrice.setEnabled(false);
			mRlPrice.setVisibility(GONE);
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.market_price), qouteAsset));
			mEtUnitPrice.setText("");
			mRlAmount.setVisibility(GONE);
			mEtQuantity.setText("");
			mTvLocalPrice.setText("");
		} else if (curFixPriceType == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS) {
			if (fixedLossStrategy == null) {
				fixedLossStrategy = new FixedLossStrategy();
			}
			tradeStrategy = fixedLossStrategy;
			mRlTouchPrice.setVisibility(VISIBLE);
			mTvLocalPrice.setVisibility(GONE);
			mRlPrice.setVisibility(GONE);
			mEtUnitPrice.setEnablePlusSub(true);
			mEtUnitPrice.setEnabled(true);
			mEtUnitPrice.setText("");
			mRlAmount.setVisibility(VISIBLE);
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.entrust_price), qouteAsset));
			mEtTouchPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.touch_price), qouteAsset));
			mEtQuantity.setText("");
			setFirstPrice();
		} else if (curFixPriceType == Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS) {
			if (marketLossStrategy == null) {
				marketLossStrategy = new MarketLossStrategy();
			}
			tradeStrategy = marketLossStrategy;


			mRlTouchPrice.setVisibility(VISIBLE);
			mRlPrice.setVisibility(GONE);
			mTvLocalPrice.setVisibility(GONE);
			mEtUnitPrice.setEnablePlusSub(false);
			mEtUnitPrice.setEnabled(false);
			mEtTouchPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.touch_price), qouteAsset));
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.market_price), qouteAsset));
			mEtUnitPrice.setText("");
			mRlAmount.setVisibility(GONE);
			mEtQuantity.setText("");
			mTvLocalPrice.setText("");
		} else if (curFixPriceType == Constants.FIXED_PRICE_OCO) {

			if (fixedOcoStrategy == null) {
				fixedOcoStrategy = new FixedOcoStrategy();
			}
			tradeStrategy = fixedOcoStrategy;

			mRlTouchPrice.setVisibility(VISIBLE);
			mTvLocalPrice.setVisibility(GONE);
			mRlPrice.setVisibility(VISIBLE);
			mEtUnitPrice.setEnablePlusSub(true);
			mEtUnitPrice.setEnabled(true);
			mEtUnitPrice.setText("");
			mRlAmount.setVisibility(VISIBLE);
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.entrust_price), qouteAsset));
			mEtTouchPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.touch_price), qouteAsset));
			mEtPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.fixed_price), qouteAsset));
			mEtQuantity.setText("");
			setFirstPrice();
		} else if (curFixPriceType == Constants.MARKET_PRICE_OCO) {
			if (marketOcoStrategy == null) {
				marketOcoStrategy = new MarketOcoStrategy();
			}
			tradeStrategy = marketOcoStrategy;
			mRlTouchPrice.setVisibility(VISIBLE);
			mTvLocalPrice.setVisibility(GONE);
			mRlPrice.setVisibility(VISIBLE);
			mEtUnitPrice.setEnablePlusSub(false);
			mEtUnitPrice.setEnabled(false);
			mEtUnitPrice.setText("");
			mRlAmount.setVisibility(VISIBLE);
			mEtTouchPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.touch_price), qouteAsset));
			mEtUnitPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.market_price), qouteAsset));
			mEtPrice.setHint(String.format("%s(%s)", getResources().getString(R.string.fixed_price), qouteAsset));
			mEtQuantity.setText("");
			mRlAmount.setVisibility(GONE);
		}
		initStrategy();
		setFixBalanceAvl();
	}


	private void initTextChangeListener() {

		mSeekBar.setProgress(0);
		progressChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
			@Override
			public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
				if (fromUser) {
					mTvPosition.setText(String.valueOf(progress));
				}
				removeTextWatcher(null);
				double percent = BigDecimalUtils.divideDouble(String.valueOf(progress), String.valueOf(100), 2);
				mEtQuantity.setText(tradeStrategy.calQuantityFormSeekBar(mEtPrice.getText(), mEtUnitPrice.getText(), percent));
				mEtAmount.setText(tradeStrategy.calTotalPrice(mEtPrice.getText(), mEtUnitPrice.getText(), mEtQuantity.getText()));
				tradeStrategy.calExpect(mTvMarketFieldValue, mEtQuantity.getText(), mEtTouchPrice.getText());
				addTextWatcher(null);
			}

			@Override
			public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

			}

			@Override
			public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

			}
		};
		mSeekBar.setOnProgressChangedListener(progressChangedListener);

		mEtPrice.removeTextChangedListener(priceTextWacher);
		priceTextWacher = new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEtPrice.getmEditText(), s, mPresenter.getPricePrecision());
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				removeTextWatcher(priceTextWacher);
				mEtAmount.setText(tradeStrategy.calTotalPrice(mEtPrice.getText(), mEtUnitPrice.getText(), mEtQuantity.getText()));
				addTextWatcher(priceTextWacher);
			}
		};
		mEtPrice.addTextChangedListener(priceTextWacher);


		mEtTouchPrice.removeTextChangedListener(touchPriceTextWacher);
		touchPriceTextWacher = new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEtTouchPrice.getmEditText(), s, mPresenter.getPricePrecision());
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				removeTextWatcher(touchPriceTextWacher);
				tradeStrategy.calExpect(mTvMarketFieldValue, mEtQuantity.getText(), mEtTouchPrice.getText());
				addTextWatcher(touchPriceTextWacher);
			}
		};
		mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);


		mEtUnitPrice.removeTextChangedListener(unitPriceTextWacher);
		unitPriceTextWacher = new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEtUnitPrice.getmEditText(), s, mPresenter.getPricePrecision());
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				removeTextWatcher(unitPriceTextWacher);
				mEtAmount.setText(tradeStrategy.calTotalPrice(mEtPrice.getText(), mEtUnitPrice.getText(), mEtQuantity.getText()));
				tradeStrategy.calPercentage(mEtAmount.getText(), mEtQuantity.getText(), mSeekBar, mTvPosition);
				mTvLocalPrice.setText(String.format("≈%s", tradeStrategy.calLocalPrice(mEtUnitPrice.getText())));
				addTextWatcher(unitPriceTextWacher);
			}
		};
		mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
		mEtQuantity.removeTextChangedListener(quantityTextWatcher);
		quantityTextWatcher = new BaseTextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (curFixPriceType == Constants.FIXED_PRICE || curFixPriceType == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS || curFixPriceType == FIXED_PRICE_OCO)//限价的时候  是数量精度
					PrecisionUtils.setPrecision(mEtQuantity.getmEditText(), s, mPresenter.getVolumePrecision());
				else {//市价的时候  买入是金额  （买多少金额的币）  卖出的是数量  （卖多少数量的币）
					if (isBuyTab()) {
						PrecisionUtils.setPrecision(mEtQuantity.getmEditText(), s, mPresenter.getPricePrecision());
					} else {
						PrecisionUtils.setPrecision(mEtQuantity.getmEditText(), s, mPresenter.getVolumePrecision());
					}
				}


			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				removeTextWatcher(quantityTextWatcher);
				mEtAmount.setText(tradeStrategy.calTotalPrice(mEtPrice.getText(), mEtUnitPrice.getText(), mEtQuantity.getText()));
				tradeStrategy.calPercentage(mEtAmount.getText(), mEtQuantity.getText(), mSeekBar, mTvPosition);
				tradeStrategy.calExpect(mTvMarketFieldValue, mEtQuantity.getText(), mEtTouchPrice.getText());
				addTextWatcher(quantityTextWatcher);
			}
		};
		mEtQuantity.addTextChangedListener(quantityTextWatcher);


		mEtAmount.removeTextChangedListener(amountTextWacher);
		amountTextWacher = new BaseTextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				PrecisionUtils.setPrecision(mEtAmount.getmEditText(), s, mPresenter.getPricePrecision());
			}

			@Override
			public void afterTextChanged(Editable s) {
				super.afterTextChanged(s);
				removeTextWatcher(amountTextWacher);
				if (!TextUtils.isEmpty(s.toString().trim())) {
					tradeStrategy.calQuantityFromTotalPrice(mEtQuantity.getmEditText(), mEtUnitPrice.getText(), mEtAmount.getText());
					tradeStrategy.calPercentage(mEtAmount.getText(), mEtQuantity.getText(), mSeekBar, mTvPosition);
				} else {
					mEtQuantity.setText("");
					mSeekBar.setProgress(0);
					mTvPosition.setText("0");
				}
				addTextWatcher(amountTextWacher);
			}
		};
		mEtAmount.addTextChangedListener(amountTextWacher);


		initTouchListener();


	}


	@SuppressLint("ClickableViewAccessibility")
	private void initTouchListener() {
		mEtPrice.setOnTouchListener((v, event) -> {
			mEtPrice.setFocusableInTouchMode(true);
			mEtTouchPrice.setFocusableInTouchMode(false);
			mEtUnitPrice.setFocusableInTouchMode(false);
			mEtQuantity.setFocusableInTouchMode(false);
			mEtAmount.setFocusableInTouchMode(false);
			return false;
		});
		mEtTouchPrice.setOnTouchListener((v, event) -> {
			mEtPrice.setFocusableInTouchMode(false);
			mEtTouchPrice.setFocusableInTouchMode(true);
			mEtUnitPrice.setFocusableInTouchMode(false);
			mEtQuantity.setFocusableInTouchMode(false);
			mEtAmount.setFocusableInTouchMode(false);
			return false;
		});
		mEtUnitPrice.setOnTouchListener((v, event) -> {
			mEtPrice.setFocusableInTouchMode(false);
			mEtTouchPrice.setFocusableInTouchMode(false);
			mEtUnitPrice.setFocusableInTouchMode(true);
			mEtQuantity.setFocusableInTouchMode(false);
			mEtAmount.setFocusableInTouchMode(false);
			return false;
		});

		mEtQuantity.setOnTouchListener((v, event) -> {
			mEtPrice.setFocusableInTouchMode(false);
			mEtTouchPrice.setFocusableInTouchMode(false);
			mEtUnitPrice.setFocusableInTouchMode(false);
			mEtAmount.setFocusableInTouchMode(false);
			mEtQuantity.setFocusableInTouchMode(true);
			return false;
		});
		mEtAmount.setOnTouchListener((v, event) -> {
			mEtPrice.setFocusableInTouchMode(false);
			mEtTouchPrice.setFocusableInTouchMode(false);
			mEtUnitPrice.setFocusableInTouchMode(false);
			mEtQuantity.setFocusableInTouchMode(false);
			mEtAmount.setFocusableInTouchMode(true);
			return false;
		});
	}

	private void removeTextWatcher(TextWatcher textWatcher) {
		if (textWatcher == quantityTextWatcher) {
			mEtPrice.clearTextChangedListeners();
			mEtTouchPrice.clearTextChangedListeners();
			mSeekBar.setOnProgressChangedListener(null);
			mEtUnitPrice.clearTextChangedListeners();
			mEtAmount.clearTextChangedListeners();
		} else if (textWatcher == unitPriceTextWacher) {
			mEtPrice.clearTextChangedListeners();
			mEtTouchPrice.clearTextChangedListeners();
			mSeekBar.setOnProgressChangedListener(null);
			mEtQuantity.clearTextChangedListeners();
			mEtAmount.clearTextChangedListeners();
		} else if (textWatcher == amountTextWacher) {
			mEtPrice.clearTextChangedListeners();
			mEtTouchPrice.clearTextChangedListeners();
			mSeekBar.setOnProgressChangedListener(null);
			mEtQuantity.clearTextChangedListeners();
			mEtUnitPrice.clearTextChangedListeners();
		} else if (textWatcher == touchPriceTextWacher) {
			mEtPrice.clearTextChangedListeners();
			mEtAmount.clearTextChangedListeners();
			mSeekBar.setOnProgressChangedListener(null);
			mEtQuantity.clearTextChangedListeners();
			mEtUnitPrice.clearTextChangedListeners();
		} else if (textWatcher == priceTextWacher) {
			mEtTouchPrice.clearTextChangedListeners();
			mEtAmount.clearTextChangedListeners();
			mSeekBar.setOnProgressChangedListener(null);
			mEtQuantity.clearTextChangedListeners();
			mEtUnitPrice.clearTextChangedListeners();
		} else if (textWatcher == null) {
			mEtPrice.clearTextChangedListeners();
			mEtTouchPrice.clearTextChangedListeners();
			mEtUnitPrice.clearTextChangedListeners();
			mEtQuantity.clearTextChangedListeners();
			mEtAmount.clearTextChangedListeners();
			mEtPrice.clearFocus();
			mEtTouchPrice.clearFocus();
			mEtUnitPrice.clearFocus();
			mEtQuantity.clearFocus();
			mEtPrice.clearFocus();
			KeyboardUtils.hideKeyboard(mEtQuantity);

		}
	}

	private void addTextWatcher(TextWatcher textWatcher) {
		if (textWatcher == quantityTextWatcher) {
			mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);
			mEtPrice.addTextChangedListener(priceTextWacher);
			mSeekBar.setOnProgressChangedListener(progressChangedListener);
			mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
			mEtAmount.addTextChangedListener(amountTextWacher);
		} else if (textWatcher == unitPriceTextWacher) {
			mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);
			mEtPrice.addTextChangedListener(priceTextWacher);
			mSeekBar.setOnProgressChangedListener(progressChangedListener);
			mEtQuantity.addTextChangedListener(quantityTextWatcher);
			mEtAmount.addTextChangedListener(amountTextWacher);
		} else if (textWatcher == amountTextWacher) {
			mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);
			mEtPrice.addTextChangedListener(priceTextWacher);
			mSeekBar.setOnProgressChangedListener(progressChangedListener);
			mEtQuantity.addTextChangedListener(quantityTextWatcher);
			mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
		} else if (textWatcher == touchPriceTextWacher) {
			mEtAmount.addTextChangedListener(amountTextWacher);
			mEtPrice.addTextChangedListener(priceTextWacher);
			mSeekBar.setOnProgressChangedListener(progressChangedListener);
			mEtQuantity.addTextChangedListener(quantityTextWatcher);
			mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
		} else if (textWatcher == priceTextWacher) {
			mEtAmount.addTextChangedListener(amountTextWacher);
			mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);
			mSeekBar.setOnProgressChangedListener(progressChangedListener);
			mEtQuantity.addTextChangedListener(quantityTextWatcher);
			mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
		} else if (textWatcher == null) {
			mEtTouchPrice.addTextChangedListener(touchPriceTextWacher);
			mEtPrice.addTextChangedListener(priceTextWacher);
			mEtUnitPrice.addTextChangedListener(unitPriceTextWacher);
			mEtQuantity.addTextChangedListener(quantityTextWatcher);
			mEtAmount.addTextChangedListener(amountTextWacher);
		}
	}

	/**
	 * 滑动条监听计算数量
	 *
	 * @param inputPrice
	 * @param progress
	 */
	private void calculationNumber(String inputPrice, double progress) {
		mEtQuantity.setText(CalculationUtils.calQuantityFromSeekBar(inputPrice,
				String.valueOf(progress),
				isBuyTab() ? buyBalance : sellBalance,
				mPresenter.getTakeFee(),
				mPresenter.getVolumePrecision(),
				isBuyTab()));
	}

	/**
	 * 滑动条计算总额
	 *
	 * @param unitPrice
	 * @param vol
	 */
	private void calTotalPrice(String unitPrice, String vol) {
		mEtAmount.setText(CalculationUtils.calTotalPrice(unitPrice
				, vol
				, mPresenter.getTakeFee()
				, mPresenter.getPricePrecision()
				, isBuyTab()));
	}

	/**
	 * 市价下  拖动滑动条计算
	 *
	 * @param percent
	 */
	private void calculationMarketFromSeekBar(double percent) {
		if (isBuyTab()) {//买的时候计算
			if (!TextUtils.isEmpty(buyBalance)) {
				mEtQuantity.setText(BigDecimalUtils.multiplyDown(buyBalance, String.valueOf(percent), mPresenter.getPricePrecision()));
			}
		} else {//卖的时候计算
			if (!TextUtils.isEmpty(sellBalance)) {
				mEtQuantity.setText(BigDecimalUtils.multiplyDown(sellBalance, String.valueOf(percent), mPresenter.getVolumePrecision()));
			}
		}
	}

	/**
	 * 市价下  输入买入金额或者卖出数量输入框  计算百分比及买入买出的量
	 *
	 * @param number
	 */
	private void calculationMarketFromNumber(String number) {
		if (isBuyTab()) {//买的时候计算
			if (!TextUtils.isEmpty(buyBalance)) {
				int progress = BigDecimalUtils.divideHalfUp(number, buyBalance, 2);
				if (progress > 100 || progress < 0) {
					progress = 100;
				}

				mSeekBar.setProgress(progress);
				mTvPosition.setText(String.valueOf(progress));
			}
		} else {//卖的时候计算
			if (!TextUtils.isEmpty(sellBalance)) {
				int progress = BigDecimalUtils.divideHalfUp(number, sellBalance, 2);
				if (progress > 100 || progress < 0) {
					progress = 100;
				}
				mSeekBar.setProgress(progress);
				mTvPosition.setText(String.valueOf(progress));
			}
		}
	}

	/**
	 * 计算买卖数量  当总额变化
	 *
	 * @param unitPrice
	 * @param amount
	 */
	private void calQuantityFromTotalPrice(String unitPrice, String amount) {
		mEtQuantity.setText(CalculationUtils.calQuantityFromTotalPrice(unitPrice,
				amount,
				mPresenter.getTakeFee(),
				mPresenter.getVolumePrecision(),
				isBuyTab()));
	}

	/**
	 * 计算当前价格 本地汇率价格
	 *
	 * @param unitPrice
	 */
	private void calLocalPrice(String unitPrice) {
		if (BigDecimalUtils.isEmptyOrZero(unitPrice) || BigDecimalUtils.isEmptyOrZero(rate)) {
			mTvLocalPrice.setText("");
			return;
		}
		mTvLocalPrice.setText(String.format("≈%s%s", currency, BigDecimalUtils.multiplyHalfUp(unitPrice, rate, 4)));
	}

	/**
	 * 计算仓位占比
	 *
	 * @param totalAmount
	 */
	private void calPercentage(String totalAmount, String vol) {
		if (BigDecimalUtils.isEmptyOrZero(totalAmount) || (BigDecimalUtils.isEmptyOrZero(vol))) {
			mSeekBar.setProgress(0);
			mTvPosition.setText("0");
			return;
		}
		long percentage = isBuyTab() ?
				BigDecimalUtils.divideHalfUp(totalAmount, buyBalance, 2) :
				BigDecimalUtils.divideHalfUp(vol, sellBalance, 2);
		//小于0是为了 让算出来的数据大于integer的最大值的时候会变成负数
		if (percentage > 100 || percentage < 0) {
			percentage = 100;
		}
		mSeekBar.setProgress(percentage);
		mTvPosition.setText(String.valueOf(percentage));
	}

	/**
	 * 解析分子分母
	 */
	private void parseTradePairName() {
		String[] arrays = TradeUtils.parseSymbol(symbol);
		baseAsset = arrays[0];
		qouteAsset = arrays[1];
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		mSeekBar.setProgress(0);
//		mTvPriceCoin.setText(qouteAsset);
//		mTvQuantityCoin.setText(baseAsset);
//		mTvAmountCoin.setText(qouteAsset);

		mEtAmount.setHint(String.format("%s(%s)", getResources().getString(R.string.total_price), qouteAsset));

		mEtUnitPrice.setText("");
		mEtUnitPrice.setTag(null);
		mEtQuantity.setText("");
		mEtAmount.setText("");
		sellPriceOne = null;
		buyPriceOne = null;
		initPlaceOrderBtn();
		setFixBalanceAvl();

	}

	private void setBalanceObserver() {
		CBRepository.boxFor(BalanceInfoTable.class)
				.query()
				.equal(BalanceInfoTable_.asset, qouteAsset)
				.build()
				.subscribe(subscriptions)
				.on(AndroidScheduler.mainThread())
				.onError(error -> error.printStackTrace())
				.observer(new DataObserver<List<BalanceInfoTable>>() {
					@Override
					public void onData(List<BalanceInfoTable> data) {
						if (CommonUtil.isLoginAndUnLocked() && data.size() > 0) {
							buyBalance = data.get(0).availableBalance;
						} else {
							buyBalance = "0";
						}
						tradeStrategy.setAvlBuyBalance(buyBalance);
						setFixBalanceAvl();
					}
				});

		CBRepository.boxFor(BalanceInfoTable.class)
				.query()
				.equal(BalanceInfoTable_.asset, baseAsset)
				.build()
				.subscribe(subscriptions)
				.on(AndroidScheduler.mainThread())
				.onError(error -> error.printStackTrace())
				.observer(new DataObserver<List<BalanceInfoTable>>() {
					@Override
					public void onData(List<BalanceInfoTable> data) {
						if (CommonUtil.isLoginAndUnLocked() && data.size() > 0) {
							sellBalance = data.get(0).availableBalance;
						} else {
							sellBalance = "0";
						}
						tradeStrategy.setAvlSellBalance(sellBalance);
						setFixBalanceAvl();
					}
				});
	}


	public void setMarginBalance(MarginSingleAccountModel.DataBean data) {
		for (int i = 0; i < data.getBalanceList().size(); i++) {
			if (baseAsset.equals(data.getBalanceList().get(i).getAsset())) {
				if (ServiceRepo.getUserService().isLogin()) {
					sellBalance = data.getBalanceList().get(i).getAvailable();
				} else {
					sellBalance = "0";
				}
				tradeStrategy.setAvlSellBalance(sellBalance);
				continue;
			}
			if (qouteAsset.equals(data.getBalanceList().get(i).getAsset())) {
				if (ServiceRepo.getUserService().isLogin()) {
					buyBalance = data.getBalanceList().get(i).getAvailable();
				} else {
					buyBalance = "0";
				}
				tradeStrategy.setAvlBuyBalance(buyBalance);
			}
		}
		setFixBalanceAvl();
	}

	/**
	 * 初始化下单按钮
	 */
	public void initPlaceOrderBtn() {
		if (CommonUtil.isLoginAndUnLocked()) {
			String btnName = isBuyTab() ? getResources().getString(R.string.buy_btn_txt) : getResources().getString(R.string.sell_btn_txt);
			if (isBuyTab()) {
				mTvPlaceOrder.setBackgroundResource(SwitchUtils.isRedRise() ? R.drawable.bg_red_sharp : R.drawable.bg_green_sharp);

			} else {
				mTvPlaceOrder.setBackgroundResource(SwitchUtils.isRedRise() ? R.drawable.bg_green_sharp : R.drawable.bg_red_sharp);
//				mTvPlaceOrder.setText(String.format(btnName, qouteAsset));
			}
			mTvPlaceOrder.setText(String.format(btnName, baseAsset));

		} else {
			mTvPlaceOrder.setBackgroundResource(R.drawable.btn_bg_sharp);
			mTvPlaceOrder.setText(getResources().getString(R.string.login_register_label));
		}

		initViewByDirection(curTab);

		//杠杆不显示市价、限价切换区域
		mLlFixedPrice.setVisibility(isMargin ? INVISIBLE : VISIBLE);
	}

	/**
	 * 限价市价下的状态及余额
	 */
	public void setFixBalanceAvl() {
		String ableNumStr = "";
		if (isBuyTab()) {
			ableNumStr = String.format(getResources().getString(R.string.trade_balance_num), buyBalance, qouteAsset);
		} else {
			ableNumStr = String.format(getResources().getString(R.string.trade_balance_num), sellBalance, baseAsset);
		}
		mTvAvlValue.setText(ableNumStr);
		if (curFixPriceType == Constants.FIXED_PRICE ||
				curFixPriceType == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS ||
				curFixPriceType == Constants.FIXED_PRICE_OCO) {
			mLlMarketField.setVisibility(GONE);
			mEtQuantity.setHint(String.format("%s(%s)", getResources().getString(R.string.input_num_hint), baseAsset));
			mEtQuantity.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getVolumePrecision()));

		} else {
			//市价oco不需要计算预估
			if(curFixPriceType == MARKET_PRICE_OCO){
				mLlMarketField.setVisibility(GONE);
			}else {
				mLlMarketField.setVisibility(VISIBLE);
			}
			if (isBuyTab()) {
				mTvMarketField.setText(R.string.estimate_buy);
				mTvMarketFieldValue.setText(String.format("--%s", qouteAsset));
				mEtQuantity.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getPricePrecision()));
				mEtQuantity.setHint(String.format("%s(%s)", getResources().getString(R.string.amount), qouteAsset));
			} else {
				mTvMarketField.setText(R.string.estimate_sell);
				mTvMarketFieldValue.setText(String.format("--%s", baseAsset));
				mEtQuantity.setMinPriceChange(PrecisionUtils.changeNumToPrecisionStr(mPresenter.getVolumePrecision()));
				mEtQuantity.setHint(String.format("%s(%s)", getResources().getString(R.string.input_num_hint), baseAsset));
			}
			if (CommonUtil.isLoginAndUnLocked()) {
				tradeStrategy.calExpect(mTvMarketFieldValue, mEtQuantity.getText(), mEtTouchPrice.getText());
			}
		}
	}

	public void setFirstPrice() {
		if (!isMarketPrice()) {
			if (isBuyTab()) {
				if (!BigDecimalUtils.isEmptyOrZero(sellPriceOne)) {
					mEtUnitPrice.setText(sellPriceOne);
				}
			} else if (!isBuyTab()) {
				if (!BigDecimalUtils.isEmptyOrZero(buyPriceOne)) {
					mEtUnitPrice.setText(buyPriceOne);
				}
			}
		}
	}

//	/**
//	 * 计算市价下预估可买或者可卖
//	 */
//	private void calculationMarketExpect() {
//		String marketExpect = CalculationUtils.calMarketExpect(isBuyTab(),
//				mEtQuantity.getText().toString(),
//				lastPrice,
//				buyPriceOne,
//				sellPriceOne,
//				mPresenter.getPricePrecision(),
//				mPresenter.getVolumePrecision(),
//				String.valueOf(mPresenter.getTakeFee()),
//				baseAsset,
//				qouteAsset);
//
//
//		if (!AppUtil.isMainThread()) {
//			post(() -> mTvMarketFieldValue.setText(marketExpect));
//		} else {
//			mTvMarketFieldValue.setText(marketExpect);
//		}
//	}
//
//	private void calculationMarketLossExpect() {
//		mTvMarketFieldValue.setText(CalculationUtils.calMarketLossExpect(isBuyTab(),
//				mEtQuantity.getText().toString(),
//				mEtTouchPrice.getText().toString(),
//				baseAsset,
//				qouteAsset,
//				mPresenter.getPricePrecision()));
//
//	}


	@NeedLogin(jump = true)
	private void placeOrder() {

		//判断是否开通了杠杆协议   从本地拿
		if (isMargin && !SpUtil.isMarginUserConfig()) {
			if (mParentFragment instanceof MarginFragment) {
				((MarginFragment) mParentFragment).showAgreementDialog();
			}
			return;
		}

		//巴西站kyc未认证 不能下单
		if (SiteController.getInstance().isBrSite() && !UserInfoController.getInstance().checkKycStatus()) {
			ToastUtil.show(R.string.br_kyc_tip);
			return;
		}

		if (!isMargin) {
			if (isBuyTab()) {
				PostPointHandler.postClickData(PostPointHandler.spot_coin_buy);
			} else {
				PostPointHandler.postClickData(PostPointHandler.spot_coin_sell);
			}
		}

		SpotPlaceOrderModel placeOrderModel = null;


		boolean checkParms = tradeStrategy.checkParms(mEtPrice.getText(),
				mEtTouchPrice.getText(),
				mEtUnitPrice.getText(),
				mEtQuantity.getText(),
				mEtAmount.getText());
		if (!checkParms) {
			return;
		}
		if (mPresenter != null) {
			placeOrderModel = new SpotPlaceOrderModel();
			placeOrderModel.setOcoPrice(mEtPrice.getText());
			placeOrderModel.setTouchPrice(mEtTouchPrice.getText());
			placeOrderModel.setOrderType(curFixPriceType);
			placeOrderModel.setBuy(isBuyTab());
			placeOrderModel.setSymbol(symbol);
			placeOrderModel.setQuantity(mEtQuantity.getText());
			placeOrderModel.setPrice(mEtUnitPrice.getText());
		}


		if (placeOrderModel != null) {
			//如果是IEO，先弹窗确认对话框，ok后再继续处理
			mPresenter.placeOrder(placeOrderModel);
			mTvPlaceOrder.setClickable(false);
		}
	}

	private boolean isBuyTab() {
		return curTab == TAB_BUY;
	}

	/**
	 * 初始化view状态根据买卖选中方向
	 *
	 * @param curTabDirection
	 */
	private void initViewByDirection(int curTabDirection) {
		curTab = curTabDirection;
		if (curTabDirection == TAB_BUY) {
			mTvTabBuy.setBackground(SwitchUtils.isRedRise() ? getResources().getDrawable(R.drawable.res_red_button_left) : getResources().getDrawable(R.drawable.res_green_button_left));
			mTvTabBuy.setTextColor(getResources().getColor(R.color.res_white));
			mTvTabSell.setBackground(getResources().getDrawable(R.drawable.res_trade_btn_normal_right));
			mTvTabSell.setTextColor(getResources().getColor(R.color.res_textColor_2));
			mSeekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
			mSeekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_red) : getResources().getColor(R.color.res_green));
		} else {
			mTvTabBuy.setBackground(getResources().getDrawable(R.drawable.res_trade_btn_normal_left));
			mTvTabBuy.setTextColor(getResources().getColor(R.color.res_textColor_2));
			mTvTabSell.setBackground(SwitchUtils.isRedRise() ? getResources().getDrawable(R.drawable.res_green_button_right) : getResources().getDrawable(R.drawable.res_red_button_right));
			mTvTabSell.setTextColor(getResources().getColor(R.color.res_white));
			mSeekBar.setThumbColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
			mSeekBar.setSecondTrackColor(SwitchUtils.isRedRise() ? getResources().getColor(R.color.res_green) : getResources().getColor(R.color.res_red));
		}
	}

	/**
	 * 获取法币符号   费率  以及最新价 买一卖一价
	 *
	 * @param quote
	 */
	private synchronized void setCacheData(WsMarketData quote) {
		if (quote != null) {
			if (TextUtils.isEmpty(lastPrice)) {
				lastPrice = quote.getLastPrice() == null ? "" : quote.getLastPrice();
			} else {
				if (!lastPrice.equals(quote.getLastPrice())) {
					lastPrice = quote.getLastPrice();
					tradeStrategy.setLastPrice(lastPrice);
					if (curFixPriceType == Constants.MARKET_PRICE) {
						tradeStrategy.calExpect(mTvMarketFieldValue, mEtQuantity.getText(), mEtTouchPrice.getText());
					}
				}
			}
			if (TextUtils.isEmpty(NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate())) {
				rate = "0";
			} else {
				rate = NewMarketWebsocket.getInstance().getCurrentExchangeRate().getRate();
			}
			tradeStrategy.setRate(rate);

			if (!TextUtils.isEmpty(quote.getLocalPrice()) && quote.getLocalPrice().length() > 1) {
				String tempCurrency;
				//巴西语 法币符号有两位
				if (LanguageHelper.isPortuguese(getContext())) {
					tempCurrency = quote.getLocalPrice().substring(0, 2);
				} else {
					tempCurrency = quote.getLocalPrice().substring(0, 1);
				}
				if (TextUtils.isEmpty(currency) || !currency.equals(tempCurrency)) {
					currency = tempCurrency;
					if (currency.contains("￥")) {
						currency = currency.replace("￥", Html.fromHtml("&yen"));
					}
				}
			}
		}
	}

	@Override
	public void onPlaceOrderResult(boolean success, int orderType) {
		mTvPlaceOrder.setClickable(true);
		if (success) {
//			if (orderType == Constants.FIXED_PRICE || orderType == Constants.MARKET_PRICE) {
			mEtQuantity.setText("");
			mEtAmount.setText("");
//			} else if (orderType == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS || orderType == Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS) {
//				mEtQuantity.setText("");
//				mEtAmount.setText("");
//			}else if (curFixPriceType == Constants.FIXED_PRICE_OCO) {
//				mEtTouchPrice.setText("");
//				mEtAmount.setText("");
//			}
			ToastUtil.show(R.string.buyorsell_success);
			KeyboardUtils.hideKeyboard(mEtQuantity);
		}

	}

	public void setParentFragment(Fragment fragment, boolean isMargin) {
		this.mParentFragment = fragment;
		this.isMargin = isMargin;
		if (mPresenter == null) {
			if (isMargin) {
				mPresenter = new MarginTradeLayoutPresenter(this);
			} else {
				mPresenter = new SpotTradeLayoutPresenter(this);
			}
		}
		mPresenter.setActivity(mParentFragment.getActivity());
	}

	private boolean isMarketPrice() {
		return curFixPriceType == Constants.MARKET_PRICE || curFixPriceType == Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS || curFixPriceType == Constants.MARKET_PRICE_OCO;
	}


	@Override
	public void onPriceClick(String price) {
		if (!TextUtils.isEmpty(price) && !isMarketPrice()) {
			mEtUnitPrice.setText(price);
		}
	}

	@Override
	public void onReceiveQuoteData(WsMarketData quote) {
		setCacheData(quote);
	}

	public void registerUserEvent() {


		if (userEventListener == null) {
			userEventListener = new BaseUserEventListener() {
				@Override
				public void onAccountChanged() {
					if (ServiceRepo.getUserService().isLogin() && mPresenter != null) {
						mPresenter.getBanlance();
					}
				}
			};
		}
		if (isMargin) {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().registerUsereventListener(WebSocketType.SPOT, userEventListener);
		}
	}


	public void unregisterUserEvent() {
		if (isMargin) {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.MARGIN, userEventListener);
		} else {
			UsereventWebsocket.getInstance().unregisterUsereventListener(WebSocketType.SPOT, userEventListener);
		}
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		EventBus.getDefault().unregister(this);
		if (subscriptions != null && !subscriptions.isCanceled()) {
			subscriptions.cancel();
		}
		if (mPresenter != null) {
			mPresenter.onDestory();
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onBuyOrSellPriceOneMessage(BuyOrSellPriceOneMessage message) {
		if (message.getBuyPriceOne() == null && message.getSellPriceOne() == null) {
			return;
		}
		this.buyPriceOne = message.getBuyPriceOne() == null ? "" : message.getBuyPriceOne();
		this.sellPriceOne = message.getSellPriceOne() == null ? "" : message.getSellPriceOne();
		if(tradeStrategy != null){
			tradeStrategy.setBuyPriceOne(buyPriceOne);
			tradeStrategy.setSellPriceOne(sellPriceOne);
		}
		if (TextUtils.isEmpty((String) mEtUnitPrice.getTag()) || !(mEtUnitPrice.getTag()).equals(message.getSymbol())) {
			mEtUnitPrice.setTag(message.getSymbol());
			setFirstPrice();
		}
	}

	public void changeTab(int tabBuyOrSell) {

		initInput(tabBuyOrSell);

		initViewByDirection(tabBuyOrSell);
		tradeStrategy.setBuy(isBuyTab());
		initPlaceOrderBtn();
		setFirstPrice();
		setFixBalanceAvl();
		mEtQuantity.setText("");
	}

	private void initInput(int tabBuyOrSell) {
		if (curTab == tabBuyOrSell) {
			return;
		}
		if (curFixPriceType == Constants.FIXED_PRICE_STOP_LOSS_AND_STOP_LOSS || curFixPriceType == Constants.MARKET_PRICE_STOP_LOSS_AND_STOP_LOSS) {
			mEtTouchPrice.setText("");
		} else if (curFixPriceType == FIXED_PRICE_OCO || curFixPriceType == MARKET_PRICE_OCO) {
			mEtTouchPrice.setText("");
			mEtPrice.setText("");
		}

	}

	public void setBalance() {
		if (!ServiceRepo.getUserService().isLogin()) {
			buyBalance = "0";
			sellBalance = "0";
			tradeStrategy.setAvlSellBalance(sellBalance);
			tradeStrategy.setAvlBuyBalance(buyBalance);
			setFixBalanceAvl();
		}
	}
}
