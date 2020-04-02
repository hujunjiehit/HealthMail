package com.coinbene.common.widget.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.coinbene.common.R;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.app.QrCodeDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

public class CommuinityDialog extends QMUIDialog implements View.OnClickListener {

	private LinearLayout qrCodeClose;
	private ImageView communityQrCode;
	private Bitmap bitmap;
	private QrCodeDialog.DialogClickListener dialogClickListener;
	private TextView save;
	private @DrawableRes
	int qrCodeRes;
	private String wechatNumber;
	private TextView mTvShowWechat;
	private ImageView mIvCopy;

	public CommuinityDialog(@NonNull Context context) {
		super(context);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog_qrcode);

		qrCodeClose = findViewById(R.id.qr_code_close);
		communityQrCode = findViewById(R.id.community_qr_code);
		save = findViewById(R.id.code_tv_save);
		mTvShowWechat = findViewById(R.id.tv_show_wechat_number);
		mIvCopy = findViewById(R.id.iv_copy);
		qrCodeClose.setOnClickListener(this);
		save.setOnClickListener(this);
		mIvCopy.setOnClickListener(this);
	}

	public void setQrCode(@DrawableRes int qrCodeRes) {
		this.qrCodeRes = qrCodeRes;
	}

	public void setWechatNumber(String wechatNumber) {
		this.wechatNumber = wechatNumber;
	}

	@SuppressLint({"ResourceType", "StringFormatInvalid"})
	@Override
	public void show() {
		super.show();
		if (communityQrCode != null && qrCodeRes > 0) {
			bitmap = BitmapFactory.decodeResource(getContext().getResources(), qrCodeRes);
			communityQrCode.setImageResource(qrCodeRes);
		} else {
			bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.community_wechat_code);
		}
		if (mTvShowWechat != null && !TextUtils.isEmpty(wechatNumber)) {
			mTvShowWechat.setText(String.format(getContext().getResources().getString(R.string.Wechat_number), wechatNumber));
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.qr_code_close) {
			dismiss();
		} else if (id == R.id.code_tv_save) {
			AndPermission.with(getContext()).runtime().permission(Permission.Group.STORAGE)
					.onGranted(permissions -> {
						if (bitmap != null) {
							if (PhotoUtils.saveImageToGallery(getContext(), bitmap)) {
								dismiss();
								ToastUtil.show(getContext().getResources().getString(R.string.save_suc));
							} else {
								ToastUtil.show(getContext().getResources().getString(R.string.save_err));
							}
						}
					})
					.onDenied(permissions -> {
						ToastUtil.show(getContext().getResources().getString(R.string.please_give_permission_grant));
						if (AndPermission.hasAlwaysDeniedPermission(getContext(), permissions)) {
							// 用Dialog展示没有某权限，询问用户是否去设置中授权。
							AndPermission.with(getContext())
									.runtime()
									.setting()
									.start(0x123);
						}
					})
					.start();
		} else if (id == R.id.iv_copy) {
			if (!TextUtils.isEmpty(wechatNumber)) {
				StringUtils.copyStrToClip(wechatNumber);
			}
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		bitmap = null;
	}
}
