package com.coinbene.manbiwang.user.about;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.BuildConfig;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.dialog.UpdateDialog;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.AppUtil;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.dialog.UpdateProcessDialog;
import com.coinbene.manbiwang.model.http.CheckVersionResponse;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 */

public class AboutActivity extends CoinbeneBaseActivity {
	@BindView(R2.id.about_version_tv)
	TextView curVersionView;
	@BindView(R2.id.about_currentVersion_layout)
	View currentVersion_layout;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.about_TermsOfService_layout)
	View termsOfService;
	@BindView(R2.id.about_joinCommunity_layout)
	View joinCommunity;
	@BindView(R2.id.about_version)
	TextView aboutVersion;
	@BindView(R2.id.about_logo)
	View aboutLogo;

	@BindView(R2.id.about_share_layout)
	View shareLayout;

	private String TAG = "AboutActivity";
	public static final int APPLY_READ_EXTERNAL_STORAGE = 0x111;
	private String downLoadUrl = null;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, AboutActivity.class);
		context.startActivity(intent);
	}


	@Override
	public int initLayout() {
		return R.layout.settings_setting_about;
	}

	@Override
	public void initView() {
		titleView.setText(getText(R.string.set_about_title));
		String version = AppUtil.getVersionName(this);
		curVersionView.setText(String.format(getString(R.string.set_about_version), version));
		if (SpUtil.getHasNewVersion()) {
			aboutVersion.setVisibility(View.VISIBLE);
		} else {
			aboutVersion.setVisibility(View.GONE);
		}

		if (!ConfigHelper.shareEnable()) {
			shareLayout.setVisibility(View.GONE);
		}

		if (!ConfigHelper.communityEnable()) {
			joinCommunity.setVisibility(View.GONE);
		}
	}

	@Override
	public void setListener() {
		currentVersion_layout.setOnClickListener(this);
		shareLayout.setOnClickListener(this);
		backView.setOnClickListener(this);
		termsOfService.setOnClickListener(this);
		joinCommunity.setOnClickListener(this);
		aboutLogo.setOnClickListener(this);
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
		if (v.getId() == R.id.about_currentVersion_layout) {
			checkVersion(v.getContext());
		} else if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.about_joinCommunity_layout) {
			CommunityActivity.startActivity(this);
		} else if (v.getId() == R.id.about_TermsOfService_layout) {
			Bundle bundle = new Bundle();
			bundle.putString("title", getResources().getString(R.string.set_about_service_link));
			UIBusService.getInstance().openUri(AboutActivity.this, UrlUtil.getAboutUsUrl(), bundle);
		} else if (v.getId() == R.id.about_logo) {
			if (CBRepository.getEnableDebug()) {
				ARouter.getInstance().build(RouteHub.App.debugActivity).navigation(v.getContext());
			}
		} else if (v.getId() == R.id.about_share_layout) {
			ShareCoinBeneActivity.startMe(this);
		}
	}

	private void checkVersion(Context context) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("type", "android");
		httpParams.put("versionCode", AppUtil.getVersionCode(context));
		httpParams.put("versionName", AppUtil.getVersionName(context));

		OkGo.<CheckVersionResponse>get(Constants.APP_CHECK_UPDATE).tag(this).params(httpParams).execute(new DialogCallback<CheckVersionResponse>(this) {
			@Override
			public void onSuc(Response<CheckVersionResponse> response) {
				CheckVersionResponse baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (baseResponse.getData() != null &&
							!TextUtils.isEmpty(baseResponse.getData().getDownUrl())) {
						UpdateDialog updateDialog = new UpdateDialog(context);
						updateDialog.notNeedForceUpdate(true);
						if (baseResponse.getData() != null) {
							updateDialog.setData(baseResponse.getData());//(baseResponse.getData().getDes());
						}
						updateDialog.setClickLisenter(updateDialogClickListener);
						updateDialog.show();

					} else {
						ToastUtil.show(R.string.check_version_new);
					}
				}
			}

			@Override
			public void onE(Response<CheckVersionResponse> response) {
			}

		});
	}

	UpdateDialog.UpdateDialogClickListener updateDialogClickListener = new UpdateDialog.UpdateDialogClickListener() {
		@Override
		public void onCancel() {

		}

		@Override
		public void goToUpdate(String downUrl, boolean isForceUpdate) {
			downLoadUrl = downUrl;
			if (checkWritePermission()) {
				doUpdate();
			}
		}
	};

	private void doUpdate() {
		ToastUtil.show(R.string.toast_tips_downing);
		UpdateProcessDialog updateProcessDialog = new UpdateProcessDialog(AboutActivity.this);
		updateProcessDialog.setData(downLoadUrl, false);
		updateProcessDialog.show();
	}

	private boolean checkWritePermission() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission
				.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			//权限还没有授予，需要在这里写申请权限的代码
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
					APPLY_READ_EXTERNAL_STORAGE);
			return false;
		}
		return true;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (grantResults == null || grantResults.length == 0) {
			ToastUtil.show(R.string.please_give_permission_grant);
			return;
		}

		if (requestCode == APPLY_READ_EXTERNAL_STORAGE) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				doUpdate();
			} else {
				ToastUtil.show(R.string.please_give_permission_grant);
			}
		}
	}
}
