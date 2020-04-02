package com.coinbene.common.websocket.contract;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.base.BaseWebSocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.core.WsSubscriber;
import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsOrderbook;
import com.coinbene.common.websocket.model.WsTradeList;
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
 * on 2020-01-15
 */
public class NewContractUsdtWebsocket extends BaseWebSocket implements WebsocketOperatiron {

	private static final String WS_TRADEPAIR_KEY = "contractUsdtWsTradePair";

	private static volatile NewContractUsdtWebsocket mInstance;

	private static int defaultDepth = 20;

	private String symbol;

	private WsSubscriber<WsOrderbook> orderBookSubscriber;
	private WsSubscriber<WsMarketData> tickerSubscriber;
	private WsSubscriber<List<String>> tradeListSubscriber;

	//数据缓存
	private OrderbookModel orderbookModel;
	private WsMarketData tickerData;
	private List<WsTradeList> tradeDetailList;

	//listener
	private List<OrderbookDataListener> orderbookDataListenerList;
	private List<TradeListDataListener> tradeListDataListenerList;

	private NewContractUsdtWebsocket() {

		tickerData = new WsMarketData();

		symbol = MMKV.defaultMMKV().decodeString(WS_TRADEPAIR_KEY, "BTC-SWAP");
		if (TextUtils.isEmpty(symbol) || !ContractUsdtInfoController.getInstance().checkContractIsExist(symbol)) {
			symbol = "BTC-SWAP";
		}

		orderbookDataListenerList = Collections.synchronizedList(new ArrayList<>());
		tradeListDataListenerList = Collections.synchronizedList(new ArrayList<>());
		tradeDetailList = Collections.synchronizedList(new ArrayList<>());

		orderbookModel = new OrderbookModel();
		orderbookModel.setBuyMap(new ConcurrentHashMap<>());
		orderbookModel.setSellMap(new ConcurrentHashMap<>());

		orderBookSubscriber = new WsSubscriber<WsOrderbook>(getOrderbookTopic(symbol, defaultDepth)) {
			@Override
			protected void onMessage(List<WsOrderbook> data, String action) {
				handleOrderBookData(data, action);
			}
		};

		tradeListSubscriber = new WsSubscriber<List<String>>(getTradeDetailTopic(symbol)) {
			@Override
			protected void onMessage(List<List<String>> data, String action) {
				handleTradeListData(data, action);
			}
		};

		tickerSubscriber = new WsSubscriber<WsMarketData>(getTickerTopic(symbol)) {
			@Override
			protected void onMessage(List<WsMarketData> data, String action) {
				if (tickerData == null) {
					return;
				}
				tickerData = data.get(0);
				tickerData.setUpsAndDowns(TradeUtils.getUpsAndDowns(tickerData));
				if (tradeDetailList != null && tradeDetailList.size() >= 2) {
					//比较tradeDetailList最后两条的最新价
					synchronized (tradeDetailList) {
						int riseType = BigDecimalUtils.compare(tradeDetailList.get(tradeDetailList.size() - 1).getPrice(),
								tradeDetailList.get(tradeDetailList.size() - 2).getPrice());
						tickerData.setRiseType(riseType);
					}
				} else {
					tickerData.setRiseType(Constants.RISE_DEFAULT);
				}
				if(orderbookModel.getBuyMap()==null||orderbookModel.getBuyMap().size()==0){
					tickerData.setBestBidPrice("");
				}

				if(orderbookModel.getSellMap()==null||orderbookModel.getSellMap().size()==0){
					tickerData.setBestAskPrice("");
				}

				//分发数据给listener
				synchronized (orderbookDataListenerList) {
					for (OrderbookDataListener listener : orderbookDataListenerList) {
						if (!symbol.equals(tickerData.getSymbol())) {
							return;
						}
						listener.onTickerArrived(tickerData, symbol);
					}
				}
			}
		};
	}

	private String getOrderbookTopic(String symbol, int depth) {
		//	spot/orderBook.{symbol}.{depth}
		return String.format("usdt/orderBook.%s.%d", symbol, depth);
	}

