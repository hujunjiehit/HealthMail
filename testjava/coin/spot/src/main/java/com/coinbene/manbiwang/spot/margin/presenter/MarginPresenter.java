package com.coinbene.manbiwang.spot.margin.presenter;

import android.app.Activity;

import com.coinbene.common.Constants;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * 杠杆的presenter
 */
public class MarginPresenter implements MarginInterface.Presenter {
	private String curSymbol;
	private MarginInterface.View marginView;
	private Activity mContext;

	public MarginPresenter(MarginInterface.View marginView, Activity context) {
		this.marginView = marginView;
		this.mContext = context;
	}

	@Override
	public void setCurSymbol(String symbol) {
		this.curSymbol = symbol;
	}

	@Override
	public void onResume() {

	}


	@Override
	public void onPause() {
	}

	@Override
	public void createUserMarginConfig() {
		OkGo.<BaseRes>post(Constants.CREATE_MARGIN_USER_CONFIG).params("flag", "1").execute(new NewJsonSubCallBack<BaseRes>() {

			@Override
			public void onSuc(Response<BaseRes> response) {
			}

			@Override
			public void onE(Response<BaseRes> response) {
			}
		});
	}

	@Override
	public void onDestory() {

	}

}
