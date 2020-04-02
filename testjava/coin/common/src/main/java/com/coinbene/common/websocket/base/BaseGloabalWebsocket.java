package com.coinbene.common.websocket.base;

import com.coinbene.common.websocket.core.CBWebSocketSubscriber;
import com.coinbene.common.websocket.core.WebSocketManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by june
 * on 2019-08-04
 */
public abstract class BaseGloabalWebsocket {

	protected int PERIOD_TIME = 200;

	public abstract void subScribeAll();

	public abstract void unSubScribeAll();

	public abstract void subAll();

	public abstract void unSubAll();

	protected ExecutorService mThreadPool = Executors.newFixedThreadPool(2);

	/**
	 * 检查当前Subscriber,如果连接断开，重新连接；如果dispose了，重新subScribe
	 * @param socketSubscriber
	 */
	public void checkSubscriberStatus(CBWebSocketSubscriber socketSubscriber) {
		if (WebSocketManager.getInstance().isSocketConnect(socketSubscriber.getUrl())) {
			WebSocketManager.getInstance().connectSocket(socketSubscriber.getUrl());
		}
		if (socketSubscriber.getDisposable() == null || socketSubscriber.getDisposable().isDisposed()) {
			socketSubscriber.subScribe();
		}
	}
}
