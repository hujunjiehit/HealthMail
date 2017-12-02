package com.june.healthmail.improve.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.improve.activity.NewPingjiaActivity;
import com.june.healthmail.model.Order;
import com.june.healthmail.model.OrdersModel;
import com.june.healthmail.model.PingjiaModel;
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

public class PingjiaService extends BaseService {


  private static final int START_TO_PING_JIA = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int START_TO_GET_ORDER_LIST = 3;
  private static final int GET_ORDER_LIST_SUCCESS = 4;
  private static final int START_TO_PING_JIA_ONE_COURSE = 5;
  private static final int PING_JIA_ONE_COURSE_SUCCESS = 6;
  private static final int PING_JIA_ONE_COURSE_FAILED = 7;
  private static final int GET_ORDER_LIST_FAILED = 8;
  private static final int GET_TOKEN_FAILED = 9;
  private static final int USER_PWD_WRONG = 10;
  private static final int REQUEST_INVAILED = 11;
  private static final int TASK_FINISHED = 12;


  private PingjiaBinder mBinder = new PingjiaBinder();
  private int pageIndex = 0;
  private int courseIndex = 0;
  private OrdersModel ordersModel;
  private PingjiaModel pingjiaModel;
  private String pingWord;
  private ArrayList<Order> coureseList = new ArrayList<>();

