package com.coinbene.manbiwang.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.aspect.annotation.NeedLogin;
import com.coinbene.common.base.CoinbeneBaseFragment;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.ConfigHelper;
import com.coinbene.common.utils.LockUtils;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.websocket.userevent.UsereventWebsocket;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.about.AboutActivity;
import com.coinbene.manbiwang.user.balance.SettingBalanceActivity;
import com.coinbene.manbiwang.user.preference.SettingDefinedActivity;
import com.coinbene.manbiwang.user.safe.SettingSafeActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * 个人中心Fragment
 */
public class MySelfFragment extends CoinbeneBaseFragment {

	@BindView(R2.id.tv_VIP)
	TextView tvVIP;
	@BindView(R2.id.layout_VIP)
	RelativeLayout layoutVIP;
	@BindView(R2.id.name_tv)
	TextView nameTv;
	@BindView(R2.id.safe_layout)
	RelativeLayout safeLayout;
	@BindView(R2.id.customer_service_layout)
	RelativeLayout customerService;
	@BindView(R2.id.balance_layout)
	RelativeLayout balanceLayout;
	@BindView(R2.id.defined_layout)
	RelativeLayout definedLayout;
	@BindView(R2.id.about_layout)
	RelativeLayout aboutLayout;
	@BindView(R2.id.quit_layout)
	RelativeLayout quiteLayout;
	@BindView(R2.id.header_img)
	ImageView headerImgView;
	@BindView(R2.id.sub_line_view)
	View subLineView;
	@BindView(R2.id.userInfo_layout)
	View userInfoLayout;
	@BindView(R2.id.uid_tv)
	TextView uidTv;
	@BindView(R2.id.invitation_layout)
	View invitationLayout;
	@BindView(R2.id.self_version)
	TextView selfVersion;
	@BindView(R2.id.iv_copy)
	View ivCopy;
	@BindView(R2.id.left_quit_tv)
	View leftQuitTv;

	/**
	 * 上次登录的UID
	 */
	private int lastUid = 0;

