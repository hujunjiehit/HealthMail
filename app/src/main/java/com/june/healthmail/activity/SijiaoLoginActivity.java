package com.june.healthmail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.SijiaoModel;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.model.TrainerModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;

import java.io.IOException;
import java.io.Serializable;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/7/6.
 */

public class SijiaoLoginActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener{

  private ToggleButton mTgBtnShowPsw;
  private EditText mEditPsw;
  private EditText mEditUid;
  private ImageView mBtnClearUid;
  private ImageView mBtnClearPsw;
  private Button mBtnLogin;
  private ShowProgress showProgress;
  private UserInfo userInfo;

  private CheckBox cbRemberPwd;

  private String sijiaoUid;
  private String sijiaoPwd;
  private String errmsg;

  private String accessToken;

  private static final int START_TO_LOGIN = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int GET_USER_MODEL_SUCCES = 3;
  private static final int GET_USER_MODEL_FAILED = 4;
  private static final int GET_TOKEN_FAILED = 5;
  private static final int USER_PWD_WRONG = 6;
  private static final int REQUEST_INVAILED = 7;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_LOGIN:
          Log.e("test","uid = " + sijiaoUid);
          Log.e("test","pwd = " + sijiaoPwd);
          if(showProgress != null && !showProgress.isShowing()){
            showProgress.setMessage("正在获取私教信息...");
            showProgress.show();
          }
          getAccountToken();
          break;
        case GET_TOKEN_SUCCESS:
          Log.e("test","token:" + accessToken);
          getUserModel(sijiaoUid);
          break;
        case GET_USER_MODEL_SUCCES:
          GetUserModel getUserModel = (GetUserModel)msg.obj;
          TrainerModel trainerModel = getUserModel.getValuse().getPTrainerModel();
          //Log.e("test","trainerModel:" + trainerModel);
          PreferenceHelper.getInstance().setSijiaoUid(sijiaoUid);
          PreferenceHelper.getInstance().setSijiaoPwd(sijiaoPwd);
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }

          SijiaoModel sijiaoModel = new SijiaoModel();
          sijiaoModel.setUserName(userInfo.getUsername());
          sijiaoModel.setSijiaoName(trainerModel.getHM_PT_Name());
          sijiaoModel.setIdCard(trainerModel.getHM_PT_IDCard());
          sijiaoModel.setCollege(trainerModel.getHM_PT_College());
          sijiaoModel.setCourseCost(trainerModel.getHM_PT_CourseCost());
          sijiaoModel.setUserId(trainerModel.getUser_Id());
          sijiaoModel.setUserAccount(sijiaoUid);
          sijiaoModel.setUserPwd(sijiaoPwd);
          sijiaoModel.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
              if(e == null){
                Log.e("test","保存成功");
              }else {
                Log.e("test","保存失败：" + e.getMessage());
              }
            }
          });

          Intent it = new Intent();
          it.putExtra("accessToken",accessToken);
          it.putExtra("trainerModel", (Serializable)trainerModel);
          it.setClass(SijiaoLoginActivity.this,SpecialFunctionListActivity.class);
          startActivity(it);
          break;
        case GET_USER_MODEL_FAILED:
          toast("获取私教信息失败");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          break;
        case GET_TOKEN_FAILED:
          toast("登录失败，请检查网络");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          break;
        case USER_PWD_WRONG:
          toast("私教密码不正确");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          break;
        case REQUEST_INVAILED:
          toast("请求失效，请设置手机本地时间");
          if(showProgress != null && showProgress.isShowing()){
            showProgress.dismiss();
          }
          break;
        default:
          break;
      }
    }
  };


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(!CommonUntils.hasPermission()){
      Toast.makeText(this,"当前用户无授权，无法进入本页面",Toast.LENGTH_SHORT).show();
      finish();
    }
    setContentView(R.layout.activity_post_course);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    if(getIntent() != null){
      if(getIntent().getBooleanExtra("exception",false)){
        //exception
      }
    }
    initView();
    setListener();
    initData();
  }

  private void initView() {
    showProgress = new ShowProgress(this);
    mBtnLogin = (Button) findViewById(R.id.btn_login);
    mEditUid = (EditText) findViewById(R.id.edit_uid);
    mEditPsw = (EditText) findViewById(R.id.edit_psw);
    mBtnClearUid = (ImageView) findViewById(R.id.img_login_clear_uid);
    mBtnClearPsw = (ImageView) findViewById(R.id.img_login_clear_psw);
    mTgBtnShowPsw = (ToggleButton) findViewById(R.id.tgbtn_show_psw);
    cbRemberPwd = (CheckBox) findViewById(R.id.cb_rember_pwd);
  }

  private void setListener() {
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

    cbRemberPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          PreferenceHelper.getInstance().setRemberPwd(true);
        } else {
          PreferenceHelper.getInstance().setRemberPwd(false);
        }
      }
    });
  }

  private void initData () {
    if(PreferenceHelper.getInstance().getRemberPwd()){
      cbRemberPwd.setChecked(true);
      mEditUid.setText(PreferenceHelper.getInstance().getSijiaoUid());
      mEditPsw.setText(PreferenceHelper.getInstance().getSijiaoPwd());
    }else {
      cbRemberPwd.setChecked(false);
      mEditUid.setText("");
      mEditPsw.setText("");
    }
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

  /**
   * 清空控件文本
   */
  private void clearText(EditText edit) {
    edit.setText("");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_login:	//登录
        startToLogin();
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.img_login_clear_uid:	//清除用户名
        clearText(mEditUid);
        break;
      case R.id.img_login_clear_psw:	//清除密码
        clearText(mEditPsw);
        break;
      default:
        break;
    }
  }

  private void startToLogin() {
    sijiaoUid = mEditUid.getText().toString();
    sijiaoPwd = mEditPsw.getText().toString();
    Message msg = mHandler.obtainMessage(START_TO_LOGIN);
    msg.sendToTarget();
  }

  private void getAccountToken() {

    String url = "http://ssl.healthmall.cn/data/app/token/accessToken.do";
    JsonObject job = new JsonObject();
    job.addProperty("userPassword", CommonUntils.md5(sijiaoPwd));
    job.addProperty("grantType","app_credential");
    job.addProperty("userName",sijiaoUid);
    job.addProperty("thirdLoginType","0");

    FormBody body = new FormBody.Builder()
        .add("data",job.toString())
        .build();

    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,1000);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        //获取token成功之后Log.e("test","response = " + response.body().toString());
        try {
          Gson gson = new Gson();
          TokenModel tokenmodel = gson.fromJson(response.body().charStream(), TokenModel.class);
          response.body().close();
          if(tokenmodel.getData() == null){
            //一般是用户名或者密码错误
            Log.e("test","message = " + tokenmodel.getMsg());
            errmsg = tokenmodel.getMsg();
            if(errmsg.contains("密码")){
              mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,1000);
            }else {
              //请求失效
              mHandler.sendEmptyMessageDelayed(REQUEST_INVAILED,1000);
            }
          } else {
            //更新小号昵称
            accessToken = tokenmodel.getData().getAccessToken();
            Message msg = mHandler.obtainMessage(GET_TOKEN_SUCCESS);
            msg.sendToTarget();
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,1000);
        }
      }
    });
  }

  private void getUserModel(final String uid) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("whichFunc","GETUSERMODEL");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_USER_MODEL_FAILED,1000);
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          Gson gson = new Gson();
          GetUserModel getUserModel = gson.fromJson(response.body().charStream(), GetUserModel.class);
          response.body().close();
          //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
          //获取成功之后
          if(getUserModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_USER_MODEL_SUCCES);
            msg.obj = getUserModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_USER_MODEL_FAILED,1000);
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_USER_MODEL_FAILED,1000);
        }

      }
    });
  }
}
