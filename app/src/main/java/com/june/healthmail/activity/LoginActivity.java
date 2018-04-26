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
import com.june.healthmail.model.DeviceInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.Installation;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;
import com.tencent.bugly.beta.Beta;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class LoginActivity extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

  /**通过Bmob登录*/
  private final int LOG_BY_BMOB = 1;

  private static final int START_TO_LOGIN = 2;
  private static final int USER_LOGIN_SUCESS = 3;
  private static final int GO_TO_MAIN_ACTIVITY = 4;
  private static final int GO_TO_UNBIND_ACTIVITY = 5;
  private static final int REQUEST_CODE_REGISTER = 6;


  private ToggleButton mTgBtnShowPsw;
  private EditText mEditPsw;
  private EditText mEditUid;
  private ImageView mBtnClearUid;
  private ImageView mBtnClearPsw;
  private Button mBtnLogin;
  private ShowProgress showProgress;

  private String objectUid;
  private DeviceInfo deviceInfo;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      Intent intent;
      switch (msg.what) {
        case START_TO_LOGIN:
          login();
          break;
        case USER_LOGIN_SUCESS:
          Log.d("test","用户登录成功,处理后续设备绑定");
          UserInfo userInfo = (UserInfo) msg.obj;
          Log.d("test","userInfo = " + userInfo.toString());
          objectUid = userInfo.getObjectId();
          if(deviceInfo == null){
            Log.d("test","用户第一次登录，插入设备信息");
            deviceInfo = new DeviceInfo();
            deviceInfo.setUsername(userInfo.getUsername());
            deviceInfo.setDeviceId(Installation.id(LoginActivity.this).trim());
            deviceInfo.setDeviceMac(CommonUntils.getLocalMacAddressFromIp(LoginActivity.this).trim());
            deviceInfo.setDeviceDesc(CommonUntils.getUserAgent(LoginActivity.this).trim());
            deviceInfo.setUnbindTimes(3);
            deviceInfo.save(new SaveListener<String>() {
              @Override
              public void done(String s, BmobException e) {
                if(e == null){
                  Log.d("test","插入设备信息成功," + s + ":" + deviceInfo.toString());
                  mHandler.sendEmptyMessage(GO_TO_MAIN_ACTIVITY);
                }else {
                  Log.d("test","插入设备信息失败，" + e.getMessage());
                }
              }
            });
          }else {
            if(TextUtils.isEmpty(deviceInfo.getDeviceId())){
              Log.d("test","设备id为空，更新设备信息");
              deviceInfo.setDeviceId(Installation.id(LoginActivity.this).trim());
              deviceInfo.setDeviceMac(CommonUntils.getLocalMacAddressFromIp(LoginActivity.this).trim());
              deviceInfo.setDeviceDesc(CommonUntils.getUserAgent(LoginActivity.this).trim());
              deviceInfo.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                  if(e == null){
                    Log.d("test","更新设备信息成功");
                    mHandler.sendEmptyMessage(GO_TO_MAIN_ACTIVITY);
                  }else{
                    Log.d("test","更新设备信息失败，" + e.getMessage());
                  }
                }
              });
            }else {
              Log.d("test","设备id不为空，直接登录");
              mHandler.sendEmptyMessage(GO_TO_MAIN_ACTIVITY);
            }
          }
          break;
        case GO_TO_MAIN_ACTIVITY:
          Log.d("test","go to main activity");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          intent = new Intent(LoginActivity.this, MainActivity.class);
          if(objectUid != null) {
            intent.putExtra("uid",objectUid);
          }
          startActivity(intent);
          overridePendingTransition(0, 0);
          LoginActivity.this.finish();
          break;
        case GO_TO_UNBIND_ACTIVITY:
          Log.d("test","go to unbind activity");
          intent = new Intent(LoginActivity.this, UnbindActivity.class);
          Bundle bundle = new Bundle();
          bundle.putSerializable("deviceInfo",deviceInfo);
          intent.putExtras(bundle);
          startActivity(intent);
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
    findViewById(R.id.tv_find_back_psw).setOnClickListener(this);
    findViewById(R.id.btn_unbind_device).setOnClickListener(this);
    findViewById(R.id.btn_check_update).setOnClickListener(this);
  }

  /**
   * 初始化记住的用户名
   */
  private void initUid() {
    SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
    String uid = sp.getString("uid", "");
    mEditUid.setText(uid);
    PreferenceHelper.getInstance().saveUid(uid);
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
          if (e.getErrorCode() == 101) {
            Toast.makeText(LoginActivity.this, "用户名或者密码不正确，请重试", Toast.LENGTH_LONG).show();
          } else {
            Toast.makeText(LoginActivity.this, "登录失败:"+e.getMessage(), Toast.LENGTH_LONG).show();
          }

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
        prepareLogin();
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
        //startActivity(new Intent(this, LoginActivityOnekey.class));
        Intent intent = new Intent(this,LoginActivityOnekey.class);
        startActivityForResult(intent,REQUEST_CODE_REGISTER);
        break;
      case R.id.tv_find_back_psw:	//找回密码
        startActivity(new Intent(this, ResetPasswordActivity.class));
        break;
      case R.id.btn_unbind_device: //解除设备绑定
        startToUnbindDevice();
        break;
      case R.id.btn_check_update: // 点击检查更新按钮
        toast("当前应用版本：" + CommonUntils.getVersion(LoginActivity.this));
        //BmobUpdateAgent.forceUpdate(LoginActivity.this);
        Beta.checkUpgrade();
        break;
      default:
        break;
    }
  }

  private void prepareLogin() {
    //登录前需要先获取设备信息
    String userName = mEditUid.getText().toString();
    if(TextUtils.isEmpty(userName)){
      toast("请先输入需要登录的手机号");
    }else {
      if(showProgress != null && !showProgress.isShowing()){
        showProgress.setMessage("正在登陆,请稍后...");
        showProgress.show();
      }

      //判断邀请人存不存在
      BmobQuery<DeviceInfo> query = new BmobQuery<DeviceInfo>();
      query.addWhereEqualTo("username",userName);
      query.findObjects(new FindListener<DeviceInfo>() {
        @Override
        public void done(List<DeviceInfo> list, BmobException e) {
          if(e == null){
            if(list.size() == 0){
              Log.d("test","设备信息不存在");
              deviceInfo = null;
              mHandler.sendEmptyMessage(START_TO_LOGIN);
            }else {
              deviceInfo = list.get(0);
              Log.d("test","设备信息存在:" + list.get(0).toString());
              if(TextUtils.isEmpty(deviceInfo.getDeviceId())
                      || deviceInfo.getDeviceId().equals(Installation.id(LoginActivity.this))){
                //设备信息验证成功
                Log.d("test","设备信息验证通过");
                mHandler.sendEmptyMessage(START_TO_LOGIN);
              }else {
                showDeviceErrorDialog(deviceInfo);
              }
            }
          }else {
            if(e.getErrorCode() == 101 || e.getErrorCode() == 503){
              deviceInfo = null;
              mHandler.sendEmptyMessage(START_TO_LOGIN);
            }else{
              if(showProgress != null && showProgress.isShowing()){
                showProgress.dismiss();
              }
              toast("验证设备信息异常，请检查网络，" + e.getMessage());
            }
          }
        }
      });
    }
  }

  private void startToUnbindDevice() {
    String userName = mEditUid.getText().toString();
    String pwd = mEditPsw.getText().toString();
    if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(pwd)){
      toast("请先输入账号密码再解绑");
    }else {
      if (showProgress != null && !showProgress.isShowing()) {
        showProgress.setMessage("正在查询设备信息...");
        showProgress.show();
      }
      BmobQuery<DeviceInfo> query = new BmobQuery<DeviceInfo>();
      query.addWhereEqualTo("username", userName);
      query.findObjects(new FindListener<DeviceInfo>() {
        @Override
        public void done(List<DeviceInfo> list, BmobException e) {
          if (showProgress != null && showProgress.isShowing()) {
            showProgress.dismiss();
          }
          if (e == null) {
            if (list.size() > 0) {
              deviceInfo = list.get(0);
              showUnbindDeviceDialog();
            } else {
              toast("没有查到该账号的对应的设备信息，请确认账号是否正确");
            }
          } else {
            toast("查询设备信息异常，请检查网络，" + e.getMessage());
          }
        }
      });
    }
  }

  private void showUnbindDeviceDialog() {
    if(deviceInfo != null) {
      if(TextUtils.isEmpty(deviceInfo.getDeviceId())){
        toast("当前账号暂未绑定设备，无需解绑");
        return;
      }

      //登录才能解绑
      String userName = mEditUid.getText().toString();
      String pwd = mEditPsw.getText().toString();
      final UserInfo user = new UserInfo();
      user.setUsername(userName);
      user.setPassword(pwd);
      user.login(new SaveListener<Object>() {
        @Override
        public void done(Object o, BmobException e) {
          //密码验证成功才能解绑
          if( e == null) {
            //将用户名和密码保存到Preference
            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putString("uid", mEditUid.getText().toString());
            edit.putString("pwd", mEditPsw.getText().toString());
            edit.commit();

            Log.d("test","登陆成功，跳转到解绑界面");
            Message msg = mHandler.obtainMessage();
            //msg.obj = deviceInfo;
            mHandler.sendEmptyMessage(GO_TO_UNBIND_ACTIVITY);
          } else {
            if (e.getErrorCode() == 101) {
              Toast.makeText(LoginActivity.this, "用户名或者密码不正确，请重试", Toast.LENGTH_LONG).show();
            } else {
              Toast.makeText(LoginActivity.this, "登录失败:"+e.getMessage(), Toast.LENGTH_LONG).show();
            }
          }
        }
      });

    }
  }
  private void showDeviceErrorDialog(DeviceInfo deviceInfo) {
    Log.d("test","DEVICE_ERROR");
    if(showProgress != null && showProgress.isShowing()){
      showProgress.dismiss();
    }
    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
        .setTitle("提醒")
        .setMessage("此帐号已经绑定了另外一台设备("+deviceInfo.getDeviceDesc()+")" +
            " 如需继续,请先解除绑定")
        .setPositiveButton("点击解绑", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
              startToUnbindDevice();
          }
        }).create();
    dialog.show();
  }

  private void showInfoDialog() {
    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
        .setTitle("提醒")
        .setMessage("首次登录需要在WIFI环境下登录,请连接WIFI之后再继续登录.")
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    Log.e("test","onActivityResult, requestCode = " + requestCode + "  resultCode = " + resultCode);
    if(requestCode == REQUEST_CODE_REGISTER && resultCode == RESULT_OK) {
      Log.e("test","userName = " + data.getExtras().getString("userName"));
      mEditUid.setText(data.getExtras().getString("userName"));
      mEditPsw.setText(data.getExtras().getString("passWord"));
    }
  }
}
