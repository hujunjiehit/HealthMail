package com.coinbene.manbiwang.kline.contractkline;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
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
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.contractkline.view.ContractKlineTopView;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentItem;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentType;
import com.coinbene.manbiwang.kline.fragment.adapter.FragmentTypeAdapter;
import com.coinbene.manbiwang.kline.fragment.klineinterface.ActivityInterface;
import com.coinbene.manbiwang.kline.spotkline.KlineInterface;
import com.coinbene.manbiwang.kline.spotkline.listener.ChartViewListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.ChartView;
import com.coinbene.manbiwang.kline.spotkline.view.TimeAndZhibiaoView;
import com.coinbene.manbiwang.kline.widget.KlineShareDialog;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.contract.ContractChangePopWindow;
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
 * on 2019-11-26
 */
public class ContractKlineActivity extends CoinbeneBaseActivity implements KlineInterface.View, ActivityInterface {

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
	@BindView(R2.id.contract_top_view)
	ContractKlineTopView mContractKlineTopView;
	@BindView(R2.id.time_and_zhibiao_view)
	TimeAndZhibiaoView mTimeAndZhibiaoView;
	@BindView(R2.id.chart_view)
	ChartView mChartView;
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

	private KlineInterface.Presenter mPresenter;
	private String symbol;
	private int contractType;

	private FragmentTypeAdapter fragmentTypeAdapter;
	private List<FragmentItem> fragmentItems;
	private List<ActivityInterface.IActivityListener> listenerList;

	private ContractChangePopWindow contractChangePopWindow;
	private KlineShareDialog shareDialog;

	@AddFlowControl(timeInterval = 200)
	public static void startMe(Context context, String symbol) {
		if (TextUtils.isEmpty(symbol)) {
			return;
		}
		Intent intent = new Intent(context, ContractKlineActivity.class);
		intent.putExtra("symbol", symbol);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_contract_kline_activity;
	}

	@Override
	public void initView() {
		setMerBarWhite();

		if (getIntent() != null) {
			symbol = getIntent().getStringExtra("symbol");
			contractType = symbol.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;

			initSymbol(symbol);
		}


		if (TextUtils.isEmpty(symbol)) {
			finish();
			return;
		}


		if (symbol.contains("-")) {
			NewContractUsdtWebsocket.getInstance().changeSymbol(symbol);
		} else {
			NewContractBtcWebsocket.getInstance().changeSymbol(symbol);
		}

		initTabSegment();

		mIvContractGuide.setVisibility(View.VISIBLE);


		mBuyLayout.setBackground(getResources().getDrawable(SwitchUtils.isRedRise() ? R.drawable.btn_red_bg : R.drawable.btn_green_bg));
		mSellLayout.setBackground(getResources().getDrawable(SwitchUtils.isRedRise() ? R.drawable.btn_green_bg : R.drawable.btn_red_bg));
	}

	@Override
	protected void onSaveInstanceState(@NonNull Bundle outState) {
//		super.onSaveInstanceState(outState);
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//		super.onSaveInstanceState(outState, outPersistentState);
	}

