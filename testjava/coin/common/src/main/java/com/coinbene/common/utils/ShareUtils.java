package com.coinbene.common.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.view.View;

import com.coinbene.common.R;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.twitter.Twitter;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.whatsapp.WhatsApp;


/**
 * @author ding
 */
public class ShareUtils {

	static final String TAG = "ShareUtils";

	/**
	 * 微信分享
	 */
	public static class WechartBuilder {
		private Wechat.ShareParams params;
		private Platform weChart;

		/**
		 *
		 */
		public WechartBuilder(String platform, int shareType) {
			params = new Wechat.ShareParams();
			params.setShareType(shareType);
			weChart = ShareSDK.getPlatform(platform);
		}

		/**
		 * 设置标题
		 */
		public WechartBuilder setTitle(String title) {

			if (!TextUtils.isEmpty(title)) {
				params.setTitle(title);
			}

			return this;
		}


		/**
		 * 设置图片链接
		 */
		public WechartBuilder setImageurl(String imageUrl) {
			if (!TextUtils.isEmpty(imageUrl)) {
				params.setImageUrl(imageUrl);
			}
			return this;
		}


		/**
		 * 设置图片路径
		 */
		public WechartBuilder setImagePath(String imagePath) {
			if (!TextUtils.isEmpty(imagePath)) {
				params.setImagePath(imagePath);
			}
			return this;
		}


		/**
		 * 设置视屏文件路径
		 */
		public WechartBuilder setFilePath(String filePath) {
			if (!TextUtils.isEmpty(filePath)) {
				params.setUrl(filePath);
			}
			return this;
		}


		/**
		 * 设置内容
		 */
		public WechartBuilder setText(String text) {

			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}

			return this;
		}

		/**
		 * 设置网页链接
		 */
		public WechartBuilder setUrl(String link) {

			if (!TextUtils.isEmpty(link)) {
				params.setUrl(link);
			}

			return this;
		}


		/**
		 * 设置分享图片Bitmap
		 */
		public WechartBuilder setBitmap(Bitmap bitmap) {
			if (bitmap != null) {
				params.setImageData(bitmap);
			}
			return this;
		}


