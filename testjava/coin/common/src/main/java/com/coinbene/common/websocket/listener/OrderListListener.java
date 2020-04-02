package com.coinbene.common.websocket.listener;

import com.coinbene.manbiwang.model.websocket.OrderListMapsModel;

/**
 * Created by june
 * on 2019-08-04
 */
public interface OrderListListener {
	void onDataArrived(OrderListMapsModel map, String tradePairId, boolean isFull);
}
