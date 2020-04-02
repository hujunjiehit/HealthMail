package com.coinbene.common.websocket.market;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.model.websocket.WsBaseRequest;
import com.coinbene.common.rxjava.FlowControl;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.websocket.base.BaseGloabalWebsocket;
import com.coinbene.common.websocket.core.CBWebSocketSubscriber;
import com.coinbene.common.websocket.listener.ContractQuoteDataListener;
import com.coinbene.common.websocket.listener.MarketDataListener;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;
import com.coinbene.manbiwang.model.websocket.WsContractQouteResponse;
import com.coinbene.manbiwang.model.websocket.WsExchangeRate;
import com.coinbene.manbiwang.model.websocket.WsMarketDataModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by june
 * on 2019-07-26
 */
public class MarketWebsosket extends BaseGloabalWebsocket {

	private static volatile MarketWebsosket mInstance;

	private CBWebSocketSubscriber<WsMarketDataModel> mMarketDataSubscriber;
	private CBWebSocketSubscriber<WsContractQouteResponse> mContractBtcQuoteSubscriber;
	private CBWebSocketSubscriber<WsContractQouteResponse> mContractUsdtQuoteSubscriber;
	private CBWebSocketSubscriber<WsExchangeRate> mExchangeRateSubscriber;

	private Map<String, TradePairMarketRes.DataBean> mMarketTradePairMap;

	//BTC合约用到的map
	private Map<String, TradePairMarketRes.DataBean> mContractBtcTradePairMap;
	private Map<String, WsContractQouteResponse.DataBean> mContractBtcQuoteMap;

	//USDT合约用到的map
	private Map<String, TradePairMarketRes.DataBean> mContractUsdtTradePairMap;
	private Map<String, WsContractQouteResponse.DataBean> mContractUsdtQuoteMap;

	private Map<String, String[]> mExchangeRateMap;

	//现货行情listener
	private List<MarketDataListener> mSpotMarketDataListenerList;

	//BTC合约行情listener
	private List<MarketDataListener> mContractBtcMarketDataListenerList;
	private List<ContractQuoteDataListener> mContractBtcQuoteDataListenerList;

	//USDT合约行情listener
	private List<MarketDataListener> mContractUsdtMarketDataListenerList;
	private List<ContractQuoteDataListener> mContractUsdtQuoteDataListenerList;

	FlowControl mMarketFlowControl;

