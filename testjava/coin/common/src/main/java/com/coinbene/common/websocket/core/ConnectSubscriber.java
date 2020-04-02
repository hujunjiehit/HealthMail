package com.coinbene.common.websocket.core;

import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.NetUtil;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.TimeUtils;
import com.coinbene.common.websocket.model.WsRequest;
import com.coinbene.common.websocket.model.WsResponse;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.websocket.WebSocketSubscriber;
import com.google.gson.JsonSyntaxException;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import okhttp3.WebSocket;

/**
 * 保持websocket连接的subscriber
 * 只要有subscriber存在，websocket连接断开就会自动重连
 */
public class ConnectSubscriber extends WebSocketSubscriber {

	private String connectUrl;
	private boolean isConnect;

	public boolean isConnect() {
		return isConnect;
	}

	public ConnectSubscriber(String connectUrl) {
		this.connectUrl = connectUrl;
	}

	@Override
	protected void onOpen(WebSocket webSocket) {
		super.onOpen(webSocket);
	}

	@Override
	protected void onReconnect() {
		DLog.d("websocket", "websocket is reconneting.....");
		super.onReconnect();
		isConnect = false;
	}

	@Override
	protected void onMessage(String text) {
		if ("ping".equals(text)) {
			DLog.d("websocket", "==> client send pong to server");
			WebSocketManager.getInstance().sendMessage("pong");
			return;
		}

		if (text.contains("\"event\":\"login\"")) {
			//处理login订阅返回结果
			handlerLoginResponse(text);
		} else if (text.contains("\"event\":\"error\"")) {
			//处理error
			handlerError(text);
		}
	}

	private void handlerLoginResponse(String text) {
		DLog.e("websocket", "===> handlerLoginResponse, text = " + text);
		WsResponse loginResponse;
		try {
			loginResponse = CBRepository.gson.fromJson(text, WsResponse.class);
		} catch (JsonSyntaxException e) {
			DLog.d("websocket", "handlerLoginResponse,JsonSyntaxException");
			return;
		}
		if (loginResponse != null && !TextUtils.isEmpty(loginResponse.getEvent())) {
			if (loginResponse.getEvent().equals(WsRequest.OPERATION_LOGIN)) {
				if (loginResponse.isSuccess()) {
					DLog.d("websocket", "==> ws login success");

					UsereventWebsocket.getInstance().subScribeAll();
					UsereventWebsocket.getInstance().subAll();
				} else {
					DLog.d("websocket", "==> ws login failed, " + loginResponse.toString());
				}
			}
		}
	}


	private void handlerError(String text) {
		DLog.e("websocket", "===> handlerError, text = " + text);
		WsResponse errorResponse;
		try {
			errorResponse = CBRepository.gson.fromJson(text, WsResponse.class);
		} catch (JsonSyntaxException e) {
			DLog.d("websocket", "handlerLoginResponse,JsonSyntaxException");
			return;
		}
		if (errorResponse != null) {
			switch (errorResponse.getCode()) {
				case 10511:
					//token 失效，重新刷token
					refreshToken();
					break;
				default:
					DLog.e("websocket", "unhandled error," + errorResponse.toString());
			}
		}
	}

	@AddFlowControl
	private void refreshToken() {
		DLog.e("websocket", "token 失效，重新刷token");

		UserInfoTable userInfoTable = UserInfoController.getInstance().getUserInfo();
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		if (userInfoTable == null || TextUtils.isEmpty(userInfoTable.refreshToken)) {
			gotoLogin();
		}
		DLog.d("websocket", "post USER_REFRESH_TOKEN request");
		OkGo.<UserInfoResponse>post(Constants.USER_REFRESH_TOKEN)
				.headers("site", SiteController.getInstance().getSiteName())
				.headers("clientData", NetUtil.getSystemParam().toString())
				.headers("lang", LanguageHelper.getProcessedCode(localeCode))
				.headers("timeZone", TimeUtils.getCurrentTimeZone())
				.headers("refresh-token", userInfoTable.refreshToken)
				.execute(new NewJsonSubCallBack<UserInfoResponse>() {
					@Override
					public void onSuc(Response<UserInfoResponse> response) {

					}

					@Override
					public UserInfoResponse dealJSONConvertedResult(UserInfoResponse response) {
						if(response != null && response.getData() != null) {
							UserInfoController.getInstance().updateUserInfo(response.getData());
							WebSocketManager.getInstance().sendLoginRequest();
						}
						return super.dealJSONConvertedResult(response);
					}

					@Override
					public void onE(Response<UserInfoResponse> response) {
						gotoLogin();
					}
				});

	}

	private void gotoLogin() {
		ServiceRepo.getUserService().logOut();

		//token 失效跳转到登陆页面，如果用户不登陆，按返回键回到行情页面
		ARouter.getInstance().build(RouteHub.User.loginActivity)
				.withBoolean("forceQuit",true)
				.navigation(CBRepository.getLifeCallback().getCurrentAcitivty());
	}

	@Override
	protected void onConnectSuccess() {
		super.onConnectSuccess();
		isConnect = true;
		DLog.d("websocket", connectUrl + "   ConnectSubscriber ----连接成功");

		if (CommonUtil.isLoginAndUnLocked() && connectUrl.equals(Constants.BASE_WEBSOCKET_CONTRACT_BTC)) {
			WebSocketManager.getInstance().sendSetBtcClientData();
		}
		if (CommonUtil.isLoginAndUnLocked() && connectUrl.equals(Constants.BASE_WEBSOCKET_CONTRACT_USDT)) {
			WebSocketManager.getInstance().sendSetUsdtClientData();
		}

		if (connectUrl.equals(Constants.BASE_WEBSOCKET)) {
			WebSocketManager.getInstance().sendChangeSiteLang();
		}

		if (CommonUtil.isLoginAndUnLocked() && connectUrl.equals(Constants.BASE_WEBSOCKET_URL)) {
			WebSocketManager.getInstance().sendLoginRequest();
		}
	}


	@Override
	protected void onClose() {
		super.onClose();
		isConnect = false;
	}
}
