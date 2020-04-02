package com.coinbene.manbiwang.user.safe;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.DialogCallback;
import com.coinbene.common.utils.DESUtils;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.model.base.BaseRes;
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
 * 邮箱注册(来自注册页面),认证邮箱(来自激活),重置资金密码(忘记资金密码)，密码找回,绑定邮箱
 */

public class SendEmailActivity extends CoinbeneBaseActivity {
    public static final int TYPE_MAIL_REGISTER = 1;//来自邮箱的注册,进行认证页面
    public static final int TYPE_MAIL_ACTIVATE = 2;//激活邮箱--》认证邮箱
    public static final int TYPE_MAIL_BIND = 6;

    public static final int TYPE_RESET_CAP_PWD = 3;
    public static final int TYPE_FORGET_LOGIN_PWD = 4;
    public static final int DEFAULT_COUNT = 59;
    public static final int TYPE_BING_PHONE_NO = 7;//绑定手机

    private Unbinder mUnbinder;

    private int numCount = DEFAULT_COUNT;
    @BindView(R2.id.resend_btn)
    TextView resendBtn;
    @BindView(R2.id.menu_right_tv)
    TextView menuRegisterBtn;
    @BindView(R2.id.menu_back)
    View backView;
    @BindView(R2.id.menu_title_tv)
    TextView titleView;
    @BindView(R2.id.email_label)
    TextView emailTipsLabel;
    private int code = 1;
    private int type;
    private String email;
    private String msgCode;

    public static void startMe(Context context, String email, int type) {
        Intent intent = new Intent(context, SendEmailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("email", email);
        context.startActivity(intent);
    }

    public static void startMe(Context context, String email, String msgCode, int type) {
        Intent intent = new Intent(context, SendEmailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("email", email);
        intent.putExtra("msgCode", msgCode);
        context.startActivity(intent);
    }

    @Override
    public int initLayout() {
        return R.layout.settings_user_send_email;
    }

    @Override
    public void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra("type", TYPE_MAIL_ACTIVATE);
            email = intent.getStringExtra("email");
            msgCode = intent.getStringExtra("msgCode");
        }
        mUnbinder = ButterKnife.bind(this);
        resendBtn.setOnClickListener(this);
        menuRegisterBtn.setOnClickListener(this);

        if (TYPE_RESET_CAP_PWD == type) {//充值资金密码
            titleView.setText(getText(R.string.set_reset_cap_pwd_title));
            menuRegisterBtn.setText(getText(R.string.user_send_email_menu_skip));
            menuRegisterBtn.setVisibility(View.VISIBLE);
        } else if (TYPE_FORGET_LOGIN_PWD == type) {//忘记密码
            titleView.setText(getText(R.string.user_find_pwd_title));
            menuRegisterBtn.setText(R.string.btn_go_login);
            emailTipsLabel.setText(R.string.user_send_register_email_label);
        } else if (TYPE_MAIL_BIND == type) {
            titleView.setText(getText(R.string.bing_mail_title));
            menuRegisterBtn.setVisibility(View.GONE);
            emailTipsLabel.setText(R.string.user_send_email_sended_label);
        } else if (TYPE_MAIL_ACTIVATE == type) {//激活邮箱，认证邮箱
            titleView.setText(getText(R.string.user_menu_send_title));
            menuRegisterBtn.setText(R.string.btn_close);
            emailTipsLabel.setText(R.string.user_send_email_sended_label);
        } else if (TYPE_BING_PHONE_NO == type) {
            titleView.setText(getText(R.string.bing_phone_title));
            menuRegisterBtn.setVisibility(View.GONE);
            emailTipsLabel.setText(R.string.user_send_email_phone_sended_label);
        } else if (TYPE_MAIL_REGISTER == type) {
            titleView.setText(getText(R.string.user_menu_send_title));
            //menuRegisterBtn.setText(R.string.user_send_email_menu_skip);
            menuRegisterBtn.setVisibility(View.GONE);
            emailTipsLabel.setText(R.string.user_send_email_sended_label);
            //这里发送广播，关闭前一个页面
            Intent broadcast_intent = new Intent(Constants.BROAD_SEND_REGISTER_EMAIL);
            LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast_intent);
        }

