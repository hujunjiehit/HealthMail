package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by bjhujunjie on 2017/3/6.
 */

public class LoginActivityOnekey extends Activity implements View.OnClickListener {

  private MyCountTimer timer;
  private ImageView img_back;
  private EditText et_phone;
  private EditText et_code;
  private Button btn_send;
  private EditText et_pwd;
  private Button btn_regist;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login_one_key);
    initUI();
    setListener();
  }


  private void initUI() {
    img_back = (ImageView) findViewById(R.id.img_back);
    et_phone = (EditText) findViewById(R.id.et_phone);
    et_pwd = (EditText) findViewById(R.id.et_pwd);
    et_code = (EditText) findViewById(R.id.et_verify_code);
    btn_send = (Button)findViewById(R.id.btn_send);
    btn_regist = (Button)findViewById(R.id.btn_regist);
  }

  private void setListener() {
    img_back.setOnClickListener(this);
    btn_send.setOnClickListener(this);
    btn_regist.setOnClickListener(this);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.btn_send:
        requestSMSCode();
        break;
      case R.id.btn_regist:
        signOrLogin();
        break;
      default:
        break;
    }
  }

  private void signOrLogin() {
    final String code = et_code.getText().toString();
    final String pwd = et_pwd.getText().toString();
    final String phoneNumber = et_phone.getText().toString();
    if (TextUtils.isEmpty(code)) {
      toast("验证码不能为空");
      return;
    }
    if (TextUtils.isEmpty(pwd)) {
      toast("密码不能为空");
      return;
    }
    final ShowProgress showProgress = new ShowProgress(LoginActivityOnekey.this);
    showProgress.setMessage("正在注册...");
    showProgress.setCanceledOnTouchOutside(false);
    showProgress.show();

    UserInfo user = new UserInfo();
    user.setMobilePhoneNumber(phoneNumber); //设置手机号码（必填）
    user.setUsername(phoneNumber);          //设置用户名，如果没有传用户名，则默认为手机号码
    user.setPassword(pwd);                  //设置用户密码
    user.setUserType(1);
    user.setAllowDays(1);
    user.setUnbindTimes(3);
    user.setBindMac("");
    user.setBindDesc("");
    user.signOrLogin(code, new SaveListener<UserInfo>() {
      @Override
      public void done(UserInfo user,BmobException e) {
        if(showProgress != null && showProgress.isShowing()) {
          showProgress.dismiss();
        }
        if(e==null){
          Log.e("test","注册成功,userInfo = " + user.toString());
          toast("注册成功，请用您注册的手机号和密码登录" );
          finish();
        }else{
          toast("注册失败:" + e.getMessage());
        }
      }
    });
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
