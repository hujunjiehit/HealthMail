package com.coinbene.manbiwang.debug;

import android.content.Intent;
import android.util.Log;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.BuildConfig;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.manbiwang.debug.fragment.FunctionListFragment;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.R;

@Route(path = RouteHub.App.debugActivity)
public class DebugActivity extends CoinbeneBaseActivity {

	FunctionListFragment mFunctionListFragment;

	@Override
	public int initLayout() {
		return R.layout.settings_activity_debug;
	}

	@Override
	public void initView() {
		if (!CBRepository.getEnableDebug()) {
			return;
		}
		setDefaultFragment();
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}

	private void setDefaultFragment() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction tansaction = fm.beginTransaction();

		mFunctionListFragment = new FunctionListFragment();
		tansaction.replace(R.id.fragment_container, mFunctionListFragment);
		tansaction.commit();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
}
