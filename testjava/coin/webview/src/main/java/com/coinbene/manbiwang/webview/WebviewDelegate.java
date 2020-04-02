package com.coinbene.manbiwang.webview;


import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.dialog.ShareMenu;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.Params;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.websocket.market.NewMarketWebsocket;
import com.coinbene.common.websocket.model.WsMarketData;
import com.coinbene.common.widget.CoinbeneShareParam;
import com.coinbene.common.widget.GameShareFragment;
import com.coinbene.common.widget.InviteShareDialog;
import com.coinbene.common.widget.MiningShareDialog;
import com.coinbene.manbiwang.model.http.InviteIndoModel;
import com.coinbene.manbiwang.model.http.MiningSummaryResponse;
import com.coinbene.manbiwang.webview.bean.BaseAction;
import com.coinbene.manbiwang.webview.bean.DeviceAction;
import com.coinbene.manbiwang.webview.bean.DeviceResult;
import com.coinbene.manbiwang.webview.bean.NativeAction;
import com.coinbene.manbiwang.webview.bean.NavigatorAction;
import com.coinbene.manbiwang.webview.bean.UserAction;
import com.coinbene.manbiwang.webview.jsbridge.CallBackFunction;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.lang.ref.WeakReference;

import cn.sharesdk.framework.Platform;

public class WebviewDelegate {

	//navigator
	private static final String NAVIGATOR_CLOSE_WEBAPP = "closewebapp";
	private static final String NAVIGATOR_GO_BACK = "goback";
	private static final String NAVIGATOR_SET_TITLE = "settitle";
	private static final String NAVIGATOR_SET_RIGHT = "setright";

	//native
	private static final String NATIVE_SHOWLOADING = "showloading";
	private static final String NATIVE_HIDELOADING = "hideloading";
	private static final String NATIVE_GOTOPAGE = "gotopage";
	private static final String NATIVE_SHARE = "share";
	private static final String NATIVE_COPY = "copy";
	private static final String NATIVE_SET_REFRESH_STATUS = "setrefreshstatus";

	//user
	private static final String USER_LOGIN = "login";
	private static final String USER_GET_USER = "getuser";

	//device
	private static final String DEVICE_GET_CURRENT_LANGUAGE = "getcurrentlanguage";
	private static final String DEVICE_GET_CURRENT_SITE = "getcurrentsite";
	private static final String DEVICE_GET_APP_VERSION = "getappversion";

	public static final String SHARE_TYPE_RANK = "rank";
	public static final String SHARE_TYPE_INVITE = "invite";
	public static final String SHARE_TYPE__MINING = "mining";
	public static final String SHARE_TYPE_POSTER = "poster";

	private static volatile WebviewDelegate instance;

	private WeakReference<WebviewActivity> mWebviewReference;

	private WebviewDelegate() {

	}

	public static WebviewDelegate getInstance() {
		if (instance == null) {
			synchronized (WebviewDelegate.class) {
				if (instance == null) {
					instance = new WebviewDelegate();
				}
			}
		}
		return instance;
	}

	public void showGameShare(String imgBase64) {
		if (mWebviewReference != null) {
			WebviewActivity activity = mWebviewReference.get();
			GameShareFragment.init(imgBase64).show(activity.getSupportFragmentManager(), "GameShare");
		}
	}

	public void setWebviewActivity(WebviewActivity activity) {

		mWebviewReference = new WeakReference<>(activity);
	}


	public void onDestroy() {
		mWebviewReference = null;
	}

	public void dispatchNavigatorAction(String action) {
		NavigatorAction navigatorAction = new NavigatorAction();
		navigatorAction.setAction(action);
		dispatchAction(navigatorAction, null);
	}

	public void dispatchAction(BaseAction action, CallBackFunction callBackFunction) {
		if (action instanceof NavigatorAction) {
			performNavigatorAction((NavigatorAction) action, callBackFunction);
		} else if (action instanceof NativeAction) {
			performNativeAction((NativeAction) action, callBackFunction);
		} else if (action instanceof UserAction) {
			performUserAction((UserAction) action, callBackFunction);
		} else if (action instanceof DeviceAction) {
			performDeviceAction((DeviceAction) action, callBackFunction);
		}
	}

