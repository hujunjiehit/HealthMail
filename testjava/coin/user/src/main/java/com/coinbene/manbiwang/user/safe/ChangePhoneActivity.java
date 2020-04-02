package com.coinbene.manbiwang.user.safe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.coinbene.common.Constants;
import com.coinbene.common.SelectCountryActivity;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.manbiwang.service.RouteHub;
import com.coinbene.manbiwang.service.ServiceRepo;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/*
 *
 * create by ding
 *
 * 换绑手机号
 *
 * */

public class ChangePhoneActivity extends CoinbeneBaseActivity {

	@BindView(R2.id.menu_back)
	RelativeLayout menuBack;

	@BindView(R2.id.oldPhone_text)
	TextView oldPhoneText;

	@BindView(R2.id.oldPhone_verification_Code)
	EditText oldPhoneVerificationCode;

	@BindView(R2.id.oldSend_text)
	TextView oldSendText;

	@BindView(R2.id.newPhone_select_country)
	TextView newPhoneSelectCountry;

	@BindView(R2.id.newPhone_editText)
	EditTextOneIcon newPhoneEditText;

	@BindView(R2.id.newPhone_verification_Code)
	EditText newPhoneVerificationCode;

	@BindView(R2.id.newSend_text)
	TextView newSendText;

	@BindView(R2.id.newPhone_submit)
	Button newPhoneSubmit;
	@BindView(R2.id.oldPhone_select_country)
	TextView oldPhoneSelectCountry;

	private String sendMsgWait, sendMsgAgain;
	private int oldTime;
	private int newTime;
	//    旧手机号
	private String phone;
	//    旧区号
	private String areaCode = "86";

	//默认新区号为86
	private String countryArea = "86";

	public static void startActivity(Context context){
		context.startActivity(new Intent(context,ChangePhoneActivity.class));
	}


