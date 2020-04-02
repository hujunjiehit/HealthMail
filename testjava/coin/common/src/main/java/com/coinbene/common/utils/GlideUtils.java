package com.coinbene.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.coinbene.common.R;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;

import java.io.File;

/**
 * glide utils
 */
public class GlideUtils {

	private RequestOptions mOptions, mCoinOptions, autoHeightOption, blackOptions, mCoinNoHolderOptions;

	//在装载该内部类时才会去创建单例对象
	private static class Singleton {
		private static GlideUtils ourInstance = new GlideUtils();
	}

	public static GlideUtils newInstance() {
		return Singleton.ourInstance;
	}

	public GlideUtils() {
		mOptions = new RequestOptions()
				.centerCrop()
				.placeholder(R.drawable.icon_white_back)
				.error(R.drawable.icon_white_back)
				.priority(Priority.HIGH)
				.diskCacheStrategy(DiskCacheStrategy.ALL);
		mCoinOptions = new RequestOptions()
				.centerCrop()
				.placeholder(R.drawable.coin_default_icon)
				.error(R.drawable.coin_default_icon)
				.priority(Priority.HIGH)
				.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
		mCoinNoHolderOptions = new RequestOptions()
				.centerCrop()
//                .placeholder(R.drawable.coin_default_icon)
				.error(R.drawable.coin_default_icon)
				.priority(Priority.HIGH)
				.diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
		autoHeightOption = new RequestOptions()
				.centerCrop()
				.placeholder(R.drawable.icon_white_back)
				.error(R.drawable.icon_white_back)
				.priority(Priority.HIGH)
				.override(DensityUtil.getScreenWidth(), Target.SIZE_ORIGINAL)
				.diskCacheStrategy(DiskCacheStrategy.ALL);

		blackOptions = new RequestOptions()
				.fitCenter()
				.placeholder(R.color.black)
				.error(R.color.black)
				.priority(Priority.HIGH)
				.diskCacheStrategy(DiskCacheStrategy.ALL);

	}

	// 加载网络图片
	public void loadNetImage(String url, ImageView iv) {
		Glide.with(CBRepository.getContext())
				.load(url)
				.apply(mOptions)
				.transition(new DrawableTransitionOptions().crossFade(500))
				.into(iv);
	}

	public void loadNetImageCoinIcon(String url, ImageView iv) {
		Glide.with(CBRepository.getContext())
				.load(url)
				.apply(mCoinOptions)
				.transition(new DrawableTransitionOptions().crossFade(500))
				.into(iv);
	}

	public void loadNetImageCoinIconNoHolder(String url, ImageView iv) {
		Glide.with(CBRepository.getContext())
				.load(url)
				.apply(mCoinNoHolderOptions)
				.transition(new DrawableTransitionOptions().crossFade(500))
				.into(iv);
	}

	// 加载自适应高度的ImageView
	public void loadAutoHeightNetImage(String url, ImageView iv) {
		Glide.with(CBRepository.getContext())
				.load(url)
				.apply(autoHeightOption)
				.transition(new DrawableTransitionOptions().crossFade(500))
				.into(iv);
	}

	public void loadBlackDefaultImage(String url, ImageView iv) {
		Glide.with(CBRepository.getContext())
				.load(url)
				.apply(blackOptions)
				.into(iv);
	}


	public static void loadImageViewLoad(Context mContext, String baseUrl, String url, ImageView mImageView, int lodingImage, int errorImageView) {
		UserInfoTable table = UserInfoController.getInstance().getUserInfo();
		String localeCode = LanguageHelper.getLocaleCode(mContext);
		if (table != null && !TextUtils.isEmpty(table.token)) {
			GlideUrl glideUrl = new GlideUrl(baseUrl + url, new LazyHeaders.Builder()
					.addHeader("Authorization", "Bearer " + table.token)
					.addHeader("site", SiteController.getInstance().getSiteName())
					.addHeader("clientData", NetUtil.getSystemParam().toString())
					.addHeader("lang", LanguageHelper.getProcessedCode(localeCode))
					.addHeader("timezone", TimeUtils.getCurrentTimeZone())
					.addHeader("bank", table.bank)
					.build());
			Glide.with(mContext).load(glideUrl).apply(new RequestOptions().placeholder(lodingImage).centerCrop().error(errorImageView)).into(mImageView);
		} else {
			Glide.with(mContext).load(baseUrl + url).apply(new RequestOptions().placeholder(lodingImage).centerCrop().error(errorImageView)).into(mImageView);
		}
	}

	public static void loadImageViewLoad(Context mContext, String url, ImageView mImageView, int lodingImage, int errorImageView) {
		Glide.with(mContext).load(url).apply(new RequestOptions().placeholder(lodingImage).centerCrop().error(errorImageView)).into(mImageView);
	}

	public static void loadFile(Context mContext, File file, ImageView mImageView) {
		Glide.with(mContext).load(file).into(mImageView);

	}

	@SuppressLint("NewApi")
	public static void loadImageViewDrawable(Context mContext, int drawable, ImageView mImageView, int lodingImage, int errorImageView) {
		Glide.with(mContext).load(mContext.getDrawable(drawable)).apply(new RequestOptions().placeholder(lodingImage).centerCrop().error(errorImageView)).into(mImageView);
	}

	public static void asGif(ImageView view, @DrawableRes int drawableRes) {
		Glide.with(view.getContext())
				.asGif()
				.load(drawableRes)
				.into(view);
	}

	public static void asGif(ImageView view, @DrawableRes int drawableRes, RequestOptions options) {
		Glide.with(view.getContext())
				.asGif()
				.load(drawableRes)
				.apply(options)
				.into(view);
	}
}
