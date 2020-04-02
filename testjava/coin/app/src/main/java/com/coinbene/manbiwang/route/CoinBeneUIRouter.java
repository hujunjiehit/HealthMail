package com.coinbene.manbiwang.route;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.Params;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.CoinbeneShareParam;
import com.coinbene.manbiwang.BuildConfig;
import com.coinbene.manbiwang.MainActivity;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.balance.activity.spot.DepositActivity;
import com.coinbene.manbiwang.balance.transfer.TransferActivity;
import com.coinbene.manbiwang.kline.contractkline.ContractKlineActivity;
import com.coinbene.manbiwang.kline.newspotkline.NewContractKlineActivity;
import com.coinbene.manbiwang.kline.newspotkline.NewSpotKlineActivity;
import com.coinbene.manbiwang.kline.spotkline.SpotKlineActivity;
import com.coinbene.manbiwang.model.http.KycStatusModel;
import com.coinbene.manbiwang.modules.common.parser.SchemaParser;
import com.coinbene.manbiwang.record.RecordActivity;
import com.coinbene.manbiwang.record.coinrecord.TransferRecordActivity;
import com.coinbene.manbiwang.record.coinrecord.WithDrawRechargeHisActivity;
import com.coinbene.manbiwang.record.contractrecord.CurrentDelegationBtcActivity;
import com.coinbene.manbiwang.record.contractrecord.FundCostBtcActivity;
import com.coinbene.manbiwang.record.contractrecord.HistoryDelegationBtcActivity;
import com.coinbene.manbiwang.record.miningrecord.MiningRecordActivity;
import com.coinbene.manbiwang.record.optionrecord.OptionRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.CurrentEntrustRecordActivity;
import com.coinbene.manbiwang.record.orderrecord.HistoryEntrustRecordActivity;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.safe.AuthActivity;
import com.coinbene.manbiwang.user.safe.AuthProcessingOrVerifiedActivity;
import com.coinbene.manbiwang.webview.WebviewActivity;
import com.coinbene.manbiwang.webview.WebviewDelegate;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Locale;

import cn.udesk.UdeskSDKManager;
import cn.udesk.config.UdeskConfig;
import udesk.core.LocalManageUtil;
import udesk.core.UdeskConst;

public class CoinBeneUIRouter implements UIRouter {

	@Override
	public boolean openUri(Context context, String url, Bundle bundle, int requestCode) {
		if (!TextUtils.isEmpty(url)) {
			Uri uri = Uri.parse(url);
			return openUri(context, uri, bundle, requestCode);
		}
		return true;
	}

	@Override
	public boolean openUri(Context context, String url, Bundle bundle) {
		openUri(context, url, bundle, 0);
		return true;
	}

	@Override
	public boolean openUri(Context context, Uri uri, Bundle bundle) {
		openUri(context, uri, bundle, 0);
		return true;
	}

