package com.coinbene.common.websocket.contract;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.model.websocket.WsBaseRequest;
import com.coinbene.common.rxjava.FlowControl;
import com.coinbene.common.rxjava.FlowControlStrategy;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.base.BaseGloabalWebsocket;
import com.coinbene.common.websocket.core.CBWebSocketSubscriber;
import com.coinbene.common.websocket.listener.OrderListListener;
import com.coinbene.common.websocket.listener.TradeDetailListener;
import com.coinbene.manbiwang.model.http.BookOrderRes;
import com.coinbene.manbiwang.model.http.KlineDealMedel;
import com.coinbene.manbiwang.model.http.OrderLineItemModel;
import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;
import com.coinbene.manbiwang.model.websocket.WsContractOrderBookModel;
import com.coinbene.manbiwang.model.websocket.WsTradeDetailModel;
import com.tencent.mmkv.MMKV;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by june
 * on 2019-08-04
 */
public class ContractUsdtWebsocket extends BaseGloabalWebsocket {

	private static volatile ContractUsdtWebsocket mInstance;

	private static final String WS_TRADEPAIR_KEY = "contractUsdtWsTradePair";

	private CBWebSocketSubscriber<WsContractOrderBookModel> mOrderBookSubscriber;
	private CBWebSocketSubscriber<WsTradeDetailModel> mTradeDetailSubscriber;

	//数据缓存
	private OrderListMapsModel orderListMapsModel;
	private List<KlineDealMedel.DataBean> tradeDetailList;

	//监听者
	private List<OrderListListener> mOrderBookListenerList;
	private List<TradeDetailListener> mTradeDetailListenerList;

	private String contractName;
	private String orderBookTopic;
	private String tradeDetailTopic;

	private FlowControl mTradeDetailFlowControl;
	private FlowControl mOrderBookFlowControl;

	private ContractUsdtWebsocket() {
		contractName = SpUtil.getContractCionUsdt();
		if (TextUtils.isEmpty(contractName) || !ContractUsdtInfoController.getInstance().checkContractIsExist(contractName)) {
			contractName = "BTC-SWAP";
		}
		orderBookTopic = WsBaseRequest.CONTRACT_ORDERBOOK + contractName + WsBaseRequest.TOPIK_MARKET_ORDERLIST_NUMBER;
		tradeDetailTopic = WsBaseRequest.CONTRACT_TRADEDETAIL + contractName + WsBaseRequest.TOPIK_TRADE_DETAIL_NUMBER;

		//初始化OrderListMapsModel
		orderListMapsModel = new OrderListMapsModel();
		orderListMapsModel.setBuyMap(new ConcurrentHashMap<>());
		orderListMapsModel.setSellMap(new ConcurrentHashMap<>());
		orderListMapsModel.quote = new BookOrderRes.DataBean.QuoteBean();
		orderListMapsModel.setLocalPriceModel(new OrderLineItemModel());

		tradeDetailList = Collections.synchronizedList(new ArrayList<>());

		mTradeDetailListenerList = Collections.synchronizedList(new ArrayList<>());
		mOrderBookListenerList = Collections.synchronizedList(new ArrayList<>());

		mOrderBookSubscriber = new CBWebSocketSubscriber<WsContractOrderBookModel>(Constants.BASE_WEBSOCKET_CONTRACT_USDT, orderBookTopic) {
			@Override
			protected void onMessage(WsContractOrderBookModel responseEntity) {
				handleContractOrderBookResponse(responseEntity);
			}
		};

		mTradeDetailSubscriber = new CBWebSocketSubscriber<WsTradeDetailModel>(Constants.BASE_WEBSOCKET_CONTRACT_USDT, tradeDetailTopic) {
			@Override
			protected void onMessage(WsTradeDetailModel responseEntity) {
				handleTradeDetailResponse(responseEntity);
			}
		};
	}

