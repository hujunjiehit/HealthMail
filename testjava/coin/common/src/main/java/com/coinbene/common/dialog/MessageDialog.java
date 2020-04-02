package com.coinbene.common.dialog;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.resource.R;

/**
 * ding
 * 2019-10-15
 * com.example.resource
 */
public class MessageDialog extends BaseDialog {
	private String mTittle;
	private String message;
	private String mNegativeText;
	private String mPositiveText;
	private DialogListener listener;

	private TextView tvTitle;
	private TextView tvContent;
	private TextView tvPositive;
	private TextView tvNegative;

	public MessageDialog(@NonNull Context context) {
		super(context);
	}

	public MessageDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected MessageDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_message);
		init();
		listener();
	}


	private void init() {
		tvTitle = findViewById(R.id.tv_Title);
		tvContent = findViewById(R.id.tv_Content);
		tvPositive = findViewById(R.id.tv_Positive);
		tvNegative = findViewById(R.id.tv_Negative);
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

		//title为空没有title
		if (TextUtils.isEmpty(mTittle)) {
			tvTitle.setVisibility(View.GONE);
		}

		//否定按钮文案为空隐藏否定按钮
		if (TextUtils.isEmpty(mNegativeText)) {
			tvNegative.setVisibility(View.GONE);
		}

		if (TextUtils.isEmpty(mPositiveText)) {
			//确定按钮文案默认为确定
			mPositiveText = getContext().getString(R.string.btn_confirm);
		}

		tvTitle.setText(mTittle);
		tvNegative.setText(mNegativeText);
		tvPositive.setText(mPositiveText);
		tvContent.setText(message);
	}

	/**
	 * @param tittle 设置tittle
	 */
	public void setTittle(String tittle) {
		this.mTittle = tittle;
	}

	/**
	 * @param tittle 设置tittle
	 */
	public void setTittle(@StringRes int tittle) {
		this.mTittle = getContext().getString(tittle);
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
	 * @param listener 设置监听
	 */
	public void setListener(DialogListener listener) {
		this.listener = listener;
	}
}
