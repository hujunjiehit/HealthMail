package com.june.healthmail.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
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
import com.june.healthmail.model.CourseListModel;
import com.june.healthmail.model.Order;
import com.june.healthmail.model.OrdersModel;
import com.june.healthmail.model.TrainerModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.TimeUntils;
import com.june.healthmail.untils.Tools;
import com.june.healthmail.view.CourseRadioGroupDialog;

import java.io.IOException;
import java.util.ArrayList;

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

public class PingjiaDetailActivity extends BaseActivity implements CourseRadioGroupDialog.CourseRadioGroupDialogInterface{

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
  private static final int START_TO_GET_COURSE_LIST = 2;
  private static final int GET_COURSE_LIST_SUCESS = 3;
  private static final int SHOW_COURSE_CHOOSE_DIALOG = 4;
  private static final int START_TO_GET_COURSE_DETAILS = 5;

  private static final int GET_ORDER_LIST = 6;
  private static final int GET_ORDER_LIST_SUCCESS = 7;
  private static final int GET_ORDER_LIST_FAILED = 8;
  private static final int GET_COURSE_LIST_FAILED = 9;

  private int sijiaoIndex = 0;
  private int courseIndex = 0;

  private Message message;

  private ArrayList<Order> ordelList = new ArrayList<>();
  private ArrayList<Course> coureseList = new ArrayList<>();

  private int min_time;
  private int max_time;

  private String today;
  private int year;
  private int month;
  private int day;

  private CourseRadioGroupDialog mCourseRadiaoGroupDoalog;
  private Course targetCourse;
  private int pageNumber;
  private int pageIndex;
  private OrdersModel ordersModel;
  private DBManager mDBmanager;

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_WORK:
          if(isRunning) {
            message = this.obtainMessage(START_TO_GET_COURSE_LIST);
            message.sendToTarget();
          } else {
            showTheResult("**用户自己终止获取**当前已经执行完成"+ sijiaoIndex + "个关注\n");
          }
          break;
        case START_TO_GET_COURSE_LIST:
          //传入私教id
          showTheResult("*****开始获取课程列表*****\n");
          getTheCourseList(trainerModel.getUser_Id());
          break;
        case GET_COURSE_LIST_SUCESS:
          coureseList.clear();
          CourseListModel courseListModel = (CourseListModel)msg.obj;
          if(courseListModel.getValuse() != null && courseListModel.getValuse().size() > 0){
            for(int i = 0; i < courseListModel.getValuse().size(); i++){
              Course course =  courseListModel.getValuse().get(i);
              if(getCourseDay(course.getHm_gbc_date()) == day){
                coureseList.add(course);
              }
            }
            showTheResult("--课程详情获取成功\n");
            this.sendEmptyMessageDelayed(SHOW_COURSE_CHOOSE_DIALOG,getDelayTime());
          }else {
            showTheResult("--今天暂时没有发布课程，结束\n");
          }
          break;

        case SHOW_COURSE_CHOOSE_DIALOG:
          if(userInfo.getCoinsNumber() >= 0){
            showTheResult("----今天(" + month + "-" + day + ")一共有" + coureseList.size() + "节课程\n");
            mCourseRadiaoGroupDoalog.showRadioGroupDialog(coureseList);
          }else {
            showTheResult("******金币余额不足，请充值**********\n");
            isRunning = false;
            btnStart.setText("开始获取");
          }
          break;
        case START_TO_GET_COURSE_DETAILS:
            if(coureseList.size() > 0) {
              targetCourse = coureseList.get(courseIndex);
              showTheResult("\n\n上课时间:" + targetCourse.getHm_gbc_time() + " 报名人数：" + targetCourse.getApplynumber() + "\n");
              pageNumber = (targetCourse.getApplynumber() - 1) /20 + 1;
              pageIndex = 0;
              ordelList.clear();
              this.sendEmptyMessageDelayed(GET_ORDER_LIST,getDelayTime());
            }else {
              showTheResult("-----私教今日没有课程");
            }
          break;

        case GET_ORDER_LIST:
          if (pageIndex < pageNumber) {
            showTheResult("----开始获取第"+ (pageIndex + 1) + "页订单列表，(一共"+ pageNumber + "页):");
            getOrderList();
          }else {
            updateTheCoinsNumber();
            tvConinsNumber.setText(userInfo.getCoinsNumber()+"");
            showTheResult("------金币余额-" + coinsCost + "\n");

            showTheResult("--------所有订单获取完毕，结果如下：\n");
            btnStart.setText("开始获取");
            isRunning = false;
            showTheResult("******一共：" + ordelList.size() + "个订单*******\n");
            showDetailResult();
          }
          break;
        case GET_ORDER_LIST_SUCCESS:
          showTheResult("获取成功\n");
          if(ordersModel.getValuse() != null && ordersModel.getValuse().size() > 0) {
            for (int i = 0; i < ordersModel.getValuse().size(); i++) {
              ordelList.add(ordersModel.getValuse().get(i));
            }
          }
          pageIndex++;
          this.sendEmptyMessageDelayed(GET_ORDER_LIST,getDelayTime());
          break;

        case GET_ORDER_LIST_FAILED:
          showTheResult("--------订单列表获取失败，继续尝试\n");
          this.sendEmptyMessageDelayed(GET_ORDER_LIST,getDelayTime());
          break;

