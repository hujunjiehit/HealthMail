package com.coinbene.common.utils;

import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;

/**
 * 语言站点  单例
 */
public class SiteController {
	private static SiteController siteCtrl;
	private String siteName;

	private SiteController() {
		initSite();
	}

	private void initSite() {
		siteName = SpUtil.get(CBRepository.getContext(), SpUtil.PRE_SITE_SELECTED, "");
		//如果数据不存在，根据本地的语言，第一次初始化数据
		if (TextUtils.isEmpty(siteName)) {
			if (ConfigHelper.getSiteConfig().isEnable()) {
				//如果支持多个站点，初始站点根据本地语言设置
				siteName = SiteHelper.getSiteByLanguage();
			} else {
				//如果不支持多个站点，默认站点为列表第一个站点
				if (ConfigHelper.getSiteConfig().getSupportSite() == null || ConfigHelper.getSiteConfig().getSupportSite().size() == 0) {
					throw new IllegalStateException("请指定默认的站点");
				} else {
					siteName = ConfigHelper.getSiteConfig().getSupportSite().get(0);
				}
			}
			SpUtil.put(CBRepository.getContext(), SpUtil.PRE_SITE_SELECTED, siteName);
		}
	}

	public static SiteController getInstance() {
		if (siteCtrl == null) {
			synchronized (SiteController.class) {
				if (siteCtrl == null) {
					siteCtrl = new SiteController();
				}
			}
		}
		return siteCtrl;
	}


	public String getSiteName() {
		if (TextUtils.isEmpty(siteName)) {
			initSite();
		}
		return siteName;
	}

	/**
	 * 更新站点
	 *
	 * @param siteCode
	 */
	public void updateSiteName(String siteCode) {
		SpUtil.put(CBRepository.getContext(), SpUtil.PRE_SITE_SELECTED, siteCode);
		siteName = siteCode;
	}


	/**
	 * 是否是韩国站 或者巴西站
	 *
	 * @return
	 */
	public boolean isBr_KOSite() {
		String site = getSiteName();
		if (site.equals(Constants.SITE_BR) || site.equals(Constants.SITE_KO)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否是巴西站
	 *
	 * @return
	 */
	public boolean isBrSite() {
		String site = getSiteName();
		if (TextUtils.isEmpty(site)) {
			return false;
		}
		if (site.equals(Constants.SITE_BR)) {
			return true;
		}
		return false;
	}


	/**
	 * 是否是主站
	 *
	 * @return
	 */
	public boolean isMainSite() {
		String site = getSiteName();
		if (TextUtils.isEmpty(site)) {
			return false;
		}
		if (site.equals(Constants.SITE_MAIN)
				|| (ConfigHelper.getSiteConfig().getSupportSite() != null && ConfigHelper.getSiteConfig().getSupportSite().size() == 1)) {
			//主站或者只支持一个站点的情况下，都返回true
			return true;
		}
		return false;
	}
}
