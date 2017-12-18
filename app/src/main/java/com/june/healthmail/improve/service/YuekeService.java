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
import com.june.healthmail.improve.activity.NewYuekeActivity;
import com.june.healthmail.model.Course;
import com.june.healthmail.model.CourseDetail;
import com.june.healthmail.model.CourseDetailModel;
import com.june.healthmail.model.CourseListModel;
import com.june.healthmail.model.GroupbuyUser;
import com.june.healthmail.model.GroupbuyUserModel;
import com.june.healthmail.model.Guanzhu;
import com.june.healthmail.model.GuanzhuListModel;
import com.june.healthmail.model.PostYuekeModel;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.TimeUntils;
import com.june.healthmail.untils.Tools;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import cn.bmob.v3.BmobUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/8/19.
 */

public class YuekeService extends BaseService {

  private static final int START_TO_YUE_KE = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int START_TO_GET_GUANZHU_LIST = 3;
  private static final int GET_GUANZHU_LIST_SUCCESS = 4;
  private static final int START_TO_GET_COURSE_LIST = 5;
  private static final int GET_COURSE_LIST_SUCESS = 6;
  private static final int START_TO_GET_COURSE_USERS = 7;
  private static final int GET_COURSE_USERS_SUCESS = 8;
  private static final int START_TO_GET_COURSE_DETAILS = 9;
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
  private static final int TASK_FINISHED = 21;

  private int pageIndex = 0; //页索引 一页二十节课
  private int pageSize = 0;   //总页数
  private int max_courses = 50;

  private int per_sijiao_max_courses;  //每个私教最多约课数
  private int currentNum;  //每个私教当前约课数

  private int sijiaoIndex = 0;
  private int courseIndex = 0;
  private String mallId;

  private ArrayList<Guanzhu> guanzhuList = new ArrayList<>();
  private ArrayList<Course> coureseList = new ArrayList<>();
  private CourseDetail currentCourseDetail;

  private YukeBinder mBinder = new YukeBinder();

