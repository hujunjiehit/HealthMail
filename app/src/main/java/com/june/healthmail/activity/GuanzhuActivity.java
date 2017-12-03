package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.Course;
import com.june.healthmail.model.CourseDetail;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.Guanzhu;
import com.june.healthmail.model.GuanzhuListModel;
import com.june.healthmail.model.PostGuanzhuModel;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;

import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/7/5.
 */

public class GuanzhuActivity extends BaseActivity implements View.OnClickListener{

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvRemainTimes;
  private UserInfo userInfo;

  private String targetNumber;
  private boolean isTargetExist;

  private ArrayList<AccountInfo> accountList = new ArrayList<>();

  private Boolean isRunning = false;
  private String mallId;

  private int offset;

  private static final int START_TO_GUAN_ZHU = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int START_TO_GET_GUANZHU_LIST = 3;
  private static final int GET_GUANZHU_LIST_SUCCESS = 4;
  private static final int DO_THE_ACTION_GUANZHU = 5;

  private static final int GUANZHU_SUCESS = 6;
  private static final int GUANZHU_FAILED = 7;

  private static final int GET_USER_MODEL_SUCCES = 8;
  private static final int GET_USER_MODEL_FAILED = 9;

  private static final int GET_COURSE_DETAILS_SUCESS = 10;
  private static final int POST_YUE_KE_APPLAY = 11;
  private static final int YUE_KE_SUCESS = 12;
  private static final int YUE_KE_FAILED = 13;
  private static final int GET_TOKEN_FAILED = 14;
  private static final int GET_GUANZHU_LIST_FAILED = 15;
  private static final int GET_COURSE_LIST_FAILED = 16;
  private static final int GET_COURSE_USERS_FAILED = 17;
  private static final int GET_COURSE_DETAILS_FAILED = 18;
  private static final int USER_PWD_WRONG = 19;
  private static final int REQUEST_INVAILED = 20;

  private int accountIndex = 0;
  private int sijiaoIndex = 0;
  private int courseIndex = 0;
  private String accessToken;

  private Message message;

  private ArrayList<Guanzhu> guanzhuList = new ArrayList<>();
  private ArrayList<Course> coureseList = new ArrayList<>();
  private CourseDetail currentCourseDetail;


  private static final int DEELAY_TIME = 1000;
  private int min_time;
  private int max_time;
  private String errmsg;

