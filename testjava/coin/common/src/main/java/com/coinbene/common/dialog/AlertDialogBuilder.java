package com.coinbene.common.dialog;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * ding
 * 2019-10-15
 * com.example.resource
 */
public class AlertDialogBuilder {
	private AlertDialog dialog;

	public AlertDialogBuilder(Context context) {
		dialog = new AlertDialog(context);
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public AlertDialogBuilder setNegativeButton(String text) {
		if (dialog != null) {
			dialog.setNegativeButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public AlertDialogBuilder setNegativeButton(@StringRes int text) {
		if (dialog != null) {
			dialog.setNegativeButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public AlertDialogBuilder setPositiveButton(String text) {
		if (dialog != null) {
			dialog.setPositiveButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public AlertDialogBuilder setPositiveButton(@StringRes int text) {
		if (dialog != null) dialog.setPositiveButton(text);
		return this;
	}

	/**
	 * @param message 设置消息
	 */
	public AlertDialogBuilder setMessage(String message) {
		if (dialog != null) dialog.setMessage(message);
		return this;
	}

	/**
	 * @param message 设置消息
	 */
	public AlertDialogBuilder setMessage(@StringRes int message) {
		if (dialog != null) dialog.setMessage(message);
		return this;
	}

	/**
	 * @param alertIcon 设置警告图标
	 */
	public AlertDialogBuilder setAlertIcon(@DrawableRes int alertIcon) {
		if (dialog != null) dialog.setAlertIcon(alertIcon);
		return this;
	}

	/**
	 * @param listener 设置监听
	 */
	public AlertDialogBuilder setListener(DialogListener listener) {
		if (dialog != null) dialog.setListener(listener);
		return this;
	}

	/**
	 * 展示Dialog
	 */
	public void showDialog() {
		dialog.show();
	}
}
