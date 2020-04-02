package com.coinbene.manbiwang.kline.fragment.klineinterface;

/**
 * Created by june
 * on 2019-11-20
 */
public interface ActivityInterface {

//	void onReceiveQuoteData(BookOrderRes.DataBean.QuoteBean quote);
//
//	void onRiseTypeChanged(int riseType);
//
//	String getTradePaiName();

	void registerActivityListener(IActivityListener listener);

	interface IActivityListener {

		void onTradePairChanged(String tradePairName);
	}
}
