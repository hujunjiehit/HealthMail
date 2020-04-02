package com.coinbene.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.lzy.okgo.model.HttpParams;

import org.json.JSONObject;

import java.util.Map;

/**
 * 网络相关
 */
public class NetUtil {

	// 判断网络是否可用
	public static boolean isNetworkAvailable() {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) CBRepository.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}


	/**
	 * 是否连接Wifi
	 */
	public static boolean isWifiConnection() {
		ConnectivityManager connectivityManager = (ConnectivityManager) CBRepository.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		return wifiNetworkInfo.isConnected();
	}

	public static JSONObject getSystemParam() {
		Context context = CBRepository.getContext().getApplicationContext();
		JSONObject jsonObject = new JSONObject();
		try {
//            jsonObject.put("system_version", android.os.Build.VERSION.RELEASE);//系统版本
//            jsonObject.put("phone_model", android.os.Build.MODEL);//手机型号
//            jsonObject.put("device_id", AppUtil.getAndroidID(context));
//            jsonObject.put("uuid", AppUtil.getIMEI_new(context));
//            jsonObject.put("mac", AppUtil.getMac(context));
//            jsonObject.put("client_type", 1);
//            jsonObject.put("app_version", BuildConfig.VERSION_NAME);


			jsonObject.put("osVer", android.os.Build.VERSION.RELEASE);//系统版本号
			jsonObject.put("phoneType", android.os.Build.MANUFACTURER);//手机型号
			jsonObject.put("deviceId", AppUtil.getAndroidID(context));//deviceId
			jsonObject.put("uuid", AppUtil.getIMEI_new(context));//uuid
			jsonObject.put("mac", AppUtil.getMac(context));//mac地址
			jsonObject.put("clientType", 1);//客户端来源  1为android
			jsonObject.put("appVer", CBRepository.versionName);//app版本号
			jsonObject.put("store", CBRepository.getChannelValue());//市场渠道

		} catch (Exception ex) {
		}
		return jsonObject;
	}

	public static JSONObject getClickPostPointData(String clickValue) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("app_click_content", clickValue);
			jsonObject.put("app_os_version", android.os.Build.VERSION.RELEASE);
			jsonObject.put("app_phone_type", android.os.Build.MANUFACTURER);
			jsonObject.put("app_client_type", "android");
			jsonObject.put("app_site", SiteController.getInstance().getSiteName());
			jsonObject.put("app_version", CBRepository.versionName);
			jsonObject.put("app_from_channel", CBRepository.getChannelValue());//市场渠道
			jsonObject.put("app_night_mode", DayNightHelper.isNight(CBRepository.getContext()) ? "night" : "day");
		} catch (Exception ex) {
		}
		return jsonObject;
	}

	public static JSONObject getBorwerPostPointData(String pageValue) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("app_brower_page", pageValue);
			jsonObject.put("app_os_version", android.os.Build.VERSION.RELEASE);
			jsonObject.put("app_phone_type", android.os.Build.MANUFACTURER);
			jsonObject.put("app_client_type", "android");
			jsonObject.put("app_version", CBRepository.versionName);
			jsonObject.put("app_from_channel", CBRepository.getChannelValue());//市场渠道
			jsonObject.put("app_night_mode", DayNightHelper.isNight(CBRepository.getContext()) ? "night" : "day");
		} catch (Exception ex) {
		}
		return jsonObject;
	}

	public static void addCommonParam(Map<String, Object> paramMap) {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		paramMap.put("basicData", NetUtil.getSystemParam().toString());
		if (userTable != null) {
			paramMap.put("token", userTable.token);
		}
	}

	public static void addCommonHttpParam(HttpParams param) {
		param.put("basicData", NetUtil.getSystemParam().toString());
	}

	public static void addCommonHttpParamToken(HttpParams param) {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		param.put("basicData", NetUtil.getSystemParam().toString());
		if (userTable != null) {
			param.put("token", userTable.token);
		}
	}

	public static void addTokenParam(HttpParams param) {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable != null) {
			param.put("token", userTable.token);
		}
	}

}
