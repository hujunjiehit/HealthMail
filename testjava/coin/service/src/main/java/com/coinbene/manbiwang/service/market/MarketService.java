package com.coinbene.manbiwang.service.market;

import android.app.Activity;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

/**
 * Created by june
 * on 2019-10-11
 *
 * 行情对外提供的接口
 */
public interface MarketService extends IProvider {

	Fragment getMarketFargment();

	/**
	 * 添加或者删除自选
	 *
	 * @param tradePair
	 */
	void addOrDeleteOptional(String tradePair, CallBack callBack);

	/**
	 * 批量编辑自选
	 * @param tradePairList
	 * @param callBack
	 */
	void editOptionalBatch(Activity activity, List<String> tradePairList, CallBack callBack);


	/**
	 * 添加或者删除自选的回调
	 */
	interface CallBack {
		void onSuccess();

		void onFailed();
	}
}
