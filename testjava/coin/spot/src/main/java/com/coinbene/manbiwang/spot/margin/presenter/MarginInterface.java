package com.coinbene.manbiwang.spot.margin.presenter;

import com.coinbene.common.base.BasePresenter;
import com.coinbene.common.base.BaseView;

public interface MarginInterface {


	interface View extends BaseView<MarginInterface.Presenter> {
		void setPresenter(MarginInterface.Presenter presenter);
	}


	interface Presenter extends BasePresenter {
		void setCurSymbol(String symbol);

		void onResume();

		void onPause();

		void createUserMarginConfig();
	}
}
