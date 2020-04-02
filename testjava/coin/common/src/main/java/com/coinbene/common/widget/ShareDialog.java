package com.coinbene.common.widget;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.text.TextUtils;
import android.view.View;

import com.coinbene.common.R;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.ToastUtil;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ShareDialog extends BottomSheetDialog implements PlatformActionListener {

	static final String TAG = "ShareDialog";

	private String cachePath;

	private View cancel;
	private View wechat;
	private View qq;
	private View facebook;
	private View twitter;
	private View frend;
	private View sina;
	private View save;
	private View copy;

	private CoinbeneShareParam param;

	public ShareDialog(@NonNull Context context) {
		super(context);
		setContentView();
	}

	public ShareDialog(@NonNull Context context, int theme) {
		super(context, theme);
		setContentView();
	}

	protected ShareDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		setContentView();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		listener();
	}

	private void setContentView() {
		setContentView(R.layout.common_dialog_share);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public void setParam(CoinbeneShareParam param) {
		if (TextUtils.isEmpty(param.getUrl())) {
			param.setIntType(Platform.SHARE_TEXT);
		} else {
			param.setIntType(Platform.SHARE_WEBPAGE);
		}
		this.param = param;
	}

	public void initView() {
		cancel = findViewById(R.id.cancel);
		wechat = findViewById(R.id.share_wechat);
		qq = findViewById(R.id.share_qq);
		facebook = findViewById(R.id.share_facebook);
		twitter = findViewById(R.id.share_twitter);
		frend = findViewById(R.id.share_frend);
		sina = findViewById(R.id.share_sina);
		save = findViewById(R.id.share_save);
		copy = findViewById(R.id.share_copy);

	}

	public void listener() {
		qq.setOnClickListener(v -> shareQQ());
		cancel.setOnClickListener(v -> dismiss());
		wechat.setOnClickListener(v -> shareWechat());
		frend.setOnClickListener(v -> shareFrend());
		sina.setOnClickListener(v -> shareSina());
		save.setOnClickListener(v -> saveImage());
		copy.setOnClickListener(v -> copyLink());
		facebook.setOnClickListener(v -> shareFaceBook());
		twitter.setOnClickListener(v -> shareTwitter());
	}


	private void shareWechat() {
		if (param != null) {
			new ShareUtils.WechartBuilder(Wechat.NAME, param.getIntType())
					.setTitle(param.getTitle())
					.setText(param.getMessage())
					.setUrl(param.getUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void shareFrend() {
		if (param != null) {
			new ShareUtils.WechartBuilder(WechatMoments.NAME, param.getIntType())
					.setTitle(param.getTitle())
					.setText(param.getMessage())
					.setUrl(param.getUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void shareFaceBook() {
		if (param != null) {
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setTitle(param.getTitle())
					.setText(param.getMessage())
					.setUrl(param.getUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void shareTwitter() {
		if (param != null) {
			new ShareUtils.TwitterBuilder(Twitter.NAME)
					.setText(param.getUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void shareSina() {
		if (param != null) {
			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText(param.getUrl())
					.setImageUrl(param.getImageUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void shareQQ() {
		if (param != null) {
			new ShareUtils.QQBuilder()
					.setTitle(param.getTitle())
					.setText(param.getMessage())
					.setTitleUrl(param.getUrl())
					.setImageUrl(param.getImageUrl())
					.setPlatformActionListener(this)
					.share();
		}
	}

	private void saveImage() {
		//检查权限
//        if (ContextCompat.check(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12580);
//        } else {
//            PhotoUtils.saveImageToGallery(getContext(), screenShot());
//            ToastUtil.show(getContext().getResources().getString(R.string.save_suc));
//        }
	}

	private void copyLink() {
//        ClipboardManager cm = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        ClipData mClipData = ClipData.newPlainText("invite", inviteUrl.toString());
//        cm.setPrimaryClip(mClipData);
//        ToastUtil.show(R.string.qr_copy_success);
	}


	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(getContext().getString(R.string.share_succes));
		this.dismiss();
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		ToastUtil.show(getContext().getString(R.string.share_failed));
		this.dismiss();
	}

	@Override
	public void onCancel(Platform platform, int i) {
		this.dismiss();
	}

}