	private MarketWebsosket() {

		mMarketTradePairMap = new ConcurrentHashMap<>();
		mContractBtcTradePairMap = new ConcurrentHashMap<>();
		mContractBtcQuoteMap = new ConcurrentHashMap<>();
		mContractUsdtTradePairMap = new ConcurrentHashMap<>();
		mContractUsdtQuoteMap = new ConcurrentHashMap<>();
		mExchangeRateMap = new ConcurrentHashMap<>();


		mSpotMarketDataListenerList = Collections.synchronizedList(new ArrayList<>());
		mContractBtcMarketDataListenerList = Collections.synchronizedList(new ArrayList<>());
		mContractBtcQuoteDataListenerList = Collections.synchronizedList(new ArrayList<>());
		mContractUsdtMarketDataListenerList = Collections.synchronizedList(new ArrayList<>());
		mContractUsdtQuoteDataListenerList = Collections.synchronizedList(new ArrayList<>());

		//现货行情
		mMarketDataSubscriber = new CBWebSocketSubscriber<WsMarketDataModel>(Constants.BASE_WEBSOCKET,
				WsBaseRequest.TOPIK_MARKET) {
			@Override
			protected void onMessage(WsMarketDataModel responseEntity) {
				if (TextUtils.isEmpty(responseEntity.getTopic()) || responseEntity.getData() == null || responseEntity.getData().size() == 0) {
					return;
				}

				//缓存数据,暂时没有清缓存
				for (int i = 0; i < responseEntity.getData().size(); i++) {
					mMarketTradePairMap.put(responseEntity.getData().get(i).getS(), responseEntity.getData().get(i));
				}

				if (mMarketFlowControl == null) {
					mMarketFlowControl = new FlowControl(FlowControlStrategy.throttleLast, PERIOD_TIME) {
						@Override
						public void doAction(String tag) {
							//遍历listener列表分发数据
							synchronized (mSpotMarketDataListenerList) {
								for (MarketDataListener listener : mSpotMarketDataListenerList) {
									listener.onDataArrived(mMarketTradePairMap);
								}
							}
						}
					};
				}
				mMarketFlowControl.sendRequest("on market data");
			}
		};

		//BTC合约行情
		mContractBtcQuoteSubscriber = new CBWebSocketSubscriber<WsContractQouteResponse>(Constants.BASE_WEBSOCKET_CONTRACT_BTC,
				WsBaseRequest.CONTRACT_QOUTE) {
			@Override
			protected void onMessage(WsContractQouteResponse responseEntity) {
				if (TextUtils.isEmpty(responseEntity.getTopic()) || responseEntity.getData() == null || responseEntity.getData().size() == 0) {
					return;
				}


				if (responseEntity.isFull()) {
					mContractBtcTradePairMap.clear();
					mContractBtcQuoteMap.clear();
				}

				//缓存数据
				for (int i = 0; i < responseEntity.getData().size(); i++) {
					if (responseEntity.getData().get(i) == null) {
						continue;
					}
					//数据给行情页面用
					TradePairMarketRes.DataBean dataBean = new TradePairMarketRes.DataBean();
					dataBean.setName(responseEntity.getData().get(i).getS());
					dataBean.setS(responseEntity.getData().get(i).getS());
					dataBean.setN(responseEntity.getData().get(i).getN());
					dataBean.setV24(responseEntity.getData().get(i).getV24());
					dataBean.setAmt24(responseEntity.getData().get(i).getV24());
					dataBean.setP(BigDecimalUtils.toPercentage(responseEntity.getData().get(i).getP()));
					dataBean.setA(responseEntity.getData().get(i).getA());
					mContractBtcTradePairMap.put(responseEntity.getData().get(i).getS(), dataBean);

					//数据给合约交易页面用
					WsContractQouteResponse.DataBean quoteDataBean = new WsContractQouteResponse.DataBean();
					quoteDataBean.setN(responseEntity.getData().get(i).getN());
					quoteDataBean.setP(BigDecimalUtils.toPercentage(responseEntity.getData().get(i).getP()));
					quoteDataBean.setF8(responseEntity.getData().get(i).getF8());
					quoteDataBean.setA(responseEntity.getData().get(i).getA());
					quoteDataBean.setMp(responseEntity.getData().get(i).getMp());

					//合约K线用到的数据
					quoteDataBean.setV24(responseEntity.getData().get(i).getV24());
					quoteDataBean.setH(responseEntity.getData().get(i).getH());
					quoteDataBean.setL(responseEntity.getData().get(i).getL());
					quoteDataBean.setS(responseEntity.getData().get(i).getS());

					mContractBtcQuoteMap.put(responseEntity.getData().get(i).getS(), quoteDataBean);
				}

				//遍历listener列表分发数据
				synchronized (mContractBtcMarketDataListenerList) {
					for (MarketDataListener listener : mContractBtcMarketDataListenerList) {
						listener.onDataArrived(mContractBtcTradePairMap);
					}
				}

				//遍历listener列表分发数据
				synchronized (mContractBtcQuoteDataListenerList) {
					for (ContractQuoteDataListener listener : mContractBtcQuoteDataListenerList) {
						listener.onDataArrived(mContractBtcQuoteMap);
					}
				}
			}
		};

		//USDT合约行情
		mContractUsdtQuoteSubscriber = new CBWebSocketSubscriber<WsContractQouteResponse>(Constants.BASE_WEBSOCKET_CONTRACT_USDT,
				WsBaseRequest.CONTRACT_QOUTE) {
			@Override
			protected void onMessage(WsContractQouteResponse responseEntity) {
				if (TextUtils.isEmpty(responseEntity.getTopic()) || responseEntity.getData() == null || responseEntity.getData().size() == 0) {
					return;
				}


				if (responseEntity.isFull()) {
					mContractUsdtTradePairMap.clear();
					mContractUsdtQuoteMap.clear();
				}

				//缓存数据
				for (int i = 0; i < responseEntity.getData().size(); i++) {
					if (responseEntity.getData().get(i) == null) {
						continue;
					}
					//数据给行情页面用
					TradePairMarketRes.DataBean dataBean = new TradePairMarketRes.DataBean();
					dataBean.setName(responseEntity.getData().get(i).getS());
					dataBean.setS(responseEntity.getData().get(i).getS());
					dataBean.setN(responseEntity.getData().get(i).getN());
					dataBean.setV24(responseEntity.getData().get(i).getV24());
					dataBean.setAmt24(responseEntity.getData().get(i).getV24());
					dataBean.setP(BigDecimalUtils.toPercentage(responseEntity.getData().get(i).getP()));
					dataBean.setA(responseEntity.getData().get(i).getA());
					mContractUsdtTradePairMap.put(responseEntity.getData().get(i).getS(), dataBean);

					//数据给合约交易页面用
					WsContractQouteResponse.DataBean quoteDataBean = new WsContractQouteResponse.DataBean();
					quoteDataBean.setN(responseEntity.getData().get(i).getN());
					quoteDataBean.setP(BigDecimalUtils.toPercentage(responseEntity.getData().get(i).getP()));
					quoteDataBean.setF8(responseEntity.getData().get(i).getF8());
					quoteDataBean.setA(responseEntity.getData().get(i).getA());
					quoteDataBean.setMp(responseEntity.getData().get(i).getMp());

					//合约K线用到的数据
					quoteDataBean.setV24(responseEntity.getData().get(i).getV24());
					quoteDataBean.setH(responseEntity.getData().get(i).getH());
					quoteDataBean.setL(responseEntity.getData().get(i).getL());
					quoteDataBean.setS(responseEntity.getData().get(i).getS());

					mContractUsdtQuoteMap.put(responseEntity.getData().get(i).getS(), quoteDataBean);
				}

				//遍历listener列表分发数据
				synchronized (mContractUsdtMarketDataListenerList) {
					for (MarketDataListener listener : mContractUsdtMarketDataListenerList) {
						listener.onDataArrived(mContractUsdtTradePairMap);
					}
				}

				//遍历listener列表分发数据
				synchronized (mContractUsdtQuoteDataListenerList) {
					for (ContractQuoteDataListener listener : mContractUsdtQuoteDataListenerList) {
						listener.onDataArrived(mContractUsdtQuoteMap);
					}
				}
			}
		};

		//合约当前币种与法币的汇率
		mExchangeRateSubscriber = new CBWebSocketSubscriber<WsExchangeRate>(Constants.BASE_WEBSOCKET_CONTRACT_BTC,
				WsBaseRequest.CONTRACT_EXCHANGE_RATE) {
			@Override
			protected void onMessage(WsExchangeRate result) {
				if (result == null || result.getData() == null || result.getData().getRates() == null) {
					return;
				}

				for (Map.Entry<String, String[]> entry : result.getData().getRates().entrySet()) {
					mExchangeRateMap.put(entry.getKey(), entry.getValue());
				}
			}
		};
	}