	private void handleContractOrderBookResponse(WsContractOrderBookModel responseEntity) {
		if (responseEntity.getData() != null && orderBookTopic.equals(responseEntity.getTopic())) {
			if (responseEntity.isFull()) {
				orderListMapsModel.buyMap.clear();
				orderListMapsModel.sellMap.clear();
			}
			if (orderListMapsModel.getQuote() == null) {
				orderListMapsModel.setQuote(new BookOrderRes.DataBean.QuoteBean());
			}
			orderListMapsModel.getQuote().setN(responseEntity.getData().getQuote().getN());
			if (responseEntity.getData().getBids() != null
					&& responseEntity.getData().getBids().size() > 0) {
				for (int i = 0; i < responseEntity.getData().getBids().size(); i++) {
					orderListMapsModel.getBuyMap().put(responseEntity.getData().getBids().get(i)[0], responseEntity.getData().getBids().get(i)[1]);
				}
			}
			if (responseEntity.getData().getAsks() != null
					&& responseEntity.getData().getAsks().size() > 0) {
				for (int i = 0; i < responseEntity.getData().getAsks().size(); i++) {
					orderListMapsModel.getSellMap().put(responseEntity.getData().getAsks().get(i)[0], responseEntity.getData().getAsks().get(i)[1]);
				}
			}
			if (orderListMapsModel.getLocalPriceModel() == null) {
				orderListMapsModel.setLocalPriceModel(new OrderLineItemModel());
			}
			if (responseEntity.getData() != null && responseEntity.getData().getQuote() != null) {
				orderListMapsModel.getLocalPriceModel().newPrice = responseEntity.getData().getQuote().getN();
				orderListMapsModel.getLocalPriceModel().localPrice = responseEntity.getData().getQuote().getMp();
			}

			removeZeroModel(orderListMapsModel.getSellMap());
			removeZeroModel(orderListMapsModel.getBuyMap());

			if (mOrderBookFlowControl == null) {
				mOrderBookFlowControl = new FlowControl(FlowControlStrategy.throttleLast, PERIOD_TIME) {
					@Override
					public void doAction(String tag) {
						//分发数据给listener
						synchronized (mOrderBookListenerList) {
							for (OrderListListener listener : mOrderBookListenerList) {
								listener.onDataArrived(orderListMapsModel, contractName, responseEntity.isFull());
							}
						}
					}
				};
			}
			mOrderBookFlowControl.sendRequest("on orderbook data");
		}
	}

	private void removeZeroModel(Map map) {
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

	private Map<String, KlineDealMedel.DataBean> tradeDetailMap;

	private void handleTradeDetailResponse(WsTradeDetailModel responseEntity) {
		if (TextUtils.isEmpty(responseEntity.getTopic()) || responseEntity.getData() == null) {
			return;
		}

		if (!tradeDetailTopic.equals(responseEntity.getTopic())) {
			return;
		}

		if (tradeDetailMap == null) {
			tradeDetailMap = Collections.synchronizedMap(new TreeMap<>((o1, o2) -> o2.compareTo(o1)));
		}

		if (responseEntity.isFull()) {
			tradeDetailMap.clear();
			tradeDetailList.clear();
		}

		for (int i = 0; i < responseEntity.getData().size(); i++) {
			if (responseEntity.getData().get(i) != null && responseEntity.getData().get(i).size() > 4) {
				KlineDealMedel.DataBean dataBean = new KlineDealMedel.DataBean();
				dataBean.setSn(responseEntity.getData().get(i).get(0));
				dataBean.setP(responseEntity.getData().get(i).get(1));
				dataBean.setV(responseEntity.getData().get(i).get(2));
				dataBean.setT(TimeUtils.getDateTimeFromMillisecond(Long.valueOf(responseEntity.getData().get(i).get(3))));
				if ("s".equals(responseEntity.getData().get(i).get(4))) {
					dataBean.setD("sell");
				} else {
					dataBean.setD("buy");
				}
				tradeDetailMap.put(dataBean.getSn(), dataBean);
			}
		}

		//从Map中提取数据到tradeDetailList
		assemblyTradeList(tradeDetailMap);

		if (mTradeDetailFlowControl == null) {
			mTradeDetailFlowControl = new FlowControl(FlowControlStrategy.throttleLast, PERIOD_TIME) {
				@Override
				public void doAction(String tag) {
					//分发数据给listener
					synchronized (mTradeDetailListenerList) {
						for (TradeDetailListener listener : mTradeDetailListenerList) {
							listener.onDataArrived(deepCopyList(tradeDetailList), contractName, responseEntity.isFull());
						}
					}
				}
			};
		}
		mTradeDetailFlowControl.sendRequest("on tradeDetail data");

		//mContractView.setOrderDetail(assemblyTradeList(tradeDetailMap), contractName);
	}


	private List<KlineDealMedel.DataBean> deepCopyList(List<KlineDealMedel.DataBean> tradeDetailList) {
		List<KlineDealMedel.DataBean> copyList = Collections.synchronizedList(new ArrayList<>());
		synchronized (tradeDetailList) {
			for (KlineDealMedel.DataBean dataBean : tradeDetailList) {
				copyList.add((KlineDealMedel.DataBean) dataBean.clone());
			}
		}
		return copyList;
	}


	/**
	 * 组装orderDetail数据
	 *
	 * @param tradeDetailMap
	 * @return
	 */
	private void assemblyTradeList(Map<String, KlineDealMedel.DataBean> tradeDetailMap) {
		synchronized (tradeDetailList) {
			tradeDetailList.clear();
			if (tradeDetailMap != null && tradeDetailMap.size() > 0) {
				synchronized (tradeDetailMap) {
					Iterator<Map.Entry<String, KlineDealMedel.DataBean>> iterator = tradeDetailMap.entrySet().iterator();
					while (iterator.hasNext()) {
						KlineDealMedel.DataBean value = iterator.next().getValue();
						if (tradeDetailList.size() >= Constants.MARKET_NUMBER_FORETEEN) {
							iterator.remove();
						} else {
							tradeDetailList.add(value);
						}
					}
				}
			}

			if (tradeDetailList.size() < Constants.MARKET_NUMBER_FORETEEN) {
				int size = Constants.MARKET_NUMBER_FORETEEN - tradeDetailList.size();
				for (int i = 0; i < size; i++) {
					tradeDetailList.add(new KlineDealMedel.DataBean());
				}
			}
		}
//		if (CommonApplication.isOpen)
//			throw new StackOverflowError();
	}

	public static ContractUsdtWebsocket getInstance() {
		if (mInstance == null) {
			synchronized (ContractUsdtWebsocket.class) {
				if (mInstance == null) {
					mInstance = new ContractUsdtWebsocket();
				}
			}
		}
		return mInstance;
	}


	@Override
	public void subScribeAll() {
		if (mOrderBookSubscriber != null) {
			mOrderBookSubscriber.subScribe();
		}
		if (mTradeDetailSubscriber != null) {
			mTradeDetailSubscriber.subScribe();
		}
	}

	@Override
	public void unSubScribeAll() {
		if (mOrderBookSubscriber != null) {
			mOrderBookSubscriber.unSubScribe();
		}
		if (mTradeDetailSubscriber != null) {
			mTradeDetailSubscriber.unSubScribe();
		}
	}

	@Override
	public void subAll() {
		checkSubscriberStatus(mOrderBookSubscriber);
		checkSubscriberStatus(mTradeDetailSubscriber);

		mOrderBookSubscriber.sendSubRequest();
		mTradeDetailSubscriber.sendSubRequest();
	}

	@Override
	public void unSubAll() {
		mOrderBookSubscriber.sendUnsubRequest();
		mTradeDetailSubscriber.sendUnsubRequest();
	}

	public void changeTradePair(String newContractName) {
		if (TextUtils.isEmpty(newContractName)) {
			return;
		}

		if (newContractName.equals(contractName)) {
			return;
		}


		//清除上一个交易对的缓存数据
		orderListMapsModel.getBuyMap().clear();
		orderListMapsModel.getSellMap().clear();
		orderListMapsModel.setQuote(new BookOrderRes.DataBean.QuoteBean());
		orderListMapsModel.setLocalPriceModel(new OrderLineItemModel());

		synchronized (tradeDetailList) {
			tradeDetailList.clear();
		}

		//取消订阅旧的交易对
		unSubAll();

		this.contractName = newContractName;
		MMKV.defaultMMKV().encode(WS_TRADEPAIR_KEY, newContractName);

		orderBookTopic = WsBaseRequest.CONTRACT_ORDERBOOK + newContractName + WsBaseRequest.TOPIK_MARKET_ORDERLIST_NUMBER;
		tradeDetailTopic = WsBaseRequest.CONTRACT_TRADEDETAIL + newContractName + WsBaseRequest.TOPIK_TRADE_DETAIL_NUMBER;

		mOrderBookSubscriber.setTopic(orderBookTopic);
		mTradeDetailSubscriber.setTopic(tradeDetailTopic);

		//重新订阅新的交易对
		subAll();
	}


	public void registerOrderListListener(OrderListListener listener) {
		if (!mOrderBookListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(orderListMapsModel, contractName, true));
			mOrderBookListenerList.add(listener);
		}
	}

