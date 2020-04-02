package com.coinbene.manbiwang.contract.newcontract.orderlayout.listener;

import com.coinbene.common.widget.AppBarStateChangeListener;

/**
 * Created by june
 * on 2019-12-11
 */
public interface ContractDataListener {

	void registerDataListener();

	void unRegisterDataListener();

	void setSymbol(String symbol);

	void onScrollStatedChanged(AppBarStateChangeListener.ScrollState scrollState);
}
