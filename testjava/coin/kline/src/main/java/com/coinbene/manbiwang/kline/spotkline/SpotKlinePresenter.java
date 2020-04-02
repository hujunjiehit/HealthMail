package com.coinbene.manbiwang.kline.spotkline;

import android.app.Activity;
import android.content.Context;

import com.coinbene.common.Constants;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.bean.KlineData;
import com.github.mikephil.coinbene.data.BarEntry;
import com.github.mikephil.coinbene.data.CandleEntry;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by june
 * on 2019-11-22
 */
public class SpotKlinePresenter implements KlineInterface.Presenter {

	/**
	 * 涨跌颜色
	 */
	private boolean isRedRise;
	private int mRiseColor, mDropColor;

	private KlineInterface.View mView;
	private String symbol;
	private Context mContext;

	/**
	 * /解析数据
	 */
	private DataParse mData;

	/**
	 * /K线图数据
	 */
	private List<KLineBean> kLineDatas;

	private ArrayList<Integer> mMacdColors, mVolColos;

	private NewSpotWebsocket.OrderbookDataListener mOrderListListener;

	String resolution, fromStr, toStr;
	int type;
	long time;

	public SpotKlinePresenter(Context context, KlineInterface.View mView, String symbol) {
		this.mView = mView;
		this.symbol = symbol;
		this.mContext = context;

		isRedRise = SwitchUtils.isRedRise();
		if (isRedRise) {
			mRiseColor = mContext.getResources().getColor(R.color.res_red);
			mDropColor = mContext.getResources().getColor(R.color.res_green);
		} else {
			mRiseColor = mContext.getResources().getColor(R.color.res_green);
			mDropColor = mContext.getResources().getColor(R.color.res_red);
		}
	}


	@Override
	public void onResume() {
		if (mOrderListListener == null) {
			mOrderListListener = new NewSpotWebsocket.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {

				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					mView.onQuoteDataReceived(marketData);
				}
			};
		}
		NewSpotWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
	}

	@Override
	public void onPause() {
		NewSpotWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
	}

	@Override
	public void onDestory() {
		OkGo.getInstance().cancelTag(this);
	}


	@Override
	public void updateSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Override
	public void initParams(int timeStatus) {
		switch (timeStatus) {
			case 0:
				resolution = "15min";
				time = TimeUtils.getNDayBefore(4);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				type = 1;
				break;
			case 1:
				type = 2;
				resolution = "1hour";
				time = TimeUtils.getNDayBefore(30);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 2:
				type = 2;
				resolution = "4hour";
				time = TimeUtils.getNDayBefore(30 * 6);//6个月
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 3:
				type = 3;
				resolution = "1day";
				//3年
				time = TimeUtils.getNDayBefore(30 * 12 * 3);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 4:
				type = 1;
				resolution = "1min";
				time = TimeUtils.getOneDayBefore();
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 5:
				type = 1;
				resolution = "5min";
				time = TimeUtils.getNDayBefore(2);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 6:
				type = 1;
				resolution = "30min";
				time = TimeUtils.getNDayBefore(7);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 7:
				type = 3;
				resolution = "1week";
				time = TimeUtils.getNDayBefore(30 * 12 * 5);//5年
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 8:
				type = 4;
				resolution = "1month";
				time = TimeUtils.getNDayBefore(30 * 12 * 10);//10年
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 9:
				type = 1;
				resolution = "1min";
				time = TimeUtils.getNHourBefore(12);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
		}
	}

	@Override
	public void loadKlineData() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("tradePair", symbol.contains("/") ? symbol.replace("/", "") : symbol);
		httpParams.put("period", resolution);
		httpParams.put("from", fromStr);
		httpParams.put("to", toStr);
		OkGo.<KlineData>get(Constants.MARKET_TRADEPAIR_KLINE).params(httpParams).tag(this).execute(new NewDialogCallback<KlineData>((Activity) mView) {

			@Override
			public void onSuc(Response<KlineData> response) {
				if (mView != null) {
					mView.onKlineDataLoadSuccess(mData, kLineDatas);
				}
			}

			@Override
			public void onE(Response<KlineData> response) {

			}

			@Override
			public KlineData dealJSONConvertedResult(KlineData klineData) {
				if (mData != null) {
					mData.reset();
					mData = null;
				}
				if (kLineDatas != null) {
					kLineDatas.clear();
					kLineDatas = null;
				}
				if (mVolColos != null) {
					mVolColos.clear();
					mVolColos = null;
				}
				if (mMacdColors != null) {
					mMacdColors.clear();
					mMacdColors = null;
				}
				mData = new DataParse();
				kLineDatas = new CopyOnWriteArrayList<>();

				mData.setPeriodType(resolution);
				kLineDatas = mData.parseKLine_new(klineData);
				mData.initLineDatas(kLineDatas);
				mData.initMACD(kLineDatas);

				initVolBarColor();

				initMacdBarColor();

				return super.dealJSONConvertedResult(klineData);
			}
		});
	}


	/**
	 * 设置交易量BarChart颜色
	 * <p>
	 * 这里为了不修改源码，根据集合数据判断涨跌幅颜色
	 */
	private void initVolBarColor() {
		// 第一次设置为跌的颜色
		mVolColos = new ArrayList<>();
		List<CandleEntry> candleEntries = mData.getCandleEntries();//getMacdData();
		int size = candleEntries.size();
		for (int i = 0; i < size; i++) {
			CandleEntry candleEntry = candleEntries.get(i);
			if (candleEntry.getOpen() <= candleEntry.getClose()) {
				mVolColos.add(mRiseColor);
			} else {
				mVolColos.add(mDropColor);
			}
		}
		mData.setVolColos(mVolColos);
	}

	/**
	 * 设置MACD BarChart颜色
	 */
	private void initMacdBarColor() {
		if (mData == null) {
			return;
		}
		mMacdColors = new ArrayList<>();
		List<BarEntry> barEntries = mData.getMacdData();
		for (BarEntry entry : barEntries) {
			if (entry.getY() <= 0) {
				mMacdColors.add(mDropColor);
			} else {
				mMacdColors.add(mRiseColor);
			}
		}
		mData.setMacdColors(mMacdColors);
	}

	@Override
	public List<KLineBean> getkLineDatas() {
		return kLineDatas;
	}
}