        backView.setOnClickListener(this);
        resendBtn.setClickable(false);
        if (TYPE_MAIL_BIND == type) {
            resendBtn.setVisibility(View.INVISIBLE);
        } else {
            resendBtn.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(code);
        }
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
    public void onClick(View v) {
        if (v.getId() == R.id.resend_btn) {
            sendEmail();
        } else if (v.getId() == R.id.menu_right_tv) {
            if (TYPE_FORGET_LOGIN_PWD == type) {
                //返回到登录页面
                Intent intent = new Intent(Constants.BROAD_SENDMAIL_RIGHTBTN_CLICK);
                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
            }
            finish();
        } else if (v.getId() == R.id.menu_back) {
            if (TYPE_MAIL_REGISTER == type) {//邮箱注册成功后，返回或者右侧的按钮，都要发送广播，关闭页面
                Intent intent = new Intent(Constants.BROAD_SEND_REGISTER_EMAIL);
                LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(intent);
            }
            finish();
        }
    }

    private void sendEmail() {
        numCount = DEFAULT_COUNT;
        HttpParams httpParams = new HttpParams();
        String mailType = "";
        String urlPath = Constants.USER_SEND_MAIL;
        if (type == TYPE_FORGET_LOGIN_PWD) {
            mailType = Constants.MAIL_TWO_RESET_LOGIN_PWD;
            urlPath = Constants.USER_SEND_MAIL_NOLOGIN_V2;
            httpParams.put("email", DESUtils.encryptToString(email, Constants.Test));
        } else if (type == TYPE_MAIL_ACTIVATE || type == TYPE_MAIL_REGISTER) {
            mailType = Constants.MAIL_ONE_VERI;
            if (type == TYPE_MAIL_REGISTER) {
                urlPath = Constants.USER_SEND_MAIL_NOLOGIN_V2;
                httpParams.put("email", DESUtils.encryptToString(email, Constants.Test));
            }
        } else if (type == TYPE_BING_PHONE_NO) {
            mailType = Constants.MAIL_NINE_BING_PHONE;
        }
        httpParams.put("mailType", mailType);
        if (TextUtils.isEmpty(email)) {
            UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
            if (userTable == null || TextUtils.isEmpty(userTable.token)) {
                ToastUtil.show(R.string.user_info_fail);
                return;
            }
            email = userTable.loginId;
        }
        httpParams.put("loginId", email);
        OkGo.<BaseRes>post(urlPath).tag(this).params(httpParams).execute(new DialogCallback<BaseRes>(SendEmailActivity.this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                BaseRes baseResponse = response.body();
                if (baseResponse.isSuccess()) {
                    ToastUtil.show(R.string.send_email_success);
                    numCount = DEFAULT_COUNT;
                    handler.sendEmptyMessage(code);
                }
//                else {
//                    ToastUtil.show(CodeController.getInstance().getMsg(baseResponse.message));
//                    resendBtn.setClickable(true);
//                    resendBtn.setBackgroundDrawable(SendEmailActivity.this.getResources().getDrawable(R.drawable.btn_item_new_bg));
//                    resendBtn.setText(R.string.user_email_resend_btn);
//                }
            }

            @Override
            public void onE(Response<BaseRes> response) {
                resendBtn.setClickable(true);
                resendBtn.setBackgroundDrawable(SendEmailActivity.this.getResources().getDrawable(R.drawable.btn_bg_sharp));
                resendBtn.setText(R.string.user_email_resend_btn);
            }

            @Override
            public void onFail(String msg) {
                resendBtn.setClickable(true);
                resendBtn.setBackgroundDrawable(SendEmailActivity.this.getResources().getDrawable(R.drawable.btn_bg_sharp));
                resendBtn.setText(R.string.user_email_resend_btn);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && (TYPE_EMAIL_REGISTER == type)) {
//            return true;//禁止返回
//        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == code) {
                numCount--;
                if (numCount == 0 || SendEmailActivity.this.isFinishing()) {
                    resendBtn.setClickable(true);
                    resendBtn.setBackgroundDrawable(SendEmailActivity.this.getResources().getDrawable(R.drawable.btn_bg_sharp));
                    resendBtn.setText(R.string.user_email_resend_btn);
                    return;
                }
                resendBtn.setClickable(false);
                resendBtn.setBackgroundDrawable(SendEmailActivity.this.getResources().getDrawable(R.drawable.btn_bg_sharp));
                resendBtn.setText(numCount + "s");
                handler.sendEmptyMessageDelayed(code, 1000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        handler = null;
        OkGo.getInstance().cancelTag(this);
    }
}