	public static MySelfFragment newInstance() {
		Bundle args = new Bundle();
		MySelfFragment fragment = new MySelfFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public int initLayout() {
		return R.layout.fr_myself_new;
	}

	@Override
	public void initView(View rootView) {

	}

	@Override
	public void setListener() {
		leftQuitTv.setOnClickListener(v -> {

			CommonUtil.exitLoginClearData();
			showInitView();
		});
		customerService.setOnClickListener(v -> UIBusService.getInstance().openUri(getContext(), "coinbene://" + UIRouter.HOST_CUSTOMER, null));
		safeLayout.setOnClickListener(v -> SettingSafeActivity.startMe(v.getContext()));
		balanceLayout.setOnClickListener(v -> SettingBalanceActivity.startMe(v.getContext()));
		definedLayout.setOnClickListener(v -> SettingDefinedActivity.startMe(getActivity()));
		aboutLayout.setOnClickListener(v -> AboutActivity.startMe(aboutLayout.getContext()));
//		quiteLayout.setOnClickListener(v -> {
//			CommonUtil.exitLoginClearData();
//			showInitView();
//		});
		ivCopy.setOnClickListener(v -> copy());
		invitationLayout.setOnClickListener(v -> UIBusService.getInstance().openUri(getContext(), "coinbene://" + UIRouter.HOST_INVITE_REBATE, null));
		headerImgView.setOnClickListener(v -> gotoLoginOrLock());
		userInfoLayout.setOnClickListener(v -> headerImgView.performClick());

		layoutVIP.setOnClickListener(v -> {
			Bundle bundle = new Bundle();
			//bundle.putString(WebviewActivity.EXTRA_TITLE, getString(R.string.res_rate_level));
			//费率等级H5
			UIBusService.getInstance().openUri(layoutVIP.getContext(), Constants.BASE_URL_H5 + "/loading.html?redirect_url=%2Factivity%2Ffee&min_version=2.4.0&auth=false&replace=true", bundle);
		});
	}

	private void copy() {
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		if (userInfo != null && !TextUtils.isEmpty(userInfo.displayUserId)) {
			StringUtils.copyStrToClip(userInfo.displayUserId);
		}
	}

	@Override
	public void initData() {

	}

	@Override
	public void onFragmentShow() {
		showInitView();
		queryUserInfo();
	}

	@Override
	public void onFragmentHide() {

	}


	private void showInitView() {
		String site_str = SiteController.getInstance().getSiteName();

		//只有主站才有客服
		if (!TextUtils.isEmpty(site_str) && !site_str.equals(Constants.SITE_MAIN)) {
			customerService.setVisibility(View.GONE);
		}

		if (!ConfigHelper.getSupportOnlineConfig().isEnable()) {
			customerService.setVisibility(View.GONE);
		}

		if (SiteHelper.isBrSite()) {
			invitationLayout.setVisibility(View.GONE);
		}

		switch (ServiceRepo.getUserService().getUserStatus()) {
			case UN_LOGIN:
				showUnloginView();
				break;
			case LOCKED:
				LockUtils.showLockPage(getActivity());
				break;
			case LOGIN:
				showLoginView();
				break;
		}

		checkVersion();
	}

	private void showLoginView() {
		safeLayout.setVisibility(View.VISIBLE);
		balanceLayout.setVisibility(View.VISIBLE);
		quiteLayout.setVisibility(View.VISIBLE);
		layoutVIP.setVisibility(View.VISIBLE);
		ivCopy.setVisibility(View.VISIBLE);
		UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
		nameTv.setText(CheckMatcherUtils.checkEmail(userInfo.loginId) ? StringUtils.settingEmail(userInfo.loginId) : StringUtils.settingPhone(userInfo.loginId));
		if (!TextUtils.isEmpty(userInfo.displayUserId)) {
			uidTv.setText(String.format("UID:%s", userInfo.displayUserId));
			uidTv.setVisibility(View.VISIBLE);
			ivCopy.setVisibility(View.VISIBLE);
		} else {
			ivCopy.setVisibility(View.GONE);
			uidTv.setVisibility(View.GONE);
		}
	}

	private void showUnloginView() {
		safeLayout.setVisibility(View.GONE);
		balanceLayout.setVisibility(View.GONE);
		layoutVIP.setVisibility(View.GONE);
		ivCopy.setVisibility(View.GONE);
		quiteLayout.setVisibility(View.GONE);
		nameTv.setText(getText(R.string.unlogin_txt));
		uidTv.setText(getText(R.string.unlogin_tips));
		uidTv.setVisibility(View.VISIBLE);
		subLineView.setVisibility(View.GONE);
	}


	private void checkVersion() {
		if (SpUtil.getHasNewVersion()) {
			selfVersion.setVisibility(View.VISIBLE);
		} else {
			selfVersion.setVisibility(View.GONE);
		}
	}

	@NeedLogin(jump = true)
	private void gotoLoginOrLock() {

	}

	/**
	 * 获取用户信息
	 */
	@NeedLogin
	private void queryUserInfo() {
		OkGo.<UserInfoResponse>get(Constants.USER_GET_USERINFO).tag(this).execute(new NewJsonSubCallBack<UserInfoResponse>() {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {

				if (null != response.body().data) {
					if (TextUtils.isEmpty(response.body().data.level)) {
						tvVIP.setText(String.format("VIP %s", "--"));
					} else {
						tvVIP.setText(String.format("VIP %s", response.body().data.level));
					}
				}
			}

			@Override
			public UserInfoResponse dealJSONConvertedResult(UserInfoResponse userInfoResponse) {
				if (userInfoResponse.isSuccess()) {
					UserInfoController.getInstance().updateUserInfo(userInfoResponse.data);
				}

				return super.dealJSONConvertedResult(userInfoResponse);
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {
			}
		});
	}
}
