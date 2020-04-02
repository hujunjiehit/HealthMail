package com.coinbene.common.widget.app;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.coinbene.common.Constants;
import com.coinbene.common.R;
import com.coinbene.manbiwang.model.base.BaseRes;
import com.coinbene.common.network.newokgo.NewJsonSubCallBack;
import com.coinbene.common.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

public class SecondCheckDialog extends Dialog implements View.OnClickListener {

    private ImageView closeImg;

    private EditText codeEdit;

    private LinearLayout verificationLayout;

    private Button sure;

    private SecondCheckListner listner;

    private int type;

    private TextView mode;

    private TextView sendSMS;

    private int codeTime;

    private View verticalLine;

    /**
     * 发送验证码倒计时
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == R.id.dialog_send_SMS) {
                sendSMS.setText(codeTime + "s");
                sendSMS.setTextColor(getContext().getResources().getColor(R.color.res_textColor_2));
                codeTime--;
                if (codeTime > 0) {
                    handler.sendEmptyMessageDelayed(R.id.dialog_send_SMS, 1000);
                } else {
                    sendSMS.setEnabled(true);
                    sendSMS.setText(getContext().getString(R.string.send_msg_code_again));
                    sendSMS.setTextColor(getContext().getResources().getColor(R.color.res_blue));
                    codeTime = 59;
                }
            }
        }
    };


    public SecondCheckDialog(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        // 在构造方法里, 传入主题
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawable(new BitmapDrawable());
        // 获取Window的LayoutParams
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.WRAP_CONTENT;
        attributes.gravity = Gravity.CENTER;
        // 一定要重新设置, 才能生效
        window.setAttributes(attributes);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_second_check_layout);
        initView();
        initListener();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        OkGo.getInstance().cancelTag(this);
    }

    private void initListener() {
        closeImg.setOnClickListener(this);
        sure.setOnClickListener(this);
        sendSMS.setOnClickListener(this);
    }

    private void initView() {
        closeImg = findViewById(R.id.second_check_close);
        codeEdit = findViewById(R.id.second_check_edit);
        sendSMS = findViewById(R.id.dialog_send_SMS);
        mode = findViewById(R.id.check_verification_mode);
        verticalLine = findViewById(R.id.second_vertical_line);
        verificationLayout = findViewById(R.id.sercond_check_verification_layout);
        sure = findViewById(R.id.second_check_sure);



        codeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 6) {
                    listner.autoCheck(String.valueOf(s));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 设置当前验证模式
     */
    public void setMode(String mode) {
        this.mode.setText(mode);
    }


    /**
     * @param hint 设置当前模式默认输入
     */
    public void setHint(String hint) {
        codeEdit.setHint(hint);
    }

    public void hideSendSMS() {
        sendSMS.setVisibility(View.GONE);
        verticalLine.setVisibility(View.GONE);
    }
    public void showSendSMS() {
        sendSMS.setVisibility(View.VISIBLE);
        verticalLine.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.second_check_close) {
            if (listner != null) {
                listner.dismiss();
            }
            dismiss();
        } else if (id == R.id.second_check_sure) {
            if (listner != null) {
                listner.closeCheck(codeEdit.getText().toString());
            }
        } else if (id == R.id.dialog_send_SMS) {
            sendSMS();
        }
    }

    /**
     * 设置监听
     */
    public void SecondCheckListner(SecondCheckListner listner) {
        this.listner = listner;
    }


    /**
     * 发送验证码
     */
    private void sendSMS() {

        sendSMS.setEnabled(false);

        OkGo.<BaseRes>post(Constants.OLD_VERIFY_CODE).params("type", 10).tag(this).execute(new NewJsonSubCallBack<BaseRes>() {
            @Override
            public void onSuc(Response<BaseRes> response) {
                ToastUtil.show(R.string.send_msg_code_success);
                codeTime = 59;
                handler.sendEmptyMessageDelayed(R.id.dialog_send_SMS, 1000);
            }

            @Override
            public void onE(Response<BaseRes> response) {
                sendSMS.setEnabled(true);
            }
        });

    }

    /**
     * 关闭登录二次验证接口
     */
    public interface SecondCheckListner {
        void closeCheck(String code);

        void dismiss();

        void autoCheck(String code);
    }
}
