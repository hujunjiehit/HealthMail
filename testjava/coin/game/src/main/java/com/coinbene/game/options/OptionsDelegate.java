package com.coinbene.game.options;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.Keep;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.balance.TransferParams;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.manbiwang.model.http.OptionsUserInfo;
import com.coinbene.manbiwang.model.http.SettleModel;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.balance.Product;
import com.fota.android.commonlib.base.AppConfigs;
import com.fota.android.commonlib.utils.SharedPreferencesUtil;
import com.fota.option.OptionConfig;
import com.fota.option.OptionManager;
import com.fota.option.OptionSdkActivity;
import com.fota.option.bean.OptionMenuItem;
import com.fota.option.bean.OptionMenuKey;
import com.fota.option.websocket.data.AccountInfo;
import com.fota.option.websocket.data.OptionTransfer;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 期权管理类
 */
@Keep
public final class OptionsDelegate implements OptionConfig.SettleListener, OptionConfig.OrderListener, OptionConfig.LoginPageChangeListener, OptionConfig.AllOrderPageChangeListener, OptionConfig.DepositPageChangeListener {

	static final String TAG = "OptionsDelegate";
	public static final int CONI_CODE = 13;
	public static final int ETH_CODE = 3;
	public static final int BTC_CODE = 2;
	public static final int USDT_CODE = 15;
	public static final int SIMULATE_CODE = 999;

	private static OptionsDelegate delegate = new OptionsDelegate();

	private OptionsDelegate() {
		initConfig();
	}

	public static OptionsDelegate init() {
		return delegate;
	}

	/**
	 * 初始FOTA
	 */
	public void initConfig() {
		DLog.d(TAG, "初始化FOTA配置");
		SharedPreferencesUtil.init(CBRepository.getContext());
		//关闭新手引导页面
		SpUtil.closeFOTAGuide();
		OptionConfig optionConfig = new OptionConfig();
		optionConfig.setLogEnable(false);
		optionConfig.setSettleListener(this);
		optionConfig.setOrderListener(this);
		optionConfig.setLoginPageChangeListener(this);
		optionConfig.setAllOrderPageChangeListener(this);
		optionConfig.setDepositPageChangeListener(this);
		optionConfig.setShowShareMenu(false);
		optionConfig.setShowLanguageChangedMenu(true);

		//去除对应菜单 设置setOptionMenuItems 注释掉对应菜单Items即可
		List<OptionMenuItem> optionMenuItems = new ArrayList<>();
		optionMenuItems.add(new OptionMenuItem(OptionMenuKey.MENU_BACK, com.fota.option.R.mipmap.icon_back, com.fota.option.R.mipmap.icon_back));//返回
		optionMenuItems.add(new OptionMenuItem(OptionMenuKey.MENU_TRADE_HISTORY, com.fota.option.R.mipmap.left_menu_history_on, com.fota.option.R.mipmap.left_menu_history_off));//历史交易记录
		optionMenuItems.add(new OptionMenuItem(OptionMenuKey.MENU_SPOT_INDEX, com.fota.option.R.mipmap.left_menu_index_on, com.fota.option.R.mipmap.left_menu_index_off));//指数
//		optionMenuItems.add(new OptionMenuItem(OptionMenuKey.MENU_RANKING_LIST, com.fota.option.R.mipmap.left_munu_rank_on, com.fota.option.R.mipmap.left_munu_rank_off));//排行榜
//        optionMenuItems.add(new OptionMenuItem(OptionMenuKey.MENU_VIDEO_LIST, com.fota.option.R.mipmap.left_menu_video_on, com.fota.option.R.mipmap.left_menu_video_off));//教学视屏
		optionConfig.setOptionMenuItems(optionMenuItems);

		if (CBRepository.getCurrentEnvironment().environmentType == Constants.ONLINE_ENVIROMENT) {
			optionConfig.setDevelopment(false);
		} else {
			optionConfig.setDevelopment(true);
		}

		//语言配置
		if (LanguageHelper.isChinese(CBRepository.getContext())) {
			AppConfigs.setLanguege(AppConfigs.LANGAUGE_SIMPLE_CHINESE);
		} else if (LanguageHelper.isKorean(CBRepository.getContext())) {
			AppConfigs.setLanguege(AppConfigs.LANGAUGE_KOREAN);
		} else {
			AppConfigs.setLanguege(AppConfigs.LANGAUGE_ENGLISH);
		}

		OptionManager.init(Constants.OPTIONS_BROKER_ID, optionConfig, (Application) CBRepository.getContext());
	}


