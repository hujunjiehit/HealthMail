package com.coinbene.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.resource.R;

/**
 * ding
 * 2019-10-15
 * com.example.resource
 */
public class AlertDialog extends BaseDialog {
	private String message;
	private int alertIcon;
	private String mNegativeText;
	private String mPositiveText;
	private DialogListener listener;

	private ImageView imgAlert;
	private TextView tvNegative;
	private TextView tvPositive;
	private TextView tvContent;

	public AlertDialog(@NonNull Context context) {
		super(context);
	}

	public AlertDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected AlertDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_alert);
		init();
		listener();
	}

	private void init() {
		setCancelable(false);

		imgAlert = findViewById(R.id.img_alert);
		tvNegative = findViewById(R.id.tv_Negative);
		tvPositive = findViewById(R.id.tv_Positive);
		tvContent = findViewById(R.id.tv_Content);

		mPositiveText = getContext().getString(R.string.btn_confirm);
	}


	private void listener() {
		//确定点击
		tvPositive.setOnClickListener(v -> {
			if (listener != null) listener.clickPositive();
			dismiss();
		});

		//否定点击
		tvNegative.setOnClickListener(v -> {
			if (listener != null) listener.clickNegative();
			dismiss();
		});
	}

	@Override
	public void show() {
		super.show();
		//没有取消
		if (TextUtils.isEmpty(mNegativeText)) {
			tvNegative.setVisibility(View.GONE);
		}

		//设置了自定义警告图标
		if (alertIcon != 0) {
			imgAlert.setVisibility(View.VISIBLE);
			imgAlert.setImageResource(alertIcon);
		} else {
			imgAlert.setVisibility(View.GONE);
		}

		tvContent.setText(message);
		tvPositive.setText(mPositiveText);
		tvNegative.setText(mNegativeText);
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public void setNegativeButton(String text) {
		this.mNegativeText = text;
	}

	/**
	 * @param text 设置否定按钮文案
	 */
	public void setNegativeButton(@StringRes int text) {
		this.mNegativeText = getContext().getString(text);
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public void setPositiveButton(String text) {
		this.mPositiveText = text;
	}

	/**
	 * @param text 设置确定按钮文案
	 */
	public void setPositiveButton(@StringRes int text) {
		this.mPositiveText = getContext().getString(text);
	}

	/**
	 * @param message 设置消息
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @param message 设置消息
	 */
	public void setMessage(@StringRes int message) {
		this.message = getContext().getString(message);
	}

	/**
	 * @param alertIcon 设置警告图标
	 */
	public void setAlertIcon(@DrawableRes int alertIcon) {
		this.alertIcon = alertIcon;
	}

	/**
	 * @param listener 设置监听
	 */
	public void setListener(DialogListener listener) {
		this.listener = listener;
	}
}
