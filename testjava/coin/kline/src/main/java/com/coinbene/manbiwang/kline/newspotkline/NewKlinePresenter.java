package com.coinbene.manbiwang.kline.newspotkline;

import android.util.Log;

import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.core.WsSubscriber;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsKline;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;
import com.github.fujianlian.klinechart.KLineEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by june
 * on 2020-01-21
 */
public class NewKlinePresenter implements NewKlineInterface.Presenter {

	private NewKlineInterface.View mView;
	private String mSymbol;

	private WsSubscriber<WsKline> klineSubscriber;
	private List<KLineEntity> mDatas;

	private KlineType mKlineType;
	private String timeType;

	private WebsocketOperatiron.OrderbookDataListener mOrderListListener;

	public NewKlinePresenter(NewKlineInterface.View view, String symbol, String timeType, KlineType klineType) {
		this.mView = view;
		this.mSymbol = symbol;
		this.mKlineType = klineType;
		this.timeType = timeType;

		if (klineSubscriber == null) {
			klineSubscriber = new WsSubscriber<WsKline>(getTopic(mSymbol, timeType, mKlineType)) {
				@Override
				protected void onMessage(List<WsKline> data, String action) {
					Log.e("websocketKline", "data.size ==> " + data.size() + "   action = " + action);
					DLog.e("klineTime", "onMessage");

					if ("insert".equals(action)) {
						mView.hideLoading();
					}

					if (mDatas == null) {
						mDatas = Collections.synchronizedList(new ArrayList<>());
					}

					if (mView != null) {
						DLog.e("klineTime", "startData");
						mDatas.clear();
						Iterator<WsKline> iterator = data.iterator();
						while (iterator.hasNext()) {
							WsKline next = iterator.next();
							KLineEntity entity = new KLineEntity();
							entity.Open = next.getO();
							entity.High = next.getH();
							entity.Low =  next.getL();
							entity.Close = next.getC();
							entity.Volume = next.getV();
							entity.Date = TimeUtils.getYMDHMFromMillisecond(next.getT() * 1000);
							//entity.upsAndDowns = TradeUtils.getUpsAndDowns(data.get(i));
							//entity.upsAndDowns = "";
							mDatas.add(entity);
						}
//
//						for (int i = 0; i < data.size(); i++) {
//							KLineEntity entity = new KLineEntity();
//							entity.Open = (float) data.get(i).getO();
//							entity.High = (float) data.get(i).getH();
//							entity.Low = (float) data.get(i).getL();
//							entity.Close = (float) data.get(i).getC();
//							entity.Volume = (float) data.get(i).getV();
//							entity.Date = TimeUtils.getYMDHMFromMillisecond(data.get(i).getT()*1000);
//							//entity.upsAndDowns = TradeUtils.getUpsAndDowns(data.get(i));
//							//entity.upsAndDowns = "";
//							mDatas.add(entity);
//						}
						mView.onKlineData(mDatas, "insert".equals(action));
						DLog.e("klineTime", "endData");
					}

				}
			};
		}
		klineSubscriber.subScribe();
	}

	public NewKlinePresenter(NewKlineInterface.View view, String symbol, String timeType) {
		this(view, symbol, timeType, KlineType.SPOT);
	}

	@Override
	public void updateTimeType(String timeType) {
		klineSubscriber.updateTopic(getTopic(mSymbol, timeType, mKlineType));
	}

	@Override
	public void onResume() {
		mView.showLoading();
		klineSubscriber.sendSubRequest();

		if (mOrderListListener == null) {
			mOrderListListener = new NewSpotWebsocket.OrderbookDataListener() {
				@Override
				public void onDataArrived(OrderbookModel orderbookModel, String symbol) {

				}

				@Override
				public void onTickerArrived(WsMarketData marketData, String symbol) {
					mView.onTickerData(marketData);
				}
			};
		}
		switch (mKlineType) {
			case SPOT:
				NewSpotWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
				break;
			case BTC_CONTRACT:
				NewContractBtcWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
				break;
			case USDT_CONTRACT:
				NewContractUsdtWebsocket.getInstance().registerOrderbookListener(mOrderListListener);
				break;
		}
	}


	@Override
	public void onPause() {
		klineSubscriber.sendUnsubRequest();
		switch (mKlineType) {
			case SPOT:
				NewSpotWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
				break;
			case BTC_CONTRACT:
				NewContractBtcWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
				break;
			case USDT_CONTRACT:
				NewContractUsdtWebsocket.getInstance().unregisterOrderbookListener(mOrderListListener);
				break;
		}
	}

	@Override
	public void changeTime(String timeType) {
		mView.showLoading();
		this.timeType = timeType;
		klineSubscriber.sendUnsubRequest();
		klineSubscriber.updateTopic(getTopic(mSymbol, timeType, mKlineType));
		klineSubscriber.sendSubRequest();
	}

	@Override
	public void changeSymbol(String symbol) {
		klineSubscriber.sendUnsubRequest();
		this.mSymbol = symbol;
		klineSubscriber.updateTopic(getTopic(mSymbol, timeType, mKlineType));
		klineSubscriber.sendSubRequest();
	}


	@Override
	public void onDestory() {
		klineSubscriber.unSubScribe();
	}


	public String getTopic(String symbol, String type, KlineType klineType) {
		switch (klineType) {
			case SPOT:
				return String.format("spot/kline.%s.%s", symbol, type);
			case BTC_CONTRACT:
				return String.format("btc/kline.%s.%s", symbol, type);
			case USDT_CONTRACT:
				return String.format("usdt/kline.%s.%s", symbol, type);
		}
		return "";

	}
}
