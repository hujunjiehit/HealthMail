package com.coinbene.common.utils;

import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.database.UserInvitationController;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.google.gson.Gson;

import java.io.File;

/**
 * Created by mengxiangdong on 2017/12/13.
 */

public class CommonUtil {

	/**
	 * 是否设置过手势或者指纹
	 *
	 * @return
	 */
	public static boolean isSetFingerOrPattern() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			return false;
		}
		if (UserInfoController.getInstance().isSetFingerPrint() || UserInfoController.getInstance().isGesturePwdSet()) {
			return true;
		}
		return false;
	}


	/**
	 * true:已经登录，并且是解锁状态,
	 * false:未登录，或者未解锁
	 *
	 * @return
	 */
	public static boolean isLoginAndUnLocked() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			return false;
		} else if (UserInfoController.getInstance().isSetFingerPrint() || UserInfoController.getInstance().isGesturePwdSet()) {
			if (UserInfoController.getInstance().isLocked()) {
				return false;
			}
		}
		return true;
	}


	/**
	 * true:已经登录，或者锁住状态
	 * false:未登录
	 *
	 * @return
	 */
	public static boolean isLogin() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			return false;
		}
		return true;
	}

	/**
	 * 锁定用户信息或者清除用户信息
	 */
	public static void lockUserInfo() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (userTable == null || TextUtils.isEmpty(userTable.token)) {
			return;
		}
		if (UserInfoController.getInstance().isSetFingerPrint() || UserInfoController.getInstance().isGesturePwdSet()) {
			UserInfoController.getInstance().setLock(true);
		} else {
			UserInfoController.getInstance().clearUseInfo();
		}
	}


	public static boolean isMoac(String asset) {
		if (TextUtils.isEmpty(asset)) {
			return false;
		}
		if (asset.equals("MOAC")) {
			return true;
		} else {
			return false;
		}
	}

	public static void setCookieInfo(UserInfoResponse.DataBean user) {
		if (user == null) {
			return;
		}
		//TODO:改成对应的url
		String url = "http://staging.atomchain.vip/";

		CookieSyncManager.createInstance(CBRepository.getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		cookieManager.removeAllCookie();
		StringBuilder sbCookie = new StringBuilder();//创建一个拼接cookie的容器,为什么这么拼接，大家查阅一下http头Cookie的结构
		String userStr = new Gson().toJson(user);
		sbCookie.append(String.format("user=%s", userStr));
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		sbCookie.append(String.format(";lang=%s", LanguageHelper.getProcessedCode(localeCode)));
		String cookieValue = sbCookie.toString();

		cookieManager.setCookie(url, cookieValue);//为url设置cookie
		CookieSyncManager.getInstance().sync();//同步cookie
		String newCookie = cookieManager.getCookie(url);
	}

	public static void setCookieInfo(String urlHost) {
		if (TextUtils.isEmpty(urlHost)) {
			return;
		}
		UserInfoTable user = UserInfoController.getInstance().getUserInfo();
		if (user == null) {
			return;
		}
		String userStr = new Gson().toJson(user);

		CookieSyncManager.createInstance(CBRepository.getContext());
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setAcceptCookie(true);
		cookieManager.removeSessionCookie();// 移除
		cookieManager.removeAllCookie();
		StringBuilder sbCookie = new StringBuilder();//创建一个拼接cookie的容器,为什么这么拼接，大家查阅一下http头Cookie的结构

		sbCookie.append(String.format("user=%s", userStr));
//        sbCookie.append(String.format(";lang=%s", CommonUtil.getLocal()));
		String cookieValue = sbCookie.toString();
		cookieManager.setCookie(urlHost, cookieValue);//为url设置cookie

		StringBuilder sbCookie2 = new StringBuilder();
		String cookieValue2 = sbCookie2.append(String.format("lang=%s", LanguageHelper.getSystemLocale().getLanguage())).toString();
		cookieManager.setCookie(urlHost, cookieValue2);//为url设置cookie

		CookieSyncManager.getInstance().sync();//同步cookie
		String newCookie = cookieManager.getCookie(urlHost);
	}


	public static void exitLoginClearData() {
		//OptionManager.logOut();
		UserInfoController.getInstance().clearUseInfo();
		BalanceController.getInstance().clearBalanceDataBase();
		SpUtil.setMarginUserConfig(false);
		UserInvitationController.getInstance().clearData();
		UsereventWebsocket.getInstance().unsubAll();
		UsereventWebsocket.getInstance().unsubScribeAll();
	}


	public static String pathAppendUrl(String pre, String suffix) {
		if (TextUtils.isEmpty(suffix)) {
			return pre;
		}
		if (suffix.contains("http")) {
			return suffix;
		}
		if (suffix.indexOf('/') == 0) {
			return pre + suffix;
		} else {
			return pre + File.separator + suffix;
		}
	}

	public static String urlAppendParam(String baseUrl, String key, Object value) {
		if (TextUtils.isEmpty(key) || value == null) {
			return baseUrl;
		} else {
			StringBuilder urlSb = new StringBuilder(baseUrl);
			if (!baseUrl.contains("?")) {
				urlSb.append("?");
			} else {
				urlSb.append("&");
			}
			urlSb.append(key).append("=").append(value);
			return urlSb.toString();
		}
	}


}
