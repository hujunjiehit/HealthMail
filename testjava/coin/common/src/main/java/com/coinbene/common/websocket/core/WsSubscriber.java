package com.coinbene.common.websocket.core;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.websocket.model.WsRequest;
import com.coinbene.common.websocket.model.WsResponse;
import com.coinbene.manbiwang.websocket.WebSocketSubscriber;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import okhttp3.WebSocket;

/**
 * Created by june
 * on 2020-01-13
 */
public abstract class WsSubscriber<T> extends WebSocketSubscriber {
	private static final Gson GSON = new Gson();

	protected Type type;
	protected WsRequest request;

	private String topic;
	private List<String> argsList;

	private JsonParser jsonParser;
	private JsonObject jsonObject;

	private boolean hasSubed = false;

	private volatile static Map<String, ExecutorService> threadPoolMap;

	public WsSubscriber(String topic) {
		this.topic = topic;
		analysisType();
		request = new WsRequest();
	}

	private void analysisType() {
		Type superclass = getClass().getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("No generics found!");
		}
		ParameterizedType type = (ParameterizedType) superclass;
		this.type = type.getActualTypeArguments()[0];
	}

	protected ParameterizedType type(final Class raw, final Type... args) {
		return new ParameterizedType() {
			public Type getRawType() {
				return raw;
			}

			public Type[] getActualTypeArguments() {
				return args;
			}

			public Type getOwnerType() {
				return null;
			}
		};
	}

	public void updateTopic(String topic) {
		this.topic = topic;
	}

	@SuppressLint("CheckResult")
	@Override
	protected void onMessage(String text) {
		if ("ping".equals(text)) {
			//收到ping消息，直接返回
			return;
		}

		if (!topic.contains(getTopic(text))) {
			//不是当前topic，直接返回
			return;
		}

		if (threadPoolMap == null) {
			threadPoolMap = new ConcurrentHashMap<>();
		}
		String topicKey;
		if (topic.contains(".")) {
			topicKey = topic.substring(0, topic.indexOf("."));
		} else {
			topicKey = topic;
		}

		if (threadPoolMap.get(topicKey) == null) {
			//一个topic指定一个线程池
			threadPoolMap.put(topicKey, Executors.newSingleThreadExecutor());
		}

		Observable.just(text)
				.map((Function<String, WsResponse>) s -> {
					try {
						return GSON.fromJson(s, type(WsResponse.class, type));
					} catch (JsonSyntaxException e) {
						DLog.d("websocket", "JsonSyntaxException, topic = " + topic);
						DLog.d("websocket", "JsonSyntaxException, s = " + s);
						return GSON.fromJson(GSON.fromJson(s, String.class), type);
					}
				})
				.filter(wsResponse -> {
					if (wsResponse.getData() == null || wsResponse.getTopic() == null) {
						return false;
					}
					return topic.contains(wsResponse.getTopic()) && wsResponse.getData().size() > 0;
				})
				.observeOn(Schedulers.from(threadPoolMap.get(topicKey)))
				.subscribe(new Consumer<WsResponse>() {
					@Override
					public void accept(WsResponse t) throws Exception {
						onMessage(t.getData(), t.getAction());
					}
				}, new Consumer<Throwable>() {
					@Override
					public void accept(Throwable throwable) throws Exception {
						DLog.d("websocket", "JsonSyntaxException, s = " + throwable.getMessage());
					}
				});
	}

	private String getTopic(String text) {
		if (text.contains("\"topic\"")) {
			try {
				if (jsonParser == null) {
					jsonParser = new JsonParser();
				}
				jsonObject = jsonParser.parse(text).getAsJsonObject();
				return jsonObject.get("topic").toString().replace("\"", "");
			} catch (Exception e) {
				return "";
			}
		}
		return "";
	}

	protected abstract void onMessage(List<T> data, String action);


	@Override
	protected void onOpen(WebSocket webSocket) {
		WebSocketManager.getInstance().setHasOpened(true);
		super.onOpen(webSocket);
	}

	@Override
	protected void onClose() {
		DLog.d("websocket", topic + " ====> onClose");
		WebSocketManager.getInstance().setHasOpened(false);
		super.onClose();
	}

	/**
	 * 每次断开重连，重新发送订阅消息
	 */
	@Override
	protected void onConnectSuccess() {
		DLog.d("websocket", topic + " ====> onOpen");

		super.onConnectSuccess();
		if (TextUtils.isEmpty(topic) || topic.contains("userEvent")) {
			//userevent相关的topic，login成功之后再发
			return;
		}
		if (getDisposable() != null && !getDisposable().isDisposed()) {
			DLog.d("websocket", "连接成功之后订阅, topic:" + topic);
			if (WebSocketManager.getInstance().hasOpened()) {
				sendSubRequest();
			}
		}
	}

	@Override
	protected void onReconnect() {
		WebSocketManager.getInstance().setHasOpened(false);
		super.onReconnect();
		DLog.d("websocket", "websocket is reconneting.....");
		hasSubed = false;
	}

	public void subScribe() {
		WebSocketManager.with(Constants.BASE_WEBSOCKET_URL).subscribe(this);
	}

	public void unSubScribe() {
		if (getDisposable() != null && !getDisposable().isDisposed()) {
			dispose();
		}
		if (threadPoolMap != null && threadPoolMap.get(topic) != null) {
			threadPoolMap.get(topic).shutdownNow();
			threadPoolMap.remove(topic);
		}

		if (threadPoolMap != null && threadPoolMap.size() == 0) {
			threadPoolMap = null;
		}
	}

	public WsRequest getRequest() {
		return request;
	}

	public void setRequest(WsRequest request) {
		this.request = request;
	}

	public void sendSubRequest() {
		if (!topic.contains("userEvent") && !WebSocketManager.getInstance().hasOpened()) {
			//订阅userevent的时候，肯定是连接成功了，所以不用判断isSocketConnect
			return;
		}
		if (hasSubed) {
			return;
		}
		hasSubed = true;
		if (argsList == null) {
			argsList = new ArrayList<>();
		}
		argsList.clear();
		argsList.add(topic);

		request.setArgs(argsList);
		request.setOp(WsRequest.OPERATION_SUB);
		WebSocketManager.getInstance().send(request);

	}

	public void sendUnsubRequest() {
		hasSubed = false;
		if (argsList == null) {
			argsList = new ArrayList<>();
		}
		argsList.clear();
		argsList.add(topic);

		request.setArgs(argsList);
		request.setOp(WsRequest.OPERATION_UNSUB);
		WebSocketManager.getInstance().send(request);
	}
}