	public void doShare(String shareType, String url) {

		if (TextUtils.isEmpty(shareType)) {
			if (BuildConfig.DEBUG) {
				ToastUtil.show("分享类型不存在");
			}
			return;
		}

		if (shareType.equals(SHARE_TYPE_RANK)) {
			//合约排名赛分享
			if (mWebviewReference == null) {
				return;
			}
			WebviewActivity activity = mWebviewReference.get();

			CoinbeneShareParam param = new CoinbeneShareParam();
			param.setTitle("CoinBene");
			param.setMessage(activity.getString(R.string.ranked_match_title));
			param.setUrl(UrlUtil.getContractRankingUrl());
			param.setImageUrl(Constants.img_url);
			if (activity != null) {
				activity.showShareDialog(param);
			}
		} else if (shareType.equals(SHARE_TYPE_INVITE)) {
			//邀请返金活动分享
			doShareInviteRebate();
		} else if (shareType.equals(SHARE_TYPE__MINING)) {
			//合约挖矿
			if (mWebviewReference == null) {
				return;
			}

			getMiningDetails();

		} else if (shareType.equals(SHARE_TYPE_POSTER)) {
			showPosterShare(url);
		}
	}

	private void showPosterShare(String url) {
		ShareMenu shareMenu = DialogManager.getShareMenu(mWebviewReference.get());
		shareMenu.setShareType(Platform.SHARE_IMAGE);
		shareMenu.setImageBase64(url);
		shareMenu.show();
	}

	public void doShare(CoinbeneShareParam shareParam) {
		if (mWebviewReference == null) {
			return;
		}
		WebviewActivity activity = mWebviewReference.get();
		if (activity != null) {
			activity.showShareDialog(shareParam);
		}
	}


	private void doShareInviteRebate() {
		if (mWebviewReference == null) {
			return;
		}
		getInviteInfo();
	}

	/**
	 * 邀请返金弹窗
	 */
	private void getInviteInfo() {
		int userId = UserInfoController.getInstance().getUserInfo().userId;

		OkGo.<InviteIndoModel>get(Constants.INVITE_QUERY_ASSET).params("userId", userId).tag(this).execute(new NewJsonSubCallBack<InviteIndoModel>() {
			@Override
			public void onSuc(Response<InviteIndoModel> response) {
				InviteIndoModel.DataBean data = response.body().getData();
				if (data == null) {
					return;
				}

				//显示分享dialog
				if (mWebviewReference == null) {
					return;
				}
				WebviewActivity activity = mWebviewReference.get();
				InviteShareDialog dialog = new InviteShareDialog(activity, data.getHash());
				dialog.setOwnerActivity(activity);
				dialog.show();
			}

			@Override
			public void onE(Response<InviteIndoModel> response) {

			}
		});
	}

	// Navigator 相关的action
	private void performNavigatorAction(NavigatorAction action, CallBackFunction callBackFunction) {
		if (mWebviewReference == null) {
			return;
		}
		WebviewActivity activity = mWebviewReference.get();
		switch (action.getAction().toLowerCase()) {
			case NAVIGATOR_CLOSE_WEBAPP:
				if (activity != null) {
					activity.finish();
				}
				break;
			case NAVIGATOR_GO_BACK:
				if (activity != null) {
					activity.goBack();
				}
				break;
			case NAVIGATOR_SET_TITLE:
				if (activity != null) {
					activity.setWebTitle(action.getTitle());
				}
				break;
			case NAVIGATOR_SET_RIGHT:
				if (activity != null) {
					if (!TextUtils.isEmpty(action.getIcon()) && "share".equals(action.getIcon())) {
						activity.setRightImage();
					} else {
						activity.setRightText(action.getText());
					}
					if (!TextUtils.isEmpty(action.getUrl()) && action.getUrl().startsWith("coinbene://")) {
						activity.setActionUrl(action.getUrl());
					}
				}
			default:
				break;
		}
	}

