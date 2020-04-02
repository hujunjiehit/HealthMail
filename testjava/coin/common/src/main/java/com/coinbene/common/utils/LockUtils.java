package com.coinbene.common.utils;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.manbiwang.service.RouteHub;

/**
 * Created by june
 * on 2019-12-23
 */
public class LockUtils {

	/**
	 * 拉起解锁页面
	 */
	public static void showLockPage(Context context){
		showLockPage(context, "");
	}

	/**
	 * 拉起解锁页面
	 * @param routeUrl 解锁成功之后跳转的url
	 */
	@AddFlowControl
	public static void showLockPage(Context context, String routeUrl) {
		if (UserInfoController.getInstance().isSetFingerPrint() && !UserInfoController.getInstance().isGesturePwdSet()) {
			//只设置指纹
			ARouter.getInstance().build(RouteHub.User.fingerprintCheckActivity)
					.withString("routeUrl", routeUrl)
					.navigation(context);
		} else {
			ARouter.getInstance().build(RouteHub.User.patternCheckActivity)
					.withString("routeUrl", routeUrl)
					.navigation(context);
		}
	}
}
