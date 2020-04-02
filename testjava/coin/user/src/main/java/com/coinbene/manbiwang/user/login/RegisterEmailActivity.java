package com.coinbene.manbiwang.user.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.ContentSpan;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.login.verify.VerifyDialog;
import com.coinbene.manbiwang.user.login.verify.VerifyResult;
import com.coinbene.manbiwang.user.safe.SendEmailActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 邮箱注册
 */

public class RegisterEmailActivity extends CoinbeneBaseActivity {


	@BindView(R2.id.register_btn)
	TextView registerBtn;
	@BindView(R2.id.menu_right_tv)
	TextView menuRegisterBtn;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;

	@BindView(R2.id.name_view)
	EditTextOneIcon emailView;
	@BindView(R2.id.pwd_view)
	EditTextTwoIcon pwdView;
	@BindView(R2.id.service_link_tv)
	TextView linkTV;
	@BindView(R2.id.invite_code_input)
	EditText inviteCodeInput;

	private String email;
	private String inviteCode;

	private VerifyDialog mVerifyDialog;

	public static void startMe(Context context) {
		Intent intent = new Intent(context, RegisterEmailActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	protected void onResume() {
		super.onResume();
		PostPointHandler.postBrowerData(PostPointHandler.register_email_brower);

	}

	@Override
	public int initLayout() {
		return R.layout.user_email_register;
	}

	@Override
	public void initView() {
		registerBtn.setOnClickListener(this);

		titleView.setText(getText(R.string.user_menu_register_title));
		backView.setOnClickListener(this);

		emailView.setFirstRightIcon(R.drawable.icon_close);
		emailView.getInputText().setHint(R.string.user_input_email);

		pwdView.setSecondRightPwdEye();
		pwdView.getInputText().setHint(R.string.user_input_pwd);

		serviceLink();

		mVerifyDialog = new VerifyDialog(this, "nc_register_h5");
	}

	private void serviceLink() {
		int defaultColor = getResources().getColor(R.color.res_blue);
		linkTV.setMovementMethod(LinkMovementMethod.getInstance());
		linkTV.setText("");
		SpannableString spanString = new SpannableString(getString(R.string.user_register_tips_sencond));
		ContentSpan span = new ContentSpan(textClickListener, defaultColor);
		spanString.setSpan(span, 0, spanString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		linkTV.append(getString(R.string.user_register_tips_first));
		boolean isChinese = LanguageHelper.isChinese(this);
		if (!isChinese) {
			linkTV.append(" ");
		}
		linkTV.append(spanString);
	}

	private View.OnClickListener textClickListener = v -> {
//		StringBuilder linkSb = new StringBuilder();
//		linkSb.append(Constants.BASE_SERVICE_LINK_URL);
//		if (!linkSb.toString().contains("?")) {
//			linkSb.append("?");
//		}
//		linkSb.append("lang=" + SiteController.getInstance().getLangName_network());

		Bundle bundle = new Bundle();
		bundle.putString(Constants.WEB_EXTRA_TITLE, getResources().getString(R.string.user_register_tips_sencond));
		UIBusService.getInstance().openUri(this,
				UrlUtil.getAboutUsUrl(),
						bundle);
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.register_btn) {

			PostPointHandler.postClickData(PostPointHandler.register_email_register_btn);
			String inputStr = emailView.getInputStr();
			if (TextUtils.isEmpty(inputStr)) {
				ToastUtil.show(R.string.email_is_empty);
				return;
			}
			boolean isEmail = CheckMatcherUtils.checkEmail(inputStr);
			if (!isEmail) {
				ToastUtil.show(R.string.email_error);
				return;
			}
			String pwdStr = pwdView.getInputStr();
			if (TextUtils.isEmpty(pwdStr)) {
				ToastUtil.show(R.string.pwd_is_empty);
				return;
			}
			if (!CheckMatcherUtils.checkPwd6_20(pwdStr)) {
				ToastUtil.show(R.string.pwd_check);
				return;
			}
			email = inputStr;

			//邀请码
			inviteCode = inviteCodeInput.getText().toString();

			KeyboardUtils.hideKeyboard(v);

			mVerifyDialog.showWithCallback(result -> doRegisterMail(inputStr, pwdStr, result));
		} else if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.service_link_tv) {
//			String linkSb;
//			boolean isPt = SiteHelper.isLocalPt();
//			if (isPt) {
//				linkSb = Constants.BASE_SERVICE_LINK_URL_PT;
//			} else {
//				linkSb = UrlUtil.getAboutUsUrl();
//			}
//
//			Bundle bundle = new Bundle();
//			bundle.putString(WebviewActivity.EXTRA_TITLE, getResources().getString(R.string.user_register_tips_sencond));
//			UIBusService.getInstance().openUri(this, linkSb, bundle);
		}
	}

	private void doRegisterMail(String inputName, String pwd, VerifyResult verifyResult) {
		String cid = SpUtil.get(this, "ClientId", "");
		HttpParams httpParams = new HttpParams();
		httpParams.put("registerMethod", Constants.TYPE_MAIL);
		httpParams.put("loginId", DESUtils.encryptToString(inputName, Constants.Test));
		httpParams.put("passwd", MD5Util.MD5(pwd));
		httpParams.put("registerChannel", CBRepository.getChannelValue());
		if (!TextUtils.isEmpty(inviteCode)) {
			httpParams.put("inviteCode", inviteCode);
		}
		if (!TextUtils.isEmpty(cid)) {
			httpParams.put("cid", cid);
		}

		httpParams.put("sessionId", verifyResult.getSessionid());
		httpParams.put("sig", verifyResult.getSig());
		httpParams.put("token", verifyResult.getNc_token());
		httpParams.put("scene", verifyResult.getScene());
		httpParams.put("appkey", verifyResult.getAppkey());

		OkGo.<UserInfoResponse>post(Constants.USER_REGISTER_V3).tag(this).params(httpParams).execute(new DialogCallback<UserInfoResponse>(RegisterEmailActivity.this) {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
				UserInfoResponse userLoginResponse = response.body();
				if (userLoginResponse.isSuccess()) {//注册成功返回用户的信息；后端自动发邮件，前端跳到发送邮件页面
					SendEmailActivity.startMe(RegisterEmailActivity.this, email, SendEmailActivity.TYPE_MAIL_REGISTER);
					finish();
				}
				PostPointHandler.postClickData(PostPointHandler.register_email_register_success);
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {
			}

		});
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
	protected void onDestroy() {
		super.onDestroy();
	}
}
