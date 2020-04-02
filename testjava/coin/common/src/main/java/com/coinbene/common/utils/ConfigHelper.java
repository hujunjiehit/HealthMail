package com.coinbene.common.utils;

import com.coinbene.common.BuildConfig;
import com.coinbene.common.config.ProductConfig;

import java.util.List;

/**
 * ding
 * 2019-12-30
 * com.coinbene.common.utils
 * App SAAS 辅助类
 */
public class ConfigHelper {
	private static ProductConfig config;

	public static void setConfig(ProductConfig config) {
		ConfigHelper.config = config;
	}

	public static ProductConfig.LanguageConfigBean getLanguageConfig() {
		return config.getLanguageConfig();
	}

	public static ProductConfig.MultiSiteConfigBean getSiteConfig() {
		return config.getMultiSiteConfig();
	}

	public static ProductConfig.BuglyConfigBean getBuglyConfig() {
		return config.getBuglyConfig();
	}

	public static ProductConfig.BaseUrlConfigBean getUrlConfig(int index) {
		return config.getBaseUrlConfig().get(index);
	}

	public static ProductConfig.SupportOnlineConfig getSupportOnlineConfig() {
		return config.getSupportOnlineConfig();
	}

	public static ProductConfig.ZendeskConfigBean getZendeskConfig() {
		return config.getZendeskConfig();
	}

	/**
	 * @return 夜间模式是否启用
	 */
	public static boolean nightModeEnable() {
		return config.isNightModeEnable();
	}

	public static boolean communityEnable() {
		return config.isCommunityEnable();
	}

	/**
	 * @return 分享功能是否启用
	 */
	public static boolean shareEnable() {
		return config.isShareEnable();
	}

	public static boolean multiLanguageEnable() {
		return getLanguageConfig().isEnable();
	}

	public static List<ProductConfig.SupportLanguage> getSupportLanguage() {
		return getLanguageConfig().getSupportLanguage();
	}


	/**
	 * @return 根据当前环境返回腾讯Bugly的AppKey
	 */
	public static String getBuglyAppKey() {
		ProductConfig.BuglyConfigBean buglyConfig = getBuglyConfig();
		return BuildConfig.ENABLE_DEBUG ? buglyConfig.getTestAppKey() : buglyConfig.getReleaseAppKey();
	}
}
