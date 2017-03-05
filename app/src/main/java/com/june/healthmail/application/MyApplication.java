package com.june.healthmail.application;

import android.app.Application;

import com.june.healthmail.Config.BmobConfig;
import com.june.healthmail.untils.PreferenceHelper;

import cn.bmob.v3.Bmob;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MyApplication extends Application{

  @Override
  public void onCreate() {
    super.onCreate();
    Bmob.initialize(this, BmobConfig.applicationId);

    PreferenceHelper.getInstance().setContext(this);
  }
}
