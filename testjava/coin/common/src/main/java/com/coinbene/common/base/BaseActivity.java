package com.coinbene.common.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.coinbene.common.context.CBRepository;
import com.coinbene.common.utils.DayNightHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import me.imid.swipebacklayout.lib.app.SwipeBackActivity;

/**
 * @author mxd on 2017/6/16.
 */

public class BaseActivity extends SwipeBackActivity implements View.OnClickListener {

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		translucentStatusBar();

		LanguageHelper.setBaseLocal(this);

		//初始化状态栏字体颜色
		setDefaultStatusBar();

	}

	public void translucentStatusBar() {
		QMUIStatusBarHelper.translucent(this);
	}


	@Override
	protected void onResume() {
		super.onResume();
		if (!(this instanceof CoinbeneBaseActivity)) {
			setFitsSystemWindows(true);
		}
		DayNightHelper.updateConfig(this, CBRepository.uiMode);
	}

	@Override
	public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
//		super.onSaveInstanceState(outState, outPersistentState);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(LanguageHelper.setLocale(newBase));

		//修复切环境登陆之后，toast语言变成本地语言的bug
		LanguageHelper.updateAppConfig(CBRepository.getContext(), LanguageHelper.getLocaleCode(newBase));
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	private void setDefaultStatusBar() {
		//初始化状态栏字体颜色
		if (DayNightHelper.isNight(this)) {
			setMerBarWhite();  //白色状态栏字体
		} else {
			setMerBarBlack();	//黑色状态栏字体
		}
	}

	public void setMerBarBlack() {
		QMUIStatusBarHelper.setStatusBarLightMode(this);  //黑色状态栏字体
	}

	public void setMerBarWhite() {
		QMUIStatusBarHelper.setStatusBarDarkMode(this);  //白色状态栏字体
	}

	protected void setFitsSystemWindows(boolean b) {
		ViewGroup root = getWindow().getDecorView().findViewById(android.R.id.content);
		if (root != null && root.getChildAt(0) != null) {
			root.getChildAt(0).setFitsSystemWindows(b);
		}
	}
}
