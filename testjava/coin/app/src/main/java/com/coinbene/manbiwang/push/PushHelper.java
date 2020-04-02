package com.coinbene.manbiwang.push;

import android.app.Application;


import com.coinbene.common.context.CBRepository;

import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

/**
 * ding
 * 2019-12-30
 * com.coinbene.manbiwang.push
 */
public class PushHelper {
	static final String TAG = "PushHelper";
	private static boolean ENABLE = true;

	public static void init(Application application) {
		if (!ENABLE) {
			return;
		}

		XGPushConfig.enableFcmPush(application, true);
		XGPushConfig.enableOtherPush(application, true);
		XGPushConfig.setOppoPushAppId(application, getOppoPushAppId());
		XGPushConfig.setOppoPushAppKey(application, getOppoPushAppKey());
		XGPushConfig.setMiPushAppId(application, getMiPushAppId());
		XGPushConfig.setMiPushAppKey(application, getMiPushAppKey());
		XGPushConfig.setMzPushAppId(application, getMzPushAppId());
		XGPushConfig.setMzPushAppKey(application, getMzPushAppKey());
		XGPushConfig.enableDebug(application, false);
		XGPushConfig.setReportNotificationStatusEnable(application, true);
		XGPushConfig.setReportApplistEnable(application, true);
		XGPushConfig.setHuaweiDebug(false);

		//默认注册
		XGPushManager.registerPush(application, new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				//token在设备卸载重装的时候有可能会变
				DLog.d(TAG, "注册成功，设备token为：" + data);
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				DLog.d(TAG, "注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
		});

		//韩语下推送有开关，未打开push功能解除注册
		if (LanguageHelper.isKorean(application) && !SpUtil.get(application, "IsPush", false)) {
			XGPushManager.unregisterPush(application);
		}
	}

	/**
	 * 绑定账户
	 *
	 * @param uid 用户ID
	 */
	public static void bindAccount(String uid) {
		if (!ENABLE) {
			return;
		}

		XGPushManager.bindAccount(CBRepository.getContext(), uid);
	}

	//-------- 小米推送渠道----------------
	private static String getMiPushAppId() {
		return "2882303761517904122";
	}

	private static String getMiPushAppKey() {
		return "5321790481122";
	}


	//--------- 魅族推送渠道 ---------------
	private static String getMzPushAppId() {
		return "123444";
	}

	private static String getMzPushAppKey() {
		return "92b46f9340b44ce48601b272f0273959";
	}


	//-------------oppo推送渠道--------------
	private static String getOppoPushAppId() {
		return "";
	}

	private static String getOppoPushAppKey() {
		return "";
	}
}
