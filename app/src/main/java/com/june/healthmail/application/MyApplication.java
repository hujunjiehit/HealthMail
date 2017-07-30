package com.june.healthmail.application;

import android.app.Application;
import android.util.Log;

import com.june.healthmail.Config.BmobConfig;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import cn.bmob.v3.Bmob;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MyApplication extends Application{

  private RefWatcher mRefWatcher;

  @Override
  public void onCreate() {
    super.onCreate();
    Bmob.initialize(this, BmobConfig.applicationId);

    mRefWatcher = LeakCanary.install(this);

    // 使用推送服务时的初始化操作
   // BmobInstallation.getCurrentInstallation().save();
    // 启动推送服务
    //BmobPush.startWork(this);

    //创建appversion表，只需执行一次
    //BmobUpdateAgent.initAppVersion();

    PreferenceHelper.getInstance().setContext(this);
    DBManager.getInstance().setContext(this);
    HttpUntils.getInstance().setContext(this);

    CrashReport.initCrashReport(getApplicationContext(), "c3044648f0", false);
    Log.e("test","uid = " + PreferenceHelper.getInstance().getUid());
    CrashReport.setUserId(PreferenceHelper.getInstance().getUid());
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
  }

}
