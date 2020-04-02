package com.coinbene.manbiwang;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;

import androidx.core.content.res.ResourcesCompat;
import androidx.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.ActivityLifeCallback;
import com.coinbene.common.BuildConfig;
import com.coinbene.common.Constants;
import com.coinbene.common.config.ProductConfig;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.network.newokgo.BaseUrlReplaceIntercepter;
import com.coinbene.common.network.newokgo.DHttpLogInterceptor;
import com.coinbene.common.network.newokgo.TokenInterceptor;
import com.coinbene.common.router.UIBus;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.websocket.core.WebSocketManager;
import com.coinbene.manbiwang.debug.crash.CrashHelper;
import com.coinbene.manbiwang.networkcapture.CaptureInfoInterceptor;
import com.coinbene.manbiwang.route.CoinBeneUIRouter;
import com.coinbene.manbiwang.webview.route.WebviewUIRouter;
import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastAliPayStyle;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.mob.MobSDK;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.udesk.UdeskSDKManager;
import okhttp3.OkHttpClient;

/**
 * Created by june
 * on 2019-11-05
 */
public class CoinbeneApplication extends Application {

	private ActivityLifeCallback lifeCallback;
	private String channel_value;
	protected String processName;

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(LanguageHelper.setLocale(base));

		MultiDex.install(base);

