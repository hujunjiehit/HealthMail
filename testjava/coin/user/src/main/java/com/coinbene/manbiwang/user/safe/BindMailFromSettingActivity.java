package com.coinbene.manbiwang.user.safe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.coinbene.common.Constants;

import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.context.CBRepository;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.CheckMatcherUtils;
import com.coinbene.common.utils.KeyboardUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.common.widget.EditTextOneIcon;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 绑定邮箱，从设置页面过来
 */

public class BindMailFromSettingActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 12;

    private Unbinder mUnbinder;

    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.mail_input)
    EditTextOneIcon phoneInputView;
    private int resultCode;
    @BindView(R2.id.msg_code_input)
    EditText msgCodeInput;
    @BindView(R2.id.send_msgcode_tv)
    TextView sendMsgTv;

    private String msgCode;
    private String sendMsgWait, sendMsgAgain;

    public static void startMeForResult(Activity activity, int type, int resultCode) {
        Intent intent = new Intent(activity, BindMailFromSettingActivity.class);
        intent.putExtra("type", type);
        activity.startActivityForResult(intent, resultCode);
    }

    @Override
    public int initLayout() {
        return R.layout.settings_setting_bind_mail_new;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        Intent intent = getIntent();
        resultCode = intent.getIntExtra("resultcode", 0);
        titleView.setText(getText(R.string.bing_mail_title));

        backView.setOnClickListener(this);
        sendMsgTv.setOnClickListener(this);

        phoneInputView.setFirstRightIcon(R.drawable.icon_close);
        phoneInputView.getInputText().setHint(R.string.user_input_email);
        IntentFilter filterIntent = new IntentFilter(Constants.BROAD_SENDMAIL_RIGHTBTN_CLICK);
        LocalBroadcastManager.getInstance(CBRepository.getContext()).registerReceiver(broadcastReceiver, filterIntent);

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
            msgCode = msgCodeInput.getText().toString();
            if (TextUtils.isEmpty(msgCode)) {
                ToastUtil.show(R.string.user_hint_msg_code);
                return;
            }
            String emailStr = phoneInputView.getInputText().getText().toString().trim();
            if (TextUtils.isEmpty(emailStr)) {
                ToastUtil.show(R.string.user_input_email);
                return;
            }
            if (!CheckMatcherUtils.checkEmail(emailStr)) {
                ToastUtil.show(R.string.email_error);
                return;
            }

            KeyboardUtils.hideKeyboard(v);
            sendEmail(emailStr);
        } else if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.send_msgcode_tv) {
            sendMsgCode();
        }
    }

    private void sendMsgCode() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("type", Constants.CODE_BIND_MAIL);
        OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(BindMailFromSettingActivity.this) {
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
    private int code_what = 100;
    private ProgressDialog progress;

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
                    sendMsgTv.setOnClickListener(BindMailFromSettingActivity.this);
                }
            }
        }
    };


    private void sendEmail(String emailStr) {
        UserInfoTable userInfo = UserInfoController.getInstance().getUserInfo();
        HttpParams httpParams = new HttpParams();
        httpParams.put("email", emailStr);
        httpParams.put("verifyCode", msgCode);
        if (userInfo != null) {
            httpParams.put("userId", userInfo.userId);
        }
        OkGo.<BaseRes>post(Constants.USER_BIND_EMAIL_CONFIRM).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(BindMailFromSettingActivity.this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes baseResponse = response.body();
                if (baseResponse.isSuccess()) {
                    ToastUtil.show(R.string.send_email_success);
                    SendEmailActivity.startMe(BindMailFromSettingActivity.this, emailStr.trim(), msgCode, SendEmailActivity.TYPE_MAIL_BIND);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        LocalBroadcastManager.getInstance(CBRepository.getContext()).unregisterReceiver(broadcastReceiver);
    }

}
