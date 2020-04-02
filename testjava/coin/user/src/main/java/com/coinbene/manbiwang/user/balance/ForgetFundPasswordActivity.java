package com.coinbene.manbiwang.user.balance;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.google.android.material.textfield.TextInputLayout;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * @author ding
 *
 * 忘记资金密码页面
 */
public class ForgetFundPasswordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;

	@BindView(R2.id.forget_verification_mode)
	TextView verificationMode;

	@BindView(R2.id.fund_verification_code)
	EditText codeInput;
	@BindView(R2.id.input_google_code)
	EditText googleInput;

	@BindView(R2.id.forget_input)
	TextInputLayout passwordInput;

	@BindView(R2.id.send_verification_code)
	TextView sendCode;

	@BindView(R2.id.forget_input_close)
	ImageView inputClose;

	@BindView(R2.id.forget_submit_btn)
	Button submit;
	@BindView(R2.id.forget_vertical_line)
	View verticalLine;

	@BindView(R2.id.layout_phone)
	View layoutPhone;

	private int codeTime;

	/**
	 * 发送验证码倒计时
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == R.id.send_verification_code) {
				sendCode.setText(codeTime + "s");
				sendCode.setTextColor(getResources().getColor(R.color.res_textColor_2));
				codeTime--;
				if (codeTime > 0) {
					handler.sendEmptyMessageDelayed(R.id.send_verification_code, 1000);
				} else {
					sendCode.setEnabled(true);
					sendCode.setText(getString(R.string.send_msg_code_again));
					sendCode.setTextColor(getResources().getColor(R.color.res_blue));
					codeTime = 59;
				}
			}
		}
	};

	public static void startActivity(Context context){
		context.startActivity(new Intent(context,ForgetFundPasswordActivity.class));
	}

	@Override
	public int initLayout() {
		return R.layout.activity_forget_fund_password;
	}

	@Override
	public void initView() {
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

	/**
	 * 初始View
	 */
	public void init() {

		UserInfoTable user = UserInfoController.getInstance().getUserInfo();
		if (TextUtils.isEmpty(user.phone)) {
			layoutPhone.setVisibility(View.GONE);
		}

		googleInput.setHint(R.string.please_input_google_code);

		passwordInput.getEditText().setHint(R.string.set_cap_num_hint);

		initListener();
	}


	/**
	 * 初始监听事件
	 */
	public void initListener() {
		menuBack.setOnClickListener(this);
		sendCode.setOnClickListener(this);
		inputClose.setOnClickListener(this);
		submit.setOnClickListener(this);

		passwordInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				//根据变化显示清空&显示密码Icon
				if (TextUtils.isEmpty(s)) {
					passwordInput.setPasswordVisibilityToggleEnabled(false);
					inputClose.setVisibility(View.GONE);
				} else {
					inputClose.setVisibility(View.VISIBLE);
					passwordInput.setPasswordVisibilityToggleEnabled(true);
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	/**
	 * 发送验证码
	 */
	private void sendVerificationCode() {

		sendCode.setEnabled(false);

		OkGo.<BaseRes>post(Constants.OLD_VERIFY_CODE)
				.params("type", Constants.CODE_TYPE_RESET_ASSET_PWD)
				.tag(this)
				.execute(new NewJsonSubCallBack<BaseRes>() {
					@Override
					public void onSuc(Response<BaseRes> response) {
						ToastUtil.show(R.string.send_msg_code_success);
						codeTime = 59;
						handler.sendEmptyMessageDelayed(R.id.send_verification_code, 1000);
					}

					@Override
					public void onE(Response<BaseRes> response) {
						sendCode.setEnabled(true);
					}
				});

	}


	/**
	 * 校验条件
	 */
	private boolean check(String verifyCode, String password, String googleCode) {

		if (layoutPhone.getVisibility() == View.VISIBLE && TextUtils.isEmpty(verifyCode)) {
			ToastUtil.show(R.string.user_hint_msg_code);
			return false;
		}

		if (TextUtils.isEmpty(password)) {
			ToastUtil.show(R.string.capital_pwd_is_empty);
			return false;
		}

		if (!CheckMatcherUtils.checkSixDigits(password)) {
			ToastUtil.show(R.string.set_cap_num_hint);
			return false;
		}

		if (TextUtils.isEmpty(googleCode)) {
			ToastUtil.show(R.string.please_input_google_code);
			return false;
		}

		return true;
	}


	/**
	 * 重置资金密码
	 */
	private void resetBalancePassword(String verifyCode, String password, String googleCode) {
		if (check(verifyCode, password, googleCode)) {
			HttpParams httpParams = new HttpParams();
			if (layoutPhone.getVisibility() == View.VISIBLE) {
				httpParams.put("verifyCode", verifyCode);
			}
			httpParams.put("googleCode", googleCode);
			httpParams.put("newPasswd", MD5Util.MD5(password));
			OkGo.<BaseRes>post(Constants.USER_RESET_PIN).tag(this).params(httpParams).execute(new NewDialogCallback<BaseRes>(this) {
				@Override
				public void onSuc(Response<BaseRes> response) {
					ToastUtil.show(R.string.submit_success);
					finish();
				}

				@Override
				public void onE(Response<BaseRes> response) {
				}
			});

		}
	}

	/**
	 * 点击事件
	 */
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.menu_back) {
			finish();
		} else if (id == R.id.forget_submit_btn) {
			resetBalancePassword(codeInput.getText().toString(), passwordInput.getEditText().getText().toString(), googleInput.getText().toString());
		} else if (id == R.id.send_verification_code) {
			sendVerificationCode();
		} else if (id == R.id.forget_input_close) {
			passwordInput.getEditText().setText("");
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
		}
	}
}
