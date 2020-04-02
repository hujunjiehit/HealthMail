package com.coinbene.manbiwang.user.login;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.login.verify.VerifyDialog;
import com.coinbene.manbiwang.user.login.verify.VerifyResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 通过手机号找回，
 */

public class ForgetPwdActivity extends CoinbeneBaseActivity {
	public static final int TYPE_FIND_BY_PHONE = 1;
	public static final int TYPE_FIND_BY_MAIL = 2;

	@BindView(R2.id.ok_btn)
	TextView okBtn;

	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;

	@BindView(R2.id.pwd_view)
	EditTextTwoIcon pwdView;
	@BindView(R2.id.left_msg_code_tv)
	TextView codeLabel;
	@BindView(R2.id.send_msgcode_tv)
	TextView sendMsgCodeTv;
	@BindView(R2.id.msg_code_input)
	EditText msgCodeInput;
	@BindView(R2.id.tv_google_code)
	TextView mTvGoogleCode;
	@BindView(R2.id.google_code_input)
	EditText mGoogleCodeInput;
	@BindView(R2.id.layout_google)
	RelativeLayout mLayoutGoogle;

//    @BindView(R.id.country_layout)
//    RelativeLayout countryLayout;

	private int bindGoogle;
	private String phone = "";

	private String msgCode = "";
	private String pwd;
	private String googleCode = "";


    private String sendMsgWait, sendMsgAgain;

	private VerifyDialog mVerifyDialog;

	@Override
	public int initLayout() {
		return R.layout.user_find_pwd_byphone;
	}

	@Override
	public void initView() {
		phone = getIntent().getStringExtra("phone");
		bindGoogle = getIntent().getIntExtra("bindGoogle", TYPE_FIND_BY_PHONE);

		mLayoutGoogle.setVisibility(bindGoogle == 1 ? View.VISIBLE : View.GONE);

		titleView.setText(getText(R.string.user_find_pwd_title));

		pwdView.setSecondRightPwdEye();
		pwdView.getInputText().setHint(R.string.user_input_pwd);

		sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
		sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);

		mVerifyDialog = new VerifyDialog(this, "nc_other_h5");
	}

	@Override
	public void setListener() {
		backView.setOnClickListener(v -> finish());

		sendMsgCodeTv.setOnClickListener(v -> sendMsgCode());

		okBtn.setOnClickListener(v -> {
			msgCode = msgCodeInput.getText().toString();
			if (TextUtils.isEmpty(msgCode)) {
				ToastUtil.show(R.string.user_hint_msg_code);
				return;
			}
			pwd = pwdView.getInputStr();
			if (TextUtils.isEmpty(pwd)) {
				ToastUtil.show(R.string.pwd_is_empty);
				return;
			} else {
				if (!CheckMatcherUtils.checkPwd6_20(pwd)) {
					ToastUtil.show(R.string.pwd_check);
					return;
				}
			}

			if (bindGoogle == 1) {
				googleCode = mGoogleCodeInput.getText().toString();
				if (TextUtils.isEmpty(googleCode)) {
					ToastUtil.show(R.string.please_input_google_code);
					return;
				}
			}
			mVerifyDialog.showWithCallback(result -> postForgetPwd(result));
		});
	}

	@Override
	public void initData() {

	}

	@Override
	public boolean needLock() {
		return false;
	}

	private void postForgetPwd(VerifyResult verifyResult) {
		HttpParams httpParams = new HttpParams();
		httpParams.put("phone", phone);
		httpParams.put("newPasswd", MD5Util.MD5(pwd));
		httpParams.put("verifyCode", msgCode);
		httpParams.put("googleCode", googleCode);

		httpParams.put("sessionId", verifyResult.getSessionid());
		httpParams.put("sig", verifyResult.getSig());
		httpParams.put("token", verifyResult.getNc_token());
		httpParams.put("scene", verifyResult.getScene());
		httpParams.put("appkey", verifyResult.getAppkey());

		OkGo.<BaseRes>post(Constants.USER_RESET_LOGIN_PASSWD).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(ForgetPwdActivity.this) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes entity = response.body();
				if (entity.isSuccess()) {
					ToastUtil.show(R.string.set_new_pwd_success);
					setResult(RESULT_OK);
					finish();
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


	private void sendMsgCode() {
		HttpParams httpParams = new HttpParams();
		httpParams.put("phone", DESUtils.encryptToString(phone, Constants.Test));
		httpParams.put("type", Constants.CODE_TYPE_RESET_LOGIN_PWD);
		OkGo.<BaseRes>post(Constants.USER_SENDSMS_NOLOGIN_V2).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(ForgetPwdActivity.this) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes entity = response.body();
				if (entity.isSuccess()) {
					ToastUtil.show(R.string.send_msg_code_success);
					count = 59;
					handler.sendEmptyMessage(send_msg_what);
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

	private int count = 59;
	private int send_msg_what = 10;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == send_msg_what) {
				sendMsgCodeTv.setText(String.format(sendMsgWait, count));
				sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_textColor_2));
				sendMsgCodeTv.setOnClickListener(null);
				count--;
				if (count > 0) {
					handler.sendEmptyMessageDelayed(send_msg_what, 1000);
				} else {
					sendMsgCodeTv.setText(sendMsgAgain);
					sendMsgCodeTv.setTextColor(getResources().getColor(R.color.res_blue));
					sendMsgCodeTv.setOnClickListener(v -> sendMsgCode());
				}
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(null);
		}
	}
}
