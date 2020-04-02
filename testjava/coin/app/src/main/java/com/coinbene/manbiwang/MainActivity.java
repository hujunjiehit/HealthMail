package com.coinbene.manbiwang;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyEvent;

import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.MainActivityDelegate;
import com.coinbene.common.aspect.AspectManager;
import com.coinbene.common.aspect.annotation.AddFlowControl;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.DLog;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.LockUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.SwitchUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.contract.R2;
import com.coinbene.manbiwang.push.PushDialog;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.app.TabType;
import com.coinbene.manbiwang.service.user.UserStatus;
import com.coinbene.manbiwang.spot.spot.popbar.NoScrollViewPager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends CoinbeneBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback, MainActivityDelegate.IMainActivity {

	public static final String TAG = "MainActivityTag";

	@BindView(R.id.navigation_view)
	BottomNavigationView mNavigationView;
	@BindView(R.id.main_view_pager)
	NoScrollViewPager mViewPager;

	private TabFragmentPageAdapter mPageAdapter;
	private List<TabType> tabList;

	private SparseArray<TabType> tabMap;
	//必要权限
	String[] permissions = new String[]{Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_PHONE_STATE};

	private long lastReselectTime = System.currentTimeMillis();
	public static final String FIRST_LOAD_APP = "firstLoadApp";
	private long firstTime = 0;
	private boolean isFirst;
	private int count;
	@BindView(R2.id.drawer_layout)
	DrawerLayout drawerLayout;

	@Override
	public int initLayout() {
		return R.layout.activity_main;
	}

	@Override
	public void initView() {
		SpUtil.setHasNewVersion(false);
		CBRepository.setMainActivityStarted(true);
		getSwipeBackLayout().setEnableGesture(false);
		MainActivityDelegate.getInstance().setMainActivity(this);

		Configuration configuration = getResources().getConfiguration();
		CBRepository.uiMode = configuration.uiMode;

		isFirst = SpUtil.get(this, FIRST_LOAD_APP, true);
		// 第一次进入App并且语言为韩语提示
		if (LanguageHelper.isKorean(this) && isFirst) {
			SpUtil.put(this, FIRST_LOAD_APP, false);
			PushDialog pushDialog = new PushDialog(this);
			pushDialog.setCancelable(false);
			pushDialog.show();
		}

		ServiceRepo.getAppService().updateUserInfo();

		initTabFragment();

		mNavigationView.setItemIconTintList(null);
		mNavigationView.setItemTextAppearanceActive(R.style.CoinBene_Navigation_Selected);
		mNavigationView.setItemTextAppearanceInactive(R.style.CoinBene_Navigation_Normal);

		ServiceRepo.getAppService().setDrawerLayout(drawerLayout);

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
			//android 10 启动前台service，避免应用切到后台之后，ws连接中断
			//startForegroundService(new Intent(this, ForegroundService.class));
		}
	}

	private void initTabFragment() {
		if (tabList == null) {
			tabList = new ArrayList<>();
		}
		if (mPageAdapter == null) {
			tabList.add(TabType.HOME);
			tabList.add(TabType.MARKET);
			tabList.add(TabType.SPOT);
			if (SwitchUtils.isOpenContract()) {
				tabList.add(TabType.CONTRACT);
			} else {
				mNavigationView.getMenu().findItem(R.id.nav_contract).setVisible(false);
			}
			tabList.add(TabType.BALANCE);
			mPageAdapter = new TabFragmentPageAdapter(getSupportFragmentManager(), tabList);
		}
		mViewPager.setAdapter(mPageAdapter);

		getWindow().getDecorView().postDelayed(() -> {
			if (mViewPager != null) {
				mViewPager.setOffscreenPageLimit(tabList.size());
			}
		}, 1000);
	}

	@Override
	public void setListener() {
		mNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
			if (tabMap == null) {
				tabMap = new SparseArray<>(5);
				tabMap.put(R.id.nav_home, TabType.HOME);
				tabMap.put(R.id.nav_market, TabType.MARKET);
				tabMap.put(R.id.nav_spot, TabType.SPOT);
				tabMap.put(R.id.nav_balance, TabType.BALANCE);
				tabMap.put(R.id.nav_contract, TabType.CONTRACT);
			}

			int tabPosition = mPageAdapter.getTabPosition(tabMap.get(menuItem.getItemId()));

			//不是切到行情，并且是锁住状态需要跳解锁页面, 解锁成功继续切tab
			if (mUserStatus != null && mUserStatus == UserStatus.LOCKED) {
				if (menuItem.getItemId() != R.id.nav_home && menuItem.getItemId() != R.id.nav_market) {
					LockUtils.showLockPage(MainActivity.this, UrlUtil.getChangeTabUrl(tabMap.get(menuItem.getItemId())));
					//延迟500ms切换
					getWindow().getDecorView().postDelayed(() -> mViewPager.setCurrentItem(tabPosition), 500);
					return true;
				}
			}

			//直接切换
			mViewPager.setCurrentItem(tabPosition);

			return true;
		});
		mNavigationView.setSelectedItemId(R.id.nav_home);

		mNavigationView.setOnNavigationItemReselectedListener(menuItem -> {
			if (System.currentTimeMillis() - lastReselectTime > 1000) {
				lastReselectTime = System.currentTimeMillis();
				count = 0;
			} else {
				count++;
			}

			if (count >= 5 && CBRepository.getEnableDebug()) {
				goToDebug();
			}
		});
	}

	@AddFlowControl
	private void goToDebug() {
		ARouter.getInstance().build(RouteHub.App.debugActivity).navigation(MainActivity.this);
	}

	@Override
	public void initData() {
		if (!SpUtil.getAppIsRecreate()) {
			//非recreate时调用
			requestPermission();

			checkVersion();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		setFitsSystemWindows(false);

		if (getIntent() != null && getIntent().getData() != null) {
			Uri uri = getIntent().getData();
			getIntent().setData(null);
			UIBusService.getInstance().openUri(this, uri.toString(), null);
		}

		if (ServiceRepo.getUserService().getUserStatus() == UserStatus.LOCKED) {
			if (mNavigationView.getSelectedItemId() != R.id.nav_market && mNavigationView.getSelectedItemId() != R.id.nav_home) {
				LockUtils.showLockPage(this);
			}
			return;
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean needLock() {
		return false;
	}

	@Override
	public void changeTab(TabType tabType) {
		for (int i = 0; i < tabMap.size(); i++) {
			if (tabType == tabMap.get(tabMap.keyAt(i)) && mNavigationView != null) {
				mNavigationView.setSelectedItemId(tabMap.keyAt(i));
				break;
			}
		}
	}


	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		DLog.e(TAG, "onNewIntent：" + intent.toString() + " data:" + intent.getData());
		if (intent == null) {
			return;
		}
		if (intent.getData() != null) {
			Uri uri = intent.getData();
			intent.setData(null);

			if (TextUtils.isEmpty(uri.getHost())) {
				return;
			}

			// 进入ZENDESK公告
			if (UIRouter.HOST_NOTICE.equals(uri.getHost().toLowerCase())) {
				String url = getUrl(uri.toString());
				Bundle bundle = new Bundle();
				bundle.putBoolean("isNotice", true);
				UIBusService.getInstance().openUri(this, url, bundle);
				return;
			}

			//跳转某个HTML5页面
			if (UIRouter.HOST_HTML5.equals(uri.getHost().toUpperCase())) {
				String url = getUrl(uri.toString());
				UIBusService.getInstance().openUri(this, url, null);
				return;
			}

			DLog.e("DYPush", "onNewIntent" + uri.toString());
			UIBusService.getInstance().openUri(this, uri.toString(), null);
		} else if (intent.getExtras() != null && !TextUtils.isEmpty(intent.getExtras().getString("tab", ""))) {
			MainActivityDelegate.getInstance().setMainActivity(this);
			MainActivityDelegate.getInstance().changeTab(intent.getExtras());
		}

		if (!isFinishing()) {
			setIntent(intent);
		}
	}

	/**
	 * 提取链接coinbene://H5?url=https://s-m-s.coinbene.mobi/loading.html?redirect_url=%2Fh5.html&min_version=2.4.0&auth=true&replace=true
	 * url=后面的内容
	 */
	private String getUrl(String url) {
		return url.substring(url.indexOf("url=") + 4);
	}

	private void requestPermission() {
		AndPermission.with(this).runtime().permission(permissions)
				.onGranted(data -> {
					if (data.contains(Permission.WRITE_EXTERNAL_STORAGE)) {

					}
				}).start();
	}

	@SuppressLint("MissingSuperCall")
	@Override
	public void onSaveInstanceState(Bundle outState) {//重写onSaveInstanceState，防止activity被回收后fragment的问题
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			if (System.currentTimeMillis() - firstTime > 2000) {
				ToastUtil.show(R.string.back_to_exit);
				firstTime = System.currentTimeMillis();
			} else {
				SpUtil.put(this, SpUtil.PRE_QUIT_TIME, System.currentTimeMillis(), true);
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		CBRepository.setMainActivityStarted(false);

		if (AspectManager.getInstance().getFlowControlMap() != null) {
			for (String key : AspectManager.getInstance().getFlowControlMap().keySet()) {
				AspectManager.getInstance().getFlowControlMap().get(key).destroy();
			}
			AspectManager.getInstance().getFlowControlMap().clear();
		}
	}

	private void checkVersion() {
		ServiceRepo.getAppService().checkUpdate(this, result -> {
			if (!result) {
				//没有弹出升级弹窗, 则请求App首页弹窗
				ServiceRepo.getAppService().getPopupBanner(MainActivity.this);
			}
		});
	}
}
