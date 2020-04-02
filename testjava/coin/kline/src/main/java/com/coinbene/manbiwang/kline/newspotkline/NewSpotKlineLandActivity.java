package com.coinbene.manbiwang.kline.newspotkline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.TimeViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.TopViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.ZhibiaoViewLand;
import com.github.fujianlian.klinechart.KLineChartAdapter;
import com.github.fujianlian.klinechart.KLineChartView;
import com.github.fujianlian.klinechart.KLineEntity;
import com.github.fujianlian.klinechart.formatter.PrecisionValueFormatter;

import java.util.List;

import butterknife.BindView;

/**
 * Created by june
 * on 2020-04-01
 */
public class NewSpotKlineLandActivity extends BaseKlineActivity {
	@BindView(R2.id.btn_close)
	ImageButton mBtnClose;
	@BindView(R2.id.layout_close)
	RelativeLayout mLayoutClose;
	@BindView(R2.id.top_view)
	TopViewLand mTopView;
	@BindView(R2.id.chart_view)
	KLineChartView mChartView;
	@BindView(R2.id.zhibiao_view)
	ZhibiaoViewLand mZhibiaoView;
	@BindView(R2.id.time_view)
	TimeViewLand mTimeView;

	@Autowired(name = "pairName")
	String tradePairName;

	private NewKlineInterface.Presenter mPresenter;
	private String symbol;
	private KLineChartAdapter mAdapter;

	@AddFlowControl(timeInterval = 200)
	public static void startMe(Context context, String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		Intent intent = new Intent(context, NewSpotKlineLandActivity.class);
		intent.putExtra("pairName", tradePairName);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_new_spot_land_activity;
	}

	@Override
	public void initView() {
		setSwipeBackEnable(false);

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

		mAdapter = new KLineChartAdapter();
		mChartView.setAdapter(mAdapter);
		mChartView.setGridRows(4);
		mChartView.setGridColumns(5);

		initChartView(mChartView, SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
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
		isFenshi = klineTimeStatus == 9;

		mainType = SpUtil.get(this, SpUtil.K_LINE_MA_BOLL_STATUS, ZhibiaoListener.MASTER_TYPE_MA);
		subType = SpUtil.get(this, SpUtil.K_LINE_ZHIBIAO_STATUS, ZhibiaoListener.SUB2_HIDE);

		if (mPresenter == null) {
			mPresenter = new NewKlinePresenter(this, symbol, getKlineTimeType(klineTimeStatus));
		} else {
			mPresenter.changeSymbol(symbol);
		}
	}

	@Override
	public void setListener() {
		mLayoutClose.setOnClickListener(v -> finish());

		mZhibiaoView.setZhibiaoListener(new ZhibiaoListener() {
			@Override
			public void onMasterSelected(int masterType) {
				mainType = masterType;
				setMainDraw(mChartView);
			}

			@Override
			public void onSub2Selected(String type) {
				subType = type;
				setMainDraw(mChartView);
			}
		});

		mTimeView.setTimeListener(timeStatus -> {
			initChartView(mChartView, timeStatus);
			mPresenter.changeTime(getKlineTimeType(timeStatus));
		});
	}

	@Override
	public void initData() {

	}

	@Override
	protected void onResume() {
		super.onResume();
		setFitsSystemWindows(true);
		mPresenter.onResume();
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

	@Override
	public void onKlineData(List<KLineEntity> datas, boolean isFull) {
		handleKlineData(datas, isFull, mChartView, mAdapter);
	}

	@Override
	public void onTickerData(WsMarketData tickerData) {
		runOnUiThread(() -> mTopView.setData(tradePairName, tickerData));
	}
}