	/**
	 * 结算回调
	 */
	@Override
	public void settleCallback(Activity activity, String data) {
		try {
			Gson gson = new Gson();
			SettleModel settleModel = gson.fromJson(data, SettleModel.class);
			collectSettleData(settleModel);
			if (settleModel != null && Double.valueOf(settleModel.getProfit()) >= 0) {
				OptionSdkActivity compatActivity = (OptionSdkActivity) activity;
				OptionShareFragment shareFragment = OptionShareFragment.init(settleModel);
				shareFragment.show(compatActivity.getSupportFragmentManager(), "OptionShare");
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void orderCallback(Activity activity, OptionTransfer optionTransfer, String currency, String erro) {
		if (!TextUtils.isEmpty(erro)) {
			return;
		}

		//模拟账户不收集数据
		if (currency.equals("VFOTA")) {
			return;
		}

		if (optionTransfer == null || TextUtils.isEmpty(currency)) {
			return;
		}

		collectOrderData(optionTransfer, currency);
	}

	@Override
	@NeedLogin(jump = true)
	public void gotoLoginPage(Activity activity) {
		login();
	}

	@Override
	@NeedLogin(jump = true)
	public void gotoAllOrderPage(Activity activity, AccountInfo accountInfo, boolean b) {
		DLog.d(TAG, "启动完整交易记录页面");
//		OptionRecordActivity.startMe(activity);
	}

	@Override
	@NeedLogin(jump = true)
	public void gotoDepositPage(Activity activity, AccountInfo accountInfo, boolean b) {
		if (SwitchUtils.isOpenOPT_Asset()) {
			//AccountTransferActivity.startActivity(activity, Account.ACCOUNT_OPTIONS);
			UIBusService.getInstance().openUri(activity, UrlUtil.getCoinbeneUrl(UIRouter.HOST_TRANSFER),
					new TransferParams()
							.setAsset("BTC")
							.setFrom(Product.NAME_SPOT)
							.setTo(Product.NAME_OPTIONS)
							.toBundle());
		}
	}

	/**
	 * 打开期权页面
	 */
	public static void startOptions(Context context) {
		Intent intent = new Intent(context, OptionSdkActivity.class);
		context.startActivity(intent);
	}


	/**
	 * 收集结算数据
	 */
	private void collectSettleData(SettleModel settleModel) {
		if (settleModel == null) {
			return;
		}

		int userId = UserInfoController.getInstance().getUserInfo().userId;
		HttpParams params = new HttpParams();
		params.put("assetCode", settleModel.getAssetCode());
		params.put("currency", settleModel.getAccountCurrency());
		params.put("profit", settleModel.getProfit());

		OkGo.<BaseRes>post(Constants.OPTIONS_SETTLE_ORDER).headers("userId", String.valueOf(userId)).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
			}

			@Override
			public void onE(Response<BaseRes> response) {
			}
		});
	}

	/**
	 * 收集下单数据
	 */
	private void collectOrderData(OptionTransfer optionTransfer, String currency) {
		int userId = UserInfoController.getInstance().getUserInfo().userId;
		HttpParams params = new HttpParams();
		params.put("asset", optionTransfer.getAsset());
		params.put("direction", optionTransfer.getDirection());
		params.put("investmentAmount", optionTransfer.getInvestmentAmount());
		params.put("optionType", optionTransfer.getOptionType());
		params.put("strikePrice", optionTransfer.getStrikePrice());
		params.put("totalProfit", optionTransfer.getTotalProfit());
		params.put("currency", currency);

		OkGo.<BaseRes>post(Constants.OPTIONS_PLACE_ORDER_SUC).headers("userId", String.valueOf(userId)).params(params).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				DLog.d(TAG, "订单数据收集");
			}

			@Override
			public void onE(Response<BaseRes> response) {
			}
		});
	}


	/**
	 * 期权登录
	 */
	public static void login() {
		HttpParams params = new HttpParams();
		params.put("refresh", "true");
		OkGo.<OptionsUserInfo>post(Constants.OPTIONS_LOGIN).params(params).execute(new NewJsonSubCallBack<OptionsUserInfo>() {
			@Override
			public void onSuc(Response<OptionsUserInfo> response) {
				OptionsUserInfo.DataBean data = response.body().getData();
				if (data == null) {
					return;
				}
				//token|FOTA uid 不存在
				if (TextUtils.isEmpty(data.getFotauid()) || TextUtils.isEmpty(data.getToken())) {
					return;
				}
				OptionManager.setUserIdAndToken(data.getFotauid(), data.getToken());
			}

			@Override
			public void onE(Response<OptionsUserInfo> response) {
			}
		});
	}
}