		public WechartBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				weChart.setPlatformActionListener(listener);
			}
			return this;
		}

		/**
		 * 分享
		 */
		public void share() {

			if (!weChart.isClientValid()) {
				ToastUtil.show(R.string.client_not_installed);
				return;
			}

			if (weChart != null && params != null) {
				weChart.share(params);
			}

		}

	}


	/**
	 * FaceBook分享
	 */
	public static class FaceBookBuilder {
		private Facebook.ShareParams params;
		private Platform facebook;

		public FaceBookBuilder(String platform) {
			params = new Facebook.ShareParams();
			facebook = ShareSDK.getPlatform(platform);

		}

		public FaceBookBuilder setText(String text) {
			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}
			return this;
		}

		public FaceBookBuilder setTitle(String title) {
			if (!TextUtils.isEmpty(title)) {
				params.setTitle(title);
			}
			return this;
		}

		/**
		 * 设置网页链接
		 */
		public FaceBookBuilder setUrl(String link) {
			if (!TextUtils.isEmpty(link)) {
				params.setUrl(link);
			}
			return this;
		}

		/**
		 * 设置网页链接
		 */
		public FaceBookBuilder setBitmap(Bitmap bitmap) {
			if (bitmap != null) {
				params.setImageData(bitmap);
			}
			return this;
		}


		public FaceBookBuilder setImageurl(String imageUrl) {
			if (!TextUtils.isEmpty(imageUrl)) {
				params.setImageUrl(imageUrl);
			}
			return this;
		}

		public FaceBookBuilder setImagePath(String imagePath) {
			if (!TextUtils.isEmpty(imagePath)) {
				params.setImagePath(imagePath);
			}
			return this;
		}

		public FaceBookBuilder setFilePath(String filePath) {
			if (!TextUtils.isEmpty(filePath)) {
				params.setUrl(filePath);
			}
			return this;
		}


		public FaceBookBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				facebook.setPlatformActionListener(listener);
			}
			return this;
		}

		public void share() {

			if (!facebook.isClientValid()) {
				ToastUtil.show(R.string.client_not_installed);
				return;
			}

			facebook.share(params);
		}
	}



	/**
	 * FaceBook分享
	 */
	public static class WhatsAppBuilder {
		private WhatsApp.ShareParams params;
		private Platform whatsApp;

		public WhatsAppBuilder(String platform) {
			params = new WhatsApp.ShareParams();
			whatsApp = ShareSDK.getPlatform(platform);

		}

		public WhatsAppBuilder setText(String text) {
			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}
			return this;
		}

		public WhatsAppBuilder setTitle(String title) {
			if (!TextUtils.isEmpty(title)) {
				params.setTitle(title);
			}
			return this;
		}

		/**
		 * 设置网页链接
		 */
		public WhatsAppBuilder setUrl(String link) {
			if (!TextUtils.isEmpty(link)) {
				params.setUrl(link);
			}
			return this;
		}

		/**
		 * 设置网页链接
		 */
		public WhatsAppBuilder setBitmap(Bitmap bitmap) {
			if (bitmap != null) {
				params.setImageData(bitmap);
			}
			return this;
		}


		public WhatsAppBuilder setImageurl(String imageUrl) {
			if (!TextUtils.isEmpty(imageUrl)) {
				params.setImageUrl(imageUrl);
			}
			return this;
		}

		public WhatsAppBuilder setImagePath(String imagePath) {
			if (!TextUtils.isEmpty(imagePath)) {
				params.setImagePath(imagePath);
			}
			return this;
		}

		public WhatsAppBuilder setFilePath(String filePath) {
			if (!TextUtils.isEmpty(filePath)) {
				params.setUrl(filePath);
			}
			return this;
		}


		public WhatsAppBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				whatsApp.setPlatformActionListener(listener);
			}
			return this;
		}

		public void share() {

			if (!whatsApp.isClientValid()) {
				ToastUtil.show(R.string.client_not_installed);
				return;
			}

			whatsApp.share(params);
		}
	}


	/**
	 * @docment Twitter分享平台
	 */
	public static class TwitterBuilder {
		private Twitter.ShareParams params;
		private Platform twitter;

		public TwitterBuilder(String platform) {
			params = new Twitter.ShareParams();
			twitter = ShareSDK.getPlatform(platform);
		}

		/**
		 * 分享文字
		 */
		public TwitterBuilder setText(String text) {
			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}
			return this;
		}


		public TwitterBuilder setImageurl(String imageUrl) {
			if (!TextUtils.isEmpty(imageUrl)) {
				params.setImageUrl(imageUrl);
			}
			return this;
		}

		public TwitterBuilder setImagePath(String imagePath) {
			if (!TextUtils.isEmpty(imagePath)) {
				params.setImagePath(imagePath);
			}
			return this;
		}

		public TwitterBuilder setFilePath(String filePath) {
			if (!TextUtils.isEmpty(filePath)) {
				params.setFilePath(filePath);
			}
			return this;
		}

		public TwitterBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				twitter.setPlatformActionListener(listener);
			}
			return this;
		}

		public void share() {
			twitter.share(params);
		}
	}


	/**
	 * 新浪平台
	 */
	public static class SinaBuilder {
		Platform.ShareParams params;
		Platform sina;

		public SinaBuilder(String sina) {
			this.params = new Platform.ShareParams();
			this.sina = ShareSDK.getPlatform(sina);
		}

		public SinaBuilder setText(String text) {
			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}
			return this;
		}

		public SinaBuilder setImageUrl(String url) {
			if (!TextUtils.isEmpty(url)) {
				params.setImageUrl(url);
			}
			return this;
		}

		public SinaBuilder setBitmap(Bitmap bitmap) {
			if (bitmap != null) {
				params.setImageData(bitmap);
			}
			return this;
		}

		public SinaBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				sina.setPlatformActionListener(listener);
			}
			return this;
		}

		public void share() {

			if (!sina.isClientValid()) {
				ToastUtil.show(R.string.client_not_installed);
				return;
			}

			if (sina != null && params != null) {
				sina.share(params);
			}
		}


	}

	/**
	 * qq分享
	 */
	public static class QQBuilder {
		private QQ.ShareParams params;
		private Platform qq;

		public QQBuilder() {
			params = new QQ.ShareParams();
			qq = ShareSDK.getPlatform(QQ.NAME);
		}

		public QQBuilder setText(String text) {
			if (!TextUtils.isEmpty(text)) {
				params.setText(text);
			}
			return this;
		}


		public QQBuilder setTitle(String title) {
			if (!TextUtils.isEmpty(title)) {
				params.setTitle(title);
			}
			return this;
		}

		public QQBuilder setSite(String site) {
			if (!TextUtils.isEmpty(site)) {
				params.setSite(site);
			}
			return this;
		}


		public QQBuilder setTitleUrl(String titleUrl) {
			if (!TextUtils.isEmpty(titleUrl)) {
				params.setTitleUrl(titleUrl);
			}
			return this;
		}

		public QQBuilder setImageUrl(String url) {
			if (!TextUtils.isEmpty(url)) {
				params.setImageUrl(url);
			}
			return this;
		}

		public QQBuilder setImagePath(String path) {
			if (!TextUtils.isEmpty(path)) {
				params.setImagePath(path);
			}
			return this;
		}

		public QQBuilder setPlatformActionListener(PlatformActionListener listener) {
			if (listener != null) {
				qq.setPlatformActionListener(listener);
			}
			return this;
		}

		public void share() {

			if (!qq.isClientValid()) {
				ToastUtil.show(R.string.client_not_installed);
				return;
			}

			if (qq != null && params != null) {
				qq.share(params);
			}
		}

	}

	/**
	 * @return bitmap
	 * 截图
	 */
	public static Bitmap screenShot(View view) {
		//关闭硬件加速，否则截图为初始绘制
		view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth()*2, view.getHeight()*2, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.scale(2f,2f);
		view.draw(canvas);
		return bitmap;
	}

}
