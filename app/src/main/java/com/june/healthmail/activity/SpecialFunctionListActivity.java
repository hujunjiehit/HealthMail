package com.june.healthmail.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.model.TrainerModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;

import java.io.Serializable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

/**
 * Created by june on 2017/7/15.
 */

public class SpecialFunctionListActivity extends BaseActivity {
  
  private UserInfo userInfo;
  private String accessToken;
  private TrainerModel trainerModel;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if(!CommonUntils.hasPermission()){
      Toast.makeText(this,"当前用户无授权，无法进入本页面",Toast.LENGTH_SHORT).show();
      finish();
    }
    setContentView(R.layout.activity_special_function_list);
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

  @OnClick(R.id.btn_function_1)
  public void btnFunction1(View view){
    //自动发课功能
    Intent it = new Intent();
    it.putExtra("accessToken",accessToken);
    it.putExtra("trainerModel", (Serializable)trainerModel);
    it.setClass(SpecialFunctionListActivity.this,PostCourseDetailActivity.class);
    startActivity(it);
  }

  @OnClick(R.id.btn_function_2)
  public void btnFunction2(View view){
    //关注详情
    Intent it = new Intent();
    it.putExtra("accessToken",accessToken);
    it.putExtra("trainerModel", (Serializable)trainerModel);
    it.setClass(SpecialFunctionListActivity.this,GuanzhuDetailActivity.class);
    startActivity(it);
  }

  @OnClick(R.id.btn_function_3)
  public void btnFunction3(View view){
    //评价详情
    Intent it = new Intent();
    it.putExtra("accessToken",accessToken);
    it.putExtra("trainerModel", (Serializable)trainerModel);
    it.setClass(SpecialFunctionListActivity.this,PingjiaDetailActivity.class);
    startActivity(it);
  }

  @OnClick(R.id.img_back)
  public void btnImageBack(View view){
    finish();
  }

  private void initView() {
  }

  private void setListener() {
  }

  private void initData() {
  }
}
