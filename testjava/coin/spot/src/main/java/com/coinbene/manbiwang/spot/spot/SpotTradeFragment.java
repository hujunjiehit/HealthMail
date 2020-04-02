package com.coinbene.manbiwang.spot.spot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.coinbene.common.Constants;
import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.activities.SelectSearchPairActivity;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.common.widget.QMUITabSelectedListener;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.market.MarketService;
import com.coinbene.manbiwang.spot.R;
import com.coinbene.manbiwang.spot.R2;
import com.coinbene.manbiwang.spot.fragment.adapter.SpotBottomTabAdapter;
import com.coinbene.manbiwang.spot.fragment.bean.AccountType;
import com.coinbene.manbiwang.spot.fragment.bean.SpotBottomTab;
import com.coinbene.manbiwang.spot.fragment.bean.SpotBottomTabItem;
import com.coinbene.manbiwang.spot.orderlayout.OrderLayout;
import com.coinbene.manbiwang.spot.tradelayout.TradeLayout;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.google.android.material.appbar.AppBarLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SpotTradeFragment extends CoinbeneBaseFragment implements SpotBottomTabAdapter.SpotBottomInterface {
	@BindView(R2.id.tv_trade_pair)
	TextView tvTradePair;
	@BindView(R2.id.ll_trade_pair)
	LinearLayout llTradePair;
	@BindView(R2.id.iv_kline)
	ImageView ivKline;
	@BindView(R2.id.lottie_view_mining)
	LottieAnimationView lottieViewMining;
	@BindView(R2.id.iv_slef)
	ImageView ivSlef;
	@BindView(R2.id.rl_top)
	ConstraintLayout rlTop;
	@BindView(R2.id.nest_scroll_view)
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

	@BindView(R2.id.ieo_title_tv)
	TextView mIeoTitleTv;
	@BindView(R2.id.ieo_label)
	TextView mIeoLabel;
	@BindView(R2.id.ieo_layout)
	RelativeLayout mIeoLayout;

	private String symbol = SpUtil.getSpotCoin();
	private TradePairInfoTable table;
	private SpotBottomTabAdapter spotBottomTabAdapter;
	private List<SpotBottomTabItem> spotBottomTabItems;

	private boolean hideOtherCoin = false;

	public static SpotTradeFragment newInstance() {
		Bundle args = new Bundle();
		SpotTradeFragment fragment = new SpotTradeFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.spot_fr_trade_new;
	}

	@Override
	public void initView(View rootView) {
		initTabSegment();

		mOrderLayout.setParentFragment(this, mTradeLayout);
		mTradeLayout.setParentFragment(this, false);
	}

	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_SCROLLABLE);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_1));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setTabTextSize(QMUIDisplayHelper.dp2px(getContext(),15));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(getContext(), 20));
		mTabSegment.setElevation(0);

		if (spotBottomTabItems == null) {
			spotBottomTabItems = new ArrayList<>();
		}
		if (spotBottomTabAdapter == null) {
			spotBottomTabItems.add(new SpotBottomTabItem(SpotBottomTab.CURRENT_ORDER, getString(R.string.cur_entrust_label), AccountType.SPOT));
			spotBottomTabItems.add(new SpotBottomTabItem(SpotBottomTab.HIGH_LEVEL_ORDER, getString(R.string.high_lever_order), AccountType.SPOT));
			spotBottomTabItems.add(new SpotBottomTabItem(SpotBottomTab.HISTORY_ORDER, getString(R.string.history_entrust_label_new), AccountType.SPOT));
			spotBottomTabAdapter = new SpotBottomTabAdapter(getChildFragmentManager(), spotBottomTabItems);
		}
		mViewPager.setOffscreenPageLimit(spotBottomTabItems.size());
		mViewPager.setAdapter(spotBottomTabAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
	}

	@SuppressLint("CheckResult")
	@Override
	public void setListener() {
		ivKline.setOnClickListener(v -> {
			//ARouter.getInstance().build(RouteHub.Kline.spotKlineActivity).withString("pairName", tvTradePair.getText().toString()).navigation(getContext());
			UIBusService.getInstance().openUri(getContext(), "coinbene://SpotKline?pairName=" + tvTradePair.getText().toString(), null);
		});

		ivSlef.setOnClickListener(v -> {
			if (ServiceRepo.getMarketService() != null) {
				PostPointHandler.postClickData(PostPointHandler.spot_coin_optional);
				//添加或者删除自选
				ServiceRepo.getMarketService().addOrDeleteOptional(symbol, new MarketService.CallBack() {
					@Override
					public void onSuccess() {
						setOptional();
					}

					@Override
					public void onFailed() {

					}
				});
			}
		});

		llTradePair.setOnClickListener(v -> {
			PostPointHandler.postClickData(PostPointHandler.spot_coin_change);
			Intent intent = new Intent(v.getContext(), SelectSearchPairActivity.class);
			intent.putExtra("type", SelectSearchPairActivity.TYPE_SELECT);
			intent.putExtra("fenmuAsset", table != null ? table.groupName : "");

			new InlineActivityResult(this)
					.startForResult(intent)
					.onSuccess(result -> {
						if (result.getResultCode() == Activity.RESULT_OK) {
							symbol = result.getData().getStringExtra("tradePairName");
							mAppBar.setExpanded(true);
							setSymbol(symbol);
						}
					});
		});

		mTabSegment.addOnTabSelectedListener(new QMUITabSelectedListener() {
			@Override
			public void onTabSelected(int index) {
				KeyboardUtils.hideKeyboard(mTradeLayout);
				if (index == 0) {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_current_entrust);
				} else if (index == 1) {
					PostPointHandler.postClickData(PostPointHandler.spot_coin_history);
				}
			}
		});