	public static MarketWebsosket getInstance() {
		if (mInstance == null) {
			synchronized (MarketWebsosket.class) {
				if (mInstance == null) {
					mInstance = new MarketWebsosket();
				}
			}
		}
		return mInstance;
	}


	@Override
	public void subScribeAll() {
		DLog.d("websocket", "MarketWebsosket subScribeAll");
//		if (mMarketDataSubscriber != null) {
//			mMarketDataSubscriber.subScribe();
//		}
		if (mContractBtcQuoteSubscriber != null) {
			mContractBtcQuoteSubscriber.subScribe();
		}
		if (mContractUsdtQuoteSubscriber != null) {
			mContractUsdtQuoteSubscriber.subScribe();
		}
//		if (mExchangeRateSubscriber != null) {
//			mExchangeRateSubscriber.subScribe();
//		}
	}

	@Override
	public void unSubScribeAll() {
//		if (mMarketDataSubscriber != null) {
//			mMarketDataSubscriber.unSubScribe();
//		}
		if (mContractBtcQuoteSubscriber != null) {
			mContractBtcQuoteSubscriber.unSubScribe();
		}
		if (mContractUsdtQuoteSubscriber != null) {
			mContractUsdtQuoteSubscriber.unSubScribe();
		}
//		if (mExchangeRateSubscriber != null) {
//			mExchangeRateSubscriber.unSubScribe();
//		}
	}

	/**
	 * 发送sub请求
	 */
	@Override
	public void subAll() {
		//checkSubscriberStatus(mMarketDataSubscriber);
		checkSubscriberStatus(mContractBtcQuoteSubscriber);
		checkSubscriberStatus(mContractUsdtQuoteSubscriber);
		//checkSubscriberStatus(mExchangeRateSubscriber);

		//mMarketDataSubscriber.sendSubRequest();
		mContractBtcQuoteSubscriber.sendSubRequest();
		mContractUsdtQuoteSubscriber.sendSubRequest();
		//mExchangeRateSubscriber.sendSubRequest();
	}

	/**
	 * 发送unsub请求
	 */
	@Override
	public void unSubAll() {
		//mMarketDataSubscriber.sendUnsubRequest();
		mContractBtcQuoteSubscriber.sendUnsubRequest();
		mContractUsdtQuoteSubscriber.sendUnsubRequest();
		//mExchangeRateSubscriber.sendUnsubRequest();
	}

