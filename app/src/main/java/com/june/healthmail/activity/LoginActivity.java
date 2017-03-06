package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.june.healthmail.R;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.ShowProgress;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class LoginActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  /**通过Bmob登录*/
  private final int LOG_BY_BMOB = 1;
  /**通过微博登录*/
  private final int LOG_BY_WEIBO = 2;

  private static final int USER_LOGIN_SUCESS = 3;
  private static final int GO_TO_MAIN_ACTIVITY = 4;

  private ToggleButton mTgBtnShowPsw;
  private EditText mEditPsw;
  private EditText mEditUid;
  private ImageView mBtnClearUid;
  private ImageView mBtnClearPsw;
  private Button mBtnLogin;
  private ShowProgress showProgress;

  private String objectUid;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case USER_LOGIN_SUCESS:
          Log.e("test","用户登录成功,处理后续mac地址绑定");
          UserInfo userInfo = (UserInfo) msg.obj;
          Log.e("test","userInfo = " + userInfo.toString());
          objectUid = userInfo.getObjectId();
          if(userInfo.getBindMac() == null || TextUtils.isEmpty(userInfo.getBindMac())){
            //该用户暂未绑定设备
            String mac = CommonUntils.getLocalMacAddressFromIp(LoginActivity.this);
            String deviceDesc =  CommonUntils.getUserAgent(LoginActivity.this);
            Log.e("test","mac address is: " + mac + "   desc:" + deviceDesc);
            userInfo.setBindMac(mac);
            userInfo.setBindDesc(deviceDesc);

            BmobUser bmobUser = BmobUser.getCurrentUser();
            userInfo.update(bmobUser.getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e==null){
                  Log.e("test","更新用户信息成功");
                  mHandler.sendEmptyMessage(GO_TO_MAIN_ACTIVITY);
                }else{
                  toast("更新用户信息失败:" + e.getMessage());
                }
              }
            });
          }else {
            //用户已经绑定了设备  bindMac不为null
            String userMac = CommonUntils.getLocalMacAddressFromIp(LoginActivity.this).trim();
            String bindMac = userInfo.getBindMac().trim();
            String userAgent = CommonUntils.getUserAgent(LoginActivity.this).trim();
            String bindAgent = userInfo.getBindDesc().trim();
            Log.e("test","userMac address is: " + userMac + "   bindMac:" + bindMac);
            Log.e("test","userAgentis: " + userAgent + "   bindAgent:" + bindAgent);
            if(userMac.equals(bindMac) && userAgent.equals(bindAgent)){
              //设备信息验证正确
              mHandler.sendEmptyMessage(GO_TO_MAIN_ACTIVITY);
            }else{
              //设备信息验证不通过
              showDeviceErrorDialog(userInfo);
            }
          }
          break;
        case GO_TO_MAIN_ACTIVITY:
          Log.e("test","go to main activity");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          Intent intent = new Intent(LoginActivity.this, MainActivity.class);
          if(objectUid != null) {
            intent.putExtra("uid",objectUid);
          }
          startActivity(intent);
          overridePendingTransition(0, 0);
          LoginActivity.this.finish();
          break;
        default:
          break;
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    BmobUser bmobUser = BmobUser.getCurrentUser();
    if(bmobUser != null){
      // 允许用户使用应用
//      Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//      intent.putExtra("uid", bmobUser.getObjectId());
//      startActivity(intent);
//      overridePendingTransition(0, 0);
//      LoginActivity.this.finish();
    }
    setContentView(R.layout.activity_login);
    initUI();
    setOnListener();
    initUid();
  }

  private void initUI() {
    showProgress = new ShowProgress(this);
    mBtnLogin = (Button) findViewById(R.id.btn_login);
    mEditUid = (EditText) findViewById(R.id.edit_uid);
    mEditPsw = (EditText) findViewById(R.id.edit_psw);
    mBtnClearUid = (ImageView) findViewById(R.id.img_login_clear_uid);
    mBtnClearPsw = (ImageView) findViewById(R.id.img_login_clear_psw);
    mTgBtnShowPsw = (ToggleButton) findViewById(R.id.tgbtn_show_psw);
  }


  private void setOnListener() {
    mEditUid.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditUid.getText().toString().length() > 0) {
          mBtnClearUid.setVisibility(View.VISIBLE);
          if (mEditPsw.getText().toString().length() >0) {
            mBtnLogin.setEnabled(true);
          } else {
            mBtnLogin.setEnabled(false);
          }
        } else {
          mBtnLogin.setEnabled(false);
          mBtnClearUid.setVisibility(View.INVISIBLE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    mEditPsw.addTextChangedListener(new TextWatcher() {
      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mEditPsw.getText().toString().length() > 0) {
          mBtnClearPsw.setVisibility(View.VISIBLE);
          if (mEditUid.getText().toString().length() > 0) {
            mBtnLogin.setEnabled(true);
          } else {
            mBtnLogin.setEnabled(false);
          }
        } else {
          mBtnLogin.setEnabled(false);
          mBtnClearPsw.setVisibility(View.INVISIBLE);
        }
      }

      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      }

      @Override
      public void afterTextChanged(Editable s) {
      }
    });

    mBtnLogin.setOnClickListener(this);
    mBtnClearUid.setOnClickListener(this);
    mBtnClearPsw.setOnClickListener(this);
    mTgBtnShowPsw.setOnCheckedChangeListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    findViewById(R.id.btn_login_wb).setOnClickListener(this);
    findViewById(R.id.tv_quick_sign_up).setOnClickListener(this);
  }

  /**
   * 初始化记住的用户名
   */
  private void initUid() {
    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
    String uid = sp.getString("uid", "");
    mEditUid.setText(uid);
    String pwd = sp.getString("pwd", "");
    mEditPsw.setText(pwd);
  }

  /**
   * 清空控件文本
   */
  private void clearText(EditText edit) {
    edit.setText("");
  }

  /**
   * 登录按钮
   */
  private void login() {
    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("正在登陆,请稍后...");
      showProgress.show();
    }
    String userName = mEditUid.getText().toString();
    String pwd = mEditPsw.getText().toString();
    final UserInfo user = new UserInfo();
    user.setUsername(userName);
    user.setPassword(pwd);
    user.login(new SaveListener<UserInfo>() {
      @Override
      public void done(UserInfo userInfo, BmobException e) {
        if (e == null) {
          //将用户名保存到Preference
          SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
          SharedPreferences.Editor edit = sp.edit();
          edit.putString("uid", mEditUid.getText().toString());
          edit.putString("pwd", mEditPsw.getText().toString());
          edit.commit();

          //通过Bmob登录
          setSP(LOG_BY_BMOB);
          Message msg = mHandler.obtainMessage(USER_LOGIN_SUCESS);
          msg.obj = userInfo;
          msg.sendToTarget();
        } else {
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          Toast.makeText(LoginActivity.this, "登录失败，请确认帐号和密码是否正确", Toast.LENGTH_LONG).show();
        }
      }
    });
  }

  private void toast(String msg){
    Toast.makeText(LoginActivity.this,msg, Toast.LENGTH_LONG).show();
  }
  /**
   * 将登录途径保存到SharedPreferences，1为Bmob，2为微博
   */
  private void setSP(int type) {
    //保存当前位置到SharedPreferences
    SharedPreferences sp = this.getSharedPreferences("login_type", Context.MODE_PRIVATE);
    SharedPreferences.Editor edit = sp.edit();
    edit.putInt("login_type", type);
    edit.commit();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_login:	//登录
        login();
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.btn_login_wb:	//微博登录
        //loginWB();
        break;
      case R.id.img_login_clear_uid:	//清除用户名
        clearText(mEditUid);
        break;
      case R.id.img_login_clear_psw:	//清除密码
        clearText(mEditPsw);
        break;
      case R.id.tv_quick_sign_up:	//快速注册
        startActivity(new Intent(this, LoginActivityOnekey.class));
        break;
      default:
        break;
    }
  }


  private void showDeviceErrorDialog(UserInfo userInfo) {
    Log.e("test","DEVICE_ERROR");
    if(showProgress != null && showProgress.isShowing()){
      showProgress.dismiss();
    }
    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
        .setTitle("提醒")
        .setMessage("次帐号已经绑定了另外一台设备("+userInfo.getBindDesc()+")" +
            " 如需继续,请先解除绑定")
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
          }
        }).create();
    dialog.show();
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      //显示密码
      mEditPsw.setTransformationMethod(
          HideReturnsTransformationMethod.getInstance());
    } else {
      //隐藏密码
      mEditPsw.setTransformationMethod(
          PasswordTransformationMethod.getInstance());
    }
  }
}
