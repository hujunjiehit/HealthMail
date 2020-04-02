package com.coinbene.manbiwang.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.service.RouteHub;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2019/2/13.
 */
@Route(path = RouteHub.User.mySelfActivity)
public class MySelfActivity extends CoinbeneBaseActivity {
	@BindView(R2.id.fragment_container)
	FrameLayout fragment_container;

	@BindView(R2.id.menu_back)
	View backView;

	public static void startMe(Context context, Bundle bundle) {
		Intent intent = new Intent(context, MySelfActivity.class);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		backView.setOnClickListener(this);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, MySelfFragment.newInstance(), "mySelfFrag")
					.commitAllowingStateLoss();
		}
	}

	@Override
	public int initLayout() {
		return R.layout.fr_myself_contain;
	}

	@Override
	public void initView() {

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

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.menu_back) {
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
