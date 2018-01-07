package com.june.healthmail.improve.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.june.healthmail.R;
import com.june.healthmail.improve.activity.NewPingjiaActivity;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.service.AutopayAccessibilityService;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/9/26.
 */

public class FukuanService extends BaseService {

  private FukuanBinder mBinder = new FukuanBinder();
  private NotificationCompat.Builder mNotifyBuilder;

  public static final int START_TO_FUKUAN = 1;
  public static final int NEXT_ACCOUNT = 2;
  public static final int SHOW_TOAST = 3;
  public static final int TASK_FINISHED = 12;

  private int coinsCost;

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case START_TO_FUKUAN:
          AutopayAccessibilityService.setServiceHandler(mHandler);
          if (isRunning) {
            if(userInfo.getCoinsNumber() > 0) {
              if (accountIndex < accountList.size()) {
                showTheResult("开始付款第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
                if (accountList.get(accountIndex).getStatus() == 1) {
                  //getAccountToken();
                  mNotifyBuilder.setContentTitle("金币余额：" + userInfo.getCoinsNumber());
                  mNotifyBuilder.setContentText("正在付款第" + (accountIndex + 1) + "个号...");
                  mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
                  startForeground(1, mNotifyBuilder.build());

                  updateTheCoinsNumber();
                  showTheResult("-----金币余额-1\n");
                } else {
                  showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                  accountIndex++;
                  message = this.obtainMessage(START_TO_FUKUAN);
                  message.sendToTarget();
                }
              } else {
                mNotifyBuilder.setContentText("所有账号付款结束");
                mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
                startForeground(1, mNotifyBuilder.build());
                showTheResult("******所有账号付款结束**********\n");
                isRunning = false;
                finishFukuan();
              }
            }else {
              mNotifyBuilder.setContentText("金币余额不足，付款结束");
              mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
              startForeground(1, mNotifyBuilder.build());

              showTheResult("******金币余额不足，付款结束**********\n");
              isRunning = false;
              finishFukuan();
            }
          } else {
            mNotifyBuilder.setContentText("用户自己终止付款，付款结束");
            mNotifyBuilder.setProgress(accountList.size(), accountIndex, false);
            startForeground(1, mNotifyBuilder.build());

            showTheResult("**用户自己终止付款**当前已经执行完成" + accountIndex + "个小号\n");
          }
          break;
        case NEXT_ACCOUNT:
          Log.e("test","NEXT_ACCOUNT");
          accountIndex++;
          message = this.obtainMessage(START_TO_FUKUAN);
          message.sendToTarget();
          break;
        case SHOW_TOAST:
          toast((String) msg.obj);
          break;
        case TASK_FINISHED:
          Log.e("test","task finished");
          mHandler = null;
          break;
        default:
          Log.e("test","undefined message");
          break;
      }
    }
  };

  @Override
  public void onCreate() {
    super.onCreate();

    Intent notificationIntent = new Intent(this, NewPingjiaActivity.class);
    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    mNotifyBuilder = new NotificationCompat.Builder(this)
        .setContentTitle("猫友圈付款")
        .setContentText("点击查看付款详情...")
        .setWhen(System.currentTimeMillis())
        .setSmallIcon(R.drawable.login_dog)
        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.login_dog))
        .setContentIntent(pendingIntent);
    startForeground(1, mNotifyBuilder.build());

    Log.e("test", "FukuanService onCreate");
    initData();
  }

  private void initData() {
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    coinsCost = PreferenceHelper.getInstance().getPayCost();
    accountList = CommonUntils.loadAccountInfo();
    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
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
    Log.e("test", "PingjiaService onDestroy");
    updateUserInfo();
    release();
  }

  @Override
  protected void release() {
    super.release();
    mBinder = null;
    mHandler.removeCallbacksAndMessages(null);
    mHandler.sendEmptyMessage(TASK_FINISHED);
  }

  private void sendToService(String action, Bundle bundle) {
    Intent intent = new Intent(this, AutopayAccessibilityService.class);
    intent.setAction(action);
    if (bundle != null) {
      intent.putExtras(bundle);
    }
    startService(intent);
  }

  private void updateTheCoinsNumber(){
    if(userInfo == null) {
      userInfo = BmobUser.getCurrentUser(UserInfo.class);
    }
    if(coinsCost > 0) {
      userInfo.setCoinsNumber(userInfo.getCoinsNumber() - coinsCost);
      userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
        @Override
        public void done(BmobException e) {
          if(e == null){
            Log.e("test","updateTheCoinsNumber 更新用户积分成功");
            Bundle bundel = new Bundle();
            bundel.putSerializable("account",accountList.get(accountIndex));
            sendToService(AutopayAccessibilityService.INTENT_ACTION_FUKUAN,bundel);
          }
        }
      });
    }
  }

  public class FukuanBinder extends Binder {

    public void startFukuan() {
      isRunning = true;
      Message msg = mHandler.obtainMessage(START_TO_FUKUAN);
      AutopayAccessibilityService.setIsFirstTime(true);
      msg.sendToTarget();
    }

    public void stopFukuan() {
      isRunning = false;
      if(mHandler != null) {
        mHandler.removeCallbacksAndMessages(null);
      }
      sendToService(AutopayAccessibilityService.INTENT_ACTION_STOP,null);
    }

    public void setHandler(Handler handler) {
      mActivityHandler = handler;
    }
  }
}
