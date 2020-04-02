package com.coinbene.manbiwang.service.spot;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2019-11-15
 */
public interface SpotService extends IProvider {

	Fragment getSpotFragment();
}
