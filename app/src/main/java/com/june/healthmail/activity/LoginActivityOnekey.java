package com.june.healthmail.activity;

import android.app.Activity;
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
import com.june.healthmail.model.MessageDetails;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.Installation;
import com.june.healthmail.untils.ShowProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
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
  private EditText et_envite_people;
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
    et_envite_people = (EditText) findViewById(R.id.et_invite_people);
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
    final String code = et_code.getText().toString().trim();
    final String pwd = et_pwd.getText().toString().trim();
    final String phoneNumber = et_phone.getText().toString().trim();
    final String invitePeoplePhone = et_envite_people.getText().toString().trim();

    if (TextUtils.isEmpty(code)) {
      toast("验证码不能为空");
      return;
    }
    if (TextUtils.isEmpty(pwd)) {
      toast("密码不能为空");
      return;
    }
    if(!TextUtils.isEmpty(invitePeoplePhone)){
      if(invitePeoplePhone.length() != 11){
          toast("请输入正确的邀请人手机号码(11位数字)");
          return;
      }
    }
    final ShowProgress showProgress = new ShowProgress(LoginActivityOnekey.this);
    showProgress.setMessage("正在注册...");
    showProgress.setCanceledOnTouchOutside(false);
    showProgress.show();

    if(!TextUtils.isEmpty(invitePeoplePhone)){
      //判断邀请人存不存在
      BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
      query.addWhereEqualTo("username",invitePeoplePhone);
      query.findObjects(new FindListener<UserInfo>() {
        @Override
        public void done(final List<UserInfo> object, BmobException e) {
          if(e==null){
            if(object.size() == 0){
              if(showProgress != null && showProgress.isShowing()) {
                showProgress.dismiss();
              }
              toast("邀请人不存在，请确认之后再输入");
            }else {
              Log.d("test","邀请人信息：" + object.size());

              //开始注册
              UserInfo user = new UserInfo();
              user.setMobilePhoneNumber(phoneNumber); //设置手机号码（必填）
              user.setUsername(phoneNumber);          //设置用户名，如果没有传用户名，则默认为手机号码
              user.setPassword(pwd);                  //设置用户密码
              user.setUserType(0);
              user.setAllowDays(0);
              user.setUnbindTimes(3);
              user.setBindMac("");
              user.setBindDesc("");
              user.setCoinsNumber(0); //每个用户初始金币数量为100个
              user.setInstallId(Installation.id(LoginActivityOnekey.this));
              user.setInvitePeoplePhone(object.get(0).getUsername());
              user.setAppVersion(CommonUntils.getVersionInt(LoginActivityOnekey.this));
              user.signOrLogin(code, new SaveListener<UserInfo>() {
                @Override
                public void done(UserInfo user,BmobException e) {
                  if(showProgress != null && showProgress.isShowing()) {
                    showProgress.dismiss();
                  }
                  if(e==null){
                    Log.d("test","注册成功,userInfo = " + user.toString());
                    //插入邀请人积分记录
                    MessageDetails messageDetails = new MessageDetails();
                    messageDetails.setUserName(object.get(0).getUsername());
                    messageDetails.setStatus(1);
                    messageDetails.setScore(88);
                    messageDetails.setType(1);
                    messageDetails.setReasons("邀请用户注册赠送金币88");
                    messageDetails.setRelatedUserName(phoneNumber);
                    messageDetails.save(new SaveListener<String>() {
                      @Override
                      public void done(String s, BmobException e) {
                        if(e==null){
                          Log.d("test","邀请用户注册赠送金币88成功：" + s);
                        }else{
                          Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                      }
                    });

                    //插入自己积分记录
                    MessageDetails myMessageDetails = new MessageDetails();
                    myMessageDetails.setUserName(phoneNumber);
                    myMessageDetails.setStatus(1);
                    myMessageDetails.setScore(188);
                    myMessageDetails.setType(0);
                    myMessageDetails.setReasons("首次注册赠送100金币，填写邀请人额外获赠88金币，一共188金币");
                    myMessageDetails.setRelatedUserName("");
                    myMessageDetails.save(new SaveListener<String>() {
                      @Override
                      public void done(String s, BmobException e) {
                        finish();
                        if(e==null){
                          Log.d("test","首次注册赠送金币100成功：" + s);
                        }else{
                          Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                        }
                      }
                    });
                    toast("注册成功，请用您注册的手机号和密码登录" );
                  }else{
                    toast("注册失败:" + e.getMessage());
                  }
                }
              });
            }
          }else{
            if(showProgress != null && showProgress.isShowing()) {
              showProgress.dismiss();
            }
            toast("查询邀请人信息失败:" + e.getMessage());
          }
        }
      });

    }else {
      UserInfo user = new UserInfo();
      user.setMobilePhoneNumber(phoneNumber); //设置手机号码（必填）
      user.setUsername(phoneNumber);          //设置用户名，如果没有传用户名，则默认为手机号码
      user.setPassword(pwd);                  //设置用户密码
      user.setUserType(0);
      user.setAllowDays(0);
      user.setUnbindTimes(3);
      user.setBindMac("");
      user.setBindDesc("");
      user.setCoinsNumber(0);
      user.setInvitePeoplePhone("");
      user.setInstallId(Installation.id(LoginActivityOnekey.this));
      user.setAppVersion(CommonUntils.getVersionInt(LoginActivityOnekey.this));
      user.signOrLogin(code, new SaveListener<UserInfo>() {
        @Override
        public void done(UserInfo user,BmobException e) {
          if(showProgress != null && showProgress.isShowing()) {
            showProgress.dismiss();
          }
          if(e==null){
            Log.d("test","注册成功,userInfo = " + user.toString());
            toast("注册成功，请用您注册的手机号和密码登录" );
            //插入积分记录
            MessageDetails messageDetails = new MessageDetails();
            messageDetails.setUserName(phoneNumber);
            messageDetails.setStatus(1);
            messageDetails.setScore(100);
            messageDetails.setType(0);
            messageDetails.setReasons("首次注册赠送金币100");
            messageDetails.setRelatedUserName("");
            messageDetails.save(new SaveListener<String>() {
              @Override
              public void done(String s, BmobException e) {
                finish();
                if(e==null){
                  Log.d("test","首次注册赠送金币100成功：" + s);
                }else{
                  Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                }
              }
            });
          }else{
            toast("注册失败:" + e.getMessage());
          }
        }
      });
    }
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
