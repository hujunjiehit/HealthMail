package com.coinbene.common.dialog;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * ding
 * 2019-10-15
 * com.example.resource
 */
public class BaseDialog extends Dialog {
	public BaseDialog(@NonNull Context context) {
		super(context);
		initDialog();
	}

	public BaseDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
		initDialog();
	}

	protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		initDialog();
	}

	private void initDialog() {
		Window window = getWindow();
		if (window == null) {
			return;
		}
		window.setBackgroundDrawable(new BitmapDrawable());
		WindowManager.LayoutParams wmlp = window.getAttributes();
		wmlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wmlp.gravity = Gravity.CENTER;
		window.setAttributes(wmlp);
	}
}
