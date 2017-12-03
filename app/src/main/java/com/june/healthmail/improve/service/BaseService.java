package com.june.healthmail.improve.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;

import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/8/19.
 */

public class BaseService extends Service {

  protected Handler mActivityHandler;
  public static final int SHOW_RESULT = 99;
  public static final int UPDATE_TIMES = 100;
  public static final int FINISH_PINGJIA = 101;
  public static final int FINISH_YUEKE = 102;

  protected int min_time;
  protected int max_time;
  protected int max_sijiao;
  protected Boolean isRunning = false;

  protected ArrayList<AccountInfo> accountList = new ArrayList<>();
  protected int accountIndex = 0;
  protected String accessToken;
  protected String errmsg;
  protected Message message;
  protected Gson gson = new Gson(); //java.lang.IllegalStateException
  protected UserInfo userInfo;

  @Nullable
  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  protected void showTheResult(String str) {
    if (mActivityHandler != null) {
      Message msg = mActivityHandler.obtainMessage(SHOW_RESULT);
      msg.obj = str;
      msg.sendToTarget();
    }
  }

  protected void finishPingjia() {
    if (mActivityHandler != null) {
      Message msg = mActivityHandler.obtainMessage(FINISH_PINGJIA);
      msg.sendToTarget();
    }
  }

  protected void finishYueke() {
    if (mActivityHandler != null) {
      Message msg = mActivityHandler.obtainMessage(FINISH_YUEKE);
      msg.sendToTarget();
    }
  }

  protected void updateTimes(int times) {
    if (mActivityHandler != null) {
      Message msg = mActivityHandler.obtainMessage(UPDATE_TIMES);
      msg.arg1 = times;
      msg.sendToTarget();
    }
  }

  protected int getDelayTime() {
    int randTime = CommonUntils.getRandomInt(min_time, max_time);
    Log.d("test", "randTime = " + randTime);
    return randTime;
  }

  protected void updateUserInfo() {
    if(userInfo != null) {
      userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
        @Override
        public void done(BmobException e) {
          if (e == null) {
            Log.e("test", "更新用户信息成功");
          } else {
            Log.e("test", "更新用户信息失败");
          }
        }
      });
    }
  }

  protected void release() {
    accountList = null;
    mActivityHandler = null;
    gson = null;
    userInfo = null;
    message = null;
    errmsg = null;
    accessToken = null;
  }

  protected void toast(String str){
    Toast.makeText(this,str,Toast.LENGTH_LONG).show();
  }
}
