package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2020-01-06
 * com.coinbene.manbiwang.model.http
 */
public class HomeNavigationModel {
	private int navIcon;
	private String navDec;
	private String link;

	public HomeNavigationModel(int navIcon, String navDec, String link) {
		this.navIcon = navIcon;
		this.navDec = navDec;
		this.link = link;
	}


	public int getNavIcon() {
		return navIcon;
	}

	public void setNavIcon(int navIcon) {
		this.navIcon = navIcon;
	}

	public String getNavDec() {
		return navDec;
	}

	public void setNavDec(String navDec) {
		this.navDec = navDec;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
