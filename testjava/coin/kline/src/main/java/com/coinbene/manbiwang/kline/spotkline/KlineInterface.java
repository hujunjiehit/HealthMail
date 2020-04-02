package com.coinbene.manbiwang.kline.spotkline;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.kline.bean.DataParse;
import com.coinbene.manbiwang.kline.bean.KLineBean;

import java.util.List;

/**
 * Created by june
 * on 2019-11-22
 */
public interface KlineInterface {

	interface View {
		void onKlineDataLoadSuccess(DataParse dataParse,  List<KLineBean> kLineDatas);

		void onQuoteDataReceived(WsMarketData quote);

		void onRiseTypeChanged(int riseType);
	}

	interface Presenter extends BasePresenter {

		void updateSymbol(String symbol);

		void initParams(int timeStatus);

		void loadKlineData();

		List<KLineBean> getkLineDatas();

		void onResume();

		void onPause();
	}
}
