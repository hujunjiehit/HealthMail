package com.coinbene.common.websocket.listener;

import com.coinbene.manbiwang.model.websocket.WsContractQouteResponse;

import java.util.Map;

/**
 * Created by june
 * on 2019-08-04
 */
public interface ContractQuoteDataListener {
	void onDataArrived(Map<String, WsContractQouteResponse.DataBean>  map);
}
