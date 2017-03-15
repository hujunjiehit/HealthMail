package com.june.healthmail.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by bjhujunjie on 2017/3/15.
 */

public class ResetPasswordActivity extends Activity {

    MyCountTimer timer;

    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.et_verify_code)
    EditText et_code;
    @BindView(R.id.btn_send)
    Button btn_send;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.btn_reset)
    Button btn_reset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_reset)
    public void reset(View view) {
        resetPwd();
    }

    @OnClick(R.id.img_back)
    public void goBack(View view) {
        finish();
    }

    @OnClick(R.id.btn_send)
    public void sendCode(View view) {
        requestSMSCode();
    }

    private void requestSMSCode() {
        String number = et_phone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            BmobSMS.requestSMSCode(number, "默认模板", new QueryListener<Integer>() {
                @Override
                public void done(Integer integer, BmobException e) {
                    if (e == null) {// 验证码发送成功
                        toast("验证码发送成功");// 用于查询本次短信发送详情
                    }else{//如果验证码发送错误，可停止计时
                        toast("验证码发送错误");// 用于查询本次短信发送详情
                        timer.cancel();
                    }
                }
            });
        } else {
            toast("请输入手机号码");
        }
    }

    private void resetPwd() {
        final String code = et_code.getText().toString();
        final String pwd = et_pwd.getText().toString();
        if (TextUtils.isEmpty(code)) {
            toast("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            toast("密码不能为空");
            return;
        }
        final ShowProgress showProgress = new ShowProgress(ResetPasswordActivity.this);
        showProgress.setMessage("正在重置密码...");
        showProgress.setCanceledOnTouchOutside(false);
        showProgress.show();

        // V3.3.9提供的重置密码功能，只需要输入验证码和新密码即可
        UserInfo.resetPasswordBySMSCode(code, pwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null){
                    toast("密码重置成功");
                    finish();
                }else {
                    toast("密码重置失败：code="+e.getErrorCode()+"，错误描述："+e.getLocalizedMessage());
                }
            }
        });
    }

    private void toast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            btn_send.setText((millisUntilFinished / 1000) +"秒后重发");
        }
        @Override
        public void onFinish() {
            btn_send.setText("重新发送验证码");
        }
    }
}
