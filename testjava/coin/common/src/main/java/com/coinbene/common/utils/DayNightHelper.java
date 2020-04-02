package com.coinbene.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


/**
 * ding
 * 2019-05-11
 * com.coinbene.common.utils
 * App配置改变工具类
 */
public final class DayNightHelper {

	static final String TAG = "DayNightHelper";


	public static final String SP_NAME_NIGHTMODE = "night_mode";

	/**
	 * 夜间模式
	 */
	public static final int DARK = AppCompatDelegate.MODE_NIGHT_YES;

	/**
	 * 日间模式
	 */
	public static final int LIGHT = AppCompatDelegate.MODE_NIGHT_NO;

	/**
	 * @param context App系统初始配置
	 */
	public static void init(Context context) {
		if (!ConfigHelper.nightModeEnable()) {
			return;
		}

		AppCompatDelegate.setDefaultNightMode(getMode(context));
	}

	/**
	 * @param activity 必须为AppCompatActivity
	 * @param mode     设置夜间模式
	 *                 设置当前页面有效
	 */
	public static void setCurrentPageMode(@NonNull AppCompatActivity activity, int mode) {
		if (getMode(activity) != mode) {
			AppCompatDelegate delegate = activity.getDelegate();
			delegate.setLocalNightMode(mode);
		}
	}


	/**
	 * 用于所有页面默认日夜间模式，如果设置当前页面请调用setLocalNightMode（）方法设置当前页面
	 * 调用后应该recreat Activity
	 */
	public static void setMode(Context context, int mode) {
		if (!ConfigHelper.nightModeEnable()) {
			return;
		}
		Log.d(TAG, "设置默认日夜间模式");
		SpUtil.put(context, SP_NAME_NIGHTMODE, mode);
		AppCompatDelegate.setDefaultNightMode(getMode(context));
	}

	/**
	 * @return 获得本地当前日夜间模式
	 */
	private static int getMode(Context context) {
		return SpUtil.get(context, SP_NAME_NIGHTMODE, LIGHT);
	}

	/**
	 * @param context
	 * @return 是否为夜间
	 */
	public static boolean isNight(@NonNull Context context) {
		return getMode(context) == DARK;
	}

	/**
	 * @param activity
	 * @param uiNightMode 更新App配置
	 */
	public static void updateConfig(Activity activity, int uiNightMode) {
		Configuration newConfig = new Configuration(activity.getResources().getConfiguration());
		newConfig.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
		newConfig.uiMode |= uiNightMode;
		activity.getResources().updateConfiguration(newConfig, null);
	}

}
