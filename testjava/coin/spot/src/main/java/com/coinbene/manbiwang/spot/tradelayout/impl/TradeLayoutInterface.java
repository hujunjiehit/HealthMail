package com.coinbene.manbiwang.spot.tradelayout.impl;

import android.app.Activity;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.manbiwang.model.http.SpotPlaceOrderModel;

/**
 * Created by june
 * on 2019-12-02
 */
public interface TradeLayoutInterface {
	interface View {
		void onPlaceOrderResult(boolean success,int orderType);
	}

	interface Presenter extends BasePresenter {
		/**
		 * 限价下单
		 */
		void placeOrder(SpotPlaceOrderModel params);

		void setActivity(Activity mActivity);

		void getBanlance();

		void setSymbol(String symbol);

		int getPricePrecision();

		int getVolumePrecision();

		String getTakeFee();

		String getMinVolume();

		String getPriceChangeScale();
	}
}
