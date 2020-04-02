package com.coinbene.manbiwang.home;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coinbene.common.Constants;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.HomeMarketModel;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ding
 * 2019-12-31
 * com.coinbene.manbiwang.home
 */
public class HomeViewModel extends AndroidViewModel {
	static final String TAG = "HomeViewModel";

	private boolean showNavigation = false;

	private MutableLiveData<List<BannerList.DataBean>> bannerData = new MutableLiveData<>();
	private MutableLiveData<List<ZendeskArticlesResponse.ArticlesBean>> noticeData = new MutableLiveData();
	private MutableLiveData<List<AppConfigModel.MainNavigationBean>> navData = new MutableLiveData<>();

	private MutableLiveData<List<WsMarketData>> hotData = new MutableLiveData<>();
	private MutableLiveData<List<WsMarketData>> bigData = new MutableLiveData<>();

	private List<HomeMarketModel.DataBean.ListBean> hotList = new ArrayList<>();
	private List<HomeMarketModel.DataBean.ListBean> bigList = new ArrayList<>();

	private NewMarketWebsocket.MarketDataListener listener;

//	= (Map<String, TradePairMarketRes.DataBean> map) -> {
//		processHotCoin(map);
//		processHeavyCoin(map);
//	};


	public HomeViewModel(@NonNull Application application) {
		super(application);
		listener = dataMap -> {
			processHotCoin(dataMap);
			processBigCoin(dataMap);
		};
		if (SpUtil.getHotCoin() != null) {
			hotList = Collections.synchronizedList(SpUtil.getHotCoin().getList());
		}
		if (SpUtil.getBigCoin() != null) {
			bigList = Collections.synchronizedList(SpUtil.getBigCoin().getList());
		}
		if (SpUtil.getAppConfig() != null && SpUtil.getAppConfig().getMain_navigation() != null && SpUtil.getAppConfig().getMain_navigation().size() > 0) {
			navData.setValue(SpUtil.getAppConfig().getMain_navigation());
			showNavigation = true;
		}
		if (SpUtil.getBanners() != null) {
			bannerData.setValue(SpUtil.getBanners().getData());
		}

		if (SpUtil.getNotices() != null) {
			noticeData.setValue(SpUtil.getNotices().getArticles());
		}
	}

	public void registerMarketData() {
		if (listener == null) {
			listener = dataMap -> {
				processHotCoin(dataMap);
				processBigCoin(dataMap);
			};
		}
		NewMarketWebsocket.getInstance().registerMarketDataListener(listener);
	}

	public void unRegisterMarketData() {
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(listener);
	}


	public MutableLiveData<List<BannerList.DataBean>> getBannerData() {
		return bannerData;
	}

	public MutableLiveData<List<ZendeskArticlesResponse.ArticlesBean>> getNoticeData() {
		return noticeData;
	}

	public MutableLiveData<List<AppConfigModel.MainNavigationBean>> getNavData() {
		return navData;
	}

	public MutableLiveData<List<WsMarketData>> getHotData() {
		return hotData;
	}

	public MutableLiveData<List<WsMarketData>> getBigData() {
		return bigData;
	}

	/**
	 * 获取banner
	 */
	public void getBanner() {
		OkGo.<BannerList>get(Constants.CONTENT_GET_BANNER_LIST).params("position", "APP").tag(this).execute(new NewJsonSubCallBack<BannerList>() {
			@Override
			public void onSuc(Response<BannerList> response) {
				if (response.body() == null || response.body().getData() == null) {
					bannerData.setValue(null);
					SpUtil.setBanners(response.body());
					return;
				}

				if (bannerData.getValue() != null && bannerData.getValue().size() == response.body().getData().size()) {
					boolean dataChanged = false;
					for (int i = 0; i < bannerData.getValue().size(); i++) {
						BannerList.DataBean bean = bannerData.getValue().get(i);
						if (!bean.equals(response.body().getData().get(i))) {
							dataChanged = true;
							break;
						}
					}

					if (!dataChanged) {
						return;
					}
				}

				bannerData.setValue(response.body().getData());
				SpUtil.setBanners(response.body());

			}

			@Override
			public void onE(Response<BannerList> response) {
			}
		});
	}

