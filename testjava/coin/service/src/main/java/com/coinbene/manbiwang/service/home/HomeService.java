package com.coinbene.manbiwang.service.home;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * ding
 * 2019-12-31
 * com.coinbene.manbiwang.service.home
 */
public interface HomeService extends IProvider {
	Fragment getHomeFragment();
}
