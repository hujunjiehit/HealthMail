package com.coinbene.manbiwang.user.safe;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.ToastUtil;

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
 * 验证码首选项的下一个页面，短信验证和谷歌验证
 */

public class CheckCodeActivity extends CoinbeneBaseActivity {
    public static final int CODE_RESULT = 10;
    public static final int TYPE_GOOGLE = 1;
    public static final int TYPE_MSG = 2;

    private Unbinder mUnbinder;

    @BindView(R2.id.ok_btn)
    TextView okBtn;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.google_layout)
    View googleLayout;
    @BindView(R2.id.msg_layout)
    View msgLayout;
    private int type;
    @BindView(R2.id.send_msgcode_tv)
    TextView sendMsgCodeBtn;

    @BindView(R2.id.pwd_input)
    EditText msgCodeInput;
    @BindView(R2.id.google_input)
    EditText googleInput;

    private String sendMsgWait, sendMsgAgain;

    public static void startMe(Context context, int type) {
        Intent intent = new Intent(context, CheckCodeActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void startMeForResult(Activity activity, int code) {
        Intent intent = new Intent(activity, CheckCodeActivity.class);
        activity.startActivityForResult(intent, code);
    }

    @Override
    public int initLayout() {
        return R.layout.check_code_layout;
    }

    @Override
    public void initView() {
        okBtn.setOnClickListener(this);
        titleView.setText(getText(R.string.setting_check_selected));
        backView.setOnClickListener(this);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", TYPE_GOOGLE);
        }
        if (type == TYPE_GOOGLE) {
            googleLayout.setVisibility(View.VISIBLE);
            msgLayout.setVisibility(View.GONE);
        } else {
            googleLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.VISIBLE);
        }

        sendMsgCodeBtn.setOnClickListener(this);
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
        if (v.getId() == R.id.menu_back) {
            finish();
        } else if (v.getId() == R.id.ok_btn) {
            if (type == TYPE_GOOGLE) {
                String googleCode = googleInput.getText().toString();
                if (TextUtils.isEmpty(googleCode)) {
                    ToastUtil.show(R.string.please_input_google_code);
                    return;
                }
                modifyFirstCheck(googleCode, true);
            } else {
                String codeMsg = msgCodeInput.getText().toString();
                if (TextUtils.isEmpty(codeMsg)) {
                    ToastUtil.show(R.string.please_input_msg_code);
                    return;
                }
                modifyFirstCheck(codeMsg, false);
            }
        } else if (v.getId() == R.id.send_msgcode_tv) {
            sendMsgCode();
        }
    }

    private void modifyFirstCheck(String code, boolean isGoogleCode) {

        HttpParams httpParams = new HttpParams();
        httpParams.put("verifyCode", code);
        OkGo.<BaseRes>post(Constants.USER_TOGGLE_VERIFYWAY).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.set_first_check_success);
                finish();
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
        httpParams.put("type", Constants.CODE_FIVE_CHANGE_SELECT);
        OkGo.<BaseRes>post(Constants.USER_SENDSMS).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.send_msg_code_success);
                count = 59;
                handler.sendEmptyMessage(send_msg_what);
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
                sendMsgCodeBtn.setText(String.format(sendMsgWait, count));
                sendMsgCodeBtn.setTextColor(getResources().getColor(R.color.res_textColor_2));
                sendMsgCodeBtn.setOnClickListener(null);
                count--;
                if (count > 0) {
                    handler.sendEmptyMessageDelayed(send_msg_what, 1000);
                } else {
                    sendMsgCodeBtn.setText(sendMsgAgain);
                    sendMsgCodeBtn.setTextColor(getResources().getColor(R.color.res_blue));
                    sendMsgCodeBtn.setOnClickListener(CheckCodeActivity.this);
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
        if (handler != null) {
            handler.removeCallbacks(null);
            handler.removeCallbacksAndMessages(null);
        }

        OkGo.getInstance().cancelTag(this);
    }
}
