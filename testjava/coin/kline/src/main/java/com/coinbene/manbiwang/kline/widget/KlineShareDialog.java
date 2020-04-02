package com.coinbene.manbiwang.kline.widget;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.kline.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ding
 * 2019-06-28
 * com.coinbene.manbiwang.modules.kline.view
 */

@SuppressWarnings("all")
public class KlineShareDialog extends BottomSheetDialog implements PlatformActionListener {


	private TextView textTradePair;
	private ImageView imgContent;
	private ImageView imgQR;
	private View shareWechat;
	private View shareFrend;
	private View shareFaceBook;
	private View shareQQ;
	private View shareTwitter;
	private View shareSina;

	private String tradePair = "--";
	private Bitmap shareContent;

	private String imagePath;
	private String cachePath = getContext().getExternalCacheDir().getPath() + "/";
	private ConstraintLayout shareLayout;
	private View cancel;

	private View save;

	public KlineShareDialog(@NonNull Context context) {
		this(context, 0);
	}

	public KlineShareDialog(@NonNull Context context, int theme) {
		super(context, R.style.CoinBene_BottomSheet);
		setContentView(R.layout.dialog_kline_share);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		shareLayout = findViewById(R.id.share_Layout);
		textTradePair = findViewById(R.id.text_TradePair);
		imgContent = findViewById(R.id.image_Content);
		imgQR = findViewById(R.id.image_QR);
		shareWechat = findViewById(R.id.share_wechat);
		shareFrend = findViewById(R.id.share_frend);
		shareFaceBook = findViewById(R.id.share_facebook);
		shareQQ = findViewById(R.id.share_qq);
		shareTwitter = findViewById(R.id.share_twitter);
		shareSina = findViewById(R.id.share_sina);
		cancel = findViewById(R.id.cancel);
		save = findViewById(R.id.share_save);

		cancel.setOnClickListener(view -> dismiss());
		shareQQ.setOnClickListener(v -> shareToQQ());
		shareSina.setOnClickListener(view -> shareToSina());
		shareWechat.setOnClickListener(v -> shareToWechat());
		shareFrend.setOnClickListener(view -> shareToWechatCircle());
		shareFaceBook.setOnClickListener(view -> shareToFaceBook());
		shareTwitter.setOnClickListener(view -> shareToTwitter());
		save.setOnClickListener(v -> savaImage());
	}

	/**
	 * 保存图片
	 */
	private void savaImage() {
		if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
			PhotoUtils.saveImageToGallery(getContext(), ShareUtils.screenShot(shareLayout));
			ToastUtil.show(getContext().getString(com.coinbene.common.R.string.save_suc));
		}
	}


	/**
	 * 分享到Twitter
	 */
	private void shareToTwitter() {
		imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", ShareUtils.screenShot(shareLayout));
		new ShareUtils.TwitterBuilder(Twitter.NAME)
				.setText("CoinBene")
				.setImagePath(imagePath)
				.setPlatformActionListener(this)
				.share();
	}

	/**
	 * 分享到新浪微博
	 */
	private void shareToSina() {
		new ShareUtils.SinaBuilder(SinaWeibo.NAME)
				.setText("CoinBene")
				.setBitmap(ShareUtils.screenShot(shareLayout))
				.setPlatformActionListener(this)
				.share();
	}

	/**
	 * 分享到FaceBook
	 */
	private void shareToFaceBook() {
		new ShareUtils.FaceBookBuilder(Facebook.NAME)
				.setText("CoinBene")
				.setBitmap(ShareUtils.screenShot(shareLayout))
				.setPlatformActionListener(this)
				.share();
	}


	/**
	 * 分享到QQ
	 */
	private void shareToQQ() {
		imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", ShareUtils.screenShot(shareLayout));
		new ShareUtils.QQBuilder()
				.setImagePath(imagePath)
				.setPlatformActionListener(this)
				.share();
	}


	/**
	 * 分享到微信
	 */
	private void shareToWechat() {
		imagePath = FileUtils.saveBitmap(cachePath, "CoinBene_Invite.JPG", ShareUtils.screenShot(shareLayout));
		new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
				.setTitle("CoinBene")
				.setImagePath(imagePath)
				.setPlatformActionListener(this)
				.share();
	}

	/**
	 * 分享到微信朋友圈
	 */
	private void shareToWechatCircle() {
		new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
				.setTitle("CoinBene")
				.setBitmap(ShareUtils.screenShot(shareLayout))
				.setPlatformActionListener(this)
				.share();
	}


	/**
	 * @param tradePair 设置交易对
	 */
	public void setTradePair(String tradePair) {
		if (!TextUtils.isEmpty(tradePair))
			this.tradePair = tradePair;
	}

	/**
	 * @param shareContent 设置分享K线图
	 */
	public void setShareContent(Bitmap shareContent) {
		if (null != shareContent)
			this.shareContent = shareContent;
	}


	/**
	 * @return bitmap
	 * 截图
	 */
	private Bitmap screenShot() {
		Bitmap bitmap = Bitmap.createBitmap(shareLayout.getWidth(), shareLayout.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);

		shareLayout.draw(canvas);

		return bitmap;
	}

	@Override
	public void show() {
		super.show();

		imgContent.setImageBitmap(shareContent);
		textTradePair.setText(tradePair);


		String downLoadUrl = Constants.DOANLOAD_APK_QR_CODE_URL;
		downLoadUrl = UrlUtil.replaceH5Url(downLoadUrl);

		Bitmap bitmap = new QREncode.Builder(imgQR.getContext())
				.setMargin(1)
				.setParsedResultType(ParsedResultType.TEXT)
				.setContents(downLoadUrl)
				.build().encodeAsBitmap();

		imgQR.setImageBitmap(bitmap);
	}


	@Override
	public void dismiss() {
		super.dismiss();
		if (shareContent != null && !shareContent.isRecycled()) {
			shareContent.recycle();
			shareContent = null;
		}

	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		Toast.makeText(getContext(), R.string.share_succes, Toast.LENGTH_LONG).show();
		if (isShowing()) {
			dismiss();
		}
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		Toast.makeText(getContext(), R.string.share_failed, Toast.LENGTH_LONG).show();
		if (isShowing()) {
			dismiss();
		}
	}

	@Override
	public void onCancel(Platform platform, int i) {
		Toast.makeText(getContext(), R.string.cancel, Toast.LENGTH_LONG).show();
		if (isShowing()) {
			dismiss();
		}
	}
}
