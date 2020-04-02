package com.coinbene.manbiwang.service.fortune;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.List;

/**
 * Created by june
 * on 2019-10-21
 */
public interface FortuneService extends IProvider {

	void getYbbAssetList(YbbAssetCallBack ybbAssetCallBack);

	void getYbbTotalLeft(String asset, YbbTotalLeftCallBack ybbTotalLeftCallBack);

	interface YbbAssetCallBack {
		void onYbbAssetList(List<String> assetList);
	}

	interface YbbTotalLeftCallBack {
		void onYbbTotalLeft(String totalLeft);
	}
}
