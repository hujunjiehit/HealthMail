package com.coinbene.common.websocket.core;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.model.websocket.WsBaseRequest;
import com.coinbene.common.network.newokgo.WsUrlReplaceIntercepter;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.websocket.model.WsRequest;
import com.coinbene.manbiwang.websocket.BuildConfig;
import com.coinbene.manbiwang.websocket.Config;
import com.coinbene.manbiwang.websocket.RxWebSocket;
import com.coinbene.manbiwang.websocket.RxWebSocketUtil;
import com.coinbene.manbiwang.websocket.WebSocketInfo;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import okhttp3.WebSocket;

public class WebSocketManager {

	private static volatile WebSocketManager mInstance;

	private Map<String, ConnectSubscriber> subscriberMap;

	private Gson gson;

	private volatile boolean hasOpened = false;

	private WebSocketManager() {
		subscriberMap = new HashMap<>();
		gson = new Gson();
	}

	public static WebSocketManager getInstance() {
		if (mInstance == null) {
			synchronized (WebSocketManager.class) {
				if (mInstance == null) {
					mInstance = new WebSocketManager();

					RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
						@Override
						public void accept(Throwable throwable) throws Exception {
							throwable.printStackTrace();

							if (throwable.getCause() != null && throwable.getCause() instanceof Error) {
								//上报错误到Sentry服务器
								Log.e("websocket", "RxJava catch global exception， msg:" + throwable.getCause().getMessage());
							}
						}
					});
				}
			}
		}
		return mInstance;
	}

	public void initWebSocket(Application application) {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.pingInterval(Constants.WEBSOCKET_PING_INTERVAL, TimeUnit.SECONDS);
		if (CBRepository.getCurrentEnvironment().environmentType == Constants.ONLINE_ENVIROMENT) {
			//线上环境才需要添加base_url replace 拦截器
			DLog.d("WsUrlReplaceIntercepter", "add WsUrlReplaceIntercepter");

			builder.addInterceptor(new WsUrlReplaceIntercepter());
		}

		Config.Builder configBuilder = new Config.Builder();
		if (BuildConfig.DEBUG) {
			configBuilder.setShowLog(true, "websocket");
		}
		configBuilder.setClient(builder.build());

		//configBuilder.setReconnectInterval(2, TimeUnit.SECONDS); //set reconnect interval
		//configBuilder.setSSLSocketFactory(yourSSlSocketFactory, yourX509TrustManager); // wss support

		RxWebSocket.setConfig(configBuilder.build());

		application.registerActivityLifecycleCallbacks(new WebsocketLifeCallback());
	}

	public static Observable<WebSocketInfo> with(String url) {
		return RxWebSocketUtil.getInstance().getWebSocketInfo(url);
	}

	public void connectSocket(String url) {
		ConnectSubscriber subscriber = subscriberMap.get(url);
		if (subscriber == null) {
			subscriber = new ConnectSubscriber(url);
			subscriberMap.put(url, subscriber);
		}
		if (subscriber.getDisposable() == null || subscriber.getDisposable().isDisposed()) {
			Log.e("websocket", "==> subscribe connect websocket");
			RxWebSocket.get(url).subscribe(subscriber);
		}
	}

	public void disconnectSocket(String url) {
		ConnectSubscriber subscriber = subscriberMap.get(url);
		if (subscriber != null && subscriber.getDisposable() != null) {
			subscriber.dispose();
		}
	}

	public boolean getConnectSubscriberStatus(String url) {
		ConnectSubscriber subscriber = subscriberMap.get(url);
		if (subscriber == null || !subscriber.isConnect()) {
			return false;
		}
		return true;
	}


	public boolean isSocketConnect(String url) {
		return hasOpened();
	}

	public void send(String url, WsBaseRequest request) {
//		if (!isSocketConnect(url)) {
//			return;
//		}
		if (gson == null) {
			gson = new Gson();
		}
		WebSocket webSocket = RxWebSocketUtil.getInstance().getWebSocketMap().get(url);
		if (webSocket != null && request != null) {
			DLog.d("websocket", "客户端发送请求, type：" + request.getOp() + ", request:" + gson.toJson(request));
			webSocket.send(gson.toJson(request));
		}
	}

	public void sendChangeSiteLang() {
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		WsBaseRequest request = new WsBaseRequest(WsBaseRequest.CHANGE_SITE_DATA,
				WsBaseRequest.RID,
				SiteController.getInstance().getSiteName(),
				LanguageHelper.getProcessedCode(localeCode));
		if (gson == null) {
			gson = new Gson();
		}
		WebSocket webSocket = RxWebSocketUtil.getInstance().getWebSocketMap().get(Constants.BASE_WEBSOCKET);
		if (webSocket != null && request != null) {
			DLog.d("websocket", "客户端发送请求, type：" + request.getOp() + ", topic:" + gson.toJson(request));
			webSocket.send(gson.toJson(request));
		}
	}

	/**
	 * 订阅user相关需要提前setClientData 发送发送authorization   btc合约
	 */
	public void sendSetBtcClientData() {
		WsBaseRequest request = new WsBaseRequest(WsBaseRequest.USER_CLIENT_DATA, WsBaseRequest.RID);
		if (UserInfoController.getInstance().getUserInfo() == null || TextUtils.isEmpty(UserInfoController.getInstance().getUserInfo().token))
			return;
		request.setAuthorization("Bearer " + UserInfoController.getInstance().getUserInfo().token);
		send(Constants.BASE_WEBSOCKET_CONTRACT_BTC, request);
	}


	/**
	 * 订阅user相关需要提前setClientData 发送发送authorization   usdt合约
	 */
	public void sendSetUsdtClientData() {
		WsBaseRequest request = new WsBaseRequest(WsBaseRequest.USER_CLIENT_DATA, WsBaseRequest.RID);
		if (UserInfoController.getInstance().getUserInfo() == null || TextUtils.isEmpty(UserInfoController.getInstance().getUserInfo().token))
			return;
		request.setAuthorization("Bearer " + UserInfoController.getInstance().getUserInfo().token);
		send(Constants.BASE_WEBSOCKET_CONTRACT_USDT, request);
	}


	public void send(WsRequest request) {
		if (gson == null) {
			gson = new Gson();
		}
		DLog.d("websocket", "客户端发送请求, type：" + request.getOp() + ", topic:" + gson.toJson(request));
		sendMessage(gson.toJson(request));
	}

	public void sendMessage(String message) {
		WebSocket webSocket = RxWebSocketUtil.getInstance().getWebSocketMap().get(Constants.BASE_WEBSOCKET_URL);
		if (webSocket != null && !TextUtils.isEmpty(message)) {
			webSocket.send(message);
		}
	}

	public void sendLoginRequest() {
		if (UserInfoController.getInstance().getUserInfo() == null) {
			return;
		}
		WsRequest loginRequest = new WsRequest();
		loginRequest.setOp(WsRequest.OPERATION_LOGIN);
		List<String> args = new ArrayList<>();
		args.add(UserInfoController.getInstance().getUserInfo().token);
		loginRequest.setArgs(args);
		send(loginRequest);
	}

	public boolean hasOpened() {
		return hasOpened;
	}

	public void setHasOpened(boolean hasOpened) {
		this.hasOpened = hasOpened;
	}
}
