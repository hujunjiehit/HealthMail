package com.coinbene.manbiwang.user.safe;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 密码找回，绑定邮箱
 */

public class BindMailActivity extends CoinbeneBaseActivity {
	public static final int CODE_RESULT = 12;

	@BindView(R2.id.ok_btn)
	Button okBtn;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.mail_input)
	EditTextOneIcon phoneInputView;
	private int type;
	private int resultCode;

	public static void startMeForResult(Activity activity, int type, int resultCode) {
		Intent intent = new Intent(activity, BindMailActivity.class);
		intent.putExtra("type", type);
		activity.startActivityForResult(intent, resultCode);
	}

	@Override
	public int initLayout() {
		return R.layout.settings_setting_bind_mail;
	}

	@Override
	public void initView() {
		okBtn.setOnClickListener(this);
		Intent intent = getIntent();
		type = intent.getIntExtra("type", SendEmailActivity.TYPE_FORGET_LOGIN_PWD);
		resultCode = intent.getIntExtra("resultcode", 0);
		if (type == SendEmailActivity.TYPE_FORGET_LOGIN_PWD) {
			titleView.setText(getText(R.string.user_find_pwd_title));
		}

		backView.setOnClickListener(this);

		phoneInputView.setFirstRightIcon(R.drawable.icon_close);
		phoneInputView.getInputText().setHint(R.string.user_input_email);
		IntentFilter filterIntent = new IntentFilter(Constants.BROAD_SENDMAIL_RIGHTBTN_CLICK);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);
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

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == Constants.BROAD_SENDMAIL_RIGHTBTN_CLICK) {
				finish();
			}
		}
	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.ok_btn) {
			String emailStr = phoneInputView.getInputText().getText().toString();
			if (TextUtils.isEmpty(emailStr)) {
				ToastUtil.show(R.string.user_input_email);
				return;
			}
			if (!CheckMatcherUtils.checkEmail(emailStr.trim())) {
				ToastUtil.show(R.string.email_error);
				return;
			}
			KeyboardUtils.hideKeyboard(v);
			sendEmail(emailStr);
		} else if (v.getId() == R.id.menu_back) {
			finish();
		}
	}

	private void sendEmail(String emailStr) {
		HttpParams httpParams = new HttpParams();
		String mailType = "";
		if (type == SendEmailActivity.TYPE_FORGET_LOGIN_PWD) {
			mailType = Constants.MAIL_TWO_RESET_LOGIN_PWD;
		}
		httpParams.put("mailType", mailType);
		httpParams.put("email", DESUtils.encryptToString(emailStr, Constants.Test));
		OkGo.<BaseRes>post(Constants.USER_SEND_MAIL_NOLOGIN_V2).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
			@Override
			public void onSuc(Response<BaseRes> response) {
				BaseRes baseResponse = response.body();
				if (baseResponse.isSuccess()) {
					ToastUtil.show(R.string.send_email_success);
					SendEmailActivity.startMe(BindMailActivity.this, emailStr.trim(), type);
				}
			}


			@Override
			public void onE(Response<BaseRes> response) {
			}

			@Override
			public void onFail(String msg) {
				super.onFail(msg);
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
	}

}
