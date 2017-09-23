package com.june.healthmail.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.june.healthmail.Config.DeviceConfig;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.PreferenceHelper;

import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.bmob.v3.BmobUser;

/**
 * Created by june on 2017/9/11.
 */

public class MyAccessibilityService extends AccessibilityService {

  public static final String INTENT_ACTION_STATUS_CHANGE ="com.june.healthmail.status.change";

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

  //付款模式  1--快捷支付(储蓄卡)  2--快捷支付
  private static int PAY_MODE;

  /**
   * 循环点击获取验证码
   */
  private Runnable mRunnable = new Runnable() {
    @Override
    public void run() {
//      SystemClock.sleep(2000);
//      try {
//        Log.e("hujunjie","click send sms code");
//        int tryTimes = 0;
//        while (mCurrentState == STATE_WAITING_SMS_CODE) {
//          //excCommand("input tap 589 644");
//          clickPoint(DeviceConfig.x1,DeviceConfig.y1);
//          SystemClock.sleep(2000);
//          //excCommand("input tap 589 644");
//          clickPoint(DeviceConfig.x1,DeviceConfig.y1);
//          tryTimes++;
//          Log.e("hujunjie","waiting... tryTimes = " + tryTimes + "  mCurrentState = " + mCurrentState);
//          if(tryTimes >= 50 || mCurrentState == STATE_NONE) {
//            break;
//          }
//        }
//        Log.e("hujunjie","end tryTimes = " + tryTimes);
//        if(mCurrentState == STATE_RECEIVE_SMS_CODE) {
//          if(code != null) {
//            pasteSmsCode(code, DeviceConfig.x2, DeviceConfig.y2);
//          }
//          SystemClock.sleep(500);
//          mCurrentState = STATE_AFTER_SMS_CODE;
//          goBack();
//        }else if(mCurrentState == STATE_WAITING_SMS_CODE){
//          //没有收到短信
//          SystemClock.sleep(500);
//          mCurrentState = STATE_BEFORE_SMS_CODE;
//          goBack();
//        }else {
//          Log.e("hujunjie","用户手动退出");
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }
    }
  };

  private void goBack() {
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();
    List<AccessibilityNodeInfo> nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("关闭"); //找付款方式
    if(nodeList.size() > 0) {
      Log.e("test","perform action click close");
      SystemClock.sleep(500);
      nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }
  }

  @Override
  protected void onServiceConnected() {
    super.onServiceConnected();
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    if(userInfo.getAutoPay() != null && userInfo.getAutoPay() == 1) {
      Log.e("test", "开启");
      Toast.makeText(this, "_已开启辅助服务_", Toast.LENGTH_SHORT).show();
      mClipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
      mPreferenceHelper = PreferenceHelper.getInstance();
      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(INTENT_ACTION_STATUS_CHANGE);
      LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mBroadcastReceiver, intentFilter);
    }else {
      Log.e("test", "关闭");
      Toast.makeText(this, "_当前用户无权限开启辅助服务_", Toast.LENGTH_SHORT).show();
      stopSelf();
    }
  }


  @Override
  public void onAccessibilityEvent(AccessibilityEvent event) {
    if(userInfo.getAutoPay() == null || userInfo.getAutoPay() != 1){
      return;
    }
    Log.e("test","onAccessibilityEvent:" + event);
    int eventType = event.getEventType();
    switch (eventType) {
      case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED: //窗口状态变化的时候处理
        Log.e("autopay","TYPE_WINDOW_STATE_CHANGED");
        Log.e("autopay","event.getText() = " + event.getText());
        if(event.getText().contains("请选择支付渠道")){
          mCurrentState = STATE_WAITING_FOR_LOADING;
          chooseFukuanMode();
        }

        if(event.getClassName().equals("com.june.healthmail.activity.PayWebviewActivity")) {
          if(mCurrentState == STATE_WAITING_FOR_LOADING) {
            if(PreferenceHelper.getInstance().getAutoPayMode() == 1) {
              //快钱支付储蓄卡自动付款流程
              autoPayKuaiqian();
            } else if(PreferenceHelper.getInstance().getAutoPayMode() == 2){
              //快捷支付自动付款流程
              stepOne();
            }
          }
        }

        if(event.getText().contains("下一步")){
          if(mCurrentState == STATE_AFTER_SMS_CODE) {
            mCurrentState = STATE_NONE;
            Log.e("autopay","goToNext");
            goToNext();
          }else if(mCurrentState == STATE_WAITING_FOR_LOADING || mCurrentState == STATE_WAITING_SMS_CODE){
            //没有收到短信，或者没有加载出来付款界面，继续支付相同的号
            mCurrentState = STATE_NONE;
            Log.e("autopay","repeatTheSame");
            repeatTheSame();
          }else {
            mCurrentState = STATE_NONE;
          }
        }

        break;
      case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: //窗口内容变化的时候处理
        //Log.e("autopay","TYPE_WINDOW_CONTENT_CHANGED");
        /**
         *EventType: TYPE_WINDOW_STATE_CHANGED; EventTime: 46195351; PackageName: com.june.healthmail;
         * MovementGranularity: 0; Action: 0 [ ClassName: android.widget.FrameLayout;
         * Text: [请选择支付渠道, 快捷支付, 快钱支付(储蓄卡), 通联付款, 易联支付, 汇付支付, 快钱支付(信用卡)];
         * ContentDescription: null; ItemCount: -1;
         * CurrentItemIndex: -1; IsEnabled: true;
         * IsPassword: false; IsChecked: false;
         * IsFullScreen: false; Scrollable: f
         */
        break;
      case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
        Log.e("autopay","TYPE_VIEW_TEXT_SELECTION_CHANGED,  text = " + event.getText());
        if(event.getText().contains("盛付通移动收银台")){
          //test();
        }
        break;
      default:
        break;
    }


//    // get the source node of the event
//    AccessibilityNodeInfo nodeInfo = event.getSource();
//    // take action on behalf of the user
//    nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
//    // recycle the nodeInfo object
//    nodeInfo.recycle();

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

        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {
          mRootNodeInfo = getRootInActiveWindow();
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

  private void getTargetNodeByDesc(AccessibilityNodeInfo info, String desc) {
    //Log.e("autopay","getTargetNodeByDesc  childCount = " + info.getChildCount());
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

  private void getAllEditText(AccessibilityNodeInfo info) {
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

        int tryTimes = 0;
        while (targetInfo == null && mCurrentState == STATE_WAITING_FOR_LOADING) {
          mRootNodeInfo = getRootInActiveWindow();
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
    mRootNodeInfo.recycle();
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
    mRootNodeInfo.recycle();
  }

  private void chooseFukuanMode() {
    mRootNodeInfo = null;
    mRootNodeInfo = getRootInActiveWindow();
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
    }
    mRootNodeInfo.recycle();
  }

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
            inputSmscode();
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

//
//    targetInfo = null;
//    try{
//      SystemClock.sleep(200);
//      int childCount = mRootNodeInfo.getChild(3).getChild(0).getChildCount();
//      targetInfo =  mRootNodeInfo.getChild(3).getChild(0).getChild(childCount-1);
//      targetInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//    }catch (Exception e) {
//      Log.e("autopay","exception targetInfo = null inputSmscode");
//      targetInfo = null;
//    }
//    targetInfo.recycle();
//
//    SystemClock.sleep(5000);
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
        Toast.makeText(MyAccessibilityService.this,"收到验证码：" + code, Toast.LENGTH_SHORT).show();
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


    //点击取消输入法键盘
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, DeviceConfig.x4, DeviceConfig.y4, 0));
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, DeviceConfig.x4, DeviceConfig.y4, 0));

    SystemClock.sleep(1000);

    //点击确认付款按钮
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_DOWN, DeviceConfig.x5, DeviceConfig.y5, 0));
    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(),
        MotionEvent.ACTION_UP, DeviceConfig.x5, DeviceConfig.y5, 0));

    SystemClock.sleep(5000);
  }
}
