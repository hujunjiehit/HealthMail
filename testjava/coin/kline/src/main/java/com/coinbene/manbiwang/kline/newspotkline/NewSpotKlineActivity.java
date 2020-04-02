package com.coinbene.manbiwang.kline.newspotkline;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.activities.SelectSearchPairActivity;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.ResourceProvider;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentItem;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentType;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentTypeAdapter;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.TimeAndZhibiaoView;
import com.coinbene.manbiwang.kline.spotkline.view.TopView;
import com.coinbene.manbiwang.kline.widget.KlineShareDialog;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.market.MarketService;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.formatter.PrecisionValueFormatter;
import com.google.android.material.appbar.AppBarLayout;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.qmuiteam.qmui.util.QMUIDrawableHelper;
import com.qmuiteam.qmui.widget.QMUITabSegment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-11-23
 * <p>
 * 重构后的竖屏K线Activity
 */
@Route(path = RouteHub.Kline.spotKlineActivity)
public class NewSpotKlineActivity extends BaseKlineActivity implements ActivityInterface {

	@BindView(R2.id.back_view)
	View mBackView;
	@BindView(R2.id.tv_title)
	TextView mTvTitle;
	@BindView(R2.id.iv_down_icon)
	ImageView mIvDownIcon;
	@BindView(R2.id.center_view)
	View mCenterView;
	@BindView(R2.id.iv_contract_guide)
	ImageView mIvContractGuide;
	@BindView(R2.id.iv_share)
	ImageView mIvShare;
	@BindView(R2.id.iv_change_orien)
	ImageView mIvChangeOrien;
	@BindView(R2.id.top_view_portrait)
	TopView mTopView;
	@BindView(R2.id.time_and_zhibiao_view)
	TimeAndZhibiaoView mTimeAndZhibiaoView;
	@BindView(R2.id.chart_view)
	KLineChartView mChartView;
	@BindView(R2.id.tab_segment)
	QMUITabSegment mTabSegment;
	@BindView(R2.id.app_bar)
	AppBarLayout mAppBar;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.buy_layout)
	TextView mBuyLayout;
	@BindView(R2.id.sell_layout)
	TextView mSellLayout;
	@BindView(R2.id.menu_right_iv)
	ImageView mMenuRightIv;
	@BindView(R2.id.tv_favorites_stutes)
	TextView mTvFavoritesStutes;
	@BindView(R2.id.ll_add_self)
	LinearLayout mLlAddSelf;
	@BindView(R2.id.bottom_layout)
	LinearLayout mBottomLayout;
	@BindView(R2.id.layout_share_area)
	ConstraintLayout mLayoutShareArea;

	private NewKlineInterface.Presenter mPresenter;
	private String symbol;

	@Autowired(name = "pairName")
	String tradePairName;

	private boolean isOptinal = false;

	private FragmentTypeAdapter fragmentTypeAdapter;
	private List<FragmentItem> fragmentItems;
	private List<IActivityListener> listenerList;

	private KLineChartAdapter mAdapter;

	private KlineShareDialog shareDialog;

	@AddFlowControl(timeInterval = 200)
	public static void startMe(Context context, String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		Intent intent = new Intent(context, NewSpotKlineActivity.class);
		intent.putExtra("pairName", tradePairName);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_newspot_kline_activity;
	}

	@Override
	public void initView() {
		setSwipeBackEnable(false);

		setMerBarWhite();

		if (getIntent() != null) {
			tradePairName = getIntent().getStringExtra("pairName");
			initTradePair(tradePairName);
		}

		if (TextUtils.isEmpty(symbol)) {
			finish();
			return;
		}

		//websocket切换交易对
		NewSpotWebsocket.getInstance().changeSymbol(symbol);

		initTabSegment();

		mBuyLayout.setBackground(getResources().getDrawable(SwitchUtils.isRedRise() ? R.drawable.btn_red_bg : R.drawable.btn_green_bg));
		mSellLayout.setBackground(getResources().getDrawable(SwitchUtils.isRedRise() ? R.drawable.btn_green_bg : R.drawable.btn_red_bg));

		mAdapter = new KLineChartAdapter();
		mChartView.setAdapter(mAdapter);
		mChartView.setGridRows(4);
		mChartView.setGridColumns(5);

		initChartView(mChartView, SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public void setListener() {
		mBackView.setOnClickListener(v -> finish());

		mCenterView.setOnClickListener(v -> changeTradePair());

		mTimeAndZhibiaoView.setTimeListener(timeStatus -> {
			initChartView(mChartView, timeStatus);
			mPresenter.changeTime(getKlineTimeType(timeStatus));
		});

		mTimeAndZhibiaoView.setZhibiaoListener(new ZhibiaoListener() {
			@Override
			public void onMasterSelected(int masterType) {
				//mChartView.setMasterType(masterType);
				mainType = masterType;
				setMainDraw(mChartView);

			}

			@Override
			public void onSub2Selected(String type) {
				//mChartView.setSubZhibiao(type);
				subType = type;
				setMainDraw(mChartView);
			}
		});

		//解决长按滑动冲突问题
		handleTouchListener(mChartView, mAppBar);

		mBuyLayout.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "spot");
			bundle.putString("subTab", "coin");
			bundle.putString("symbol", symbol);
			bundle.putString("type", "1");
			UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
		});

		mSellLayout.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "spot");
			bundle.putString("subTab", "coin");
			bundle.putString("symbol", symbol);
			bundle.putString("type", "2");
			UIBusService.getInstance().openUri(v.getContext(), UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
		});

		mLlAddSelf.setOnClickListener(v -> {
			if (ServiceRepo.getMarketService() != null) {
				//添加或者删除自选
				ServiceRepo.getMarketService().addOrDeleteOptional(symbol, new MarketService.CallBack() {
					@Override
					public void onSuccess() {
						if (isFinishing() || isDestroyed()) {
							return;
						}
						setOptional();
					}

					@Override
					public void onFailed() {

					}
				});
			}
		});

		mIvShare.setOnClickListener(v -> showShareDialog());
		mIvChangeOrien.setOnClickListener(v -> NewSpotKlineLandActivity.startMe(this, tradePairName));
	}

	@Override
	public void initData() {

	}

	private void initTradePair(String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		TradePairInfoTable infoTable = TradePairInfoController.getInstance().queryDataByTradePairName(tradePairName);
		if (infoTable != null) {
			Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.pricePrecision);
			Constants.newScale = infoTable.pricePrecision;

			//设置价格精度
			mChartView.setValueFormatter(new PrecisionValueFormatter(infoTable.pricePrecision));
		}

		symbol = infoTable.tradePair;

		int klineTimeStatus = SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1);

		mainType = SpUtil.get(this, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		subType = SpUtil.get(this, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);

		if (mPresenter == null) {
			mPresenter = new NewKlinePresenter(this, symbol, getKlineTimeType(klineTimeStatus));
		} else {
			mPresenter.changeSymbol(symbol);
		}

		mTvTitle.setText(tradePairName);
	}

	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(ResourceProvider.getColor("#6d87a8"));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(this, 20));

		if (fragmentItems == null) {
			fragmentItems = new ArrayList<>();
		}
		if (fragmentTypeAdapter == null) {
			fragmentItems.add(new FragmentItem(FragmentType.ORDERLIST, getString(R.string.order_book)));
			fragmentItems.add(new FragmentItem(FragmentType.TRADEDETAIL, getString(R.string.market_trades)));
			fragmentItems.add(new FragmentItem(FragmentType.COININFO, getString(R.string.coin_info)));
			fragmentTypeAdapter = new FragmentTypeAdapter(getSupportFragmentManager(), fragmentItems);
		}
		mViewPager.setOffscreenPageLimit(fragmentItems.size());
		mViewPager.setAdapter(fragmentTypeAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
	}

	private void changeTradePair() {
		Intent intent = new Intent(this, SelectSearchPairActivity.class);
		intent.putExtra("type", SelectSearchPairActivity.TYPE_SELECT);
		if (tradePairName.contains("/")) {
			intent.putExtra("fenmuAsset", tradePairName.split("/")[1]);
		}
		new InlineActivityResult(this)
				.startForResult(intent)
				.onSuccess(result -> {
					if (result.getResultCode() == Activity.RESULT_OK) {
						tradePairName = result.getData().getStringExtra("tradePairName");


						onTradePairChanged(tradePairName);
					}
				});
	}

	private void onTradePairChanged(String tradePairName) {
		if (!TextUtils.isEmpty(tradePairName)) {
			initTradePair(tradePairName);
		}

		NewSpotWebsocket.getInstance().changeSymbol(tradePairName);

		if (listenerList != null) {
			synchronized (listenerList) {
				for (IActivityListener listener : listenerList) {
					listener.onTradePairChanged(tradePairName);
				}
			}
		}
	}

	private void setOptional() {
		isOptinal = TradePairOptionalController.getInstance().isOptionalTradePair(tradePairName);
		if (!isOptinal) {
			mMenuRightIv.setImageResource(R.drawable.icon_self);
			mTvFavoritesStutes.setText(R.string.add_favorite);
		} else {
			mMenuRightIv.setImageResource(R.drawable.icon_self_select);
			mTvFavoritesStutes.setText(R.string.added);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int timeStatus = SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1);
		mainType = SpUtil.get(this, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		subType = SpUtil.get(this, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);

		initChartView(mChartView, timeStatus);
		mPresenter.updateTimeType(getKlineTimeType(timeStatus));

		mPresenter.onResume();
		setOptional();
		mTimeAndZhibiaoView.initData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPresenter.onPause();
	}

	@Override
	public boolean needLock() {
		return false;
	}

	private void showShareDialog() {
		if (shareDialog == null) {
			shareDialog = new KlineShareDialog(this);
		}

		mTopView.setBackgroundColor(getResources().getColor(R.color.kline_top_share_background));
		shareDialog.setShareContent(screenShot());
		mTopView.setBackgroundColor(getResources().getColor(R.color.kline_black_131e2f));
		shareDialog.setTradePair(mTvTitle.getText().toString());
		shareDialog.show();
	}

	/**
	 * @return bitmap
	 * 截图
	 */
	private Bitmap screenShot() {
		//关闭硬件加速，否则截图为初始绘制
		mLayoutShareArea.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		return QMUIDrawableHelper.createBitmapFromView(mLayoutShareArea);
	}

	@Override
	public void registerActivityListener(IActivityListener listener) {
		if (listenerList == null) {
			listenerList = Collections.synchronizedList(new ArrayList<>());
		}
		if (!listenerList.contains(listener)) {
			listener.onTradePairChanged(tradePairName);
			listenerList.add(listener);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (listenerList != null) {
			listenerList.clear();
			listenerList = null;
		}

		if (mPresenter != null) {
			mPresenter.onDestory();
		}
		if (null != shareDialog && shareDialog.isShowing()) {
			shareDialog.dismiss();
			shareDialog = null;
		}
	}

	@Override
	public void onTickerData(WsMarketData tickerData) {
		runOnUiThread(() -> mTopView.setData(tradePairName, tickerData));
	}

	@Override
	public void onKlineData(List<KLineEntity> datas, boolean isFull) {
		handleKlineData(datas, isFull, mChartView, mAdapter);
	}
}
