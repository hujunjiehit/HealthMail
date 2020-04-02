package com.coinbene.common.websocket.spot;

import android.text.TextUtils;

import com.coinbene.common.BuildConfig;
import com.coinbene.common.Constants;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.manbiwang.model.http.BookOrderRes;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.coinbene.common.model.websocket.WsBaseRequest;
import com.coinbene.manbiwang.model.websocket.WsMarketOrderListModel;
import com.coinbene.manbiwang.model.websocket.WsTradeDetailModel;
import com.coinbene.common.rxjava.FlowControl;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.base.BaseGloabalWebsocket;
import com.coinbene.common.websocket.core.CBWebSocketSubscriber;
import com.coinbene.common.websocket.listener.OrderListListener;
import com.coinbene.common.websocket.listener.TradeDetailListener;
import com.tencent.mmkv.MMKV;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by june
 * on 2019-08-04
 */
public class SpotWebsocket extends BaseGloabalWebsocket {

	private static volatile SpotWebsocket mInstance;

	private static final String WS_TRADEPAIR_KEY = "spotWsTradePair";

	private String tradePairId;

	private String orderListTopic;
	private String tradeDetailTopic;

	//订阅者
	private CBWebSocketSubscriber<WsTradeDetailModel> mTradeDetailSubscriber;
	private CBWebSocketSubscriber<WsMarketOrderListModel> mOrderListSubscriber;

	//数据缓存
	private OrderListMapsModel orderListMapsModel;
	private List<KlineDealMedel.DataBean> tradeDetailList;

	//监听者
	private List<TradeDetailListener> mTradeDetailListenerList;
	private List<OrderListListener> mOrderListListenerList;

	public static String lastTs = System.currentTimeMillis() + "";

	private FlowControl mTradeDetailFlowControl;
	private FlowControl mOrderBookFlowControl;

	private SpotWebsocket() {

		tradePairId = MMKV.defaultMMKV().decodeString(WS_TRADEPAIR_KEY, "BTCUSDT");
		if (TextUtils.isEmpty(tradePairId) || TradePairInfoController.getInstance().queryDataById(tradePairId) == null) {
			tradePairId = "BTCUSDT";
		}

		orderListTopic = WsBaseRequest.TOPIK_MARKET_ORDERLIST + tradePairId + WsBaseRequest.TOPIK_MARKET_ORDERLIST_NUMBER;
		tradeDetailTopic = WsBaseRequest.TOPIK_TRADE_DETAIL + tradePairId + WsBaseRequest.TOPIK_TRADE_DETAIL_NUMBER;

		//初始化OrderListMapsModel
		orderListMapsModel = new OrderListMapsModel();
		orderListMapsModel.setBuyMap(new ConcurrentHashMap<>());
		orderListMapsModel.setSellMap(new ConcurrentHashMap<>());
		orderListMapsModel.setQuote(new BookOrderRes.DataBean.QuoteBean());
		orderListMapsModel.setLocalPriceModel(new OrderLineItemModel());

		tradeDetailList = Collections.synchronizedList(new ArrayList<>());

		mTradeDetailListenerList = Collections.synchronizedList(new ArrayList<>());
		mOrderListListenerList = Collections.synchronizedList(new ArrayList<>());

		mOrderListSubscriber = new CBWebSocketSubscriber<WsMarketOrderListModel>(Constants.BASE_WEBSOCKET, orderListTopic) {
			@Override
			protected void onMessage(WsMarketOrderListModel responseEntity) {
				if (BuildConfig.DEBUG) {
					if (BigDecimalUtils.compareZero(BigDecimalUtils.subtract(responseEntity.getTs(), lastTs)) < 0) {
						DLog.e("websocketts", "ts = " + BigDecimalUtils.subtract(responseEntity.getTs(), lastTs));
					}
					lastTs = responseEntity.getTs();
				}
				handleOrderListResponse(responseEntity);
			}
		};

		mTradeDetailSubscriber = new CBWebSocketSubscriber<WsTradeDetailModel>(Constants.BASE_WEBSOCKET, tradeDetailTopic) {
			@Override
			protected void onMessage(WsTradeDetailModel responseEntity) {
				handleTradeDetailData(responseEntity);
			}
		};
	}

