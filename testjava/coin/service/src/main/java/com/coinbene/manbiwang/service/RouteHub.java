package com.coinbene.manbiwang.service;

/**
 * 页面路由管理
 */
public interface RouteHub {


	/**
	 * App组件
	 */
	interface App {
		String appService = "/app/appService";
		String debugActivity = "/app/debugActivity";
	}

	interface Home {
		String homeService = "/home/homeService";
	}


	interface Kline {
		String spotKlineActivity = "/kline/spotKlineActivity";
		String klineService = "/kline/klineService";
	}

	interface Balance {
		String balanceService = "/balance/balanceService";

		String marginDetail = "/balance/marginDetail";
		String coinAddressActivity = "/balance/coinAddressActivity";
		String depositActivity = "/balance/depositActivity";
	}

	interface Spot {
		String spotService = "/spot/spotService";
	}

	interface Game {
		String gameService = "/game/gameService";
	}

	interface User {
		String userService = "/user/userService";

		String mySelfActivity = "/user/mySelfActivity";
		String settingSafeActivity = "/user/settingSafeActivity";
		String settingBalanceActivity = "/user/settingBalanceActivity";
		String loginActivity = "/user/loginActivity";
		String patternCheckActivity = "/user/patternCheckActivity";
		String fingerprintCheckActivity = "/user/fingerprintCheckActivity";
		String userPayTypesActivity = "/user/userPayTypesActivity";
	}

	interface Contract {
		String contractService = "/contract/contractService";
	}

	/**
	 * 记录组件
	 */
	interface Record {
		String recordService = "/record/recordService";
		String recordActivity = "/record/recordActivity";
		String spotHisOrderDetail = "/record/spotHisOrderDetail";
		String contractBtcOrderDetail = "/record/contractBtcOrderDetail";
		String contractUsdtOrderDetail = "/record/contractUsdtOrderDetail";
	}

	/**
	 * 行情组件
	 */
	interface Market {
		String marketService = "/market/marketService";
	}

	/**
	 * 财富组件
	 */
	interface Fortune {
		int TRANSFER_TYPE_IN = 1;    //转入余币宝
		int TRANSFER_TYPE_OUT = 2;    //转出余币宝

		String fortuneService = "/fortune/fortuneService";

		String fortuneActivity = "/fortune/fortuneActivity";
		String ybbTransferActivity = "/fortune/ybbTransferActivity";
		String ybbDetailActivity = "/fortune/ybbDetailActivity";
		String ybbRecordActivity = "/fortune/ybbRecordActivity";
	}
}
