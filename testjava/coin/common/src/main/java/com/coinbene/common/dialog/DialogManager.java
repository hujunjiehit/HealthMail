package com.coinbene.common.dialog;

import android.content.Context;

import androidx.annotation.StringRes;

/**
 * ding
 * 2019-10-15
 * com.coinbene.common.utils
 */
public class DialogManager {

	/**
	 * @return 返回消息类Builder
	 */
	public static MessageDialogBuilder getMessageDialogBuilder(Context context) {
		return new MessageDialogBuilder(context);
	}

	/**
	 * @return 返回警告类Builder
	 */
	public static AlertDialogBuilder getAlertDialogBuilder(Context context) {
		return new AlertDialogBuilder(context);
	}

	/**
	 * @param context
	 * @return 协议类Dialog
	 */
	public static ProtocolDialog getProtocolDialog(Context context) {
		return new ProtocolDialog(context);
	}

	/**
	 * @param context
	 * @param title       标题
	 * @param description 描述
	 * @param qrUrl       二维码生成链接
	 * @param contactInfo 联系方式
	 *                    展示二维码显示Dialog
	 */
	public static QRDialog showQrDialog(Context context, String title, String description, String qrUrl, String contactInfo) {
		QRDialog dialog = new QRDialog(context);
		dialog.setTitle(title);
		dialog.setDescription(description);
		dialog.setQrUrl(qrUrl);
		dialog.setContactInfo(contactInfo);
		dialog.show();
		return dialog;
	}

	/**
	 * @param context
	 * @param title       标题
	 * @param description 描述
	 * @param qrUrl       二维码生成链接
	 * @param contactInfo 联系方式
	 *                    展示二维码显示Dialog
	 */
	public static QRDialog showQrDialog(Context context, @StringRes int title, @StringRes int description, String qrUrl, @StringRes int contactInfo) {
		QRDialog dialog = new QRDialog(context);
		dialog.setTitle(title);
		dialog.setDescription(description);
		dialog.setQrUrl(qrUrl);
		dialog.setContactInfo(contactInfo);
		dialog.show();
		return dialog;
	}

	/**
	 * 返回选择类Builder
	 */
	public static <T> SelectorDialog<T> getSelectorDialog(Context context) {
		return new SelectorDialog<>(context);
	}

	/**
	 * 获得一个分享面板
	 */
	public static ShareMenu getShareMenu(Context context) {
		return new ShareMenu(context);
	}
}
