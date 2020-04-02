package com.coinbene.common.widget.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.database.UserInfoController;
import com.coinbene.common.database.UserInfoTable;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * 新增广告时，弹窗确认，输入短信密码，资金密码等
 */
public class AddAdConfirmDialog extends Dialog {

    private AdDialogClickListener clickLisenter;
    private String desStr, downUrl;
    private EditText ad_input_verificatEd;
    private EditText ad_input_passwordEd;
    private TextView ad_send_smsTv;
    private TextView send_msg_text_tv;

    /**
     * 倒计时总时长
     */
    private int codeTime;

    public void setClickLisenter(AdDialogClickListener clickLisenter) {
        this.clickLisenter = clickLisenter;
    }

    public AddAdConfirmDialog(Context context) {
        this(context, R.style.CoinBene_BottomSheet);
    }

    public AddAdConfirmDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AddAdConfirmDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        setContentView(R.layout.common_dialog_ad_confirm);
        findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onCancel();
            }
        });

        Button okBtn = findViewById(R.id.btn_update);
        okBtn.setOnClickListener(v -> {
            String identifyCode = ad_input_verificatEd.getText().toString();
            if (TextUtils.isEmpty(identifyCode)) {
                return;
            }
            String assetPassword = ad_input_passwordEd.getText().toString();
            if (TextUtils.isEmpty(assetPassword)) {
                return;
            }
            dismiss();
            if (clickLisenter != null) {
                clickLisenter.onAdDialogOk(identifyCode, assetPassword);
            }
        });
        ad_input_verificatEd = findViewById(R.id.ad_input_verificat);
        ad_input_passwordEd = findViewById(R.id.ad_input_password);
        ad_send_smsTv = findViewById(R.id.ad_send_sms);
        send_msg_text_tv = findViewById(R.id.send_msg_text_tv);

        ad_send_smsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVerificat();
            }
        });

        UserInfoTable userTable = UserInfoController.getInstance().getUserInfo();
        if (userTable != null && !TextUtils.isEmpty(userTable.verifyWay) && userTable.verifyWay.equals(Constants.CODE_GOOGLE_CHECK_TYPE)) {
            ad_input_verificatEd.setHint(R.string.please_input_google_code);
            ad_send_smsTv.setVisibility(View.GONE);
            send_msg_text_tv.setText(R.string.check_google_code_label);
        } else {
//            isGoogleVerify = false;
            ad_send_smsTv.setVisibility(View.VISIBLE);
            ad_input_verificatEd.setHint(R.string.msg_code_is_empty);
            send_msg_text_tv.setText(R.string.user_msg_code_label);
        }


        ad_input_passwordEd.setSingleLine();
        ad_input_passwordEd.setHint(R.string.cap_pwd_hint);
        ad_input_passwordEd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    /**
     * 发送验证码
     */
    public void sendVerificat() {
        ad_send_smsTv.setEnabled(false);

        OkGo.<BaseRes>post(Constants.USER_SENDSMS).params("type", 17).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.send_msg_code_success);
                codeTime = 59;
                handler.sendEmptyMessageDelayed(200, 1000);
            }

            @Override
            public void onE(Response<BaseRes> response) {
                ad_send_smsTv.setEnabled(true);
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
            if (msg.what == 200) {
                if (handler == null) {
                    return;
                }
                ad_send_smsTv.setText(String.format("%ds", codeTime));
                ad_send_smsTv.setTextColor(getContext().getResources().getColor(R.color.res_textColor_2));
                codeTime--;
                if (codeTime > 0) {
                    handler.sendEmptyMessageDelayed(200, 1000);
                } else {
                    ad_send_smsTv.setEnabled(true);
                    ad_send_smsTv.setText(getContext().getString(R.string.send_msg_code_again));
                    ad_send_smsTv.setTextColor(getContext().getResources().getColor(R.color.res_blue));
                    codeTime = 59;
                }
            }
        }
    };

    @Override
    public void dismiss() {
        super.dismiss();
        handler.removeCallbacks(null);
        handler = null;
    }

    public interface AdDialogClickListener {
        void onCancel();

        void onAdDialogOk(String identifyCode, String assetCode);
    }
}
