package com.coinbene.manbiwang.user.safe;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.CommonUtil;
import com.coinbene.common.utils.SiteController;
import com.coinbene.common.utils.StringUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.app.SecondCheckDialog;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.KycStatusModel;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.safe.finger.FingerCheckDialog;
import com.coinbene.manbiwang.user.safe.pattern.PatternSettingActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;
import com.wei.android.lib.fingerprintidentify.FingerprintIdentify;

import butterknife.BindView;

import static com.coinbene.common.Constants.CODE_GOOGLE_CHECK_TYPE;

/**
 * Created by mengxiangdong on 2018/10/8.
 * <p>
 * 安全中心页面
 */
@Route(path = RouteHub.User.settingSafeActivity)
public class SettingSafeActivity extends CoinbeneBaseActivity implements SecondCheckDialog.SecondCheckListner {

	@BindView(R2.id.second_checked_swich)
	ToggleButton checkSwitch;
	@BindView(R2.id.left_email_tv)
	TextView emailBtn;
	@BindView(R2.id.phone_layout)
	View phoneLayout;

	@BindView(R2.id.bind_phone_tv)
	TextView bindPhoneTv;

	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.mail_layout)
	View mailLayout;
	@BindView(R2.id.bind_email_tv)
	TextView bindEmailTv;
	@BindView(R2.id.email_value_tv)
	TextView emailValueTv;
	@BindView(R2.id.google_layout)
	View googleLayout;
	@BindView(R2.id.changeType_layout)
	View changeTypelayout;

	@BindView(R2.id.arrow_img0)
	View arrowImg0;

	@BindView(R2.id.check_txt_tv)
	TextView firstSelectTv;
	@BindView(R2.id.bind_google_tv)
	TextView bindGoogleTv;
	@BindView(R2.id.bind_google_stutas)
	TextView bindGoogleStutas;

	@BindView(R2.id.gesture_tg_btn)
	ToggleButton gesture_taggleBtn;
	@BindView(R2.id.finger_tg_btn)
	ToggleButton toggleFingerBtn;
	private boolean checkedStatus = false;
	private boolean gesture_retry_error = false;
	private boolean checkFingerStatus = false;
	private FingerprintIdentify mFingerprintIdentify;
	@BindView(R2.id.fingerprint_layout)
	RelativeLayout fingerLayout;
	@BindView(R2.id.rl_auth)
	RelativeLayout rl_auth;
	@BindView(R2.id.tv_auth_state)
	TextView tv_auth_state;
	@BindView(R2.id.iv_auth_state)
	ImageView iv_auth_state;
	@BindView(R2.id.finger_line)
	View fingerLine;
	private boolean isDeviceLocked;
	private int authStutes = 0;
	private boolean otcSwitch = false;
	@BindView(R2.id.auth_line_view)
	View authLineView;
	@BindView(R2.id.reset_phoneNum)
	TextView resetPhone;
	@BindView(R2.id.save_reset_password)
	View resetPwd;
	private SecondCheckDialog secondCheckDialog;
	private UserInfoTable user;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, SettingSafeActivity.class);
		context.startActivity(intent);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_setting_safe;
	}

	@Override
	public void initView() {
		//需要监听返回事件，滑动返回的时候，无法监听
		setSwipeBackEnable(false);
		backView.setOnClickListener(this);
		phoneLayout.setOnClickListener(this);
		mailLayout.setOnClickListener(this);
		resetPwd.setOnClickListener(this);
		googleLayout.setOnClickListener(this);
		changeTypelayout.setOnClickListener(this);
		rl_auth.setOnClickListener(this);
		checkSwitch.setOnClickListener(this);

		// 登录二次验证状态
		UserInfoTable user = UserInfoController.getInstance().getUserInfo();
		if (user == null) {//未登录，不要进入这里
			finish();
			return;
		}
		checkSwitch.setChecked(user.loginVerify);

		checkedStatus = UserInfoController.getInstance().isGesturePwdSet();
		checkFingerStatus = UserInfoController.getInstance().isSetFingerPrint();
		gesture_taggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
			if (checkedStatus == isChecked) {
				return;
			}
			checkedStatus = isChecked;
			//监听切换后的状态
			if (isChecked) {
				PatternSettingActivity.startMeForResult(SettingSafeActivity.this, PatternSettingActivity.CODE_RESULT, PatternSettingActivity.TYPE_SET_OPEN);
			} else {
				PatternSettingActivity.startMeForResult(SettingSafeActivity.this, PatternSettingActivity.CODE_RESULT, PatternSettingActivity.TYPE_SET_CHECK_CLOSE);
			}
		});
		toggleFingerBtn.setOnCheckedChangeListener(onFingerCheckedChangeListener);
		mFingerprintIdentify = new FingerprintIdentify(CBRepository.getContext());
		mFingerprintIdentify.init();
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


	private void init() {
		UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
		if (bindEmailTv == null) {
			return;
		}
		if (userTable == null) {
			ToastUtil.show(R.string.user_info_fail);
			return;
		}
		arrowImg0.setVisibility(View.VISIBLE);
		if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_UNVERIFY)) {
			bindEmailTv.setVisibility(View.VISIBLE);
			bindEmailTv.setText(R.string.setting_not_activate_label);
			bindEmailTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
			emailValueTv.setVisibility(View.VISIBLE);
			String email = TextUtils.isEmpty(userTable.email) ? "" : userTable.email;
			//            emailValueTv.setText(CommonUtil.parseEmail(email));
			emailValueTv.setText(StringUtils.settingEmail(email));
		} else if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
			bindEmailTv.setVisibility(View.GONE);
			mailLayout.setEnabled(false);
			arrowImg0.setVisibility(View.GONE);
			String email = TextUtils.isEmpty(userTable.email) ? "" : userTable.email;
			//            emailValueTv.setText(CommonUtil.parseEmail(email));
			emailValueTv.setText(StringUtils.settingEmail(email));
			emailValueTv.setTextColor(getResources().getColor(R.color.res_textColor_3));
			emailValueTv.setVisibility(View.VISIBLE);
		} else if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_UNBIND)) {
			bindEmailTv.setText(getString(R.string.setting_bind));
			bindEmailTv.setVisibility(View.VISIBLE);
			bindEmailTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
			emailValueTv.setVisibility(View.GONE);
		}
		if (!TextUtils.isEmpty(userTable.phone)) {
			//            bindPhoneTv.setText(CommonUtil.parsePhoneNum(userTable.phoneNo));
			bindPhoneTv.setText(StringUtils.settingPhone(userTable.phone));
			bindPhoneTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_textColor_3));
			resetPhone.setVisibility(View.VISIBLE);
		} else {
			bindPhoneTv.setText(R.string.setting_bind);
			bindPhoneTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
		}
		if (userTable.googleBind) {
			bindGoogleTv.setText(R.string.setting_rest);
			bindGoogleStutas.setVisibility(View.VISIBLE);
			bindGoogleStutas.setText(R.string.binded);
			bindGoogleTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
		} else {
			bindGoogleStutas.setVisibility(View.GONE);
			bindGoogleTv.setText(getString(R.string.setting_bind));
			bindGoogleTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
		}

		if (!TextUtils.isEmpty(userTable.verifyWay)) {
			if (userTable.verifyWay.equals(CODE_GOOGLE_CHECK_TYPE)) {
				firstSelectTv.setText(R.string.first_check_google);
			} else {
				firstSelectTv.setText(R.string.first_check_phone_msg);
			}
		}
	}

	private void refreshFingerPatternView() {
		checkedStatus = UserInfoController.getInstance().isGesturePwdSet();
		gesture_taggleBtn.setChecked(checkedStatus);
		if (mFingerprintIdentify != null && mFingerprintIdentify.isHardwareEnable()) {
			fingerLine.setVisibility(View.VISIBLE);
			fingerLayout.setVisibility(View.VISIBLE);
			checkFingerStatus = UserInfoController.getInstance().isSetFingerPrint();
			toggleFingerBtn.setChecked(checkFingerStatus);
		} else {
			fingerLine.setVisibility(View.GONE);
			fingerLayout.setVisibility(View.GONE);
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.phone_layout) {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			//邮箱账户，未认证邮箱点击绑定手机时
			if (TextUtils.isEmpty(userTable.phone)) {
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
					String dialogContent = v.getContext().getString(R.string.dialog_setting_email_1);
					dialog.setMessage(dialogContent);
					dialog.setNegativeButton(v.getContext().getString(R.string.btn_ok), (dialog16, which) -> dialog16.dismiss());
					dialog.show();
					return;
				}
				sendBindPhoneEmail(userTable.email);
			} else {

				//                绑定手机
				if (!UserInfoController.getInstance().checkKycStatus()) {

					String site_str = SiteController.getInstance().getSiteName();
					/* 巴西站app没有实名认证 */
					if (!TextUtils.isEmpty(site_str) && site_str.equals(Constants.SITE_BR)) {
						AlertDialog.Builder dialog = new AlertDialog.Builder(this);
						dialog.setMessage(R.string.please_to_br_kyc);
						dialog.setPositiveButton(R.string.btn_ok, (dialog1, which) -> {
							dialog1.dismiss();
						});
						dialog.show();
					} else {
						AlertDialog.Builder dialog = new AlertDialog.Builder(this);
						dialog.setMessage(R.string.please_to_kyc);
						dialog.setPositiveButton(R.string.btn_ok, (dialog1, which) -> {
							dialog1.dismiss();
						});
						dialog.show();
					}

				} else {
					//                跳转换绑手机页面
					ChangePhoneActivity.startActivity(this);
				}

			}
		} else if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.mail_layout) {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_UNVERIFY)) {//未认证，去激活页面
				ActivateEmailActivity.startMe(v.getContext());
			} else if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {//已经认证，点击没有反应
				AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
				String dialogContent = v.getContext().getString(R.string.dialog_setting_email_2);
				dialog.setMessage(dialogContent);
				dialog.setNegativeButton(v.getContext().getString(R.string.btn_ok), (dialog1, which) -> dialog1.dismiss());
				dialog.show();
			} else if (String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_UNBIND)) {//没有绑定邮箱，去绑定邮箱页面
				BindMailFromSettingActivity.startMeForResult(SettingSafeActivity.this, SendEmailActivity.TYPE_MAIL_BIND, BindMailActivity.CODE_RESULT);
			}

		} else if (v.getId() == R.id.google_layout) {
			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (!userTable.googleBind) {
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
					String dialogContent = v.getContext().getString(R.string.dialog_setting_email_1);
					dialog.setMessage(dialogContent);
					dialog.setNegativeButton(v.getContext().getString(R.string.btn_ok), (dialog1, which) -> dialog1.dismiss());
					dialog.show();
					return;
				}

				BindRestGoogleActivity.startMe(v.getContext(), true);
			} else {
				BindRestGoogleActivity.startMe(v.getContext(), false);
			}
		} else if (v.getId() == R.id.changeType_layout) {

			//            验证码首选项
			UserInfoTable user = UserInfoController.getInstance().getUserInfo();

			//  手机绑定&Google绑定才能设置首选项
			if (user.googleBind && !TextUtils.isEmpty(user.phone)) {
				VerificationPreferenceActivity.startActivity(this);
			} else {

				AlertDialog dialog = new AlertDialog.Builder(this).create();

				if (!user.googleBind && TextUtils.isEmpty(user.phone)) {
					dialog.setMessage(getString(R.string.dialog_buy_sell_content));
					dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_confirm), (dialog14, which) -> {
						dialog14.dismiss();//关闭对话框
					});
				} else if (!user.googleBind) {
					dialog.setMessage(getString(R.string.please_first_bind_google));
					dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_confirm), (dialog14, which) -> {
						dialog14.dismiss();//关闭对话框
					});
				} else if (TextUtils.isEmpty(user.phone)) {
					dialog.setMessage(getString(R.string.please_first_bind_phone));
					dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_confirm), (dialog14, which) -> {
						dialog14.dismiss();//关闭对话框
					});
				}

				dialog.show();//显示对话框

				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.res_blue));

			}


		} else if (v.getId() == R.id.rl_auth) {//实名认证

//            AuthActivity.startMe(SettingSafeActivity.this);

			UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
			if (TextUtils.isEmpty(userTable.phone)) {
				if (!String.valueOf(userTable.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
					String dialogContent = v.getContext().getString(R.string.dialog_recharge_content);
					dialog.setMessage(dialogContent);
					dialog.setNegativeButton(v.getContext().getString(R.string.btn_ok), (dialog12, which) -> dialog12.dismiss());
					dialog.show();
					return;
				}
			}
			//
			refreshAuthStates(true);

		} else if (v.getId() == R.id.save_reset_password) {
			ResetPasswordActivity.startActivity(this);
		} else if (v.getId() == R.id.second_checked_swich) {

			//  登录二次验证开关
			user = UserInfoController.getInstance().getUserInfo();

			if (user.googleBind || !TextUtils.isEmpty(user.phone)) {

				//  已绑定逻辑
				if (checkSwitch.isChecked()) {
					openSecondCheck();
				} else {
					//   显示弹窗
					secondCheckDialog = new SecondCheckDialog(this);
					secondCheckDialog.setCancelable(false);
					secondCheckDialog.show();
					secondCheckDialog.SecondCheckListner(this);
					if (CODE_GOOGLE_CHECK_TYPE.equals(user.verifyWay)) {
						secondCheckDialog.setMode(getString(R.string.setting_google_auth));
						secondCheckDialog.setHint(getString(R.string.check_google_code_label));
						secondCheckDialog.hideSendSMS();

					} else {
						secondCheckDialog.setMode(getString(R.string.check_msg_item_label));
						secondCheckDialog.setHint(getString(R.string.user_msg_code_label));
						secondCheckDialog.showSendSMS();
					}
				}

			} else {
				checkSwitch.setChecked(false);

				AlertDialog dialog = new AlertDialog.Builder(this).create();

				dialog.setMessage(getString(R.string.dialog_buy_sell_content));

				dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_confirm), (dialog13, which) -> {
					dialog13.dismiss();//关闭对话框
				});

				dialog.show();//显示对话框

				dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.res_blue));
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED && requestCode == PatternSettingActivity.CODE_RESULT) {
			if (data != null) {
				int type = data.getIntExtra("type", 0);
				if (type == 0) {
					return;
				}
				if (type == PatternSettingActivity.TYPE_SET_OPEN) {
					checkedStatus = false;
					gesture_taggleBtn.setChecked(false);
				} else {
					checkedStatus = true;
					gesture_taggleBtn.setChecked(checkedStatus);
				}
			}
			return;
		}
		if (requestCode == BindMailActivity.CODE_RESULT) {
			bindEmailTv.setText(R.string.setting_not_activate_label);
			bindEmailTv.setTextColor(bindEmailTv.getContext().getResources().getColor(R.color.res_blue));
			emailValueTv.setVisibility(View.VISIBLE);
		} else if (requestCode == PatternSettingActivity.CODE_RESULT) {
			int type = data.getIntExtra("type", 0);
			if (type == 0) {
				return;
			}
			if (type == PatternSettingActivity.TYPE_SET_OPEN) {//设置成功,打开锁的状态
				checkedStatus = true;
				gesture_taggleBtn.setChecked(checkedStatus);
			} else if (type == PatternSettingActivity.TYPE_SET_CHECK_CLOSE) {
				gesture_retry_error = data.getBooleanExtra("retry_error", false);
				//超过错误次数，跳转至登录页面，手势解锁关闭;否则，关闭锁的状态
				checkedStatus = false;
				gesture_taggleBtn.setChecked(checkedStatus);
				if (gesture_retry_error) {
					ToastUtil.show(R.string.gesture_login_check_error);
					UserInfoController.getInstance().clearGesturePwd();

					CommonUtil.exitLoginClearData();

					ARouter.getInstance().build(RouteHub.
							User.loginActivity)
							.withBoolean("forceQuit", true)
							.withInt("tabIndex", Constants.TAB_INDEX_DEFAULT)
							.navigation(this);
				}
			}
		}
	}

	private void sendBindPhoneEmail(String emailStr) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("mailType", Constants.MAIL_NINE_BING_PHONE);
		OkGo.<BaseRes>post(Constants.USER_SEND_MAIL).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					ToastUtil.show(R.string.send_email_success);
					SendEmailActivity.startMe(SettingSafeActivity.this, emailStr.trim(), SendEmailActivity.TYPE_BING_PHONE_NO);
				}
			}

			@Override
			public void onE(Response<BaseRes> response) {

			}

			@Override
			public void onFail(String msg) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		//这个逻辑必须加；在15分钟退出的时候，会影响
		if (CommonUtil.isLoginAndUnLocked()) {
			queryUserInfo();
			refreshFingerPatternView();
			refreshAuthStates(false);
		}
	}

	private void refreshAuthStates(boolean isClick) {
		//巴西站点，不显示实名认证
		String site_str = SiteController.getInstance().getSiteName();
		if (!TextUtils.isEmpty(site_str) && site_str.equals(Constants.SITE_BR)) {
			rl_auth.setVisibility(View.GONE);
			authLineView.setVisibility(View.GONE);
			return;
		}
		rl_auth.setVisibility(View.VISIBLE);
		authLineView.setVisibility(View.VISIBLE);
		OkGo.<KycStatusModel>get(Constants.KYC_GET_STATUS).tag(this).execute(new NewJsonSubCallBack<KycStatusModel>() {
			@Override
			public void onSuc(Response<KycStatusModel> response) {
				KycStatusModel baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (baseResponse.getData() != null) {
						authStutes = baseResponse.getData().getStatus();
						if (!isClick)
							setAuthStutasToView();
						else {
							if (authStutes == Constants.AUTH_PROCESSING || authStutes == Constants.AUTH_VERIFIED) {//审核中  已通过   跳单独的界面
								AuthProcessingOrVerifiedActivity.startMe(SettingSafeActivity.this, authStutes);
							} else {
								AuthActivity.startMe(SettingSafeActivity.this);
							}
						}

					}
				}
			}


			@Override
			public void onE(Response<KycStatusModel> response) {
				if (rl_auth != null)
					rl_auth.setVisibility(View.GONE);
				if (isClick)
					ToastUtil.show(R.string.query_fail);
			}

			@Override
			public void onFail(String msg) {
				if (rl_auth != null)
					rl_auth.setVisibility(View.GONE);
				if (isClick)
					ToastUtil.show(R.string.query_fail);
			}
		});
	}

	private void setAuthStutasToView() {
		if (tv_auth_state != null)
			if (authStutes == Constants.AUTH_PROCESSING) {//审核中
				tv_auth_state.setText(getString(R.string.Processing));
				tv_auth_state.setTextColor(getResources().getColor(R.color.res_blue));
				iv_auth_state.setVisibility(View.GONE);
			} else if (authStutes == Constants.AUTH_VERIFIED) {//已认证
				tv_auth_state.setText(getString(R.string.Verified));
				tv_auth_state.setTextColor(getResources().getColor(R.color.res_textColor_3));
				iv_auth_state.setImageResource(R.drawable.icon_auth_status_verify);
				iv_auth_state.setVisibility(View.GONE);
			} else if (authStutes == Constants.AUTH_FAILED) {//认证失败
				tv_auth_state.setText(getString(R.string.failed));
				iv_auth_state.setImageResource(R.drawable.icon_auth_status_tip);
				tv_auth_state.setTextColor(getResources().getColor(R.color.res_blue));
				iv_auth_state.setVisibility(View.GONE);
			} else if (authStutes == Constants.AUTH_UNFINISHED) {//待完善
				tv_auth_state.setText(getString(R.string.unfinished));
				iv_auth_state.setImageResource(R.drawable.icon_auth_status_tip);
				tv_auth_state.setTextColor(getResources().getColor(R.color.res_blue));
				iv_auth_state.setVisibility(View.GONE);
			} else if (authStutes == Constants.AUTH_UNVERIFIED) {//未认证
				tv_auth_state.setText(getString(R.string.unverified));
				tv_auth_state.setTextColor(getResources().getColor(R.color.res_blue));
				iv_auth_state.setImageResource(R.drawable.icon_auth_status_tip);
				iv_auth_state.setVisibility(View.GONE);
			} else {
				rl_auth.setVisibility(View.GONE);
			}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mFingerprintIdentify != null) {
			mFingerprintIdentify.cancelIdentify();
		}
	}

	private void queryUserInfo() {
		if (gesture_retry_error) {
			return;
		}
		OkGo.<UserInfoResponse>get(Constants.USER_GET_USERINFO).tag(this).execute(new NewJsonSubCallBack<UserInfoResponse>() {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
				UserInfoResponse baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					if (SettingSafeActivity.this.isFinishing()) {
						return;
					}
					init();//重新刷新Ui
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

			@Override
			public void onFail(String msg) {

			}
		});
	}

	private FingerCheckDialog fingerCheckDialog;
	CompoundButton.OnCheckedChangeListener onFingerCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			if (checkFingerStatus == isChecked) {
				return;
			}
			checkFingerStatus = isChecked;
			if (mFingerprintIdentify == null) {
				return;
			}
			if (!mFingerprintIdentify.isRegisteredFingerprint()) {
				checkFingerStatus = !checkFingerStatus;
				toggleFingerBtn.setChecked(checkFingerStatus);
				ToastUtil.show(R.string.finger_not_enable);
				return;
			}

			if (!mFingerprintIdentify.isFingerprintEnable()) {
				checkFingerStatus = !checkFingerStatus;
				toggleFingerBtn.setChecked(checkFingerStatus);
				ToastUtil.show(R.string.finger_check_fail_try_again);
				return;
			}

			fingerCheckDialog = new FingerCheckDialog(buttonView.getContext());
			fingerCheckDialog.setFingerListener(listener);
			fingerCheckDialog.show();
		}
	};

	private FingerCheckDialog.FingerListener listener = new FingerCheckDialog.FingerListener() {
		@Override
		public void verifySuccess() {
			if (mFingerprintIdentify != null) {
				mFingerprintIdentify.cancelIdentify();
			}
			isDeviceLocked = false;
			UserInfoController.getInstance().setFingerPrint(checkFingerStatus);
			ToastUtil.show(R.string.finger_set_success);
		}

		@Override
		public void verifyFail(boolean deviceLocked) {
			if (mFingerprintIdentify != null) {
				mFingerprintIdentify.cancelIdentify();
			}
			isDeviceLocked = deviceLocked;
			checkFingerStatus = !checkFingerStatus;
			toggleFingerBtn.setChecked(checkFingerStatus);
			UserInfoController.getInstance().setFingerPrint(checkFingerStatus);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
			return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		OkGo.getInstance().cancelTag(this);
		if (mFingerprintIdentify != null) {
			mFingerprintIdentify.cancelIdentify();
			mFingerprintIdentify = null;
		}
		if (fingerCheckDialog != null && fingerCheckDialog.isShowing()) {
			fingerCheckDialog.dismiss();
		}
		if (fingerCheckDialog != null) {
			fingerCheckDialog.setFingerListener(null);
			fingerCheckDialog = null;
		}

		if (secondCheckDialog != null) {
			secondCheckDialog = null;
		}
	}

	/**
	 * 二次验证关闭
	 *
	 * @param code
	 */
	private void closeValidate(String code) {

		if (TextUtils.isEmpty(code)) {
			ToastUtil.show(R.string.user_hint_msg_code);
			return;
		}

		OkGo.<BaseRes>post(Constants.LOGIN_VERIFY_CLOSE).params("verifyCode", code).tag(this).execute(new NewDialogCallback<BaseRes>(this) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.close_second_check);
				checkSwitch.setChecked(false);
				secondCheckDialog.dismiss();
			}

			@Override
			public void onE(Response<BaseRes> response) {
				checkSwitch.setChecked(true);
			}
		});
	}

	/**
	 * 二次验证开启
	 */
	private void openSecondCheck() {
		OkGo.<BaseRes>post(Constants.LOGIN_VERIFY_OPEN).params("verifyCode", "").tag(this).execute(new NewDialogCallback<BaseRes>(this) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.open_second_check);
				checkSwitch.setChecked(true);
			}

			@Override
			public void onE(Response<BaseRes> response) {
				checkSwitch.setChecked(false);
			}
		});
	}

	@Override
	public void closeCheck(String code) {
		closeValidate(code);

	}

	@Override
	public void dismiss() {
		checkSwitch.setChecked(user.loginVerify);
	}

	@Override
	public void autoCheck(String code) {
		closeValidate(code);
	}
}