	// Native 相关的action
	private void performNativeAction(NativeAction action, CallBackFunction callBackFunction) {
		if (mWebviewReference == null) {
			return;
		}
		WebviewActivity activity = mWebviewReference.get();
		switch (action.getAction().toLowerCase()) {
			case NATIVE_SHOWLOADING:
				if (activity != null) {
					activity.displayProgress();
				}
				break;
			case NATIVE_HIDELOADING:
				if (activity != null) {
					activity.hideProgress();
				}
				break;
			case NATIVE_SHARE:
				if (activity != null) {

					if (SHARE_TYPE_POSTER.equals(action.getType())) {
						Bundle bundle = new Bundle();
						bundle.putString("url", action.getUrl());
						UIBusService.getInstance().openUri(activity, "coinbene://share?type=" + SHARE_TYPE_POSTER, bundle);
						return;
					}

					CoinbeneShareParam param = new CoinbeneShareParam();
					param.setTitle(action.getTitle());
					param.setMessage(action.getMessage());
					param.setUrl(action.getUrl());
					activity.showShareDialog(param);
				}
				break;
			case NATIVE_GOTOPAGE:
				if (activity != null) {
					Params params = action.getParams();
					Bundle bundle = new Bundle();
					bundle.putParcelable("params", params);
					bundle.putString("SchemeFrom", SchemeFrom.APP_HTML.name());
					UIBusService.getInstance().openUri(activity, action.getPageName(), bundle);
				}
				break;
			case NATIVE_COPY:
				if (activity != null) {
					String text = action.getContent();
					if (!TextUtils.isEmpty(text)) {
						ClipboardManager cmb = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
						if (cmb != null) {
							cmb.setText(text);
							callBackFunction.onCallBack("ok");
						} else {
							callBackFunction.onCallBack("");
						}
					}
				}
				break;
			case NATIVE_SET_REFRESH_STATUS:
				if (activity != null) {
					Params params = action.getParams();
					activity.setRefreshEnable("1".equals(params.getType()));
				}
				break;
			default:
				break;
		}
	}


	// User 相关的action
	private void performUserAction(UserAction action, final CallBackFunction callBackFunction) {
		if (mWebviewReference == null) {
			return;
		}
		final WebviewActivity activity = mWebviewReference.get();
		switch (action.getAction().toLowerCase()) {
			case USER_LOGIN:
				if (activity != null) {
					activity.setCallBackFunction(callBackFunction);
					UIBusService.getInstance().openUri(activity, "coinbene://login", null);
				}
				break;
			case USER_GET_USER:
				if (activity != null) {
					callBackFunction.onCallBack(SpUtil.getUserResponse());
				}
				break;
			default:
				break;
		}
	}

	// Device 相关的action
	private void performDeviceAction(DeviceAction action, CallBackFunction callBackFunction) {
		if (mWebviewReference == null) {
			return;
		}
		WebviewActivity activity = mWebviewReference.get();
		DeviceResult result = new DeviceResult();
		switch (action.getAction().toLowerCase()) {
			case DEVICE_GET_APP_VERSION:
				if (activity != null) {
					result.setAppVersion(AppUtil.getVersionName(activity));
				}

				break;
			case DEVICE_GET_CURRENT_LANGUAGE:
				if (activity != null) {
					String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
					result.setCurrentLanguage(LanguageHelper.getProcessedCode(localeCode));
				}
				break;
			case DEVICE_GET_CURRENT_SITE:
				if (activity != null) {
					result.setCurrentSite(SiteController.getInstance().getSiteName());
				}
				break;
			default:
				break;
		}
		callBackFunction.onCallBack(activity.getGson().toJson(result));
	}


	/**
	 * 获取累计挖矿明细
	 */
	private void getMiningDetails() {

		final WebviewActivity activity = mWebviewReference.get();

		OkGo.<MiningSummaryResponse>get(Constants.CONTRACT_MINING_SUMMARY).tag(this).execute(new DialogCallback<MiningSummaryResponse>(activity) {
			@Override
			public void onSuc(Response<MiningSummaryResponse> response) {
				if (response.body() == null || response.body().getData() == null) {
					return;
				}

				String tokenAmount = response.body().getData().getTokenAmount();

				if (TextUtils.isEmpty(tokenAmount)) {
					return;
				}

				WsMarketData cftusdt = NewMarketWebsocket.getInstance().getContractMarketMap().get("CFTUSDT");
				String valuation = "0";
				if (cftusdt != null) {
					String n = cftusdt.getLastPrice();
					if (!TextUtils.isEmpty(n) && !TextUtils.isEmpty(tokenAmount)) {
						valuation = BigDecimalUtils.multiplyDown(n, tokenAmount, 2);
					}
				}

				MiningShareDialog shareDialog = new MiningShareDialog(activity);
				shareDialog.setAddUp(tokenAmount);
				shareDialog.setValuation(valuation);
				shareDialog.show();
			}

			@Override
			public void onE(Response<MiningSummaryResponse> response) {

			}
		});
	}
}
