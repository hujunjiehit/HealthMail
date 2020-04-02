package com.coinbene.manbiwang.kline.spotkline.listener;

/**
 * Created by june
 * on 2019-11-22
 */
public interface ZhibiaoListener {

	int MASTER_TYPE_HIDE = 0;
	int MASTER_TYPE_MA = 1;
	int MASTER_TYPE_BOLL = 2;

	String MACD = "MACD";
	String KDJ = "KDJ";
	String RSI = "RSI";
	String SUB2_HIDE = "SUB2_HIDE";

	void onMasterSelected(int masterType);

	void onSub2Selected(String type);
}
