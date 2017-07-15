package com.june.healthmail.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.model.Course;
import com.june.healthmail.model.CourseDetail;
import com.june.healthmail.model.CourseDetailModel;
import com.june.healthmail.model.CourseListModel;
import com.june.healthmail.model.GroupbuyUser;
import com.june.healthmail.model.GroupbuyUserModel;
import com.june.healthmail.model.Guanzhu;
import com.june.healthmail.model.GuanzhuListModel;
import com.june.healthmail.model.TrainerModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/7/15.
 */

public class GuanzhuDetailActivity extends BaseActivity{

  private UserInfo userInfo;
  private String accessToken;
  private TrainerModel trainerModel;

  private Boolean isRunning = false;

  @BindView(R.id.btn_start)
  Button btnStart;
  @BindView(R.id.show_result)
  TextView tvShowResult;
  @BindView(R.id.tv_coins_number)
  TextView tvConinsNumber;
  @BindView(R.id.tv_coins_desc)
  TextView tvConinsDesc;
  @BindView(R.id.tv_desc_coins_cost)
  TextView tvConinsCostDesc;
  private int coinsCost;

  private int offset;

  private static final int START_TO_WORK = 1;
  private static final int START_TO_GET_GUANZHU_LIST = 3;
  private static final int GET_GUANZHU_LIST_SUCCESS = 4;
  private static final int START_TO_GET_COURSE_LIST = 5;
  private static final int GET_COURSE_LIST_SUCESS = 6;
  private static final int GET_COURSE_USERS_SUCESS = 8;
  private static final int START_TO_GET_COURSE_DETAILS = 9;
  private static final int GET_COURSE_DETAILS_SUCESS = 10;
  private static final int GET_GUANZHU_LIST_FAILED = 15;
  private static final int GET_COURSE_LIST_FAILED = 16;
  private static final int GET_COURSE_USERS_FAILED = 17;
  private static final int GET_COURSE_DETAILS_FAILED = 18;
  private int sijiaoIndex = 0;
  private int courseIndex = 0;

  private Message message;

  private ArrayList<Guanzhu> guanzhuList = new ArrayList<>();
  private ArrayList<Course> coureseList = new ArrayList<>();
  private int courseNumbers;

  private int min_time;
  private int max_time;

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_WORK:
          if(isRunning) {
              message = this.obtainMessage(START_TO_GET_GUANZHU_LIST);
              message.sendToTarget();
          } else {
            showTheResult("**用户自己终止获取**当前已经执行完成"+ sijiaoIndex + "个关注\n");
          }
          break;
        case START_TO_GET_GUANZHU_LIST:
          if(userInfo.getCoinsNumber() >= 0){
            showTheResult("开始获取关注列表:");
            getTheGuanzhuList();
          }else {
            showTheResult("******金币余额不足，请充值**********\n");
            isRunning = false;
            btnStart.setText("开始获取");
          }
          break;

        case GET_GUANZHU_LIST_SUCCESS:
          showTheResult("关注列表获取成功\n");
          //保存关注列表--私教列表
          sijiaoIndex = 0;
          guanzhuList.clear();

          GuanzhuListModel guanzhuListModel = (GuanzhuListModel)msg.obj;
          if(guanzhuListModel.getValuse() != null && guanzhuListModel.getValuse().size() > 0) {
            for (int i = 0; i < guanzhuListModel.getValuse().size(); i++) {
              guanzhuList.add(guanzhuListModel.getValuse().get(i));
            }
            showTheResult("--总共关注了"+ guanzhuList.size() + "个私教\n");
            message = this.obtainMessage(START_TO_GET_COURSE_LIST);
            message.sendToTarget();
          }else {
            //关注列表为空
            showTheResult("没有关注的私教，请先关注需要获取详情的私教\n\n");
            showTheResult("****获取关注详情结束****\n");
            isRunning = false;
            btnStart.setText("获取完成");
          }
          break;

