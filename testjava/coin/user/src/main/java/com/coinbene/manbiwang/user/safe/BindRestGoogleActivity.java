package com.coinbene.manbiwang.user.safe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.Unbinder;

/**
 * Created by mengxiangdong on 2017/11/28.
 * 谷歌绑定，重置
 */

public class BindRestGoogleActivity extends CoinbeneBaseActivity {
    private Unbinder mUnbinder;

    @BindView(R2.id.resend_btn)
    TextView resendBtn;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.send_context_tv)
    TextView sendContentTv;
    @BindView(R2.id.top_tips)
    TextView topTipsView;


    private boolean isBind = false;
    private String email, userId;
    private String sendMsgWait, sendMsgAgain;

    public static void startMe(Context context, boolean bind) {
        Intent intent = new Intent(context, BindRestGoogleActivity.class);
        intent.putExtra("bind", bind);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public int initLayout() {
        return R.layout.settings_user_bind_reset_google_email;
    }

    @Override
    public void initView() {
        resendBtn.setOnClickListener(this);
        backView.setOnClickListener(this);

        isBind = getIntent().getBooleanExtra("bind", true);

        if (isBind) {
            titleView.setText(getText(R.string.bing_google_title));
        } else {
            titleView.setText(getText(R.string.rest_google_title));
        }
//        sendContentTv.setText(R.string.bing_google_email_sended_label);

        UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
        if (userTable == null || TextUtils.isEmpty(userTable.email)) {
            ToastUtil.show(R.string.email_is_empay);
            finish();
            return;
        }
        sendMsgWait = this.getResources().getString(R.string.send_email_wait);
        sendMsgAgain = this.getResources().getString(R.string.send_msg_code_again);

        email = userTable.email;
        userId = String.valueOf(userTable.userId);
        sendEmail();
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

    /*
    * 发送邮箱,绑定或者充值google验证码
     */
    private void sendEmail() {
        HttpParams httpParams = new HttpParams();
        httpParams.put("email", email);
        if (isBind) {
            httpParams.put("mailType", Constants.MAIL_FOUR_BING_GOOGLE);
        } else {
            httpParams.put("mailType", Constants.MAIL_FIVE_RESET_GOOGLE);
        }
        OkGo.<BaseRes>post(Constants.USER_SEND_MAIL).tag(this).params(httpParams).execute(new NewJsonSubCallBack<BaseRes>() {
            @Override
            public void onSuc(Response<BaseRes> response) {
                resendBtn.setVisibility(View.VISIBLE);
                BaseRes entity = response.body();
                if (entity.isSuccess()) {
                    ToastUtil.show( R.string.send_email_success);
                    topTipsView.setVisibility(View.VISIBLE);
                    if (isBind) {
                        sendContentTv.setText(R.string.bing_google_email_sended_label);
                    } else {
                        sendContentTv.setText(R.string.rest_google_email_sended_label);
                    }
                    count = 59;
                    handler.sendEmptyMessage(send_msg_what);
                }
            }

            @Override
            public void onE(Response<BaseRes> response) {
                if(BindRestGoogleActivity.this.isDestroyed()){
                    return;
                }
                sendContentTv.setText(R.string.send_email_fail);
                resendBtn.setText(sendMsgAgain);
                resendBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFail(String msg) {
                if(BindRestGoogleActivity.this.isDestroyed()){
                    return;
                }
                sendContentTv.setText(R.string.send_email_fail);
                resendBtn.setText(sendMsgAgain);
                resendBtn.setVisibility(View.VISIBLE);
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
                resendBtn.setText(String.format(sendMsgWait, count));
                resendBtn.setOnClickListener(null);
                resendBtn.setClickable(false);
                count--;
                if (count > 0) {
                    handler.sendEmptyMessageDelayed(send_msg_what, 1000);
                } else {
                    resendBtn.setClickable(true);
                    resendBtn.setText(sendMsgAgain);
                    resendBtn.setVisibility(View.VISIBLE);
                    resendBtn.setOnClickListener(BindRestGoogleActivity.this);
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resend_btn) {
            sendEmail();
        } else if (v.getId() == R.id.menu_back) {
            finish();
        }
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