	private String getTradeDetailTopic(String symbol) {
		//	spot/tradeList.{symbol}
		return String.format("usdt/tradeList.%s", symbol);
	}

	private String getTickerTopic(String symbol) {
		return String.format("usdt/ticker.%s", symbol);
	}

	private void handleOrderBookData(List<WsOrderbook> data, String action) {
		if (data == null || data.size() == 0) {
			return;
		}

		WsOrderbook orderbook = data.get(0);

		if ("insert".equals(action)) {
			//全量数据
			orderbookModel.getBuyMap().clear();
			orderbookModel.getSellMap().clear();
		}

		if (data.get(0).getBids() != null && orderbook.getBids().size() > 0) {
			//买盘
			for (int i = 0; i < orderbook.getBids().size(); i++) {
				orderbookModel.getBuyMap().put(orderbook.getBids().get(i).get(0), orderbook.getBids().get(i).get(1));
			}
		}

		if (data.get(0).getAsks() != null && orderbook.getAsks().size() > 0) {
			//卖盘
			for (int i = 0; i < orderbook.getAsks().size(); i++) {
				orderbookModel.getSellMap().put(orderbook.getAsks().get(i).get(0), orderbook.getAsks().get(i).get(1));
			}
		}

		removeZeroModel(orderbookModel.getBuyMap());
		removeZeroModel(orderbookModel.getSellMap());

		//分发数据给listener
		synchronized (orderbookDataListenerList) {
			for (OrderbookDataListener listener : orderbookDataListenerList) {
				listener.onDataArrived(orderbookModel, symbol);
			}
		}
	}


	private void handleTradeListData(List<List<String>> data, String action) {
		if (data == null || data.size() == 0) {
			return;
		}
		if ("insert".equals(action)) {
			//全量数据
			tradeDetailList.clear();
		}

		for (int i = data.size() - 1; i >= 0; i--) {
			WsTradeList bean = new WsTradeList();
			bean.setPrice(data.get(i).get(0));
			bean.setType(data.get(i).get(1).equals("s") ? "sell" : "buy");
			bean.setAmount(data.get(i).get(2));
			bean.setTime(TimeUtils.getDateTimeFromMillisecond(Tools.parseLong(data.get(i).get(3))));

			//只缓存14条tradeList
			synchronized (tradeDetailList) {
				if (tradeDetailList.size() >= Constants.MARKET_NUMBER_EIGHTTEEN) {
					tradeDetailList.remove(0);
				}
				tradeDetailList.add(bean);
			}
		}

		//分发数据给listener
		synchronized (tradeListDataListenerList) {
			for (TradeListDataListener listener : tradeListDataListenerList) {
				listener.onDataArrived(tradeDetailList, symbol);
			}
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

	public static NewContractUsdtWebsocket getInstance() {
		if (mInstance == null) {
			synchronized (NewContractUsdtWebsocket.class) {
				if (mInstance == null) {
					mInstance = new NewContractUsdtWebsocket();
				}
			}
		}
		return mInstance;
	}

	@Override
	public void subScribeAll() {
		if (orderBookSubscriber != null) {
			orderBookSubscriber.subScribe();
		}
		if (tradeListSubscriber != null) {
			tradeListSubscriber.subScribe();
		}
		if (tickerSubscriber != null) {
			tickerSubscriber.subScribe();
		}
	}

	@Override
	public void unsubScribeAll() {
		if (orderBookSubscriber != null) {
			orderBookSubscriber.unSubScribe();
		}
		if (tradeListSubscriber != null) {
			tradeListSubscriber.unSubScribe();
		}
		if (tickerSubscriber != null) {
			tickerSubscriber.unSubScribe();
		}
	}

	@Override
	public void subAll() {
		checkSubscriberStatus(orderBookSubscriber);
		checkSubscriberStatus(tradeListSubscriber);
		checkSubscriberStatus(tickerSubscriber);

		if (orderBookSubscriber != null) {
			orderBookSubscriber.sendSubRequest();
		}
		if (tradeListSubscriber != null) {
			tradeListSubscriber.sendSubRequest();
		}
		if (tickerSubscriber != null) {
			tickerSubscriber.sendSubRequest();
		}
	}

	@Override
	public void unsubAll() {
		if (orderBookSubscriber != null) {
			orderBookSubscriber.sendUnsubRequest();
		}
		if (tradeListSubscriber != null) {
			tradeListSubscriber.sendUnsubRequest();
		}
		if (tickerSubscriber != null) {
			tickerSubscriber.sendUnsubRequest();
		}
	}

	public void changeSymbol(String newSymbol) {
		if (TextUtils.isEmpty(newSymbol)) {
			return;
		}

		if (newSymbol.contains("/")) {
			newSymbol = newSymbol.replace("/", "");
		}

		if (newSymbol.equals(symbol)) {
			return;
		}

		if(tickerData != null){
			tickerData.setBestBidPrice(null);
			tickerData.setBestAskPrice(null);
		}
		orderbookModel.getBuyMap().clear();
		orderbookModel.getSellMap().clear();

		synchronized (tradeDetailList) {
			tradeDetailList.clear();
		}

		//取消订阅旧的交易对
		unsubAll();

		this.symbol = newSymbol;
		MMKV.defaultMMKV().encode(WS_TRADEPAIR_KEY, newSymbol);

		orderBookSubscriber.updateTopic(getOrderbookTopic(symbol, defaultDepth));
		tradeListSubscriber.updateTopic(getTradeDetailTopic(symbol));
		tickerSubscriber.updateTopic(getTickerTopic(symbol));

		//重新订阅新的交易对
		subAll();
	}

	public void registerOrderbookListener(OrderbookDataListener listener) {
		if (!orderbookDataListenerList.contains(listener)) {
			if(tickerData==null){
				return;
			}
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> {
				listener.onDataArrived(orderbookModel, symbol);
				if (!symbol.equals(tickerData.getSymbol())) {
					return;
				}
				listener.onTickerArrived(tickerData, symbol);
			});
			orderbookDataListenerList.add(listener);
		}
	}

	public void unregisterOrderbookListener(OrderbookDataListener listener) {
		if (orderbookDataListenerList.contains(listener)) {
			orderbookDataListenerList.remove(listener);
		}
	}

	public void registerTradeListListener(TradeListDataListener listener) {
		if (!tradeListDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> {
				listener.onDataArrived(tradeDetailList, symbol);
			});
			tradeListDataListenerList.add(listener);
		}
	}