  private TextView tvShowNumber;
  private Button btnEditNumber;

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_GUAN_ZHU:
          if(isRunning) {
            if (accountIndex < accountList.size()) {
              showTheResult("\n\n第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "开始关注\n");
              if (accountList.get(accountIndex).getStatus() == 1) {
                getAccountToken();
              } else {
                showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                accountIndex++;
                message = this.obtainMessage(START_TO_GUAN_ZHU);
                message.sendToTarget();
              }
            } else {
              showTheResult("******所有账号收藏结束**********\n");
              isRunning = false;
              btn_start.setText("收藏完成");
            }
          } else {
            showTheResult("**用户自己终止收藏进展**当前已经执行完成"+ accountIndex + "个小号\n");
          }
          break;
        case GET_TOKEN_SUCCESS:
          showTheResult("--获取token成功\n");
          if(!isTargetExist){
            showTheResult("#########开始验证猫号\n");
            getUserModel(targetNumber.trim());
          }else {
            message = this.obtainMessage(START_TO_GET_GUANZHU_LIST);
            message.sendToTarget();
          }
          break;
        case START_TO_GET_GUANZHU_LIST:
          showTheResult("----开始获取收藏列表:");
          getTheGuanzhuList();
          break;

        case GET_GUANZHU_LIST_SUCCESS:
          showTheResult("收藏列表获取成功\n");
          //保存关注列表--私教列表
          sijiaoIndex = 0;
          guanzhuList.clear();
          GuanzhuListModel guanzhuListModel = (GuanzhuListModel)msg.obj;
          boolean targetInTheList = false;
          if(guanzhuListModel.getValuse() != null) {
            for (int i = 0; i < guanzhuListModel.getValuse().size(); i++) {
              guanzhuList.add(guanzhuListModel.getValuse().get(i));
            }
            showTheResult("*************当前小号收藏了" + guanzhuList.size() + "个私教\n");
            for(Guanzhu guanzhu:guanzhuList){
              if(targetNumber.equals(guanzhu.getUser_id())){
                targetInTheList = true;
              }
            }
          }else {
            //关注列表为空
            showTheResult("***********收藏列表为空\n");
          }
          if(targetInTheList) {
            showTheResult("***************已经收藏目标私教，继续下一个小号\n");
            accountIndex++;
            message = this.obtainMessage(START_TO_GUAN_ZHU);
            message.sendToTarget();
          } else {
            showTheResult("***************未收藏目标私教，开始收藏\n");
            message = this.obtainMessage(DO_THE_ACTION_GUANZHU);
            message.sendToTarget();
          }
          break;
        case DO_THE_ACTION_GUANZHU:
          showTheResult("------------------------------发送收藏申请\n");
          postGuanzhuApply();
          break;
        case GUANZHU_SUCESS:
          showTheResult("----------------------------------收藏成功\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_GUAN_ZHU,getDelayTime());
          break;
        case GUANZHU_FAILED:
          showTheResult("----------------------------------收藏失败，继续尝试\n");
          showTheResult("***************请看清楚红色的字，需要输入目标私教的猫号(不是手机号)\n");
          this.sendEmptyMessageDelayed(DO_THE_ACTION_GUANZHU,getDelayTime());
          break;
        case GET_TOKEN_FAILED:
          showTheResult("--获取token失败，重新开始该小号\n");
          this.sendEmptyMessageDelayed(START_TO_GUAN_ZHU,getDelayTime());
          break;
        case USER_PWD_WRONG:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_GUAN_ZHU,getDelayTime());
          break;
        case REQUEST_INVAILED:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_GUAN_ZHU,getDelayTime());
          break;
        case GET_GUANZHU_LIST_FAILED:
          showTheResult("--获取收藏列表失败，重新获取收藏列表\n");
          this.sendEmptyMessageDelayed(START_TO_GET_GUANZHU_LIST,getDelayTime());
          break;
        case GET_USER_MODEL_SUCCES:
          isTargetExist = true;
          PreferenceHelper.getInstance().setIsTargetExist(true);
          showTheResult("-------猫号验证成功\n");
          message = this.obtainMessage(START_TO_GET_GUANZHU_LIST);
          message.sendToTarget();
          break;
        case GET_USER_MODEL_FAILED:
          showTheResult("-------猫号验证失败，请检查猫号是否正确\n");
          toast("获取私教信息失败，请检查猫号是否正确");
          break;
        default:
          Log.e("test","undefined message");
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
    setContentView(R.layout.activity_guanzhu);
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

  @Override
  protected void onResume() {
    super.onResume();
    //setupSpotAd();
  }

  private void initView() {
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvShowNumber = (TextView) findViewById(R.id.tv_show_number);
    btnEditNumber = (Button) findViewById(R.id.btn_edit_number);
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    btnEditNumber.setOnClickListener(this);
  }

  private void initData() {
    accountList.clear();

    tvShowNumber.setText(PreferenceHelper.getInstance().getTargetNumber());
    targetNumber = PreferenceHelper.getInstance().getTargetNumber().trim();
    isTargetExist = PreferenceHelper.getInstance().getIsTargetExist();

    SQLiteDatabase db = DBManager.getInstance().getDb();
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
    cursor.close();
    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
  }

