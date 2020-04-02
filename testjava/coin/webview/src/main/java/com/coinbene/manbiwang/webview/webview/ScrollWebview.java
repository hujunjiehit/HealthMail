package com.coinbene.manbiwang.webview.webview;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import com.coinbene.common.widget.LollipopFixedWebView;

/**
 * Created by june
 * on 2019-08-10
 */
public class ScrollWebview extends LollipopFixedWebView {

	public OnScrollChangeListener listener;

	public ScrollWebview(Context context) {
		super(context);
	}

	public ScrollWebview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScrollWebview(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ScrollWebview(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null) {
			listener.onScrollChanged(l, t, oldl, oldt);
		}
	}

	public void setOnScrollChangeListener(OnScrollChangeListener listener) {
		this.listener = listener;
	}

	public interface OnScrollChangeListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}
}
