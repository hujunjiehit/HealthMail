package com.coinbene.manbiwang.spot.orderlayout.listener;

import com.coinbene.common.websocket.model.WsMarketData;

/**
 * Created by june
 * on 2019-12-04
 */
public interface OrderBookListener {

	void onPriceClick(String price);

	void onReceiveQuoteData(WsMarketData quote);
}