	private void handleTradeDetailData(WsTradeDetailModel responseEntity) {
		if (!TextUtils.isEmpty(responseEntity.getTopic()) && responseEntity.getData() != null
				&& tradeDetailTopic.equals(responseEntity.getTopic())) {
			if (responseEntity.getData().size() > 0) {
				if (responseEntity.isFull()) {
					tradeDetailList.clear();
				}
				//倒叙保存
				for (int i = responseEntity.getData().size() - 1; i >= 0; i--) {

					if (responseEntity.getData().get(i) != null && responseEntity.getData().get(i).size() > 3) {
						KlineDealMedel.DataBean dataBean = new KlineDealMedel.DataBean();
//                                                for (int j = 0; j < responseEntity.getData().get(i).size(); j++) {
						dataBean.setP(responseEntity.getData().get(i).get(0));
						dataBean.setV(responseEntity.getData().get(i).get(1));
						dataBean.setT(TimeUtils.getDateTimeFromMillisecond(Long.valueOf(responseEntity.getData().get(i).get(2)) * 1000));
						if ("s".equals(responseEntity.getData().get(i).get(3))) {
							dataBean.setD("sell");
						} else {
							dataBean.setD("buy");
						}
						tradeDetailList.add(dataBean);
					}
				}
			}

			if (mTradeDetailFlowControl == null) {
				mTradeDetailFlowControl = new FlowControl(FlowControlStrategy.throttleLast, PERIOD_TIME) {
					@Override
					public void doAction(String tag) {
						//分发数据给listener
						synchronized (mTradeDetailListenerList) {
							for (TradeDetailListener listener : mTradeDetailListenerList) {
								DLog.d("SpotTradeDetail", "tradeDetailList.size ===> " + tradeDetailList.size());
								listener.onDataArrived(tradeDetailList, tradePairId, responseEntity.isFull());
							}
						}
					}
				};
			}
			mTradeDetailFlowControl.sendRequest("on spot tradeDetail data");
		}
	}

	private void handleOrderListResponse(WsMarketOrderListModel responseEntity) {
		if (!TextUtils.isEmpty(responseEntity.getTopic())
				&& responseEntity.getData() != null && responseEntity.getTopic().equals(orderListTopic)) {

			//全量数据
			if (responseEntity.data.isFull()) {
				orderListMapsModel.getBuyMap().clear();
				orderListMapsModel.getSellMap().clear();
			}

			if (responseEntity.getData().quote != null) {
				orderListMapsModel.quote = responseEntity.getData().quote;
			}
			if (responseEntity.getData().getOrderDepth() != null && responseEntity.getData().getOrderDepth().bids != null
					&& responseEntity.getData().getOrderDepth().bids.size() > 0) {
				for (int i = 0; i < responseEntity.getData().getOrderDepth().bids.size(); i++) {
					orderListMapsModel.getBuyMap().put(responseEntity.getData().getOrderDepth().bids.get(i)[0], responseEntity.getData().getOrderDepth().bids.get(i)[1]);
				}

			}
			if (responseEntity.getData().getOrderDepth() != null && responseEntity.getData().getOrderDepth().asks != null
					&& responseEntity.getData().getOrderDepth().asks.size() > 0) {
				for (int i = 0; i < responseEntity.getData().getOrderDepth().asks.size(); i++) {
					orderListMapsModel.getSellMap().put(responseEntity.getData().getOrderDepth().asks.get(i)[0], responseEntity.getData().getOrderDepth().asks.get(i)[1]);
				}
			}
			removeZeroModel(orderListMapsModel.getSellMap());
			removeZeroModel(orderListMapsModel.getBuyMap());

			if (mOrderBookFlowControl == null) {
				mOrderBookFlowControl = new FlowControl(FlowControlStrategy.throttleLast, PERIOD_TIME) {
					@Override
					public void doAction(String tag) {
						//分发数据给listener
						synchronized (mOrderListListenerList) {
							for (OrderListListener listener : mOrderListListenerList) {
								DLog.d("SpotOrderBook", "buyMap -> " + orderListMapsModel.buyMap.size() + " ===> sellMap -> " + orderListMapsModel.sellMap.size());
								listener.onDataArrived(orderListMapsModel, tradePairId, responseEntity.data.isFull());
							}
						}
					}
				};
			}
			mOrderBookFlowControl.sendRequest("on spot orderbook data");
		}
	}