	public void unregisterTradeListListener(TradeListDataListener listener) {
		if (tradeListDataListenerList.contains(listener)) {
			tradeListDataListenerList.remove(listener);
		}
	}

	/**
	 * 手动拉取全量的tradedetail数据
	 */
	public void pullTradeDetailData(String tradePair) {
		if (tradePair.contains("/")) {
			tradePair = tradePair.replace("/", "");
		}

		if (!symbol.equals(tradePair)) {
			return;
		}

		//分发数据给listener
		synchronized (tradeListDataListenerList) {
			for (TradeListDataListener listener : tradeListDataListenerList) {
				mThreadPool.execute(() -> listener.onDataArrived(tradeDetailList, symbol));
			}
		}
	}

	/**
	 * 手动拉取全量的orderList数据
	 */
	public void pullOrderListData(String tradePair) {
		if (tradePair.contains("/")) {
			tradePair = tradePair.replace("/", "");
		}

		if (!symbol.equals(tradePair) || tickerData == null) {
			return;
		}

		//分发数据给listener
		synchronized (orderbookDataListenerList) {
			for (OrderbookDataListener listener : orderbookDataListenerList) {
				mThreadPool.execute(() -> {
					listener.onDataArrived(orderbookModel, symbol);
					if (!symbol.equals(tickerData.getSymbol())) {
						return;
					}
					listener.onTickerArrived(tickerData, symbol);
				});
			}
		}
	}

	public void pullOrderListData() {
		pullOrderListData(symbol);
	}


	public void pullTradeDetailData() {
		pullTradeDetailData(symbol);
	}

	public WsMarketData getTickerData() {
		return tickerData;
	}
}