	@Override
	public void setListener() {
		mBackView.setOnClickListener(v -> finish());

		mCenterView.setOnClickListener(v -> changeTradePair());

		mIvContractGuide.setOnClickListener(v -> {
			String url = contractType == Constants.CONTRACT_TYPE_BTC ? UrlUtil.getBtcContractGuideUrl() : UrlUtil.getUsdtContractGuideUrl();
			Bundle bundle = new Bundle();
			bundle.putString(Constants.WEB_EXTRA_TITLE, contractType == Constants.CONTRACT_TYPE_BTC
					? getResources().getString(R.string.res_btc_contract_guide) : getResources().getString(R.string.res_usdt_contract_guide));
			bundle.putInt(Constants.WEB_EXTRA_RIGHT_IMAGE, R.drawable.guide_title_rigth);
			bundle.putString(Constants.WEB_EXTRA_RIGHT_URL, "coinbene://" + UIRouter.HOST_CUSTOMER);
			UIBusService.getInstance().openUri(v.getContext(), url, bundle);
		});

		mTimeAndZhibiaoView.setTimeListener(timeStatus -> initParamsAndLoadData(timeStatus));

		mTimeAndZhibiaoView.setZhibiaoListener(new ZhibiaoListener() {
			@Override
			public void onMasterSelected(int masterType) {
				mChartView.setMasterType(masterType);
			}

			@Override
			public void onSub2Selected(String type) {
				mChartView.setSubZhibiao(type);
			}
		});


		mChartView.setChartViewListener(new ChartViewListener() {
			@Override
			public void highLightPress(int index) {
				//长按K线
				mTimeAndZhibiaoView.setVisibility(View.GONE);

				if (index < 0 || index >= mPresenter.getkLineDatas().size()) {
					return;
				}
				mContractKlineTopView.highLightPress(mPresenter.getkLineDatas().get(index));
			}

			@Override
			public void hideHighValueSelectedValue() {
				//取消长按
				mTimeAndZhibiaoView.setVisibility(View.VISIBLE);
				mContractKlineTopView.hideHighValueSelectedValue();
			}
		});

		mBuyLayout.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "contract");
			bundle.putString("subTab", contractType == Constants.CONTRACT_TYPE_BTC ? "btc" : "usdt");
			bundle.putString("symbol", symbol);
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
			finish();
		});

		mSellLayout.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			bundle.putString("tab", "contract");
			bundle.putString("subTab", contractType == Constants.CONTRACT_TYPE_BTC ? "btc" : "usdt");
			bundle.putString("symbol", symbol);
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
			finish();
		});

		mIvShare.setOnClickListener(v -> showShareDialog());
	}

	private void changeTradePair() {
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
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
			initSymbol(symbol);
			initParamsAndLoadData(SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
			mContractKlineTopView.setSymbol(symbol);
		}

		//切换合约交易队
		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			NewContractBtcWebsocket.getInstance().changeSymbol(symbol);
		} else {
			NewContractUsdtWebsocket.getInstance().changeSymbol(symbol);
		}

		if (listenerList != null) {
			synchronized (listenerList) {
				for (IActivityListener listener : listenerList) {
					listener.onTradePairChanged(tradePairName);
				}
			}
		}
	}


	@Override
	public void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		mPresenter.onResume();
		mTimeAndZhibiaoView.initData();
		mChartView.initData();
		initParamsAndLoadData(SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPresenter.onPause();
	}

	private void initParamsAndLoadData(int timeStatus) {
		mChartView.setFenshi(timeStatus == 9);

		//初始化网络请求参数
		mPresenter.initParams(timeStatus);

		//加载k数据
		mPresenter.loadKlineData();
	}

	/**
	 * 初始化精度
	 *
	 * @param symbol
	 */
	private void initSymbol(String symbol) {

		if (contractType == Constants.CONTRACT_TYPE_BTC) {
			ContractInfoTable infoTable = ContractInfoController.getInstance().queryContrackByName(symbol);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);
			}
		} else {
			ContractUsdtInfoTable infoTable = ContractUsdtInfoController.getInstance().queryContrackByName(symbol);
			mContractKlineTopView.setContractType(contractType);
			mContractKlineTopView.setSymbol(symbol);
			mContractKlineTopView.setContractTable(infoTable);
			mChartView.setContractType(contractType);
			mChartView.setContractTable(infoTable);
			if (infoTable != null) {
				Constants.newScale = infoTable.precision;
				Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.precision);
			}
		}

		if (mPresenter == null) {
			mPresenter = new ContractKlinePresenter(this, this, symbol);
		} else {
			mPresenter.updateSymbol(symbol);
		}
		if (!TextUtils.isEmpty(symbol) && symbol.contains("-")) {
			mTvTitle.setText(String.format(getString(R.string.forever_no_delivery), TradeUtils.getUsdtContractBase(symbol)));
		} else
			mTvTitle.setText(String.format(getString(R.string.forever_no_delivery), symbol));


		mChartView.setTradePairName(symbol);
		mChartView.setIsContract(true);
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

	@Override
	public boolean needLock() {
		return false;
	}

	private void showShareDialog() {
		if (shareDialog == null) {
			shareDialog = new KlineShareDialog(this);
		}
		mContractKlineTopView.setBackgroundColor(getResources().getColor(R.color.kline_top_share_background));
		shareDialog.setShareContent(screenShot());
		mContractKlineTopView.setBackgroundColor(getResources().getColor(R.color.kline_black_131e2f));
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
	public void onKlineDataLoadSuccess(DataParse dataParse, List<KLineBean> kLineDatas) {
		if (mContractKlineTopView != null) {
			mChartView.hideLine();
			mTimeAndZhibiaoView.setVisibility(View.VISIBLE);
			mContractKlineTopView.hideHighValueSelectedValue();
		}
		if (mChartView != null) {
			mChartView.setData(dataParse, kLineDatas);
		}
	}

	@Override
	public void onQuoteDataReceived(WsMarketData quote) {
		runOnUiThread(() -> {
			if (mContractKlineTopView != null) {
				mContractKlineTopView.setData(quote);
			}
		});
	}

	@Override
	public void onRiseTypeChanged(int riseType) {
		runOnUiThread(() -> {
			if (mContractKlineTopView != null) {
				mContractKlineTopView.setRiseType(riseType);
			}
		});
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

		if (null != shareDialog && shareDialog.isShowing()) {
			shareDialog.dismiss();
			shareDialog = null;
		}
	}
}
