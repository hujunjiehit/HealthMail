package com.coinbene.manbiwang.spot.margin.lisenter;

import com.coinbene.manbiwang.model.http.MarginPlaceOrderParms;

public interface MarginCenterLayoutLisenter {

	void getUserMarginConfig();

	void createUserMargin();

	void placeOrder(MarginPlaceOrderParms placeOrderModel);
}