	@Override
	public boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode) {
		String scheme = uri.getScheme();
		if ("http".equals(scheme) || "https".equals(scheme)) {
			return false;
		} else {
			if (!UIRouter.COINBENE_SCHEME.equals(scheme.toLowerCase(Locale.US))) {
				return false;
			}
			handleCustomUri(context, uri, bundle, requestCode);
			return true;
		}
	}

	/**
	 * @param context
	 * @param uri
	 * @param bundle
	 * @param requestCode requestCode > 0 需要调用startActivityForResult
	 */
	public void handleCustomUri(Context context, Uri uri, Bundle bundle, int requestCode) {
		if (uri == null) {
			return;
		}

		//  以 coinbene:// 开始的uri才会走到这里
		String host = uri.getHost().toLowerCase(Locale.US);
		HashMap<String, String> map = UrlUtil.parseParams(uri);

		//参数可能有两种传递情况，一种是拼接到url传过来的，另外一种是通过bundle传过来的
		if (bundle != null) {
			Params params = bundle.getParcelable("params");
			if (params != null) {
				map.put(Params.KEY_GROUP_NAME, params.getGroupName());
				map.put(Params.KEY_PAIR_NAME, params.getPairName());
				map.put(Params.KEY_SYMBOL, params.getSymbol());
				map.put(Params.KEY_TRADE_PAIR, params.getTradePair());
				map.put(Params.KEY_CNT_PRECISION, params.getCntPrecision() + "");
				map.put(Params.KEY_PRICE_PRECISION, params.getPricePrecision() + "");
				map.put(Params.KEY_TYPE, params.getType() + "");
				map.put(Params.KEY_ASSET, params.getAsset() + "");
				map.put(Params.KEY_FROM, params.getFrom() + "");
				map.put(Params.KEY_TO, params.getTo() + "");
			}
			for (String key : bundle.keySet()) {
				if (bundle.get(key) instanceof String) {
					map.put(key, bundle.getString(key));
				}
			}
		}

		if (host.equals(HOST_RECORDS)) {
			parseRecordsUri(context, uri, map);
			return;
		}

		switch (host) {
			case HOST_LOGIN:
				gotoLoginOrLock();
				break;
			case HOST_INVITE_REBATE: {
				//跳转到邀请返金webview
				String url = UrlUtil.getInviteUrl();
				if (bundle == null) {
					bundle = new Bundle();
				}
				bundle.putBoolean("isInviteRebate", true);
				bundle.putString("title", context.getResources().getString(R.string.invite_activity_title));
				UIBusService.getInstance().openUri(context, url, bundle);
				break;
			}
			case HOST_SHARE:
				if (map.get(Params.KEY_TYPE) == null) {
					CoinbeneShareParam param = new CoinbeneShareParam();
					param.setTitle(map.get("title"));
					param.setMessage(map.get("message"));
					param.setUrl(map.get("url"));
					param.setImageUrl(map.get("thumbimage"));
					WebviewDelegate.getInstance().doShare(param);
				} else {
					if (map.get(Params.KEY_TYPE) instanceof String) {
						WebviewDelegate.getInstance().doShare(map.get("type"), map.get("url"));
					}
				}
				break;
			case HOST_RANKING: {
				String url = UrlUtil.getContractRankingUrl();
				if (bundle == null) {
					bundle = new Bundle();
				}
				bundle.putString(WebviewActivity.EXTRA_TITLE, context.getResources().getString(R.string.ranked_match_title));
				bundle.putInt(WebviewActivity.EXTRA_RIGHT_IMAGE, R.drawable.icon_share_grey);
				bundle.putString(WebviewActivity.EXTRA_RIGHT_URL, "coinbene://" + UIRouter.HOST_SHARE + "?type=" + WebviewDelegate.SHARE_TYPE_RANK);
				UIBusService.getInstance().openUri(context, url, bundle);
				break;
			}
			case HOST_CUSTOMER:
				//拉起在线客服
				if (LanguageHelper.isChinese(context)) {
					LocalManageUtil.saveSelectLanguage(context, Locale.CHINA);
				} else {
					LocalManageUtil.saveSelectLanguage(context, Locale.US);
				}
				String androidID = AppUtil.getAndroidID(context);
				UdeskConfig udeskConfig = makeBuider();

				//在线客服
				UdeskSDKManager.getInstance().entryChat(context, udeskConfig, androidID);
				break;

			case HOST_RECHARGE:
				//充币
				if (TextUtils.isEmpty(map.get("asset"))) {
					return;
				}
				DepositActivity.startMe(context, map.get("asset"));
				break;

			case HOST_TRANSFER:
				//划转

				gotoTransfer(context, uri, map);
				break;

			case HOST_YBBTRANSFER:
				//余币宝划转
				gotoYbbTransfer(context, uri, map);
				break;

			case HOST_KYC:
				//跳转到实名认证
				gotoKYC(context);
				break;

			case HOST_GAME_SHARE:
				String imgBase64 = uri.getQueryParameter("data");
				WebviewDelegate.getInstance().showGameShare(imgBase64);
				break;

			case HOST_NAVIGATOR:
				WebviewDelegate.getInstance().dispatchNavigatorAction(map.get("action"));
				break;

			case HOST_CHANGE_TAB:
				//切换Tab
				//coinbene://changetab?tab=contract&subTab=btc&symbol=BTCUSDT
				Intent intent = new Intent(context, MainActivity.class);
				Bundle params = new Bundle();
				params.putString("tab", map.get("tab"));
				params.putString("subTab", map.get("subTab"));
				params.putString("symbol", map.get("symbol"));
				params.putString("type", map.get("type"));
				intent.putExtras(params);
				context.startActivity(intent);
				break;

			case HOST_SPOTKLINE:
				//现货K线， coinbene://SpotKline?pairName=ETH/USDT
				String pairName = map.get(Params.KEY_PAIR_NAME);
				if (SpUtil.getEnableNewKline()) {
					//新版k线
					//ToastUtil.show("newKline");
					NewSpotKlineActivity.startMe(context, pairName);
				} else {
					//旧版K线
					SpotKlineActivity.startMe(context, pairName);
				}
				break;

			case HOST_CONTRACT_KLINE:
				//合约K线， coinbene://ContractKline?symbol=ETHUSDT
				String symbol = map.get(Params.KEY_SYMBOL);
				if (SpUtil.getEnableNewKline()) {
					//新版k线
					//ToastUtil.show("newKline");
					NewContractKlineActivity.startMe(context, symbol);
				} else {
					//旧版K线
					ContractKlineActivity.startMe(context, symbol);
				}
				break;

			case HOST_FORTUNE_DETAIL:
				ARouter.getInstance().build(RouteHub.Fortune.fortuneActivity).navigation(context);
				break;

			case HOST_HELP_CENTER:
				UIBusService.getInstance().openUri(context, UrlUtil.getHelpCenterUrl(), null);
				break;

			default:
				SchemaParser.handleSchema(context, uri.getScheme() + "://" + uri.getHost(), map);
				break;
		}
	}
	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	/**
	 * 跳转到余币宝转入页面
	 * @param context
	 * @param uri
	 * @param map
	 */
	@NeedLogin(jump = true)
	private void gotoYbbTransfer(Context context,  Uri uri, HashMap<String, String> map) {
		ARouter.getInstance().build(RouteHub.Fortune.ybbTransferActivity)
				.withInt("type", "in".equals(map.get("type")) ? RouteHub.Fortune.TRANSFER_TYPE_IN : RouteHub.Fortune.TRANSFER_TYPE_OUT)
				.withString("asset", map.get("asset"))
				.navigation(context);
	}

	/**
	 * 跳转到划转页面
	 *
	 * @param context
	 * @param map     coinbene://Transfer?from=margin&to=contract&asset=BTC&fromsymbol=BTC/USDT&tosymbol=LTC/USDT
	 */
	@NeedLogin(jump = true)
	private void gotoTransfer(Context context, Uri uri, HashMap<String, String> map) {
		//AccountTransferActivity.startActivity(context);
//
//		if (!CommonUtil.isLoginAndUnLocked()) {
//			//未登陆或者锁住状态需要跳转到登陆或者解锁，然后再继续跳转
//			UtilsApp.goToPopLoginActivity(context);
//			return;
//		}

		UserInfoTable userInfoTable = UserInfoController.getInstance().getUserInfo();
		if (userInfoTable != null && userInfoTable.fundTrans == false) {
			//该用户被禁止划转
			AlertDialog.Builder dialog = new AlertDialog.Builder(context);
			dialog.setMessage(R.string.res_disable_transfer_desc);
			dialog.setPositiveButton(context.getText(R.string.btn_ok), (dialog1, which) -> dialog1.dismiss());
			dialog.show();
			return;
		}

		//猜涨跌使用旧的划转
//		if (Product.NAME_OPTIONS.equals(map.get("from")) || Product.NAME_OPTIONS.equals(map.get("to"))) {
//			AccountTransferActivity.startActivity(context, Account.ACCOUNT_OPTIONS);
//			return;
//		}

		TransferActivity.startMe(context,
				new TransferParams()
						.setAsset(map.get("asset"))
						.setFrom(map.get("from"))
						.setTo(map.get("to"))
						.setFromSymbol(map.get("fromsymbol"))
						.setToSymbol(map.get("tosymbol"))
						.toBundle());
	}

	/**
	 * 跳转到kyc界面
	 *
	 * @param context
	 */
	private void gotoKYC(Context context) {
		OkGo.<KycStatusModel>get(Constants.KYC_GET_STATUS).tag(this).execute(new NewJsonSubCallBack<KycStatusModel>() {
			@Override
			public void onSuc(Response<KycStatusModel> response) {
				KycStatusModel baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (baseResponse.getData() != null) {
						int authStutes = baseResponse.getData().getStatus();
						if (authStutes == Constants.AUTH_PROCESSING || authStutes == Constants.AUTH_VERIFIED) {//审核中  已通过   跳单独的界面
							AuthProcessingOrVerifiedActivity.startMe(context, authStutes);
						} else {
							AuthActivity.startMe(context);
						}
					}
				}
			}

			@Override
			public void onE(Response<KycStatusModel> response) {
			}
		});

		return;
	}

	/**
	 * 处理记录页面相关的schema
	 *
	 * @param context
	 * @param uri
	 * @param map
	 */
	@NeedLogin(jump = true)
	private void parseRecordsUri(Context context, Uri uri, HashMap<String, String> map) {
//		if (!CommonUtil.isLoginAndUnLocked()) {
//			//未登陆或者锁住状态需要跳转到登陆或者解锁，然后再继续跳转
//			UtilsApp.goToPopLoginActivity(context, uri.toString());
//			return;
//		}

		if (TextUtils.isEmpty(uri.getPath())) {
			//跳到记录列表页
			RecordActivity.startMe(context);
			return;
		}

		switch ((uri.getHost() + uri.getPath()).toLowerCase()) {
			case HOST_RECORDS_RECHARGE:
				//充币记录
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_RECHARGE_TYPE);
				break;
			case HOST_RECORDS_WITHDRAW:
				//提币记录
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_WITHDRAW_TYPE);
				break;
			case HOST_RECORDS_TRANSFER:
				//转账记录
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_TRANSFER_TYPE);
				break;
			case HOST_RECORDS_TURN:
				//划转记录
				TransferRecordActivity.startMe(context);
				break;
			case HOST_RECORDS_DISTRIBUTE:
				//其它记录
				WithDrawRechargeHisActivity.startMe(context, Constants.CODE_RECORD_DISPATCH_TYPE);
				break;
			case HOST_RECORDS_SPOT_OPENORDERS:
				//币币当前委托
				CurrentEntrustRecordActivity.startMe(context);
				break;
			case HOST_RECORDS_SPOT_HISTORYORDERS:
				//币币历史委托
				HistoryEntrustRecordActivity.startMe(context);
				break;
			case HOST_RECORDS_CONTRACT_OPENORDERS:
				//合约当前委托
				CurrentDelegationBtcActivity.startMe(context);
				break;
			case HOST_RECORDS_CONTRACT_HISTORYORDERS:
				//合约历史委托
				HistoryDelegationBtcActivity.startMe(context);
				break;
			case HOST_RECORDS_CONTRACT_FEES:
				//合约资金费用
				FundCostBtcActivity.startMe(context);
				break;
			case HOST_RECORDS_OPTION_ORDERS:
				//期权完整交易记录
				OptionRecordActivity.startMe(context);
				break;
			case HOST_RECORDS_MINING:
				//挖矿明细
				MiningRecordActivity.startMe(context);
				break;
			default:
				//未识别
				if (BuildConfig.ENABLE_DEBUG) {
					ToastUtil.show("未识别的记录页：" + uri.toString());
				}
				break;
		}
	}

	@Override
	public boolean verifyUri(Uri uri) {
		return uri != null && uri.getScheme() != null;
	}

	/**
	 * 在线客服设置并唤起
	 */
	private UdeskConfig makeBuider() {
		UserInfoTable user = UserInfoController.getInstance().getUserInfo();

		UdeskConfig.Builder builder = new UdeskConfig.Builder();

		HashMap<String, String> userMap = new HashMap<>();

		//  用户信息不为空设置用户信息
		if (user != null) {
			userMap.put(UdeskConst.UdeskUserInfo.NICK_NAME, user.loginId);
			builder.setDefualtUserInfo(userMap);
		}


		/**
		 * 1，设置在线客服标题栏背景色；
		 * 2，设置在线客服标题栏字体颜色；
		 * 3，设置在线客服返回Image资源文件；
		 */
		builder.setUdeskTitlebarBgResId(R.color.res_background)
				.setUdeskTitlebarTextLeftRightResId(R.color.res_textColor_1)
				.setUdeskbackArrowIconResId(R.drawable.res_icon_back);

		return builder.build();
	}

}
