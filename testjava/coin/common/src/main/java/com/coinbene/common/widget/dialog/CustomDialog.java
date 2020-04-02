package com.coinbene.common.widget.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.airbnb.lottie.LottieAnimationView;
import com.coinbene.common.R;

/**
 * Created by zhangle on 2018/3/6.
 */

public class CustomDialog extends ProgressDialog {

	private LottieAnimationView lottie_view;

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		init(getContext());
	}

	private void init(Context context) {
		//设置不可取消，点击其他区域不能取消，实际中可以抽出去封装供外包设置
		setCancelable(false);
		setCanceledOnTouchOutside(true);

		setContentView(R.layout.common_dialog_load);
		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		getWindow().setAttributes(params);
		lottie_view = findViewById(R.id.lottie_view);
		lottie_view.useHardwareAcceleration(true);
//        lottie_view.buildDrawingCache(true);
	}

	@Override
	public void dismiss() {
		super.dismiss();
		if (lottie_view != null) {
			lottie_view.cancelAnimation();
			lottie_view = null;
		}
	}
}
