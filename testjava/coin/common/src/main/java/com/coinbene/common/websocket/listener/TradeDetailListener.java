package com.coinbene.common.websocket.listener;

import com.coinbene.manbiwang.model.http.KlineDealMedel;

import java.util.List;

/**
 * Created by june
 * on 2019-08-04
 */
public interface TradeDetailListener {
	void onDataArrived(List<KlineDealMedel.DataBean> tradeDetailList, String tradePairId, boolean isFull);
}