  private NotificationCompat.Builder mNotifyBuilder;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_PING_JIA:
          if(isRunning) {
            if (accountIndex < accountList.size()) {
              if (PreferenceHelper.getInstance().getRemainPingjiaTimes() <= 0) {
                showTheResult("剩余评价次数不足，请先充值");
                toast("剩余评价次数不足，请先充值");
                isRunning = false;
                finishPingjia();
                //btn_start.setText("评价完成");
                return;
              }

              mNotifyBuilder.setContentText("正在评价第" + (accountIndex + 1) + "个号...");
              mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
              startForeground(1, mNotifyBuilder.build());

              showTheResult("开始评价第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
              if (accountList.get(accountIndex).getStatus() == 1) {
                getAccountToken();
              } else {
                showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                accountIndex++;
                message = this.obtainMessage(START_TO_PING_JIA);
                message.sendToTarget();
              }
            } else {
              showTheResult("******所有账号评价结束**********\n");
              isRunning = false;
              finishPingjia();
              updateUserInfo();

              mNotifyBuilder.setContentText("所有勾选的账号评价完成...");
              mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
              startForeground(1, mNotifyBuilder.build());
              //btn_start.setText("评价完成");
            }
          } else {
            showTheResult("**用户自己终止评价**当前已经执行完成"+ accountIndex + "个小号\n");
            updateUserInfo();
          }
          break;
        case GET_TOKEN_SUCCESS:
          showTheResult("--获取token成功\n");
          pageIndex = 0;
          message = this.obtainMessage(START_TO_GET_ORDER_LIST);
          message.sendToTarget();
          break;

        case START_TO_GET_ORDER_LIST:
          if (pageIndex < 5) {
            showTheResult("----开始获取第"+ (pageIndex + 1) + "页订单列表:");
            getOrderList();
          } else {
            accountIndex++;
            showTheResult("此账号评价结束************************\n\n\n");
            message = this.obtainMessage(START_TO_PING_JIA);
            message.sendToTarget();
          }
          break;

        case GET_ORDER_LIST_SUCCESS:
          showTheResult("订单列表获取成功\n");
          //保存可以评价的课程列表
          courseIndex = 0;
          coureseList.clear();
          if(ordersModel.getValuse() != null){
            boolean needPingjia = false;
            for(int i = 0; i < ordersModel.getValuse().size(); i++){
              coureseList.add(ordersModel.getValuse().get(i));
              if(ordersModel.getValuse().get(i).getHm_go_orderstatus() == 9){
                needPingjia = true;
              }
            }
            if(needPingjia){
              //有待评价的课程
              message = this.obtainMessage(START_TO_PING_JIA_ONE_COURSE);
              message.sendToTarget();
            }else {
              showTheResult("------当前页无可评价订单\n");
              if(coureseList.size() < 20){
                showTheResult("******第" + (pageIndex +1) + "页订单小于20，继续评价下一个小号\n");
                showTheResult("此账号评价结束************************\n\n\n");
                accountIndex++;
                message = this.obtainMessage(START_TO_PING_JIA);
                message.sendToTarget();
              }else {
                pageIndex++;
                message = this.obtainMessage(START_TO_GET_ORDER_LIST);
                message.sendToTarget();
              }
            }
          }else {
            showTheResult("--------订单列表为空，重新获取\n");
            this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
          }
          break;

        case START_TO_PING_JIA_ONE_COURSE:
          if(courseIndex < coureseList.size()){
            showTheResult("-----------开始评价第"+ (courseIndex + 1) + "节课程[" +
                coureseList.get(courseIndex).getHm_go_orderstatus() + "]:");
            if(coureseList.get(courseIndex).getHm_go_orderstatus() != 9){
              showTheResult("无需评价\n");
              courseIndex++;
              message = this.obtainMessage(START_TO_PING_JIA_ONE_COURSE);
              message.sendToTarget();
            }else{
              pingjiaTheCourse(coureseList.get(courseIndex).getGrouporder_id());
            }
          } else {
            //需要判断是继续获取订单还是开始下一个小号
            if(coureseList.size() < 20){
              showTheResult("******第" + (pageIndex +1) + "页订单小于20，继续评价下一个小号\n");
              showTheResult("此账号评价结束************************\n\n\n");
              accountIndex++;
              message = this.obtainMessage(START_TO_PING_JIA);
              message.sendToTarget();
            }else {
              pageIndex++;
              message = this.obtainMessage(START_TO_GET_ORDER_LIST);
              message.sendToTarget();
            }
          }
          break;

        case PING_JIA_ONE_COURSE_SUCCESS:
          showTheResult("评价成功\n");
          courseIndex++;
          CommonUntils.minusPingjiaTimes();
          updateTimes(PreferenceHelper.getInstance().getRemainPingjiaTimes());
          this.sendEmptyMessageDelayed(START_TO_PING_JIA_ONE_COURSE,getDelayTime());
          break;

        case PING_JIA_ONE_COURSE_FAILED:
          showTheResult("评价失败，继续尝试 错误信息:"+errmsg+"\n");
          this.sendEmptyMessageDelayed(START_TO_PING_JIA_ONE_COURSE,getDelayTime());
          break;
        case GET_ORDER_LIST_FAILED:
          showTheResult("订单列表获取失败，继续尝试\n");
          this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
          break;

        case GET_TOKEN_FAILED:
          showTheResult("--获取token失败，重新开始该小号\n");
          this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
          break;
        case USER_PWD_WRONG:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
          break;
        case REQUEST_INVAILED:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***请求失效，小号管理标记为绿色，继续下一个(手机时间不准确，请设置手机时间为自动时间)****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
          break;
        case TASK_FINISHED:
          Log.e("test","task finished");
          mHandler = null;
          break;
        default:
          Log.e("test","undefined message");
          break;
      }
    }
  };

  @Override
  public void onCreate() {
    super.onCreate();

    Intent notificationIntent = new Intent(this, NewPingjiaActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    mNotifyBuilder = new NotificationCompat.Builder(this)
        .setContentTitle("猫友圈评价")
        .setContentText("点击查看评价详情...")
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.login_dog)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_dog))
        .setContentIntent(pendingIntent);
    startForeground(1, mNotifyBuilder.build());

    Log.e("test", "PingjiaService onCreate");
    initData();
  }

  private void initData() {
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    accountList = CommonUntils.loadAccountInfo();
    min_time = PreferenceHelper.getInstance().getMinPingjiaTime();
    max_time = PreferenceHelper.getInstance().getMaxPingjiaTime();
  }

  @Override
  public IBinder onBind(Intent intent) {
    // TODO: Return the communication channel to the service.
    Log.e("test", "onBind");
    return mBinder;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.e("test", "PingjiaService onDestroy");
    updateUserInfo();
    release();
  }

  @Override
  protected void release() {
    super.release();
    mBinder = null;
    coureseList = null;
    ordersModel = null;
    pingjiaModel = null;
    mHandler.removeCallbacksAndMessages(null);
    mHandler.sendEmptyMessage(TASK_FINISHED);
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
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        //获取token成功之后Log.e("test","response = " + response.body().toString());
        try{
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
            mHandler.sendEmptyMessageDelayed(GET_TOKEN_SUCCESS,getDelayTime());
          }
        }catch (Exception e){
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void getOrderList() {

    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("count","20");
    job.addProperty("whichFunc","Getorderlist");
    job.addProperty("type","ALL");
    job.addProperty("page",pageIndex+1);
    job.addProperty("Which","user");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();

    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          ordersModel = gson.fromJson(response.body().charStream(), OrdersModel.class);
          response.body().close();
          //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
          if(ordersModel.isSucceed()){
            //获取成功之后
            Message msg = mHandler.obtainMessage(GET_ORDER_LIST_SUCCESS);
            msg.sendToTarget();
          }else {
            mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
          }
        }catch (Exception e){
          Log.e("test","Exception:" + e.toString());
          e.printStackTrace();
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void pingjiaTheCourse(String hm_orderid) {

    String url = "http://api.healthmall.cn/Post";

    // {
    // "model":
    //      {
    //         "hm_orderid":"go170228213618603667",
    //         "hm_ptc_content":"非常感谢教练的指导",
    //         "hm_ptc_score":5
    //      },
    //  "type":"GBC",
    //  "whichFunc":"IN_PTCOMMENT"
    // }

    JsonObject modelJob  = new JsonObject();
    modelJob.addProperty("hm_orderid",hm_orderid);
    modelJob.addProperty("hm_ptc_content",pingWord.trim());
    modelJob.addProperty("hm_ptc_score",5);

    JsonObject job = new JsonObject();
    job.add("model",modelJob);
    job.addProperty("type","GBC");
    job.addProperty("whichFunc","IN_PTCOMMENT");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();
    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          pingjiaModel = gson.fromJson(response.body().charStream(), PingjiaModel.class);
          response.body().close();
          Log.e("test","succeed = " + pingjiaModel.isSucceed());
          if(pingjiaModel.isSucceed()){
            accountList.get(accountIndex).setPingjiaTimes(accountList.get(accountIndex).getPingjiaTimes() + 1);
            DBManager.getInstance().updatePingjiaTimes(accountList.get(accountIndex));
            mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_SUCCESS,getDelayTime());
          } else {
            errmsg = pingjiaModel.getErrmsg();
            mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
          }
        }catch (Exception e){
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
          }
        }
      }
    });
  }



  public class PingjiaBinder extends Binder {

    public void startPingjia() {
      isRunning = true;
      Message msg = mHandler.obtainMessage(START_TO_PING_JIA);
      msg.sendToTarget();
    }

    public void stopPingjia() {
      isRunning = false;
      if(mHandler != null) {
        mHandler.removeCallbacksAndMessages(null);
      }
    }

    public void setHandler(Handler handler) {
      mActivityHandler = handler;
    }

    public void setPingjiaWord(String word) {
      Log.e("test", "setPingjiaWords execute,words =  " + word);
      pingWord = word;
    }
  }
}
