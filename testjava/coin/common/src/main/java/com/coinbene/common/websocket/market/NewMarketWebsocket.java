package com.coinbene.common.websocket.market;

import android.text.TextUtils;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.enumtype.ContractType;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.common.websocket.base.BaseWebSocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.core.WsSubscriber;
import com.coinbene.common.websocket.model.WsExchangeRate;
import com.coinbene.common.websocket.model.WsMarketData;
import com.google.gson.JsonSyntaxException;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by june
 * on 2020-01-13
 */
public class NewMarketWebsocket extends BaseWebSocket implements WebsocketOperatiron {

	private static final String EXCHANGE_RATE_KEY = "exchange_rate_key";

	private static final String TOPIC_SPOT_TICKER = "spot/ticker.all";
	private static final String TOPIC_BTC_TICKER = "btc/ticker.all";
	private static final String TOPIC_USDT_TICKER = "usdt/ticker.all";
	private static final String TOPIC_EXCHANGE_RATE = "exchangeRate";

	private static volatile NewMarketWebsocket mInstance;

	//订阅者
	private WsSubscriber<WsMarketData> spotMarketSubscriber;
	private WsSubscriber<WsMarketData> btcMarketSubscriber;
	private WsSubscriber<WsMarketData> usdtMarketSubscriber;
	private WsSubscriber<WsExchangeRate> exchangeRateSubscriber;

	//现货行情数据的map
	private Map<String, WsMarketData> spotMarketMap;

	//合约行情数据的map
	private Map<String, WsMarketData> contractMarketMap;

	//货币费率map
	private Map<String, WsExchangeRate> exchangeRateMap;
	private WsExchangeRate currentExchangeRate;

	private List<MarketDataListener> mSpotMarketListenerList; 	//现货行情listener
	private List<MarketDataListener> mBtcMarketListenerList; 	//btc合约行情listener
	private List<MarketDataListener> mUsdtMarketListenerList;  	//usdt合约行情listener

	public static NewMarketWebsocket getInstance() {
		if (mInstance == null) {
			synchronized (NewMarketWebsocket.class) {
				if (mInstance == null) {
					mInstance = new NewMarketWebsocket();
				}
			}
		}
		return mInstance;
	}

