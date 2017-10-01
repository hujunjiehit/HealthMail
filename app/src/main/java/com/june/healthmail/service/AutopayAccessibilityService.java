package com.june.healthmail.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.june.healthmail.Config.DeviceConfig;
import com.june.healthmail.improve.service.FukuanService;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.PreferenceHelper;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by june on 2017/9/11.
 */

public class AutopayAccessibilityService extends AccessibilityService {

  public static final String INTENT_ACTION_STATUS_CHANGE ="com.june.healthmail.status.change";
  public static final String INTENT_ACTION_FUKUAN = "com.june.healthmail.action.fukuan";//开始付款
  public static final String INTENT_ACTION_STOP = "com.june.healthmail.action.stop";//停止

  //窗口状态
  public static final int STATE_NONE = 0;
  public static final int STATE_WAITING_FOR_LOADING = 1;
  public static final int STATE_BEFORE_SMS_CODE = 2;
  public static final int STATE_WAITING_SMS_CODE = 3;
  public static final int STATE_RECEIVE_SMS_CODE = 4;
  public static final int STATE_AFTER_SMS_CODE = 5;
  public static final int STATE_OTHER = -1;

  //当前窗口状态
  private int mCurrentState = STATE_NONE;
  private UserInfo userInfo;

  private ClipboardManager mClipboardManager;
  private AccessibilityNodeInfo mRootNodeInfo;
  private AccessibilityNodeInfo mResultInfo;
  private ExecutorService mExecutorService = Executors.newSingleThreadExecutor(); //建一个单线程的线程池

  private String code; //收到的验证码
  private PreferenceHelper mPreferenceHelper;
  private List<AccessibilityNodeInfo> mListEditText = new ArrayList<>();
  private List<AccessibilityNodeInfo> nodeList;
  private AccountInfo accountInfo;
  private boolean isInLoginActivity;
  protected Message message;
  protected boolean isRunning = true;

  //付款模式  1--快捷支付(储蓄卡)  2--快捷支付
  private static int PAY_MODE;

