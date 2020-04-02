package com.coinbene.manbiwang.market.manager;

import com.coinbene.common.Constants;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 行情数据管理单例，负责将行情数据分发到下面的分组
 */
public class MarketDataManager {

	private static volatile MarketDataManager mInstance;

	private List<MarketDataListener> mMarketDataListenerList;

	private ExecutorService mThreadPool;

	private MarketDataManager() {
		mMarketDataListenerList = Collections.synchronizedList(new ArrayList<>());
		mThreadPool = Executors.newFixedThreadPool(4);
	}

	public static MarketDataManager getInstance() {
		if (mInstance == null) {
			synchronized (MarketDataManager.class) {
				if (mInstance == null) {
					mInstance = new MarketDataManager();
				}
			}
		}
		return mInstance;
	}

	public void registerMarketDataListener(MarketDataListener listener) {
		if (!mMarketDataListenerList.contains(listener)) {
			mThreadPool.execute(() -> {
				listener.onReceiveSpotMarketData(NewMarketWebsocket.getInstance().getSpotMarketMap());
				listener.onReceiveContractMarketData(NewMarketWebsocket.getInstance().getContractMarketMap(), Constants.CONTRACT_TYPE_BTC);
				listener.onReceiveContractMarketData(NewMarketWebsocket.getInstance().getContractMarketMap(), Constants.CONTRACT_TYPE_USDT);
			});
			mMarketDataListenerList.add(listener);
		}
	}

	public void unregisterMarketDataListener(MarketDataListener listener) {
		if (mMarketDataListenerList.contains(listener)) {
			mMarketDataListenerList.remove(listener);
		}
	}


	/**
	 * 收到现货行情数据
	 */
	public void dispatchSpotMarketData() {
		synchronized (mMarketDataListenerList) {
			for (MarketDataListener listener : mMarketDataListenerList) {
				listener.onReceiveSpotMarketData(NewMarketWebsocket.getInstance().getSpotMarketMap());
			}
		}
	}

	/**
	 * 收到Btc合约行情数据
	 */
	public void dispatchContractBtcMarketData() {
		synchronized (mMarketDataListenerList) {
			for (MarketDataListener listener : mMarketDataListenerList) {
				listener.onReceiveContractMarketData(NewMarketWebsocket.getInstance().getContractMarketMap(), Constants.CONTRACT_TYPE_BTC);
			}
		}
	}

	/**
	 * 收到Usdt合约行情数据
	 */
	public void dispatchContractUsdtMarketData() {
		synchronized (mMarketDataListenerList) {
			for (MarketDataListener listener : mMarketDataListenerList) {
				listener.onReceiveContractMarketData(NewMarketWebsocket.getInstance().getContractMarketMap(), Constants.CONTRACT_TYPE_USDT);
			}
		}
	}

	/**
	 * http现货行情数据
	 */
	public void dispatchHttpMarketData(String groupId, Map<String, TradePairMarketRes.DataBean> mapTradePair) {
		synchronized (mMarketDataListenerList) {
			for (MarketDataListener listener : mMarketDataListenerList) {
				listener.onReceiveHttpMarketData(groupId, mapTradePair);
			}
		}
	}

	/**
	 * 排序规则发生变化
	 */
	public void dispatchSortTypeClick(String sortField, String sortType) {
		synchronized (mMarketDataListenerList) {
			for (MarketDataListener listener : mMarketDataListenerList) {
				mThreadPool.execute(() -> listener.onSortTypeClick(sortField, sortType));
			}
		}
	}

	public interface MarketDataListener {
		void onReceiveSpotMarketData(Map<String, WsMarketData> spotTradePairMap);

		void onReceiveContractMarketData(Map<String, WsMarketData> spotTradePairMap, int contractType);

		void onSortTypeClick(String sortField, String sortType);

		void onReceiveHttpMarketData(String groupId, Map<String, TradePairMarketRes.DataBean> httpDataMap);
	}
}
