package com.coinbene.manbiwang.user.safe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
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
import butterknife.Unbinder;


/**
 * @author ding
 * 修改密码
 */
public class ResetPasswordActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;
	@BindView(R2.id.old_password_input)
	TextInputLayout oldPasswordInput;
	@BindView(R2.id.old_pwd_close)
	ImageView oldPwdClose;
	@BindView(R2.id.new_password_input)
	TextInputLayout newPasswordInput;
	@BindView(R2.id.new_pwd_close)
	ImageView newPwdClose;
	@BindView(R2.id.reset_pwd_btn)
	Button resetBtn;

	public static void startActivity(Context context) {
		context.startActivity(new Intent(context, ResetPasswordActivity.class));
	}

	@Override
	public int initLayout() {
		return R.layout.settings_activity_reset_password;
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

	public void init() {
		oldPasswordInput.getEditText().setHint(R.string.pwd_is_empty);
		newPasswordInput.getEditText().setHint(R.string.pwd_check);

		initListener();
	}


	public void initListener() {
		menuBack.setOnClickListener(this);
		resetBtn.setOnClickListener(this);
		oldPwdClose.setOnClickListener(this);
		newPwdClose.setOnClickListener(this);

		oldPasswordInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				showCloseImg(true, s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		newPasswordInput.getEditText().addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				showCloseImg(false, s);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});


	}


	/**
	 * 清空数据icon展示隐藏
	 */
	private void showCloseImg(boolean type, CharSequence s) {

		if (type) {
			//                旧密码清空icon
			if (!TextUtils.isEmpty(s)) {
				oldPasswordInput.setPasswordVisibilityToggleEnabled(true);
				oldPwdClose.setVisibility(View.VISIBLE);
			} else {
				oldPwdClose.setVisibility(View.GONE);
				oldPasswordInput.setPasswordVisibilityToggleEnabled(false);
			}
		} else {
			//                旧密码清空icon
			if (!TextUtils.isEmpty(s)) {
				newPwdClose.setVisibility(View.VISIBLE);
				newPasswordInput.setPasswordVisibilityToggleEnabled(true);
			} else {
				newPwdClose.setVisibility(View.GONE);
				newPasswordInput.setPasswordVisibilityToggleEnabled(false);
			}
		}
	}


	/**
	 * 校验
	 */
	public Boolean verificationPassword(String oldPassword, String newPassword) {

		if (TextUtils.isEmpty(oldPassword)) {
			ToastUtil.show(R.string.please_enter_oldpassword);
			return false;
		}

		if (TextUtils.isEmpty(newPassword)) {
			ToastUtil.show(R.string.please_enter_newpassword);
			return false;
		}

		if (CheckMatcherUtils.checkChiness(newPassword)) {
			ToastUtil.show(R.string.contains_chinese_chat);
			return false;
		}

		if (!CheckMatcherUtils.checkPassword(newPassword)) {
			ToastUtil.show(R.string.pwd_check);
			return false;
		}

		return true;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.menu_back) {
			finish();
		} else if (id == R.id.reset_pwd_btn) {
			updatePassword();
		} else if (id == R.id.old_pwd_close) {
			oldPasswordInput.getEditText().setText("");
		} else if (id == R.id.new_pwd_close) {
			newPasswordInput.getEditText().setText("");
		}
	}

	/**
	 * 更改密码
	 */
	private void updatePassword() {
		String oldPwd = oldPasswordInput.getEditText().getText().toString();
		String newPwd = newPasswordInput.getEditText().getText().toString();

		if (verificationPassword(oldPwd, newPwd)) {

			HttpParams httpParams = new HttpParams();
			httpParams.put("orginPasswd", MD5Util.MD5(oldPwd));
			httpParams.put("newPasswd", MD5Util.MD5(newPwd));

			OkGo.<BaseRes>post(Constants.UPDATE_LOGIN_PASSWORD).params(httpParams).tag(this).execute(new NewDialogCallback<BaseRes>(this) {
				@Override
				public void onSuc(Response<BaseRes> response) {
					ToastUtil.show(R.string.set_first_check_success);
					finish();
				}

				@Override
				public void onE(Response<BaseRes> response) {
				}
			});

		}
	}

}
