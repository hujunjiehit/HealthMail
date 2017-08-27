package com.june.healthmail.improve.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.activity.BaseActivity;
import com.june.healthmail.broadcast.MyReceiver;
import com.june.healthmail.improve.service.BaseService;
import com.june.healthmail.improve.service.PingjiaService;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;

/**
 * Created by june on 2017/8/17.
 */

public class NewPingjiaActivity extends BaseActivity implements View.OnClickListener{

  private Button btn_edit_words;
  private TextView tv_show_words;

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvRemainTimes;
  private Boolean isRunning = false;
  private int offset;

  private CheckBox cbPingjiaAlarm;
  private TextView tvShowTime;

  private PingjiaService.PingjiaBinder mBinder;

  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      mBinder = (PingjiaService.PingjiaBinder) service;
      mBinder.setHandler(mHandler);
      mBinder.setPingjiaWord(tv_show_words.getText().toString().trim());
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
        case BaseService.UPDATE_TIMES:
          tvRemainTimes.setText(msg.arg1 + "");
          break;
        case BaseService.FINISH_PINGJIA:
          btn_start.setText("评价完成");
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
    setContentView(R.layout.activity_pingjia);
    if(getIntent() != null){
      if(getIntent().getBooleanExtra("exception",false)){
        PreferenceHelper.getInstance().setRemainPingjiaTimes(3000);
      }
    }
    initView();
    setListener();
    initData();

    //bindService
    Intent bindIntent = new Intent(this,PingjiaService.class);
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
    Log.e("test","onResume + " + getIntent().getStringExtra("fromAlarm"));
  }

  private void initView() {
    btn_edit_words = (Button) findViewById(R.id.btn_edit_words);
    tv_show_words = (TextView) findViewById(R.id.tv_show_words);
    btn_start = (Button) findViewById(R.id.btn_start);
    cbPingjiaAlarm = (CheckBox) findViewById(R.id.cb_pingjia_alarm);
    tvShowTime = (TextView) findViewById(R.id.tv_show_time);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvRemainTimes = (TextView) findViewById(R.id.tv_remmain_times);
  }

  private void setListener() {
    btn_edit_words.setOnClickListener(this);
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    cbPingjiaAlarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
          PreferenceHelper.getInstance().setPingjiaAlarm(true);
          PreferenceHelper.getInstance().setPingjiaAlarmTime(1000);
          setAlarm();
        } else {
          PreferenceHelper.getInstance().setPingjiaAlarm(false);
          PreferenceHelper.getInstance().setPingjiaAlarmTime(0);
          cancelAlarm();
        }
      }
    });
  }

  private void initData() {
    String pingjiaWord = PreferenceHelper.getInstance().getPingjiaWord();
    if(pingjiaWord != null){
      tv_show_words.setText(pingjiaWord);
    }
    tvRemainTimes.setText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "");
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.btn_edit_words:
        showEditWordsDialog();
        break;
      case R.id.btn_start:
        startToPingjia();
        break;
      case R.id.img_back:	//返回
        finish();
        break;
      default:
        break;
    }
  }

  private void startToPingjia() {
    if("评价完成".equals(btn_start.getText().toString().trim())){
      Toast.makeText(this,"评价已完成，如需继续评价请重新进入本页面",Toast.LENGTH_LONG).show();
    }else {
      if(PreferenceHelper.getInstance().getRemainPingjiaTimes() <= 0) {
        toast("今日评价次数已用完，请充值");
        return;
      }
      if (isRunning == false) {
        isRunning = true;
        if(mBinder != null) {
          btn_start.setText("停止评价");
          mBinder.startPingjia();
        }
      } else {
        isRunning = false;
        if(mBinder != null) {
          btn_start.setText("开始评价");
          mBinder.stopPingjia();
        }
      }
    }
  }

  private void setAlarm() {
    AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
    Intent intent=new Intent();
    intent.setClass(this, NewPingjiaActivity.class);
    intent.putExtra("fromAlarm","yes");
    PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
    );
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      aManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, pi);
    }else {
      aManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+10000, pi);
    }
    Log.e("test","定时已设置");
  }

  private void cancelAlarm() {
    AlarmManager aManager=(AlarmManager)getSystemService(Service.ALARM_SERVICE);
    Intent intent=new Intent();
    intent.setClass(this, NewPingjiaActivity.class);
    intent.putExtra("fromAlarm","yes");
    PendingIntent sender = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_NO_CREATE);
    if(sender != null) {
      aManager.cancel(sender);
      Log.e("test","定时已取消");
    } else {
      Log.e("test","sender == null");
    }
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }

  private void showEditWordsDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改评价语");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String new_pingjia_word = edit_text.getText().toString().trim();
        Log.d("test","new_pingjia_word = " + new_pingjia_word);
        if(new_pingjia_word.length() >= 5){
          PreferenceHelper.getInstance().savePingjiaWord(new_pingjia_word);
          tv_show_words.setText(new_pingjia_word);
          if(mBinder != null) {
            mBinder.setPingjiaWord(tv_show_words.getText().toString().trim());
          }
          dialog.dismiss();
        }else {
          Toast.makeText(NewPingjiaActivity.this,"评价语需要大于五个字，请重新输入",Toast.LENGTH_LONG).show();
        }
      }
    });
    builder.create().show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    Log.e("test","onDestroy");
    unbindService(connection);
    mHandler.removeCallbacksAndMessages(null);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("test","requstCode = " + requestCode);
  }
}