        case START_TO_GET_COURSE_LIST:
          //传入私教id
          if(sijiaoIndex < guanzhuList.size()){
            showTheResult("\n\n************开始获取私教["+ (sijiaoIndex+1) +"]-" +
                guanzhuList.get(sijiaoIndex).getHm_u_nickname_concerned()+ "的课程详情\n");
            getTheCourseList(guanzhuList.get(sijiaoIndex).getUser_id());
          }else {
            showTheResult("*****所有关注的私教详情都获取完毕*****\n\n");
            showTheResult("****获取关注详情结束****\n");
            isRunning = false;
            btnStart.setText("获取完成");
          }
          break;
        case GET_COURSE_LIST_SUCESS:
          courseIndex = 0;
          coureseList.clear();
          courseNumbers = 0;
          String current_date;
          CourseListModel courseListModel = (CourseListModel)msg.obj;
          if(courseListModel.getValuse() != null && courseListModel.getValuse().size() > 0){
            current_date = courseListModel.getValuse().get(0).getHm_gbc_date();
            coureseList.add(courseListModel.getValuse().get(0));
            for(int i = 0; i < courseListModel.getValuse().size(); i++){
              if(!current_date.equals(courseListModel.getValuse().get(i).getHm_gbc_date())) {
                coureseList.add(courseListModel.getValuse().get(i));
                current_date = courseListModel.getValuse().get(i).getHm_gbc_date();
              }
              courseNumbers++;
            }
            showTheResult("--私教"+ (sijiaoIndex+1) + "有" + courseNumbers + "节课程\n");
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS,getDelayTime());
          }else {
            showTheResult("--私教"+ (sijiaoIndex+1) + "暂时没有发布课程，继续下一个私教\n");
            sijiaoIndex++;
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
          }
          break;
        case START_TO_GET_COURSE_DETAILS:
          if(courseIndex < coureseList.size()){
            showTheResult("----获取" + getCourseDate(coureseList.get(courseIndex).getHm_gbc_date()) + "号课程详情\n");
            getCourseDetails(coureseList.get(courseIndex).getGroupbuy_id());
          }else {
            sijiaoIndex++;

            updateTheCoinsNumber();
            tvConinsNumber.setText(userInfo.getCoinsNumber()+"");
            showTheResult("----------金币余额-" + coinsCost + "\n");

            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST, getDelayTime());
          }
          break;
        case GET_COURSE_DETAILS_SUCESS:
          CourseDetailModel courseDetailModel = (CourseDetailModel)msg.obj;
          if(courseDetailModel.getValuse() == null) {
            showTheResult("--------课程详情有误，重新获取\n");
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS,getDelayTime());
          }else {
            showTheResult("--------报名人数(" + courseDetailModel.getValuse().getHm_gbc_currnum() + "/" + courseDetailModel.getValuse().getHm_gbc_maxnum() + ")\n");
            courseIndex++;
            this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS,getDelayTime());
          }
          break;
        case GET_GUANZHU_LIST_FAILED:
          showTheResult("--获取关注列表失败，重新获取关注列表\n");
          this.sendEmptyMessageDelayed(START_TO_GET_GUANZHU_LIST,getDelayTime());
          break;
        case GET_COURSE_LIST_FAILED:
          showTheResult("--获取私教课程列表失败，重新获取该私教课程列表\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST,getDelayTime());
          break;
        case GET_COURSE_DETAILS_FAILED:
          showTheResult("--获取课程详情失败，重新获取\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_DETAILS,getDelayTime());
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
    setContentView(R.layout.activity_guanzhu_detail);
    ButterKnife.bind(this);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    if(getIntent() != null){
      if(getIntent().getBooleanExtra("exception",false)){
        //exception
      }
      accessToken = getIntent().getStringExtra("accessToken");
      trainerModel = (TrainerModel) getIntent().getSerializableExtra("trainerModel");
    }
    if(trainerModel == null) {
      toast("私教信息异常");
      finish();
      return;
    }
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();

    tvConinsNumber.setText(userInfo.getCoinsNumber() + "");
    Log.e("test","userInfo.getCoinsNumber() = " + userInfo.getCoinsNumber());
    coinsCost = PreferenceHelper.getInstance().getCoinsCostForSpecialFunction();
    if(coinsCost == 0){
      tvConinsCostDesc.setText("活动期间，获取关注详情免金币");
      tvConinsNumber.setVisibility(View.GONE);
      tvConinsDesc.setVisibility(View.GONE);
    }else {
      tvConinsCostDesc.setText("获取一次关注详情消耗" + coinsCost + "金币");
      tvConinsNumber.setVisibility(View.VISIBLE);
      tvConinsDesc.setVisibility(View.VISIBLE);
    }
  }

  @OnClick(R.id.btn_start)
  public void btnStart(View view){
    if("获取完成".equals(btnStart.getText().toString().trim())){
      Toast.makeText(this,"获取已完成，如需继续获取请重新进入本页面",Toast.LENGTH_LONG).show();
    }else {
      if (isRunning == false) {
        isRunning = true;
        btnStart.setText("停止获取");
        startToWork();
      } else {
        isRunning = false;
        mHandler.removeCallbacksAndMessages(null);
        btnStart.setText("开始获取");
      }
    }
  }

  private void startToWork() {
    Message msg = mHandler.obtainMessage(START_TO_WORK);
    msg.sendToTarget();
  }

  @OnClick(R.id.img_back)
  public void btnImageBack(View view){
    finish();
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }

  private int getDelayTime() {
    int randTime = CommonUntils.getRandomInt(min_time,max_time);
    Log.d("test","randTime = " + randTime);
    return randTime;
  }

  private String getCourseDate(String hm_gbc_date) {
    if(hm_gbc_date.contains("T")) {
      String array[] = hm_gbc_date.split("T")[0].split("-");
      return array[1] + "-" + array[2];
    }else {
      return hm_gbc_date;
    }
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
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
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

  private void getTheCourseList(String userId) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("count","20");
    job.addProperty("Privateid",userId);
    job.addProperty("page",1);
    job.addProperty("whichFunc","GetCourseListforSJ");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();

    //try
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_COURSE_LIST_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          Gson gson = new Gson();
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
          mHandler.sendEmptyMessageDelayed(GET_COURSE_LIST_FAILED,getDelayTime());
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


    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_COURSE_USERS_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();
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
          mHandler.sendEmptyMessageDelayed(GET_COURSE_USERS_FAILED,getDelayTime());
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


    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_COURSE_DETAILS_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();
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
          mHandler.sendEmptyMessageDelayed(GET_COURSE_DETAILS_FAILED,getDelayTime());
        }
      }
    });
  }

  private void updateTheCoinsNumber(){
    if(coinsCost > 0) {
      userInfo.setCoinsNumber(userInfo.getCoinsNumber() - coinsCost);
      userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
        @Override
        public void done(BmobException e) {
          if(e == null){
            Log.e("test","updateTheCoinsNumber 更新金币成功");
          }
        }
      });
    }
  }
}