	private synchronized void removeZeroModel(Map map) {
		if (map == null || map.size() == 0) {
			return;
		}
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			BigDecimal bigDecimal = new BigDecimal(entry.getValue());
			if (bigDecimal.compareTo(BigDecimal.ZERO) == 0) {
				it.remove();
			}
		}

	}

	public static SpotWebsocket getInstance() {
		if (mInstance == null) {
			synchronized (SpotWebsocket.class) {
				if (mInstance == null) {
					mInstance = new SpotWebsocket();
				}
			}
		}
		return mInstance;
	}

	@Override
	public void subScribeAll() {
		if (mOrderListSubscriber != null) {
			mOrderListSubscriber.subScribe();
		}
		if (mTradeDetailSubscriber != null) {
			mTradeDetailSubscriber.subScribe();
		}
	}

	@Override
	public void unSubScribeAll() {
		if (mOrderListSubscriber != null) {
			mOrderListSubscriber.unSubScribe();
		}
		if (mTradeDetailSubscriber != null) {
			mTradeDetailSubscriber.unSubScribe();
		}
	}

	@Override
	public void subAll() {
		checkSubscriberStatus(mOrderListSubscriber);
		checkSubscriberStatus(mTradeDetailSubscriber);

		mOrderListSubscriber.sendSubRequest();
		mTradeDetailSubscriber.sendSubRequest();
	}

	@Override
	public void unSubAll() {
		mOrderListSubscriber.sendUnsubRequest();
		mTradeDetailSubscriber.sendUnsubRequest();
	}

	public void changeTradePair(String newTradePair) {
		if (TextUtils.isEmpty(newTradePair)) {
			return;
		}

		if (newTradePair.contains("/")) {
			newTradePair = newTradePair.replace("/", "");
		}

		if (newTradePair.equals(tradePairId)) {
			return;
		}


		DLog.d("websocket", "changeTradePair   " + tradePairId + " ==>" + newTradePair);

		//清除上一个交易对的缓存数据
		orderListMapsModel.getBuyMap().clear();
		orderListMapsModel.getSellMap().clear();
		orderListMapsModel.setLocalPriceModel(new OrderLineItemModel());
		orderListMapsModel.setQuote(new BookOrderRes.DataBean.QuoteBean());

		synchronized (tradeDetailList) {
			tradeDetailList.clear();
		}

		//取消订阅旧的交易对
		unSubAll();

		this.tradePairId = newTradePair;
		MMKV.defaultMMKV().encode(WS_TRADEPAIR_KEY, newTradePair);

		this.orderListTopic = WsBaseRequest.TOPIK_MARKET_ORDERLIST + newTradePair + WsBaseRequest.TOPIK_MARKET_ORDERLIST_NUMBER;
		this.tradeDetailTopic = WsBaseRequest.TOPIK_TRADE_DETAIL + newTradePair + WsBaseRequest.TOPIK_TRADE_DETAIL_NUMBER;

		mOrderListSubscriber.setTopic(orderListTopic);
		mTradeDetailSubscriber.setTopic(tradeDetailTopic);

		//重新订阅新的交易对
		subAll();
	}

	public void registerOrderListListener(OrderListListener listener) {
		if (!mOrderListListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(orderListMapsModel, tradePairId, true));
			mOrderListListenerList.add(listener);
		}
	}

	public void unregisterOrderListListener(OrderListListener listener) {
		if (mOrderListListenerList.contains(listener)) {
			mOrderListListenerList.remove(listener);
		}
	}

	public void registerTradeDetailListener(TradeDetailListener listener) {
		if (!mTradeDetailListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(tradeDetailList, tradePairId, true));
			mTradeDetailListenerList.add(listener);
		}
	}

	public void unregisterTradeDetailListener(TradeDetailListener listener) {
		if (mTradeDetailListenerList.contains(listener)) {
			mTradeDetailListenerList.remove(listener);
		}
	}

	/**
	 * 手动拉取全量的tradedetail数据
	 */
	public void pullTradeDetailData(String symbol) {
		if (symbol.contains("/")) {
			symbol = symbol.replace("/", "");
		}

		if (!tradePairId.equals(symbol)) {
			return;
		}
		//分发数据给listener
		synchronized (mTradeDetailListenerList) {
			for (TradeDetailListener listener : mTradeDetailListenerList) {
				mThreadPool.execute(() -> listener.onDataArrived(tradeDetailList, tradePairId, true));
			}
		}
	}

	/**
	 * 手动拉取全量的orderList数据
	 */
	public void pullOrderListData(String symbol) {
		if (symbol.contains("/")) {
			symbol = symbol.replace("/", "");
		}

		if (!tradePairId.equals(symbol)) {
			return;
		}
		//分发数据给listener
		synchronized (mOrderListListenerList) {
			for (OrderListListener listener : mOrderListListenerList) {
				mThreadPool.execute(() -> listener.onDataArrived(orderListMapsModel, tradePairId, true));
			}
		}
	}

	public void pullOrderListData() {
		pullOrderListData(tradePairId);
	}


	public void pullTradeDetailData() {
		pullTradeDetailData(tradePairId);
	}
}
