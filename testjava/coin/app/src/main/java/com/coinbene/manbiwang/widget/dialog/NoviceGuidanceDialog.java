package com.coinbene.manbiwang.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.coinbene.common.R;

/**
 * 新手引导弹窗
 */
public class NoviceGuidanceDialog extends Dialog implements View.OnClickListener {


	private View llIhaveExperience, llIamNovice, ivClose;

	public NoviceGuidanceDialog(@NonNull Context context) {
		super(context);
		init();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog_novice_guidance);
		initView();
		initListener();
	}

	private void initView() {
		llIhaveExperience = findViewById(R.id.ll_i_have_experience);
		llIamNovice = findViewById(R.id.ll_i_am_novice);
		ivClose = findViewById(R.id.iv_close);
	}

	public void initListener() {
		llIhaveExperience.setOnClickListener(this);
		llIamNovice.setOnClickListener(this);
		ivClose.setOnClickListener(this);
	}


	/**
	 * 初始化
	 */
	private void init() {
		// 在构造方法里, 传入主题
		Window window = getWindow();
		window.getDecorView().setPadding(0, 0, 0, 0);
		window.setBackgroundDrawable(new BitmapDrawable());
		// 获取Window的LayoutParams
		WindowManager.LayoutParams attributes = window.getAttributes();
		attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
		attributes.gravity = Gravity.CENTER;
		// 一定要重新设置, 才能生效
		window.setAttributes(attributes);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_i_have_experience) {
			dismiss();
		} else if (id == R.id.ll_i_am_novice) {
			NoviceGuidanceSecondDialog noviceGuidanceSecondDialog = new NoviceGuidanceSecondDialog(getContext());
			noviceGuidanceSecondDialog.show();
			dismiss();
		} else if (id == R.id.iv_close) {
			dismiss();
		}

	}
}
