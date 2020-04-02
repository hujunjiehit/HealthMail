package com.coinbene.manbiwang.service;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.manbiwang.service.app.AppService;
import com.coinbene.manbiwang.service.balance.BalanceService;
import com.coinbene.manbiwang.service.contract.ContractService;
import com.coinbene.manbiwang.service.fortune.FortuneService;
import com.coinbene.manbiwang.service.game.GameService;
import com.coinbene.manbiwang.service.home.HomeService;
import com.coinbene.manbiwang.service.market.MarketService;
import com.coinbene.manbiwang.service.record.RecordService;
import com.coinbene.manbiwang.service.spot.SpotService;
import com.coinbene.manbiwang.service.user.UserService;

/**
 * Created by june
 * on 2019-10-12
 */
public class ServiceRepo {
	public static AppService getAppService() {
		return ARouter.getInstance().navigation(AppService.class);
	}

	public static ContractService getContractService() {
		return ARouter.getInstance().navigation(ContractService.class);
	}

	public static RecordService getRecordService() {
		return ARouter.getInstance().navigation(RecordService.class);
	}

	public static MarketService getMarketService() {
		return ARouter.getInstance().navigation(MarketService.class);
	}

	public static FortuneService getFortuneService() {
		return ARouter.getInstance().navigation(FortuneService.class);
	}

	public static UserService getUserService() {
		return ARouter.getInstance().navigation(UserService.class);
	}

	public static BalanceService getBalanceService() {
		return ARouter.getInstance().navigation(BalanceService.class);
	}

	public static SpotService getSpotService() {
		return ARouter.getInstance().navigation(SpotService.class);
	}

	public static GameService getGameService() {
		return ARouter.getInstance().navigation(GameService.class);
	}

	public static HomeService getHomeService(){
		return ARouter.getInstance().navigation(HomeService.class);
	}
}
