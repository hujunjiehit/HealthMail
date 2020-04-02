package com.coinbene.manbiwang.spot.serviceimpl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.spot.SpotService;
import com.coinbene.manbiwang.spot.SpotGoodsFragment;

/**
 * Created by june
 * on 2019-11-15
 */
@Route(path = RouteHub.Spot.spotService)
public class SpotServiceImpl implements SpotService {

	@Override
	public Fragment getSpotFragment() {
		return new SpotGoodsFragment();
	}

	@Override
	public void init(Context context) {

	}
}