	@Override
	public int initLayout() {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		return R.layout.settings_activity_change_phone;
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//        离开页面必须清除请求与handler
		OkGo.getInstance().cancelTag(this);

		if (handler != null) {
			handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(null);
		}

	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SelectCountryActivity.REQUEST_CODE && data != null) {
			countryArea = data.getStringExtra("countryArea");
			if (!TextUtils.isEmpty(countryArea)) {
				newPhoneSelectCountry.setText("+" + countryArea);
			}
		}
	}


	/**
	 * 初始化控件
	 */
	private void init() {
		if (!TextUtils.isEmpty(UserInfoController.getInstance().getUserInfo().areaCode)) {
			areaCode = UserInfoController.getInstance().getUserInfo().areaCode;
			countryArea = areaCode;
		}

		sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
		sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);
		phone = UserInfoController.getInstance().getUserInfo().phone;
		oldPhoneText.setText(phone);
		oldPhoneSelectCountry.setText(new StringBuilder().append("+").append(areaCode).toString());
		newPhoneSelectCountry.setText(new StringBuilder().append("+").append(areaCode).toString());
		newPhoneEditText.getInputText().setInputType(InputType.TYPE_CLASS_NUMBER);
		newPhoneEditText.setHintText(getString(R.string.please_enter_newPhone));
		initOnClickListener();
	}

	/**
	 * 初始化点击事件监听
	 */
	private void initOnClickListener() {
		menuBack.setOnClickListener(this);
		oldSendText.setOnClickListener(this);
		newPhoneSelectCountry.setOnClickListener(this);
		newSendText.setOnClickListener(this);
		newPhoneSubmit.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.menu_back) {
			finish();
		} else if (id == R.id.newPhone_submit) {
			//                提交
			submitPhoneNumber();
		} else if (id == R.id.newSend_text) {
			//                新手机号发送验证码
			if (phone.equals(newPhoneEditText.getInputStr())) {
				ToastUtil.show(R.string.phone_num_agreement);
				return;
			}
			sendVerificationCode(newPhoneEditText.getInputStr(), Constants.CODE_MODIFY_NEW_PHONE);


		} else if (id == R.id.oldSend_text) {
			//                旧手机号发送验证码
			sendVerificationCode(Constants.CODE_TWO_MODIFY_PHONE);
		} else if (id == R.id.newPhone_select_country) {
			//                新手机号选择国家
			SelectCountryActivity.startMe(this, SelectCountryActivity.REQUEST_CODE);
		}
	}


	/**
	 * 提交手机号
	 */
	private void submitPhoneNumber() {
		if (checkPhoneNumber()) {
			//            参数无误提交手机号
			HttpParams httpParams = new HttpParams();
			httpParams.put("verifyCodeOld", oldPhoneVerificationCode.getText().toString());
			httpParams.put("areaCodeNew", countryArea);
			httpParams.put("phoneNew", newPhoneEditText.getInputStr().trim());
			httpParams.put("verifyCodeNew", newPhoneVerificationCode.getText().toString());

			OkGo.<BaseRes>post(Constants.UPDATE_PHONE_NUM).params(httpParams).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {

				@Override
				public void onSuc(Response<BaseRes> response) {
					SpUtil.put(ChangePhoneActivity.this, SpUtil.PRE_USER_NAME, newPhoneEditText.getInputStr().trim());
					ServiceRepo.getUserService().logOut();
					ARouter.getInstance().build(RouteHub.User.loginActivity)
							.withBoolean("forceQuit", true)
							.navigation(ChangePhoneActivity.this);
					ToastUtil.show(R.string.reset_phoneNum_success);
					finish();

				}

				@Override
				public void onE(Response<BaseRes> response) {

				}
			});
		}
	}


	/**
	 * 校验换绑手机条件是否成立
	 */
	private boolean checkPhoneNumber() {


		if (TextUtils.isEmpty(oldPhoneVerificationCode.getText().toString().trim())) {
			ToastUtil.show(R.string.user_hint_msg_code);
			return false;
		}

		if (TextUtils.isEmpty(newPhoneEditText.getInputStr().trim())) {

			ToastUtil.show(R.string.user_input_phone);
			return false;
		}
		if (TextUtils.isEmpty(newPhoneVerificationCode.getText().toString().trim())) {
			ToastUtil.show(R.string.user_hint_msg_code);
			return false;
		}

		return true;
	}


	/**
	 * 发送新手机验证码
	 */
	private void sendVerificationCode(String phoneNum, String type) {

		if (TextUtils.isEmpty(phoneNum)) {
			ToastUtil.show(R.string.user_input_phone);
			return;
		}


		if (!CheckMatcherUtils.checkPhoneNumber(phoneNum)) {
			ToastUtil.show(R.string.phone_is_not_right);
			return;
		}


		if (!CheckMatcherUtils.checkPhoneNumber(phoneNum)) {
			ToastUtil.show(R.string.phone_is_not_right);
			return;
		}

		newSendText.setEnabled(false);

		HttpParams httpParams = new HttpParams();
		httpParams.put("phone", DESUtils.encryptToString(phoneNum, Constants.Test));
		httpParams.put("type", type);
		httpParams.put("areaCode", countryArea);


		OkGo.<BaseRes>post(Constants.USER_SENDSMS_NOLOGIN_V2).params(httpParams).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {

			@Override
			public void onSuc(Response<BaseRes> response) {
				ToastUtil.show(R.string.send_msg_code_success);
				newTime = 59;
				handler.sendEmptyMessageDelayed(R.id.newSend_text, 1000);
			}

			@Override
			public void onE(Response<BaseRes> response) {
				newSendText.setEnabled(true);
			}

		});
	}


	/*
	 * 发送旧手机验证码
	 * */
	private void sendVerificationCode(String type) {


		oldSendText.setEnabled(false);

		OkGo.<BaseRes>post(Constants.OLD_VERIFY_CODE).params("type", type).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
			@Override
			public void onSuc(Response<BaseRes> response) {

				ToastUtil.show(R.string.send_msg_code_success);
				oldTime = 59;
				handler.sendEmptyMessageDelayed(R.id.oldSend_text, 1000);

			}

			@Override
			public void onE(Response<BaseRes> response) {
				oldSendText.setEnabled(true);
			}
		});

	}

	/**
	 * 发送验证码倒计时
	 */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == R.id.newSend_text) {
				newSendText.setText(String.format(sendMsgWait, newTime));
				newSendText.setTextColor(getResources().getColor(R.color.res_textColor_2));
				newTime--;
				if (newTime > 0) {
					handler.sendEmptyMessageDelayed(R.id.newSend_text, 1000);
				} else {
					newSendText.setEnabled(true);
					newSendText.setText(sendMsgAgain);
					newSendText.setTextColor(getResources().getColor(R.color.res_blue));
					newTime = 59;
				}
			} else if (msg.what == R.id.oldSend_text) {
				oldSendText.setText(String.format(sendMsgWait, oldTime));
				oldSendText.setTextColor(getResources().getColor(R.color.res_textColor_2));
				oldTime--;
				if (oldTime > 0) {
					handler.sendEmptyMessageDelayed(R.id.oldSend_text, 1000);
				} else {
					oldSendText.setEnabled(true);
					oldSendText.setText(sendMsgAgain);
					oldSendText.setTextColor(getResources().getColor(R.color.res_blue));
					oldTime = 59;
				}

			}


		}
	};
}