        case GET_COURSE_LIST_FAILED:
          showTheResult("--------课程详情获取失败，继续尝试\n");
          this.sendEmptyMessageDelayed(START_TO_GET_COURSE_LIST,getDelayTime());
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
    setContentView(R.layout.activity_pingjia_detail);
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
    initData();
  }

  private void initData() {
    mDBmanager = DBManager.getInstance();
    today = TimeUntils.transForDate1(System.currentTimeMillis()/1000);
    String[] array = today.split(" ")[0].split("-");
    year = Tools.parseInt(array[0]);
    month = Tools.parseInt(array[1]);
    day = Tools.parseInt(array[2]);
    coureseList.clear();
    mCourseRadiaoGroupDoalog = new CourseRadioGroupDialog(this);

    tvConinsNumber.setText(userInfo.getCoinsNumber() + "");
    Log.e("test","userInfo.getCoinsNumber() = " + userInfo.getCoinsNumber());
    coinsCost = PreferenceHelper.getInstance().getCoinsCostForSpecialFunction();
    if(coinsCost == 0){
      tvConinsCostDesc.setText("活动期间，获取评价详情免金币");
      tvConinsNumber.setVisibility(View.GONE);
      tvConinsDesc.setVisibility(View.GONE);
    }else {
      tvConinsCostDesc.setText("获取一次评价详情消耗" + coinsCost + "金币");
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
    if(coureseList.size() == 0) {
      Message msg = mHandler.obtainMessage(START_TO_WORK);
      msg.sendToTarget();
    }else {
      tvShowResult.setText("清空上一次的查询结果\n");
      Message msg = mHandler.obtainMessage(SHOW_COURSE_CHOOSE_DIALOG);
      msg.sendToTarget();
    }
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

  private int getCourseDay(String hm_gbc_date) {
    if(hm_gbc_date.contains("T")) {
      String array[] = hm_gbc_date.split("T")[0].split("-");
      return Tools.parseInt(array[2]);
    }else {
      return 0;
    }
  }

  private void getTheCourseList(String userId) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("month",Integer.toString(month));
    job.addProperty("year",Integer.toString(year));
    job.addProperty("count","20");
    job.addProperty("Privateid",userId);
    job.addProperty("page",1);
    job.addProperty("whichFunc","GetCourseListforSJ");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();

    HttpUntils.getInstance().postForm(url, body, new Callback() {
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

  private void getOrderList() {

    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("groupbuy_id",targetCourse.getGroupbuy_id());
    job.addProperty("Which","trainer");
    job.addProperty("count","20");
    job.addProperty("page",pageIndex+1);
    job.addProperty("whichFunc","Getorderlist");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          Gson gson = new Gson();
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
          mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
        }
      }
    });
  }

  @Override
  public void onCourseRadiogroupPositive() {
    courseIndex = mCourseRadiaoGroupDoalog.getSelected();
    showTheResult("------用户选择课程编号:" + courseIndex + "\n");
    Message msg = mHandler.obtainMessage(START_TO_GET_COURSE_DETAILS);
    msg.sendToTarget();
  }

  @Override
  public void onCourseRadiogroupNegative() {

  }

  private void showDetailResult() {
    ArrayList<Order> orderList2 = new ArrayList<>();
    ArrayList<Order> orderList4 = new ArrayList<>();
    ArrayList<Order> orderList5 = new ArrayList<>();
    ArrayList<Order> orderList9 = new ArrayList<>();
    orderList2.clear();
    orderList4.clear();
    orderList5.clear();
    orderList9.clear();
    for(Order order:ordelList) {
      if(order.getHm_go_orderstatus() == 2) {
        orderList2.add(order);
      }else if(order.getHm_go_orderstatus() == 4){
        orderList4.add(order);
      }else if(order.getHm_go_orderstatus() == 5){
        orderList5.add(order);
      }else if(order.getHm_go_orderstatus() == 9){
        orderList9.add(order);
      }
    }
    showTheResult("未支付状态的订单：" + orderList2.size() + "个\n");
    showTheResult("已完成状态的订单：" + orderList4.size() + "个\n");
    showTheResult("已支付状态的订单：" + orderList5.size() + "个\n");
    showTheResult("待评价状态的订单：" + orderList9.size() + "个\n\n");

    if(orderList2.size() > 0) {
      showTheResult("\n\n未支付状态的订单如下：\n");
      for (int i = 0; i < orderList2.size(); i++) {
        DBManager.QueryResult result = mDBmanager.getPhoneByMallID(orderList2.get(i).getUser_id());
        if(result == null){
          showTheResult((i + 1) + "--猫号:" + orderList2.get(i).getUser_id() + "(手机号未知)\n");
        }else {
          showTheResult((i + 1) + "--猫号:" + orderList2.get(i).getUser_id() + "（" + result.getPhoneNumber() + "）-- 编号：" +
              result.getId() +  "\n");
        }

      }
      showTheResult("****温馨提示，用猫号和密码也能登录健康猫***\n");

    }else if(orderList9.size() > 0) {
      showTheResult("\n\n待评价状态的订单如下：\n");
      for (int i = 0; i < orderList9.size(); i++) {
        DBManager.QueryResult result = mDBmanager.getPhoneByMallID(orderList9.get(i).getUser_id());
        if(result == null){
          showTheResult((i + 1) + "--猫号:" + orderList9.get(i).getUser_id() + "(手机号未知)\n");
        }else {
          showTheResult((i + 1) + "--猫号:" + orderList9.get(i).getUser_id() + "（" + result.getPhoneNumber() + "）-- 编号：" +
              result.getId() +  "\n");
        }
      }
      showTheResult("****温馨提示，用猫号和密码也能登录健康猫***\n");
    }
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
