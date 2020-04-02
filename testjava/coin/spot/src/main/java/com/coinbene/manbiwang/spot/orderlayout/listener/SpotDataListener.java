package com.coinbene.manbiwang.spot.orderlayout.listener;

/**
 * Created by june
 * on 2019-12-11
 */
public interface SpotDataListener {

	void registerDataListener();

	void unRegisterDataListener();

	void setSymbol(String symbol);

}
