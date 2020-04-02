package com.coinbene.manbiwang.user.login;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.router.UIRouter;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.datacollection.DataCollectionHanlder;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.lock.PatternFingerPrintSetActivity;
import com.coinbene.manbiwang.user.login.verify.VerifyDialog;
import com.coinbene.manbiwang.user.login.verify.VerifyResult;
import com.coinbene.manbiwang.user.safe.BindMailActivity;
import com.coinbene.manbiwang.user.safe.SendEmailActivity;
import com.github.florent37.inlineactivityresult.InlineActivityResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 15分钟退出，先不清空用户信息；用户没有登录，或者直接返回的时候，才清空用户信息
 */
@Route(path = RouteHub.User.loginActivity)
public class LoginActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.login_btn)
	TextView loginBtn;
	@BindView(R2.id.forget_pwd)
	TextView forgetPwdBtn;
	@BindView(R2.id.account_view)
	EditTextTwoIcon accountInputView;

	@BindView(R2.id.pwd_view)
	EditTextTwoIcon pwdInputView;

	private String inputName = "";
	private String inputPwd = "";

	@Autowired
	String routeUrl;

	@Autowired
	boolean forceQuit;

	@Autowired
	int tabIndex;

	private VerifyDialog mVerifyDialog;

	@Override
	public int initLayout() {
		return R.layout.user_login;
	}

	@Override
	public void initView() {
		mTopBar.setTitle(R.string.user_login_title);

		Button rightTextButton = mTopBar.addRightTextButton(R.string.user_menu_register, R.id.res_title_right_text);
		rightTextButton.setAllCaps(false);
		rightTextButton.setOnClickListener(v -> {
					PostPointHandler.postClickData(PostPointHandler.login_login_register_btn);
					RegisterPhoneActivity.startMeForResult(LoginActivity.this);
				});

		SpUtil.put(this, SpUtil.PRE_LOGIN_ACTIVITY_EXIST, true);

		setSwipeBackEnable(false);

		accountInputView.setFirstRightIcon(R.drawable.icon_close);
		accountInputView.setSecondRightDown();
		accountInputView.getInputText().setHint(R.string.user_input_name);

		pwdInputView.setFirstRightIcon(R.drawable.icon_close);
		pwdInputView.setSecondRightPwdEye();
		pwdInputView.getInputText().setHint(R.string.user_input_pwd);

		IntentFilter filterIntent = new IntentFilter(Constants.BROAD_SEND_REGISTER_EMAIL);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);

		String name = SpUtil.get(this, SpUtil.PRE_USER_NAME, "");
		if (!TextUtils.isEmpty(name)) {
			accountInputView.getInputText().setText(name);
			accountInputView.getInputText().setSelection(name.length());
		}

		mVerifyDialog = new VerifyDialog(this, "nc_login_h5");
	}


	@Override
	public void setListener() {
		loginBtn.setOnClickListener(v -> {
			PostPointHandler.postClickData(PostPointHandler.login_login_btn);

			KeyboardUtils.hideKeyboard(pwdInputView);

			inputName = accountInputView.getInputStr();
			if (TextUtils.isEmpty(inputName)) {
				ToastUtil.show(R.string.account_is_empty);
				return;
			}
			inputPwd = pwdInputView.getInputStr();
			if (TextUtils.isEmpty(inputPwd)) {
				ToastUtil.show(R.string.pwd_is_empty);
				return;
			}
			inputName = inputName.trim();
			boolean isEmail = false, isPhone = false;
			if (inputName.contains("@")) {
				isEmail = CheckMatcherUtils.checkEmail(inputName);
			} else {
				isPhone = CheckMatcherUtils.checkPhoneNumber(inputName);
			}
			if (!isEmail && !isPhone) {
				ToastUtil.show(R.string.login_name_check_is_wrong);
				return;
			}
			if (!CheckMatcherUtils.checkPwd6_20(inputPwd)) {
				ToastUtil.show(R.string.pwd_check);
				return;
			}
			String type = isEmail ? Constants.TYPE_MAIL : Constants.TYPE_PHONE;
			KeyboardUtils.hideKeyboard(v);

			mVerifyDialog.showWithCallback(result -> doLogin(type, result));
		});

		forgetPwdBtn.setOnClickListener(v -> {
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setItems(new String[]{getResources().getString(R.string.dialog_get_pwd_phone), getResources().getString(R.string.dialog_get_pwd_mail)}, clickListener)
					.setCancelable(false).setNegativeButton(getResources().getString(R.string.btn_cancel),
					(dialog1, which) -> {

					}).setTitle(getResources().getString(R.string.dialog_get_pwd_title))
					.create();
			dialog.show();
		});
	}

	@Override
	public void initData() {
		routeUrl = getIntent().getStringExtra("routeUrl");
	}

	@Override
	public boolean needLock() {
		return false;
	}

	@Override
	protected void onBack() {
		super.onBack();
		if (loginBtn != null) {
			KeyboardUtils.hideKeyboard(loginBtn);
		}

		//forceQuit 提前清空用户信息
		if (forceQuit) {
			//回到行情页面
			Bundle bundle = new Bundle();
			bundle.putString("tab", Constants.TAB_HOME);
			UIBusService.getInstance().openUri(this, UrlUtil.getCoinbeneUrl(UIRouter.HOST_CHANGE_TAB), bundle);
		}
	}

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == Constants.BROAD_SEND_REGISTER_EMAIL) {
				finish();
			}
		}
	};


	DialogInterface.OnClickListener clickListener = (dialog, which) -> {
		if (which == 0) {
			//手机号找回密码
			pwdInputView.getInputText().setText("");
			GetUserAuthActivity.startMe(LoginActivity.this);
		} else {
			//邮箱找回密码
			pwdInputView.getInputText().setText("");
			BindMailActivity.startMeForResult(LoginActivity.this, SendEmailActivity.TYPE_FORGET_LOGIN_PWD, BindMailActivity.CODE_RESULT);
		}
		dialog.dismiss();
	};

	private void doLogin(String type, VerifyResult verifyResult) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("loginId", DESUtils.encryptToString(inputName, Constants.Test));
		httpParams.put("passwd", MD5Util.MD5(inputPwd));

		httpParams.put("sessionId", verifyResult.getSessionid());
		httpParams.put("sig", verifyResult.getSig());
		httpParams.put("token", verifyResult.getNc_token());
		httpParams.put("scene", verifyResult.getScene());
		httpParams.put("appkey", verifyResult.getAppkey());

		OkGo.<UserInfoResponse>post(Constants.USER_LOGIN_V3).tag(this).params(httpParams).execute(new DialogCallback<UserInfoResponse>(LoginActivity.this) {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
				UserInfoResponse userLoginResponse = response.body();

				PostPointHandler.postClickData(PostPointHandler.login_login_success);

				//修复切环境登陆之后，toast语言变成本地语言的bug
				LanguageHelper.updateAppConfig(CBRepository.getContext(), LanguageHelper.getLocaleCode(LoginActivity.this));

				if (userLoginResponse.isSuccess()) {
					UserInfoResponse.DataBean userData = userLoginResponse.data;
					if (userData != null) {
						UserInfoController.getInstance().resetPwdErrorCount();
						if (!TextUtils.isEmpty(userData.token)) {
							//如果用户是邮箱登录，不论 手机号是否存在，都需要判断邮件是否认证，如果未认证，就进入到邮箱认证页面
							if (inputName != null && inputName.contains("@")) {
								//邮箱登录
								if (!String.valueOf(userData.emailStatus).equals(Constants.STATUS_MAIL_VERIFYED)) {
									sendActiveEmail();
									SendEmailActivity.startMe(LoginActivity.this, inputName, SendEmailActivity.TYPE_MAIL_REGISTER);
									finish();
									return;
								}
							}
							//手机登录,或者 邮箱登录已经认证
							SpUtil.put(LoginActivity.this, SpUtil.PRE_USER_NAME, inputName);

							UserInfoController.getInstance().regisiterUser(userLoginResponse);
							UserInfoController.getInstance().setLock(false);

							ToastUtil.show(R.string.login_success_toast);

							loginSuccess();
						} else {

							// 二次验证,如果二次验证失败，用户并没有成功登陆，登陆页面不能关闭
							Bundle bundle = new Bundle();
							bundle.putString("name", inputName);
							bundle.putString("passwd", inputPwd);
							bundle.putString("verifyWay", userData.verifyWay);
							bundle.putString("phoneNo", userData.phone);
							bundle.putString("regChannel", type);
							bundle.putSerializable("verifyResult", verifyResult);

							Intent intent = new Intent(LoginActivity.this, SecondCheckActivity.class);
							intent.putExtras(bundle);
							new InlineActivityResult(LoginActivity.this).startForResult(intent)
									.onSuccess(result -> {
										if (result.getResultCode() == RESULT_OK) {
											loginSuccess();
										}
									});
						}
					}
				}
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {

			}
		});
	}

	private void loginSuccess() {

		//如果两个都没有设置过，则同时进入手势页面，同时弹出指纹设置
		if (!UserInfoController.getInstance().isGesturePwdSet() && !UserInfoController.getInstance().isSetFingerPrint()) {
			PatternFingerPrintSetActivity.startMe(LoginActivity.this, PatternFingerPrintSetActivity.TYPE_LOGIN_SET);
		}

		ServiceRepo.getAppService().onUserLoginSuccess(tabIndex);

		DataCollectionHanlder.getInstance().putClientData(DataCollectionHanlder.appLogin, "");

		finish();

		if (!TextUtils.isEmpty(routeUrl)) {
			UIBusService.getInstance().openUri(LoginActivity.this, routeUrl, null);
		}
	}

	/**
	 * 发送认证邮箱的邮件
	 */
	private void sendActiveEmail() {
		HttpParams httpParams = new HttpParams();
		String mailType = Constants.MAIL_ONE_VERI;
		String urlPath = Constants.USER_SEND_MAIL_NOLOGIN_V2;
		httpParams.put("mailType", mailType);
		httpParams.put("email", DESUtils.encryptToString(inputName.trim(), Constants.Test));

		OkGo.<BaseRes>post(urlPath).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {

			@Override
			public void onSuc(Response<BaseRes> response) {

			}

			@Override
			public void onE(Response<BaseRes> response) {

			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		PostPointHandler.postBrowerData(PostPointHandler.login_brower);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		SpUtil.put(this, SpUtil.PRE_LOGIN_ACTIVITY_EXIST, false);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
	}
}