	public void registerMarketDataListener(MarketDataListener listener) {
		if (!mSpotMarketDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(mMarketTradePairMap));
			mSpotMarketDataListenerList.add(listener);
		}
	}

	public void unregisterMarketDataListener(MarketDataListener listener) {
		if (mSpotMarketDataListenerList.contains(listener)) {
			mSpotMarketDataListenerList.remove(listener);
		}
	}

	public void registerContractBtcDataListener(MarketDataListener listener) {
		if (!mContractBtcMarketDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(mContractBtcTradePairMap));
			mContractBtcMarketDataListenerList.add(listener);
		}
	}

	public void unregisterContractBtcDataListener(MarketDataListener listener) {
		if (mContractBtcMarketDataListenerList.contains(listener)) {
			mContractBtcMarketDataListenerList.remove(listener);
		}
	}

	public void registerContractBtcQuoteDataListener(ContractQuoteDataListener listener) {
		if (!mContractBtcQuoteDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(mContractBtcQuoteMap));
			mContractBtcQuoteDataListenerList.add(listener);
		}
	}

	public void unregisterContractBtcQuoteDataListener(ContractQuoteDataListener listener) {
		if (mContractBtcQuoteDataListenerList.contains(listener)) {
			mContractBtcQuoteDataListenerList.remove(listener);
		}
	}

	public void registerContractUsdtDataListener(MarketDataListener listener) {
		if (!mContractUsdtMarketDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(mContractUsdtTradePairMap));
			mContractUsdtMarketDataListenerList.add(listener);
		}
	}

	public void unregisterContractUsdtDataListener(MarketDataListener listener) {
		if (mContractUsdtMarketDataListenerList.contains(listener)) {
			mContractUsdtMarketDataListenerList.remove(listener);
		}
	}

	public void registerContractUsdtQuoteDataListener(ContractQuoteDataListener listener) {
		if (!mContractUsdtQuoteDataListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(mContractUsdtQuoteMap));
			mContractUsdtQuoteDataListenerList.add(listener);
		}
	}

	public void unregisterContractUsdtQuoteDataListener(ContractQuoteDataListener listener) {
		if (mContractUsdtQuoteDataListenerList.contains(listener)) {
			mContractUsdtQuoteDataListenerList.remove(listener);
		}
	}

	/**
	 * 手动拉取全量的行情数据
	 */
	public void pullMarketData() {
		mThreadPool.execute(() -> {
//			synchronized (mSpotMarketDataListenerList) {
//				for (MarketDataListener listener : mSpotMarketDataListenerList) {
//					listener.onDataArrived(mMarketTradePairMap);
//				}
//			}

			synchronized (mContractBtcMarketDataListenerList) {
				for (MarketDataListener listener : mContractBtcMarketDataListenerList) {
					listener.onDataArrived(mContractBtcTradePairMap);
				}
			}

			synchronized (mContractUsdtMarketDataListenerList) {
				for (MarketDataListener listener : mContractUsdtMarketDataListenerList) {
					listener.onDataArrived(mContractUsdtTradePairMap);
				}
			}
			synchronized (mContractBtcQuoteDataListenerList) {
				for (ContractQuoteDataListener listener : mContractBtcQuoteDataListenerList) {
					listener.onDataArrived(mContractBtcQuoteMap);
				}
			}


			synchronized (mContractUsdtQuoteDataListenerList) {
				for (ContractQuoteDataListener listener : mContractUsdtQuoteDataListenerList) {
					listener.onDataArrived(mContractUsdtQuoteMap);
				}
			}
		});
	}

	public String getCurrentExchangeRate() {
		String[] rates = mExchangeRateMap.get(LanguageHelper.getCurrencyExchangeRateCode());
		if (rates == null || rates.length <= 0) {
			return "";
		}
		return rates[0];
	}

	public String getCurrentSymbol() {
		String[] rates = mExchangeRateMap.get(LanguageHelper.getCurrencyExchangeRateCode());
		if (rates == null || rates.length <= 1) {
			return "";
		}
		return rates[1];
	}

	public Map<String, TradePairMarketRes.DataBean> getMarketTradePairMap() {
		return mMarketTradePairMap;
	}

	public Map<String, TradePairMarketRes.DataBean> getContractBtcTradePairMap() {
		return mContractBtcTradePairMap;
	}

	public Map<String, TradePairMarketRes.DataBean> getContractUsdtTradePairMap() {
		return mContractUsdtTradePairMap;
	}

	public Map<String, WsContractQouteResponse.DataBean> getContractBtcQuoteMap() {
		return mContractBtcQuoteMap;
	}

	public Map<String, WsContractQouteResponse.DataBean> getContractUsdtQuoteMap() {
		return mContractUsdtQuoteMap;
	}
}
