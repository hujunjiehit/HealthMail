package com.coinbene.common.utils;

import android.content.Context;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * SiteHelper
 * 2019-12-20
 * @author ding
 */
public final class SiteHelper {
	/**
	 * 备注，这里的主站名字，必须和array里面的主站名字一样；否则，数据对不上号
	 *
	 * @return
	 */
	public static String getSiteByLanguage() {
		Locale locale = LanguageHelper.getSystemLocale();
		String language = locale.getLanguage();
		language = language.toLowerCase();
		String siteName = "";
		if (language.equals("zh")) {
			siteName = "MAIN";
		} else if (language.equals("ko")) {
			siteName = "KO";
		} else if (language.equals("pt")) {
			siteName = "BR";
		} else if (language.equals("vn")) {
			siteName = "VN";
		} else {
			siteName = "MAIN";
		}
		return siteName;
	}

	/**
	 * @return 返回当前站点
	 */
	public static String getCurrentSite() {

		String site = SiteController.getInstance().getSiteName();

		if (!TextUtils.isEmpty(site)) {

			if (site.equals(Constants.SITE_MAIN)) {
				return Constants.SITE_MAIN;
			} else if (site.equals(Constants.SITE_KO)) {
				return Constants.SITE_KO;
			}  else if (site.equals(Constants.SITE_VN)) {
				return Constants.SITE_VN;
			} else {
				return Constants.SITE_BR;
			}

		} else {
			return Constants.SITE_MAIN;
		}
	}


	/**
	 * @return 是否主站点
	 */
	public static boolean isMainSite() {
		return getCurrentSite().equals(Constants.SITE_MAIN);
	}

	/**
	 * @return 是否韩国站点
	 */
	public static boolean isKoSite() {
		return getCurrentSite().equals(Constants.SITE_KO);
	}

	/**
	 * @return 是否越南站
	 */
	public static boolean isVietSite() {
		return getCurrentSite().equals(Constants.SITE_VN);
	}

	/**
	 * @return 是否巴西站点
	 */
	public static boolean isBrSite() {
		return getCurrentSite().equals(Constants.SITE_BR);
	}

	public static List<String> getSupportSiteList(Context context) {
		List<String> result = new ArrayList<>();
		String[] siteArray = context.getResources().getStringArray(R.array.site_list);

		for(String site : siteArray) {
			for (String supportSite : ConfigHelper.getSiteConfig().getSupportSite()) {
				if (site.endsWith(supportSite)) {
					result.add(site);
				}
			}
		}
		return result;
	}
}
