package com.coinbene.common.websocket.userevent;

import com.coinbene.common.utils.DLog;
import com.coinbene.common.websocket.base.BaseWebSocket;
import com.coinbene.common.websocket.core.WebsocketOperatiron;
import com.coinbene.common.websocket.core.WsSubscriber;
import com.coinbene.common.websocket.model.UserEventType;
import com.coinbene.common.websocket.model.WebSocketType;
import com.coinbene.common.websocket.model.WsUserEvent;
import com.coinbene.manbiwang.service.ServiceRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by june
 * on 2020-01-16
 */
public class UsereventWebsocket extends BaseWebSocket implements WebsocketOperatiron {

	public static final String TOPIC_SPOT_USER_EVENT = "spot/userEvent";
	public static final String TOPIC_MARGIN_USER_EVENT = "margin/userEvent";
	public static final String TOPIC_BTC_USER_EVENT = "btc/userEvent";
	public static final String TOPIC_USDT_USER_EVENT = "usdt/userEvent";

	private static volatile UsereventWebsocket mInstance;

	private WsSubscriber<WsUserEvent> spotUserEventSubscriber;
	private WsSubscriber<WsUserEvent> marginUserEventSubscriber;
	private WsSubscriber<WsUserEvent> btcUserEventSubscriber;
	private WsSubscriber<WsUserEvent> usdtUserEventSubscriber;

	private List<UserEventListener> spotUserEventListenerList;
	private List<UserEventListener> marginUserEventListenerList;
	private List<UserEventListener> btcUserEventListenerList;
	private List<UserEventListener> usdtUserEventListenerList;

	private UsereventWebsocket() {

		spotUserEventListenerList =  Collections.synchronizedList(new ArrayList<>());
		marginUserEventListenerList =  Collections.synchronizedList(new ArrayList<>());
		btcUserEventListenerList =  Collections.synchronizedList(new ArrayList<>());
		usdtUserEventListenerList =  Collections.synchronizedList(new ArrayList<>());

		spotUserEventSubscriber = new WsSubscriber<WsUserEvent>(TOPIC_SPOT_USER_EVENT) {
			@Override
			protected void onMessage(List<WsUserEvent> data, String action) {
				handleUserEvent(data, WebSocketType.SPOT);
			}

			@Override
			protected void onConnectSuccess() {
				super.onConnectSuccess();

				if (ServiceRepo.getUserService().isLogin()) {
					//连接成功之后，手动将每个事件分发一次；
					//避免websocket断开之后，重新连上的时候界面没有刷新
					DLog.d("websocket", "spot subscriber 连接成功之后，手动下发 userevent");
					handleUserEvent(getAllUserEvent(false), WebSocketType.SPOT);
				}
			}
		};

		marginUserEventSubscriber = new WsSubscriber<WsUserEvent>(TOPIC_MARGIN_USER_EVENT) {
			@Override
			protected void onMessage(List<WsUserEvent> data, String action) {
				handleUserEvent(data, WebSocketType.MARGIN);
			}

			@Override
			protected void onConnectSuccess() {
				super.onConnectSuccess();

				if (ServiceRepo.getUserService().isLogin()) {
					//连接成功之后，手动将每个事件分发一次；
					//避免websocket断开之后，重新连上的时候界面没有刷新
					DLog.d("websocket", "margin subscriber 连接成功之后，手动下发 userevent");
					handleUserEvent(getAllUserEvent(false), WebSocketType.MARGIN);
				}
			}
		};

		btcUserEventSubscriber = new WsSubscriber<WsUserEvent>(TOPIC_BTC_USER_EVENT) {
			@Override
			protected void onMessage(List<WsUserEvent> data, String action) {
				handleUserEvent(data, WebSocketType.BTC);
			}

			@Override
			protected void onConnectSuccess() {
				super.onConnectSuccess();

				if (ServiceRepo.getUserService().isLogin()) {
					//连接成功之后，手动将每个事件分发一次；
					//避免websocket断开之后，重新连上的时候界面没有刷新
					DLog.d("websocket", "btc subscriber 连接成功之后，手动下发 userevent");
					handleUserEvent(getAllUserEvent(true), WebSocketType.BTC);
				}
			}
		};

		usdtUserEventSubscriber = new WsSubscriber<WsUserEvent>(TOPIC_USDT_USER_EVENT) {
			@Override
			protected void onMessage(List<WsUserEvent> data, String action) {
				handleUserEvent(data, WebSocketType.USDT);
			}

			@Override
			protected void onConnectSuccess() {
				super.onConnectSuccess();

				if (ServiceRepo.getUserService().isLogin()) {
					//连接成功之后，手动将每个事件分发一次；
					//避免websocket断开之后，重新连上的时候界面没有刷新
					DLog.d("websocket", "usdt subscriber 连接成功之后，手动下发 userevent");
					handleUserEvent(getAllUserEvent(true), WebSocketType.USDT);
				}
			}
		};
	}

