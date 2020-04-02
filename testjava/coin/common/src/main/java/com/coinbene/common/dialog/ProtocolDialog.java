package com.coinbene.common.dialog;

import android.content.Context;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.router.UIBusService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ding
 * 2019-11-04
 * com.coinbene.common.dialog
 */
public class ProtocolDialog extends BaseDialog {
	@BindView(R2.id.dialog_protocol)
	View dialogProtocol;
	@BindView(R2.id.tv_Title)
	TextView tvTitle;
	@BindView(R2.id.tv_Content)
	TextView tvContent;
	@BindView(R2.id.checkbox_Agree)
	CheckBox checkboxAgree;
	@BindView(R2.id.tv_Pact)
	TextView tvPact;
	@BindView(R2.id.tv_Positive)
	TextView tvPositive;
	@BindView(R2.id.tv_Negative)
	TextView tvNegative;
	private String title;
	private String negativeText;
	private String positiveText;
	private String content;
	private String protocolText;
	private String protocolUrl;

	private DialogListener listener;

	public ProtocolDialog(@NonNull Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_protocol);
		ButterKnife.bind(this);

		setListener();
		tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
		tvPact.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private void setListener() {
		//是否同意协议
		checkboxAgree.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (isChecked) {
				tvPositive.setEnabled(true);
				tvPositive.setAlpha(1f);
			} else {
				tvPositive.setEnabled(false);
				tvPositive.setAlpha(0.5f);
			}
		});

		tvPositive.setOnClickListener(v -> {
			if (listener != null) listener.clickPositive();
			dismiss();
		});

		tvNegative.setOnClickListener(v -> {
			if (listener != null) listener.clickNegative();
			dismiss();
		});
	}

	/**
	 * @return 处理下链接文案
	 */
	private SpannableString handlePactText() {
		int start = protocolText.indexOf("《");
		int end = protocolText.indexOf("》") + 1;
		String zendeskTitle = protocolText.substring(start + 1, end-1);
		SpannableString pactString = new SpannableString(protocolText);
		ClickableSpan clickableSpan = new ClickableSpan() {
			@Override
			public void onClick(@NonNull View widget) {
				if (widget instanceof TextView) {
					((TextView) widget).setHighlightColor(getContext().getResources().getColor(android.R.color.transparent));
				}
				Bundle bundle = new Bundle();
				bundle.putString("title", zendeskTitle);
				UIBusService.getInstance().openUri(getContext(), protocolUrl, bundle);
			}

			@Override
			public void updateDrawState(@NonNull TextPaint ds) {
				ds.setColor(getContext().getResources().getColor(R.color.res_blue));
				ds.clearShadowLayer();
				ds.setUnderlineText(false);
			}
		};
		pactString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		return pactString;
	}

	@Override
	public void show() {
		super.show();
		//没有消极文案隐藏
		if (TextUtils.isEmpty(negativeText)) {
			tvNegative.setVisibility(View.GONE);
		}
		//积极文案为空默认设置文案为确定
		if (TextUtils.isEmpty(positiveText)) {
			positiveText = getContext().getResources().getString(R.string.btn_confirm);
		}
		tvTitle.setText(title);
		tvContent.setText(content);
		tvPact.setText(protocolText);
		tvPositive.setText(positiveText);
		tvNegative.setText(negativeText);
		tvPact.setText(handlePactText());
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setPositiveText(String positiveText) {
		this.positiveText = positiveText;
	}

	public void setNegativeText(String negativeText) {
		this.negativeText = negativeText;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param protocolText 设置协议文案
	 */
	public void setProtocolText(String protocolText) {
		this.protocolText = protocolText;
	}

	/**
	 * @param protocolUrl 设置协议跳转链接
	 */
	public void setProtocolUrl(String protocolUrl) {
		this.protocolUrl = protocolUrl;
	}

	public void setDialogListener(DialogListener listener) {
		this.listener = listener;
	}
}
