/*
 * Copyright 2017 GcsSloop
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Last modified 2017-03-16 00:27:18
 *
 * GitHub:  https://github.com/GcsSloop
 * Website: http://www.gcssloop.com
 * Weibo:   http://weibo.com/GcsSloop
 */

package com.coinbene.common.utils;

import android.net.Uri;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.router.UIRouter;
import com.coinbene.manbiwang.service.app.TabType;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.HttpUrl;

/**
 * UrlUtil
 */
public class UrlUtil {
	/**
	 * Url添加参数
	 * @param url
	 * @param key
	 * @param value
	 * @return
	 */
	public static String appendParams(String url, String key, String value) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(url);
		if (url.contains("?")) {
			stringBuilder.append("&").append(key).append("=").append(value);
		} else {
			stringBuilder.append("?").append(key).append("=").append(value);
		}
		return stringBuilder.toString();
	}

	public static boolean isUrlPrefix(String url) {
		return url.startsWith("http://") || url.startsWith("https://");
	}

	/**
	 * 判断后缀是不是图片类型的
	 *
	 * @param url url
	 */
	public static boolean isImageSuffix(String url) {
		return url.endsWith(".png")
				|| url.endsWith(".PNG")
				|| url.endsWith(".jpg")
				|| url.endsWith(".JPG")
				|| url.endsWith(".jpeg")
				|| url.endsWith(".JPEG");
	}

	/**
	 * 判断后缀是不是 GIF
	 *
	 * @param url url
	 */
	public static boolean isGifSuffix(String url) {
		return url.endsWith(".gif")
				|| url.endsWith(".GIF");
	}

	/**
	 * 获取后缀名
	 */
	public static String getSuffix(String url) {
		if ((url != null) && (url.length() > 0)) {
			int dot = url.lastIndexOf('.');
			if ((dot > -1) && (dot < (url.length() - 1))) {
				return url.substring(dot + 1);
			}
		}
		return url;
	}

	/**
	 * 获取 mimeType
	 */
	public static String getMimeType(String url) {
		if (url.endsWith(".png") || url.endsWith(".PNG")) {
			return "data:image/png;base64,";
		} else if (url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".JPG") || url.endsWith(".JPEG")) {
			return "data:image/jpg;base64,";
		} else if (url.endsWith(".gif") || url.endsWith(".GIF")) {
			return "data:image/gif;base64,";
		} else {
			return "";
		}
	}

	/**
	 * 根据 url 获取 host name
	 * http://www.gcssloop.com/ => www.gcssloop.com
	 */
	public static String getHost(String url) {
		if (url == null || url.trim().equals("")) {
			return "";
		}
		String host = "";
		Pattern p = Pattern.compile("(?<=//|)((\\w)+\\.)+\\w+");
		Matcher matcher = p.matcher(url);
		if (matcher.find()) {
			host = matcher.group();
		}
		return host;
	}

	public static HashMap<String, String> parseParams(Uri uri) {
		if (uri == null) {
			return new HashMap<String, String>();
		}
		//uri.toString().re
		HashMap<String, String> temp = new HashMap<String, String>();
		Set<String> keys = getQueryParameterNames(uri);
		for (String key : keys) {
			temp.put(key, uri.getQueryParameter(key));
		}
		return temp;
	}

	public static HashMap<String, String> parseParams(Uri uri, HashMap<String, String> map) {
		Set<String> keys = getQueryParameterNames(uri);
		for (String key : keys) {
			map.put(key, uri.getQueryParameter(key));
		}
		return map;
	}


	public static Set<String> getQueryParameterNames(Uri uri) {
		String query = uri.getEncodedQuery();
		if (query == null) {
			return Collections.emptySet();
		}
		Set<String> names = new LinkedHashSet<String>();
		int start = 0;
		do {
			int next = query.indexOf('&', start);
			int end = (next == -1) ? query.length() : next;

			int separator = query.indexOf('=', start);
			if (separator > end || separator == -1) {
				separator = end;
			}
			String name = query.substring(start, separator);
			try {
				names.add(URLDecoder.decode(name, "UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			start = end + 1;
		} while (start < query.length());

		return Collections.unmodifiableSet(names);
	}

	public static String parseUrl(String linkUrl) {
		String finalTarget;
		if (TextUtils.isEmpty(linkUrl)) {
			finalTarget = "coinbene" + "://" + "no_action";
		} else if (linkUrl.toLowerCase().startsWith("http://") || linkUrl.toLowerCase().startsWith("https://")) {
			finalTarget = linkUrl;
		} else if (linkUrl.toLowerCase().startsWith("coinbene")) {
			finalTarget = linkUrl;
		} else {
			finalTarget = CommonUtil.pathAppendUrl(Constants.BASE_URL_H5, linkUrl);
		}
		return finalTarget;
	}

	public static String getInviteUrl() {
		StringBuilder url = new StringBuilder(Constants.BASE_URL_H5 + "/appInvite.html#/invite");
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		String site = SpUtil.get(CBRepository.getContext(), SpUtil.PRE_SITE_SELECTED, "");
		url.append("?lang=" + LanguageHelper.getProcessedCode(localeCode));
		url.append("&site=" + site);

		if (CommonUtil.isLoginAndUnLocked()) {
			int userId = UserInfoController.getInstance().getUserInfo().userId;
			String token = UserInfoController.getInstance().getUserInfo().token;
			url.append("&userId=" + userId);
			url.append("&token=" + token);
		}
		return url.toString();
	}

	public static String getContractRankingUrl() {
		StringBuilder link = new StringBuilder(Constants.BASE_URL_H5 + "/contract/h5/activity.html?title=jiaoyipaimingsai");
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		link.append("&lang=" + LanguageHelper.getProcessedCode(localeCode));
		return link.toString();
	}

	/**
	 * 获取BTC合约指南url
	 *
	 * @return
	 */
	public static String getBtcContractGuideUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getBtcContractGuide(), LanguageHelper.getZendeskLanguage());
	}

	/**
	 * 获取USDT合约指南url
	 *
	 * @return
	 */
	public static String getUsdtContractGuideUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getUsdtContractGuide(), LanguageHelper.getZendeskLanguage());
	}

	/**
	 * 获取帮助中心url
	 *
	 * @return
	 */
	public static String getHelpCenterUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getHelpCenter(), LanguageHelper.getZendeskLanguage());
	}


	public static String getAboutUsUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getAboutUs(), LanguageHelper.getZendeskLanguage());
	}

	/**
	 * 余币宝指南url
	 *
	 * @return
	 */
	public static String getYbbGuideUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getYbbGuide(), LanguageHelper.getZendeskLanguage());
	}

	/**
	 * 杠杆指南url
	 *
	 * @return
	 */
	public static String getMarginGuideUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getMarginGuide(), LanguageHelper.getZendeskLanguage());
	}

	/**
	 * 杠杆用户使用协议url
	 *
	 * @return
	 */
	public static String getMarginUserProtocol() {
		return String.format(ConfigHelper.getZendeskConfig().getMarginUserProtocol(), LanguageHelper.getZendeskLanguage());
	}


