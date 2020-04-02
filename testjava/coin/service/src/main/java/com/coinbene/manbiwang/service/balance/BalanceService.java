package com.coinbene.manbiwang.service.balance;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2019-11-15
 */
public interface BalanceService extends IProvider {
	Fragment getBalanceFragment();
}
