package com.coinbene.manbiwang.user.about;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.utils.DensityUtil;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.client.result.ParsedResultType;
import com.mylhyl.zxing.scanner.encode.QREncode;

import java.util.HashMap;

import butterknife.BindView;
import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareCoinBeneActivity extends CoinbeneBaseActivity implements PlatformActionListener {

	@BindView(R2.id.share_back_img)
	ImageView shareBackImg;
	@BindView(R2.id.share_verification_code)
	ImageView codeImg;
	@BindView(R2.id.share_code)
	TextView shareCode;
	@BindView(R2.id.save_code)
	TextView saveCode;
	@BindView(R2.id.share_title_bar)
	RelativeLayout titleBar;
	private BottomSheetDialog bottomSheetDialog;
	private View shareWechart;
	private View shareFrend;
	private View shareFaceBook;
	private View shareTwitter;
	private View cancelShare;
	private Bitmap verificationCode;
	private Bitmap logoBitmap;
	private int codeSize;
	private Handler handler = new Handler(Looper.myLooper());

	public static void startMe(Context context) {
		Intent intent = new Intent(context, ShareCoinBeneActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_activity_share;
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (verificationCode != null) {
			verificationCode.recycle();
			verificationCode = null;
		}

		if (logoBitmap != null) {
			logoBitmap.recycle();
			logoBitmap = null;
		}
	}

	/**
	 * 保存到相册
	 */
	private void saveIntoAlbum() {
		//下载将要保存的图片
		Glide.with(getApplicationContext())
				.asBitmap()
				.load(getShareImg())
				.into(new SimpleTarget<Bitmap>() {
					@Override
					public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
						//检查权限
						if (ContextCompat.checkSelfPermission(ShareCoinBeneActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
							ActivityCompat.requestPermissions(ShareCoinBeneActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12580);
						} else {
							//保存图片
							if (PhotoUtils.saveImageToGallery(ShareCoinBeneActivity.this, resource)) {
								ToastUtil.show(getString(R.string.save_suc));
							} else {
								ToastUtil.show(getString(R.string.save_err));
							}
						}
					}

					@Override
					public void onLoadFailed(@Nullable Drawable errorDrawable) {
						super.onLoadFailed(errorDrawable);
						ToastUtil.show(getString(R.string.get_url_failed));
					}
				});
	}

	/**
	 * 请求权限回调
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length == 0) {
			return;
		}
		switch (requestCode) {
			//调用系统相册申请Sdcard权限回调
			case 12580:
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					if (verificationCode != null) {
						if (PhotoUtils.saveImageToGallery(this, verificationCode)) {
							ToastUtil.show(getString(R.string.save_suc));
						} else {
							ToastUtil.show(getString(R.string.save_err));
						}
					}
				}
				break;
			default:
		}
	}


	/**
	 * 初始化View
	 */
	public void init() {
		//获取当前站点
		String site = SpUtil.get(this, SpUtil.PRE_SITE_SELECTED, "");

		//   如果支持获取状态栏高度
		int stateBar = getStateBar() + DensityUtil.dip2px(10);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(0, stateBar, 0, 0);
		titleBar.setLayoutParams(layoutParams);

		// bottomsheet初始化&内部控件初始化
		bottomSheetDialog = new BottomSheetDialog(this);
		bottomSheetDialog.setContentView(R.layout.bottom_sheet_layout);
		shareWechart = bottomSheetDialog.findViewById(R.id.share_wechart);
		shareFrend = bottomSheetDialog.findViewById(R.id.share_frend);
		shareFaceBook = bottomSheetDialog.findViewById(R.id.share_facebook);
		shareTwitter = bottomSheetDialog.findViewById(R.id.share_twitter);
		cancelShare = bottomSheetDialog.findViewById(R.id.share_cancel_text);

		//韩国站隐藏微信
		if ("KO".equals(site)) {
			shareWechart.setVisibility(View.GONE);
			shareFrend.setVisibility(View.GONE);
		}

		String downLoadUrl = Constants.DOANLOAD_APK_QR_CODE_URL;
		downLoadUrl = UrlUtil.replaceH5Url(downLoadUrl);

		// 生成下载App二维码
		codeSize = DensityUtil.dip2px(222);
		logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_logo_bitmap);
		verificationCode = new QREncode.Builder(this)
				.setMargin(0)
				.setLogoBitmap(logoBitmap)
				.setParsedResultType(ParsedResultType.TEXT)
				.setContents(downLoadUrl)
				.setSize(codeSize)
				.build().encodeAsBitmap();

		codeImg.setImageBitmap(verificationCode);

		initListener();

	}

	/**
	 * 初始化监听
	 */
	public void initListener() {
		shareBackImg.setOnClickListener(this);
		cancelShare.setOnClickListener(this);
		saveCode.setOnClickListener(this);
		shareCode.setOnClickListener(this);
		shareWechart.setOnClickListener(this);
		shareFaceBook.setOnClickListener(this);
		shareFrend.setOnClickListener(this);
		shareTwitter.setOnClickListener(this);
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();//返回
		if (id == R.id.share_back_img) {
			finish();

			//显示bottomsheet
		} else if (id == R.id.share_code) {
			bottomSheetDialog.show();

			//保存二维码
		} else if (id == R.id.save_code) {
			saveIntoAlbum();

			//分享微信
		} else if (id == R.id.share_wechart) {
			new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImageurl(getShareImg())
					.setPlatformActionListener(this)
					.share();

			//分享微信朋友圈
		} else if (id == R.id.share_frend) {
			new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImageurl(getShareImg())
					.setPlatformActionListener(this)
					.share();

			//分享Facebook
		} else if (id == R.id.share_facebook) {
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setImageurl(getShareImg())
					.setPlatformActionListener(this)
					.share();

			//分享Twitter
		} else if (id == R.id.share_twitter) {
			new ShareUtils.TwitterBuilder(Twitter.NAME)
					.setText("CoinBene")
					.setImageurl(getShareImg())
					.setPlatformActionListener(this)
					.share();
		} else if (id == R.id.share_cancel_text) {
			bottomSheetDialog.dismiss();
		}
	}


	/**
	 * 获取状态栏高度
	 */
	private int getStateBar() {
		int result = 60;
		int resourceId = this.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = this.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}


	/**
	 * 根据语言获得分享图片
	 */
	public String getShareImg() {
		if (LanguageHelper.isChinese(this)) {
			return "http://res.coinbene.mobi/coinbene-activity/d124e34473a6d648.jpg";
		} else if (LanguageHelper.isEnglish(this)) {
			return "http://res.coinbene.mobi/coinbene-activity/b8b5160873aa3ee0.jpg";
		} else if (LanguageHelper.isKorean(this)) {
			return "http://res.coinbene.mobi/coinbene-activity/e031ef0073ab4b0b.jpg";
		} else if (LanguageHelper.isPortuguese(this)) {
			return "http://res.coinbene.mobi/coinbene-activity/ccf6df7873abd1d0.jpg";
		} else if (LanguageHelper.isJapanese(this)) {
			return "http://res.coinbene.mobi/coinbene-activity/433728c2acc50a2.png";
		} else {
			return "http://res.coinbene.mobi/coinbene-activity/b8b5160873aa3ee0.jpg";
		}
	}


	/**
	 * 分享回调
	 */

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(R.string.share_succes);
		bottomSheetDialog.dismiss();
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		Log.e("share", throwable.getMessage());

	}

	@Override
	public void onCancel(Platform platform, int i) {
		bottomSheetDialog.dismiss();
	}


}
