package com.coinbene.manbiwang.user.login;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.coinbene.common.Constants;
import com.coinbene.common.PostPointHandler;
import com.coinbene.common.SelectCountryActivity;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.router.UIBusService;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.LanguageHelper;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.SiteHelper;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.utils.UrlUtil;
import com.coinbene.common.widget.ContentSpan;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.EditTextTwoIcon;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.manbiwang.service.ServiceRepo;
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
 */

public class RegisterPhoneActivity extends CoinbeneBaseActivity {
	public static final int REQUEST_CODE = 101;

	@BindView(R2.id.register_btn)
	TextView registerBtn;
	@BindView(R2.id.menu_right_tv)
	TextView menuRegisterBtn;
	@BindView(R2.id.menu_back)
	View backView;
	@BindView(R2.id.menu_title_tv)
	TextView titleView;

	@BindView(R2.id.phone_input_view)
	EditTextOneIcon phoneInputView;
	@BindView(R2.id.pwd_view)
	EditTextTwoIcon pwdView;

	@BindView(R2.id.send_msgcode_tv)
	TextView sendMsgTv;
	private String sendMsgWait, sendMsgAgain;

	@BindView(R2.id.country_layout)
	View countryLayout;
	@BindView(R2.id.left_phone_tv)
	TextView left_phone_tv;

	@BindView(R2.id.select_country_tv)
	TextView selectedCountryTv;
	@BindView(R2.id.msg_code_input)
	EditText msgCodeInput;
	@BindView(R2.id.service_link_tv_txt)
	TextView serviceLinkTvNew;
	@BindView(R2.id.invite_code_input)
	EditText inviteCodeInput;

	private String country;
	private String code;
	private String countryAreaCode = Constants.CODE_CHINA_PHONE;

	private VerifyDialog mVerifyDialog;

