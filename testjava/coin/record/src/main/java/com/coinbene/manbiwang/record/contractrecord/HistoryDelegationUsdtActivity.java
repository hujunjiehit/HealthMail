package com.coinbene.manbiwang.record.contractrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.contractrecord.fragment.HistoryOrderUsdtFragment;

import butterknife.BindView;

/**
 * @author ding
 * 合约历史委托
 */
public class HistoryDelegationUsdtActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout back;
	@BindView(R2.id.menu_title_tv)
	TextView title;
	public static void startMe(Context context) {
		Intent intent = new Intent(context, HistoryDelegationUsdtActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, HistoryOrderUsdtFragment.newInstance())
					.commit();
		}
	}

	@Override
	public int initLayout() {
		return R.layout.record_activity_history_delegation;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void setListener() {
		//监听设置
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
		//初始设置
		title.setText(R.string.contract_histor_delegation);
	}

	public void listener() {
		back.setOnClickListener(v -> finish());
	}
}