  private static Handler serviceHandler;
  private static boolean isFirstTime;




  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.e("autopay", "onStartCommand");
    if(intent != null) {
      if (INTENT_ACTION_FUKUAN.equals(intent.getAction()) && intent.getExtras() != null) {
        Bundle bundle = intent.getExtras();
        accountInfo = (AccountInfo) bundle.getSerializable("account");
        Log.e("autopay", "start to fukuan: " + accountInfo.toString());

        isInLoginActivity = false;
        isRunning = true;
        startToFukuan();
      }else if(INTENT_ACTION_STOP.equals(intent.getAction())){
        Log.e("autopay", "stop fukuan thread");
        //mExecutorService.shutdownNow();
        isRunning = false;
      }
    }
    return super.onStartCommand(intent, flags, startId);
  }

  private void startToFukuan() {
    mExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        Log.e("autopay", "startToFukuan  isFirstTime = " + isFirstTime);
        if(isFirstTime) {
          //1、等待进入健康猫登录界面
          waitingForEnterLogin();
        }
        SystemClock.sleep(200);
        doLoginAction();
      }
    });
  }



  private void doLoginAction() {
    mRootNodeInfo = null;
    while (mRootNodeInfo == null) {
      SystemClock.sleep(400);
      Log.i("autopay", "mRootNodeInfo is null, waiting...");
      mRootNodeInfo = getRootInActiveWindow();
    }

    Log.e("autopay", "user now enter login activity");
    //进入登录界面

    //输入用户名
    nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.zhanyun.ihealth:id/etv_login_username");
    if(nodeList.size() > 0) {
      Bundle arguments = new Bundle();
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, accountInfo.getPhoneNumber());
      nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    //输入密码
    nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.zhanyun.ihealth:id/etv_login_password");
    if(nodeList.size() > 0) {
      Bundle arguments = new Bundle();
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, accountInfo.getPassWord());
      nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    //点击登录
    nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.zhanyun.ihealth:id/btn_login_login");
    if(nodeList.size() > 0) {
      if(nodeList.get(0).isClickable()) {
        nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);

        //等待登录成功
        //com.zhanyun.ihealth:id/rl_non_payment_orders
        AccessibilityNodeInfo targetNode = waitUntilTargetNodeAppear("com.zhanyun.ihealth:id/rl_non_payment_orders",8000);
        if(targetNode != null) {
          Log.e("autopay", "login sucess, targetNode = " + targetNode);
          if(targetNode.isClickable()) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
          }

          targetNode = waitUntilTargetNodeAppear("com.zhanyun.ihealth:id/ll_check_all",3000);
          if(targetNode != null) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            SystemClock.sleep(500);
            targetNode = waitUntilTargetNodeAppear("com.zhanyun.ihealth:id/btn_pay",2000);
            if(targetNode != null) {
              if(targetNode.isClickable()){
                targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
              }
            }
            Log.e("autopay", "waiting users to return mine page");
            waitingForEnterSetup();
          }else {
            Log.e("autopay", "waiting users to return mine page");
            waitingForEnterSetup();
          }
        }
      }
    }
  }

  private void waitingForEnterSetup() {
    int tryTimes = 0;
    nodeList.clear();

    mRootNodeInfo = null;
    while (mRootNodeInfo == null) {
      SystemClock.sleep(400);
      Log.i("autopay", "mRootNodeInfo is null, waiting...");
      mRootNodeInfo = getRootInActiveWindow();
    }

    if(mRootNodeInfo != null) {
      nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.zhanyun.ihealth:id/btn_setting_quit_login");
    }
    //等待目标node出现
    while(nodeList.size() == 0) {
      Log.e("autopay", "waiting for entry setup...tryTimes = " + tryTimes);
      if(tryTimes % 10 == 0){
        //Toast.makeText(this, "付款之后请进入设置界面", Toast.LENGTH_SHORT).show();
        Log.e("autopay", "toast....");
        if(serviceHandler != null) {
          Log.e("autopay", "next account");
          message = serviceHandler.obtainMessage(FukuanService.SHOW_TOAST);
          message.obj = "请在付款之后请进入设置界面...";
          message.sendToTarget();
        }
      }
      if(isRunning == false) {
        break;
      }
      SystemClock.sleep(2000);

      mRootNodeInfo = null;
      while (mRootNodeInfo == null) {
        SystemClock.sleep(400);
        Log.i("autopay", "mRootNodeInfo is null, waiting...");
        mRootNodeInfo = getRootInActiveWindow();
      }
      if(mRootNodeInfo != null) {
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.zhanyun.ihealth:id/btn_setting_quit_login");
      }
      tryTimes++;
    }
    if(isRunning) {
      if(nodeList.get(0).isClickable()){
        Log.e("autopay", "click logout");
        nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(800);
      }

      mRootNodeInfo = null;
      while (mRootNodeInfo == null) {
        SystemClock.sleep(400);
        Log.i("autopay", "mRootNodeInfo is null, waiting...");
        mRootNodeInfo = getRootInActiveWindow();
      }
      nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("android:id/button1");
      if(nodeList.size() > 0) {
        Log.e("autopay", "click sure");
        nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(800);
      }

      AccessibilityNodeInfo targetNode = waitUntilTargetNodeAppear("com.zhanyun.ihealth:id/ll_tab_me",8000);
      if(targetNode != null) {
        Log.e("autopay", "click mine");
        targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(500);
      }

      if(serviceHandler != null) {
        Log.e("autopay", "next account");
        serviceHandler.sendEmptyMessageDelayed(FukuanService.NEXT_ACCOUNT, 2000);
      }
    }
  }

  private void waitingForEnterLogin() {
    int tryTimes = 0;
    while (isInLoginActivity == false) {

      if(tryTimes % 10 == 0) {
        if (serviceHandler != null) {
          message = serviceHandler.obtainMessage(FukuanService.SHOW_TOAST);
          message.obj = "请手动进入健康猫登录界面...";
          message.sendToTarget();
        }
      }
      if(isRunning == false) {
        break;
      }
      SystemClock.sleep(2000);
      Log.e("autopay", "waiting for enter login activity");
      tryTimes++;
    }
    isFirstTime = false;
  }


  private AccessibilityNodeInfo waitUntilTargetNodeAppear(String resId, int maxTime) {
      int tryTimes = 0;
      nodeList.clear();

    mRootNodeInfo = null;
    while (mRootNodeInfo == null) {
      SystemClock.sleep(400);
      Log.i("autopay", "mRootNodeInfo is null, waiting...");
      mRootNodeInfo = getRootInActiveWindow();
    }
      if(mRootNodeInfo != null) {
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(resId);
      }

      //等待目标node出现
     while(nodeList.size() == 0) {
          Log.e("autopay", "waiting for target... resId = " +  resId + "   tryTimes = " + tryTimes);
       if(tryTimes > maxTime/1000) {
         break;
       }
       SystemClock.sleep(1000);

       mRootNodeInfo = null;
       while (mRootNodeInfo == null) {
         SystemClock.sleep(400);
         Log.i("autopay", "mRootNodeInfo is null, waiting...");
         mRootNodeInfo = getRootInActiveWindow();
       }
       if(mRootNodeInfo != null) {
          nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId(resId);
       }
       tryTimes++;
     }
     if(nodeList.size() > 0) {
       return nodeList.get(0);
     }else {
       return null;
     }
  }

  @Override
  protected void onServiceConnected() {
    super.onServiceConnected();
    Log.e("test", "onServiceConnected");
    Toast.makeText(this, "_已开启辅助服务_", Toast.LENGTH_SHORT).show();
//    userInfo = BmobUser.getCurrentUser(UserInfo.class);
//    if(userInfo.getAutoPay() != null && userInfo.getAutoPay() > 0) {
//      Log.e("test", "开启");
//      Toast.makeText(this, "_已开启辅助服务_", Toast.LENGTH_SHORT).show();
//      mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//      mPreferenceHelper = PreferenceHelper.getInstance();
//      IntentFilter intentFilter = new IntentFilter();
//      intentFilter.addAction(INTENT_ACTION_STATUS_CHANGE);
//      LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, intentFilter);
//    }else {
//      Log.e("test", "关闭");
//      Toast.makeText(this, "_当前用户无权限开启辅助服务_", Toast.LENGTH_SHORT).show();
//      stopSelf();
//    }
  }


  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
