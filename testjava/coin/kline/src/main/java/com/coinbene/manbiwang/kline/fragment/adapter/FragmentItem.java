package com.coinbene.manbiwang.kline.fragment.adapter;

/**
 * Created by june
 * on 2019-11-20
 */
public class FragmentItem {

	private FragmentType type;
	private String title;

	public FragmentItem(FragmentType type, String title) {
		this.type = type;
		this.title = title;
	}


	public FragmentType getType() {
		return type;
	}

	public void setType(FragmentType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
