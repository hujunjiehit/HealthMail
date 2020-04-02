package com.coinbene.common.websocket.listener;

import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.Map;

/**
 * Created by june
 * on 2019-07-26
 */
public interface MarketDataListener {
	void onDataArrived(Map<String, TradePairMarketRes.DataBean> map);
}

