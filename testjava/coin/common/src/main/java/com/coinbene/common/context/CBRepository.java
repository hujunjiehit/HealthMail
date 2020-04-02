package com.coinbene.common.context;

import android.content.Context;
import android.text.TextUtils;

import com.coinbene.common.ActivityLifeCallback;
import com.coinbene.common.BuildConfig;
import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.database.MyObjectBox;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.SpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.tencent.mmkv.MMKV;

import io.objectbox.Box;
import io.objectbox.BoxStore;

public class CBRepository {

	static Context mContext = null;

	private static int current_environment = -1;
	private static int default_environment;

	private static Environment mEnvironment;
	private static ActivityLifeCallback mLifeCallback;

	private static boolean isWebSocket = false;  //是否启用websocket

	private static boolean isInDebugWhiteList = false;  //是否在调试白名单内

	private static boolean isStartedMainActivity = false;//是否启动过MainActivity

	private static BoxStore boxStore;

	private static String channel_value;

	public static int uiMode;

	public static Gson gson = new Gson();

	public static int versionCode;
	public static String versionName;

	public static void init(Context context) {
		mContext = context;
		if (BuildConfig.ENABLE_DEBUG) {
			default_environment = Constants.TEST_ENVIROMENT;
		} else {
			default_environment = Constants.ONLINE_ENVIROMENT;
		}

		//init mmkv
		String rootDir = MMKV.initialize(context);

		current_environment = SpUtil.get(mContext, SpUtil.CURRENT_ENVIRONMENT, default_environment);

		isInDebugWhiteList = SpUtil.isInDebugWhiteList();

		boxStore = MyObjectBox.builder().androidContext(context).build();
	}

	public static BoxStore getBoxStore() {
		return boxStore;
	}

	public static <T> Box<T> boxFor(Class<T> t) {
		return boxStore.boxFor(t);
	}

	public static Context getContext() {
		return mContext;
	}

	public static int getDefaultEnvironment() {
		return default_environment;
	}

	public static String getChannelValue() {
		return channel_value;
	}

	public static void setChannelValue(String channel_value) {
		CBRepository.channel_value = channel_value;
	}

	public static Environment getCurrentEnvironment() {
		if (mEnvironment == null) {
			if (current_environment >= 0 && current_environment < Constants.ENVIRONMENT_COUNT) {
				mEnvironment = new Environment(current_environment);
			} else {
				mEnvironment = new Environment(default_environment);
			}
		}
		return mEnvironment;
	}


	public static void setContext(Context mContext) {
		CBRepository.mContext = mContext;
	}

	public static ActivityLifeCallback getLifeCallback() {
		return mLifeCallback;
	}

	public static void setLifeCallback(ActivityLifeCallback mLifeCallback) {
		CBRepository.mLifeCallback = mLifeCallback;
	}

	public static void setIsWebSocket(boolean webSocket) {
		isWebSocket = webSocket;
	}

	public static boolean isWebSocket() {
		return isWebSocket;
	}

	public static boolean isInDebugWhiteList() {
		return isInDebugWhiteList;
	}

	public static void setIsInDebugWhiteList() {
		if (isInDebugWhiteList()) {
			//如果已经在白名单直接返回
			return;
		}
		UserInfoTable userInfoTable = UserInfoController.getInstance().getUserInfo();
		if (userInfoTable == null || TextUtils.isEmpty(userInfoTable.loginId)) {
			return;
		}
		String[] whiteList = getContext().getResources().getStringArray(R.array.debug_white_list);
		for (String item : whiteList) {
			if (userInfoTable.loginId.equals(item.trim()) || (userInfoTable.phone != null && userInfoTable.phone.equals(item.trim()))) {
				isInDebugWhiteList = true;
				SpUtil.setDebugWhiteList(true);
				return;
			}
		}
	}

	public static boolean isStartedMainActivity() {
		return isStartedMainActivity;
	}

	public static void setMainActivityStarted(boolean isStarted) {
		isStartedMainActivity = isStarted;
	}

	public static boolean getEnableDebug() {
		if (BuildConfig.ENABLE_DEBUG || isInDebugWhiteList()) {
			return true;
		}
		return false;
	}


	public static <T> T fromJson(String json, Class<T> classOfT) {
		try {
			return gson.fromJson(json, classOfT);
		} catch (JsonSyntaxException jsonSyntaxException) {
			jsonSyntaxException.printStackTrace();
			return null;
		}
	}

	public static class Environment {
		public String BASE_URL;
		public String BASE_URL_H5;
		public String BASE_URL_RES;
		public String BASE_WEBSOCKET;
		public String BASE_WEBSOCKET_CONTRACT;
		public String BASE_WEBSOCKET_USDT;
		public String BASE_WEBSOCKET_URL;
		public String OPTIONS_BROKER_ID;

		public int environmentType; //对应当前环境的int值

		public Environment(int environmentType) {

			ProductConfig.BaseUrlConfigBean urlConfig = ConfigHelper.getUrlConfig(environmentType);

			BASE_URL = urlConfig.getBase_url(environmentType);
			BASE_URL_H5 = urlConfig.getBase_url_h5(environmentType);
			BASE_URL_RES = urlConfig.getBase_url_res();
			BASE_WEBSOCKET = urlConfig.getSpot_websocket();
			BASE_WEBSOCKET_CONTRACT = urlConfig.getBtc_contract_websocket();
			BASE_WEBSOCKET_USDT = urlConfig.getUsdt_contract_websocket();
			BASE_WEBSOCKET_URL = urlConfig.getWebsocket_url(environmentType);
			OPTIONS_BROKER_ID = urlConfig.getOption_broker_id();

			this.environmentType = environmentType;
		}


		/**
		 * @return 锁定App时间
		 */
		public long[] getLockedTime() {

			long[] lockedTime = new long[3];

			if (SpUtil.get(getContext(), SpUtil.CURRENT_LOCK_TIME, 0) == 0) {
				lockedTime[0] = 15 * 60 * 1000;
				lockedTime[1] = 60 * 1000 * 60 * 72;
				lockedTime[2] = 60 * 1000 * 60 * 24 * 7;
			} else {
				lockedTime[0] = 10 * 1000;
				lockedTime[1] = 30 * 1000;
				lockedTime[2] = 60 * 1000;
			}
			return lockedTime;
		}

	}
}
