package com.coinbene.manbiwang.user.login.verify;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.user.R;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;

/**
 * Created by june
 * on 2020-01-06
 */
public class VerifyDialog extends QMUIDialog {

	private WebView mWebView;
	private TextView mTvTitle;
	private TextView mTvError;
	private Gson gson;

	private Callback mCallback;
	private String mScene;
	private String mUrl;
	private boolean isError;

	public VerifyDialog(Context context, String scene) {
		super(context);

		this.mScene = scene;

		setContentView(R.layout.user_verify_dialog_layout);

		mWebView = findViewById(R.id.web_view);
		mTvTitle = findViewById(R.id.tv_title);
		mTvError = findViewById(R.id.tv_error);

		initWebview();

		mTvError.setOnClickListener(v -> {
			mWebView.reload();
			mTvError.setVisibility(View.GONE);
		});


	}

	private void initWebview() {
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);

		// 设置屏幕自适应。
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.getSettings().setLoadWithOverviewMode(true);

		mWebView.setOnLongClickListener(v -> true);
		// 建议禁止缓存加载，以确保在攻击发生时可快速获取最新的滑动验证组件进行对抗。
		mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

		mWebView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				view.loadUrl(request.getUrl().toString());
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if (isError) {
					 mWebView.setVisibility(View.GONE);
					 mTvError.setVisibility(View.VISIBLE);
				} else {
					mWebView.setVisibility(View.VISIBLE);
					mTvError.setVisibility(View.GONE);
				}
				isError = false;
			}

			@Override
			public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
				super.onReceivedError(view, request, error);
				if (request.isForMainFrame()) {
					isError = true;
				}
			}
		});

		// 设置WebView组件支持加载JavaScript。
		mWebView.getSettings().setJavaScriptEnabled(true);

		// 建立JavaScript调用Java接口的桥梁。
		mWebView.addJavascriptInterface(new WebInterface(), "webInterface");

		StringBuilder urlBuilder = new StringBuilder();
		String localeCode = LanguageHelper.getLocaleCode(CBRepository.getContext());
		urlBuilder.append(UrlUtil.replaceH5Url(Constants.VERIFY_SLIDE_URL));
		urlBuilder.append("?lang=").append(LanguageHelper.getProcessedCode(localeCode));
		urlBuilder.append("&scene=").append(mScene);
		urlBuilder.append("&background=").append(DayNightHelper.isNight(getContext()) ? "131E2F" : "FFFFFF");
		urlBuilder.append("&night=").append(DayNightHelper.isNight(getContext()) ? "true" : "false");

		// 加载业务页面。
		//mWebView.loadUrl(urlBuilder.toString());

		mUrl = urlBuilder.toString();

		mWebView.loadUrl(mUrl);
	}


	public void showWithCallback(Callback callback) {
		this.mCallback = callback;

		Log.e("WebInterface","url => " + mWebView.getUrl());

		mWebView.reload();

		getWindow().setWindowAnimations(0);

		show();
	}

	public interface Callback {
		void onSlideData(VerifyResult result);
	}

	public class WebInterface {
		@JavascriptInterface
		public void getSlideData(String callData) {
			VerifyResult result = null;
			try {
				if (gson == null) {
					gson = new Gson();
				}
				result = gson.fromJson(callData, VerifyResult.class);
				if (result != null) {
					result.setScene(mScene);
					//rmex appkey : FFFF0N0N000000008942
					result.setAppkey("FFFF0N1N00000000509E");
				}
			} catch (JsonSyntaxException e) {
				e.printStackTrace();
			} finally {
				VerifyResult finalResult = result;
				getWindow().getDecorView().postDelayed(() -> {
					if ( mCallback != null) {
						mCallback.onSlideData(finalResult);
					}
					mWebView.reload();
					dismiss();
				}, 300);
			}
		}
	}
}
