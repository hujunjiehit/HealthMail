package com.coinbene.manbiwang.push;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.NotificationUtils;
import com.coinbene.manbiwang.MainActivity;
import com.coinbene.manbiwang.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;


/**
 * ding
 * 2019-08-22
 * com.coinbene.manbiwang.push
 */
public class PushReceiver extends XGPushBaseReceiver {
	public final String channelName = "CoinBene";
	public final String channelID = "push";
	private String url = "";
	private String content = "";

	@Override
	public void onRegisterResult(Context context, int i, XGPushRegisterResult xgPushRegisterResult) {

	}

	@Override
	public void onUnregisterResult(Context context, int i) {

	}

	@Override
	public void onSetTagResult(Context context, int i, String s) {

	}

	@Override
	public void onDeleteTagResult(Context context, int i, String s) {

	}

	@Override
	public void onTextMessage(Context context, XGPushTextMessage xgPushTextMessage) {

		/**
		 * 小米手机
		 * xgPushTextMessage.getContent() = test-value
		 *
		 * xgPushTextMessage.getCustomContent() = {"msgId":"1177449752","mat":"1568696645365","k":"v","__m_ts":"1568696645334","mrt":"1568696645334"}
		 */


		/**
		 * 魅族手机、oppo、三星
		 * xgPushTextMessage.getContent() = test-value
		 *
		 * xgPushTextMessage.getCustomContent() = {"k":"v"}
		 */


		/**
		 * 华为手机
		 * xgPushTextMessage.getContent() = {"content":"test-value"}
		 *
		 * xgPushTextMessage.getCustomContent() = ""
		 */



		//  通知权限未打开，跳转打开通知权限设置
//		if (!XGPushManager.isNotificationOpened(context)) {
//			openNotificationSettings(channelID, context);
//		}

		JsonParser jsonParser = new JsonParser();
		if (xgPushTextMessage.getContent().contains("{")) {
			//华为的消息，json解析   {"content":"test-value"}
			JsonElement jsonElement = jsonParser.parse(xgPushTextMessage.getContent());
			if (jsonElement instanceof JsonObject) {
				content = jsonElement.getAsJsonObject().get("content").toString();
			}
		} else {
			content = xgPushTextMessage.getContent();
		}


		if (!content.contains("|")) {
			return;
		}


		//华为的content会有""，去掉开始和结尾的引号
		if (content.startsWith("\"") && content.endsWith("\"")) {
			content = content.substring(1, content.length() - 1);
		}

		String[] array = content.split("\\|");

		content = array[0];     //通知内容

		//跳转协议
		if (array.length > 1) {
			url = array[1];
		}


		//跳转待定意图
		Intent intent = new Intent(context, MainActivity.class);
		intent.setData(Uri.parse(url));
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 1222, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		//透传消息
		new NotificationUtils.Builder(context, channelID, channelName)
				.setTitle("CoinBene")
				.setContentText(content)
				.setChannelDes("用于Push推送")
				.setContentIntent(pendingIntent)
				.setVisibility(Notification.VISIBILITY_PUBLIC)
				.setImportance(NotificationManagerCompat.IMPORTANCE_MAX)
				.setSmallIcon(R.mipmap.launcher_logo)
				.setPriority(NotificationCompat.PRIORITY_MAX)
				.setAutoCancel(true)
				.creat();
	}


	@Override
	public void onNotifactionClickedResult(Context context, XGPushClickedResult xgPushClickedResult) {

	}

	@Override
	public void onNotifactionShowedResult(Context context, XGPushShowedResult xgPushShowedResult) {

	}

	/**
	 * @param channel 通知渠道ID
	 *                打开通知设置
	 */
	public void openNotificationSettings(String channel, Context context) {
		Intent intent = new Intent();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
			if (channel != null) {
				intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
				intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
			} else {
				intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			}
			intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			if (channel != null) {
				intent.setAction(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
				intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
			} else {
				intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			}
			intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
			intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
			intent.putExtra("app_package", context.getPackageName());
			intent.putExtra("app_uid", context.getApplicationInfo().uid);
		} else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			intent.addCategory(Intent.CATEGORY_DEFAULT);
			intent.setData(Uri.parse("package:" + context.getPackageName()));
		}
		context.startActivity(intent);
	}
}
