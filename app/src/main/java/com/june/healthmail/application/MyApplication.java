package com.june.healthmail.application;

import android.app.Application;
import android.util.Log;

import com.june.healthmail.Config.BmobConfig;
import com.june.healthmail.untils.PreferenceHelper;
import com.tencent.bugly.crashreport.CrashReport;

import net.youmi.android.normal.spot.SpotManager;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.update.BmobUpdateAgent;

/**
 * Created by bjhujunjie on 2017/3/2.
 */

public class MyApplication extends Application{

  @Override
  public void onCreate() {
    super.onCreate();
    Bmob.initialize(this, BmobConfig.applicationId);

    // 使用推送服务时的初始化操作
   // BmobInstallation.getCurrentInstallation().save();
    // 启动推送服务
    //BmobPush.startWork(this);

    //创建appversion表，只需执行一次
    //BmobUpdateAgent.initAppVersion();

    PreferenceHelper.getInstance().setContext(this);

    CrashReport.initCrashReport(getApplicationContext(), "c3044648f0", false);
    CrashReport.setUserId(PreferenceHelper.getInstance().getUid());
  }

  @Override
  public void onTerminate() {
    SpotManager.getInstance(getApplicationContext()).onAppExit();
    Log.e("test","application onTerminate");
    super.onTerminate();
  }

}
