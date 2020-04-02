package com.coinbene.manbiwang.serviceimpl;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;

import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.ContractConfigController;
import com.coinbene.common.database.ContractInfoController;
import com.coinbene.common.database.ContractUsdtConfigController;
import com.coinbene.common.database.ContractUsdtInfoController;
import com.coinbene.common.database.MarginSymbolController;
import com.coinbene.common.database.OtcConfigController;
import com.coinbene.common.database.TradePairGroupController;
import com.coinbene.common.database.TradePairInfoController;
import com.coinbene.common.database.TradePairOptionalController;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInvitationController;
import com.coinbene.common.dialog.UpdateDialog;
import com.coinbene.common.network.newokgo.NewDialogJsonCallback;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.Tools;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.common.widget.dialog.UpdateProcessDialog;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.model.contract.UnitChangeMode;
import com.coinbene.manbiwang.model.http.AdResponse;
import com.coinbene.manbiwang.model.http.AppConfigModel;
import com.coinbene.manbiwang.model.http.BannerList;
import com.coinbene.manbiwang.model.http.CheckVersionResponse;
import com.coinbene.manbiwang.model.http.CoinTotalInfoModel;
import com.coinbene.manbiwang.model.http.ContractListModel;
import com.coinbene.manbiwang.model.http.FavoriteListModel;
import com.coinbene.manbiwang.model.http.HomeMarketModel;
import com.coinbene.manbiwang.model.http.LeverSymbolListModel;
import com.coinbene.manbiwang.model.http.MarginUserConfigModel;
import com.coinbene.manbiwang.model.http.OtcAdConfigModel;
import com.coinbene.manbiwang.model.http.SimpleModel;
import com.coinbene.manbiwang.model.http.SomePicUrlData;
import com.coinbene.manbiwang.model.http.SwitchModel;
import com.coinbene.manbiwang.model.http.TradePairResponse;
import com.coinbene.manbiwang.model.http.UserConfigModel;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.model.http.UserInvitationModel;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;
import com.coinbene.manbiwang.modules.common.other.DialogConiDescribeActivity;
import com.coinbene.manbiwang.push.PushHelper;
import com.coinbene.manbiwang.push.PushSelectDialog;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.app.AppService;
import com.coinbene.manbiwang.widget.dialog.NoviceGuidanceDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.tencent.android.tpush.XGPushManager;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by june
 * on 2019-11-09
 */
@Route(path = RouteHub.App.appService)
public class AppServiceImpl implements AppService {


	private DrawerLayout draweLayout;

	@Override
	public void init(Context context) {

	}

	/**
	 * 处理用户成功登陆之后的逻辑
	 */
	@Override
	public void onUserLoginSuccess(int tabIndex) {
		if (SwitchUtils.isOpenContract()) {
			WebSocketManager.getInstance().sendSetBtcClientData();
			WebSocketManager.getInstance().sendSetUsdtClientData();
		}

		//新版websocket login
		WebSocketManager.getInstance().sendLoginRequest();

		getBalanceList();
		getOptional();

		getMarginUserConfig();
		checkShowNoviceGuidanceDialog();
		registPush();

		//如果没有开通协议去查询
		if (!SpUtil.getProtocolStatusOfContract("usdt")) {
			getProtoclStatusOfContract("usdtContract_protocol");
		}

		if (!SpUtil.getProtocolStatusOfContract("btc")) {
			getProtoclStatusOfContract("btcContract_protocol");
		}

		getProtoclStatusOfContract("usdtContract_tradeUnit");


		getUserInInvitation(null);

	}


