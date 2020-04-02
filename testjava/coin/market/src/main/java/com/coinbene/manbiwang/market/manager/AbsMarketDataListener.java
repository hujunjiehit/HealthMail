package com.coinbene.manbiwang.market.manager;

import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;

import java.util.Map;

/**
 * Created by june
 * on 2019-10-11
 */
public abstract class AbsMarketDataListener implements MarketDataManager.MarketDataListener {
	@Override
	public void onReceiveSpotMarketData(Map<String, WsMarketData> spotTradePairMap) {

	}

	@Override
	public void onReceiveContractMarketData(Map<String, WsMarketData> contractTradePairMap, int contractType) {

	}

	@Override
	public void onSortTypeClick(String sortField, String sortType) {

	}

	@Override
	public void onReceiveHttpMarketData(String groupId, Map<String, TradePairMarketRes.DataBean> httpDataMap) {

	}
}
