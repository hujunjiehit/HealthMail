package com.coinbene.manbiwang.user.safe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.base.CoinbeneBaseActivity;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.okgo.NewDialogCallback;
import com.coinbene.common.utils.ToastUtil;
import com.coinbene.manbiwang.user.R;
import com.coinbene.manbiwang.user.R2;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.coinbene.common.Constants.CODE_PHONE_MSG_CHECK_TYPE;

/**
 * @author ding
 * 验证方式首选项
 */
public class VerificationPreferenceActivity extends CoinbeneBaseActivity {

    @BindView(R2.id.menu_back)
    RelativeLayout menuBack;

    @BindView(R2.id.preference_verficationCode)
    EditText preferenceCode;

    @BindView(R2.id.preference_confirm)
    Button submit;

    @BindView(R2.id.preference_verification_mode)
    TextView preferenceMode;

    @BindView(R2.id.preference_vertical_line)
    View verticalLine;

    @BindView(R2.id.preference_send_sms)
    TextView sendSMS;

    @BindView(R2.id.preference_tip)
    TextView tip;
    private int codeTime;

    /**
     * 发送验证码倒计时
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == R.id.preference_send_sms) {
                sendSMS.setText(codeTime + "s");
                sendSMS.setTextColor(getResources().getColor(R.color.res_textColor_2));
                codeTime--;
                if (codeTime > 0) {
                    handler.sendEmptyMessageDelayed(R.id.preference_send_sms, 1000);
                } else {
                    sendSMS.setEnabled(true);
                    sendSMS.setText(getString(R.string.send_msg_code_again));
                    sendSMS.setTextColor(getResources().getColor(R.color.res_blue));
                    codeTime = 59;
                }
            }
        }
    };

    private UserInfoTable user;

    public static void startActivity(Context context){
        context.startActivity(new Intent(context,VerificationPreferenceActivity.class));
    }

    @Override
    public int initLayout() {
        return R.layout.settings_activity_verification_preference;
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
        OkGo.getInstance().cancelTag(this);
        handler.removeCallbacksAndMessages(null);
    }


    /**
     * 初始化View
     */
    public void init() {

        //            验证码首选项
        user = UserInfoController.getInstance().getUserInfo();

        if (CODE_PHONE_MSG_CHECK_TYPE.equals(user.verifyWay)) {
            preferenceMode.setText(getString(R.string.check_msg_item_label));
            preferenceCode.setHint(R.string.input_verification_code);
            tip.setText(R.string.set_google_code);
            sendSMS.setVisibility(View.VISIBLE);
            verticalLine.setVisibility(View.VISIBLE);
        } else {
            preferenceMode.setText(getString(R.string.setting_google_auth));
            preferenceCode.setHint(R.string.please_input_google_code);
            tip.setText(R.string.set_SMS_code);
        }

        initListener();
    }

    private void initListener() {
        menuBack.setOnClickListener(this);
        submit.setOnClickListener(this);
        sendSMS.setOnClickListener(this);
    }


    /**
     * 发送验证码
     */
    private void sendSMS() {
        sendSMS.setEnabled(false);

        OkGo.<BaseRes>post(Constants.OLD_VERIFY_CODE).params("type", 5).tag(this).execute(new NewDialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.send_msg_code_success);
                codeTime = 59;
                handler.sendEmptyMessageDelayed(R.id.preference_send_sms, 1000);
            }

            @Override
            public void onE(Response<BaseRes> response) {
                sendSMS.setEnabled(true);
            }
        });
    }


    /**
     * 更改验证首选项
     */
    private void updatePreference() {

        HttpParams params = new HttpParams();
        params.put("verifyCode", preferenceCode.getText().toString());
        OkGo.<BaseRes>post(Constants.USER_TOGGLE_VERIFYWAY).params(params).tag(this).execute(new NewDialogCallback<BaseRes>(this) {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.submit_success);
                finish();
            }

            @Override
            public void onE(Response<BaseRes> response) {
            }
        });

    }


    /**
     * @param v 点击事件
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.menu_back) {
            finish();
        } else if (id == R.id.preference_send_sms) {
            sendSMS();
        } else if (id == R.id.preference_confirm) {//   验证码不为空才可以更改
            if (TextUtils.isEmpty(preferenceCode.getText().toString())) {
                ToastUtil.show(R.string.user_hint_msg_code);
                return;
            }

            updatePreference();
        }
    }
}
