package com.june.healthmail.improve.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
import com.june.healthmail.activity.BaseActivity;
import com.june.healthmail.activity.PingjiaActivity;
import com.june.healthmail.improve.service.BaseService;
import com.june.healthmail.improve.service.PingjiaService;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.Order;
import com.june.healthmail.model.OrdersModel;
import com.june.healthmail.model.PingjiaModel;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.TimeUntils;

import java.io.IOException;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/8/17.
 */

public class NewPingjiaActivity extends BaseActivity implements View.OnClickListener{

  private Button btn_edit_words;
  private TextView tv_show_words;

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvRemainTimes;
  private UserInfo userInfo;
  private ArrayList<AccountInfo> accountList = new ArrayList<>();

  private Boolean isRunning = false;
  private int offset;

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
  protected void onResume() {
    super.onResume();
    //setupSpotAd();
  }

  private void initView() {
    btn_edit_words = (Button) findViewById(R.id.btn_edit_words);
    tv_show_words = (TextView) findViewById(R.id.tv_show_words);
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvRemainTimes = (TextView) findViewById(R.id.tv_remmain_times);
  }

  private void setListener() {
    btn_edit_words.setOnClickListener(this);
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
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
    unbindService(connection);
    mHandler.removeCallbacksAndMessages(null);
  }
}
