package com.coinbene.game.serviceimpl;

import android.content.Context;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.game.GameFragment;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.game.GameService;

/**
 * Created by june
 * on 2019-11-15
 */
@Route(path = RouteHub.Game.gameService)
public class GameServiceImpl implements GameService {

	@Override
	public Fragment getGameFragment() {
		return new GameFragment();
	}

	@Override
	public void init(Context context) {

	}
}