	/**
	 * 获取公告信息
	 */
	public void getNotice() {
		String zendeskArticlesUrl = UrlUtil.getZendeskNoticeUrl();
		Map<String, String> parmas = new HashMap<>();
		parmas.put("page", "1");
		parmas.put("per_page", "5");
		OkGo.<ZendeskArticlesResponse>get(zendeskArticlesUrl).params(parmas).tag(this).execute(new NewJsonSubCallBack<ZendeskArticlesResponse>() {
			@Override
			public void onSuc(Response<ZendeskArticlesResponse> response) {

				if (noticeData.getValue() != null && noticeData.getValue().size() == response.body().getArticles().size()) {
					boolean dataChanged = false;
					for (int i = 0; i < noticeData.getValue().size(); i++) {
						ZendeskArticlesResponse.ArticlesBean articlesBean = noticeData.getValue().get(i);
						if (!articlesBean.toString().equals(response.body().getArticles().get(i).toString())) {
							dataChanged = true;
							break;
						}
					}
					if (!dataChanged) {
						return;
					}
				}

				SpUtil.setNotice(response.body());
				noticeData.setValue(response.body().getArticles());
			}

			@Override
			public void onE(Response<ZendeskArticlesResponse> response) {
				noticeData.setValue(null);
			}
		});
	}

	/**
	 * 获取导航信息
	 */
	public void getNavigation() {

		OkGo.<AppConfigModel>get(UrlUtil.getAppConfigUrl()).tag(this).execute(new NewJsonSubCallBack<AppConfigModel>() {
			@Override
			public void onSuc(Response<AppConfigModel> response) {

				if (response.body() == null || response.body().getMain_navigation() == null || response.body().getMain_navigation().size() == 0) {
					navData.setValue(null);
					showNavigation = false;
					return;
				}
				SpUtil.setAppConfig(response.body());

				if (navData.getValue() != null && navData.getValue().size() == response.body().getMain_navigation().size()) {
					boolean dataChanged = false;
					for (int i = 0; i < navData.getValue().size(); i++) {
						AppConfigModel.MainNavigationBean navigationBean = navData.getValue().get(i);
						if (!navigationBean.toString().equals(response.body().getMain_navigation().get(i).toString())) {
							dataChanged = true;
							break;
						}
					}
					if (!dataChanged) {
						return;
					}
				}
				navData.setValue(response.body().getMain_navigation());
				showNavigation = true;
			}

			@Override
			public void onE(Response<AppConfigModel> response) {
//				AppConfigModel model = SpUtil.getAppConfig();
//				if (model != null) {
//					navData.setValue(model.getMain_navigation());
//					showNavigation = true;
//				} else {
				navData.setValue(null);
				showNavigation = false;
//				}


			}
		});


//		List<HomeNavigationModel> list = new ArrayList<>();
//
//		if (SwitchUtils.isOpenOTC_Asset()) {
//			list.add(new HomeNavigationModel(R.drawable.icon_otc_trading, ResourceProvider.getString(R.string.res_otc_trade), ""));
//		}
//
//		if (SwitchUtils.isOpenFortune()) {
//			list.add(new HomeNavigationModel(R.drawable.icon_fortune, ResourceProvider.getString(R.string.res_fortune), ""));
//		}
//
//		if (SwitchUtils.isOpenMargin()) {
//			list.add(new HomeNavigationModel(R.drawable.icon_margin_trading, ResourceProvider.getString(R.string.res_margin_trade), ""));
//		}
//
//		list.add(new HomeNavigationModel(R.drawable.icon_helper_center, ResourceProvider.getString(R.string.res_help_center), ""));
//		list.add(new HomeNavigationModel(R.drawable.icon_helper_center, ResourceProvider.getString(R.string.res_help_center), ""));
//		list.add(new HomeNavigationModel(R.drawable.icon_helper_center, ResourceProvider.getString(R.string.res_help_center), ""));
//		list.add(new HomeNavigationModel(R.drawable.icon_helper_center, ResourceProvider.getString(R.string.res_help_center), ""));
//
//		if (list.size() == 0) {
//
//		} else {
//
//		}

	}


