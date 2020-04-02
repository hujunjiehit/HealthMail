package com.coinbene.manbiwang.kline.newspotkline;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.websocket.model.WsMarketData;
import com.github.fujianlian.klinechart.KLineEntity;

import java.util.List;

/**
 * Created by june
 * on 2019-11-22
 */
public interface NewKlineInterface {

	interface View {
		void onKlineData(List<KLineEntity> data, boolean isFull);

		void onTickerData(WsMarketData tickerData);

		void showLoading();

		void hideLoading();
	}

	interface Presenter extends BasePresenter {
		void updateTimeType(String timeType);

		void onResume();

		void onPause();

		void changeTime(String timeType);

		void changeSymbol(String symbol);
	}
}
