package com.coinbene.manbiwang.modules.common.splash;

import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.coinbene.common.Constants;
import com.coinbene.common.base.BaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.MainActivity;
import com.coinbene.manbiwang.R;
import com.coinbene.manbiwang.model.http.AdResponse;
import com.coinbene.manbiwang.service.AdDownloadService;
import com.coinbene.manbiwang.service.ServiceRepo;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * 先播放动画，动画大约需要3秒，结束后，查找图片是否下载完成；如果没有下载完成，则直接进入系统；否则展示图片
 */

public class SplashActivity extends BaseActivity implements View.OnClickListener {

	//    private static final long delayMillis = 5000;
	private Handler handler;
	private ImageView adImageView;
	private String url;
	private static final int MSG_FINISH_DELAY = 1;
	private static final int MSG_SHOW_IMG = 2;
	private TextView skipTv;
	private boolean isShowImg;
	private AdResponse.DataBean adData;
	private boolean isAnimtionEnd;
	private LottieAnimationView animationView;

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			getWindow().setNavigationBarColor(getResources().getColor(com.coinbene.manbiwang.kline.R.color.black));
		}

		//  防止二次进入Splash
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0 || CBRepository.isStartedMainActivity()) {
			if ((getIntent().getFlags() & Intent.FLAG_RECEIVER_FOREGROUND) != 0) {
				//扫码安装会有这个flag
				if (getIntent().getData() == null) {
					finish();
					return;
				}
			}
			finish();
			startMainActivity();
			return;
		}

		//        PrintKeyHash.printKeyHash(SplashActivity.this);
		//收集第一次启动信息
		if (SwitchUtils.isFirstInstallApp()) {
			DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appInstall, "");
			SpUtil.put(this, SpUtil.IS_APP_FIRST_INSTALL, false);
		}
		DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appGroundFore, "");

		//上报
