package com.june.healthmail.improve.activity;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.BaseActivity;
import com.june.healthmail.improve.service.BaseService;
import com.june.healthmail.improve.service.FukuanService;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.untils.Tools;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/9/26.
 */

public class NewFukuanActivity extends BaseActivity implements View.OnClickListener {

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvCoinsNumber;
  private TextView tvCoinsDesc;
  private TextView tvLeftTime;
  private CheckBox cbPayAllOrders;
  private CheckBox cbOpenAccess;
  private ShowProgress showProgress;
  private UserInfo userInfo;
  private int coinsCost;
  private int offset;

  private FukuanService.FukuanBinder mBinder;
  private AccessibilityManager mAccessibilityManager;
  private Boolean isRunning = false;

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mBinder = (FukuanService.FukuanBinder) service;
      mBinder.setHandler(mHandler);
      Log.e("test","FukuanService onServiceConnected");
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
  };

  private Handler mHandler = new Handler(){

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case BaseService.SHOW_RESULT:
          showTheResult((String) msg.obj);
          break;
        case BaseService.FINISH_FUKUAN:
          btn_start.setText("付款完成");
          break;
        default:
          Log.e("test","undefined message");
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.e("test","onCreate");
    if(!CommonUntils.hasPermission()){
      Toast.makeText(this,"当前用户无授权，无法进入本页面",Toast.LENGTH_SHORT).show();
      finish();
    }
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    setContentView(R.layout.activity_fukuan);
    if(getIntent() != null){
      if(getIntent().getBooleanExtra("exception",false)){
        PreferenceHelper.getInstance().setRemainPingjiaTimes(3000);
      }
    }
    initView();
    setListener();
    initData();

    mAccessibilityManager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);

    //bindService
    Intent bindIntent = new Intent(this,FukuanService.class);
    bindService(bindIntent,connection, BIND_AUTO_CREATE);
  }

  @Override
  protected void onStart() {
    super.onStart();
    Log.e("test","onStart");
  }

  @Override
  protected void onResume() {
    super.onResume();
    boolean isServiceEnabled = isServiceEnabled();
    boolean toolsIsServiceEnabled = Tools.isServiceOpenedByReadSettings(this,"com.june.healthmail.pay/com.june.healthmail.service.AutopayAccessibilityService");
    Log.e("test", isServiceEnabled + "---" + toolsIsServiceEnabled);
    if(isServiceEnabled || toolsIsServiceEnabled) {
      cbOpenAccess.setChecked(true);
    }else {
      cbOpenAccess.setChecked(false);
    }
  }

  private void initView() {
    showProgress = new ShowProgress(this);
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvCoinsNumber = (TextView) findViewById(R.id.tv_coins_number);
    tvCoinsDesc =  (TextView) findViewById(R.id.tv_coins_desc);
    cbPayAllOrders = (CheckBox) findViewById(R.id.cb_pay_all_orders);
    cbOpenAccess = (CheckBox) findViewById(R.id.cb_open_access);
    tvLeftTime = (TextView) findViewById(R.id.tv_left_time);
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    findViewById(R.id.img_setup).setOnClickListener(this);
    cbPayAllOrders.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          PreferenceHelper.getInstance().setPayAllOrders(true);
        } else {
          PreferenceHelper.getInstance().setPayAllOrders(false);
        }
      }
    });

    cbOpenAccess.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        cbOpenAccess.setChecked(!cbOpenAccess.isChecked());
        Intent accessibleIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        accessibleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        toast("请选择付款助手辅助功能，并开启或者关闭服务");
        startActivity(accessibleIntent);
      }
    });
  }

  private void initData() {
    coinsCost = PreferenceHelper.getInstance().getPayCost();
    if(coinsCost == 0) {
      tvCoinsNumber.setVisibility(View.GONE);
      tvCoinsDesc.setText("今日活动，付款免费");
    }
    tvCoinsNumber.setText(userInfo.getCoinsNumber() + "");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_start:
        if(!cbOpenAccess.isChecked()){
          //辅助功能未打开
          showNoticeDialog("辅助功能未打开，请先开启辅助功能之后再开始付款");
          return;
        }
        if(userInfo.getCoinsNumber() <= 0) {
          //金币余额不足
          showNoticeDialog("金币余额不足，请先充值金币(温馨提醒：邀请好友开通永久，可以获得1888金币，记得让好友注册邀请人写你的猫友圈账号)");
          return;
        }

        if(!CommonUntils.checkPackage(this,"com.zhanyun.ihealth")) {
          //未安装健康猫app
          showNoticeDialog("检测到手机未安装健康猫app，请先安装健康猫app");
          return;
        }
        if (isRunning == false) {
          isRunning = true;
          if(mBinder != null) {
            btn_start.setText("停止付款");
            mBinder.startFukuan();
            showNoticeDialog("点击确定之后，请将软件切换到后台，然后前往健康猫登录界面(如果健康猫处于登录状态需要退出登录，然后回到登录界面)，软件会自动帮您登录到健康猫付款界面");
          }
        } else {
          isRunning = false;
          if(mBinder != null) {
            btn_start.setText("开始付款");
            mBinder.stopFukuan();
          }
        }
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      default:
        break;
    }
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.e("test","onDestroy");
    if(mBinder != null) {
      mBinder.stopFukuan();
    }
    unbindService(connection);
    mHandler.removeCallbacksAndMessages(null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("test","requstCode = " + requestCode);
  }

  /**
   * 获取 RobotService 是否启用状态
   * @return true or false
   */
  private boolean isServiceEnabled() {
    List<AccessibilityServiceInfo> accessibilityServices =
        mAccessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
    for (AccessibilityServiceInfo info : accessibilityServices) {
      if (info.getId().equals("com.june.healthmail.pay/com.june.healthmail.service.AutopayAccessibilityService")) {
        return true;
      }
    }
    return false;
  }

  private void showNoticeDialog(String msg) {
    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("提醒")
        .setMessage(msg)
        .setCancelable(false)
        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        }).create();
    dialog.show();
  }

}
