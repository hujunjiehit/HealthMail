package com.coinbene.manbiwang.kline.spotkline;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.PrecisionUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.R2;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.spotkline.listener.ChartViewListener;
import com.coinbene.manbiwang.kline.spotkline.listener.ZhibiaoListener;
import com.coinbene.manbiwang.kline.spotkline.view.ChartView;
import com.coinbene.manbiwang.kline.spotkline.view.TimeViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.TopViewLand;
import com.coinbene.manbiwang.kline.spotkline.view.ZhibiaoViewLand;
import com.coinbene.manbiwang.model.http.KlineTopData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;

/**
 * Created by june
 * on 2019-11-22
 *
 * 重构后的横屏K线Activity
 */
public class SpotKlineLandActivity extends CoinbeneBaseActivity implements KlineInterface.View {

	@BindView(R2.id.zhibiao_view)
	ZhibiaoViewLand mZhibiaoView;
	@BindView(R2.id.layout_close)
	View mLayoutClose;
	@BindView(R2.id.top_view)
	TopViewLand mTopView;
	@BindView(R2.id.chart_view)
	ChartView mChartView;
	@BindView(R2.id.time_view)
	TimeViewLand mTimeView;

	private KlineInterface.Presenter mPresenter;
	private String symbol;
	private String tradePairName;

	private TimerTask timerTask;
	private Timer timer;
	private static final long PERIOD_TIME = 3000;

	@AddFlowControl
	public static void startMe(Context context, String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		Intent intent = new Intent(context, SpotKlineLandActivity.class);
		intent.putExtra("pairName", tradePairName);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.kline_spot_land_activity;
	}

	@Override
	public void initView() {
		if (getIntent() != null) {
			tradePairName = getIntent().getStringExtra("pairName");
			initTradePair(tradePairName);
		}

		if (TextUtils.isEmpty(symbol)) {
			finish();
			return;
		}

		NewSpotWebsocket.getInstance().changeSymbol(symbol);
	}

	private void initTradePair(String tradePairName) {
		if (TextUtils.isEmpty(tradePairName)) {
			return;
		}
		TradePairInfoTable infoTable = TradePairInfoController.getInstance().queryDataByTradePairName(tradePairName);
		if (infoTable != null) {
			Constants.accurasyStr = PrecisionUtils.changeNumToPrecisionStr(infoTable.pricePrecision);
			Constants.newScale = infoTable.pricePrecision;
		}

		symbol = infoTable.tradePair;

		if (mPresenter == null) {
			mPresenter = new SpotKlinePresenter(this, this, symbol);
		} else {
			mPresenter.updateSymbol(symbol);
		}
	}

	@Override
	public void setListener() {
		mLayoutClose.setOnClickListener(v -> finish());

		mZhibiaoView.setZhibiaoListener(new ZhibiaoListener() {
			@Override
			public void onMasterSelected(int masterType) {
				mChartView.setMasterType(masterType);
			}

			@Override
			public void onSub2Selected(String type) {
				mChartView.setSubZhibiao(type);
			}
		});


		mTimeView.setTimeListener(timeStatus -> initParamsAndLoadData(timeStatus));

		mChartView.setChartViewListener(new ChartViewListener() {
			@Override
			public void highLightPress(int index) {
				if (index < 0 || index >= mPresenter.getkLineDatas().size()) {
					return;
				}
				mTopView.highLightPress(mPresenter.getkLineDatas().get(index));
				mChartView.updateText(index);
			}

			@Override
			public void hideHighValueSelectedValue() {
				mTopView.hideHighValueSelectedValue();
				mChartView.updateText(mPresenter.getkLineDatas().size() - 1);
			}
		});
	}

	@Override
	public void initData() {
		initParamsAndLoadData(SpUtil.get(this, SpUtil.K_LINE_TIME_STATUS, 1));
	}

	@Override
	protected void onResume() {
		super.onResume();
		setFitsSystemWindows(true);
		mPresenter.onResume();

		timer = new Timer();
		timerTask = new TimerTask() {
			@Override
			public void run() {
				if (!WebSocketManager.getInstance().getConnectSubscriberStatus(Constants.BASE_WEBSOCKET)) {
					//loadHttpOrderList();
				}
			}
		};
		timer.schedule(timerTask, 0, PERIOD_TIME);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPresenter.onPause();

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void initParamsAndLoadData(int timeStatus) {
		mChartView.setFenshi(timeStatus == 9);

		//初始化网络请求参数
		mPresenter.initParams(timeStatus);

		//加载k数据
		mPresenter.loadKlineData();
	}

	@Override
	public boolean needLock() {
		return false;
	}


	/**
	 * K线http请求成功
	 */
	@Override
	public void onKlineDataLoadSuccess(DataParse dataParse, List<KLineBean> kLineDatas) {
		if (mTopView != null) {
			mTopView.hideHighValueSelectedValue();
		}
		if (mChartView != null) {
			mChartView.setData(dataParse, kLineDatas);
		}
	}

	/**
	 * 接收到websocket行情数据，更新topview
	 */
	@Override
	public void onQuoteDataReceived(WsMarketData quote) {
		runOnUiThread(() -> {
			if (mTopView != null) {
				mTopView.setData(tradePairName, quote);
			}
		});
	}

	@Override
	public void onRiseTypeChanged(int riseType) {

	}



	//顶部数据和14档图
	private void loadHttpOrderList() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("tradePairName", tradePairName);
		httpParams.put("num", Constants.MARKET_NUMBER_FORETEEN);
		OkGo.<KlineTopData>get(Constants.MARKET_ORDER_LIST).params(httpParams).tag(this).execute(new NewJsonSubCallBack<KlineTopData>() {

			@Override
			public void onSuc(Response<KlineTopData> response) {

			}

			@Override
			public KlineTopData dealJSONConvertedResult(KlineTopData klineTopData) {
				if (klineTopData.getData().quote != null) {
					//onQuoteDataReceived(klineTopData.getData().quote);
				}
				return super.dealJSONConvertedResult(klineTopData);
			}


			@Override
			public void onE(Response<KlineTopData> response) {

			}

		});
	}
}
