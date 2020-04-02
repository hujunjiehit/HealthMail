package com.coinbene.manbiwang.webview.route;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.coinbene.common.PostPointHandler;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.webview.R;
import com.coinbene.manbiwang.webview.WebviewActivity;
import com.coinbene.manbiwang.webview.WebviewDelegate;

import java.util.Locale;

public class WebviewUIRouter implements UIRouter {

	private Context mContext;

	public WebviewUIRouter() {

	}

	@Override
	public boolean openUri(Context context, String url, Bundle bundle) {
		openUri(context, url, bundle, 0);
		return true;
	}

	@Override
	public boolean openUri(Context context, String url, Bundle bundle, int requestCode) {
		if (mContext == null) {
			mContext = CBRepository.getContext();
		}
		if (url != null) {
			if (url.toLowerCase(Locale.US).startsWith("http") || url.toLowerCase(Locale.US).startsWith("file")) {
				if (bundle == null) {
					bundle = new Bundle();
					bundle.putString(WebviewActivity.EXTRA_URL, url);
				} else if (TextUtils.isEmpty(bundle.getString(WebviewActivity.EXTRA_URL))) {
					bundle.putString(WebviewActivity.EXTRA_URL, url);
				}

				if (bundle.getBoolean("isNotice") || url.contains(".zendesk.com")) {
					//展示公告
					if (TextUtils.isEmpty(bundle.getString(WebviewActivity.EXTRA_TITLE))) {
						//zendesk相关的链接，没有指定title，则指定为全部公告
						bundle.putString(WebviewActivity.EXTRA_TITLE, context.getResources().getString(R.string.notice));
					}
					bundle.putString(WebviewActivity.EXTRA_RIGHT_TEXT, context.getResources().getString(R.string.all_notice));
					if (TextUtils.isEmpty(bundle.getString(WebviewActivity.EXTRA_RIGHT_URL))) {
						//zendesk相关的链接，没有指定右侧跳转链接，直接跳转到公告中心
						bundle.putString(WebviewActivity.EXTRA_RIGHT_URL, UrlUtil.getZendeskNoticeCenterUrl());
					}
				} else if (bundle.getBoolean("isInviteRebate")) {
					PostPointHandler.postClickData(PostPointHandler.invitation);
					//邀请返金
					bundle.putString(WebviewActivity.EXTRA_TITLE, bundle.getString("title"));
					bundle.putInt(WebviewActivity.EXTRA_RIGHT_IMAGE, R.drawable.icon_share_grey);
					bundle.putString(WebviewActivity.EXTRA_RIGHT_URL, "coinbene://share?type=" + WebviewDelegate.SHARE_TYPE_INVITE);
				}
				WebviewActivity.startMe(context, bundle);

			}
			return true;
		}
		return true;
	}

	@Override
	public boolean openUri(Context context, Uri uri, Bundle bundle) {
		return openUri(context, uri, bundle, 0);
	}

	@Override
	public boolean openUri(Context context, Uri uri, Bundle bundle, int requestCode) {
		return openUri(context, uri.toString(), bundle, requestCode);
	}

	@Override
	public boolean verifyUri(Uri uri) {
		if (uri != null && uri.getScheme() != null) {
			return uri.getScheme().toLowerCase(Locale.US).startsWith("http") ||
					uri.getScheme().toLowerCase(Locale.US).startsWith("file");
		}
		return false;
	}
}
