package com.coinbene.manbiwang.balance.activity.spot;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.BalanceController;
import com.coinbene.common.database.BalanceInfoTable;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.dialog.AlertDialogBuilder;
import com.coinbene.common.dialog.DialogListener;
import com.coinbene.common.dialog.DialogManager;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.zxing.ScannerActivity;
import com.coinbene.manbiwang.balance.R;
import com.coinbene.manbiwang.balance.R2;
import com.coinbene.manbiwang.model.http.WithDrawAddressModel;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.service.record.RecordType;
import com.google.android.material.tabs.TabLayout;
import com.lzy.okgo.OkGo;
import com.mylhyl.zxing.scanner.common.Scanner;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 提币页面
 */
public class WithDrawActivity extends CoinbeneBaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_right_tv)
	TextView rightView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.view_pager)
	ViewPager mViewPager;
	@BindView(R2.id.menu_line_view)
	View view;

	@BindView(R2.id.tab_layout)
	TabLayout mTabLayout;
	private BalanceInfoTable model;


	private WithDrawFragment withDrawFragment;
	private int mCurrentPosition = 0;
	private InPlatformTransferAccountsFragment transferAccountsFragment;

	public static void startMe(Context context, String asset) {
		Intent intent = new Intent(context, WithDrawActivity.class);
		intent.putExtra("model", asset);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		return R.layout.coin_withdraw;
	}

	@Override
	public void initView() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		Intent intent = getIntent();
		String asset = intent.getStringExtra("model");
		model = BalanceController.getInstance().findByAsset(asset);
		if (model == null) {
			ToastUtil.show(R.string.toast_data_transfer_error);
			finish();
			return;
		}
		rightView.setText(getText(R.string.reflect_coin_new));
		mViewPager.setOffscreenPageLimit(2);
		withDrawFragment = WithDrawFragment.newInstance(model.asset);
		transferAccountsFragment = InPlatformTransferAccountsFragment.newInstance(model.asset);
		Fragment[] pagerFragments = {withDrawFragment, transferAccountsFragment};

		mViewPager.setAdapter(new FragmentPagerAdapter(this.getSupportFragmentManager()) {
			String[] types = {getResources().getString(R.string.withdraw), getResources().getString(R.string.transfer_accounts)};

			@Override
			public Fragment getItem(int position) {
				if (position < pagerFragments.length) {
					return pagerFragments[position];
				}
				return withDrawFragment;
			}

			@Override
			public int getCount() {
				return pagerFragments.length;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return types[position];
			}
		});

		mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				mCurrentPosition = position;
				if (mCurrentPosition == 0) {
					rightView.setText(getText(R.string.reflect_coin_new));
				} else {
					rightView.setText(getText(R.string.transfer));
					checkKycStatus();

				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		mViewPager.setCurrentItem(mCurrentPosition);

		mTabLayout.setupWithViewPager(mViewPager);
		backView.setOnClickListener(this);
		rightView.setOnClickListener(this);

		init();
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

	private void checkKycStatus() {
		if (!UserInfoController.getInstance().checkKycStatus()) {
			String site_str = SiteController.getInstance().getSiteName();
			//巴西站app没有实名认证
			if (!TextUtils.isEmpty(site_str) && site_str.equals(Constants.SITE_BR)) {
				AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(this);
				builder.setMessage(R.string.please_to_br_kyc);
				builder.setPositiveButton(R.string.btn_ok);
				builder.showDialog();

				builder.setListener(new DialogListener() {
					@Override
					public void clickNegative() {

					}

					@Override
					public void clickPositive() {
						mViewPager.setCurrentItem(0);
					}
				});
			} else {
				AlertDialogBuilder builder = DialogManager.getAlertDialogBuilder(this);
				builder.setMessage(R.string.please_to_kyc);
				builder.setPositiveButton(R.string.go_setting);
				builder.setNegativeButton(R.string.btn_cancel);
				builder.showDialog();

				builder.setListener(new DialogListener() {
					@Override
					public void clickNegative() {
						mViewPager.setCurrentItem(0);
					}

					@Override
					public void clickPositive() {
						ARouter.getInstance().build(RouteHub.User.settingSafeActivity).navigation(WithDrawActivity.this);
						mViewPager.postDelayed(() ->
								mViewPager.setCurrentItem(0), 500);
					}
				});

			}
		}
	}


	@SuppressLint("MissingSuperCall")
	@Override
	public void onSaveInstanceState(Bundle outState) {//重写onSaveInstanceState，防止activity被回收后fragment的问题
	}

	private void init() {
		String titleStr = this.getText(R.string.withdraw_title_new).toString();
		titleStr = String.format(titleStr, model.asset);
		titleView.setText(titleStr);
		view.setVisibility(View.GONE);
	}


	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.menu_right_tv) {
			if (mCurrentPosition == 0) {
				//提币记录
				//WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_WITHDRAW_TYPE);
				ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.WITHDRAW);

			} else {
				//平台内转账记录
				ServiceRepo.getRecordService().gotoRecord(v.getContext(), RecordType.PLATFORM_TRANSFER);
				//WithDrawRechargeHisActivity.startMe(v.getContext(), Constants.CODE_RECORD_TRANSFER_TYPE);
			}
		} else if (v.getId() == R.id.menu_back) {
			finish();
		}
//
	}

//

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		if (requestCode == ScannerActivity.CODE_RESULT_ADDRESS && withDrawFragment != null) {
			String result = data.getStringExtra(Scanner.Scan.RESULT);
			withDrawFragment.setAddressValue(result);
		} else if (requestCode == Constants.CoinAddressActivity_CODE_REQUEST) {
			WithDrawAddressModel.DataBean.ListBean item = (WithDrawAddressModel.DataBean.ListBean) data.getSerializableExtra("item");
			if (item != null && withDrawFragment != null) {
				if (!TextUtils.isEmpty(item.getAddress()))
					withDrawFragment.setAddressValue(item.getAddress());
				if (!TextUtils.isEmpty(item.getTag()))
					withDrawFragment.setTagValue(item.getTag());
			}
		} else if (requestCode == TransferAddressActivity.CODE_REQUEST) {
			String targetId = data.getStringExtra("targetId");
			if (!TextUtils.isEmpty(targetId) && transferAccountsFragment != null) {
				transferAccountsFragment.setTargetId(targetId);
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults.length == 0) {
			return;
		}
		if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
			ScannerActivity.startMeForResult(WithDrawActivity.this, ScannerActivity.CODE_RESULT_ADDRESS);
		} else {
			ToastUtil.show(R.string.please_give_permission_grant);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
	}

}
