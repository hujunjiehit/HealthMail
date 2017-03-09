package com.june.healthmail.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.view.ChoosePayOptionsPopwindow;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class FukuanActivity extends Activity implements View.OnClickListener{

  private Button btn_start;
  private TextView tvShowResult;

  private ArrayList<AccountInfo> accountList = new ArrayList<>();

  private Boolean isRunning = false;
  private int offset;

  private static final int START_TO_FU_KUAN = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int GET_TOKEN_FAILED = 3;
  private static final int USER_PWD_WRONG = 4;
  private static final int START_TO_GET_USERINFO = 5;
  private static final int GET_USERINFO_SUCCESS = 6;
  private static final int GET_USERINFO_FAILED = 7;

  private int accountIndex = 0;

  private Message message;
  private String accessToken;
  private int min_time;
  private int max_time;
  private String errmsg;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_FU_KUAN:
          if (isRunning) {
            if (accountIndex < accountList.size()) {
              showTheResult("开始付款第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
              if (accountList.get(accountIndex).getStatus() == 1) {
                getAccountToken();
              } else {
                showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                accountIndex++;
                message = this.obtainMessage(START_TO_FU_KUAN);
                message.sendToTarget();
              }
            } else {
              showTheResult("******所有账号约课结束**********\n");
              isRunning = false;
              btn_start.setText("约课完成");
            }
          } else {
            showTheResult("**用户自己终止约课**当前已经执行完成" + accountIndex + "个小号\n");
          }
          break;
        case GET_TOKEN_SUCCESS:
          showTheResult("--获取token成功\n");
          message = this.obtainMessage(START_TO_GET_USERINFO);
          message.sendToTarget();
          break;
        case START_TO_GET_USERINFO:
          showTheResult("----获取个人信息：");
          getUserInfo();
          break;
        case GET_USERINFO_SUCCESS:
          showTheResult("----获取个人信息：");
          getUserInfo();
          break;
        case GET_TOKEN_FAILED:
          showTheResult("---获取token失败，重新开始该小号\n");
          this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
          break;
        case USER_PWD_WRONG:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
          break;
        default:
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fukuan);
    initView();
    setListener();
    initData();
  }



  private void initView() {
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
  }

  private void initData() {
    accountList.clear();
    SQLiteDatabase db = DBManager.getInstance(this).getDb();
    Cursor cursor = db.rawQuery("select * from account",null);
    if(cursor.moveToFirst()){
      do {
        AccountInfo info = new AccountInfo();
        info.setPassWord(cursor.getString(cursor.getColumnIndex("passWord")));
        info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        info.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
        info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        info.setId(cursor.getInt(cursor.getColumnIndex("id")));
        accountList.add(info);
      }while(cursor.moveToNext());
    }

    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_start:
        if("付款完成".equals(btn_start.getText().toString().trim())){
          Toast.makeText(this,"付款已完成，如需继续付款请重新进入本页面",Toast.LENGTH_LONG).show();
        }else {
          if (isRunning == false) {
            isRunning = true;
            btn_start.setText("停止付款");
            startToFuKuan();
          } else {
            isRunning = false;
            mHandler.removeCallbacksAndMessages(null);
            btn_start.setText("开始付款");
          }
        }
        break;
      case R.id.btn_fukuan_kuaijie://快捷支付
        Log.e("test","click btn_fukuan_kuaijie");
        break;
      case R.id.btn_fukuan_kuaiqian://块钱支付
        Log.e("test","click btn_fukuan_kuaiqian");
        break;
      case R.id.btn_fukuan_jingdong://京东支付
        Log.e("test","click btn_fukuan_jingdong");
        break;
      case R.id.btn_fukuan_tonglian://通联支付
        Log.e("test","click btn_fukuan_tonglian");
        break;
      case R.id.btn_fukuan_yilian://易联支付
        Log.e("test","click btn_fukuan_yilian");
        break;
      default:
        break;
    }
  }

  private void getAccountToken() {
    String url = "http://ssl.healthmall.cn/data/app/token/accessToken.do";
    JsonObject job = new JsonObject();
    job.addProperty("userPassword", CommonUntils.md5(accountList.get(accountIndex).getPassWord()));
    job.addProperty("grantType","app_credential");
    job.addProperty("userName",accountList.get(accountIndex).getPhoneNumber());
    job.addProperty("thirdLoginType","0");

    FormBody body = new FormBody.Builder()
        .add("data",job.toString())
        .build();

    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        //获取token成功之后Log.e("test","response = " + response.body().toString());
        Gson gson = new Gson();
        TokenModel tokenmodel = gson.fromJson(response.body().charStream(), TokenModel.class);

        if(tokenmodel.getData() == null){
          //一般是用户名或者密码错误
          Log.e("test","message = " + tokenmodel.getMsg());
          errmsg = tokenmodel.getMsg();
          DBManager.getInstance(FukuanActivity.this).setPwdInvailed(accountList.get(accountIndex).getPhoneNumber());
          mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,getDelayTime());
        } else {
          //更新小号昵称
          DBManager.getInstance(FukuanActivity.this).updateNickName(accountList.get(accountIndex).getPhoneNumber(),
              tokenmodel.getData().getHmMemberUserVo().getNickName());
          accessToken = tokenmodel.getData().getAccessToken();
          Message msg = mHandler.obtainMessage(GET_TOKEN_SUCCESS);
          msg.sendToTarget();
        }
      }
    });
  }

  private void getUserInfo() {
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
        //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        Gson gson = new Gson();
        GetUserModel getUserModel = gson.fromJson(response.body().charStream(), GetUserModel.class);
        //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
        //获取成功之后
        if(getUserModel.isSucceed()){
          Message msg = mHandler.obtainMessage(GET_USERINFO_SUCCESS);
          msg.obj = getUserModel;
          msg.sendToTarget();
        }else{
          //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
        }
      }
    });
  }

  private void startToFuKuan() {
    Message msg = mHandler.obtainMessage(START_TO_FU_KUAN);
    msg.sendToTarget();

    //--------------
    int[] data = {1,1,1,1,1};
    //显示popupwindow
    ChoosePayOptionsPopwindow popwindow = new ChoosePayOptionsPopwindow(this,data,this);
    popwindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.alpha = 0.5f;
    getWindow().setAttributes(lp);
    popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha=1f;
        getWindow().setAttributes(lp);
      }
    });
  }

  private int getDelayTime() {
    int randTime = CommonUntils.getRandomInt(min_time,max_time);
    Log.d("test","randTime = " + randTime);
    return randTime;
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }
  private void toast(String msg){
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
  }
}