	//得到用户当前部分状态信息（代理商、邀请返金））
	@Override
	public void getUserInInvitation(CallBack callBack) {
		if (!CommonUtil.isLogin()) {
			return;
		}
		OkGo.<UserInvitationModel>get(Constants.USER_INVITATION).execute(new NewJsonSubCallBack<UserInvitationModel>() {
			@Override
			public void onSuc(Response<UserInvitationModel> response) {
				if (callBack != null) {
					callBack.onResult(response.body());
				}
			}

			@Override
			public UserInvitationModel dealJSONConvertedResult(UserInvitationModel userInvitationModel) {
				if (null == userInvitationModel.getData()) {
					return null;
				} else {
					UserInvitationController.getInstance().addInToDatabase(userInvitationModel.getData());
				}
				return super.dealJSONConvertedResult(userInvitationModel);
			}

			@Override
			public void onE(Response<UserInvitationModel> response) {
				if (callBack != null)
					callBack.onResult("error");
			}
		});


	}

	@Override
	public void getTradePair() {
		//如果没有清除过hash 则清除hash
		if (!SpUtil.getIsClearHash()) {
			SpUtil.setTradePairHash("");
			SpUtil.setIsClearHash(true);
		}

		String lastHash = TradePairInfoController.getInstance().checkTradeExist() ? SpUtil.getTradePairHash() : "";

		OkGo.<TradePairResponse>get(Constants.MARKET_TRADEPAIR_GROUP_HASH).params("hash", lastHash).execute(new NewJsonSubCallBack<TradePairResponse>() {
			@Override
			public void onSuc(Response<TradePairResponse> response) {
			}

			@Override
			public TradePairResponse dealJSONConvertedResult(TradePairResponse tradePairResponse) {
				TradePairGroupController.getInstance().insertContractGroup();

				if (tradePairResponse == null || tradePairResponse.getData() == null || tradePairResponse.getData().getList() == null) {
					return null;
				}
//
				if (!TextUtils.isEmpty(tradePairResponse.getData().getHash()) && !TextUtils.isEmpty(lastHash)) {
					if (tradePairResponse.getData().getHash().equals(lastHash)) {
						return null;
					}
				}
				List<TradePairResponse.DataBean.ListBeanX> dataBeans = tradePairResponse.getData().getList();
				TradePairInfoController.getInstance().addDataToDataBase(dataBeans);
				SpUtil.setTradePairHash(tradePairResponse.getData().getHash());
				return super.dealJSONConvertedResult(tradePairResponse);
			}

			@Override
			public void onE(Response<TradePairResponse> response) {
			}
		});
	}

	@Override
	public void getAdInfo(CallBack<AdResponse> callBack) {
		OkGo.<AdResponse>get(Constants.GET_AD_INFO).execute(new NewJsonSubCallBack<AdResponse>() {
			@Override
			public void onSuc(Response<AdResponse> response) {
				if (response == null || response.body() == null || response.body().data == null) {
					return;
				}
				if (callBack != null) {
					callBack.onResult(response.body());
				}
			}

			@Override
			public void onE(Response<AdResponse> response) {
			}

		});
	}


	@Override
	public void getContractList() {
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
	}

	@Override
	public void getOtcConfig() {
		OkGo.<OtcAdConfigModel>post(Constants.OTC_GET_CONFIG).execute(new NewJsonSubCallBack<OtcAdConfigModel>() {
			@Override
			public void onSuc(Response<OtcAdConfigModel> response) {
			}

			@Override
			public OtcAdConfigModel dealJSONConvertedResult(OtcAdConfigModel otcAdConfigModel) {
				if (otcAdConfigModel == null || otcAdConfigModel.getData() == null) {
					return null;
				}
				OtcConfigController.getInstance().addAssetInToDatabase(otcAdConfigModel.getData().getAssetList());
				OtcConfigController.getInstance().addCurrencyInToDatabase(otcAdConfigModel.getData().getCurrencyList());
				OtcConfigController.getInstance().addPayTypeInToDatabase(otcAdConfigModel.getData().getPayTypesList());
				return super.dealJSONConvertedResult(otcAdConfigModel);
			}

			@Override
			public void onE(Response<OtcAdConfigModel> response) {
			}
		});
	}