  private NotificationCompat.Builder mNotifyBuilder;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_YUE_KE:
          if (isRunning) {
            if (accountIndex < accountList.size()) {
              if (PreferenceHelper.getInstance().getRemainYuekeTimes() <= 0) {
                showTheResult("剩余约课次数不足，请先充值");
                toast("剩余约课次数不足，请先充值");
                isRunning = false;
                finishYueke();
                //btn_start.setText("约课完成");
                return;
              }

              mNotifyBuilder.setContentText("正在约第" + (accountIndex + 1) + "个号...");
              mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
              startForeground(1, mNotifyBuilder.build());

              showTheResult("开始约课第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
              if (accountList.get(accountIndex).getStatus() == 1) {
                getAccountToken();
              } else {
                showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                accountIndex++;
                message = this.obtainMessage(START_TO_YUE_KE);
                message.sendToTarget();
              }
            } else {
              showTheResult("******所有账号约课结束**********\n");
              isRunning = false;
              finishYueke();
              //btn_start.setText("约课完成");
              updateUserInfo();
              mNotifyBuilder.setContentText("所有勾选的账号约课完成...");
              mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
              startForeground(1, mNotifyBuilder.build());
            }
          } else {
            showTheResult("**用户自己终止约课**当前已经执行完成" + accountIndex + "个小号\n");
            updateUserInfo();
          }
          break;
        case GET_TOKEN_SUCCESS:
          showTheResult("--获取token成功\n");
          message = this.obtainMessage(START_TO_GET_GUANZHU_LIST);
          message.sendToTarget();
          break;

        case START_TO_GET_GUANZHU_LIST:
          showTheResult("----开始获取收藏列表:");
          getTheGuanzhuList();
          break;

        case GET_GUANZHU_LIST_SUCCESS:
          showTheResult("收藏列表获取成功\n");
          //保存关注列表--私教列表
          sijiaoIndex = 0;
          pageIndex = 0;
          guanzhuList.clear();

          GuanzhuListModel guanzhuListModel = (GuanzhuListModel) msg.obj;
          if (guanzhuListModel.getValuse() != null && guanzhuListModel.getValuse().size() > 0) {
            for (int i = 0; i < guanzhuListModel.getValuse().size(); i++) {
              guanzhuList.add(guanzhuListModel.getValuse().get(i));
            }
            mallId = guanzhuListModel.getValuse().get(0).getUser_id_fans();
            message = this.obtainMessage(START_TO_GET_COURSE_LIST);
            message.sendToTarget();
          } else {
            //关注列表为空
            showTheResult("!!!!没有收藏的私教，请先收藏要约课的私教(提示：软件有一键收藏的功能)\n");
            showTheResult("******当前小号未收藏任何私教，跳过，继续下一个小号\n\n\n");
            accountIndex++;
            message = this.obtainMessage(START_TO_YUE_KE);
            message.sendToTarget();
          }
          break;

        case START_TO_GET_COURSE_LIST:
          //传入私教id
          if (sijiaoIndex < guanzhuList.size()) {
            if (sijiaoIndex < max_sijiao) {
              showTheResult("**********用户设置一共" + pageSize + "页课程\n");
              if (pageIndex < pageSize) {//页数
                showTheResult("************开始获取私教[" + (sijiaoIndex + 1) + "]-" +
                    guanzhuList.get(sijiaoIndex).getHm_u_nickname_concerned() + "第" + (pageIndex + 1) + "页的课程\n");
                getTheCourseList(guanzhuList.get(sijiaoIndex).getUser_id());
              } else {
                showTheResult("**************没有更多页，开始下一个收藏的私教\n");
                pageIndex = 0;
                sijiaoIndex++;
                currentNum = 0;
                this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
              }
            } else {
              showTheResult("*******用户设置了最多只约" + max_sijiao + "个私教，开始下一个小号\n\n\n");
              accountIndex++;
              currentNum = 0;
              userInfo.setYuekeTimes(PreferenceHelper.getInstance().getRemainYuekeTimes());
              userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getRemainPingjiaTimes());
              message = this.obtainMessage(START_TO_YUE_KE);
              message.sendToTarget();
            }
          } else {
            showTheResult("*******所有收藏的私教课程都约完了，开始下一个小号\n\n\n");
            accountIndex++;
            currentNum = 0;
            userInfo.setYuekeTimes(PreferenceHelper.getInstance().getRemainYuekeTimes());
            userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getRemainPingjiaTimes());
            message = this.obtainMessage(START_TO_YUE_KE);
            message.sendToTarget();
          }
          break;
        case GET_COURSE_LIST_SUCESS:
          courseIndex = 0;
          coureseList.clear();

          CourseListModel courseListModel = (CourseListModel) msg.obj;

          if (courseListModel.getValuse() != null) {
            for (int i = 0; i < courseListModel.getValuse().size(); i++) {
              coureseList.add(courseListModel.getValuse().get(i));
              if(PreferenceHelper.getInstance().getSortCourse()) {
                //需要排序
                Collections.sort(coureseList);
              }

            }
            showTheResult("私教" + (sijiaoIndex + 1) + "第" + (pageIndex + 1) + "页有" + coureseList.size() + "节课程\n");
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
          } else {
            showTheResult("私教" + (sijiaoIndex + 1) + "暂时没有发布课程，继续下一个私教\n");
            sijiaoIndex++;
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
          }
          break;

        case START_TO_GET_COURSE_USERS:
          if (isRunning) {
            if (courseIndex < coureseList.size()) {
              Log.e("test","courseIndex = " + courseIndex + "    currentNum = " + currentNum);
              if (currentNum < per_sijiao_max_courses) {
                if (isOutofDate(coureseList.get(courseIndex))) {
                  //上课时间是否过了
                  showTheResult("--第" + (courseIndex + 1) + "节课上课时间过了，跳过，开始下一节课\n");
                  courseIndex++;
                  currentNum++;
                  this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
                } else if (PreferenceHelper.getInstance().getOnlyToday() &&
                    !Tools.isToday(coureseList.get(courseIndex).getHm_gbc_date())) {
                  //只约今天的课
                  showTheResult("-----第" + (courseIndex + 1) + "节课不是今天的课，跳过，开始下一节课\n");
                  courseIndex++;
                  currentNum++;
                  this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
                } else {
                  showTheResult("----第" + (courseIndex + 1) + "节课时间："+ coureseList.get(courseIndex).getHm_gbc_time() + "\n");
                  showTheResult("-------获取第" + (courseIndex + 1) + "节课程的约课名单\n");
                  getCourseUsers(coureseList.get(courseIndex).getGroupbuy_id());
                }
              }else {
                showTheResult("-----用户设置每个私教最多约" + per_sijiao_max_courses + "节课，继续下一个私教\n");
                sijiaoIndex++;
                pageIndex = 0;
                currentNum = 0;
                this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
              }
            } else {
              if (coureseList.size() < 20) {
                showTheResult("******第" + (pageIndex + 1) + "页私教课程小于20，继续下一个私教\n");
                sijiaoIndex++;
                pageIndex = 0;
                currentNum = 0;
                this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
              } else {
                pageIndex++;
                this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
              }
            }
          } else {
            showTheResult("**用户自己终止约课**当前已经执行完成" + accountIndex + "个小号\n");
          }
          break;
        case GET_COURSE_USERS_SUCESS:
          if(isRunning == false) {
            return;
          }
          GroupbuyUserModel groupbuyUserModel = (GroupbuyUserModel) msg.obj;
          boolean isIntheList = false;
          if (groupbuyUserModel.getValuse() != null) {
            //String mallId = groupbuyUserModel.getAccessToken().getMallId();
            if (mallId != null) {
              for (GroupbuyUser groupbuyUser : groupbuyUserModel.getValuse()) {
                if (mallId.equals(groupbuyUser.getUser_id())) {
                  isIntheList = true;
                }
              }
            }
            if (isIntheList == true) {
              showTheResult("--------------经约过课了\n");
              courseIndex++;
              currentNum++;
              this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
            } else if (groupbuyUserModel.getValuse().size() >= max_courses) {
              if (userInfo.getUserType() == 3) {
                showTheResult("-------------课程已经约满了(上课人数已达补贴上限"+max_courses+"人)\n");
              } else {
                showTheResult("******当前课程上课人数已达限制" + max_courses + "人\n");
                showTheResult("********请升级上课人数上限，当前每节课上课人数不能超过" + max_courses + "人(高级永久用户无此限制)\n");
              }
              courseIndex++;
              currentNum++;
              this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
            } else {
              this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS, getDelayTime());
            }
          } else {
            showTheResult("---------约课名单为空，重新获取\n");
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
          }
          break;

        case START_TO_GET_COURSE_DETAILS:
          if(isRunning == false) {
            return;
          }
          showTheResult("------------------可以约课-获取课程详情\n");
          if (courseIndex < coureseList.size()) {
            getCourseDetails(coureseList.get(courseIndex).getGroupbuy_id());
          }
          break;
        case GET_COURSE_DETAILS_SUCESS:
          if(isRunning == false) {
            return;
          }
          CourseDetailModel courseDetailModel = (CourseDetailModel) msg.obj;
          if (courseDetailModel.getValuse() == null) {
            showTheResult("-------------课程详情有误，重新获取\n");
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS, getDelayTime());
          } else {
            if (courseDetailModel.getValuse().getHm_gbc_currnum() < courseDetailModel.getValuse().getHm_gbc_maxnum()
                && courseDetailModel.getValuse().getHm_gbc_status() == 1) {
              currentCourseDetail = courseDetailModel.getValuse();
              this.sendEmptyMessageDelayed(POST_YUE_KE_APPLAY, getDelayTime());
            } else {
              showTheResult("-------------课程已经约满了(上课人数已经达到了发布课程时设置的上限"+courseDetailModel.getValuse().getHm_gbc_maxnum()+"人)\n");
              courseIndex++;
              currentNum++;
              this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
            }
          }
          break;
        case POST_YUE_KE_APPLAY:
          showTheResult("----------------------发送约课申请\n");
          postYuekeApplay();
          break;
        case YUE_KE_SUCESS:
          showTheResult("--------------------------约课成功\n");
          courseIndex++;
          currentNum++;
          CommonUntils.minusYuekeTimes();
          updateTimes(PreferenceHelper.getInstance().getRemainYuekeTimes());
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
          break;
        case YUE_KE_FAILED:
          showTheResult("---------------------------约课失败\n");
          //courseIndex++;
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
          break;
        case GET_TOKEN_FAILED:
          showTheResult("--获取token失败，重新开始该小号\n");
          this.sendEmptyMessageDelayed(START_TO_YUE_KE, getDelayTime());
          break;
        case USER_PWD_WRONG:
          showTheResult("***错误信息：" + errmsg + "\n");
          showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_YUE_KE, getDelayTime());
          break;
        case REQUEST_INVAILED:
          showTheResult("***错误信息：" + errmsg + "\n");
          showTheResult("***请求失效，小号管理标记为绿色，继续下一个(手机时间不准确，请设置手机时间为自动时间)****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_YUE_KE, getDelayTime());
          break;
        case GET_GUANZHU_LIST_FAILED:
          showTheResult("--获取关注列表失败，重新获取关注列表\n");
          this.sendEmptyMessageDelayed(START_TO_GET_GUANZHU_LIST, getDelayTime());
          break;
        case GET_COURSE_LIST_FAILED:
          showTheResult("--获取私教课程列表失败，重新获取该私教课程列表\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
          break;
        case GET_COURSE_USERS_FAILED:
          showTheResult("--获取课程约课名单失败，重新获取\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_USERS, getDelayTime());
          break;
        case GET_COURSE_DETAILS_FAILED:
          showTheResult("--获取课程详情失败，重新获取\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS, getDelayTime());
          break;
        case TASK_FINISHED:
          Log.e("test","task finished");
          mHandler = null;
          break;
        default:
          Log.e("test", "undefined message");
          break;
      }
    }
  };

  @Override
  public void onCreate() {
    super.onCreate();
    Intent notificationIntent = new Intent(this, NewYuekeActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    mNotifyBuilder = new NotificationCompat.Builder(this)
        .setContentTitle("猫友圈约课")
        .setContentText("点击查看约课详情...")
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.login_dog)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_dog))
        .setContentIntent(pendingIntent);

    startForeground(1, mNotifyBuilder.build());
    Log.e("test", "YuekeService onCreate");
    initData();
  }

  private void initData() {
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    accountList = CommonUntils.loadAccountInfo();
    max_sijiao = PreferenceHelper.getInstance().getMaxSijiao();
    max_courses = Integer.parseInt(Tools.getMaxNumber(userInfo));
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
    Log.e("test", "YuekeService onDestroy");
    updateUserInfo();
    release();
  }

  @Override
  protected void release() {
    super.release();
    isRunning = false;
    mBinder = null;
    coureseList = null;
    guanzhuList = null;
    currentCourseDetail = null;
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
        try {
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
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
          }
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
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
        }
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
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
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void getTheCourseList(String userId) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("count","20");
    job.addProperty("Privateid",userId);
    job.addProperty("page",pageIndex + 1);
    job.addProperty("whichFunc","GetCourseListforSJ");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();

    //try
    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_COURSE_LIST_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          CourseListModel courseListModel = gson.fromJson(response.body().charStream(), CourseListModel.class);
          response.body().close();
          if(courseListModel.isSucceed()) {
            Message msg = mHandler.obtainMessage(GET_COURSE_LIST_SUCESS);
            msg.obj = courseListModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_COURSE_LIST_FAILED,getDelayTime());
          }
        }catch (Exception e){
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_COURSE_LIST_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void getCourseUsers(String groupbuy_id) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("type","ALL");
    job.addProperty("groupbuy_id",groupbuy_id);
    job.addProperty("whichFunc","GetGroupbuyuser");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_COURSE_USERS_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          GroupbuyUserModel groupbuyUserModel = gson.fromJson(response.body().charStream(), GroupbuyUserModel.class);
          response.body().close();
          //获取成功之后
          if(groupbuyUserModel.isSucceed()) {
            Message msg = mHandler.obtainMessage(GET_COURSE_USERS_SUCESS);
            msg.obj = groupbuyUserModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_COURSE_USERS_FAILED,getDelayTime());
          }
        }catch (Exception e){
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_COURSE_USERS_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void getCourseDetails(String groupbuy_id) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("groupbuy_id",groupbuy_id);
    job.addProperty("whichFunc","GetCoursedetailmodel");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(GET_COURSE_DETAILS_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          CourseDetailModel courseDetailModel = gson.fromJson(response.body().charStream(), CourseDetailModel.class);
          response.body().close();
          //获取成功之后
          if(courseDetailModel.isSucceed()) {
            Message msg = mHandler.obtainMessage(GET_COURSE_DETAILS_SUCESS);
            msg.obj = courseDetailModel;
            msg.sendToTarget();
          }else {
            mHandler.sendEmptyMessageDelayed(GET_COURSE_DETAILS_FAILED,getDelayTime());
          }
        }catch (Exception e){
          if(mHandler != null) {
            mHandler.sendEmptyMessageDelayed(GET_COURSE_DETAILS_FAILED,getDelayTime());
          }
        }
      }
    });
  }

  private void postYuekeApplay() {
    if(currentCourseDetail == null){
      return;
    }

    String url = "http://api.healthmall.cn/Post";
    JsonObject modelJob  = new JsonObject();
    modelJob.addProperty("groupbuy_id",currentCourseDetail.getGroupbuy_id());
    modelJob.addProperty("hm_go_address",currentCourseDetail.getHm_gbc_address());
    modelJob.addProperty("hm_go_amount",currentCourseDetail.getHm_gbc_avgprice());
    modelJob.addProperty("trainer_id",currentCourseDetail.getUser_id());
    modelJob.addProperty("hm_go_program",currentCourseDetail.getHm_gbc_type());
    modelJob.addProperty("hm_go_payment",0);

    JsonObject job = new JsonObject();
    job.add("model",modelJob);
    job.addProperty("whichFunc","ADDGROUPBUYSIGNUPCOURE");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(mHandler != null) {
          mHandler.sendEmptyMessageDelayed(YUE_KE_FAILED,getDelayTime());
        }
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          PostYuekeModel postYuekeModel = gson.fromJson(response.body().charStream(), PostYuekeModel.class);
          response.body().close();
          //获取成功之后
          if(postYuekeModel.isSucceed()){
            Message msg = mHandler.obtainMessage(YUE_KE_SUCESS);
            //msg.obj = ordersModel;
            accountList.get(accountIndex).setYuekeTimes(accountList.get(accountIndex).getYuekeTimes() + 1);
            DBManager.getInstance().updateYukeTimes(accountList.get(accountIndex));
            msg.sendToTarget();
          }else{
            Message msg = mHandler.obtainMessage(YUE_KE_FAILED);
            //msg.obj = ordersModel;
            msg.sendToTarget();
          }
        }catch (Exception e){
          if(mHandler != null) {
            Message msg = mHandler.obtainMessage(YUE_KE_FAILED);
            //msg.obj = ordersModel;
            msg.sendToTarget();
          }
        }
      }
    });
  }

  private boolean isOutofDate(Course course) {
    String[] array = course.getHm_gbc_enddate().split("T");
    try {
      long time1 = TimeUntils.dateToStamp(array[0] + " " + array[1]);
      if(time1 > System.currentTimeMillis()) {
        Log.e("test","time1 > currentime");
        //可以约课
        return false;
      }else {
        Log.e("test","time1 <= currentime");
        return true;
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  public class YukeBinder extends Binder {

    public void startYueke() {
      isRunning = true;
      Message msg = mHandler.obtainMessage(START_TO_YUE_KE);
      msg.sendToTarget();
    }

    public void stopYuke() {
      isRunning = false;
      if(mHandler != null) {
        mHandler.removeCallbacksAndMessages(null);
      }
    }

    public void setHandler(Handler handler) {
      mActivityHandler = handler;
    }

    public void setPageSize(int size) {
      per_sijiao_max_courses = size;
      pageSize = (per_sijiao_max_courses - 1)/20 + 1;
      Log.e("test", "setPageSize execute,per_sijiao_max_courses =  " + size + "  pageSize = " + pageSize);
    }
  }

  protected int getDelayTime() {
    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
    int randTime = CommonUntils.getRandomInt(min_time, max_time);
    Log.d("test", "randTime = " + randTime);
    return randTime;
  }
}
