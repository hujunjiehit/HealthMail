package com.coinbene.manbiwang.user.balance;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.MD5Util;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.common.widget.EditTextTwoIcon;
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
 * 设置资金密码，谷歌验证或者短信验证
 */

public class SetCapPwdActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 11;

    public static final int CODE_GOOGLE = 1;
    public static final int CODE_PHONE_MSG = 2;

    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;

    @BindView(R2.id.google_code_input)
    EditTextOneIcon googleCodeInput;

    @BindView(R2.id.cap_code_input)
    EditTextTwoIcon capCodeInput;

    @BindView(R2.id.google_layout)
    View googleLayout;
    @BindView(R2.id.phone_msg_layout)
    View phoneMsgLayout;

    @BindView(R2.id.msg_code_input)
    EditText msgCodeInput;
    @BindView(R2.id.send_msgcode_tv)
    TextView sendMsgTv;

    private String sendMsgWait, sendMsgAgain;

    public static void startMe(Context context) {
        Intent intent = new Intent(context, SetCapPwdActivity.class);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, SetCapPwdActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public int initLayout() {
        return R.layout.setting_set_cap_pwd;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.set_set_cap_pwd_title));
        backView.setOnClickListener(this);

        UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
        if (userTable == null) {
            finish();
            return;
        }

        googleLayout.setVisibility(View.VISIBLE);
        phoneMsgLayout.setVisibility(View.GONE);
        //sendMsgTv.setOnClickListener(this);

        googleCodeInput.setFirstRightIcon(R.drawable.icon_close);
        googleCodeInput.getInputText().setHint(R.string.google_auth_code_hint);
        googleCodeInput.setInputTypeNumer();

        capCodeInput.setFirstRightIcon(R.drawable.icon_close);
        capCodeInput.setSecondRightEye_Num();
        capCodeInput.getInputText().setHint(R.string.set_cap_num_hint);

        sendMsgWait = this.getResources().getString(R.string.send_msg_code_wait);
        sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);
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
    public void onClick(View v) {
        if (v.getId() == R.id.ok_btn) {
            String code;
            String capCode;
            if (googleLayout.getVisibility() == View.VISIBLE) {
                code = googleCodeInput.getInputText().getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(R.string.please_input_google_code);
                    return;
                }
            } else {
                code = msgCodeInput.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    ToastUtil.show(R.string.please_input_msg_code);
                    return;
                }
            }
            capCode = capCodeInput.getInputText().getText().toString();
            if (TextUtils.isEmpty(capCode)) {
                ToastUtil.show(R.string.capital_pwd_is_empty);
                return;
            }
            if (capCode.length() > 8 || capCode.length() < 6) {
                ToastUtil.show(R.string.please_set_cap_num_hint);
                return;
            }

            setCapPwd(code, capCode);
        } else if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.send_msgcode_tv) {
            sendMsgCode();
        }
    }

    /**
     * 设置资金密码
     */
    private void setCapPwd(String code, String capPwd) {
        HttpParams httpParams = new HttpParams();
        httpParams.put("googleCode", code);
        httpParams.put("pin", MD5Util.MD5(capPwd));
        OkGo.<BaseRes>post(Constants.USER_SET_PIN).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                handler.sendEmptyMessageDelayed(send_msg_what_dialog, 1000);
            }

            @Override
            public void onE(Response<BaseRes> response) {

            }

            @Override
            public void onFail(String msg) {

            }
        });

    }

    private ProgressDialog progress;

    private void showDialog() {
        progress = new ProgressDialog(SetCapPwdActivity.this);
        progress.setMessage(this.getResources().getString(R.string.dialog_loading));
        progress.show();
    }

    private void dismissDialog() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    /**
     * 发送短信验证码
     * 登录成功后，才能设置资金密码；
     */
    private void sendMsgCode() {

        HttpParams httpParams = new HttpParams();
        UserInfoTable user = UserInfoController.getInstance().getUserInfo();
        httpParams.put("type", Constants.CODE_SEVEN_SET_CAP_PWD);
        OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes t = response.body();
                if (t.isSuccess()) {
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
    private int send_msg_what_dialog = 11;
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
                    sendMsgTv.setOnClickListener(SetCapPwdActivity.this);
                }
            } else if (msg.what == send_msg_what_dialog) {
                dismissDialog();
                ToastUtil.show(R.string.set_cap_pwd_success);
                setResult(Activity.RESULT_OK);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(null);
            handler.removeCallbacksAndMessages(null);
        }
    }

}
