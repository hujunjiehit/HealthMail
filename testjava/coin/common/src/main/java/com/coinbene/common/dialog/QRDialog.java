package com.coinbene.common.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.PermissionManager;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ToastUtil;
import com.example.resource.R;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.util.List;

/**
 * ding
 * 2019-10-16
 * com.coinbene.common.dialog
 */
public class QRDialog extends BaseDialog {
	private String mTitle;
	private String mDescription;
	private String mContactInfo;
	private String mQrUrl;
	private TextView tvTitle;
	private TextView tvDescription;
	private TextView tvContactUs;
	private View imgCopy;
	private ImageView imgQR;
	private View imgDismiss;
	private View tvSave;
	private Bitmap bitmap;

	public QRDialog(@NonNull Context context) {
		super(context);
	}

	public QRDialog(@NonNull Context context, int themeResId) {
		super(context, themeResId);
	}

	protected QRDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_qr);
		tvTitle = findViewById(R.id.tv_Title);
		tvDescription = findViewById(R.id.tv_Description);
		tvContactUs = findViewById(R.id.tv_ContactUs);
		tvSave = findViewById(R.id.tv_Save);
		imgDismiss = findViewById(R.id.img_Dismiss);
		imgCopy = findViewById(R.id.img_Copy);
		imgQR = findViewById(R.id.img_QR);

		tvSave.setOnClickListener(v -> saveQR());
		imgDismiss.setOnClickListener(v -> dismiss());

		imgCopy.setOnClickListener(v -> {
			ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
			ClipData data = ClipData.newPlainText("Label", tvContactUs.getText().toString());
			cm.setPrimaryClip(data);
			ToastUtil.show(getContext().getString(R.string.qr_copy_success));
		});
	}

	/**
	 * 保存二维码到相册
	 */
	private void saveQR() {
		PermissionManager.requestStoragePermission(getContext(), new PermissionManager.PermissionListener() {
			@Override
			public void onGranted(List<String> permissions) {
				if (bitmap != null && !bitmap.isRecycled()) {
					if (PhotoUtils.saveImageToGallery(getContext(), bitmap)) {
						ToastUtil.show(getContext().getResources().getString(R.string.save_suc));
						dismiss();
					} else {
						ToastUtil.show(getContext().getResources().getString(R.string.save_err));
					}
				}
			}

			@Override
			public void onDenied(List<String> permissions) {

			}
		});
	}

	@Override
	public void show() {
		super.show();
		tvTitle.setText(mTitle);
		tvContactUs.setText(mContactInfo);
		tvDescription.setText(mDescription);

		if (!TextUtils.isEmpty(mQrUrl)) {
			int sizeQR = QMUIDisplayHelper.dp2px(getContext(), 190);
			bitmap = new QREncode.Builder(getContext())
					.setMargin(1)
					.setParsedResultType(ParsedResultType.TEXT)
					.setContents(mQrUrl)
					.setSize(sizeQR)
					.build().encodeAsBitmap();
			imgQR.setImageBitmap(bitmap);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * @param mTitle 设置title
	 */
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	/**
	 * @param mDescription 设置描述
	 */
	public void setDescription(String mDescription) {
		this.mDescription = mDescription;
	}

	/**
	 * @param mContactInfo 设置联系信息
	 */
	public void setContactInfo(String mContactInfo) {
		this.mContactInfo = mContactInfo;
	}

	public void setTitle(@StringRes int mTitle) {
		this.mTitle = getContext().getString(mTitle);
	}

	public void setDescription(@StringRes int mDescription) {
		this.mDescription = getContext().getString(mDescription);
	}

	public void setContactInfo(@StringRes int mContactInfo) {
		this.mContactInfo = getContext().getString(mContactInfo);
	}

	public void setQrUrl(String mQrUrl) {
		this.mQrUrl = mQrUrl;
	}
}