//        DataCollectionHanlder.getInstance().postClientInfo();


		setContentView(R.layout.activity_spalsh);
		setSwipeBackEnable(false);
		animationView = (LottieAnimationView) findViewById(R.id.lottie_view);
		animationView.useHardwareAcceleration(true);
		animationView.enableMergePathsForKitKatAndAbove(true);
		animationView.addAnimatorListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				isAnimtionEnd = true;
				if (isDownLoadFile) {
					sendShowImgMessage();
				} else {
					startMainActivity();
					finish();
				}
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});
		adImageView = (ImageView) findViewById(R.id.ad_imgview);
		adImageView.setOnClickListener(this);
		skipTv = (TextView) findViewById(R.id.skip_btn);
		skipTv.setOnClickListener(this);
		IntentFilter intentFilter = new IntentFilter(Constants.DOWNLOAD_IMAGE_SUCCESS);
		LocalBroadcastManager.getInstance(SplashActivity.this).registerReceiver(downloadFileSuccess_broadcastReceiver, intentFilter);

		initData();

		handler = new NextHandler();
		//        test();
		//        showGifImg();
	}

	private void test() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				url = "https://b-ssl.duitang.com/uploads/item/201711/09/20171109092231_GiNLe.thumb.1000_0.jpeg";
				adData = new AdResponse.DataBean();
				adData.showTime = 3000;
				checkLocalImgUrl(url);
			}
		}).start();
	}

	private void startMainActivity() {
		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
		if (getIntent() != null && getIntent().getData() != null) {
			intent.setData(getIntent().getData());
		}
		startActivity(intent);
	}


	private void initData() {
		if (ServiceRepo.getAppService() != null) {
			ServiceRepo.getAppService().getAppConfig(null,null);
//			ServiceRepo.getAppService().getSwitch(null, null);
			ServiceRepo.getAppService().getAdInfo(result -> returnAdInfo(result)); //获取广告信息
			ServiceRepo.getAppService().getContractList();
			ServiceRepo.getAppService().getContractUsdtList();
			ServiceRepo.getAppService().getOtcConfig();
//			ServiceRepo.getAppService().getActivitySwitch();
			ServiceRepo.getAppService().getOptional();
			ServiceRepo.getAppService().getMarginSymbolList();
			ServiceRepo.getAppService().getBalanceList();
			ServiceRepo.getAppService().getHotCoinList();
			ServiceRepo.getAppService().getBigCoinList();
			ServiceRepo.getAppService().getNotice();
			ServiceRepo.getAppService().getBanners();

		}
	}

	private void returnAdInfo(AdResponse adResponse) {
		if (adResponse == null) {
			return;
		}
		adData = adResponse.data;
		if (adData == null || adData.picInfos == null || adData.picInfos.size() == 0 || adData.showTime <= 0) {
			return;
		}
		if (SplashActivity.this.isFinishing()) {
			return;
		}
		url = adData.picInfos.get(0).picUrl;
		if (!TextUtils.isEmpty(url)) {
			//1：检查倒计时是否到 2：检查图片是否已经下载， 3 去下载图片，下载结束后 返回下载的图片 4 不用下载，直接去本地的图片展示
			new Thread(() -> checkLocalImgUrl(url)).start();
		}
	}

	private void checkLocalImgUrl(String url) {
		RequestOptions options = new RequestOptions();
		options.onlyRetrieveFromCache(true);
		File file = null;
		try {//需要在子线程中运行,不能在UI线程运行
			file = Glide.with(SplashActivity.this).downloadOnly().load(url).apply(options).submit().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if (file != null) {//本地存在，直接展示
			isDownLoadFile = true;
			if (isAnimtionEnd) {
				sendShowImgMessage();
			}
		} else {//本地不存在，开启服务下载图片；下载结束后，通过广播返回结果
			AdDownloadService.startMe(SplashActivity.this, url);
		}
	}

	private boolean isDownLoadFile;
	private BroadcastReceiver downloadFileSuccess_broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (SplashActivity.this.isFinishing()) {
				return;
			}
			if (adData == null) {
				return;
			}
			isDownLoadFile = true;
			if (isAnimtionEnd) {
				sendShowImgMessage();
			}
		}
	};

	private void sendShowImgMessage() {
		Message message = new Message();
		message.what = MSG_SHOW_IMG;
		handler.sendMessage(message);
	}


	class NextHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (isFinishing()) {
				return;
			}
			if (msg.what == MSG_FINISH_DELAY) {
				startMainActivity();
				finish();
			} else if (msg.what == MSG_SHOW_IMG) {
				showSkipView();
				showImgView();
			}
		}
	}

	private void showImgView() {
		if (adData.advertFormat == 0) {
			RequestOptions options = new RequestOptions();
			options.onlyRetrieveFromCache(true);
			Glide.with(SplashActivity.this).load(url).apply(options).into(adImageView);
		} else if (adData.advertFormat == 1) {
			//这部分逻辑这版本先不上
			//            showGifImg();
		}
	}

	private void showSkipView() {
		handler.removeCallbacksAndMessages(null);
		isShowImg = true;
		handler.sendEmptyMessageDelayed(MSG_FINISH_DELAY, adData.showTime * 1000);
		skipTv.setVisibility(View.VISIBLE);
	}

	private void showGifImg() {
		RequestOptions requestOptions = new RequestOptions();
		requestOptions.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
		url = "file:///android_asset/sun.gif";
		Glide.with(SplashActivity.this).asGif().apply(requestOptions).listener(new RequestListener<GifDrawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
				return false;
			}

			@Override
			public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
				return false;
			}
		}).load(url).into(adImageView);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.skip_btn) {
			handler.removeMessages(MSG_FINISH_DELAY);
			handler.removeMessages(MSG_SHOW_IMG);
			startMainActivity();
			finish();
		} else if (v.getId() == R.id.ad_imgview) {
			if (!isShowImg) {
				return;
			}
			if (TextUtils.isEmpty(adData.clickUrl)) {
				return;
			}
			handler.removeMessages(MSG_FINISH_DELAY);
			handler.removeMessages(MSG_SHOW_IMG);
			startMainActivity();
			//TODO: 进入广告页面
			goToAdActivity();
			finish();
		}
	}


	private void goToAdActivity() {
		if (TextUtils.isEmpty(adData.clickUrl)) {
			return;
		}
//        BrowserItem browserItem = new BrowserItem();
//        //        browserItem.title = getString(R.string.set_about_service_link);
//        String url = "";
//        if (adData.clickUrl.contains("http")) {
//            url = adData.clickUrl.trim();
//            browserItem.not_needUrlFilter = true;
//        } else {
//            //            url = Constants.BASE_URL_H5 + adData.clickUrl.trim();
//            url = CommonUtil.pathAppendUrl(Constants.BASE_URL_H5, adData.clickUrl.trim());
//        }
//        browserItem.url = url;
//        BrowserActivity.startMe(SplashActivity.this, browserItem, false);

		String linkUrl = adData.clickUrl;
		String finalUrl = UrlUtil.parseUrl(linkUrl);
		Bundle bundle = new Bundle();
		bundle.putString("SchemeFrom", SchemeFrom.APP_SPLASH.name());
		UIBusService.getInstance().openUri(SplashActivity.this, finalUrl, bundle);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (animationView != null) {
			animationView.cancelAnimation();
			animationView.removeAllAnimatorListeners();
			animationView = null;
		}

		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}

		LocalBroadcastManager.getInstance(SplashActivity.this).unregisterReceiver(downloadFileSuccess_broadcastReceiver);
	}
}
