package com.coinbene.common.balance;

import android.os.Handler;
import android.os.Looper;

import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.user.UserStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by june
 * on 2019-08-15
 */
public class AssetManager {

	private static volatile AssetManager instance;

	//是否隐藏资产详情
	private boolean hideValue;

	//用户登陆状态
	private boolean isLogin;

	private List<TotalAccountListener> mTotalAccountListenerList;

	private AssetManager() {
		mTotalAccountListenerList = Collections.synchronizedList(new ArrayList<>());
		hideValue = SpUtil.getPreHideAssetEstimation();
		isLogin = ServiceRepo.getUserService().getUserStatus() == UserStatus.LOGIN;
	}

	public static AssetManager getInstance() {
		if (instance == null) {
			synchronized (AssetManager.class) {
				if (instance == null) {
					instance = new AssetManager();
				}
			}
		}
		return instance;
	}

	public void registerTotalAccountListener(TotalAccountListener listener) {
		if (!mTotalAccountListenerList.contains(listener)) {
			listener.onHideValueChanged(hideValue);
			listener.onLoginStatusChanged(isLogin);
			mTotalAccountListenerList.add(listener);
		}
	}

	public void unregisterTotalAccountListener(TotalAccountListener listener) {
		if (mTotalAccountListenerList.contains(listener)) {
			mTotalAccountListenerList.remove(listener);
		}
	}

	public boolean isHideValue() {
		return hideValue;
	}

	public void setHideValue(boolean hideValue) {
		this.hideValue = hideValue;
		//通知监听者
		synchronized (mTotalAccountListenerList) {
			for (TotalAccountListener listener : mTotalAccountListenerList) {
				listener.onHideValueChanged(hideValue);
			}
		}
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean login) {
		this.isLogin = login;
		if (AppUtil.isMainThread()) {
			notifyLogin();
		} else {
			new Handler(Looper.getMainLooper()).post(() -> notifyLogin());
		}
	}

	private void notifyLogin() {
		//在主线程通知监听者
		synchronized (mTotalAccountListenerList) {
			for (TotalAccountListener listener : mTotalAccountListenerList) {
				listener.onLoginStatusChanged(isLogin);
			}
		}
	}

	/**
	 * 分发下拉刷新事件
	 * @param product
	 */
	public void dispatchPullRefresh(Product product) {
		synchronized (mTotalAccountListenerList) {
			for (TotalAccountListener listener : mTotalAccountListenerList) {
				listener.onPullRefresh(product);
			}
		}
	}

	public void dispatchTabSelected(int index) {
		synchronized (mTotalAccountListenerList) {
			for (TotalAccountListener listener : mTotalAccountListenerList) {
				listener.onTabSelected(index);
			}
		}
	}

	public void dispatchConiSwitchChanged() {
		synchronized (mTotalAccountListenerList) {
			for (TotalAccountListener listener : mTotalAccountListenerList) {
				listener.onConiSwitchChanged();
			}
		}
	}

	public interface TotalAccountListener {
		void onHideValueChanged(boolean hide);

		void onLoginStatusChanged(boolean isLogin);

		void onPullRefresh(Product product);

		void onTabSelected(int index);

		void onConiSwitchChanged();
	}
}
