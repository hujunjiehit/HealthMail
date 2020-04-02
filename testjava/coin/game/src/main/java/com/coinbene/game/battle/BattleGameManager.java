package com.coinbene.game.battle;

import android.content.Context;
import android.os.Bundle;

import com.coinbene.common.Constants;
import com.coinbene.common.router.UIBusService;

/**
 * ding
 * 2019-09-15
 * com.coinbene.game.battle
 */
public class BattleGameManager {

	/**
	 * 游戏授权页URL
	 */
	public static final String GAME_URL = Constants.BASE_URL_H5 + "/loading.html?redirect_url=%2Fauth%2Fauthorize%3Fcode%3DSICKLE&min_version=2.4.0&auth=false&replace=true";

	/**
	 * 开始游戏
	 */
	public static void play(Context context) {
		Bundle bundle = new Bundle();
		bundle.putBoolean("isLandScape", true);
		UIBusService.getInstance().openUri(context, GAME_URL, bundle);
	}
}
