package com.coinbene.manbiwang.spot.margin;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.MarginSymbolTable;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.common.widget.ContentSpan;
import com.coinbene.manbiwang.model.http.MarginSingleAccountModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.fragment.adapter.SpotBottomTabAdapter;
import com.coinbene.manbiwang.spot.fragment.bean.AccountType;
import com.coinbene.manbiwang.spot.fragment.bean.SpotBottomTab;
import com.coinbene.manbiwang.spot.fragment.bean.SpotBottomTabItem;
import com.coinbene.manbiwang.spot.margin.presenter.MarginInterface;
import com.coinbene.manbiwang.spot.margin.presenter.MarginPresenter;
import com.coinbene.manbiwang.spot.margin.widget.SymbolChangeWindow;
import com.coinbene.manbiwang.spot.orderlayout.OrderLayout;
import com.coinbene.manbiwang.spot.tradelayout.TradeLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 杠杆fragment
 */
public class MarginFragment extends CoinbeneBaseFragment implements MarginInterface.View, SpotBottomTabAdapter.SpotBottomInterface {

	@BindView(R2.id.tv_symbol)
	TextView tvSymbol;
	@BindView(R2.id.tv_lever)
	TextView tvLever;
	@BindView(R2.id.ll_title)
	LinearLayout llTitle;
	@BindView(R2.id.iv_margin_risk_descrip)
	ImageView ivMarginRiskDescrip;
	@BindView(R2.id.tv_risk_rate_value)
	TextView tvRiskRateValue;
	@BindView(R2.id.tv_risk_rate)
	TextView tvRiskRate;
	@BindView(R2.id.tv_burst_price_value)
	TextView tvforceClosePrice;
	@BindView(R2.id.tv_borrow_return_asset)
	TextView tvBorrowReturnAsset;
	@BindView(R2.id.iv_right)
	ImageView ivRight;
	@BindView(R2.id.iv_chart)
	ImageView ivChart;
	@BindView(R2.id.nsv_content)
	NestedScrollView nestedScrollView;
	@BindView(R2.id.app_bar)
	AppBarLayout mAppBar;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;

	@BindView(R2.id.new_order_layout)
	OrderLayout mOrderLayout;
	@BindView(R2.id.new_trade_layout)
	TradeLayout mTradeLayout;

	private String curSymbol = SpUtil.getMarginSymbol();
	private MarginInterface.Presenter presenter;
	private SymbolChangeWindow symbolChangeWindow;
	private QMUIDialog dialog;

	private MarginSymbolTable table;

	private SpotBottomTabAdapter spotBottomTabAdapter;
	private List<SpotBottomTabItem> spotBottomTabItems;

	private boolean hideOtherCoin = false;
	private String coinOptions;

	public static Fragment newInstance() {
		Bundle args = new Bundle();
		MarginFragment fragment = new MarginFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.spot_fr_margin_trade;
	}

