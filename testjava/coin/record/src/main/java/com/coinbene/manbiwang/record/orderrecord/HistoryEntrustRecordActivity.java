package com.coinbene.manbiwang.record.orderrecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.record.R;
import com.coinbene.manbiwang.record.R2;
import com.coinbene.manbiwang.record.orderrecord.fragment.HistoryEntrustFragment;
import com.coinbene.manbiwang.record.widget.HisFilterPopWindow;
import com.coinbene.manbiwang.record.widget.HistoryFilterListener;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/12/4.
 * 历史委托页面
 */

public class HistoryEntrustRecordActivity extends CoinbeneBaseActivity {
	private Unbinder mUnbinder;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_back)
	View backView;

	@BindView(R2.id.search_img)
	ImageView checkBoxImg;

	private Fragment currentFrag;
	private HisFilterPopWindow filterPopWindow;
	private String accountType;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, HistoryEntrustRecordActivity.class);
		context.startActivity(intent);
	}

	public static void startMe(Context context, String accountType) {
		Intent intent = new Intent(context, HistoryEntrustRecordActivity.class);
		intent.putExtra("accountType", accountType);
		context.startActivity(intent);
	}


	@Override
	public int initLayout() {
		return R.layout.record_trade_history;
	}

	@Override
	public void initView() {
		titleView.setText(getText(R.string.history_entrust_label));
		backView.setOnClickListener(this);
		checkBoxImg.setVisibility(View.VISIBLE);
		checkBoxImg.setBackground(this.getResources().getDrawable(R.drawable.res_icon_filter));
		checkBoxImg.setOnClickListener(this);
		showHistoryFragment();
	}

	@Override
	public void setListener() {

	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return true;
	}

	private void showHistoryFragment() {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Bundle bundle = new Bundle() ;//传递数据
		accountType = getIntent().getStringExtra("accountType");
		bundle.putString("accountType",accountType);
		currentFrag = Fragment.instantiate(this, HistoryEntrustFragment.class.getName(), bundle);
		ft.add(R.id.context_layout, currentFrag, HistoryEntrustFragment.class.getName());
		ft.commitAllowingStateLoss();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.search_img) {
			boolean isSelect = checkBoxImg.isSelected();
			isSelect = !isSelect;
			checkBoxImg.setSelected(isSelect);
			if (isSelect) {
				if (filterPopWindow == null) {
					filterPopWindow = new HisFilterPopWindow(v, accountType);
					filterPopWindow.setOnFilterListener(filterListener);
				}
				filterPopWindow.showBelowAnchor();
			} else {
				if (filterPopWindow != null && filterPopWindow.isShow()) {
					filterPopWindow.onDismiss();
				}
			}
		}
	}

	private HistoryFilterListener filterListener = new HistoryFilterListener() {
		@Override
		public void onPopWindowDimiss() {
			checkBoxImg.setSelected(false);
		}

		@Override
		public void invalidTradepair() {
			if (currentFrag != null && currentFrag instanceof HistoryEntrustFragment) {
				((HistoryEntrustFragment) currentFrag).invalidTradepair();
			}

		}

		@Override
		public void doHttpFilter(String inputBaseAsset, String quoteAsset, String beginTime, String endTime, int typeDirection, boolean ignoreCancelled) {
			if (currentFrag != null && currentFrag instanceof HistoryEntrustFragment) {
				((HistoryEntrustFragment) currentFrag).doFilter(inputBaseAsset, quoteAsset, beginTime, endTime, typeDirection, ignoreCancelled);
			}
		}
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (currentFrag != null) {
			getSupportFragmentManager().beginTransaction().remove(currentFrag).commitAllowingStateLoss();
		}
	}
}