		if (BuildConfig.DEBUG) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
			ARouter.openLog();     // 打印日志
			ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
		}

		//路由初始化
		ARouter.init(getApplication());

	}

	private Application getApplication() {
		return this;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		parseConfig();

		processName = getCurProcessName();

		CBRepository.init(getApplication());

		// 第三方SDK可能会重新启动Application，导致重复初始化
		if (!processName.equals(getApplication().getPackageName())) {
			return;
		}

		LanguageHelper.updateAppConfig(CBRepository.getContext(), LanguageHelper.getLocaleCode(this));

		ToastUtils.init(getApplication(), new ToastAliPayStyle());

		initWebsocket();

		registerLifeCallBack();

		UIBusService.getInstance().register(new CoinBeneUIRouter(), UIBus.PRIORITY_LOW);
		UIBusService.getInstance().register(new WebviewUIRouter());
//
		DayNightHelper.init(getApplication());

		setTypeface();

		// 网络请求OKGO
		initOkGo();
//
//		initGrowingIO();

		initBugly();

		initShare();

		//FOTA
		//OptionsDelegate.init();

		registPush();

		//屏幕适配支持副单位，没有用到
//		AutoSizeConfig.getInstance()
//				.setCustomFragment(true)
//				.getUnitsManager()
//				.setSupportSubunits(Subunits.PT);

//		AutoSizeConfig.getInstance().setExcludeFontScale(true);

		//  解决屏幕适配对期权造成的影响
//		AutoSizeConfig.getInstance()
//				.getExternalAdaptManager()
//				.addCancelAdaptOfActivity(OptionSdkActivity.class)
//				.addCancelAdaptOfActivity(HistoryFragment.class)
//				.addCancelAdaptOfActivity(IndexFragment.class);

		//        在线客服初始化
		if (ConfigHelper.getSupportOnlineConfig().isEnable()){
			ProductConfig.SupportOnlineConfig config = ConfigHelper.getSupportOnlineConfig();
			UdeskSDKManager.getInstance().initApiKey(getApplication(), config.getDomain(), config.getAppKey(), config.getAppID());
		}

		initCrashHelper();
	}

	private void parseConfig() {
		StringBuilder newstringBuilder = new StringBuilder();
		InputStream inputStream = null;
		BufferedReader reader = null;
		InputStreamReader inputStreamReader = null;
		try {
			inputStream = getResources().getAssets().open("config.json");
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);
			String jsonLine;
			while ((jsonLine = reader.readLine()) != null) {
				newstringBuilder.append(jsonLine);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (inputStreamReader != null) {
					inputStreamReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String result = newstringBuilder.toString();

		Gson gson = new Gson();
		ProductConfig productConfig = gson.fromJson(result, ProductConfig.class);
		ConfigHelper.setConfig(productConfig);
	}

	public String getChannel(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);

			CBRepository.versionCode = packageInfo.versionCode;
			CBRepository.versionName = packageInfo.versionName;

			return appInfo.metaData.getString("CHANNEL");
		} catch (PackageManager.NameNotFoundException ignored) {

		}
		return "manbiwang";
	}

	private void initWebsocket() {
		WebSocketManager.getInstance().initWebSocket(getApplication());
	}

	private void registerLifeCallBack() {
		if (lifeCallback != null) {
			getApplication().unregisterActivityLifecycleCallbacks(lifeCallback);
		}
		lifeCallback = new ActivityLifeCallback();
		CBRepository.setLifeCallback(lifeCallback);
		getApplication().registerActivityLifecycleCallbacks(lifeCallback);
	}

	public void setTypeface() {
		Typeface typeFace = ResourcesCompat.getFont(getApplication(), R.font.roboto_regular);
		try {
			Field field3 = Typeface.class.getDeclaredField("SERIF");
			field3.setAccessible(true);
			field3.set(null, typeFace);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private void initOkGo() {
		OkHttpClient.Builder builder = new OkHttpClient.Builder();
//		builder.connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES));
		DHttpLogInterceptor loggingInterceptor = new DHttpLogInterceptor("httpLog");
		//log打印级别，决定了log显示的详细程度
		loggingInterceptor.setPrintLevel(DHttpLogInterceptor.Level.BODY);
		if (CBRepository.getCurrentEnvironment().environmentType == Constants.ONLINE_ENVIROMENT) {
			//线上环境才需要添加base_url replace 拦截器
			builder.addInterceptor(new BaseUrlReplaceIntercepter());
		}
		//log颜色级别，决定了log在控制台显示的颜色
		loggingInterceptor.setColorLevel(Level.INFO);
		if (CBRepository.getEnableDebug()) {
			builder.addInterceptor(loggingInterceptor);
		}
		//全局的读取超时时间
		builder.readTimeout(60000, TimeUnit.MILLISECONDS);
		//全局的写入超时时间
		builder.writeTimeout(60000, TimeUnit.MILLISECONDS);
		//全局的连接超时时间
		builder.connectTimeout(60000, TimeUnit.MILLISECONDS);



		builder.addInterceptor(new TokenInterceptor());

		//添加抓包拦截器
		if (CBRepository.getEnableDebug()) {
			builder.addInterceptor(new CaptureInfoInterceptor());
		}

		HttpParams httpParams = new HttpParams();
		httpParams.put("source", "android");
		OkGo.getInstance().init(getApplication())
				.addCommonParams(httpParams)
				//必须调用初始化
				.addCommonHeaders(new HttpHeaders("Connection", HttpHeaders.HEAD_VALUE_CONNECTION_KEEP_ALIVE))
				.setOkHttpClient(builder.build())
				//建议设置OkHttpClient，不设置将使用默认的
				.setCacheMode(CacheMode.NO_CACHE)
				//全局统一缓存模式，默认不使用缓存，可以不传
				.setRetryCount(0);
		//                .addCommonParams(httpParams);                          //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0

	}

//	private void initGrowingIO() {
//		//获取渠道
//		String channelId = CBRepository.getChannelValue();
//		GrowingIO.startWithConfiguration(getApplication(), new com.growingio.android.sdk.collection.Configuration()
//				.setChannel(channelId)
//				.setTestMode(BuildConfig.DEBUG)
//				.setDebugMode(BuildConfig.DEBUG)
//				.setChannel(channelId)); //打开调试Log;
//	}

	private void initBugly() {
		if (Build.BRAND.contains("WindsongAOB") || Build.MODEL.contains("WindsongAOB")) {
			//bugly过滤掉 WindsongAOB 设备的崩溃统计
			return;
		}

		channel_value = getChannel(getApplication());
		CBRepository.setChannelValue(channel_value);

		//设置渠道
		CrashReport.setAppChannel(getApplication(), channel_value);
		CrashReport.initCrashReport(getApplication(), ConfigHelper.getBuglyAppKey(), true);
	}

	private void initShare() {
		// 第三方分享
		MobSDK.init(getApplication());
	}

	private void initCrashHelper() {
		CrashHelper.getInstance().init(getApplication());
	}

	public void registPush() {
		XGPushConfig.enableFcmPush(getApplication(), true);
		XGPushConfig.enableOtherPush(getApplication(), true);
		XGPushConfig.setMiPushAppId(getApplication(), "2882303761517904122");
		XGPushConfig.setMiPushAppKey(getApplication(), "5321790481122");
		XGPushConfig.setMzPushAppId(getApplication(), "123444");
		XGPushConfig.setMzPushAppKey(getApplication(), "92b46f9340b44ce48601b272f0273959");
		XGPushConfig.enableDebug(getApplication(), false);
		XGPushConfig.setReportNotificationStatusEnable(getApplication(), true);
		XGPushConfig.setReportApplistEnable(getApplication(), true);
		XGPushConfig.setHuaweiDebug(false);

		//默认注册
		XGPushManager.registerPush(getApplication(), new XGIOperateCallback() {
			@Override
			public void onSuccess(Object data, int flag) {
				//token在设备卸载重装的时候有可能会变
				DLog.d("TPush", "注册成功，设备token为：" + data);
			}

			@Override
			public void onFail(Object data, int errCode, String msg) {
				DLog.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
			}
		});

		//韩语下推送有开关，未打开push功能解除注册
		if (LanguageHelper.isKorean(getApplication()) && !SpUtil.get(getApplication(), "IsPush", false)) {
			XGPushManager.unregisterPush(getApplication());
		}
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//LanguageHelper.setLocale(this);
	}

	protected String getCurProcessName() {
		int pid = android.os.Process.myPid();
		String processNameString = "";
		ActivityManager mActivityManager = (ActivityManager) getApplication().getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				processNameString = appProcess.processName;
			}
		}
		return processNameString;
	}
}

