package com.coinbene.common.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.List;

/**
 * ding
 * 2019-08-09
 * com.coinbene.basiclib.utils
 * 权限管理
 */
public final class PermissionManager {

	/**
	 * @param context
	 * @param listener 请求相机权限
	 */
	public static void requestCameraPermission(Context context, PermissionListener listener) {
		requestPermission(context, Permission.CAMERA, listener);
	}

	/**
	 * @param context
	 * @param listener 请求存储权限组
	 */
	public static void requestStoragePermission(Context context, PermissionListener listener) {
		requestPermissionGroup(context, Permission.Group.STORAGE, listener);
	}

	/**
	 * @param context
	 * @param listener 请求短信权限组
	 */
	public static void requestSMSPermission(Context context, PermissionListener listener) {
		requestPermissionGroup(context, Permission.Group.SMS, listener);
	}

	/**
	 * @param context
	 * @param listener 请求手机状态权限
	 */
	public static void requestPhoneStatusPermission(Context context, PermissionListener listener) {
		requestPermission(context, Permission.READ_PHONE_STATE, listener);
	}

	/**
	 * @param context
	 * @param permission
	 * @param listener   请求权限组
	 */
	public static void requestPermissionGroup(Context context, String[] permission, PermissionListener listener) {

		AndPermission.with(context)
				.runtime()
				.permission(permission)
				.onGranted(permissions -> {
					// 用户授予权限
					if (listener != null)
						listener.onGranted(permissions);

				})
				.onDenied(permissions -> {
					//用户拒绝权限
					if (listener != null)
						listener.onDenied(permissions);

				})
				.start();
	}

	/**
	 * @param context
	 * @param permission
	 * @param listener   请求单独权限
	 */
	public static void requestPermission(Context context, String permission, PermissionListener listener) {
		AndPermission.with(context)
				.runtime()
				.permission(permission)
				.onGranted(permissions -> {
					// 用户授予权限
					if (listener != null)
						listener.onGranted(permissions);
				})
				.onDenied(permissions -> {
					//用户拒绝权限
					if (listener != null)
						listener.onDenied(permissions);
				})
				.start();
	}


	/**
	 * @param context
	 * @param permission
	 * @return 检查权限
	 */
	public static boolean check(Context context, String permission) {
		return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
	}

	/**
	 * 权限授予监听
	 */
	public interface PermissionListener {
		void onGranted(List<String> permissions);

		void onDenied(List<String> permissions);
	}
}
