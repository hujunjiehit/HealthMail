package com.coinbene.common.widget;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.FileUtils;
import com.coinbene.common.utils.PhotoUtils;
import com.coinbene.common.utils.ShareUtils;
import com.coinbene.common.utils.ToastUtil;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.HashMap;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class GameShareFragment extends DialogFragment implements PlatformActionListener {

	private Bitmap bitmap;
	public static String IMAGE_DECODE;
	private View imgWechat;
	private View imgWechatCircle;
	private View imgQQ;
	private View imgFaceBook;
	private View imgSina;
	private View imgSave;
	private ImageView imgShareImage;

	private String filePath;
	private View layoutRoot;

	public static GameShareFragment init(String decode) {
		IMAGE_DECODE = decode;
		return new GameShareFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, R.style.Dialog_FullScreen);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.common_fragment_game_share, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init(view);
		listener();
	}

	public void init(View view) {
		if (TextUtils.isEmpty(IMAGE_DECODE)) {
			dismiss();
			return;
		}

		filePath = getContext().getExternalCacheDir().getPath() + "/";

		imgWechat = view.findViewById(R.id.img_wechat);
		imgWechatCircle = view.findViewById(R.id.img_wechat_circle);
		imgQQ = view.findViewById(R.id.img_QQ);
		imgFaceBook = view.findViewById(R.id.img_facebook);
		imgSina = view.findViewById(R.id.img_sina);
		imgSave = view.findViewById(R.id.img_save);
		imgShareImage = view.findViewById(R.id.img_ShareImage);
		layoutRoot = view.findViewById(R.id.layout_root);

		//Base64转图片并设置到ImageView
		String[] split = IMAGE_DECODE.split(",");
		byte[] decode = Base64.decode(split[1], Base64.DEFAULT);
		bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
		imgShareImage.setImageBitmap(bitmap);
	}


	private void listener() {
		layoutRoot.setOnClickListener(v -> dismiss());

		imgQQ.setOnClickListener(v -> {
			String imagePath = FileUtils.saveBitmap(filePath, "CoinBene_Option.JPG", bitmap);
			new ShareUtils.QQBuilder()
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		imgSina.setOnClickListener(v -> {
			new ShareUtils.SinaBuilder(SinaWeibo.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		imgWechat.setOnClickListener(v -> {
			String imagePath = FileUtils.saveBitmap(filePath, "CoinBene_Option.JPG", bitmap);
			new ShareUtils.WechartBuilder(Wechat.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setImagePath(imagePath)
					.setPlatformActionListener(this)
					.share();
		});

		imgWechatCircle.setOnClickListener(v -> {
			new ShareUtils.WechartBuilder(WechatMoments.NAME, Platform.SHARE_IMAGE)
					.setTitle("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		imgFaceBook.setOnClickListener(v -> {
			new ShareUtils.FaceBookBuilder(Facebook.NAME)
					.setText("CoinBene")
					.setBitmap(bitmap)
					.setPlatformActionListener(this)
					.share();
		});

		imgSave.setOnClickListener(v -> {
			AndPermission.with(imgSave.getContext())
					.runtime()
					.permission(Permission.WRITE_EXTERNAL_STORAGE)
					.onGranted(data -> {
						PhotoUtils.saveImageToGallery(getContext(), bitmap);
						ToastUtil.show(getContext().getString(R.string.save_suc));
					}).start();
		});
	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (bitmap != null) {
			bitmap.recycle();
			bitmap = null;
		}

		IMAGE_DECODE = null;
	}

	@Override
	public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
		ToastUtil.show(getString(R.string.share_succes));
	}

	@Override
	public void onError(Platform platform, int i, Throwable throwable) {
		ToastUtil.show(R.string.share_failed);
	}

	@Override
	public void onCancel(Platform platform, int i) {

	}
}
