package com.coinbene.common.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.coinbene.common.R;

/**
 * 通知 utils
 */
public class NotificationUtils {
	public static final int NOTIFY_TYPE_UPDATE_APP = -2;
	public static final int NOTIFY_TYPE_NOTIFICATION = -3;
	private static final String LOG_TAG = "NotificationUtils";


	/**
	 * 信道名称从此往下列
	 */
	public static final String CHANNEL_NAME_PUSH = "CoinBenePush";

	/**
	 * 信道ID从此往下列
	 */
	public static final String CHANNEL_ID_PUSH = "push";

	private NotificationUtils() {
	}

	private static int getSmallIcon() {
		return R.mipmap.launcher_logo;
	}

	public static NotificationCompat.Builder builder(Context context, Intent intent, String desc) {
		if (desc == null) {
			desc = "";
		}
		PendingIntent pendingIntent = null;
		if (intent != null) {
			pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
		BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.launcher_logo);
		return new NotificationCompat.Builder(context)
				.setContentText(desc)
				.setContentTitle(context.getString(R.string.res_app_name))
				.setSmallIcon(getSmallIcon())
				.setLargeIcon(bd.getBitmap())
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.setAutoCancel(true);
	}

	public static void send(Context context, int notifyType, Notification notification) {
		send(context, notifyType, 0, notification);
	}

	public static void send(Context context, int notifyType, long messageId, Notification notification) {
		NotificationManager manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
		manager.notify(notifyType + "", (int) messageId, notification);
	}

	//适配8.0添加channel参数  不然报错
	public static void send(Context context, int notifyType, Notification notification, NotificationChannel notificationChannel) {
		send(context, notifyType, 0, notification, notificationChannel);
	}

	//适配8.0添加channel参数  不然报错
	public static void send(Context context, int notifyType, long messageId, Notification notification, NotificationChannel notificationChannel) {
		NotificationManager manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			manager.createNotificationChannel(notificationChannel);
		}
		manager.notify(notifyType + "", (int) messageId, notification);
	}

	public static void cancelNotification(Context context, int notifyType, long messageId) {
		try {
			NotificationManager manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
			manager.cancel(notifyType + "", (int) messageId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void cancelAllNotifications(Context context) {
		try {
			NotificationManager manager = ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE));
			manager.cancelAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static class Builder {
		private Context context;
		private NotificationCompat.Builder builder;
		private String channelId, channelName, channelDes;
		private int importance = NotificationManagerCompat.IMPORTANCE_DEFAULT;

		public Builder(Context context, String channelId, String channelName) {
			this.context = context;
			this.channelId = channelId;
			this.channelName = channelName;
			this.builder = getNotificationBuilder(context, channelId);
		}

		/**
		 * @param title 通知标题
		 * @return Builder
		 * 设置通知标题
		 */
		public Builder setTitle(@NonNull String title) {
			builder.setContentTitle(title);
			return this;
		}

		/**
		 * 类型
		 */
		public Builder setCategory(String category){
			builder.setCategory(category);
			return this;
		}

		/**
		 * @param contentText 通知内容
		 * @return Builder
		 * 设置通知内容
		 */
		public Builder setContentText(@NonNull String contentText) {
			builder.setContentText(contentText);
			return this;
		}

		/**
		 * @param intent 点击通知Intent
		 * @return Builder
		 * 设置通知Intent
		 */
		public Builder setContentIntent(@NonNull PendingIntent intent) {
			builder.setContentIntent(intent);
			return this;
		}


		/**
		 * @param drawbleRes 图标资源ID
		 * @return Builder
		 * 设置通知小图标
		 */
		public Builder setSmallIcon(@DrawableRes int drawbleRes) {
			builder.setSmallIcon(drawbleRes);
			return this;
		}

		/**
		 * @param largeIcon 图标资源ID
		 * @return Builder
		 * 设置通知大图标
		 */
		public Builder setLargeIcon(@NonNull Bitmap largeIcon) {
			builder.setLargeIcon(largeIcon);
			return this;
		}

		/**
		 * @param autoCancel
		 * @return Builder
		 * 设置自动取消
		 */
		public Builder setAutoCancel(boolean autoCancel) {
			builder.setAutoCancel(autoCancel);
			return this;
		}


		/**
		 * @param priority 优先级
		 * @return Builder
		 * 设置优先级
		 */
		public Builder setPriority(@NonNull int priority) {
			builder.setPriority(priority);
			return this;
		}

		/**
		 * @param style 通知样式
		 * @return Builder
		 * 设置通知样式
		 */
		public Builder setStyle(@NonNull NotificationCompat.Style style) {
			builder.setStyle(style);
			return this;
		}

		/**
		 * @param visibility
		 * @return 设置通知显示模式
		 */
		public Builder setVisibility(@NonNull int visibility) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				builder.setVisibility(visibility);
			}
			return this;
		}

		/**
		 * @param channelDes
		 * @return 设置信道描述（可不设置）
		 */
		public Builder setChannelDes(@NonNull String channelDes) {
			this.channelDes = channelDes;
			return this;
		}


		/**
		 * @param importance 设置通知信道级别
		 *                   默认Default
		 */
		public Builder setImportance(int importance) {
			this.importance = importance;
			return this;
		}

		/**
		 * 创建通知
		 */
		public void creat() {

			//Android 8以上设置通知信道
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				NotificationUtils.createNotificationChannel(context, channelId, channelName, importance, channelDes);
			}

			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
			notificationManager.notify(generateNotificationId(), builder.build());
		}

		/**
		 * @param context
		 * @param channelId
		 * @return 返回通知构造者
		 */
		private NotificationCompat.Builder getNotificationBuilder(Context context, String channelId) {
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
			return builder;
		}
	}


	/**
	 * @param context
	 * @param channelId
	 * @param channelName
	 * @param importance
	 * @param channelDes
	 * @return channel
	 * 返回通知信道
	 */
	@RequiresApi(api = Build.VERSION_CODES.O)
	public static NotificationChannel createNotificationChannel(@NonNull Context context, @NonNull String channelId, @NonNull String channelName, @NonNull int importance, @Nullable String channelDes) {

		//创建通知信道
		NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

		//添加通知信道描述
		channel.setDescription(channelDes);

		//获得通知管理者
		NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

		//创建通知信道
		notificationManager.createNotificationChannel(channel);

		return channel;
	}


	/**
	 * @return notificationId
	 * 生成 notificationId
	 */
	public static int generateNotificationId() {
		int max = Integer.MAX_VALUE, min = Integer.MIN_VALUE;
		long timeMillis = System.currentTimeMillis();
		int notificationId = (int) (timeMillis % (max - min) + min);
		return notificationId;
	}

	/**
	 * @param context
	 * @param channelId 消息渠道ID
	 *                  打开消息渠道设置
	 */
	public static void openChannelSettings(Context context, String channelId) {
		Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
		intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
		intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
		context.startActivity(intent);
	}

	/**
	 * @param context 上下文
	 *                打开默认消息通知设置
	 */
	public static void openNotificationSettings(Context context) {
		Intent intent = new Intent();
		intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
		intent.setData(Uri.fromParts("package", context.getPackageName(), null));
		context.startActivity(intent);

	}

	/**
	 * @param context 环境
	 * @return 返回是否开启通知
	 */
	public static boolean checkNotificationPermission(Context context) {
		return NotificationManagerCompat.from(context).areNotificationsEnabled();
	}
}