	@Override
	public void getOptional() {
		if (!CommonUtil.isLogin()) {
			return;
		}

		OkGo.<FavoriteListModel>get(Constants.GET_FAVORITE_LIST).execute(new NewJsonSubCallBack<FavoriteListModel>() {
			@Override
			public void onSuc(Response<FavoriteListModel> response) {
			}

			@Override
			public FavoriteListModel dealJSONConvertedResult(FavoriteListModel favoriteListModel) {
				if (null == favoriteListModel.getData()) {
					return null;
				} else {
					TradePairOptionalController.getInstance().addOptionalTradePair(favoriteListModel);
				}
				return super.dealJSONConvertedResult(favoriteListModel);
			}

			@Override
			public void onE(Response<FavoriteListModel> response) {
			}
		});
	}


	@Override
	public void getMarginSymbolList() {
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

	@Override
	@NeedLogin
	public void getBalanceList() {
		OkGo.<CoinTotalInfoModel>get(Constants.BALANCE_SPOT_COIN).execute(new NewJsonSubCallBack<CoinTotalInfoModel>() {
			@Override
			public void onSuc(Response<CoinTotalInfoModel> response) {
			}

			@Override
			public CoinTotalInfoModel dealJSONConvertedResult(CoinTotalInfoModel coinTotalInfoModel) {
				if (coinTotalInfoModel != null && coinTotalInfoModel.getData() != null) {
					BalanceController.getInstance().addInToDatabase(coinTotalInfoModel.getData().getList());
				}
				return super.dealJSONConvertedResult(coinTotalInfoModel);
			}

			@Override
			public void onE(Response<CoinTotalInfoModel> response) {

			}
		});
	}


	@Override
	public void getContractUsdtList() {
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
	 * 更新用户信息
	 */
	@Override
	@NeedLogin
	public void updateUserInfo() {
		OkGo.<UserInfoResponse>get(Constants.USER_GET_USERINFO).tag(this).execute(new NewJsonSubCallBack<UserInfoResponse>() {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
			}

			@Override
			public UserInfoResponse dealJSONConvertedResult(UserInfoResponse userInfoResponse) {
				if (userInfoResponse.isSuccess()) {
					UserInfoController.getInstance().updateUserInfo(userInfoResponse.data);
				}
				return super.dealJSONConvertedResult(userInfoResponse);
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {
			}
		});
	}


	@Override
	public void showPushSwitchDialog(Context context, boolean isOpen) {
		if (isOpen) {
			PushSelectDialog pushSelectDialog = new PushSelectDialog(context, PushSelectDialog.TYPE_OPEN_PUSH);
			pushSelectDialog.setCancelable(false);
			pushSelectDialog.show();
		} else {
			PushSelectDialog pushSelectDialog = new PushSelectDialog(context, PushSelectDialog.TYPE_CLOSE_PUSH);
			pushSelectDialog.setCancelable(false);
			pushSelectDialog.show();
		}
	}

	@Override
	public void checkUpdate(Activity activity, CallBack<Boolean> callBack) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("type", "android");
		httpParams.put("versionCode", AppUtil.getVersionCode(activity));
		httpParams.put("versionName", AppUtil.getVersionName(activity));

		OkGo.<CheckVersionResponse>get(Constants.APP_CHECK_UPDATE).tag(this).params(httpParams).execute(new NewJsonSubCallBack<CheckVersionResponse>() {
			@Override
			public void onSuc(Response<CheckVersionResponse> response) {
				CheckVersionResponse baseResponse = response.body();
				if (baseResponse.getData() != null && !TextUtils.isEmpty(baseResponse.getData().getDownUrl())) {
					SpUtil.setHasNewVersion(true);

					UpdateDialog updateDialog = new UpdateDialog(activity);
					if (baseResponse.getData() != null) {
						updateDialog.setData(baseResponse.getData());//(baseResponse.getData().getDes());
					}

					updateDialog.setClickLisenter(new UpdateDialog.UpdateDialogClickListener() {
						@Override
						public void onCancel() {
							if (callBack != null) {
								callBack.onResult(false);
							}
						}

						@Override
						public void goToUpdate(String downUrl, boolean isForceUpdate) {
							//没有写入权限返回
//							if (!PermissionManager.check(activity, Permission.WRITE_EXTERNAL_STORAGE)) {
//								PermissionManager.requestPermission(activity, Permission.WRITE_EXTERNAL_STORAGE, null);
//								if (callBack != null) {
//									callBack.onResult(false);
//								}
//								return;
//							}

							if (TextUtils.isEmpty(downUrl)) {
								if (callBack != null) {
									callBack.onResult(false);
								}
								return;
							}

							ToastUtil.show(R.string.toast_tips_downing);
							UpdateProcessDialog updateProcessDialog = new UpdateProcessDialog(activity);
							updateProcessDialog.setData(downUrl, isForceUpdate);
							updateProcessDialog.show();

							if (callBack != null) {
								callBack.onResult(true);
							}
						}
					});
					updateDialog.show();
				} else {
					if (callBack != null) {
						callBack.onResult(false);
					}
				}
			}

			@Override
			public void onE(Response<CheckVersionResponse> response) {
				if (callBack != null) {
					callBack.onResult(false);
				}
			}

		});
	}

	@Override
	public void getPopupBanner(Activity activity) {
		OkGo.<SomePicUrlData>get(Constants.POPUP_BANNER).tag(this).execute(new NewJsonSubCallBack<SomePicUrlData>() {
			@Override
			public void onSuc(Response<SomePicUrlData> response) {
				SomePicUrlData t = response.body();
				if (t == null || t.getData() == null || TextUtils.isEmpty(t.getData().img_url) || TextUtils.isEmpty(t.getData().id)) {
					return;
				}
				if (SpUtil.getPopupActivityId().equals(t.getData().id)) {
					//和本地保存的活动id相同，判断弹窗开关
					if (SpUtil.getPopupEnable()) {
						DialogConiDescribeActivity.startMe(activity, t.getData().img_url, t.getData().link_url);
					}
				} else {
					//新的活动，需要重置弹窗开关
					SpUtil.setPopupActivityId(t.getData().id);
					SpUtil.setPopupEnable(true);
					DialogConiDescribeActivity.startMe(activity, t.getData().img_url, t.getData().link_url);
				}
			}

			@Override
			public void onE(Response<SomePicUrlData> response) {

			}
		});

	}

	@Override
	public void getBigCoinList() {
		HttpParams params = new HttpParams();
		params.put("tabId", "BIG");
		params.put("web", "0");
		OkGo.<HomeMarketModel>get(Constants.HOME_HOT_COIN).params(params).tag(this).execute(new NewJsonSubCallBack<HomeMarketModel>() {
			@Override
			public void onSuc(Response<HomeMarketModel> response) {
				SpUtil.setBigCoin(response.body().getData());
			}

			@Override
			public void onE(Response<HomeMarketModel> response) {
			}
		});
	}

	@Override
	public void getHotCoinList() {
		HttpParams params = new HttpParams();
		params.put("tabId", "BIG");
		params.put("web", "0");
		OkGo.<HomeMarketModel>get(Constants.HOME_HOT_COIN).params(params).tag(this).execute(new NewJsonSubCallBack<HomeMarketModel>() {
			@Override
			public void onSuc(Response<HomeMarketModel> response) {
				SpUtil.setHotCoin(response.body().getData());
//				heavyList = Collections.synchronizedList(response.body().getData().getList());
			}

			@Override
			public void onE(Response<HomeMarketModel> response) {
			}
		});
	}

	@Override
	public void getAppConfig(Activity activity, CallBack<Boolean> callBack) {
		OkGo.<AppConfigModel>get(UrlUtil.getAppConfigUrl()).tag(this).execute(new NewDialogJsonCallback<AppConfigModel>(activity) {
			@Override
			public void onSuc(Response<AppConfigModel> response) {
				SpUtil.setAppConfig(response.body());
				if (callBack != null) {
					callBack.onResult(true);
				}

			}

			@Override
			public void onE(Response<AppConfigModel> response) {
				if (callBack != null) {
					callBack.onResult(false);
				}
			}

			@Override
			public void onFinish() {
				super.onFinish();
				getTradePair();//可能是为了切换站点的时候  重新刷交易对
				getContractList();
				getContractUsdtList();
			}
		});


	}

	@Override
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
				SpUtil.setNotice(response.body());
			}