//    if(userInfo.getAutoPay() == null || userInfo.getAutoPay() < 1){
//      return;
//    }
    Log.e("test","onAccessibilityEvent:" + event);
    int eventType = event.getEventType();
    switch (eventType) {
      case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: //窗口状态变化的时候处理
        Log.e("test","TYPE_WINDOW_STATE_CHANGED");
        Log.e("test","event.getText() = " + event.getText());
        if(event.getClassName().equals("com.gzdxjk.healthmall.ui.login.LoginActivity")) {
          isInLoginActivity = true;
        }

          break;
      case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: //窗口内容变化的时候处理
        Log.e("test","TYPE_WINDOW_CONTENT_CHANGED");
        break;
      case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
        Log.e("test","TYPE_VIEW_TEXT_SELECTION_CHANGED,  text = " + event.getText());
        break;
      default:
        break;
    }
  }


  private void goBack() {
    SystemClock.sleep(1000);
    mRootNodeInfo = null;
    while (mRootNodeInfo == null) {
      SystemClock.sleep(400);
      Log.i("autopay", "mRootNodeInfo is null, waiting...");
      mRootNodeInfo = getRootInActiveWindow();
    }
    if(mRootNodeInfo != null) {
      List<AccessibilityNodeInfo> nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("关闭"); //找付款方式
      if(nodeList.size() > 0) {
        Log.e("test","perform action click close");
        SystemClock.sleep(500);
        nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
      }
    }
  }

  /**
   * 通联付款自动付款
   */
  private void autoPayTonglian() {
    SystemClock.sleep(200);

    mExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        AccessibilityNodeInfo targetInfo = null;
        mRootNodeInfo = null;

        SystemClock.sleep(2000);

        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }

          try {
            targetInfo =  mRootNodeInfo.getChild(3).getChild(0).getChild(0).getChild(0);
            if(targetInfo != null) {
              Log.e("autopay","targetInfo is not null, targetInfo.getChildCount = " + targetInfo.getChildCount());
            }
          }catch (Exception e) {
            Log.e("autopay","targetInfo is null, waiting for loading... tryTimes = " + tryTimes);
            targetInfo = null;
            SystemClock.sleep(2000);
            tryTimes++;
          }
          if(tryTimes >= 20 || mCurrentState == STATE_NONE) {
            targetInfo = null;
            break;
          }
        }

        if(targetInfo != null) {
          mCurrentState = STATE_BEFORE_SMS_CODE;
          SystemClock.sleep(500);

          mResultInfo = null;
          if(PreferenceHelper.getInstance().getChoosePayCard()) {
            //需要指定银行卡
            getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"添加常用卡");
            //getTargetNodeByClassName2(mRootNodeInfo.getChild(3).getChild(0),"android.widget.ListView");
            if(mResultInfo != null) {
              mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
              SystemClock.sleep(1500);

              mRootNodeInfo = null;
              while (mRootNodeInfo == null) {
                SystemClock.sleep(400);
                Log.i("autopay", "mRootNodeInfo is null, waiting...");
                mRootNodeInfo = getRootInActiveWindow();
              }

              mListEditText.clear();
              getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
              Log.e("autopay","mListEditText.size = " + mListEditText.size());
              if(mListEditText.size() > 0) {
                Log.e("autopay","EditText = " +  mListEditText.get(0));
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayBankCard());
                mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                SystemClock.sleep(500);
              }

              mResultInfo = null;
              getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"下一步");
              if(mResultInfo != null && mResultInfo.isClickable()) {
                mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                SystemClock.sleep(500);
              }
            }
          }else {
            //使用默认的第一张银行卡付款
            getTargetNodeByClassName2(mRootNodeInfo.getChild(3).getChild(0),"android.widget.ListView");
            if(mResultInfo != null) {
              Rect rect = new Rect();
              mResultInfo.getChild(0).getBoundsInScreen(rect);
              clickPoint((rect.left + rect.right)/2,(rect.top + rect.bottom)/2);
              SystemClock.sleep(500);
            }
          }
          mCurrentState = STATE_WAITING_SMS_CODE;
          tonglianStepTwo();
        }else {
          SystemClock.sleep(500);
          mCurrentState = STATE_WAITING_FOR_LOADING;
          goBack();
        }
      }
    });
  }

  private void tonglianStepTwo() {
    SystemClock.sleep(1000);

    try {
      mRootNodeInfo = null;
      while (mRootNodeInfo == null) {
        SystemClock.sleep(400);
        Log.i("autopay", "mRootNodeInfo is null, waiting...");
        mRootNodeInfo = getRootInActiveWindow();
      }

      mResultInfo = null;
      getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"获取验证码");
      if(mResultInfo != null) {
        //需要获取验证码
        Log.e("autopay","need to get sms code");
        int size1 = mResultInfo.getContentDescription().toString().length();
        while (mResultInfo != null && mResultInfo.getContentDescription().toString().length() <= size1) {
          Log.e("autopay","text length = " + mResultInfo.getContentDescription().toString().length());
          mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
          SystemClock.sleep(3000);
          mResultInfo = null;

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }
          getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"获取验证码");
        }
        getSmsCode();
      } else {
        //不要验证码，直接输入支付密码就行了
        Log.e("autopay","no need to get sms code");
        SystemClock.sleep(500);
        mListEditText.clear();
        getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
        if(mListEditText.size() == 1) {

          Bundle arguments = new Bundle();
          arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayPassword());
          mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
          SystemClock.sleep(1500);

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }
          mResultInfo = null;
          getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"立即支付");
          if(mResultInfo != null && mResultInfo.isClickable()) {
            mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            SystemClock.sleep(3000);
          }else {
            SystemClock.sleep(1000);

            mRootNodeInfo = null;
            while (mRootNodeInfo == null) {
              SystemClock.sleep(400);
              Log.i("autopay", "mRootNodeInfo is null, waiting...");
              mRootNodeInfo = getRootInActiveWindow();
            }
            mResultInfo = null;
            getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"立即支付");
            if(mResultInfo != null) {
              mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
              SystemClock.sleep(3000);
            }
          }
          mCurrentState = STATE_AFTER_SMS_CODE;
          goBack();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 快钱储蓄卡自动付款
   */
  private void autoPayKuaiqian() {
    SystemClock.sleep(200);

    mExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        AccessibilityNodeInfo targetInfo = null;
        mRootNodeInfo = null;

        SystemClock.sleep(2000);

        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }
          try {
            targetInfo =  mRootNodeInfo.getChild(3).getChild(0).getChild(0).getChild(0);
            if(targetInfo != null) {
              Log.e("autopay","targetInfo is not null, targetInfo.getChildCount = " + targetInfo.getChildCount());
            }
          }catch (Exception e) {
            Log.e("autopay","targetInfo is null, waiting for loading... tryTimes = " + tryTimes);
            targetInfo = null;
            SystemClock.sleep(2000);
            tryTimes++;
          }
          if(tryTimes >= 20 || mCurrentState == STATE_NONE) {
            targetInfo = null;
            break;
          }
        }

        if(targetInfo != null) {
          mCurrentState = STATE_BEFORE_SMS_CODE;
          SystemClock.sleep(500);

          mResultInfo = null;
          getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"获取验证码");
          if(mResultInfo != null) {
            Log.e("autopay","perform action click 获取验证码");
            mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            SystemClock.sleep(500);

            mCurrentState = STATE_WAITING_SMS_CODE;
            getSmsCode();
          }
        }else {
          SystemClock.sleep(500);
          mCurrentState = STATE_WAITING_FOR_LOADING;
          goBack();
        }
      }
    });
  }

  /**
   * 快钱信用卡自动付款
   */
  private void autoPayKuaiqianXinyongka() {
    SystemClock.sleep(200);

    mExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        AccessibilityNodeInfo targetInfo = null;
        mRootNodeInfo = null;

        SystemClock.sleep(2000);

        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(100);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }
          try {
            targetInfo =  mRootNodeInfo.getChild(3).getChild(0).getChild(0).getChild(0);
            if(targetInfo != null) {
              Log.e("autopay","targetInfo is not null, targetInfo.getChildCount = " + targetInfo.getChildCount());
            }
          }catch (Exception e) {
            Log.e("autopay","targetInfo is null, waiting for loading... tryTimes = " + tryTimes);
            targetInfo = null;
            SystemClock.sleep(2000);
            tryTimes++;
          }
          if(tryTimes >= 20 || mCurrentState == STATE_NONE) {
            targetInfo = null;
            break;
          }
        }

        if(targetInfo != null) {
          mCurrentState = STATE_BEFORE_SMS_CODE;
          SystemClock.sleep(1000);

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }
          mListEditText.clear();
          getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
          if(mListEditText.size() == 2) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getCreditCode());
            mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            SystemClock.sleep(500);
          }else if(mListEditText.size() == 3) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getCreditDate());
            mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            SystemClock.sleep(500);

            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getCreditCode());
            mListEditText.get(1).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

            SystemClock.sleep(500);
          }

          mResultInfo = null;
          getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"获取验证码");
          if(mResultInfo != null) {
            Log.e("autopay","perform action click 获取验证码");
            mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            SystemClock.sleep(500);

            mCurrentState = STATE_WAITING_SMS_CODE;
            getSmsCode();
          }
        }else {
          SystemClock.sleep(500);
          mCurrentState = STATE_WAITING_FOR_LOADING;
          goBack();
        }
      }
    });
  }

  private void getTargetNodeByDesc(AccessibilityNodeInfo info, String desc) {
    //Log.e("autopay","getTargetNodeByDesc  childCount = " + info.getChildCount());
    if(info == null) {
      return;
    }
    if(info.getChildCount() == 0) {
      Log.e("autopay","info.getContentDescription = " + info.getContentDescription());
      if(info.getContentDescription() != null && info.getContentDescription().toString().trim().contains(desc)) {
        mResultInfo = info;
      }
    } else {
      for(int i = 0; i < info.getChildCount(); i++) {
        if(info.getChild(i) != null) {
         getTargetNodeByDesc(info.getChild(i),desc);
        }
      }
    }
  }

  private void getTargetNodeBySize(AccessibilityNodeInfo info, int size) {
    if(info == null) {
      return;
    }
    if(info.getChildCount() == 0) {
      Log.e("autopay","info.getContentDescription = " + info.getContentDescription());
      if(info.getContentDescription() != null) {
        Log.e("autopay","info.getContentDescription.length = " + info.getContentDescription().length());
      }
      if(info.getContentDescription() != null && info.getContentDescription().length() == size) {
        mResultInfo = info;
      }
    } else {
      for(int i = 0; i < info.getChildCount(); i++) {
        if(info.getChild(i) != null) {
          getTargetNodeBySize(info.getChild(i),size);
        }
      }
    }
  }

  private void getTargetNodeByClassName(AccessibilityNodeInfo info, String className) {
    if(info == null) {
      return;
    }
    if(info.getChildCount() == 0) {
      Log.e("autopay","info.getClassName = " + info.getClassName());
      if(info.getClassName() != null && className.equals(info.getClassName().toString().trim())) {
        mResultInfo = info;
      }
    } else {
      for(int i = 0; i < info.getChildCount(); i++) {
        if(info.getChild(i) != null) {
          getTargetNodeByClassName(info.getChild(i),className);
        }
      }
    }
  }

  private void getTargetNodeByClassName2(AccessibilityNodeInfo info, String className) {
    if(info == null) {
      return;
    }
    Log.e("autopay","info.getClassName = " + info.getClassName());
    if(info.getClassName() != null && className.equals(info.getClassName().toString().trim())) {
      mResultInfo = info;
    }
    if(info.getChildCount() == 0) {
      return;
    } else {
      for(int i = 0; i < info.getChildCount(); i++) {
        if(info.getChild(i) != null) {
          getTargetNodeByClassName2(info.getChild(i),className);
        }
      }
    }
  }

  private void getAllEditText(AccessibilityNodeInfo info) {
    if(info == null) {
      return;
    }
    if(info.getChildCount() == 0) {
      Log.e("autopay","info.getClassName = " + info.getClassName());
      if(info.getClassName() != null && "android.widget.EditText".equals(info.getClassName().toString().trim())) {
        mListEditText.add(info);
      }
    } else {
      for(int i = 0; i < info.getChildCount(); i++) {
        if(info.getChild(i) != null) {
          getAllEditText(info.getChild(i));
        }
      }
    }
  }

  /**
   * 快捷支付 点击下拉箭头，弹出选择银行卡界面
   */
  private void stepOne() {
    SystemClock.sleep(200);

    mExecutorService.execute(new Runnable() {
      @Override
      public void run() {
        AccessibilityNodeInfo targetInfo = null;
        mRootNodeInfo = null;

        SystemClock.sleep(2000);
        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {

          mRootNodeInfo = null;
          while (mRootNodeInfo == null) {
            SystemClock.sleep(400);
            Log.i("autopay", "mRootNodeInfo is null, waiting...");
            mRootNodeInfo = getRootInActiveWindow();
          }

          try {
            targetInfo =  mRootNodeInfo.getChild(3).getChild(0).getChild(0).getChild(0);
          }catch (Exception e) {
            Log.e("autopay","targetInfo is null, waiting for loading... tryTimes = " + tryTimes);
            targetInfo = null;
            SystemClock.sleep(2000);
            tryTimes++;
          }
          if(tryTimes >= 20 || mCurrentState == STATE_NONE) {
            targetInfo = null;
            break;
          }
        }

        if(targetInfo != null) {
          mCurrentState = STATE_BEFORE_SMS_CODE;
          SystemClock.sleep(500);

          mResultInfo = null;
          getTargetNodeBySize(mRootNodeInfo.getChild(3).getChild(0),1);
          if(mResultInfo != null && mResultInfo.isClickable()) {
            mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            SystemClock.sleep(500);
            stepTwo();
          }
        }else {
          SystemClock.sleep(500);
          mCurrentState = STATE_WAITING_FOR_LOADING;
          goBack();
        }
      }
    });
  }

  /**
   * 点击使用其它银行卡
   */
  private void stepTwo() {
    SystemClock.sleep(500);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    mResultInfo = null;
    getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"使用其他银行卡");
    if(mResultInfo != null && mResultInfo.isClickable()) {
      Log.e("autopay","perform action click targetInfo stepTwo");
      mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
      SystemClock.sleep(500);
      stepThree();
    }
  }

  /**
   * 输入银行卡号
   */
  private void stepThree() {
    SystemClock.sleep(200);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    try {
      mResultInfo = null;
      getTargetNodeByClassName(mRootNodeInfo.getChild(3).getChild(0),"android.widget.EditText");
      if(mResultInfo != null) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayBankCard());
        mResultInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
      }

      SystemClock.sleep(500);

      mResultInfo = null;
      getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"下一步");
      if(mResultInfo != null && mResultInfo.isClickable()) {
        Log.e("autopay","perform action click targetInfo stepThree");
        mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(500);
        stepFour();
      }
    }catch (Exception e) {
      e.printStackTrace();
    }

  }

  /**
   * 输入姓名、身份证、手机号
   */
  private void stepFour() {
    SystemClock.sleep(1000);

    mListEditText.clear();
    getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
    if(mListEditText.size() == 3) {
      Bundle arguments = new Bundle();
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayName());
      mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

      SystemClock.sleep(500);

      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayIdCard());
      mListEditText.get(1).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

      SystemClock.sleep(500);
      
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayPhoneNumber());
      mListEditText.get(2).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    mResultInfo = null;
    getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"下一步");
    if(mResultInfo != null && mResultInfo.isClickable()) {
      Log.e("autopay","perform action click targetInfo stepFour");
      mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
      SystemClock.sleep(500);
      mCurrentState = STATE_WAITING_SMS_CODE;
      getSmsCode();
    }
  }

  private void repeatTheSame() {
    SystemClock.sleep(200);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    List<AccessibilityNodeInfo> nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("重新付款"); //找付款方式
    if(nodeList.size() > 1) {
      Log.e("test","perform action click repeatTheSame");
      SystemClock.sleep(500);
      nodeList.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
      Toast.makeText(this, "继续付款相同的号", Toast.LENGTH_SHORT).show();
    }
    if(mRootNodeInfo != null) {
      mRootNodeInfo.recycle();
    }
  }

  private void goToNext() {
    SystemClock.sleep(200);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    List<AccessibilityNodeInfo> nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("继续付款"); //找付款方式
    if(nodeList.size() > 1) {
      Log.e("test","perform action click goNext");
      SystemClock.sleep(500);
      nodeList.get(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
      Toast.makeText(this, "继续付款下一个号", Toast.LENGTH_SHORT).show();
    }
    if(mRootNodeInfo != null) {
      mRootNodeInfo.recycle();
    }
  }

  private void chooseFukuanMode() {
    SystemClock.sleep(500);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    if(mRootNodeInfo != null) {
      PAY_MODE = PreferenceHelper.getInstance().getAutoPayMode();
      List<AccessibilityNodeInfo> nodeList;
      if(PAY_MODE == 1) {
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("快钱支付(储蓄卡)"); //找付款方式
        if(nodeList.size() > 0) {
          Log.e("test","perform action click");
          SystemClock.sleep(500);
          nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
          Toast.makeText(this, "没有快钱支付(储蓄卡)支付了", Toast.LENGTH_SHORT).show();
        }
      }else if(PAY_MODE == 2){
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("快捷支付"); //找付款方式
        if(nodeList.size() > 0) {
          Log.e("test","perform action click");
          SystemClock.sleep(500);
          nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
          Toast.makeText(this, "没有快捷付款支付了", Toast.LENGTH_SHORT).show();
        }
      }else if(PAY_MODE == 3){
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("通联付款"); //找付款方式
        if(nodeList.size() > 0) {
          Log.e("test","perform action click");
          SystemClock.sleep(500);
          nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
          Toast.makeText(this, "没有通联支付了", Toast.LENGTH_SHORT).show();
        }
      }else if(PAY_MODE == 4){
        nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("快钱支付(信用卡)"); //找付款方式
        if(nodeList.size() > 0) {
          Log.e("test","perform action click");
          SystemClock.sleep(500);
          nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
          Toast.makeText(this, "没有快钱支付(信用卡)支付了", Toast.LENGTH_SHORT).show();
        }
      }
    }
    if(mRootNodeInfo != null) {
      mRootNodeInfo.recycle();
    }
  }

//  private void chooseOrders() {
//    SystemClock.sleep(200);
//    mRootNodeInfo = null;
//    mRootNodeInfo = getRootInActiveWindow();
//    List<AccessibilityNodeInfo> nodeList;
//    nodeList = mRootNodeInfo.findAccessibilityNodeInfosByViewId("com.june.healthmail:id/cb_status");
//    Log.e("autopay","cb_status nodelist.size = " + nodeList.size());
//    if(nodeList.size() > 0) {
//
//    }
//    //getTargetNodeByClassName(mRootNodeInfo.getChild(3).getChild(0),"android.widget.EditText");
//
//  }

  private void getSmsCode() {
      try {
        int tryTimes = 0;
        while (mCurrentState == STATE_WAITING_SMS_CODE) {
          SystemClock.sleep(2000);
          tryTimes++;
          Log.e("autopay","waiting for sms code... tryTimes = " + tryTimes + "  mCurrentState = " + mCurrentState);
          if(tryTimes >= 30 || mCurrentState == STATE_NONE) {
            break;
          }
        }
        Log.e("autopay","end tryTimes = " + tryTimes);
        if(mCurrentState == STATE_RECEIVE_SMS_CODE) {
          if(code != null) {
            //pasteSmsCode(code, DeviceConfig.x2, DeviceConfig.y2);
            if(PreferenceHelper.getInstance().getAutoPayMode() == 3) {
              inputSmscodeTonglian();
            } else if(PreferenceHelper.getInstance().getAutoPayMode() == 4){
              inputSmscodeKuaiqianXinyongka();
            }else {
              inputSmscode();
            }
          }
          SystemClock.sleep(500);
          mCurrentState = STATE_AFTER_SMS_CODE;
          goBack();
        }else if(mCurrentState == STATE_WAITING_SMS_CODE){
          //没有收到短信
          SystemClock.sleep(500);
          mCurrentState = STATE_WAITING_SMS_CODE;
          goBack();
        }else {
          Log.e("autopay","用户手动退出");
        }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void inputSmscode() {
    Log.e("autopay","inputSmscode,  code = " + code);
    SystemClock.sleep(500);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    mResultInfo = null;
    getTargetNodeByClassName(mRootNodeInfo.getChild(3).getChild(0),"android.widget.EditText");
    if(mResultInfo != null) {
      Log.e("autopay","mResultInfo = " + mResultInfo);
      SystemClock.sleep(200);
      Bundle arguments = new Bundle();
      //输入验证码
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,code);
      mResultInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
    }

    SystemClock.sleep(1000);

    if(PreferenceHelper.getInstance().getAutoPayMode() == 1) {
      //快钱储蓄卡支付
      mRootNodeInfo = getRootInActiveWindow();
      mResultInfo = null;
      getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"立即支付");
      if(mResultInfo != null && mResultInfo.isClickable()) {
        mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(2000);
      }
    }else if(PreferenceHelper.getInstance().getAutoPayMode() == 2) {
      //快捷支付
      mRootNodeInfo = getRootInActiveWindow();
      mResultInfo = null;
      getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"确认付款");
      if(mResultInfo != null && mResultInfo.isClickable()) {
        mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        SystemClock.sleep(5000);
      }
    }
  }

  private void inputSmscodeTonglian() {
    Log.e("autopay","inputSmscodeTonglian,  code = " + code);
    SystemClock.sleep(500);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    mListEditText.clear();
    getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
    if(mListEditText.size() > 2) {
      Bundle arguments = new Bundle();
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, mPreferenceHelper.getPayPassword());
      mListEditText.get(0).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

      SystemClock.sleep(500);

      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, code);
      mListEditText.get(mListEditText.size()-1).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);

      SystemClock.sleep(500);
    }

    SystemClock.sleep(1000);

    mRootNodeInfo = getRootInActiveWindow();
    mResultInfo = null;
    getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"立即支付");
    if(mResultInfo != null && mResultInfo.isClickable()) {
      mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
      SystemClock.sleep(2000);
    }
  }

  private void inputSmscodeKuaiqianXinyongka() {
    Log.e("autopay","inputSmscodeTonglian,  code = " + code);
    SystemClock.sleep(500);
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();

    mListEditText.clear();
    getAllEditText(mRootNodeInfo.getChild(3).getChild(0));
    if(mListEditText.size() > 0) {
      Bundle arguments = new Bundle();
      arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, code);
      mListEditText.get(mListEditText.size()-1).performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
      SystemClock.sleep(500);
    }

    SystemClock.sleep(1000);

    mRootNodeInfo = getRootInActiveWindow();
    mResultInfo = null;
    getTargetNodeByDesc(mRootNodeInfo.getChild(3).getChild(0),"立即支付");
    if(mResultInfo != null && mResultInfo.isClickable()) {
      mResultInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
      SystemClock.sleep(3000);
    }
  }

  @Override
  public void onInterrupt() {
    Log.e("test","onInterrupt");
    Toast.makeText(this, "辅助服务被中断啦~", Toast.LENGTH_SHORT).show();
  }

  private void excCommand(String command) throws Exception {
    Runtime runtime = Runtime.getRuntime();//获取Runtime对象
    Process process = runtime.exec("su");
    if (process == null) {
      throw new NullPointerException();
    }
    DataOutputStream os;
    os = new DataOutputStream(process.getOutputStream());
    os.writeBytes(command + "\n");
    os.writeBytes("exit\n");
    os.flush();
    process.waitFor();
  }

  private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      if (intent.getAction().equals(INTENT_ACTION_STATUS_CHANGE)) {
        Log.e("test","broadcast, code = " + intent.getStringExtra("code"));
        Log.e("test","broadcast, state = " + intent.getIntExtra("state",STATE_NONE));
        code = intent.getStringExtra("code");
        mCurrentState = intent.getIntExtra("state",STATE_NONE);
        Toast.makeText(AutopayAccessibilityService.this,"收到验证码：" + code, Toast.LENGTH_SHORT).show();
      }
    }
  };

  private void clickPoint(int x, int y) {
    Instrumentation inst = new Instrumentation();
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, x, y, 0));
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, x, y, 0));
  }

  private void pasteSmsCode(String code, int x, int y) {
    Instrumentation inst = new Instrumentation();
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, x, y, 0));
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, x, y, 0));

    Log.e("hujunjie","吊起输入法");
    mClipboardManager.setText(code);
    SystemClock.sleep(1000);

    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, x, y, 0));
    SystemClock.sleep(2000);
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, x, y, 0));

    Log.e("hujunjie","长按");

    SystemClock.sleep(1000);


    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, DeviceConfig.x3, DeviceConfig.y3, 0));
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, DeviceConfig.x3, DeviceConfig.y3, 0));

    SystemClock.sleep(1000);
    Log.e("hujunjie","粘贴");
  }

  public static Handler getServiceHandler() {
    return serviceHandler;
  }

  public static void setServiceHandler(Handler serviceHandler) {
    AutopayAccessibilityService.serviceHandler = serviceHandler;
  }

  public static boolean isFirstTime() {
    return isFirstTime;
  }

  public static void setIsFirstTime(boolean isFirstTime) {
    AutopayAccessibilityService.isFirstTime = isFirstTime;
  }
}
