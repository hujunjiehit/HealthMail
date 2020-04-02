package com.coinbene.manbiwang.service.app;

import android.app.Activity;
import android.content.Context;

import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.facade.template.IProvider;
import com.coinbene.manbiwang.model.http.AdResponse;
import com.coinbene.manbiwang.model.http.ZendeskArticlesResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by june
 * on 2019-11-09
 */
public interface AppService extends IProvider {
	/**
	 * 处理用户成功登陆之后的逻辑
	 */
	void onUserLoginSuccess(int tabIndex);

	void getTradePair();

	void getAdInfo(CallBack<AdResponse> callBack);

//	void getSwitch(Activity activity, CallBack<Boolean> callBack);

	void getContractList();

	void getOtcConfig();

	void getOptional();

	void getBanners();
//	void getActivitySwitch();

	void getMarginSymbolList();

	void getBalanceList();

	void getContractUsdtList();

	void updateUserInfo();

	void showPushSwitchDialog(Context context, boolean isOpen);

	void checkUpdate(Activity activity, CallBack<Boolean> callBack);

	void getPopupBanner(Activity activity);

	void getBigCoinList();

	void getHotCoinList();


	void getAppConfig(Activity activity, CallBack<Boolean> callBack);

	void getNotice();

	DrawerLayout getMainDrawerLayout();

	void setDrawerLayout(DrawerLayout drawerLayout);

	void getUserInInvitation(CallBack callBack);

	interface CallBack<T> {
		void onResult(T result);
	}
}
