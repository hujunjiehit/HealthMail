package com.june.healthmail.service;

import android.accessibilityservice.AccessibilityService;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.june.healthmail.Config.DeviceConfig;
import com.june.healthmail.model.UserInfo;

import java.io.DataOutputStream;
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
  public static final int STATE_BEFORE_SMS_CODE = 1;
  public static final int STATE_WAITING_SMS_CODE = 2;
  public static final int STATE_RECEIVE_SMS_CODE = 3;
  public static final int STATE_AFTER_SMS_CODE = 4;
  public static final int STATE_OTHER = -1;

  //当前窗口状态
  private int mCurrentState = STATE_NONE;
  private UserInfo userInfo;

  private ClipboardManager mClipboardManager;
  private AccessibilityNodeInfo mRootNodeInfo;
  private ExecutorService mExecutorService = Executors.newSingleThreadExecutor(); //建一个单线程的线程池

  private String code; //收到的验证码

  /**
   * 循环点击获取验证码
   */
  private Runnable mRunnable = new Runnable() {
    @Override
    public void run() {
      SystemClock.sleep(2000);
      try {
        Log.e("hujunjie","click send sms code");
        int tryTimes = 0;
        while (mCurrentState == STATE_WAITING_SMS_CODE) {
          //excCommand("input tap 589 644");
          clickPoint(DeviceConfig.x1,DeviceConfig.y1);
          SystemClock.sleep(2000);
          //excCommand("input tap 589 644");
          clickPoint(DeviceConfig.x1,DeviceConfig.y1);
          tryTimes++;
          Log.e("hujunjie","waiting... tryTimes = " + tryTimes + "  mCurrentState = " + mCurrentState);
          if(tryTimes >= 40 || mCurrentState == STATE_NONE) {
            break;
          }
        }
        Log.e("hujunjie","end tryTimes = " + tryTimes);
        if(mCurrentState == STATE_RECEIVE_SMS_CODE) {
          if(code != null) {
            int x1 = 50;
            int y1 = 945;
            pasteSmsCode(code, DeviceConfig.x2, DeviceConfig.y2);
          }
          SystemClock.sleep(500);
          mCurrentState = STATE_AFTER_SMS_CODE;
          goBack();
        }else if(mCurrentState == STATE_WAITING_SMS_CODE){
          //没有收到短信
          SystemClock.sleep(500);
          mCurrentState = STATE_BEFORE_SMS_CODE;
          goBack();
        }else {
          Log.e("hujunjie","用户手动退出");
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
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
        Log.e("test","TYPE_WINDOW_STATE_CHANGED");
        Log.e("test","event.getText() = " + event.getText());
        if(event.getText().contains("请选择支付渠道")){
          mCurrentState = STATE_BEFORE_SMS_CODE;
          chooseFukuanMode();
        }

        if(event.getClassName().equals("com.june.healthmail.activity.PayWebviewActivity")) {
            if(mCurrentState == STATE_BEFORE_SMS_CODE) {
              mCurrentState = STATE_WAITING_SMS_CODE;
              getSmsCode();
            }
        }

        if(event.getText().contains("下一步")){
          if(mCurrentState == STATE_AFTER_SMS_CODE) {
            mCurrentState = STATE_NONE;
            Log.e("test","goToNext");
            goToNext();
          }else if(mCurrentState == STATE_BEFORE_SMS_CODE){
            //没有收到短信，继续支付相同的号
            mCurrentState = STATE_NONE;
            Log.e("test","repeatTheSame");
            repeatTheSame();
          }else {
            mCurrentState = STATE_NONE;
          }
        }

        break;
      case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: //窗口内容变化的时候处理
        Log.e("test","TYPE_WINDOW_CONTENT_CHANGED");
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
    List<AccessibilityNodeInfo> nodeList = mRootNodeInfo.findAccessibilityNodeInfosByText("快捷支付"); //找付款方式
    if(nodeList.size() > 0) {
      Log.e("test","perform action click");
      SystemClock.sleep(500);
      nodeList.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
    }else {
      Toast.makeText(this, "没有快捷付款支付了", Toast.LENGTH_SHORT).show();
    }
    mRootNodeInfo.recycle();
  }

  private void getSmsCode() {
    mExecutorService.execute(mRunnable);
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
