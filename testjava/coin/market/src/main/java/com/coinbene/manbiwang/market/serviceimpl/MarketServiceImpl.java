package com.coinbene.manbiwang.market.serviceimpl;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairInfoTable;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ListUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.market.MarketFragment;
import com.coinbene.manbiwang.market.R;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.market.MarketService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * Created by june
 * on 2019-10-11
 */
@Route(path = RouteHub.Market.marketService)
public class MarketServiceImpl implements MarketService {

	@Override
	public Fragment getMarketFargment() {
		return new MarketFragment();
	}

	/**
	 * 添加或者删除自选
	 * @param tradePair
	 * @param callBack
	 */
	@Override
	public void addOrDeleteOptional(String tradePair, CallBack callBack) {
		if (tradePair.contains("/")){
			tradePair = tradePair.replace("/", "");
		}
		TradePairInfoTable item = TradePairInfoController.getInstance().queryDataByTradePair(tradePair);
		if(item == null || TextUtils.isEmpty(item.tradePairName)) {
			return;
		}
		boolean isOptional = TradePairOptionalController.getInstance().isOptionalTradePair(item.tradePairName);

		if (!CommonUtil.isLogin()) {
			//未登陆状态下加自选
			if (isOptional) {
				TradePairOptionalController.getInstance().deleteOptionalTradePair(item.tradePair);
				ToastUtil.show(R.string.successfully_deleted);
			} else {
				TradePairOptionalController.getInstance().addOneOptionalTradePair(item.tradePair, item.tradePairName);
				ToastUtil.show(R.string.successfully_added);
			}
			if (callBack != null) {
				callBack.onSuccess();
			}
		} else {
			//已登陆状态下 或者 锁定状态下 加自选
			favoriteToggle(item, isOptional ? 2 : 1, callBack);
		}
	}

	/**
	 * 批量编辑自选
	 * @param activity
	 * @param tradePairList
	 * @param callBack
	 */
	@Override
	public void editOptionalBatch(Activity activity, List<String> tradePairList, CallBack callBack) {
		if (!CommonUtil.isLogin()) {
			TradePairOptionalController.getInstance().notifyOptionTables(tradePairList);
			if (callBack != null) {
				callBack.onSuccess();
			}
		} else {
			String tradePairs = ListUtils.listToString(tradePairList);
			OkGo.<BaseRes>post(Constants.FAVORITE_EDIT).params("tradePairs", tradePairs).tag(this).execute(new DialogCallback<BaseRes>(activity) {

				@Override
				public void onSuc(Response<BaseRes> response) {
					TradePairOptionalController.getInstance().notifyOptionTables(tradePairList);
					if (callBack != null) {
						callBack.onSuccess();
					}
				}

				@Override
				public void onE(Response<BaseRes> response) {
					if (callBack != null) {
						callBack.onFailed();
					}
				}

			});
		}
	}


	//添加或者删除自选交易对   type  1添加自选   2删除自选
	private void favoriteToggle(TradePairInfoTable item, int type, CallBack callBack) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("tradePair", item.tradePair);
		httpParams.put("type", type);
		OkGo.<BaseRes>post(Constants.GET_FAVORITE_TOGGLE).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				if (type == 1) {
					TradePairOptionalController.getInstance().addOneOptionalTradePair(item.tradePair, item.tradePairName);
					ToastUtil.show(R.string.successfully_added);
				} else {
					TradePairOptionalController.getInstance().deleteOptionalTradePair(item.tradePair);
					ToastUtil.show(R.string.successfully_deleted);
				}
				if (callBack != null) {
					callBack.onSuccess();
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {
				if (callBack != null) {
					callBack.onFailed();
				}
			}
		});
	}

	@Override
	public void init(Context context) {

	}
}
