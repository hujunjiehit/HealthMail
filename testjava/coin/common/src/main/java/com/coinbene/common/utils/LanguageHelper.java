package com.coinbene.common.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * ding
 * 2019-12-11
 * com.coinbene.common.utils
 */
public final class LanguageHelper {
	public static String TAG = "LanguageHelper";
	private static String SP_NAME = "language_setting";
	private static String SP_LANGUAGE_CODE = "language_code";

	//ZenDesk语言Map
	private static Map<String, String> zendeskLanguageMap;

	public static Context setLocale(Context context) {
		return setLocale(context, getLocaleCode(context));
	}

	/**
	 * 设置Locale
	 */
	public static Context setLocale(Context context, String localeCode) {
		//生成Locale并设置
		Locale locale = generateLocale(localeCode);
		Locale.setDefault(locale);
		Configuration config = context.getResources().getConfiguration();

		//系统版本大于7.0创建Context，否则使用旧版方式更新资源
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			config.setLocale(locale);
		} else {
			config.locale = locale;
		}
		DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
		context.getResources().updateConfiguration(config, displayMetrics);

		//保存选择的语言到本地
		setLocaleCode(context, locale);

		return context;
	}


	public static void setBaseLocal(Context context){
		setLocale(context,getLocaleCode(context));
	}

	/**
	 * 生成Locale
	 */
	private static Locale generateLocale(String localeCode) {
		//localeCode不存在返回系统Locale
		if (TextUtils.isEmpty(localeCode)) {
			return getSystemLocale();
		}

		//解析语言代码创建Locale
		String[] split = localeCode.split("_");
		String language = split[0];
		String country = split[1];

		return new Locale(language, country);
	}

	/**
	 * 更新App应用程序设置（更新语言后App也要更新一下否则使用AppContext调用资源时会有问题）
	 */
	public static void updateAppConfig(Context context, String localeCode) {
		Resources resources = context.getApplicationContext().getResources();
		DisplayMetrics dm = resources.getDisplayMetrics();
		Configuration config = resources.getConfiguration();
		Locale locale = generateLocale(localeCode);
		config.locale = locale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			LocaleList localeList = new LocaleList(locale);
			LocaleList.setDefault(localeList);
			config.setLocales(localeList);
			context.getApplicationContext().createConfigurationContext(config);
			Locale.setDefault(locale);
		}
		resources.updateConfiguration(config, dm);
	}


	/**
	 * @return 获取系统Locale
	 */
	public static Locale getSystemLocale() {
		Locale locale;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			locale = LocaleList.getDefault().get(0);
		} else {
			locale = Locale.getDefault();
		}

		//有的手机会出现类似zh_BR的问题，因此这里需要将国家写死

		String language = locale.getLanguage().toLowerCase();
		if (language.equals("zh")) {
			return new Locale(locale.getLanguage(),"CN");
		} else if (language.equals("en")) {
			return new Locale(locale.getLanguage(),"US");
		} else if (language.equals("ko")) {
			return new Locale(locale.getLanguage(),"KR");
		} else if (language.equals("pt")) {
			return new Locale(locale.getLanguage(),"BR");
		} else if (language.equals("ja")) {
			return new Locale(locale.getLanguage(),"JP");
		} else if (language.equals("vi") || language.equals("vn")) {
			return new Locale(locale.getLanguage(),"VN");
		}
