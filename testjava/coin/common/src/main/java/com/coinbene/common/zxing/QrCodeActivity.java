package com.coinbene.common.zxing;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.BigDecimalUtils;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ToastUtil;

import butterknife.BindView;
import butterknife.Unbinder;

public class QrCodeActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	View menuBack;
	@BindView(R2.id.menu_title_tv)
	TextView menuTitleTv;
	@BindView(R2.id.iv_qr_code)
	ImageView ivQrCode;
	@BindView(R2.id.iv_save_icon)
	ImageView ivSaveIcon;
	private Unbinder mUnbinder;
	private Bitmap qrBitmap;
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private AlertDialog.Builder dialog;

	// 1支付宝  2微信
	public static void startMe(Context context, int type, String url) {
		Intent intent = new Intent(context, QrCodeActivity.class);
		intent.putExtra("type", type);
		intent.putExtra("url", url);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.activity_qr_code;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}

	private void init() {
		ivQrCode.setOnLongClickListener(v -> {
			initTipDialog(getString(R.string.comfirm_save_qr_code));
			return false;
		});
		ivSaveIcon.setOnClickListener(v -> {
			initTipDialog(getString(R.string.comfirm_save_qr_code));
		});
		menuBack.setOnClickListener(v -> finish());
		int type = getIntent().getIntExtra("type", 1);
		String url = getIntent().getStringExtra("url");
		if (type == 2) {
			menuTitleTv.setText(R.string.alipay_qr);
		} else {
			menuTitleTv.setText(R.string.wechat_qr);
		}
//		GlideUtils.loadImageViewLoad(this, Constants.BASE_URL, url, ivQrCode, R.drawable.icon_id_loading_default, R.drawable.icon_id_card_front);


		Glide.with(this).asBitmap().load(url).apply(new RequestOptions().placeholder(R.drawable.icon_id_loading_default).error(R.drawable.icon_id_loading_default)).into(new SimpleTarget<Bitmap>() {
			@Override
			public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
				qrBitmap = resource;
				setImageViewWirth(resource);
				ivQrCode.setImageBitmap(resource);
			}
		});
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults == null || grantResults.length == 0) {
			return;
		}
		switch (requestCode) {
			//调用系统相册申请Sdcard权限回调
			case CODE_GALLERY_REQUEST:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (qrBitmap != null) {
						if (PhotoUtils.saveImageToGallery(QrCodeActivity.this, qrBitmap)) {
							ToastUtil.show(getString(R.string.save_suc));
						} else
							ToastUtil.show(getString(R.string.save_err));
					}
				}
				break;
			default:
		}
	}


	private void autoStoragePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_GALLERY_REQUEST);
		} else {
			if (qrBitmap != null) {
				if (PhotoUtils.saveImageToGallery(QrCodeActivity.this, qrBitmap)) {
					ToastUtil.show(getString(R.string.save_suc));
				} else
					ToastUtil.show(getString(R.string.save_err));
			}
		}

	}

	private void setImageViewWirth(Bitmap bitmap) {

		if (bitmap == null) {
			return;
		}
		int width = bitmap.getWidth();
		float ratio = BigDecimalUtils.divide(String.valueOf(DensityUtil.getScreenWidth()), String.valueOf(width), 2);
		int heigth = (int) (bitmap.getHeight() * ratio);
		ViewGroup.LayoutParams lp = ivQrCode.getLayoutParams();
		lp.height = heigth;
//        lp.width = DensityUtil.getScreenWidth(this);
		ivQrCode.setLayoutParams(lp);
	}

	private void initTipDialog(String content) {

		if (dialog == null) {
			dialog = new AlertDialog.Builder(this);
		}
		dialog.setMessage(content);
		dialog.setCancelable(true);
		dialog.setPositiveButton(getString(R.string.btn_ok), (dialog, which) -> {
			autoStoragePermission();
			dialog.dismiss();
		});
		dialog.setNegativeButton(getString(R.string.btn_cancel), (dialog, which) -> dialog.dismiss());
		dialog.show();
	}

}