//	/**
//	 * 获取合约挖矿url
//	 *
//	 * @return
//	 */
//	public static String getContractMiningUrl() {
//		StringBuilder link = new StringBuilder();
//		link.append(Constants.BASE_URL_H5);
//		link.append("/loading.html?redirect_url=%2Factivity%2Fmining&min_version=2.4.1&auth=false&replace=true");
//		return link.toString();
//	}


	/**
	 * url 添加站点和语言
	 *
	 * @param mLoadUrl
	 * @return
	 */
	public static String appendSiteAndLanguage(String mLoadUrl) {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(mLoadUrl);
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		HashMap<String, String> paramsMap = parseParams(Uri.parse(mLoadUrl.replace("#", "")));
		if (paramsMap.get("lang") == null) {
			if (!urlBuilder.toString().contains("?")) {
				urlBuilder.append("?");
			} else {
				urlBuilder.append("&");
			}
			urlBuilder.append("lang").append("=").append(LanguageHelper.getProcessedCode(localeCode));
		}
		if (paramsMap.get("site") == null) {
			if (!urlBuilder.toString().contains("?")) {
				urlBuilder.append("?");
			} else {
				urlBuilder.append("&");
			}
			urlBuilder.append("site").append("=").append(SiteController.getInstance().getSiteName());
		}
		return urlBuilder.toString();
	}

	//公告列表， %s为语言，目前支持 en-us ja  ko-kr  zh-cn
	public static String getZendeskNoticeUrl() {
		String siteName = SiteController.getInstance().getSiteName();
		for (ProductConfig.ZendeskConfigBean.NoticeConfigBean noticeConfig : ConfigHelper.getZendeskConfig().getNoticeConfig()) {
			if (siteName.equals(noticeConfig.getSiteName())) {
				return String.format(noticeConfig.getBase() + noticeConfig.getNoticeList(), LanguageHelper.getZendeskLanguage());
			}
		}
		return "";
	}

	public static String getZendeskNoticeCenterUrl() {
		String siteName = SiteController.getInstance().getSiteName();
		for (ProductConfig.ZendeskConfigBean.NoticeConfigBean noticeConfig : ConfigHelper.getZendeskConfig().getNoticeConfig()) {
			if (siteName.equals(noticeConfig.getSiteName())) {
				return String.format(noticeConfig.getBase() + noticeConfig.getNoticeCenter(), LanguageHelper.getZendeskLanguage());
			}
		}
		return "";
	}


	/**
	 * 得到策略委托url
	 *
	 * @return
	 */
	public static String getZendeskSpotOrderTypeUrl() {
		return String.format(ConfigHelper.getZendeskConfig().getSpotStrategyOrder(), LanguageHelper.getZendeskLanguage());
	}

	public static String getCoinbeneUrl(String host) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("coinbene://");
		stringBuilder.append(host);
		return stringBuilder.toString();
	}


	/**
	 * 得到appconfig接口 url  每个环境对应不一样
	 *
	 * @return
	 */
	public static String getAppConfigUrl() {
		StringBuilder builder = new StringBuilder();
		builder.append(Constants.BASE_IMG_URL);
		builder.append("APPCONFIG_");
		builder.append(SiteHelper.getCurrentSite());

		String url = "";
		switch (CBRepository.getCurrentEnvironment().environmentType) {
			case Constants.DEV_ENVIROMENT:
				url = "_ANDROID_DEV.json";
				break;
			case Constants.TEST_ENVIROMENT:
				url = "_ANDROID_TEST.json";
//				url = "_ANDROID_DEV.json";
				break;
			case Constants.STAGING_ENVIROMENT:
				url = "_ANDROID_STAGING.json";
				break;
			case Constants.ONLINE_ENVIROMENT:
				url = "_ANDROID_PROD.json";
				//url = "_ANDROID_TEST.json";
				break;
		}
		return builder.append(url).append("?t=" + System.currentTimeMillis()).toString();
	}


	public static String getUsdtProtocol() {
		return String.format(ConfigHelper.getZendeskConfig().getUsdtContractProtocol(), LanguageHelper.getZendeskLanguage());
	}

	public static String getBtcProtocol() {
		return String.format(ConfigHelper.getZendeskConfig().getBtcContractProtocol(), LanguageHelper.getZendeskLanguage());
	}

	public static String getChangeTabUrl(TabType tabType) {
		return getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB) + "?tab=" + getTabName(tabType);
	}

	public static String getTabName(TabType tabType) {
		switch (tabType) {
			case HOME:
				return Constants.TAB_HOME;
			case MARKET:
				return Constants.TAB_MARKET;
			case SPOT:
				return Constants.TAB_SPOT;
			case CONTRACT:
				return Constants.TAB_CONTRACT;
			case GAME:
				return Constants.TAB_GAME;
			case BALANCE:
				return Constants.TAB_BALANCE;

		}
		return "";
	}

	public static TabType getTabType(String tabName) {
		switch (tabName) {
			case Constants.TAB_HOME:
				return TabType.HOME;
			case Constants.TAB_MARKET:
				return TabType.MARKET;
			case Constants.TAB_CONTRACT:
				return TabType.CONTRACT;
			case Constants.TAB_SPOT:
				return TabType.SPOT;
			case Constants.TAB_GAME:
				return TabType.GAME;
			case Constants.TAB_BALANCE:
				return TabType.BALANCE;
		}
		return TabType.MARKET;
	}

	/**
	 * H5 URL 替换
	 * @param url
	 * @return
	 */
	public static String replaceH5Url(String url) {
		if (CBRepository.getCurrentEnvironment().environmentType != Constants.ONLINE_ENVIROMENT) {
			//不是线上环境，不需要替换
			DLog.e("h5Url测试","不是线上环境，不需要替换 ====>");
			return url;
		}

		if (SpUtil.getAppConfig() == null ||  SpUtil.getAppConfig().getUrl_config() == null || TextUtils.isEmpty(SpUtil.getAppConfig().getUrl_config().getH5_url())) {
			//没有配置可以替换的h5 url不用替换
			DLog.e("h5Url测试","没有配置可以替换的h5 url不用替换 ====>");
			return url;
		}

		HttpUrl httpUrl = HttpUrl.parse(url);
		if (!SpUtil.getCurrentH5Url().contains(httpUrl.host())) {
			//不是h5_url的请求不需要处理
			DLog.e("h5Url测试","不是h5_url的请求不需要处理 ====>");
			return url;
		}

		HttpUrl configUrl = HttpUrl.parse(SpUtil.getAppConfig().getUrl_config().getH5_url());
		if (httpUrl.host().equals(configUrl.host()) && httpUrl.scheme().equals(configUrl.scheme())) {
			//不需要替换
			DLog.e("h5Url测试","和配置的域名一致不需要替换 ====>");
			return url;
		} else {
			//需要替换
			String resultUrl = url.replace(httpUrl.scheme() + "://" + httpUrl.host(), SpUtil.getAppConfig().getUrl_config().getH5_url());
			DLog.e("h5Url测试","替换后的域名为： ====>" + resultUrl);
			return resultUrl;
		}
	}
}
