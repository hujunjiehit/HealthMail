package com.coinbene.manbiwang.model.http;

/**
 * ding
 * 2019-12-12
 * com.coinbene.manbiwang.model.http
 */
public class LanguageModel {
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private String language;
	private String code;
	private boolean selected;

}
