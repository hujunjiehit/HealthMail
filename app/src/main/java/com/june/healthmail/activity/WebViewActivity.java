package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.june.healthmail.R;

/**
 * Created by bjhujunjie on 2017/3/8.
 */

public class WebViewActivity extends Activity {

  private WebView webView;
  private String url;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_webview_layout);
    init();
  }

  @Override
  protected void onPause() {
    super.onPause();
    webView.pauseTimers();
  }

  @Override
  protected void onResume() {
    super.onResume();
    webView.resumeTimers();
  }

  private void init() {
    url = "https://item.taobao.com/item.htm?spm=a230r.1.14.21.2l6ruV&id=540430775263";
    webView = (WebView) findViewById(R.id.webview);
    //启用支持javascript
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);

    webView.loadUrl(url);

    //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
    webView.setWebViewClient(new WebViewClient(){
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // TODO Auto-generated method stub
        if (url.startsWith("scheme:") || url.startsWith("scheme：")) {
          Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
          startActivity(intent);
        }
        return false;
      }
    });
  }
}