	@Override
	public void initView(View rootView) {
		initTabSegment();

		mOrderLayout.setParentFragment(this, mTradeLayout);
		mTradeLayout.setParentFragment(this, true);
	}

	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));
		mTabSegment.setElevation(0);

		if (spotBottomTabItems == null) {
			spotBottomTabItems = new ArrayList<>();
		}
		if (spotBottomTabAdapter == null) {
			spotBottomTabItems.add(new SpotBottomTabItem(SpotBottomTab.CURRENT_ORDER, getString(R.string.cur_entrust_label), AccountType.MARGIN));
			spotBottomTabItems.add(new SpotBottomTabItem(SpotBottomTab.HISTORY_ORDER, getString(R.string.history_entrust_label_new), AccountType.MARGIN));
			spotBottomTabAdapter = new SpotBottomTabAdapter(getChildFragmentManager(), spotBottomTabItems);
		}
		mViewPager.setOffscreenPageLimit(spotBottomTabItems.size());
		mViewPager.setAdapter(spotBottomTabAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
	}

	@Override
	public void setListener() {
		ivChart.setOnClickListener(v -> {
			//ARouter.getInstance().build(RouteHub.Kline.spotKlineActivity).withString("pairName", table.symbol).navigation(getContext());
			UIBusService.getInstance().openUri(getContext(), "coinbene://SpotKline?pairName=" + table.symbol, null);
		});

		ivRight.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString(Constants.WEB_EXTRA_TITLE, getString(R.string.margin_guide));
			UIBusService.getInstance().openUri(getContext(), UrlUtil.getMarginGuideUrl(), bundle);
		});

		ivMarginRiskDescrip.setOnClickListener(v -> showRiskRateDialog());
		tvRiskRate.setOnClickListener(v -> showRiskRateDialog());
		tvBorrowReturnAsset.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkAndStartMargin();
			}
		});


		llTitle.setOnClickListener(v -> {
			if (symbolChangeWindow == null)
				symbolChangeWindow = new SymbolChangeWindow(llTitle);
				symbolChangeWindow.showBelowAnchor();
				symbolChangeWindow.setSelectSymbol(curSymbol);
				symbolChangeWindow.setOnItemClickContrckListener(symbol -> {
					symbolChangeWindow.getPopupWindow().dismiss();
					mAppBar.setExpanded(true);
					setMarginSymbol(symbol);
					SpUtil.setMarginSymbol(symbol);
				});
		});
	}

	@NeedLogin(jump = true)
	private void checkAndStartMargin() {
		//判断是否开通了杠杆协议   从本地拿
		if (!SpUtil.isMarginUserConfig()) {
			showAgreementDialog();
			return;
		}
		ARouter.getInstance().build(RouteHub.Balance.marginDetail).withString("symbol", curSymbol).navigation(getContext());
	}

	/**
	 * 风险提示框
	 */
	private void showRiskRateDialog() {
		if (dialog == null)
			dialog = new QMUIDialog.CustomDialogBuilder(getContext())
					.setLayout(R.layout.spot_dialog_risk_rate)
					.create();
		dialog.findViewById(R.id.tv_i_kown).setOnClickListener(v -> dialog.dismiss());
		dialog.show();
	}

	@Override
	public void initData() {
		presenter = new MarginPresenter(this, getActivity());

		setMarginSymbol(curSymbol);
	}

	@Override
	public void onFragmentShow() {
		SpUtil.setSpotTradingOptions("margin");
		if (presenter != null) {
			presenter.onResume();
			if (!TextUtils.isEmpty(curSymbol)) {
				setMarginSymbol(curSymbol);
			}
		}

		PostPointHandler.postBrowerData(PostPointHandler.tab_margin);

		if (CommonUtil.isLoginAndUnLocked()) {

		} else {
			tvforceClosePrice.setText("--");
			tvRiskRateValue.setText("--");
		}
		mTradeLayout.setBalance();

		changeTab();

		if (mTradeLayout != null) {
			mTradeLayout.registerUserEvent();
			mTradeLayout.setFirstPrice();
		}

		if (mOrderLayout != null) {
			mOrderLayout.registerDataListener();
		}
	}

	private void changeTab() {
		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSymbol())) {
			mAppBar.setExpanded(true);
			setMarginSymbol(MainActivityDelegate.getInstance().getSymbol());
			MainActivityDelegate.getInstance().clearSymbol();
		}

		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getType())) {
			//1 买，2卖
			if (MainActivityDelegate.getInstance().getType().equals("1")) {
				mTradeLayout.changeTab(Constants.TAB_BUY);
			} else {
				mTradeLayout.changeTab(Constants.TAB_SELL);
			}
			MainActivityDelegate.getInstance().clearType();
		}
	}

	@Override
	public void onFragmentHide() {
		if (presenter != null) {
			presenter.onPause();
		}

		if (mTradeLayout != null) {
			mTradeLayout.unregisterUserEvent();
		}

		if (mOrderLayout != null) {
			mOrderLayout.unRegisterDataListener();
		}
	}

	/**
	 * 设置币对  切换币对
	 *
	 * @param symbol
	 */
	public void setMarginSymbol(String symbol) {
		table = MarginSymbolController.getInstance().querySymbolByName(symbol);

		if (table != null && !TextUtils.isEmpty(table.symbol)) {
			curSymbol = table.symbol;
			tvLever.setText(String.format("%sX", table.leverage));
//
//			marginCenterLayout.setSymboleTable(table);
//			marginCenterLayout.setPlaceOrderStutus();
//			marginCenterLayout.setNeedChangePrice(true);
//			marginCenterLayout.initInput();

			presenter.setCurSymbol(curSymbol);

			NewSpotWebsocket.getInstance().changeSymbol(curSymbol);

			tvSymbol.setText(curSymbol);

			mTradeLayout.setSymbol(symbol);
			mOrderLayout.setSymbol(symbol);
		}
	}

	@Override
	public void setPresenter(MarginInterface.Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMarginBalance(MarginSingleAccountModel.DataBean  data){
		tvRiskRateValue.setText(TradeUtils.getDisplayRiskRate(data.getRiskRate()));
		tvforceClosePrice.setText(TradeUtils.getLineForNullString(data.getForceClosePrice()));

		mTradeLayout.setMarginBalance(data);
	}

	@Override
	public String getTradePair() {
		return curSymbol;
	}

	@Override
	public void changeTradePair(String tradePair) {
		if (mAppBar != null) {
			mAppBar.setExpanded(true);
		}
		setMarginSymbol(tradePair);
	}

	@Override
	public boolean getHideOtherCoin() {
		return this.hideOtherCoin;
	}

	@Override
	public void setHideOtherCoin(boolean hideOtherCoin) {
		this.hideOtherCoin = hideOtherCoin;
	}


	/**
	 * 展示开通杠杆协议弹窗
	 */
	public void showAgreementDialog() {
		QMUIDialog dialog = new QMUIDialog.CustomDialogBuilder(getContext())
				.setLayout(R.layout.spot_dialog_agreement_custom)
				.create();

		dialog.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
		CheckBox cb = dialog.findViewById(R.id.cb_check);


		TextView tvUserArgeement = dialog.findViewById(R.id.tv_check_discribe);
		tvUserArgeement.setMovementMethod(LinkMovementMethod.getInstance());
		tvUserArgeement.setText("");
		SpannableString spanString = new SpannableString(getResources().getString(R.string.margin_user_agreement));
		ContentSpan span = new ContentSpan(v -> {
			//合约用户指南
			Bundle bundle = new Bundle();
			bundle.putString(Constants.WEB_EXTRA_TITLE, getContext().getString(R.string.margin_user_protocol));
			UIBusService.getInstance().openUri(getContext(), UrlUtil.getMarginUserProtocol(), bundle);
		}, getResources().getColor(R.color.res_blue));
		spanString.setSpan(span, 0, spanString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ContentSpan span1 = new ContentSpan(v -> cb.setChecked(!cb.isChecked()), getResources().getColor(R.color.res_textColor_2));
		SpannableString spanString1 = new SpannableString(getResources().getString(R.string.margin_trade_tips_two));
		spanString1.setSpan(span1, 0, spanString1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvUserArgeement.append(spanString1);
		tvUserArgeement.append(spanString);
		tvUserArgeement.setHighlightColor(getResources().getColor(R.color.transparent));

		dialog.findViewById(R.id.tv_sure).setOnClickListener(v -> {
			if (!cb.isChecked()) {
				ToastUtil.show(R.string.please_read_and_tick_first);
				return;
			}
			dialog.dismiss();
			SpUtil.setMarginUserConfig(true);

			createUserMargin();

		});
		dialog.show();
	}

	private void createUserMargin() {
		if (presenter != null) {
			presenter.createUserMarginConfig();
		}
	}
}