			@Override
			public void onE(Response<ZendeskArticlesResponse> response) {
			}
		});
	}

	@Override
	public DrawerLayout getMainDrawerLayout() {
		return draweLayout;
	}

	@Override
	public void setDrawerLayout(DrawerLayout drawerLayout) {
		this.draweLayout = drawerLayout;

	}

	/**
	 * 获取banner
	 */
	@Override
	public void getBanners() {
		OkGo.<BannerList>get(Constants.CONTENT_GET_BANNER_LIST).params("position", "APP").tag(this).execute(new NewJsonSubCallBack<BannerList>() {
			@Override
			public void onSuc(Response<BannerList> response) {
				SpUtil.setBanners(response.body());

			}

			@Override
			public void onE(Response<BannerList> response) {
			}
		});
	}


	/**
	 * 获取用户是否开通了杠杆
	 */
	private void getMarginUserConfig() {
		OkGo.<MarginUserConfigModel>get(Constants.MARGIN_USER_CONFIG).execute(new NewJsonSubCallBack<MarginUserConfigModel>() {

			@Override
			public void onSuc(Response<MarginUserConfigModel> response) {
				MarginUserConfigModel.DataBean data = response.body().getData();
				if (data != null) {
					if (TextUtils.isEmpty(data.getFlag())) {
						return;
					}
					//设置用户是否开通了杠杆   1开通  0未开通
					SpUtil.setMarginUserConfig(data.getFlag().equals("1"));
				}
			}

			@Override
			public void onE(Response<MarginUserConfigModel> response) {
			}
		});
	}

	private void checkShowNoviceGuidanceDialog() {
		if (SpUtil.isNeedNewUserGuide() && SwitchUtils.isOpenOTC()) {
			new Handler().postDelayed(() -> {
				NoviceGuidanceDialog noviceGuidanceDialog = new NoviceGuidanceDialog(CBRepository.getLifeCallback().getCurrentAcitivty());
				noviceGuidanceDialog.show();
			}, 200);

			SpUtil.setNeedNewUserGuide(false);
		}
	}

	public void registPush() {
		if (!CommonUtil.isLogin()) {
			return;
		}
		PushHelper.bindAccount(String.valueOf(UserInfoController.getInstance().getUserInfo().userId));
		//韩语下推送有开关，未打开解除push功能
		if (LanguageHelper.isKorean(CBRepository.getContext()) && !SpUtil.get(CBRepository.getContext(), "IsPush", false)) {
			XGPushManager.unregisterPush(CBRepository.getContext());
		}
	}


	private void getProtoclStatusOfContract(String key) {
		OkGo.<UserConfigModel>get(Constants.QUERY_USER_CONFIG).params("settingKey", key).tag(this).execute(new NewJsonSubCallBack<UserConfigModel>() {
			@Override
			public void onSuc(Response<UserConfigModel> response) {
				//开启状态进行设置

				switch (key) {
					case "usdtContract_protocol":
						if (response.body().getData().equals("1"))
							SpUtil.setProtocolStatusForContract("usdt", true);
						break;
					case "btcContract_protocol":
						if (response.body().getData().equals("1"))
							SpUtil.setProtocolStatusForContract("btc", true);
						break;
					case "usdtContract_tradeUnit":
						if ("1".equals(response.body().getData())) {
							SpUtil.setContractUsdtUnitSwitch(1);
							EventBus.getDefault().post(new UnitChangeMode(1));
						} else {
							SpUtil.setContractUsdtUnitSwitch(0);
							EventBus.getDefault().post(new UnitChangeMode(0));
						}


						break;
				}
			}

			@Override
			public void onE(Response<UserConfigModel> response) {

			}
		});
	}
}
