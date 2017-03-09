package com.june.healthmail.application;

import android.app.Application;

import com.june.healthmail.Config.BmobConfig;
import com.june.healthmail.untils.PreferenceHelper;
import com.tencent.bugly.crashreport.CrashReport;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MyApplication extends Application{

  @Override
  public void onCreate() {
    super.onCreate();
    Bmob.initialize(this, BmobConfig.applicationId);

    //创建appversion表，只需执行一次
    //BmobUpdateAgent.initAppVersion();

    PreferenceHelper.getInstance().setContext(this);

    CrashReport.initCrashReport(getApplicationContext(), "c3044648f0", false);
  }
}
