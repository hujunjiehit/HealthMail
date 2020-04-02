package com.coinbene.common.dialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Group;

import com.coinbene.common.R;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.ShareUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * ding
 * 2019-10-18
 * com.coinbene.common.dialog
 */
public class ShareMenu extends BottomSheetDialog implements PlatformActionListener {

	private View tvCancel;
	private View imgWechat;
	private View imgWechatCircle;
	private View imgQQ;
	private View imgSina;
	private View imgFaceBook;
	private View imgSave;
	private Group groupSave;


	private String PATH;
	private int SHARE_TYPE;
	private String imgBase64;
	private Bitmap bitmap;
	private boolean ifNeedSave = false;
	private ImageView imgContent;


	public ShareMenu(@NonNull Context context) {
		super(context,R.style.CoinBene_BottomSheet);

		Window window = getWindow();
		if (window == null) {
			return;
		}
		WindowManager.LayoutParams wmlp = window.getAttributes();
		wmlp.height = ViewGroup.LayoutParams.MATCH_PARENT;
		window.setAttributes(wmlp);
		PATH = context.getExternalCacheDir().getPath() + "/";
		setContentView(R.layout.dialog_share_menu);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tvCancel = findViewById(R.id.tv_cancel);
		imgWechat = findViewById(R.id.img_wechat);
		imgWechatCircle = findViewById(R.id.img_wechat_circle);
		imgQQ = findViewById(R.id.img_QQ);
		imgSina = findViewById(R.id.img_sina);
		imgFaceBook = findViewById(R.id.img_facebook);
		imgSave = findViewById(R.id.img_save);
		groupSave = findViewById(R.id.group_Save);
		imgContent = findViewById(R.id.img_Content);

		listener();

	}

	private void listener() {
		tvCancel.setOnClickListener(v -> dismiss());
		//QQ分享
		imgQQ.setOnClickListener(v -> {
			String imgPath = FileUtils.saveBitmap(PATH, "CoinBene.JPG", bitmap);
			new ShareUtils.QQBuilder()
					.setImagePath(imgPath)
					.setPlatformActionListener(this)
					.share();
		});

		//微信好友分享
		imgWechat.setOnClickListener(v -> {
			String imgPath = FileUtils.saveBitmap(PATH, "CoinBene.JPG", bitmap);
			new ShareUtils.WechartBuilder(Wechat.NAME, SHARE_TYPE)
					.setTitle("CoinBene")
					.setImagePath(imgPath)
					.setPlatformActionListener(this)
					.share();
		});

		//微信朋友圈分享
		imgWechatCircle.setOnClickListener(v -> {
			new ShareUtils.WechartBuilder(WechatMoments.NAME, SHARE_TYPE)
					.setTitle("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		//Facebook分享
		imgFaceBook.setOnClickListener(v -> {
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		// 新浪分享
		imgSina.setOnClickListener(v -> {
			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});
	}

	@Override
	public void show() {
		super.show();
		if (ifNeedSave) {
			groupSave.setVisibility(View.VISIBLE);
		}

		if (!TextUtils.isEmpty(imgBase64)) {
			//Base64转图片并设置到ImageView
			String[] split = imgBase64.split(",");

			InputStream input = null;
			byte[] decode = null;

			try {
				decode = Base64.decode(split[1], Base64.DEFAULT);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 1;
				input = new ByteArrayInputStream(decode);
				SoftReference softRef = new SoftReference(BitmapFactory.decodeStream(input, null, options));
				bitmap = (Bitmap)softRef.get();

				imgContent.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				decode = null;
			}
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
	 * @param b 需要保存选项 参数为true
	 */
	public ShareMenu setSaveEnable(boolean b) {
		this.ifNeedSave = b;
		return this;
	}

	public void setShareType(int type) {
		this.SHARE_TYPE = type;
	}

	public void setImageBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}


	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {

	}

	@Override
	public void onCancel(Platform platform, int i) {

	}
}