	private List<WsUserEvent> getAllUserEvent(boolean isContract) {
		List<WsUserEvent> list = new ArrayList<>();
		list.add(new WsUserEvent(UserEventType.ACCOUNT_CHANGED.value()));
		list.add(new WsUserEvent(UserEventType.CURORDER_CHANGED.value()));
		list.add(new WsUserEvent(UserEventType.HISORDER_CHANGED.value()));
		list.add(new WsUserEvent(UserEventType.PLANORDER_CHANGED.value()));
		if (isContract) {
			//合约才有的UserEvent
			list.add(new WsUserEvent(UserEventType.POSITIONS_CHANGED.value()));
			list.add(new WsUserEvent(UserEventType.LIQUIDATION.value()));
		}
		return list;
	}

	public static UsereventWebsocket getInstance() {
		if (mInstance == null) {
			synchronized (UsereventWebsocket.class) {
				if (mInstance == null) {
					mInstance = new UsereventWebsocket();
				}
			}
		}
		return mInstance;
	}


	public void registerUsereventListener(WebSocketType webSocketType, UserEventListener listener) {
		switch (webSocketType) {
			case SPOT:
				if (!spotUserEventListenerList.contains(listener)) {
					spotUserEventListenerList.add(listener);
				}
				break;
			case MARGIN:
				if (!marginUserEventListenerList.contains(listener)) {
					marginUserEventListenerList.add(listener);
				}
				break;
			case BTC:
				if (!btcUserEventListenerList.contains(listener)) {
					btcUserEventListenerList.add(listener);
				}
				break;
			case USDT:
				if (!usdtUserEventListenerList.contains(listener)) {
					usdtUserEventListenerList.add(listener);
				}
				break;
		}

	}

	public void unregisterUsereventListener(WebSocketType webSocketType, UserEventListener listener) {
		switch (webSocketType) {
			case SPOT:
				if (spotUserEventListenerList.contains(listener)) {
					spotUserEventListenerList.remove(listener);
				}
				break;
			case MARGIN:
				if (marginUserEventListenerList.contains(listener)) {
					marginUserEventListenerList.remove(listener);
				}
				break;
			case BTC:
				if (btcUserEventListenerList.contains(listener)) {
					btcUserEventListenerList.remove(listener);
				}
				break;
			case USDT:
				if (usdtUserEventListenerList.contains(listener)) {
					usdtUserEventListenerList.remove(listener);
				}
				break;
		}

	}

	private void handleUserEvent(List<WsUserEvent> data, WebSocketType type) {
		for (int i = 0; i < data.size(); i++) {
			UserEventType userEventType = UserEventType.getUserEvent(data.get(i).getKey());
			switch (type) {
				case SPOT:
					dispatchUserEventType(userEventType, spotUserEventListenerList);
					break;
				case MARGIN:
					dispatchUserEventType(userEventType, marginUserEventListenerList);
					break;
				case BTC:
					dispatchUserEventType(userEventType, btcUserEventListenerList);
					break;
				case USDT:
					dispatchUserEventType(userEventType, usdtUserEventListenerList);
					break;
			}
		}
	}

	private void dispatchUserEventType(UserEventType userEventType, List<UserEventListener> listenerList) {
		if (userEventType == null) {
			return;
		}
		switch (userEventType) {
			case ACCOUNT_CHANGED:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onAccountChanged();
					}
				}
				break;
			case POSITIONS_CHANGED:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onPositionsChanged();
					}
				}
				break;
			case CURORDER_CHANGED:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onCurorderChanged();
					}
				}
				break;
			case HISORDER_CHANGED:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onHisorderChanged();
					}
				}
				break;
			case PLANORDER_CHANGED:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onPlanorderChanged();
					}
				}
				break;
			case LIQUIDATION:
				synchronized (listenerList) {
					for (UserEventListener listener : listenerList) {
						listener.onLiquidation();
					}
				}
				break;
		}
	}

	@Override
	public void subScribeAll() {
		spotUserEventSubscriber.subScribe();
		marginUserEventSubscriber.subScribe();
		btcUserEventSubscriber.subScribe();
		usdtUserEventSubscriber.subScribe();
	}

	@Override
	public void unsubScribeAll() {
		spotUserEventSubscriber.unSubScribe();
		marginUserEventSubscriber.unSubScribe();
		btcUserEventSubscriber.unSubScribe();
		usdtUserEventSubscriber.unSubScribe();
	}

	@Override
	public void subAll() {
		checkSubscriberStatus(spotUserEventSubscriber);
		checkSubscriberStatus(marginUserEventSubscriber);
		checkSubscriberStatus(btcUserEventSubscriber);
		checkSubscriberStatus(usdtUserEventSubscriber);

		spotUserEventSubscriber.sendSubRequest();
		marginUserEventSubscriber.sendSubRequest();
		btcUserEventSubscriber.sendSubRequest();
		usdtUserEventSubscriber.sendSubRequest();
	}

	@Override
	public void unsubAll() {
		spotUserEventSubscriber.sendUnsubRequest();
		marginUserEventSubscriber.sendUnsubRequest();
		btcUserEventSubscriber.sendUnsubRequest();
		usdtUserEventSubscriber.sendUnsubRequest();
	}

	public interface UserEventListener {

		void onAccountChanged();

		void onPositionsChanged();

		void onCurorderChanged();

		void onHisorderChanged();

		void onPlanorderChanged();

		void onLiquidation();
	}
}

