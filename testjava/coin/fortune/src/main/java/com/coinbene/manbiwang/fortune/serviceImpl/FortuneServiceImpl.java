package com.coinbene.manbiwang.fortune.serviceImpl;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.manbiwang.fortune.manager.FortuneManager;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.fortune.FortuneService;

/**
 * Created by june
 * on 2019-10-21
 */
@Route(path = RouteHub.Fortune.fortuneService)
public class FortuneServiceImpl implements FortuneService {

	/**
	 * 获取余币宝支持的资产列表
	 * @param ybbAssetCallBack
	 */
	@Override
	public void getYbbAssetList(YbbAssetCallBack ybbAssetCallBack) {
		FortuneManager.getInstance().getYbbAssetList(assetList -> ybbAssetCallBack.onYbbAssetList(assetList));
	}

	/**
	 * 获取余币宝可转余额
	 * @param asset
	 * @param ybbTotalLeftCallBack
	 */
	@Override
	public void getYbbTotalLeft(String asset, YbbTotalLeftCallBack ybbTotalLeftCallBack) {
		FortuneManager.getInstance().getYbbTotalLeft(asset, ybbTotalLeftCallBack);
	}

	@Override
	public void init(Context context) {

	}

}
