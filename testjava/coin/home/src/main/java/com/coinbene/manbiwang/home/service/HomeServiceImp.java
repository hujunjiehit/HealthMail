package com.coinbene.manbiwang.home.service;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.manbiwang.home.HomeFragment;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.home.HomeService;

/**
 * ding
 * 2019-12-31
 * com.coinbene.manbiwang.home.service
 */

@Route(path = RouteHub.Home.homeService)
public class HomeServiceImp implements HomeService {
	@Override
	public void init(Context context) {

	}

	@Override
	public Fragment getHomeFragment() {
		return new HomeFragment();
	}
}
