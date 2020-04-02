package com.coinbene.common.utils;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;

import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

/**
 * app相关信息工具类
 */
public class AppUtil {

	/**
	 * 获取当前程序包名
	 *
	 * @param context 上下文
	 * @return 程序包名
	 */
	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	/**
	 * 获取程序版本信息
	 *
	 * @param context 上下文
	 * @return 版本名称
	 */
	public static String getVersionName(Context context) {
		String versionName = null;
		String pkName = context.getPackageName();
		try {
			versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return versionName;
	}

	/**
	 * 获取程序版本号
	 *
	 * @param context 上下文
	 * @return 版本号
	 */
	public static int getVersionCode(Context context) {
		int versionCode = -1;
		String pkName = context.getPackageName();
		try {
			versionCode = context.getPackageManager().getPackageInfo(pkName, 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
		}
		return versionCode;
	}

	public static String getIMEI_new(Context context) {
		String imei = "";
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
			return "";
		} else {
			imei = mTelephony.getDeviceId();
		}
		return TextUtils.isEmpty(imei) ? "" : imei;
	}

	public static String getAndroidID(Context context) {
		String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
		if (TextUtils.isEmpty(androidId) || "9774d56d682e549c".equals(androidId) || androidId.length() < 8) {
			return "";
		}
		return androidId;
	}

	public static String getMac(Context context) {
		try {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			String mac = wm.getConnectionInfo().getMacAddress();
			return mac == null ? "" : MD5Util.md5(mac.getBytes());
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * @return
	 */
	public static String getOsVer() {
		return android.os.Build.MODEL;
	}


	/*
	 * 判断服务是否启动,context上下文对象 ，className服务的name
	 */
	public static boolean isServiceRunning(Context mContext, String className) {

		boolean isRunning = false;
		ActivityManager activityManager = (ActivityManager) mContext
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> serviceList = activityManager
				.getRunningServices(30);

		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (int i = 0; i < serviceList.size(); i++) {
			if (serviceList.get(i).service.getClassName().contains(className)) {
				isRunning = true;
				break;
			}
		}
		return isRunning;
	}


	public static boolean isMainThread() {
		return Looper.getMainLooper() == Looper.myLooper();
	}
}
