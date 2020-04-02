package com.coinbene.manbiwang.balance.serviceimpl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.manbiwang.balance.BalanceTotalFragment;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.balance.BalanceService;

/**
 * Created by june
 * on 2019-11-15
 */
@Route(path = RouteHub.Balance.balanceService)
public class BalanceServiceImpl implements BalanceService {


	@Override
	public Fragment getBalanceFragment() {
		return new BalanceTotalFragment();
	}

	@Override
	public void init(Context context) {

	}
}
