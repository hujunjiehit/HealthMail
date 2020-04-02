package com.coinbene.manbiwang.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.RequiresApi;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.datacollection.SchemeFrom;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LockUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.coinbene.manbiwang.webview.bean.DeviceAction;
import com.coinbene.manbiwang.webview.bean.JsAction;
import com.coinbene.manbiwang.webview.bean.NativeAction;
import com.coinbene.manbiwang.webview.bean.NavigatorAction;
import com.coinbene.manbiwang.webview.bean.UserAction;
import com.coinbene.manbiwang.webview.jsbridge.BridgeHandler;
import com.coinbene.manbiwang.webview.jsbridge.BridgeWebView;
import com.coinbene.manbiwang.webview.jsbridge.BridgeWebViewClient;
import com.coinbene.manbiwang.webview.jsbridge.CallBackFunction;
import com.coinbene.manbiwang.webview.jsbridge.DefaultHandler;
import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import java.util.Locale;

public class WebviewActivity extends WebBaseActivity implements DownloadListener {

	private static final String TAG = "DebugWebview";

	public static final String EXTRA_URL = "webview_load_url";
	public static final String EXTRA_TITLE = "title";       //actionbar 标题
	public static final String EXTRA_RIGHT_TEXT = "right_text";     //actionbar 右侧文本
	public static final String EXTRA_RIGHT_IMAGE = "right_image";   //actionbar 右侧图片
	public static final String EXTRA_RIGHT_URL = "right_url";       //actionbar 右侧跳转链接

	private CallBackFunction mCallBackFunction;

	//图片
	private final static int FILE_CHOOSER_RESULT_CODE = 128;

	private String mPreUrl;
	private String mLoadUrl;

	private String mTitle;
	private String mRightText;
	private @DrawableRes
	int mRightImage;

	private String mRightUrl;

	private BridgeWebView mWebView;

	private Gson gson = new Gson();

	private ValueCallback<Uri[]> uploadMessageAboveL;

	//加载进度条
	private ProgressBar mProgressBar;
	private FrameLayout mWebviewContainer;
	private TextView mTvError;
	private SwipeRefreshLayout mSwipeRefresh;

	private boolean enableRefresh = true;

	private boolean isError = false;
	private boolean isLandScape = false;

	//是否是邀请返金
	private boolean isInviteRebate = false;
	private boolean hasUrlReplaced = false;

