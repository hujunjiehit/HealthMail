package com.coinbene.manbiwang.market.presenter;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.database.ContractConfigController;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractUsdtConfigController;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairGroupTable;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.market.manager.MarketDataManager;
import com.coinbene.manbiwang.market.presenter.impl.MarketInterface;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.ContractListModel;
import com.coinbene.manbiwang.model.http.LeverSymbolListModel;
import com.coinbene.manbiwang.model.http.TradePairMarketRes;
import com.coinbene.manbiwang.model.http.TradePairResponse;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by KG on 2017/8/2.
 */
public class MarketPresenter implements MarketInterface.Presenter {

	private MarketInterface.View mMarketFragment;

	private HashMap<String, TradePairMarketRes.DataBean> mapTradePair;

	public MarketPresenter(@NonNull MarketInterface.View view) {
		mMarketFragment = view;
	}

	/**
	 * 查询新的交易对信息，如果有，则更新数据库，并显示出来；
	 */
	@Override
	public void getTradePair(boolean firstLoad) {
		String lastHash = SpUtil.getTradePairHash();
		OkGo.<TradePairResponse>get(Constants.MARKET_TRADEPAIR_GROUP_HASH).params("hash", lastHash).tag(this).execute(new NewJsonSubCallBack<TradePairResponse>() {
			@Override
			public void onSuc(Response<TradePairResponse> response) {
				if (response == null || response.body() == null || response.body().getData() == null) {
					return;
				}
				if (mMarketFragment == null) {
					return;
				}
				if (firstLoad) {
					List<TradePairGroupTable> pairGroup = TradePairGroupController.getInstance().getTradePairGroups();
					mMarketFragment.setTabData(pairGroup);
				}
			}

			@Override
			public TradePairResponse dealJSONConvertedResult(TradePairResponse tradePairResponse) {
				if (tradePairResponse == null||tradePairResponse.getData()==null) {
					return null;
				}
				if (!TextUtils.isEmpty(tradePairResponse.getData().getHash()) && !TextUtils.isEmpty(lastHash)) {
					if (tradePairResponse.getData().getHash().equals(lastHash)) {
						return null;
					}
				}
				List<TradePairResponse.DataBean.ListBeanX> dataBeans = tradePairResponse.getData().getList();
				TradePairInfoController.getInstance().addDataToDataBase(dataBeans);
				SpUtil.setTradePairHash(tradePairResponse.getData().getHash());
				return tradePairResponse;
			}

			@Override
			public void onE(Response<TradePairResponse> response) {
				if (mMarketFragment == null) {
					return;
				}
				if (firstLoad) {
					mMarketFragment.setTabDataError();
				}
			}
		});
	}

	@Override
	public void getContractList() {
		if (!SwitchUtils.isOpenContract()) {
			return;
		}

		OkGo.<ContractListModel>get(Constants.MARKET_CONTRACT_LIST).execute(new NewJsonSubCallBack<ContractListModel>() {
			@Override
			public void onSuc(Response<ContractListModel> response) {

			}

			@Override
			public ContractListModel dealJSONConvertedResult(ContractListModel contractListModel) {
				if (contractListModel == null || contractListModel.getData() == null) {
					return null;
				}
				List<ContractListModel.DataBean.ListBean> dataBeans = contractListModel.getData().getList();
				ContractConfigController.getInstance().addInToDatabase(contractListModel.getData().getConfig());
				ContractInfoController.getInstance().addInToDatabase(dataBeans);
				return super.dealJSONConvertedResult(contractListModel);
			}

			@Override
			public void onE(Response<ContractListModel> response) {
			}
		});

		OkGo.<ContractListModel>get(Constants.MARKET_CONTRACT_USDT_LIST).execute(new NewJsonSubCallBack<ContractListModel>() {
			@Override
			public void onSuc(Response<ContractListModel> response) {
			}

			@Override
			public ContractListModel dealJSONConvertedResult(ContractListModel contractListModel) {
				if (contractListModel == null || contractListModel.getData() == null) {
					return null;
				}
				List<ContractListModel.DataBean.ListBean> dataBeans = contractListModel.getData().getList();
				ContractUsdtConfigController.getInstance().addInToDatabase(contractListModel.getData().getConfig());
				ContractUsdtInfoController.getInstance().addInToDatabase(dataBeans);
				return super.dealJSONConvertedResult(contractListModel);
			}

			@Override
			public void onE(Response<ContractListModel> response) {
			}
		});
	}

	/**
	 * 获取banner信息
	 */
	@Override
	public void getBannerList() {
		OkGo.<BannerList>get(Constants.CONTENT_GET_BANNER_LIST).params("position", "APP").tag(this).execute(new NewJsonSubCallBack<BannerList>() {
			@Override
			public void onSuc(Response<BannerList> response) {
				if (response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
					if (mMarketFragment == null) {
						return;
					}
					mMarketFragment.onGetBannerList(response.body().getData());
				} else {
					mMarketFragment.onGetBannerList(null);
				}
			}

			@Override
			public void onE(Response<BannerList> response) {
				mMarketFragment.onGetBannerList(null);
			}
		});
	}