	/**
	 * 获取大币种
	 */
	public void getBigCoin() {
		HttpParams params = new HttpParams();
		params.put("tabId", "BIG");
		params.put("web", "0");
		OkGo.<HomeMarketModel>get(Constants.HOME_HOT_COIN).params(params).tag(this).execute(new NewJsonSubCallBack<HomeMarketModel>() {
			@Override
			public void onSuc(Response<HomeMarketModel> response) {
				if (response.body().getData() == null || response.body().getData().getList() == null || response.body().getData().getList().size() == 0) {
					bigData.setValue(null);
					SpUtil.setBigCoin(null);
					if (bigList != null) {
						bigList.clear();
					}
					return;
				}
//
//				if (SpUtil.getBigCoin() != null && CBRepository.gson.toJson(SpUtil.getBigCoin()).equals(CBRepository.gson.toJson(response.body().getData()))) {
//					return;
//				}
				SpUtil.setBigCoin(response.body().getData());


				bigList = Collections.synchronizedList(response.body().getData().getList());
				NewMarketWebsocket.getInstance().pullMarketData();
			}

			@Override
			public void onE(Response<HomeMarketModel> response) {
				if (bigList == null) {
					bigData.setValue(null);
				}
			}
		});
	}


	/**
	 * 获取热门币种
	 */
	public void getHotCoin() {
		HttpParams params = new HttpParams();
		params.put("tabId", "HOT");
		params.put("web", "0");
		OkGo.<HomeMarketModel>get(Constants.HOME_HOT_COIN).params(params).tag(this).execute(new NewJsonSubCallBack<HomeMarketModel>() {
			@Override
			public void onSuc(Response<HomeMarketModel> response) {
				if (response.body().getData() == null || response.body().getData().getList() == null || response.body().getData().getList().size() == 0) {
					hotData.setValue(null);
					SpUtil.setHotCoin(null);
					if (hotList != null) {
						hotList.clear();
					}
					return;
				}

//				if (SpUtil.getHotCoin() != null && CBRepository.gson.toJson(SpUtil.getHotCoin()).equals(CBRepository.gson.toJson(response.body().getData()))) {
//					return;
//				}
				SpUtil.setHotCoin(response.body().getData());
				hotList = Collections.synchronizedList(response.body().getData().getList());
				NewMarketWebsocket.getInstance().pullMarketData();
			}

			@Override
			public void onE(Response<HomeMarketModel> response) {
			}
		});
	}


	/**
	 * 处理大币种数据
	 */
	private void processBigCoin(Map<String, WsMarketData> map) {
		if (bigList.size() == 0) {
			return;
		}

		List<WsMarketData> heavyCoins = new ArrayList<>();
		synchronized (bigList) {
			for (HomeMarketModel.DataBean.ListBean data : bigList) {
				WsMarketData bean = map.get(data.getTradePair());
				if (bean == null) {
					continue;
				}
				if (TextUtils.isEmpty(bean.getTradePairName()))
					bean.setTradePairName(data.getBaseAsset() + "/" + data.getQuoteAsset());
				heavyCoins.add(bean);
			}
		}

		bigData.postValue(heavyCoins);
	}

	/**
	 * 处理热门币种数据
	 */
	private void processHotCoin(Map<String, WsMarketData> map) {
		if (hotList.size() == 0) {
			return;
		}

		List<WsMarketData> hotCoins = new ArrayList<>();
		synchronized (hotList) {
			for (HomeMarketModel.DataBean.ListBean data : hotList) {
				WsMarketData bean = map.get(data.getTradePair());

				if (bean == null) {
					continue;
				}
				if (TextUtils.isEmpty(bean.getTradePairName()))
					bean.setTradePairName(data.getBaseAsset() + "/" + data.getQuoteAsset());
				hotCoins.add(bean);
			}
		}
		hotData.postValue(hotCoins);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		NewMarketWebsocket.getInstance().unregisterMarketDataListener(listener);
	}

	public boolean isShowNavigation() {
		return showNavigation;
	}
}