	private NewMarketWebsocket(){
		spotMarketMap = new ConcurrentHashMap<>();
		contractMarketMap = new ConcurrentHashMap<>();
		exchangeRateMap = new ConcurrentHashMap<>();

		mSpotMarketListenerList =  Collections.synchronizedList(new ArrayList<>());
		mBtcMarketListenerList = Collections.synchronizedList(new ArrayList<>());
		mUsdtMarketListenerList = Collections.synchronizedList(new ArrayList<>());

		spotMarketSubscriber = new WsSubscriber<WsMarketData>(TOPIC_SPOT_TICKER) {
			@Override
			protected void onMessage(List<WsMarketData> data, String action) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setUpsAndDowns(TradeUtils.getUpsAndDowns(data.get(i)));
					data.get(i).setContractType(ContractType.NONE);
					spotMarketMap.put(data.get(i).getSymbol(), data.get(i));
				}
				dispatchMarketData(MarketType.SPOT);
			}
		};

		btcMarketSubscriber = new WsSubscriber<WsMarketData>(TOPIC_BTC_TICKER) {
			@Override
			protected void onMessage(List<WsMarketData> data, String action) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setUpsAndDowns(TradeUtils.getUpsAndDowns(data.get(i)));
					data.get(i).setContractType(ContractType.BTC);
					contractMarketMap.put(data.get(i).getSymbol(), data.get(i));
				}
				dispatchMarketData(MarketType.BTC_CONTRACT);
			}
		};

		usdtMarketSubscriber = new WsSubscriber<WsMarketData>(TOPIC_USDT_TICKER) {
			@Override
			protected void onMessage(List<WsMarketData> data, String action) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setUpsAndDowns(TradeUtils.getUpsAndDowns(data.get(i)));
					data.get(i).setContractType(ContractType.USDT);
					contractMarketMap.put(data.get(i).getSymbol(), data.get(i));
				}
				dispatchMarketData(MarketType.USDT_CONTRACT);
			}
		};

		exchangeRateSubscriber = new WsSubscriber<WsExchangeRate>(TOPIC_EXCHANGE_RATE) {
			@Override
			protected void onMessage(List<WsExchangeRate> data, String action) {
				for (int i = 0; i < data.size(); i++) {
					exchangeRateMap.put(data.get(i).getLangId(), data.get(i));
				}
				currentExchangeRate = exchangeRateMap.get(LanguageHelper.getCurrencyExchangeRateCode());
			}
		};
	}

	private void dispatchMarketData(MarketType marketType) {
		switch (marketType) {
			case BTC_CONTRACT:
				synchronized (mBtcMarketListenerList) {
					for (MarketDataListener listener : mBtcMarketListenerList) {
						listener.onDataArrived(contractMarketMap);
					}
				}
				break;
			case USDT_CONTRACT:
				synchronized (mUsdtMarketListenerList) {
					for (MarketDataListener listener : mUsdtMarketListenerList) {
						listener.onDataArrived(contractMarketMap);
					}
				}
				break;
			case SPOT:
				synchronized (mSpotMarketListenerList) {
					for (MarketDataListener listener : mSpotMarketListenerList) {
						listener.onDataArrived(spotMarketMap);
					}
				}
				break;
		}
	}

	@Override
	public void subScribeAll() {
		if (spotMarketSubscriber != null) {
			spotMarketSubscriber.subScribe();
		}
		if (btcMarketSubscriber != null) {
			btcMarketSubscriber.subScribe();
		}
		if (usdtMarketSubscriber != null) {
			usdtMarketSubscriber.subScribe();
		}
		if (exchangeRateSubscriber != null) {
			exchangeRateSubscriber.subScribe();
		}
	}

	@Override
	public void unsubScribeAll() {
		if (spotMarketSubscriber != null) {
			spotMarketSubscriber.unSubScribe();
		}
		if (btcMarketSubscriber != null) {
			btcMarketSubscriber.unSubScribe();
		}
		if (usdtMarketSubscriber != null) {
			usdtMarketSubscriber.unSubScribe();
		}
		if (exchangeRateSubscriber != null) {
			exchangeRateSubscriber.unSubScribe();
		}
	}

	@Override
	public void subAll() {
		checkSubscriberStatus(spotMarketSubscriber);
		checkSubscriberStatus(btcMarketSubscriber);
		checkSubscriberStatus(usdtMarketSubscriber);
		checkSubscriberStatus(exchangeRateSubscriber);

		if (spotMarketSubscriber != null) {
			spotMarketSubscriber.sendSubRequest();
		}
		if (btcMarketSubscriber != null) {
			btcMarketSubscriber.sendSubRequest();
		}
		if (usdtMarketSubscriber != null) {
			usdtMarketSubscriber.sendSubRequest();
		}
		if (exchangeRateSubscriber != null) {
			exchangeRateSubscriber.sendSubRequest();
		}
	}

	@Override
	public void unsubAll() {
		if (spotMarketSubscriber != null) {
			spotMarketSubscriber.sendUnsubRequest();
		}
		if (btcMarketSubscriber != null) {
			btcMarketSubscriber.sendUnsubRequest();
		}
		if (usdtMarketSubscriber != null) {
			usdtMarketSubscriber.sendUnsubRequest();
		}
		if (exchangeRateSubscriber != null) {
			exchangeRateSubscriber.sendUnsubRequest();
		}
	}

	public void registerMarketDataListener(MarketDataListener listener) {
		//默认现货
		registerMarketDataListener(listener, MarketType.SPOT);
	}

	public void unregisterMarketDataListener(MarketDataListener listener) {
		//默认现货
		unregisterMarketDataListener(listener, MarketType.SPOT);
	}

	public void registerMarketDataListener(MarketDataListener listener, MarketType contractType) {
		switch (contractType) {
			case BTC_CONTRACT:
				if (!mBtcMarketListenerList.contains(listener)) {
					//首次添加监听需要推送一次全量数据
					mThreadPool.execute(() -> listener.onDataArrived(contractMarketMap));
					mBtcMarketListenerList.add(listener);
				}
				break;
			case USDT_CONTRACT:
				if (!mUsdtMarketListenerList.contains(listener)) {
					//首次添加监听需要推送一次全量数据
					mThreadPool.execute(() -> listener.onDataArrived(contractMarketMap));
					mUsdtMarketListenerList.add(listener);
				}
				break;
			case SPOT:
				if (!mSpotMarketListenerList.contains(listener)) {
					//首次添加监听需要推送一次全量数据
					mThreadPool.execute(() -> listener.onDataArrived(spotMarketMap));
					mSpotMarketListenerList.add(listener);
				}
				break;
		}
	}

	public void unregisterMarketDataListener(MarketDataListener listener, MarketType contractType) {
		switch (contractType) {
			case BTC_CONTRACT:
				if (mBtcMarketListenerList.contains(listener)) {
					mBtcMarketListenerList.remove(listener);
				}
				break;
			case USDT_CONTRACT:
				if (mUsdtMarketListenerList.contains(listener)) {
					mUsdtMarketListenerList.remove(listener);
				}
				break;
			case SPOT:
				if (mSpotMarketListenerList.contains(listener)) {
					mSpotMarketListenerList.remove(listener);
				}
				break;
		}
	}

	/**
	 * 手动拉取全量的行情数据
	 */
	public void pullMarketData() {
		mThreadPool.execute(() -> {
			dispatchMarketData(MarketType.SPOT);
			dispatchMarketData(MarketType.BTC_CONTRACT);
			dispatchMarketData(MarketType.USDT_CONTRACT);
		});
	}

	public WsExchangeRate getCurrentExchangeRate() {
		if (currentExchangeRate == null) {
			String json = MMKV.defaultMMKV().decodeString(EXCHANGE_RATE_KEY, "");
			if (!TextUtils.isEmpty(json)) {
				try {
					currentExchangeRate = CBRepository.gson.fromJson(json, WsExchangeRate.class);
				} catch (JsonSyntaxException e) {
					e.printStackTrace();
					currentExchangeRate =  new WsExchangeRate();
					currentExchangeRate.setSymbol("");
				}
			} else {
				currentExchangeRate =  new WsExchangeRate();
				currentExchangeRate.setSymbol("");
			}
		}
		return currentExchangeRate;
	}

	public Map<String, WsExchangeRate> getExchangeRateMap() {
		return exchangeRateMap;
	}

	public Map<String, WsMarketData> getSpotMarketMap() {
		return spotMarketMap;
	}

	public Map<String, WsMarketData> getContractMarketMap() {
		return contractMarketMap;
	}

	public void saveExchangeRate() {
		if (currentExchangeRate != null) {
			MMKV.defaultMMKV().encode(EXCHANGE_RATE_KEY, CBRepository.gson.toJson(currentExchangeRate));
		}
	}

	public interface MarketDataListener {
		void onDataArrived(Map<String, WsMarketData> dataMap);
	}
}