	/**
	 * 获取zendesk公告
	 */
	@Override
	public void getZendeskArticleList() {
		String zendeskArticlesUrl = UrlUtil.getZendeskNoticeUrl();
		Map<String, String> parmas = new HashMap<>();
		parmas.put("page", "1");
		parmas.put("per_page", "5");
		OkGo.<ZendeskArticlesResponse>get(zendeskArticlesUrl).params(parmas).tag(this).execute(new NewJsonSubCallBack<ZendeskArticlesResponse>() {
			@Override
			public void onSuc(Response<ZendeskArticlesResponse> response) {
				if (response.body() != null && response.body().getArticles() != null && response.body().getArticles().size() > 0) {
					if (mMarketFragment != null) {
						mMarketFragment.onGetZendeskArticleList(response.body().getArticles());
					}
				} else {
					if (mMarketFragment != null) {
						mMarketFragment.onGetZendeskArticleList(null);
					}
				}
			}

			@Override
			public void onE(Response<ZendeskArticlesResponse> response) {
				if (mMarketFragment != null) {
					mMarketFragment.onGetZendeskArticleList(null);
				}
			}
		});
	}

	/**
	 * 获取新的杠杆信息
	 */
	@Override
	public void getNewMarginSymbol() {
		OkGo.<LeverSymbolListModel>get(Constants.MARGIN_SYMBOL_LIST).execute(new NewJsonSubCallBack<LeverSymbolListModel>() {
			@Override
			public void onSuc(Response<LeverSymbolListModel> response) {
			}

			@Override
			public LeverSymbolListModel dealJSONConvertedResult(LeverSymbolListModel leverSymbolListModel) {
				if (null == leverSymbolListModel.getData()) {
					return null;
				} else {
					MarginSymbolController.getInstance().addInToDatabase(leverSymbolListModel.getData());
				}
				return super.dealJSONConvertedResult(leverSymbolListModel);
			}

			@Override
			public void onE(Response<LeverSymbolListModel> response) {
			}
		});
	}

	/**
	 * 获取自选行情的http请求
	 */
	@Override
	public void getOptionalMarket(String id, String tradePairList, String sortField, String sortType) {

		HttpParams params = new HttpParams();
//		params.put("group", id);
		params.put("tradePair", tradePairList);
		//自选排序没有24小时
		if (!TextUtils.isEmpty(sortType) && !TextUtils.isEmpty(sortField) && !sortType.equals(Constants.SORT_V24_VOL)) {
			params.put("sortField", sortField);
			params.put("sortType", sortType);
		}


		OkGo.<TradePairMarketRes>get(Constants.GET_MARKET_TRADEPAIR_QUOTE).params(params).tag(this).execute(new NewJsonSubCallBack<TradePairMarketRes>() {
			@Override
			public void onSuc(Response<TradePairMarketRes> response) {
				if (response.body() != null && response.body().getData() != null && mapTradePair != null && mapTradePair.size() > 0) {
					MarketDataManager.getInstance().dispatchHttpMarketData(id, mapTradePair);
				}
			}

			@Override
			public TradePairMarketRes dealJSONConvertedResult(TradePairMarketRes tradePairMarketRes) {
				if (tradePairMarketRes == null || tradePairMarketRes.getData() == null || tradePairMarketRes.getData().size() == 0) {
					return null;
				}

				if (mapTradePair == null) {
					mapTradePair = new HashMap<>();
				}
				for (int i = 0; i < tradePairMarketRes.getData().size(); i++) {
					mapTradePair.put(tradePairMarketRes.getData().get(i).getS(), tradePairMarketRes.getData().get(i));
				}
				return tradePairMarketRes;
			}

			@Override
			public void onE(Response<TradePairMarketRes> response) {
				if (mMarketFragment == null) {
					return;
				}
				mMarketFragment.refreshMarketDataError(response.message());
			}
		});
	}

	@Override
	public void getHttpSpotMarket(String id, String sortField, String sortType) {
		HttpParams params = new HttpParams();
		params.put("group", id);
		if (!TextUtils.isEmpty(sortType) && !TextUtils.isEmpty(sortField)) {
			params.put("sortField", sortField);
			params.put("sortType", sortType);
		}
		OkGo.<TradePairMarketRes>get(Constants.MARKET_TRADEPAIR_DATA).tag(this).params(params).execute(new NewJsonSubCallBack<TradePairMarketRes>() {
			@Override
			public void onSuc(Response<TradePairMarketRes> response) {
				if (response.body() != null && response.body().getData() != null && mapTradePair != null && mapTradePair.size() > 0) {
					MarketDataManager.getInstance().dispatchHttpMarketData(id, mapTradePair);
				}
			}

			@Override
			public TradePairMarketRes dealJSONConvertedResult(TradePairMarketRes tradePairMarketRes) {
				if (mapTradePair == null) {
					mapTradePair = new HashMap<>();
				}
				for (int i = 0; i < tradePairMarketRes.getData().size(); i++) {
					mapTradePair.put(tradePairMarketRes.getData().get(i).getS(), tradePairMarketRes.getData().get(i));
				}
				return tradePairMarketRes;
			}


			@Override
			public void onE(Response<TradePairMarketRes> response) {
			}

		});
	}

	@Override
	public void onDestory() {
		mMarketFragment = null;
		OkGo.getInstance().cancelTag(this);
	}
}
