package com.coinbene.manbiwang.fortune.manager;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.http.YbbAssetListModel;
import com.coinbene.manbiwang.model.http.YbbTransferPageInfoModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.manbiwang.service.fortune.FortuneService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by june
 * on 2019-10-17
 */
public class FortuneManager {
	private volatile static FortuneManager instance;

	private List<String> ybbAssetList;

	private FortuneManager(){
	}

	public static FortuneManager getInstance() {
		if (instance == null) {
			synchronized (FortuneManager.class) {
				if (instance == null) {
					instance = new FortuneManager();
				}
			}
		}
		return instance;
	}

	public void getYbbAssetList(CallBack callBack) {
		if (ybbAssetList != null) {
			callBack.onAssetList(ybbAssetList);
		} else {
			getYbbAssetListFromNetwork(callBack);
		}
	}

	public void getYbbTotalLeft(String asset, FortuneService.YbbTotalLeftCallBack callBack) {
		OkGo.<YbbTransferPageInfoModel>get(Constants.YBB_TRANSFER_IN_PAGEINFO).params("asset", asset).tag(this).execute(new NewJsonSubCallBack<YbbTransferPageInfoModel>() {
			@Override
			public void onSuc(Response<YbbTransferPageInfoModel> response) {
				if (response.body() != null && response.body().getData() != null) {
					callBack.onYbbTotalLeft(response.body().getData().getUserTotalLeft());
				} else {
					callBack.onYbbTotalLeft("-1");
				}
			}

			@Override
			public void onE(Response<YbbTransferPageInfoModel> response) {
				callBack.onYbbTotalLeft("-1");
			}
		});
	}

	private void getYbbAssetListFromNetwork(CallBack callBack) {
		OkGo.<YbbAssetListModel>get(Constants.YBB_ASSET_LIST).tag(this).execute(new NewJsonSubCallBack<YbbAssetListModel>() {
			@Override
			public void onSuc(Response<YbbAssetListModel> response) {
				if (response != null && response.body() != null && response.body().getData() != null) {
					if (ybbAssetList == null) {
						ybbAssetList = new ArrayList<>();
					}
					ybbAssetList.clear();

					for(YbbAssetListModel.DataBean dataBean : response.body().getData()) {
						ybbAssetList.add(dataBean.getAsset());
					}
					callBack.onAssetList(ybbAssetList);
				}
			}

			@Override
			public void onE(Response<YbbAssetListModel> response) {

			}
		});
	}

	public interface CallBack {
		void onAssetList(List<String> assetList);
	}
}