	public static void startMeForResult(Activity activity) {
		Intent intent = new Intent(activity, RegisterPhoneActivity.class);
		activity.startActivityForResult(intent, REQUEST_CODE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


	}

	private void serviceLink() {
		int defaultColor = getResources().getColor(R.color.res_blue);
		serviceLinkTvNew.setMovementMethod(LinkMovementMethod.getInstance());
		serviceLinkTvNew.setText("");
		SpannableString spanString = new SpannableString(getString(R.string.user_register_tips_sencond));
		ContentSpan span = new ContentSpan(textClickListener, defaultColor);
		spanString.setSpan(span, 0, spanString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		serviceLinkTvNew.append(getString(R.string.user_register_tips_first));
		boolean isChinese = LanguageHelper.isChinese(this);
		if (!isChinese) {
			serviceLinkTvNew.append(" ");
		}
		serviceLinkTvNew.append(spanString);
	}


	@Override
	public int initLayout() {
		return R.layout.user_phone_register;
	}

	@Override
	public void initView() {

		String site = SpUtil.get(this, SpUtil.PRE_SITE_SELECTED, "");
		code = Constants.CODE_CHINA_PHONE;
		if ("MAIN".equals(site)) {
			country = getString(R.string.ch_str);
			code = "86";

		} else if ("KO".equals(site)) {
			country = getString(R.string.country_Korea_key);
			code = "82";

		} else if ("BR".equals(site)) {
			country = getString(R.string.country_Brazil);
			code = "55";
		} else if (SiteHelper.isVietSite()) {
			country = getString(R.string.country_Vietnam_key);
			code = "84";
		}

		countryAreaCode = code;

		left_phone_tv.setText(country);
		selectedCountryTv.setText("+" + code);

		registerBtn.setOnClickListener(this);
		menuRegisterBtn.setOnClickListener(this);
		sendMsgTv.setOnClickListener(this);
//        countryLayout.setOnClickListener(this);
		left_phone_tv.setOnClickListener(this);
		selectedCountryTv.setOnClickListener(this);
		titleView.setText(getText(R.string.user_menu_register_title));
		menuRegisterBtn.setTextColor(getResources().getColor(R.color.res_blue));
		menuRegisterBtn.setText(getText(R.string.user_register_phone_right_btn));
		backView.setOnClickListener(this);

		phoneInputView.setFirstRightIcon(R.drawable.icon_close);
		phoneInputView.getInputText().setHint(R.string.user_input_phone_hint);
		phoneInputView.setInputTypeNumer();

		pwdView.setSecondRightPwdEye();
		pwdView.getInputText().setHint(R.string.user_input_pwd);

		IntentFilter filterIntent = new IntentFilter(Constants.BROAD_SEND_REGISTER_EMAIL);
		LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);

		sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
		sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);

		serviceLink();

		mVerifyDialog = new VerifyDialog(this, "nc_register_h5");
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

	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction() == Constants.BROAD_SEND_REGISTER_EMAIL) {
				finish();
			}
		}
	};

	private String phoneNum;
	private String country_index;
	private String pwdStr;
	private String msgCode;
	private String inviteCode;

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.register_btn) {
			PostPointHandler.postClickData(PostPointHandler.register_register_btn);

			if (TextUtils.isEmpty(countryAreaCode)) {
				ToastUtil.show(R.string.country_select_tips);
				return;
			}
			phoneNum = phoneInputView.getInputStr();
			if (TextUtils.isEmpty(phoneNum)) {
				ToastUtil.show(R.string.user_input_phone);
				return;
			}
			if (!CheckMatcherUtils.checkPhoneNumber(phoneNum)) {
				ToastUtil.show(R.string.phone_is_not_right);
				return;
			}
			msgCode = msgCodeInput.getText().toString();
			if (TextUtils.isEmpty(msgCode)) {
				ToastUtil.show(R.string.user_hint_msg_code);
				return;
			}

			//邀请码
			inviteCode = inviteCodeInput.getText().toString();


			pwdStr = pwdView.getInputStr();
			if (TextUtils.isEmpty(pwdStr)) {
				ToastUtil.show(R.string.pwd_is_empty);
				return;
			}
			boolean isRight = CheckMatcherUtils.checkPwd6_20(pwdStr);
			if (!isRight) {
				ToastUtil.show(R.string.pwd_check);
				return;
			}
			KeyboardUtils.hideKeyboard(v);

			mVerifyDialog.showWithCallback(result -> registerPhone(result));

		} else if (v.getId() == R.id.menu_right_tv) {

			RegisterEmailActivity.startMe(v.getContext());

			PostPointHandler.postClickData(PostPointHandler.register_email_register);
		} else if (v.getId() == R.id.menu_back) {
			finish();
		} else if (v.getId() == R.id.send_msgcode_tv) {
			phoneNum = phoneInputView.getInputStr();
			if (TextUtils.isEmpty(phoneNum)) {
				ToastUtil.show(R.string.user_input_phone);
				return;
			}
			boolean isPhone = CheckMatcherUtils.checkPhoneNumber(phoneNum);
			if (!isPhone) {
				ToastUtil.show(R.string.phone_is_not_right);
				return;
			}
			KeyboardUtils.hideKeyboard(v);
			sendMsgCode();
		} else if (v.getId() == R.id.country_layout) {
//            SelectCountryActivity.startMe_default(RegisterPhoneActivity.this, country_index, SelectCountryActivity.REQUEST_CODE);
		} else if (v.getId() == R.id.left_phone_tv || v.getId() == R.id.select_country_tv) {
			SelectCountryActivity.startMe(RegisterPhoneActivity.this, SelectCountryActivity.REQUEST_CODE);
		}
	}

	private void registerPhone(VerifyResult verifyResult) {
		String cid = SpUtil.get(this, "ClientId", "");
		HttpParams httpParams = new HttpParams();
		httpParams.put("registerChannel", CBRepository.getChannelValue());
		httpParams.put("loginId", DESUtils.encryptToString(phoneNum, Constants.Test));
		httpParams.put("passwd", MD5Util.MD5(pwdStr));
		httpParams.put("areaCode", countryAreaCode);
		httpParams.put("verifyCode", msgCode);
		httpParams.put("registerMethod", "2");
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

		OkGo.<UserInfoResponse>post(Constants.USER_REGISTER_V3).tag(this).params(httpParams).execute(new DialogCallback<UserInfoResponse>(RegisterPhoneActivity.this) {
			@Override
			public void onSuc(Response<UserInfoResponse> response) {
//                hideDialog();
				UserInfoResponse userLoginResponse = response.body();
				PostPointHandler.postClickData(PostPointHandler.register_register_success);

				if (userLoginResponse.isSuccess()) {
					ToastUtil.show(R.string.register_success);
					handler.removeCallbacksAndMessages(null);//将发送短信的倒计时暂停
					UserInfoController.getInstance().regisiterUser(userLoginResponse);

					Intent intent = new Intent(Constants.BROAD_SEND_REGISTER_EMAIL);
					LocalBroadcastManager.getInstance(RegisterPhoneActivity.this).sendBroadcast(intent);

					SpUtil.setNeedNewUserGuide(true);

					ServiceRepo.getAppService().onUserLoginSuccess(Constants.TAB_INDEX_DEFAULT);

					finish();
				}
			}

			@Override
			public void onE(Response<UserInfoResponse> response) {
			}
		});
	}


	private void sendMsgCode() {

		HttpParams httpParams = new HttpParams();
		httpParams.put("phone", DESUtils.encryptToString(phoneNum, Constants.Test));
		httpParams.put("type", Constants.CODE_ONE_REGISTER);
		httpParams.put("areaCode", countryAreaCode);


		OkGo.<BaseRes>post(Constants.USER_SENDSMS_NOLOGIN_V2).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(RegisterPhoneActivity.this) {
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
				sendMsgTv.setText(String.format(sendMsgWait, count));
				sendMsgTv.setTextColor(getResources().getColor(R.color.res_textColor_2));
				sendMsgTv.setOnClickListener(null);
				count--;
				if (count > 0) {
					handler.sendEmptyMessageDelayed(send_msg_what, 1000);
				} else {
					sendMsgTv.setText(sendMsgAgain);
					sendMsgTv.setTextColor(getResources().getColor(R.color.res_blue));
					sendMsgTv.setOnClickListener(RegisterPhoneActivity.this);
				}
			}
		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_CANCELED) {
			return;
		}
		if (requestCode == SelectCountryActivity.REQUEST_CODE) {
			country = data.getStringExtra("countryName");
			code = data.getStringExtra("countryArea");
			if (!code.equals(countryAreaCode)) {
				phoneInputView.setInputText("");
				pwdView.setInputText("");
				msgCodeInput.setText("");
			}
			countryAreaCode = code.trim();
			left_phone_tv.setText(country);
			selectedCountryTv.setText("+" + code.trim());
			selectedCountryTv.setTextColor(selectedCountryTv.getContext().getResources().getColor(R.color.res_textColor_1));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		PostPointHandler.postBrowerData(PostPointHandler.register_brower);
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
		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(null);
		}
		LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
	}
}
