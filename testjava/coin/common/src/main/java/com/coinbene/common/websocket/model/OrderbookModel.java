package com.coinbene.common.websocket.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by june
 * on 2020-01-15
 */
public class OrderbookModel {
	private ConcurrentHashMap<String, String> buyMap;
	private ConcurrentHashMap<String, String> sellMap;

	public ConcurrentHashMap<String, String> getBuyMap() {
		return buyMap;
	}

	public void setBuyMap(ConcurrentHashMap<String, String> buyMap) {
		this.buyMap = buyMap;
	}

	public ConcurrentHashMap<String, String> getSellMap() {
		return sellMap;
	}

	public void setSellMap(ConcurrentHashMap<String, String> sellMap) {
		this.sellMap = sellMap;
	}
}
