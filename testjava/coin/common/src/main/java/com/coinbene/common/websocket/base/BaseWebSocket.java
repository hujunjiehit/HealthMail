package com.coinbene.common.websocket.base;

import com.coinbene.common.Constants;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.websocket.core.WsSubscriber;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by june
 * on 2020-03-22
 */
public abstract class BaseWebSocket {

	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(1);

	/**
	 * 检查当前Subscriber,如果连接断开，重新连接；如果dispose了，重新subScribe
	 * @param socketSubscriber
	 */
	public void checkSubscriberStatus(WsSubscriber socketSubscriber) {
		if (!WebSocketManager.getInstance().isSocketConnect(Constants.BASE_WEBSOCKET_URL)) {
			WebSocketManager.getInstance().connectSocket(Constants.BASE_WEBSOCKET_URL);
		}
		if (socketSubscriber.getDisposable() == null || socketSubscriber.getDisposable().isDisposed()) {
			socketSubscriber.subScribe();
		}
	}
}
