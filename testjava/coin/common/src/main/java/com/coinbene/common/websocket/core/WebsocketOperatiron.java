package com.coinbene.common.websocket.core;

import com.coinbene.common.websocket.model.OrderbookModel;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.websocket.model.WsTradeList;

import java.util.List;

/**
 * Created by june
 * on 2020-01-14
 */
public interface WebsocketOperatiron {

	void subScribeAll();

	void unsubScribeAll();

	void subAll();

	void unsubAll();


	interface OrderbookDataListener {
		void onDataArrived(OrderbookModel orderbookModel, String symbol);

		void onTickerArrived(WsMarketData marketData, String symbol);
	}

	interface TradeListDataListener {
		void onDataArrived(List<WsTradeList> tradeLists, String symbol);
	}
}