	@AddFlowControl
	public static void startMe(Context context, Bundle bundle) {
		Intent intent = new Intent(context, WebviewActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.webview_web_activity;
	}

	@Override
	public void initView() {
		super.initView();
		mProgressBar = (ProgressBar) findViewById(R.id.webview_progressbar);
		mWebviewContainer = (FrameLayout) findViewById(R.id.webview_container);
		mTvError = (TextView) findViewById(R.id.webview_tv_error);
		if (mProgressBar != null) {
			mProgressBar.setMax(100);
			mProgressBar.setProgress(0);
		}
		mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
	}

	@Override
	public void setListener() {
		super.setListener();
		mTvError.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mWebView.reload();
				mTvError.setVisibility(View.GONE);
			}
		});

		mSwipeRefresh.setOnRefreshListener(() -> {
			JsAction action = new JsAction();
			action.setAction("pageRefresh");
			mWebView.callHandler("appCallBack", new Gson().toJson(action), new CallBackFunction() {
				@Override
				public void onCallBack(String data) {

				}
			});
			mSwipeRefresh.setRefreshing(false);
		});
		mSwipeRefresh.setColorSchemeColors(getResources().getColor(R.color.res_blue));
	}


	@Override
	public void initData() {
		init();

		loadUrl(getIntent());

		WebviewDelegate.getInstance().setWebviewActivity(this);
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		loadUrl(intent);
	}



	private void init() {
		//是否横屏
		if (getIntent() != null && getIntent().getBooleanExtra("isLandScape", false)) {
			isLandScape = true;
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			mProgressBar.setVisibility(View.GONE);
			hideTitleBar();
			setSwipeBackEnable(false);
			mSwipeRefresh.setEnabled(false);

			hideStatusBar();
		}

		initWebview();
		setJsHandler();
	}


	private void loadUrl(Intent intent) {
		if (intent != null) {
			parseIntent(intent);
			if ((mPreUrl != null && !mPreUrl.equals(mLoadUrl)) || hasUrlReplaced) {
				//需要重新实例化webview
				DLog.e("h5Url测试","hasUrlReplaced:" + hasUrlReplaced + "   re init Webview");

				hasUrlReplaced = false;
				init();
			}
			mLoadUrl = UrlUtil.appendSiteAndLanguage(mLoadUrl);
			mWebView.loadUrl(mLoadUrl);
		}
	}

	@SuppressLint("ResourceType")
	private void parseIntent(Intent intent) {
		mPreUrl = mLoadUrl;

		mLoadUrl = intent.getStringExtra(EXTRA_URL);

		DLog.e("h5Url测试","mLoadUrl before ====>" + mLoadUrl);

		//执行H5 URL替换
		String replaceUrl = UrlUtil.replaceH5Url(mLoadUrl);

		DLog.e("h5Url测试","mLoadUrl after ====>" + replaceUrl);

		if (!replaceUrl.equals(mLoadUrl)) {
			hasUrlReplaced = true;
			mLoadUrl = replaceUrl;
		}


		mTitle = intent.getStringExtra(EXTRA_TITLE);
		mRightText = intent.getStringExtra(EXTRA_RIGHT_TEXT);
		mRightImage = intent.getIntExtra(EXTRA_RIGHT_IMAGE, -1);
		mRightUrl = intent.getStringExtra(EXTRA_RIGHT_URL);

		isInviteRebate = intent.getBooleanExtra("isInviteRebate", false);

		if (!TextUtils.isEmpty(mTitle)) {
			setWebTitle(mTitle);
		}

		//右边的图标，没有图标才显示右边的文字
		if (mRightImage > 0) {
			if (isInviteRebate) {
				if (CommonUtil.isLoginAndUnLocked()) {
					setRightImage(mRightImage);
				}
			} else {
				setRightImage(mRightImage);
			}
		} else {
			if (!TextUtils.isEmpty(mRightText)) {
				setRightText(mRightText);
			}
		}

		if (!TextUtils.isEmpty(mRightUrl)) {
			setActionUrl(mRightUrl);
		}
	}

	public void setCallBackFunction(CallBackFunction mCallBackFunction) {
		this.mCallBackFunction = mCallBackFunction;
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWebView != null) {
			mWebView.onResume();
			mWebView.getSettings().setJavaScriptEnabled(true);
			mWebView.getSettings().setLightTouchEnabled(true);

			if (isInviteRebate) {
				/**
				 * 邀请返金判断登陆之后需要重新加载新的url
				 */
				if (mUserStatus == UserStatus.LOCKED) {
					LockUtils.showLockPage(this);
					return;
				}
				String newUrl = UrlUtil.getInviteUrl();
				newUrl = UrlUtil.replaceH5Url(newUrl);
				if (!newUrl.equals(mLoadUrl)) {
					if (CommonUtil.isLoginAndUnLocked()) {
						setRightImage(mRightImage);
					}
					init();
					mWebView.loadUrl(newUrl);
				}
			}
		}

		if (mCallBackFunction != null) {
			//jsbridge跳到登陆页面设置的回调，登陆成功需要返回user信息给web页面
			mCallBackFunction.onCallBack(SpUtil.getUserResponse());
			mCallBackFunction = null;
		}

		JsAction action = new JsAction();
		action.setAction("pageShow");
		mWebView.callHandler("appCallBack", new Gson().toJson(action), data -> {

		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mWebView != null) {
			mWebView.onPause();
			mWebView.getSettings().setJavaScriptEnabled(false);
			mWebView.getSettings().setLightTouchEnabled(false);
		}

		JsAction action = new JsAction();
		action.setAction("pageHide");
		mWebView.callHandler("appCallBack", new Gson().toJson(action), data -> {

		});
	}


	public void goBack() {
		if (mWebView != null && mWebView.canGoBack()) {
			mWebView.goBack();
		}
	}

	public Gson getGson() {
		if (gson == null) {
			gson = new Gson();
		}
		return gson;
	}

	private void initWebview() {
		if (mWebviewContainer.getChildCount() > 0) {
			mWebviewContainer.removeAllViews();
			mWebView.removeAllViews();
			mWebView.destroy();
		}
		mWebView = new BridgeWebView(this);
		mWebviewContainer.addView(mWebView, 0, new FrameLayout.LayoutParams(-1, -1));


		mWebView.setOnScrollChangeListener((l, t, oldl, oldt) -> {

			//mWebView.loadUrl("javascript:window.jo.run(document.documentElement.scrollHeight+'');");


			//横屏状态没有刷新
			if (isLandScape) {
				return;
			}

			if (!enableRefresh) {
				return;
			}

			//监听webview滑动开启刷新功能
			if (t > 15) {
				mSwipeRefresh.setEnabled(false);
			} else {
				mSwipeRefresh.setEnabled(true);
			}
		});

		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setDisplayZoomControls(false);

		settings.setDatabaseEnabled(true);
		settings.setSaveFormData(true);
		settings.setDomStorageEnabled(true);
		settings.setGeolocationEnabled(true);
		settings.setAppCacheEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setUseWideViewPort(true); // 将图片调整到适合WebView的大小
		settings.setLoadWithOverviewMode(true); // 自适应屏幕
		settings.setAllowFileAccess(true); // 允许访问文件
		settings.setPluginState(WebSettings.PluginState.ON);
		settings.setUserAgentString(settings.getUserAgentString() + "/CoinBene");
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			settings.setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		mWebView.setDownloadListener(this);

		mWebView.clearCache(false);

		mWebView.setWebViewClient(new MyWebViewClient(mWebView));

		mWebView.setWebChromeClient(new WebChromeClient() {

			@Override
			public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
				return super.onJsAlert(webView, s, s1, jsResult);
			}

			@Override
			public void onReceivedTitle(WebView webView, String s) {
				super.onReceivedTitle(webView, s);
			}

			@Override
			public void onProgressChanged(WebView webView, int newProgress) {
				if (newProgress < 100) {
					if (mProgressBar.getVisibility() == View.GONE) {
						mProgressBar.setVisibility(View.VISIBLE);
					}
					mProgressBar.setProgress(newProgress);
				} else if (newProgress >= 100) {
					mProgressBar.setProgress(newProgress);
					mProgressBar.setVisibility(View.GONE);
				}
				super.onProgressChanged(webView, newProgress);
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback customViewCallback) {
				super.onShowCustomView(view, customViewCallback);
			}

			@Override
			public void onHideCustomView() {
				super.onHideCustomView();
			}

			@Override
			public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
				uploadMessageAboveL = valueCallback;
				openImageChooserActivity();
				return true;
			}
		});
	}

	/**
	 * 注入jsbridge
	 */
	private void setJsHandler() {
		if (getIntent() == null || TextUtils.isEmpty(getIntent().getStringExtra(WebviewActivity.EXTRA_URL))) {
			return;
		}
		String host = Uri.parse(getIntent().getStringExtra(WebviewActivity.EXTRA_URL)).getHost();
		if (!host.endsWith("atomchain.vip") && !host.endsWith("coinbene.mobi") && !host.endsWith("coinbene.com") && !host.endsWith("coinbene.vip")
				&& !CBRepository.isInDebugWhiteList()) {
			//如果host不在白名单，并且不是调试白名单用户，直接返回，不再注入jsbridge
			return;
		}

		mWebView.setDefaultHandler(new DefaultHandler());

		//js调用java方法
		mWebView.registerHandler("navigator", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				NavigatorAction action = gson.fromJson(data, NavigatorAction.class);
				//function.onCallBack("执行结果，response data from java");

				WebviewDelegate.getInstance().dispatchAction(action, function);
			}
		});

		//
		mWebView.registerHandler("native", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				NativeAction action = gson.fromJson(data, NativeAction.class);

				WebviewDelegate.getInstance().dispatchAction(action, function);

				function.onCallBack("");
			}
		});

		mWebView.registerHandler("user", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				UserAction action = gson.fromJson(data, UserAction.class);

				WebviewDelegate.getInstance().dispatchAction(action, function);
			}
		});

		mWebView.registerHandler("device", new BridgeHandler() {
			@Override
			public void handler(String data, CallBackFunction function) {
				DeviceAction action = gson.fromJson(data, DeviceAction.class);

				WebviewDelegate.getInstance().dispatchAction(action, function);
			}
		});


		//java 调用js方法
