package com.coinbene.manbiwang.kline.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.utils.TradeUtils;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.kline.KlineService;

/**
 * Created by june
 * on 2020-03-25
 */
@Route(path = RouteHub.Kline.klineService)
public class KlineServiceImpl implements KlineService {

	@Override
	public String getUpsAndDowns(String close, String open) {
		return TradeUtils.getUpsAndDowns(close, open);
	}

	@Override
	public void init(Context context) {

	}
}