//		lottieViewMining.setOnClickListener(v -> UIBusService.getInstance().openUri(getActivity(), UrlUtil.getContractMiningUrl(), null));
	}

	@Override
	public void initData() {
		setSymbol(symbol);
	}

	/**
	 * 设置或者切换交易对
	 */
	public void setSymbol(String symbol) {
		table = TradePairInfoController.getInstance().queryDataByTradePair(symbol);

		if (table != null && !TextUtils.isEmpty(table.tradePairName)) {
			this.symbol = table.tradePairName;
			tvTradePair.setText(table.tradePairName);

			NewSpotWebsocket.getInstance().changeSymbol(symbol);

			mTradeLayout.setSymbol(symbol);
			mOrderLayout.setSymbol(symbol);

			setOptional();
			SpUtil.put(getContext(), SpUtil.SPOT_COIN, symbol);

			controlIEOView(table);
		}
	}

	private void controlIEOView(TradePairInfoTable table) {
		if (table.sellDisabled == 0) {
			mIeoLayout.setVisibility(View.GONE);
			return;
		}
		mIeoLayout.setVisibility(View.VISIBLE);
		String ieoLabel = getResources().getString(R.string.tips_ieo_label);
		mIeoLabel.setText(String.format(ieoLabel, TradeUtils.parseSymbol(table.tradePairName)[0]));
	}


	/**
	 * 设置是否自选
	 */
	private void setOptional() {
		if (!TradePairOptionalController.getInstance().isOptionalTradePair(symbol)) {
			ivSlef.setImageResource(R.drawable.res_icon_favorite);
		} else {
			ivSlef.setImageResource(R.drawable.icon_self_select);
		}
	}

	@Override
	public void onFragmentShow() {
		SpUtil.setSpotTradingOptions("coin");

		if (!TextUtils.isEmpty(symbol)) {
			setSymbol(symbol);
		}

		if (mTradeLayout != null) {
			mTradeLayout.registerUserEvent();
			mTradeLayout.setFirstPrice();
			mTradeLayout.setBalance();
			mTradeLayout.initPlaceOrderBtn();
		}

		changeTab();

//		showMiningAnimation();

		if (mOrderLayout != null) {
			mOrderLayout.registerDataListener();
		}
	}

	private void changeTab() {
		if (!TextUtils.isEmpty(MainActivityDelegate.getInstance().getSymbol())) {
			mAppBar.setExpanded(true);
			setSymbol(MainActivityDelegate.getInstance().getSymbol());
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
		if (mTradeLayout != null) {
			mTradeLayout.unregisterUserEvent();
		}

//		pauseMiningAnimation();

		if (mOrderLayout != null) {
			mOrderLayout.unRegisterDataListener();
		}
	}


//	/**
//	 * 是否展示合约挖矿动画
//	 */
//	private void showMiningAnimation() {
//		if (!TextUtils.isEmpty(symbol) && !"CFTUSDT".equals(symbol.replace("/", ""))) {
//			lottieViewMining.setVisibility(View.GONE);
//			return;
//		}
//		if (!SpUtil.getContractMiningSwitch()) {
//			//合约挖矿开关未开启
//			lottieViewMining.setVisibility(View.GONE);
//			return;
//		}
//		lottieViewMining.setVisibility(View.VISIBLE);
//		if (lottieViewMining != null && lottieViewMining.getVisibility() == View.VISIBLE) {
//			lottieViewMining.useHardwareAcceleration(true);
//			lottieViewMining.enableMergePathsForKitKatAndAbove(true);
//			lottieViewMining.playAnimation();
//		}
//	}

//	/**
//	 * 是否停止合约挖矿动画
//	 */
//	private void pauseMiningAnimation() {
//		if (lottieViewMining != null && lottieViewMining.getVisibility() == View.VISIBLE) {
//			lottieViewMining.cancelAnimation();
//		}
//	}

	@Override
	public String getTradePair() {
		return symbol;
	}

	@Override
	public void changeTradePair(String tradePair) {
		if (mAppBar != null) {
			mAppBar.setExpanded(true);
		}
		setSymbol(tradePair);
	}

	@Override
	public boolean getHideOtherCoin() {
		return this.hideOtherCoin;
	}

	@Override
	public void setHideOtherCoin(boolean hideOtherCoin) {
		this.hideOtherCoin = hideOtherCoin;
	}
}
