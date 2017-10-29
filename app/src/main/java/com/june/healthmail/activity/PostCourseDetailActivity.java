package com.june.healthmail.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.model.PostCourseModel;
import com.june.healthmail.model.TrainerModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.TimeUntils;
import com.june.healthmail.untils.Tools;
import com.june.healthmail.view.PicRadioGroupDialog;
import com.june.healthmail.view.RadioGroupDialog;
import com.june.healthmail.view.TimePickerDialog;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.ParseException;

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
 * Created by june on 2017/7/6.
 */
public class PostCourseDetailActivity extends BaseActivity implements View.OnClickListener, TimePickerDialog.TimePickerDialogInterface,
    RadioGroupDialog.RadioGroupDialogInterface,PicRadioGroupDialog.PicRadioGroupDialogInterface {

  private UserInfo userInfo;

  @BindView(R.id.show_result)
  TextView tvShowResult;
  @BindView(R.id.btn_start)
  Button btnStart;

  @BindView(R.id.tv_show_time)
  TextView tvShowTime;
  @BindView(R.id.btn_edit_time)
  Button btnEditTime;
  @BindView(R.id.edit_course_number)
  EditText etCourseNumber;
  @BindView(R.id.edit_course_title)
  EditText etCourseTitle;
  @BindView(R.id.tv_show_course_type)
  TextView tvShowCourseType;
  @BindView(R.id.btn_edit_course_type)
  Button btnEditType;
  @BindView(R.id.edit_max_people)
  EditText etMaxPeople;
  @BindView(R.id.edit_averge_price)
  EditText etAvergePrice;
  @BindView(R.id.select_picture)
  Button btnSelectPicture;
  @BindView(R.id.show_course_pic)
  ImageView ivShowCoursePic;
  @BindView(R.id.config_layout)
  View mConfigLayout;
  @BindView(R.id.tv_coins_number)
  TextView tvConinsNumber;
  @BindView(R.id.tv_coins_desc)
  TextView tvConinsDesc;
  @BindView(R.id.tv_desc_coins_cost)
  TextView tvConinsCostDesc;

  private int courseNumber;
  private String courseTitle;
  private int maxPeople;
  private float avergePrice;
  private int courseType;
  private String courseTime;

  private TimePickerDialog mTimePickerDialog;
  private RadioGroupDialog mRadiaGroupDialog;
  private PicRadioGroupDialog mPicRadiaGroupDialog;

  private String accessToken;
  private TrainerModel trainerModel;
  private String[] types;
  private int offset;
  private Boolean isRunning = false;
  private String errMsg;
  private Message message;
  private String coursePicUrl;

  private int coinsCost;

  private static final int START_TO_POST_COURSE = 1;
  private static final int POST_COURSE_SUCESS = 2;
  private static final int POST_COURSE_EXIST = 3;
  private static final int POST_COURSE_FAILED = 4;

  private int courseIndex = 0;

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_POST_COURSE:
          if(isRunning) {
            if(userInfo.getCoinsNumber() >= 0){
              if (courseIndex < courseNumber) {
                mConfigLayout.setVisibility(View.GONE);
                tvShowResult.setVisibility(View.VISIBLE);
                showTheResult("\n\n开始发布第" + (courseIndex + 1) + "节课，一共" + courseNumber +"节课\n");
                postTheCourse();
              } else {
                showTheResult("******所有课程发布结束**********\n");
                isRunning = false;
                btnStart.setText("显示课程配置");
              }
            }else {
              showTheResult("******金币余额不足，发布结束**********\n");
              isRunning = false;
              btnStart.setText("开始发布");
            }
          } else {
            showTheResult("**用户自己终止发布进展**当前已经成功发布" + courseIndex + "节课\n");
          }
          break;
        case POST_COURSE_SUCESS:
          updateTheCoinsNumber();
          tvConinsNumber.setText(userInfo.getCoinsNumber()+"");
          showTheResult("--金币余额-" + coinsCost + "\n");
          showTheResult("----发布成功\n");
          showTheResult("-----上课时间：" + courseTime.split(" ")[0] + " " + getDetailTime()+ "\n");
          showTheResult("-------报名截止时间：" + getEndDate() + "\n");
          showTheResult("---------继续发布下一节课\n");
          courseIndex++;
          mHandler.sendEmptyMessageDelayed(START_TO_POST_COURSE,500);
          break;
        case POST_COURSE_EXIST:
          showTheResult("----改时间段已经发布过课程，不能重叠发布，继续发布下一节课\n");
          courseIndex++;
          message = this.obtainMessage(START_TO_POST_COURSE);
          message.sendToTarget();
          break;
        case POST_COURSE_FAILED:
          showTheResult("---课程发布失败:");
          if(errMsg != null){
            showTheResult(errMsg + "\n");
          }
          showTheResult("--------继续尝试\n");
          message = this.obtainMessage(START_TO_POST_COURSE);
          message.sendToTarget();
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
    setContentView(R.layout.activity_post_course_detail);
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
    initView();
    setListener();
    initData();
  }

  @OnClick(R.id.btn_edit_time)
  public void btnEditTime(View view){
    String time = tvShowTime.getText().toString().trim();
    String[] array = time.split(" ");
    String[] array1 = array[0].split("-");
    String[] array2 = array[1].split(":");

    mTimePickerDialog.setYear(Integer.parseInt(array1[0]));
    mTimePickerDialog.setMonth(Integer.parseInt(array1[1]));
    mTimePickerDialog.setDay(Integer.parseInt(array1[2]));
    mTimePickerDialog.setHour(Integer.parseInt(array2[0]));
    mTimePickerDialog.setMinute(Integer.parseInt(array2[1]));
    mTimePickerDialog.showDateAndTimePickerDialog();
  }

  @OnClick(R.id.btn_edit_course_type)
  public void setBtnEditType(View view) {
    if(types != null){
      mRadiaGroupDialog.showRadioGroupDialog(types);
    }
  }

  @OnClick(R.id.select_picture)
  public void selectPicture(View view) {
    String[] ids = new String[trainerModel.getHm_ptwi_images().size()];
    for(int i = 0; i < trainerModel.getHm_ptwi_images().size(); i ++) {
      ids[i] = i + "";
    }
    mPicRadiaGroupDialog.showRadioGroupDialog(ids);
  }

  private void initView() {
    mTimePickerDialog = new TimePickerDialog(this);
    mRadiaGroupDialog = new RadioGroupDialog(this);
    mPicRadiaGroupDialog = new PicRadioGroupDialog(this);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    coursePicUrl = PreferenceHelper.getInstance().getCoursePicture();
    if(TextUtils.isEmpty(coursePicUrl)){
      if(trainerModel.getHm_ptwi_images().size() == 0){
        toast("私教小屋图片为空，需要至少上传一张图片");
        finish();
        return;
      }
      coursePicUrl = trainerModel.getHm_ptwi_images().get(0).getIurl();
      PreferenceHelper.getInstance().setCoursePicture(coursePicUrl);
    }
    Picasso.with(this).load(coursePicUrl).into(ivShowCoursePic);
  }

  private void setListener() {
    findViewById(R.id.img_back).setOnClickListener(this);
    btnStart.setOnClickListener(this);
  }

  private void initData() {
    etCourseNumber.setText(PreferenceHelper.getInstance().getCourseNumber() + "");
    etCourseTitle.setText(PreferenceHelper.getInstance().getCourseTitle() + "");
    etMaxPeople.setText(PreferenceHelper.getInstance().getMaxPeople() + "");
    etAvergePrice.setText(PreferenceHelper.getInstance().getAvergePrice() + "");
    tvShowTime.setText(TimeUntils.transForDate1(System.currentTimeMillis()/1000 + 3600*2));
    types = trainerModel.getHM_PT_TeachingProgram().split(",");
    tvShowCourseType.setText(types[0]);

    tvConinsNumber.setText(userInfo.getCoinsNumber() + "");
    Log.e("test","userInfo.getCoinsNumber() = " + userInfo.getCoinsNumber());
    coinsCost = PreferenceHelper.getInstance().getCoinsCostForPost();
    if(coinsCost == 0){
      tvConinsCostDesc.setText("活动期间，发布课程免金币");
      tvConinsNumber.setVisibility(View.GONE);
      tvConinsDesc.setVisibility(View.GONE);
    }else {
      tvConinsCostDesc.setText("发布一节课消耗" + coinsCost + "金币");
      tvConinsNumber.setVisibility(View.VISIBLE);
      tvConinsDesc.setVisibility(View.VISIBLE);
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_start:
        if (isRunning == false) {
          if(btnStart.getText().equals("显示课程配置")){
            mConfigLayout.setVisibility(View.VISIBLE);
            tvShowResult.setVisibility(View.GONE);
            btnStart.setText("显示发布结果");
          }else if(btnStart.getText().equals("显示发布结果")){
            mConfigLayout.setVisibility(View.GONE);
            tvShowResult.setVisibility(View.VISIBLE);
            btnStart.setText("显示课程配置");
          }else if(btnStart.getText().equals("开始发布")){
            if(checkParams()){
              isRunning = true;
              btnStart.setText("停止发布");
              PreferenceHelper.getInstance().setCourseNumber(courseNumber);
              PreferenceHelper.getInstance().setCourseTitle(courseTitle);
              PreferenceHelper.getInstance().setMaxPeople(maxPeople);
              PreferenceHelper.getInstance().setAvergePrice(avergePrice);
              Message msg = mHandler.obtainMessage(START_TO_POST_COURSE);
              msg.sendToTarget();
            }
          }
        } else {
          isRunning = false;
          mHandler.removeCallbacksAndMessages(null);
          btnStart.setText("开始发布");
        }
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      default:
        break;
    }
  }



  private boolean checkParams() {
    //检查发布多少节课
    courseNumber = Tools.parseInt(etCourseNumber.getText().toString().trim());
    if(courseNumber > 10) {
      toast("一次最多只能发布十节课");
      return false;
    }else if(courseNumber == 0){
      toast("请设置发布多少节课");
      return false;
    }

    courseTitle = etCourseTitle.getText().toString().trim();
    if(courseTitle.length() == 0){
      toast("请设置课程标题前缀");
      return false;
    }

    //检查最大人数
    maxPeople = Tools.parseInt(etMaxPeople.getText().toString().trim());
    if(maxPeople > 200){
      toast("最大人数不能超过200");
      return false;
    }else if(maxPeople < 1){
      toast("请输入最大人数且不能为0");
      return false;
    }

    avergePrice = Tools.parseFloat(etAvergePrice.getText().toString().trim());
    if(avergePrice > 300){
      toast("团课补贴不超过300");
      return false;
    }else if(avergePrice <= 0){
      toast("请输入团课补贴且大于0");
      return false;
    }

    //检查时间
    String time = tvShowTime.getText().toString().trim();
    if(isBeforeNow(time)){
      toast("开课时间不能早于现在");
      return false;
    }
    if(!TimeUntils.isInTenDays(time)) {
      toast("只能发布十天内的课程");
      return false;
    }

    courseType = Tools.parseInt(tvShowCourseType.getText().toString().trim());
    courseTime = tvShowTime.getText().toString().trim();

    //根据courseTime计算最大可发布课程
    int maxCourseNumber = getMaxCourseNumber();
    if(courseNumber > maxCourseNumber) {
      toast("当前时间点开始，最多只能发布" + maxCourseNumber + "节课");
      return false;
    }

    if(trainerModel.getHM_PT_TeachingSite().size() == 0){
      toast("请登录健康猫->私教信息设置一个教学场地");
      return false;
    }else if(TextUtils.isEmpty(trainerModel.getHM_PT_TeachingSite().get(0).getSitename())){
      toast("教学场地设置不规范，请添加一个新的教学场地并删除第一个教学场地");
      return false;
    }
    return true;
  }

  private int getMaxCourseNumber() {
    int startTime = Tools.parseInt(courseTime.split(" ")[1].split(":")[0]);
    return 23 - startTime;
  }

  private boolean isBeforeNow(String time) {
    try {
      long time1 = TimeUntils.dateToStamp(time);
      Log.e("test","time1 = " + time1);
      Log.e("test","now = " + System.currentTimeMillis());
      if(time1 > System.currentTimeMillis()) {
        Log.e("test","time1 > currentime");
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

  private void postTheCourse() {
    String url = "http://api.healthmall.cn/Post";

    JsonObject model = new JsonObject();
    model.addProperty("hm_gbc_address",trainerModel.getHM_PT_TeachingSite().get(0).getSitename());

    JsonArray jsonArray = new JsonArray();
    JsonObject image = new JsonObject();
    image.addProperty("url",coursePicUrl);
    image.addProperty("flag",1);
    jsonArray.add(image);
    model.add("hm_gbci_image",jsonArray);

    JsonObject coordinate = new JsonObject();
    coordinate.addProperty("hm_venue_lat",trainerModel.getHM_PT_TeachingSite().get(0).getCoordinate().getHm_venue_lat());
    coordinate.addProperty("hm_venue_lng",trainerModel.getHM_PT_TeachingSite().get(0).getCoordinate().getHm_venue_lng());
    model.add("hm_gbc_coordinate",coordinate);

    model.addProperty("hm_gbc_whopay","1");
    model.addProperty("hm_gbc_date",courseTime.split(" ")[0]);
    model.addProperty("hm_gbc_desc","场地费用自理");
    model.addProperty("hm_gbc_enddate",getEndDate());
    model.addProperty("hm_gbc_title",courseTitle + (courseIndex + 1));
    model.addProperty("hm_gbc_time",getDetailTime());
    model.addProperty("hm_gbc_publishdate",TimeUntils.transForDate1(System.currentTimeMillis()/1000));
    model.addProperty("hm_gbc_avgprice",avergePrice);
    model.addProperty("hm_gbc_maxnum",maxPeople);
    model.addProperty("hm_gbc_type",courseType);
    model.addProperty("hm_gbc_currnum",1);
    model.addProperty("hm_gbc_minnum",1);

    JsonObject job = new JsonObject();
    job.add("model",model);
    job.addProperty("whichFunc","ADDGROUPBUYCOURSE");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();


    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(POST_COURSE_FAILED,1000);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();//java.lang.IllegalStateException
          PostCourseModel postCourseModel = gson.fromJson(response.body().charStream(), PostCourseModel.class);
          response.body().close();
          //获取成功之后
          if(postCourseModel.isSucceed()){
            Message msg = mHandler.obtainMessage(POST_COURSE_SUCESS);
            msg.sendToTarget();
          }else{
            errMsg = postCourseModel.getMsg();
            if(errMsg.contains("已经开过课")){
              Message msg = mHandler.obtainMessage(POST_COURSE_EXIST);
              msg.sendToTarget();
            }else {
              Message msg = mHandler.obtainMessage(POST_COURSE_FAILED);
              msg.sendToTarget();
            }
          }
        }catch (Exception e){
          Message msg = mHandler.obtainMessage(POST_COURSE_FAILED);
          msg.sendToTarget();
        }
      }
    });
  }

  private String getDetailTime() {
    String[] arrays = courseTime.split(" ")[1].split(":");
    StringBuilder sb = new StringBuilder();
    int startTime = Integer.parseInt(arrays[0]) + courseIndex;
    int endTime = startTime + 1;
    if(startTime <= 9){
      sb.append("0" + startTime + ":");
    }else {
      sb.append(startTime + ":");
    }
    sb.append(arrays[1] + "-");
    if(endTime <= 9){
      sb.append("0" + endTime + ":");
    }else {
      sb.append(endTime + ":");
    }
    sb.append(arrays[1]);
    return sb.toString();
  }

  private String getEndDate() {
    //courseTime = 2017-05-07 12:42:00
    String[] arrays = courseTime.split(" ")[1].split(":");
    StringBuilder sb = new StringBuilder();
    sb.append(courseTime.split(" ")[0]);
    sb.append(" ");

    int startTime = Integer.parseInt(arrays[0]) + courseIndex;
    if(startTime <= 9){
      sb.append("0" + startTime + ":");
    }else {
      sb.append(startTime + ":");
    }
    sb.append(arrays[1] + ":00");
    return sb.toString();
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
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

  @Override
  public void positiveListener() {
    tvShowTime.setText(mTimePickerDialog.getDetailTime());
  }

  @Override
  public void negativeListener() {

  }

  @Override
  public void onRadiogroupPositive() {
    tvShowCourseType.setText(mRadiaGroupDialog.getSelected());
  }

  @Override
  public void onRadiogroupNegative() {

  }

  @Override
  public void onPicRadiogroupPositive() {
    coursePicUrl = trainerModel.getHm_ptwi_images().get(mPicRadiaGroupDialog.getSelected()).getIurl();
    PreferenceHelper.getInstance().setCoursePicture(coursePicUrl);
    Picasso.with(this).load(coursePicUrl).into(ivShowCoursePic);
  }

  @Override
  public void onPicRadiogroupNegative() {

  }
}
