package com.coinbene.manbiwang.kline.newspotkline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractInfoTable;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.ContractUsdtInfoTable;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.contractkline.view.ContractKlineTopView;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentItem;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentType;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentTypeAdapter;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.TimeAndZhibiaoView;
import com.coinbene.manbiwang.kline.widget.KlineShareDialog;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;
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
public class NewContractKlineActivity extends BaseKlineActivity implements ActivityInterface {

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
	@BindView(R2.id.contract_top_view)
	ContractKlineTopView mTopView;
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
	@BindView(R2.id.layout_share_area)
	ConstraintLayout mLayoutShareArea;

	private NewKlineInterface.Presenter mPresenter;

	private KlineType klineType;
	private String symbol;

	private FragmentTypeAdapter fragmentTypeAdapter;
	private List<FragmentItem> fragmentItems;
	private List<IActivityListener> listenerList;

	private KLineChartAdapter mAdapter;

	private KlineShareDialog shareDialog;
	private ContractChangePopWindow contractChangePopWindow;

	@AddFlowControl(timeInterval = 200)
	public static void startMe(Context context, String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}

		Intent intent = new Intent(context, NewContractKlineActivity.class);
		intent.putExtra("pairName", tradePairName);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_newcontract_kline_activity;
	}

	@Override
	public void initView() {
		setSwipeBackEnable(false);

		setMerBarWhite();

		if (getIntent() != null) {
			symbol = getIntent().getStringExtra("pairName");
			initTradePair(symbol);
		}

		if (TextUtils.isEmpty(symbol)) {
			finish();
			return;
		}

		//websocket切换交易对
		if (klineType == KlineType.BTC_CONTRACT) {
			NewContractBtcWebsocket.getInstance().changeSymbol(symbol);
		} else if (klineType == KlineType.USDT_CONTRACT) {
			NewContractUsdtWebsocket.getInstance().changeSymbol(symbol);
		}

		initTabSegment();

		mIvContractGuide.setVisibility(View.VISIBLE);

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
			bundle.putString("tab", "contract");
			bundle.putString("subTab", klineType == KlineType.BTC_CONTRACT ? "btc" : "usdt");
			bundle.putString("symbol", symbol);
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
			finish();
		});

		mSellLayout.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "contract");
			bundle.putString("subTab", klineType == KlineType.BTC_CONTRACT  ? "btc" : "usdt");
			bundle.putString("symbol", symbol);
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
			finish();
		});

		mIvContractGuide.setOnClickListener(v -> {
			String url = klineType == KlineType.BTC_CONTRACT ? UrlUtil.getBtcContractGuideUrl() : UrlUtil.getUsdtContractGuideUrl();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.WEB_EXTRA_TITLE, klineType == KlineType.BTC_CONTRACT
					? getResources().getString(R.string.res_btc_contract_guide) : getResources().getString(R.string.res_usdt_contract_guide));
			bundle.putInt(Constants.WEB_EXTRA_RIGHT_IMAGE, R.drawable.guide_title_rigth);
			bundle.putString(Constants.WEB_EXTRA_RIGHT_URL, "coinbene://" + UIRouter.HOST_CUSTOMER);
			UIBusService.getInstance().openUri(v.getContext(), url, bundle);
		});

		mIvShare.setOnClickListener(v -> showShareDialog());
		mIvChangeOrien.setOnClickListener(v -> NewContractKlineLandActivity.startMe(this, symbol));
	}



	@Override
	public void initData() {

	}

	private void initTradePair(String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}

		symbol = tradePairName;

		klineType = tradePairName.contains("-") ? KlineType.USDT_CONTRACT : KlineType.BTC_CONTRACT;

		if (klineType == KlineType.BTC_CONTRACT) {
			ContractInfoTable infoTable = ContractInfoController.getInstance().queryContrackByName(symbol);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);

				//设置价格精度
				mChartView.setValueFormatter(new PrecisionValueFormatter(infoTable.precision));
			}
			int klineTimeStatus = SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1);
			if (mPresenter == null) {
				mPresenter = new NewKlinePresenter(this, symbol, getKlineTimeType(klineTimeStatus), klineType);
			} else {
				mPresenter.changeSymbol(symbol);
			}
		} else {
			ContractUsdtInfoTable infoTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);

				//设置价格精度
				mChartView.setValueFormatter(new PrecisionValueFormatter(infoTable.precision));
			}
			int klineTimeStatus = SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1);
			if (mPresenter == null) {
				mPresenter = new NewKlinePresenter(this, symbol, getKlineTimeType(klineTimeStatus), klineType);
			} else {
				mPresenter.changeSymbol(symbol);
			}
		}

		mainType = SpUtil.get(this, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		subType = SpUtil.get(this, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);

		if (TradeUtils.isUsdtContract(symbol)) {
			mTvTitle.setText(String.format(getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(symbol)));
		} else {
			mTvTitle.setText(String.format(getString(R.string.forever_no_delivery), symbol));
		}
	}

	private void initTabSegment() {
		mTabSegment.setMode(QMUITabSegment.MODE_FIXED);
		mTabSegment.setHasIndicator(true);
		mTabSegment.setIndicatorPosition(false);
		mTabSegment.setIndicatorWidthAdjustContent(true);
		mTabSegment.setDefaultNormalColor(getResources().getColor(R.color.res_textColor_2));
		mTabSegment.setDefaultSelectedColor(getResources().getColor(R.color.res_blue));
		mTabSegment.setItemSpaceInScrollMode(QMUIDisplayHelper.dp2px(this, 20));

		if (fragmentItems == null) {
			fragmentItems = new ArrayList<>();
		}
		if (fragmentTypeAdapter == null) {
			fragmentItems.add(new FragmentItem(FragmentType.ORDERLIST, getString(R.string.order_book)));
			fragmentItems.add(new FragmentItem(FragmentType.TRADEDETAIL, getString(R.string.market_trades)));
			//fragmentItems.add(new FragmentItem(FragmentType.COININFO, getString(R.string.coin_info)));
			fragmentTypeAdapter = new FragmentTypeAdapter(getSupportFragmentManager(), fragmentItems);
		}
		mViewPager.setOffscreenPageLimit(fragmentItems.size());
		mViewPager.setAdapter(fragmentTypeAdapter);
		mTabSegment.setupWithViewPager(mViewPager);
	}

	private void changeTradePair() {
		if (klineType == KlineType.BTC_CONTRACT) {
			contractChangePopWindow = ServiceRepo.getContractService().getBtcContractChangePopWindow(mCenterView, true);
		} else {
			contractChangePopWindow = ServiceRepo.getContractService().getUsdtContractChangePopWindow(mCenterView, true);
		}
		contractChangePopWindow.showBelowAnchor();

		contractChangePopWindow.setOnItemClickContrctListener(item -> {
			WsMarketData dataBean = (WsMarketData) item;
			contractChangePopWindow.dismiss();
			if (!TextUtils.isEmpty(symbol) && symbol.equals(dataBean.getSymbol())) {
				return;
			}
			symbol = dataBean.getSymbol();
			onTradePairChanged(symbol);
		});
	}

	private void onTradePairChanged(String tradePairName) {
		if (!TextUtils.isEmpty(tradePairName)) {
			initTradePair(tradePairName);
		}

		//切换合约交易队
		if (klineType == KlineType.BTC_CONTRACT) {
			NewContractBtcWebsocket.getInstance().changeSymbol(tradePairName);
		} else {
			NewContractUsdtWebsocket.getInstance().changeSymbol(tradePairName);
		}

		if (listenerList != null) {
			synchronized (listenerList) {
				for (IActivityListener listener : listenerList) {
					listener.onTradePairChanged(tradePairName);
				}
			}
		}
	}

	private void setOptional() {

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
			listener.onTradePairChanged(symbol);
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
		contractChangePopWindow = null;
	}

	@Override
	public void onTickerData(WsMarketData tickerData) {
		runOnUiThread(() -> mTopView.setData(tickerData));
	}

	@Override
	public void onKlineData(List<KLineEntity> datas, boolean isFull) {
		handleKlineData(datas, isFull, mChartView, mAdapter);
	}
}
