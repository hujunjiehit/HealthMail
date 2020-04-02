package com.coinbene.common;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.service.app.TabType;

import java.lang.ref.WeakReference;

/**
 * Created by june
 * on 2019-09-24
 */
public class MainActivityDelegate {

	private static volatile MainActivityDelegate instance;

	private WeakReference<IMainActivity> mIMainActivity;

	private String tab;
	private String subTab;
	private String symbol;
	private String type;  // "1", 买入； "2", 卖出

	private MainActivityDelegate() {

	}

	public static MainActivityDelegate getInstance() {
		if (instance == null) {
			synchronized (MainActivityDelegate.class) {
				if (instance == null) {
					instance = new MainActivityDelegate();
				}
			}
		}
		return instance;
	}


	public void changeTab(Bundle bundle) {
		if (bundle == null) {
			return;
		}

		tab = bundle.getString("tab", "");
		subTab = bundle.getString("subTab", "");
		symbol = bundle.getString("symbol", "");
		type = bundle.getString("type", "");

		if (mIMainActivity != null && mIMainActivity.get() != null) {
			mIMainActivity.get().changeTab(UrlUtil.getTabType(tab));
		}
	}

	public void setMainActivity(IMainActivity activity) {
		if (mIMainActivity == null || mIMainActivity.get() == null || mIMainActivity.get() != activity) {
			mIMainActivity = new WeakReference<>(activity);
		}
	}


	public void onDestroy() {
		mIMainActivity = null;
	}


	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getSubTab() {
		return subTab;
	}

	public void clearSubTab() {
		subTab = "";
	}

	public String getSymbol() {
		return symbol;
	}

	public void clearSymbol() {
		new Handler(Looper.getMainLooper()).postDelayed(() -> symbol = "", 200);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void clearType() {
		new Handler(Looper.getMainLooper()).postDelayed(() -> type = "", 200);
	}

	public interface IMainActivity {
		void changeTab(TabType tabType);
	}
}
