package com.coinbene.manbiwang.user.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.manbiwang.model.http.UserInfoResponse;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.SpUtil;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.coinbene.manbiwang.user.login.verify.VerifyResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 登录页面二次验证，短信验证和谷歌验证
 */

public class SecondCheckActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.google_layout)
    View googleLayout;
    @BindView(R2.id.msg_layout)
    View msgLayout;
    @BindView(R2.id.send_msgcode_tv)
    TextView sendMsgCodeBtn;

    @BindView(R2.id.pwd_input)
    EditText msgCodeInput;
    @BindView(R2.id.google_input)
    EditText googleInput;

    private String sendMsgWait, sendMsgAgain;

    private VerifyResult verifyResult;

    public static void startMe(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, SecondCheckActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private String name, pwd, verifyWay, phoneNo, areaCode, regChannel;

    private int count = 59;

    private int send_msg_what = 10;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == send_msg_what) {
                sendMsgCodeBtn.setText(String.format(sendMsgWait, count));
                sendMsgCodeBtn.setTextColor(getResources().getColor(R.color.res_textColor_2));
                sendMsgCodeBtn.setOnClickListener(null);
                count--;
                if (count > 0) {
                    handler.sendEmptyMessageDelayed(send_msg_what, 1000);
                } else {
                    sendMsgCodeBtn.setText(sendMsgAgain);
                    sendMsgCodeBtn.setTextColor(getResources().getColor(R.color.res_blue));
					sendMsgCodeBtn.setOnClickListener(v -> sendMsgCode());
                }
            }
        }
    };


    @Override
    public int initLayout() {
        return R.layout.check_code_layout;
    }

    @Override
    public void initView() {

        mTopBar.setTitle(R.string.title_second_check);

        setSwipeBackEnable(false);
        sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
        sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);
    }

    @Override
    public void setListener() {
        okBtn.setOnClickListener(v -> {
            if (verifyWay.equals(Constants.CODE_GOOGLE_CHECK_TYPE)) {
                String googleCode = googleInput.getText().toString();
                if (TextUtils.isEmpty(googleCode)) {
                    ToastUtil.show(R.string.please_input_google_code);
                    return;
                }
                KeyboardUtils.hideKeyboard(v);
                doLogin(googleCode);
            } else if (verifyWay.equals(Constants.CODE_PHONE_MSG_CHECK_TYPE)) {
                String codeMsg = msgCodeInput.getText().toString();
                if (TextUtils.isEmpty(codeMsg)) {
                    ToastUtil.show(R.string.please_input_msg_code);
                    return;
                }
                KeyboardUtils.hideKeyboard(v);
                doLogin(codeMsg);
            }
        });


        sendMsgCodeBtn.setOnClickListener(v -> sendMsgCode());

        googleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputStr = s.toString();
                if (!TextUtils.isEmpty(inputStr)) {
                    if (inputStr.length() == 6) {
                        KeyboardUtils.hideKeyboard(googleInput);
                        doLogin(inputStr);
                    }
                }
            }
        });

        msgCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputStr = s.toString();
                if (!TextUtils.isEmpty(inputStr)) {
                    if (inputStr.length() == 6) {
                        KeyboardUtils.hideKeyboard(msgCodeInput);
                        doLogin(inputStr);
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            finish();
        }
        name = bundle.getString("name");
        pwd = bundle.getString("passwd");
        phoneNo = bundle.getString("phoneNo");
        areaCode = bundle.getString("areaCode");
        verifyWay = bundle.getString("verifyWay");
        regChannel = bundle.getString("regChannel");
        verifyResult = (VerifyResult) bundle.getSerializable("verifyResult");


        if (verifyWay.equals(Constants.CODE_GOOGLE_CHECK_TYPE)) {
            googleLayout.setVisibility(View.VISIBLE);
            msgLayout.setVisibility(View.GONE);
        } else if (verifyWay.equals(Constants.CODE_PHONE_MSG_CHECK_TYPE)) {
            googleLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean needLock() {
        return false;
    }


    private void doLogin(String code) {

        String cid = SpUtil.get(this, "ClientId", "");

        HttpParams httpParams = new HttpParams();
        httpParams.put("passwd", MD5Util.MD5(pwd));
        httpParams.put("loginId", DESUtils.encryptToString(name, Constants.Test));
        httpParams.put("verifyCode", code);

        if (!TextUtils.isEmpty(cid)) {
            httpParams.put("cid", cid);
        }

        httpParams.put("sessionId", verifyResult.getSessionid());
        httpParams.put("sig", verifyResult.getSig());
        httpParams.put("token", verifyResult.getNc_token());
        httpParams.put("scene", verifyResult.getScene());
        httpParams.put("appkey", verifyResult.getAppkey());

        OkGo.<UserInfoResponse>post(Constants.USER_LOGIN_V3).tag(this).params(httpParams).execute(new DialogCallback<UserInfoResponse>(SecondCheckActivity.this) {
            @Override
            public void onSuc(Response<UserInfoResponse> response) {
                UserInfoResponse userLoginResponse = response.body();
                SpUtil.put(SecondCheckActivity.this, SpUtil.PRE_USER_NAME, name);
                UserInfoController.getInstance().regisiterUser(userLoginResponse);
                UserInfoController.getInstance().setLock(false);
                ToastUtil.show(R.string.login_success_toast);

                KeyboardUtils.hideKeyboard(okBtn);

                setResult(RESULT_OK);

                finish();
            }

            @Override
            public void onE(Response<UserInfoResponse> response) {

            }
        });
    }

    private void sendMsgCode() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("phone", DESUtils.encryptToString(phoneNo, Constants.Test));
        httpParams.put("type", Constants.CODE_NINE_SENCD_CHECK);
        OkGo.<BaseRes>post(Constants.USER_SENDSMS_NOLOGIN_V2).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(SecondCheckActivity.this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.send_msg_code_success);
                count = 59;
                handler.sendEmptyMessage(send_msg_what);
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(null);
            handler.removeCallbacksAndMessages(null);
        }
    }
}