//		else if (language.equals("ru")) {
//			lc = "ru_RU";
//		}
		else {
			return new Locale("en","US");
		}
	}


	/**
	 * 保存语言代码
	 */
	private static void setLocaleCode(Context context, Locale locale) {

		//禁止每次进入页面都被调用此方法
		if (getLocaleCode(context).contains(locale.getLanguage())) {
			return;
		}

		//根据locale的参数获取code
		String code;
		if (TextUtils.isEmpty(locale.getCountry())) {
			code = locale.getLanguage();
		} else {
			code = String.format("%s_%s", locale.getLanguage(), locale.getCountry());
		}

		SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit().putString(SP_LANGUAGE_CODE, code);
		editor.apply();
	}

	/**
	 * @return 语言代码
	 */
	public static String getLocaleCode(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		return preferences.getString(SP_LANGUAGE_CODE, "");
	}

	/**
	 * @return 获取语言名称
	 */
	public static String getDisplayName() {
		//获取当前支持语言列表，获取当前Locale
		List<ProductConfig.SupportLanguage> list = ConfigHelper.getSupportLanguage();
//		Locale locale = getSystemLocale();
		String localLang = getLocaleCode(CBRepository.getContext());
//		DLog.d(TAG, "getDisplayName() : " + locale.getDisplayName());

		//遍历判断是否匹配，返回当前匹配语言
		for (ProductConfig.SupportLanguage language : list) {
			if (language.getCode().contains(localLang)) {
				return language.getName();
			}
		}

		//返回默认英语
		return "English";
	}

	/**
	 * 处理语言代码（一般用于传值给后端）
	 */
	public static String getProcessedCode(String localeCode) {
		if (TextUtils.isEmpty(localeCode)) {
			Locale systemLocale = getSystemLocale();
			localeCode = systemLocale.getLanguage();
		}

		if (localeCode.toLowerCase().contains("ja")) {
			localeCode = "ja";
		} else if (localeCode.toLowerCase().contains("kr")) {
			localeCode = "ko";
		} else if (localeCode.toLowerCase().contains("vi")) {
			localeCode = "vi";
		}

		return localeCode;
	}

	/**
	 * 处理语言代码（传值给后端拿汇率用）
	 * {"ko":["7282.4700","₩"],"en_US":["6.5000","$"],"zh_CN":["43.3960","￥"],"pt_BR":["24.3627","R$"]}}}
	 */
	public static String getCurrencyExchangeRateCode() {
		String localeCode = getLocaleCode(CBRepository.getContext());

		if (TextUtils.isEmpty(localeCode)) {
			Locale locale = getSystemLocale();
			localeCode = locale.getLanguage();
		}

		if (localeCode.toLowerCase().contains("ja")) {
			localeCode = "ja";
		} else if (localeCode.toLowerCase().contains("kr")) {
			localeCode = "ko";
		} else if (localeCode.toLowerCase().contains("vi")) {
			localeCode = "vi";
		}

		return localeCode;
	}

	/**
	 * @return 获取Zendesk语言代码
	 */
	public static String getZendeskLanguage() {

		if (zendeskLanguageMap == null) {
			zendeskLanguageMap = new HashMap<>();
			zendeskLanguageMap.put("zh_CN", "zh-cn");
			zendeskLanguageMap.put("en_US", "en-us");
			zendeskLanguageMap.put("ko_KR", "ko-kr");
			zendeskLanguageMap.put("ja_JP", "ja");
			zendeskLanguageMap.put("pt_BR", "en-us");
			zendeskLanguageMap.put("ru_RU", "en-us");
			zendeskLanguageMap.put("vi_VN", "en-us");
		}

		String key = getLocaleCode(CBRepository.getContext());
		String value = zendeskLanguageMap.get(key);

		return TextUtils.isEmpty(value) ? "en-us" : value;
	}

	/**
	 * @return 获取货币String
	 */
	public static String getCurrencyString() {
		if (LanguageHelper.isChinese(CBRepository.getContext())) {
			return "CNY";
		} else if (LanguageHelper.isPortuguese(CBRepository.getContext())) {
			return "BRL";
		} else if (LanguageHelper.isJapanese(CBRepository.getContext())) {
			return "JPY";
		} else if (LanguageHelper.isVietnamese(CBRepository.getContext())) {
			return "VND";
		} else {
			return "CNY";
		}
	}


	/**
	 * @return 当前语言环境是否受支持
	 */
	public static boolean isSupport() {
		List<ProductConfig.SupportLanguage> list = ConfigHelper.getSupportLanguage();
		Locale locale = getSystemLocale();
		//遍历语言列表判断是否支持
		for (ProductConfig.SupportLanguage language : list) {
			if (language.getCode().contains(locale.getLanguage())) {
				return true;
			}
		}

		return false;
	}


	public static boolean isChinese(Context context) {
		//简体中文
		String LANGUAGE_CHINESE = "zh_CN";
		return LANGUAGE_CHINESE.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isJapanese(Context context) {
		//日本语
		String LANGUAGE_JAPANESE = "ja_JP";
		return LANGUAGE_JAPANESE.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isVietnamese(Context context) {
		//越南语
		String LANGUAGE_VIETNAMESE = "vi_VN";
		return LANGUAGE_VIETNAMESE.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isRussian(Context context) {
		//俄语
		String LANGUAGE_RUSSIAN = "ru_RU";
		return LANGUAGE_RUSSIAN.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isEnglish(Context context) {
		//美式英文
		String LANGUAGE_ENGLISH = "en_US";
		return LANGUAGE_ENGLISH.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isPortuguese(Context context) {
		//葡萄牙语
		String LANGUAGE_PORTUGUESE = "pt_BR";
		return LANGUAGE_PORTUGUESE.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}

	public static boolean isKorean(Context context) {
		//韩语
		String LANGUAGE_KOREAN = "ko_KR";
		return LANGUAGE_KOREAN.toUpperCase().equals(getLocaleCode(context).toUpperCase());
	}
}
