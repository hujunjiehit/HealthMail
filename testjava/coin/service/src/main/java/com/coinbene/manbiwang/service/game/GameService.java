package com.coinbene.manbiwang.service.game;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Created by june
 * on 2019-11-15
 */
public interface GameService extends IProvider {
	Fragment getGameFragment();
}
