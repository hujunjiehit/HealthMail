package com.coinbene.game.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.game.R;
import com.coinbene.game.R2;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * ding
 * 2019-09-10
 * com.coinbene.game.view
 */
public class JoinCommunityDialog extends Dialog {


	@BindView(R2.id.img_close)
	ImageView imgClose;
	@BindView(R2.id.img_code)
	ImageView imgCode;
	@BindView(R2.id.tv_WechatNumber)
	TextView tvWechatNumber;
	@BindView(R2.id.img_Copy)
	ImageView imgCopy;
	@BindView(R2.id.tv_save)
	TextView tvSave;
	@BindView(R2.id.tv1)
	TextView tv1;
	@BindView(R2.id.tv2)
	TextView tv2;

	private Bitmap mQRCode;

	public JoinCommunityDialog(@NonNull Context context) {
		super(context);
		init();
	}

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_join_community);
		ButterKnife.bind(this);

		if (LanguageHelper.isChinese(getContext())) {
			mQRCode = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.res_img_wechatcode);
		} else {
			mQRCode = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.res_img_telegramcode);
			tv1.setText("Telegram Note: Call&Put");
			tv2.setText("Join our Telegram to get more benefits");
			tvWechatNumber.setText(R.string.copy_link);
		}

		listener();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mQRCode != null) {
			mQRCode.recycle();
			mQRCode = null;
		}
	}

	@Override
	public void show() {
		super.show();
		imgCode.setImageBitmap(mQRCode);
	}

	private void listener() {
		imgClose.setOnClickListener(v -> dismiss());
		tvSave.setOnClickListener(v -> saveQRCode());
		imgCopy.setOnClickListener(v -> copy());
	}

	/**
	 * 复制微信号或者复制电报链接
	 */
	private void copy() {
		if (LanguageHelper.isChinese(getContext())) {
			StringUtils.copyStrToClip("CoinBene_z");
		} else {
			StringUtils.copyStrToClip("https://t.me/CoinBene_CallPut");
		}
	}

	/**
	 * 保存二维码
	 */
	private void saveQRCode() {
		AndPermission.with(getContext()).runtime().permission(Permission.Group.STORAGE)
				.onGranted(permissions -> {
					if (mQRCode != null) {
						if (PhotoUtils.saveImageToGallery(getContext(), mQRCode)) {
							ToastUtil.show(getContext().getResources().getString(R.string.save_suc));
						} else {
							ToastUtil.show(getContext().getResources().getString(R.string.save_err));
						}
					}
				}).start();
	}
}
