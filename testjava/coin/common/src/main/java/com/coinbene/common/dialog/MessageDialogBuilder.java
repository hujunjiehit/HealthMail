package com.coinbene.common.dialog;

import android.content.Context;
import androidx.annotation.StringRes;

/**
 * ding
 * 2019-10-15
 * com.example.resource
 */
public class MessageDialogBuilder {
	private MessageDialog dialog;

	public MessageDialogBuilder(Context context) {
		dialog = new MessageDialog(context);
	}

	/**
	 * @param tittle 设置tittle
	 */
	public MessageDialogBuilder setTitle(String tittle) {
		if (dialog != null) {
			dialog.setTittle(tittle);
		}
		return this;
	}

	/**
	 * @param tittle 设置tittle
	 */
	public MessageDialogBuilder setTitle(@StringRes int tittle) {
		if (dialog != null) {
			dialog.setTittle(tittle);
		}
		return this;
	}

	/**
	 * @param message 设置消息
	 */
	public MessageDialogBuilder setMessage(String message) {
		if (dialog != null) dialog.setMessage(message);
		return this;
	}

	/**
	 * @param message 设置消息
	 */
	public MessageDialogBuilder setMessage(@StringRes int message) {
		if (dialog != null) dialog.setMessage(message);
		return this;
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public MessageDialogBuilder setNegativeButton(String text) {
		if (dialog != null) {
			dialog.setNegativeButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public MessageDialogBuilder setNegativeButton(@StringRes int text) {
		if (dialog != null) {
			dialog.setNegativeButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public MessageDialogBuilder setPositiveButton(String text) {
		if (dialog != null) {
			dialog.setPositiveButton(text);
		}
		return this;
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public MessageDialogBuilder setPositiveButton(@StringRes int text) {
		if (dialog != null) {
			dialog.setPositiveButton(text);
		}
		return this;
	}

	/**
	 * @param listener 设置监听
	 */
	public MessageDialogBuilder setListener(DialogListener listener) {
		if (dialog != null) {
			dialog.setListener(listener);
		}
		return this;
	}

	/**
	 * 展示Dialog
	 */
	public void showDialog() {
		dialog.show();
	}
}
