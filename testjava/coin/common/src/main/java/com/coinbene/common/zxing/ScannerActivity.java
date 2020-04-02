package com.coinbene.common.zxing;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.coinbene.common.R;
import com.coinbene.common.R2;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.ToastUtil;
import com.google.zxing.Result;
import com.google.zxing.client.result.ParsedResult;
import com.google.zxing.client.result.ParsedResultType;
import com.google.zxing.client.result.TextParsedResult;
import com.google.zxing.client.result.URIParsedResult;
import com.mylhyl.zxing.scanner.OnScannerCompletionListener;
import com.mylhyl.zxing.scanner.ScannerView;
import com.mylhyl.zxing.scanner.common.Scanner;
import com.mylhyl.zxing.scanner.decode.QRDecode;

import butterknife.BindView;

import static com.google.zxing.client.result.ParsedResultType.TEXT;


/**
 * 扫描
 */
public class ScannerActivity extends CoinbeneBaseActivity implements OnScannerCompletionListener, ActivityCompat.OnRequestPermissionsResultCallback {

	public static boolean scanUrl = false;

	public static void startMeForResult(Activity activity, int requestCode) {
		scanUrl = false;
		Intent intent = new Intent(activity, ScannerActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static void startMeForResult(Activity activity, int requestCode, boolean scanUrl) {
		ScannerActivity.scanUrl = scanUrl;
		Intent intent = new Intent(activity, ScannerActivity.class);
		activity.startActivityForResult(intent, requestCode);
	}

	public static final String TAG = ScannerActivity.class.getSimpleName();
	private static final int IMAGE_RESULT_CODE = 100;
	public static final int APPLY_READ_EXTERNAL_STORAGE = 0x111;
	public static final int APPLY_CAMERA = 0x222;
	public static final int CODE_RESULT_ADDRESS = 101;

	private ProgressDialog progressDialog;
	private Result mLastResult;
	@BindView(R2.id.scanner_view)
	ScannerView mScannerView;
	@BindView(R2.id.menu_right_tv)
	TextView rightView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_right_tv) {
			if (ContextCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission
					.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				//权限还没有授予，需要在这里写申请权限的代码
				ActivityCompat.requestPermissions(ScannerActivity.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
						APPLY_READ_EXTERNAL_STORAGE);
			} else {
				Intent intent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, IMAGE_RESULT_CODE);
			}
		} else if (v.getId() == R.id.menu_back) {
			finish();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults == null || grantResults.length == 0) {
			return;
		}
		if (requestCode == APPLY_READ_EXTERNAL_STORAGE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Intent intent = new Intent(Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent, IMAGE_RESULT_CODE);
			} else {
				ToastUtil.show(R.string.please_give_permission_grant);
			}
		} else if (requestCode == APPLY_CAMERA) {
			restartPreviewAfterDelay(0);
		}
	}

	private void autoCameraPermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, APPLY_CAMERA);
		} else {
			restartPreviewAfterDelay(0);
		}

	}

	@Override
	protected void onResume() {
		mScannerView.onResume();
		resetStatusView();
		super.onResume();
	}

	@Override
	public int initLayout() {
		return R.layout.activity_scanner;
	}

	@Override
	public void initView() {
		titleView.setText(getText(R.string.scan_qr_title));
		mScannerView.setOnScannerCompletionListener(this);
		rightView.setText(getText(R.string.zxing_picture_label));
		backView.setOnClickListener(this);
		rightView.setOnClickListener(this);

		mScannerView.setDrawText(getText(R.string.zxing_scan_label).toString(), true);
		mScannerView.setDrawTextColor(Color.WHITE);

		//显示扫描成功后的缩略图
		mScannerView.isShowResThumbnail(false);
		//全屏识别
		mScannerView.isScanFullScreen(false);
		//隐藏扫描框
		mScannerView.isHideLaserFrame(false);

		mScannerView.setLaserLineResId(R.drawable.wx_scan_line);//线图

		autoCameraPermission();

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

	@Override
	protected void onPause() {
		mScannerView.onPause();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mLastResult != null) {
					restartPreviewAfterDelay(0L);
					return true;
				}
				break;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void restartPreviewAfterDelay(long delayMS) {
		mScannerView.restartPreviewAfterDelay(delayMS);
		resetStatusView();
	}

	private void resetStatusView() {
		mLastResult = null;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		if (requestCode == IMAGE_RESULT_CODE && data != null) {
			Uri uri = data.getData();
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
				QRDecode.decodeQR(bitmap, this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	void onResultActivity(Result result, ParsedResultType type, Bundle bundle) {
		switch (type) {
			case TEXT:
			case URI:
				Intent intent = new Intent();
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				break;
		}
		dismissProgressDialog();
		finish();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getString(R.string.dialog_wait_label));
		progressDialog.show();
	}

	void dismissProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void OnScannerCompletion(Result rawResult, ParsedResult parsedResult, Bitmap barcode) {
		if (rawResult == null) {
			ToastUtil.show(R.string.zxing_not_find_zxing);
			return;
		}
		final Bundle bundle = new Bundle();
		final ParsedResultType type = parsedResult.getType();
		switch (type) {
			case TEXT:
				TextParsedResult textParsedResult = (TextParsedResult) parsedResult;
				bundle.putString(Scanner.Scan.RESULT, textParsedResult.getText());
				break;
			case URI:
				URIParsedResult uriParsedResult = (URIParsedResult) parsedResult;
				bundle.putString(Scanner.Scan.RESULT, uriParsedResult.getURI());
			default:
				break;
		}
		if (scanUrl == false && type != TEXT) {
			ToastUtil.show(R.string.zxing_scan_not_right_result);
			restartPreviewAfterDelay(0L);
			return;
		}
		showProgressDialog();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				onResultActivity(rawResult, type, bundle);
			}
		}, 2 * 1000);
	}
}
