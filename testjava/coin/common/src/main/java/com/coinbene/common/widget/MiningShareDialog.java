package com.coinbene.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.ToastUtil;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.HashMap;
import java.util.Random;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ding
 * 2019-08-07
 * com.coinbene.manbiwang.view
 */
public class MiningShareDialog extends BottomSheetDialog implements PlatformActionListener {

	private String filePath;
	private View imgWechat;
	private View imgWechatCircle;
	private View imgSina;
	private View imgQQ;
	private View imgFacebook;
	private View imgSave;
	private View layoutShareImg;
	private View tvCancel;
	private Bitmap bitmapQR;


	/**
	 * 估值
	 */
	private String sValuation = "0";

	/**
	 * 累计
	 */
	private String sAddUp = "0";

	private ImageView imgQR;
	private TextView tvShareText;
	private TextView tvAddUp;
	private TextView tvValuation;
	private TextView text1;
	private TextView text2;
	private String string1;
	private String string2;

	public MiningShareDialog(@NonNull Context context) {
		super(context, R.style.CoinBene_BottomSheet);
		setContentView(R.layout.common_dialog_mining_share);
		filePath = context.getExternalCacheDir().getPath() + "/";
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imgWechat = findViewById(R.id.img_wechat);
		imgWechatCircle = findViewById(R.id.img_wechat_circle);
		imgSina = findViewById(R.id.img_sina);
		imgQQ = findViewById(R.id.img_QQ);
		imgFacebook = findViewById(R.id.img_facebook);
		imgSave = findViewById(R.id.img_save);
		layoutShareImg = findViewById(R.id.layout_share_image);
		tvCancel = findViewById(R.id.tv_cancel);
		imgQR = findViewById(R.id.img_QR);
		tvShareText = findViewById(R.id.tv_share_text);
		tvAddUp = findViewById(R.id.tv_addUp_mining);
		tvValuation = findViewById(R.id.tv_mining_valuation);
		text1 = findViewById(R.id.text_1);
		text2 = findViewById(R.id.text_2);

		string1 = getContext().getString(R.string.addUp_mining);
		string2 = getContext().getString(R.string.mining_valuation);

		listener();
	}


	private void listener() {

		tvCancel.setOnClickListener(v -> dismiss());

		imgWechat.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImg) == null) {
				return;
			}

			String imagePath = FileUtils.saveBitmap(filePath, "CoinBene_Option.JPG", ShareUtils.screenShot(layoutShareImg));

			if (imagePath == null) {
				ToastUtil.show(R.string.share_failed);
				return;
			}

			new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});


		imgWechatCircle.setOnClickListener(v -> {

			if (ShareUtils.screenShot(layoutShareImg) == null) {
				return;
			}

			new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImg))
					.setPlatformActionListener(this)
					.share();
		});

		imgSina.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImg) == null) {
				return;
			}

			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImg))
					.setPlatformActionListener(this)
					.share();
		});

		imgQQ.setOnClickListener(v -> {

			if (ShareUtils.screenShot(layoutShareImg) == null) {
				return;
			}

			String imagePath = FileUtils.saveBitmap(filePath, "CoinBene_Option.JPG", ShareUtils.screenShot(layoutShareImg));

			if (imagePath == null) {
				ToastUtil.show(R.string.share_failed);
				return;
			}

			new ShareUtils.QQBuilder()
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		imgFacebook.setOnClickListener(v -> {
			if (ShareUtils.screenShot(layoutShareImg) == null) {
				return;
			}

			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setBitmap(ShareUtils.screenShot(layoutShareImg))
					.setPlatformActionListener(this)
					.share();
		});

		imgSave.setOnClickListener(v ->
				AndPermission.with(imgSave.getContext())
						.runtime()
						.permission(Permission.WRITE_EXTERNAL_STORAGE)
						.onGranted(data -> {
							PhotoUtils.saveImageToGallery(getContext(), ShareUtils.screenShot(layoutShareImg));
							ToastUtil.show(getContext().getString(R.string.save_suc));
						})
						.onDenied(data -> {

						})
						.start());
	}

	public void setValuation(String sValuation) {
		this.sValuation = sValuation;
	}

	public void setAddUp(String sAddUp) {
		this.sAddUp = sAddUp;
	}

	@Override
	public void show() {
		super.show();

		int size = QMUIDisplayHelper.dp2px(getContext(), 30);

		// 生成下载App二维码
		bitmapQR = new QREncode.Builder(getContext())
				.setMargin(0)
				.setParsedResultType(ParsedResultType.TEXT)
				//.setContents(Constants.DOANLOAD_APK_QR_CODE_URL)
				.setSize(size)
				.build().encodeAsBitmap();

		imgQR.setImageBitmap(bitmapQR);

		tvAddUp.setText(sAddUp);
		tvValuation.setText(sValuation);

		text1.setText(String.format("%s(CFT)", string1));
		text2.setText(String.format("%s(USDT)", string2));

		Random random = new Random();
		String[] stringArray = getContext().getResources().getStringArray(R.array.mining_share_copywriting);
		tvShareText.setText(stringArray[random.nextInt(stringArray.length)]);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (bitmapQR != null && !bitmapQR.isRecycled()) {
			bitmapQR.recycle();
		}
		bitmapQR = null;
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(getContext().getString(R.string.share_succes));
		dismiss();
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		ToastUtil.show(getContext().getString(R.string.share_failed));
		dismiss();
	}

	@Override
	public void onCancel(Platform platform, int i) {

	}
}
