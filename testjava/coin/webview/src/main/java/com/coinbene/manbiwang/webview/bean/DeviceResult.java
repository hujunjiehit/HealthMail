package com.coinbene.manbiwang.webview.bean;

/**
 * Created by june
 * on 2019-07-19
 */
public class DeviceResult {

	private String appVersion;
	private String currentLanguage;
	private String currentSite;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getCurrentLanguage() {
		return currentLanguage;
	}

	public void setCurrentLanguage(String currentLanguage) {
		this.currentLanguage = currentLanguage;
	}

	public String getCurrentSite() {
		return currentSite;
	}

	public void setCurrentSite(String currentSite) {
		this.currentSite = currentSite;
	}
}
