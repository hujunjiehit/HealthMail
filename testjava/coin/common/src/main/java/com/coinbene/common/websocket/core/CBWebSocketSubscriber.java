package com.coinbene.common.websocket.core;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.coinbene.common.model.websocket.WsBaseRequest;
import com.coinbene.common.utils.DLog;
import com.coinbene.manbiwang.websocket.OldWebSocketSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;

public abstract class CBWebSocketSubscriber<T> extends OldWebSocketSubscriber {

	public static final int STATUS_SUBED = 0;
	public static final int STATUS_UNSUBED = 1;

	private static final Gson GSON = new Gson();
	protected Type type;
	protected String topic;
	protected String url;
	private WsBaseRequest request;

	int status = STATUS_UNSUBED;

	private CBWebSocketSubscriber() {
		analysisType();
	}

	public CBWebSocketSubscriber(String url, String topicString) {
		this.url = url;
		this.topic = topicString;
		request = new WsBaseRequest(WsBaseRequest.SUB, WsBaseRequest.RID, topic);
		analysisType();
	}


	private void analysisType() {
		Type superclass = getClass().getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("No generics found!");
		}
		ParameterizedType type = (ParameterizedType) superclass;
		this.type = type.getActualTypeArguments()[0];
	}


	@SuppressLint("CheckResult")
	@Override
	@CallSuper
	protected void onMessage(@NonNull String text) {
		if (TextUtils.isEmpty(text)) {
			return;
		}
		if (text.contains("badRequestRe")) {
			return;
		}

		if (text.contains("\"op\":\"subscribeRe\"")) {
			status = STATUS_SUBED;
		} else if (text.contains("\"op\":\"unsubscribeRe\"")) {
			status = STATUS_UNSUBED;
		}

		Observable.just(text)
				.filter(new Predicate<String>() {
					@Override
					public boolean test(String t) throws Exception {
						/**
						 * 过滤text，避免不必要的json解析
						 *
						 * 1、t.contains("\"topic\":\""+topic+"\"") 过滤非相关topic的text
						 *
						 * 2、!t.contains("subscribeRe") 过滤包含subscribeRe 和 unsubscribeRe 的text
						 */
						return t.contains("\"topic\":\"" + topic + "\"") && !t.contains("subscribeRe");
					}
				})
				.map(new Function<String, T>() {
					@Override
					public T apply(String s) throws Exception {
						try {
							return GSON.fromJson(s, type);
						} catch (JsonSyntaxException e) {
							DLog.d("websocket", "JsonSyntaxException, topic = " + topic);
							DLog.d("websocket", "JsonSyntaxException, s = " + s);
							return GSON.fromJson(GSON.fromJson(s, String.class), type);
						}
					}
				})
				.observeOn(Schedulers.single())
				.subscribe(new Consumer<T>() {
					@Override
					public void accept(T t) throws Exception {
						//if (!SiteController.getInstance().getSiteName().equals(Constants.SITE_BR)) {
							//巴西站点目前不走websocket，不分发数据
							onMessage(t);
						//}
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						DLog.d("websocket", "JsonSyntaxException, s = " + throwable.getMessage());
					}
				});

	}

	protected abstract void onMessage(T t);


	@Override
	protected void onOpen(WebSocket webSocket) {
		super.onOpen(webSocket);
		status = STATUS_UNSUBED;
	}

	/**
	 * 每次断开重连，重新发送订阅消息
	 */
	@Override
	protected void onConnectSuccess() {
		super.onConnectSuccess();
		if (getDisposable() != null && !getDisposable().isDisposed() && status == STATUS_UNSUBED) {
			DLog.d("websocket", "连接成功之后订阅, topic:" + topic);
			sendSubRequest();
		}
	}

	@Override
	protected void onReconnect() {
		super.onReconnect();
		DLog.d("websocket", url + " is reconnecting ...");
		status = STATUS_UNSUBED;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void sendSubRequest() {
		if (request == null || url == null) {
			return;
		}
		request.setOp(WsBaseRequest.SUB);
		request.setTopic(topic);
		WebSocketManager.getInstance().send(url, request);

	}

	public void sendRequest(String subOrUnsub) {
		if (request == null || url == null) {
			return;
		}
		request.setOp(subOrUnsub);
		request.setTopic(topic);
		if ("sub".equals(subOrUnsub)) {
			sendSubRequest();
		} else {
			sendUnsubRequest();
		}
	}

	public void sendUnsubRequest() {
		if (request == null || url == null) {
			return;
		}
		request.setOp(WsBaseRequest.UN_SUB);
		request.setTopic(topic);
		WebSocketManager.getInstance().send(url, request);
	}

	public void subScribe() {
		WebSocketManager.with(url).subscribe(this);
	}

	public void unSubScribe() {
		if (getDisposable() != null && !getDisposable().isDisposed()) {
			dispose();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
}