  private int getDelayTime() {
    int randTime = CommonUntils.getRandomInt(min_time,max_time);
    Log.d("test","randTime = " + randTime);
    return randTime;
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_start:
        if("收藏完成".equals(btn_start.getText().toString().trim())){
          Toast.makeText(this,"收藏已完成，如需继续收藏请重新进入本页面",Toast.LENGTH_LONG).show();
        }else {
          if (isRunning == false) {
            isRunning = true;
            btn_start.setText("停止收藏");
            startToGuanzhu();
          } else {
            isRunning = false;
            mHandler.removeCallbacksAndMessages(null);
            btn_start.setText("开始收藏");
          }
        }

        break;
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.btn_edit_number:	//修改需要关注的私教猫号
        showEditNumberDialog();
        break;
      default:
        break;
    }
  }

  private void startToGuanzhu() {
    Message msg = mHandler.obtainMessage(START_TO_GUAN_ZHU);
    msg.sendToTarget();
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

    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
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
              DBManager.getInstance().setPwdInvailed(accountList.get(accountIndex).getPhoneNumber());
              mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,getDelayTime());
            }else {
              //请求失效
              DBManager.getInstance().setRequestInvailed(accountList.get(accountIndex).getPhoneNumber());
              mHandler.sendEmptyMessageDelayed(REQUEST_INVAILED,getDelayTime());
            }
          } else {
            //更新小号昵称
            DBManager.getInstance().updateUserInfo(accountList.get(accountIndex).getPhoneNumber(),
                tokenmodel.getData().getHmMemberUserVo());
            accessToken = tokenmodel.getData().getAccessToken();
            Message msg = mHandler.obtainMessage(GET_TOKEN_SUCCESS);
            msg.sendToTarget();
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
        }
      }
    });
  }

  private void getTheGuanzhuList() {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("count","20");
    job.addProperty("page",1);
    job.addProperty("whichFunc","UserConcerned");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();
    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          Gson gson = new Gson();
          GuanzhuListModel guanzhuListModel = gson.fromJson(response.body().charStream(), GuanzhuListModel.class);
          response.body().close();
          //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
          //获取成功之后
          if(guanzhuListModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_GUANZHU_LIST_SUCCESS);
            msg.obj = guanzhuListModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
        }
      }
    });
  }

  private void postGuanzhuApply() {

    String url = "http://api.healthmall.cn/Post";

    JsonObject job = new JsonObject();
    job.addProperty("id",targetNumber);
    job.addProperty("whichFunc","USERCONCERN");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GUANZHU_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();//java.lang.IllegalStateException
          PostGuanzhuModel postGuanzhuModel = gson.fromJson(response.body().charStream(), PostGuanzhuModel.class);
          response.body().close();
          //获取成功之后
          if(postGuanzhuModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GUANZHU_SUCESS);
            msg.sendToTarget();
          }else{
            Message msg = mHandler.obtainMessage(GUANZHU_FAILED);
            msg.sendToTarget();
          }
        }catch (Exception e){
          Message msg = mHandler.obtainMessage(GUANZHU_FAILED);
          msg.sendToTarget();
        }
      }
    });
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
  }

  private void showEditNumberDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_number,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改目标私教猫号");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        if(TextUtils.isEmpty(edit_text.getText().toString().trim())){
          return;
        }
        String result = edit_text.getText().toString().trim();
        if(result.length() <= 0) {
          toast("需要关注的私教猫号不能为空");
        }else {
          PreferenceHelper.getInstance().setTargetNumber(result.trim());
          tvShowNumber.setText(result + "");
          targetNumber = result.trim();
          isTargetExist = false;
          PreferenceHelper.getInstance().setIsTargetExist(false);
        }
      }
    });
    builder.create().show();
  }

  private void getUserModel(final String uid) {
    String url = "http://api.healthmall.cn/Post";

    JsonObject job = new JsonObject();
    job.addProperty("type",uid);
    job.addProperty("whichFunc","GETUSERMODEL");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_USER_MODEL_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();//java.lang.IllegalStateException
          GetUserModel getUserModel = gson.fromJson(response.body().charStream(), GetUserModel.class);
          response.body().close();
          //获取成功之后
          if(getUserModel.isSucceed()){
            targetNumber = uid;
            Message msg = mHandler.obtainMessage(GET_USER_MODEL_SUCCES);
            msg.sendToTarget();
          }else{
            Message msg = mHandler.obtainMessage(GET_USER_MODEL_FAILED);
            msg.sendToTarget();
          }
        }catch (Exception e){
          Message msg = mHandler.obtainMessage(GET_USER_MODEL_FAILED);
          msg.sendToTarget();
        }
      }
    });
  }
}
