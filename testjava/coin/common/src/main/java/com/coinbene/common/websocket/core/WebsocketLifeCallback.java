package com.coinbene.common.websocket.core;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.coinbene.common.Constants;
import com.coinbene.common.websocket.contract.NewContractBtcWebsocket;
import com.coinbene.common.websocket.contract.NewContractUsdtWebsocket;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.spot.NewSpotWebsocket;

/**
 * Created by june
 * on 2019-07-30
 */
public class WebsocketLifeCallback implements Application.ActivityLifecycleCallbacks {


	private int created = 0;
	private int destroyed = 0;

	private int activeCount = 0;

	@Override
	public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
		created++;
		if (created == 1) {
			//应用启动
			connectSocket();
		}
	}

	@Override
	public void onActivityStarted(Activity activity) {
		activeCount++;
		if (activeCount == 1) {
			//应用从后台切换到前台
			doSubAll();
		}
	}

	@Override
	public void onActivityResumed(Activity activity) {

	}


	@Override
	public void onActivityPaused(Activity activity) {

	}

	@Override
	public void onActivityStopped(Activity activity) {
		activeCount--;
		if (activeCount <= 0) {
			//应用切换到后台
			unSubAll();

			NewMarketWebsocket.getInstance().saveExchangeRate();
		}
	}

	@Override
	public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

	}

	@Override
	public void onActivityDestroyed(Activity activity) {
		destroyed++;
		if (destroyed >= created) {
			//应用退出
			disConnectSocket();
		}
	}

	public void connectSocket() {
		WebSocketManager.getInstance().connectSocket(Constants.BASE_WEBSOCKET_URL);

//		WebSocketManager.getInstance().connectSocket(Constants.BASE_WEBSOCKET_CONTRACT_BTC);
//		WebSocketManager.getInstance().connectSocket(Constants.BASE_WEBSOCKET_CONTRACT_USDT);
	}

	public void disConnectSocket() {
		WebSocketManager.getInstance().disconnectSocket(Constants.BASE_WEBSOCKET_URL);

//		WebSocketManager.getInstance().disconnectSocket(Constants.BASE_WEBSOCKET_CONTRACT_BTC);
//		WebSocketManager.getInstance().disconnectSocket(Constants.BASE_WEBSOCKET_CONTRACT_USDT);
	}


	private void doSubAll() {
		subAll();
	}

	private void subAll() {

		NewMarketWebsocket.getInstance().subScribeAll();
		NewSpotWebsocket.getInstance().subScribeAll();
		NewContractUsdtWebsocket.getInstance().subScribeAll();
		NewContractBtcWebsocket.getInstance().subScribeAll();

		NewMarketWebsocket.getInstance().subAll();
		NewSpotWebsocket.getInstance().subAll();
		NewContractUsdtWebsocket.getInstance().subAll();
		NewContractBtcWebsocket.getInstance().subAll();

		//旧websocket
//		MarketWebsosket.getInstance().subScribeAll();
//		ContractBtcWebsocket.getInstance().subScribeAll();
//		ContractUsdtWebsocket.getInstance().subScribeAll();
//
//		MarketWebsosket.getInstance().subAll();
//		ContractBtcWebsocket.getInstance().subAll();
//		ContractUsdtWebsocket.getInstance().subAll();
	}


	private void unSubAll() {
		NewMarketWebsocket.getInstance().unsubAll();
		NewContractUsdtWebsocket.getInstance().unsubAll();
		NewContractBtcWebsocket.getInstance().unsubAll();
		NewSpotWebsocket.getInstance().unsubAll();

		NewMarketWebsocket.getInstance().unsubScribeAll();
		NewSpotWebsocket.getInstance().unsubScribeAll();
		NewContractBtcWebsocket.getInstance().unsubScribeAll();
		NewContractUsdtWebsocket.getInstance().unsubScribeAll();

		//旧websocket
//		MarketWebsosket.getInstance().unSubAll();
//		ContractBtcWebsocket.getInstance().unSubAll();
//		ContractUsdtWebsocket.getInstance().unSubAll();
//
//		MarketWebsosket.getInstance().unSubScribeAll();
//		ContractBtcWebsocket.getInstance().unSubScribeAll();
//		ContractUsdtWebsocket.getInstance().unSubScribeAll();
	}
}