//        UserInfoTable user = new UserInfoTable();
//        user.areaCode = "1000";
//        user.email = "hujunjie@gmail.com";
//        user.auth = true;
//        JsAction action = new JsAction();
//        action.setAction("pageShow");
//        action.setAction("pageHide");
//        mWebView.callHandler("appCallBack", new Gson().toJson(action), new CallBackFunction() {
//            @Override
//            public void onCallBack(String data) {
//
//            }
//        });
	}

	@Override
	public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
		if (!TextUtils.isEmpty(url)) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWebView != null && mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			} else {
				return super.onKeyDown(keyCode, event);
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDestroy() {
		if (mWebviewContainer != null) {
			mWebviewContainer.removeAllViews();
		}
		if (mWebView != null) {
			mWebView.removeAllViews();
			mWebView.destroy();
		}
		WebviewDelegate.getInstance().onDestroy();
		super.onDestroy();
	}

	/**
	 * 调用手机的相册
	 */
	private void openImageChooserActivity() {
		Intent i = new Intent(Intent.ACTION_GET_CONTENT);
		i.addCategory(Intent.CATEGORY_OPENABLE);
		i.putExtra("return-data", true);
		i.setType("image/*");
		i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		this.startActivityForResult(Intent.createChooser(i, "选择相册"), FILE_CHOOSER_RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == FILE_CHOOSER_RESULT_CODE) {
			//处理手机的相册返回的内容
			Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
			if (uploadMessageAboveL != null) {
				onActivityResultAboveL(data);
			}
		}
	}

	/**
	 * 处理手机的相册返回的内容
	 *
	 * @param intent
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void onActivityResultAboveL(Intent intent) {
		Uri[] results = null;
		if (intent != null) {
			String dataString = intent.getDataString();
			ClipData clipData = intent.getClipData();
			if (clipData != null) {
				results = new Uri[clipData.getItemCount()];
				for (int i = 0; i < clipData.getItemCount(); i++) {
					ClipData.Item item = clipData.getItemAt(i);
					results[i] = item.getUri();
				}
			}
			if (dataString != null)
				results = new Uri[]{Uri.parse(dataString)};
		}
		uploadMessageAboveL.onReceiveValue(results);
		uploadMessageAboveL = null;
	}

	public void setRefreshEnable(boolean enable) {
		mSwipeRefresh.setEnabled(enable);
		this.enableRefresh = enable;
	}

	private class MyWebViewClient extends BridgeWebViewClient {

		public MyWebViewClient(BridgeWebView webView) {
			super(webView);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView webView, String url) {
			super.shouldOverrideUrlLoading(webView, url);
			if (url.toLowerCase(Locale.US).startsWith("http")) {
				//http or https
				return false;
			}

			if (url.toLowerCase().startsWith("coinbene://")) {
				//应用内部自定义的跳转
				Bundle bundle = new Bundle();
				bundle.putString("SchemeFrom", SchemeFrom.APP_HTML.name());
				UIBusService.getInstance().openUri(WebviewActivity.this, url, bundle);
				return true;
			}
			return false;
		}

		@Override
		public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
			super.onPageStarted(webView, s, bitmap);
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onPageFinished(WebView webView, String s) {
			super.onPageFinished(webView, s);
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.GONE);
			}
			if (isError) {
				mWebviewContainer.setVisibility(View.GONE);
				mTvError.setVisibility(View.VISIBLE);
			} else {
				mWebviewContainer.setVisibility(View.VISIBLE);
				mTvError.setVisibility(View.GONE);
			}

			isError = false;
		}

		@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
		@Override
		public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
			super.onReceivedError(webView, webResourceRequest, webResourceError);
			if (mProgressBar != null) {
				mProgressBar.setVisibility(View.GONE);
			}
			if (webResourceRequest.isForMainFrame()) {
				isError = true;
			}
		}
	}

	public void setMerBar() {
		//  横屏状态下隐藏状态栏，所以不使用透明状态栏
		if (getIntent() != null && !getIntent().getBooleanExtra("isLandScape", false)) {
			QMUIStatusBarHelper.translucent(this);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && isLandScape) {
			hideStatusBar();
		}
	}

	/**
	 * 隐藏状态栏
	 */
	private void hideStatusBar() {
		getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}
}
