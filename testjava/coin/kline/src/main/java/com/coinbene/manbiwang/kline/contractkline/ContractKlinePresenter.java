package com.coinbene.manbiwang.kline.contractkline;

import android.app.Activity;
import android.content.Context;

import com.coinbene.common.Constants;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.kline.R;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;
import com.coinbene.manbiwang.kline.bean.KlineData;
import com.coinbene.manbiwang.kline.spotkline.KlineInterface;
import com.github.mikephil.coinbene.data.BarEntry;
import com.github.mikephil.coinbene.data.CandleEntry;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2019-11-26
 */
public class ContractKlinePresenter implements KlineInterface.Presenter {

	private KlineInterface.View mView;
	private String symbol;
	private Context mContext;
	private int contractType;

	private String resolution, fromStr, toStr;
	long time;
	int type = 1;

	/**
	 * 涨跌颜色
	 */
	private boolean isRedRise;
	private int mRiseColor, mDropColor;

	/**
	 * /解析数据
	 */
	private DataParse mData;

	/**
	 * /K线图数据
	 */
	private List<KLineBean> kLineDatas;

	private ArrayList<Integer> mMacdColors, mVolColos;

	private WebsocketOperatiron.OrderbookDataListener mOrderBookDataListener;

	//缓存的quote，用于刷新合约k线箭头
	private int riseType = Constants.RISE_DEFAULT;


	public ContractKlinePresenter(Context context, KlineInterface.View view, String symbol) {
		this.mView = view;
		this.symbol = symbol;
		this.mContext = context;
		this.contractType = symbol.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;

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
	public void updateSymbol(String symbol) {
		this.contractType = symbol.contains("-") ? Constants.CONTRACT_TYPE_USDT : Constants.CONTRACT_TYPE_BTC;
		this.symbol = symbol;
	}

	@Override
	public void onResume() {
		if (mOrderBookDataListener == null) {
			mOrderBookDataListener = new WebsocketOperatiron.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {

				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					if (mView != null) {
						mView.onQuoteDataReceived(marketData);

						mView.onRiseTypeChanged(marketData.getRiseType());
					}
				}
			};
		}

		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderBookDataListener);
		} else {
			NewContractBtcWebsocket.getInstance().registerOrderbookListener(mOrderBookDataListener);
		}
	}

	@Override
	public void onPause() {
		if (contractType == Constants.CONTRACT_TYPE_USDT) {
			NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderBookDataListener);
		} else {
			NewContractBtcWebsocket.getInstance().unregisterOrderbookListener(mOrderBookDataListener);
		}
	}


	@Override
	public void initParams(int timeStatus) {
		switch (timeStatus) {
			case 0:
				resolution = "15";
				time = TimeUtils.getNDayBefore(4);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				type = 1;
				break;
			case 1:
				type = 2;
				resolution = "60";
				time = TimeUtils.getNDayBefore(30);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 2:
				type = 2;
				resolution = "240";
				time = TimeUtils.getNDayBefore(30 * 6);//6个月
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 3:
				type = 3;
				resolution = "D";
				//3年
				time = TimeUtils.getNDayBefore(30 * 12 * 3);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 4:
				type = 1;
				resolution = "1";
				time = TimeUtils.getOneDayBefore();
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 5:
				type = 1;
				resolution = "5";
				time = TimeUtils.getNDayBefore(2);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 6:
				type = 1;
				resolution = "30";
				time = TimeUtils.getNDayBefore(7);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 7:
				type = 3;
				resolution = "W";
				time = TimeUtils.getNDayBefore(30 * 12 * 5);//5年
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 8:
				type = 4;
				resolution = "M";
				time = TimeUtils.getNDayBefore(30 * 12 * 10);//10年
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
			case 9:
				type = 1;
				resolution = "1";
				time = TimeUtils.getNHourBefore(12);
				fromStr = String.valueOf(time);
				toStr = String.valueOf(System.currentTimeMillis() / 1000);
				break;
		}
	}

	@Override
	public void loadKlineData() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("symbol", symbol);
		httpParams.put("resolution", resolution);
		httpParams.put("from", fromStr);
		httpParams.put("to", toStr);
		String url = contractType == Constants.CONTRACT_TYPE_BTC ? Constants.MARKET_FUTURE_KLINE : Constants.MARKET_FUTURE_KLINE_USDT;
		OkGo.<KlineData>get(url).params(httpParams).tag(this).execute(new NewDialogCallback<KlineData>((Activity) mView) {

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
				if (mData == null) {
					mData = new DataParse();
				}

				kLineDatas = new ArrayList<>();

				mData.setPeriodType(resolution);
				kLineDatas = mData.parseKLineContract(klineData,contractType,symbol);
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

	@Override
	public void onDestory() {

	}
}
