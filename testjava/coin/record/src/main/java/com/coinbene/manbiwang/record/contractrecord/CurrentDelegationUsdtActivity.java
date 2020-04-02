package com.coinbene.manbiwang.record.contractrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.fragment.CurrentOrderUsdtFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author ding
 * 合约当前委托
 */
public class CurrentDelegationUsdtActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout back;
	@BindView(R2.id.menu_title_tv)
	TextView title;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, CurrentDelegationUsdtActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, CurrentOrderUsdtFragment.newInstance())
					.commit();
		}
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_current_delegation;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {
		listener();
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return true;
	}

	public void init() {
		title.setText(R.string.contract_current_delegation);
	}

	public void listener() {
		//返回
		back.setOnClickListener(v -> finish());
	}
}