	public void unregisterOrderListListener(OrderListListener listener) {
		if (mOrderBookListenerList.contains(listener)) {
			mOrderBookListenerList.remove(listener);
		}
	}

	public void registerTradeDetailListener(TradeDetailListener listener) {
		if (!mTradeDetailListenerList.contains(listener)) {
			//首次添加监听需要推送一次全量数据
			mThreadPool.execute(() -> listener.onDataArrived(deepCopyList(tradeDetailList), contractName, true));
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
		if (!contractName.equals(symbol)) {
			return;
		}
		//分发数据给listener
		synchronized (mTradeDetailListenerList) {
			for (TradeDetailListener listener : mTradeDetailListenerList) {
				mThreadPool.execute(() -> listener.onDataArrived(deepCopyList(tradeDetailList), contractName, true));
			}
		}
	}

	/**
	 * 手动拉取全量的tradedetail数据
	 */
	public void pullTradeDetailData() {
		pullTradeDetailData(contractName);
	}

	/**
	 * 手动拉取全量的orderList数据
	 */
	public void pullOrderListData(String symbol) {
		if (!contractName.equals(symbol)) {
			return;
		}
		//分发数据给listener
		synchronized (mOrderBookListenerList) {
			for (OrderListListener listener : mOrderBookListenerList) {
				mThreadPool.execute(() -> listener.onDataArrived(orderListMapsModel, contractName, true));
			}
		}
	}

	public void pullOrderListData() {
		pullOrderListData(contractName);
	}
}
