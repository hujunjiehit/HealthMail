package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.june.healthmail.R;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.GetAllPaymentModel;
import com.june.healthmail.model.GetOrderListModel;
import com.june.healthmail.model.GetPayInfoJingdongModel;
import com.june.healthmail.model.GetPayInfoKuaijieModel;
import com.june.healthmail.model.GetPayInfoModel;
import com.june.healthmail.model.GetPayInfoTonglianModel;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.HmOrder;
import com.june.healthmail.model.PayinfoDetail;
import com.june.healthmail.model.PayinfoJingdongDetail;
import com.june.healthmail.model.PayinfoKuaijieDetail;
import com.june.healthmail.model.PayinfoTonglianDetail;
import com.june.healthmail.model.Payment;
import com.june.healthmail.model.TokenModel;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.DBManager;
import com.june.healthmail.untils.HttpUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.view.ChoosePayOptionsPopwindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class FukuanActivity extends BaseActivity implements View.OnClickListener{

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvCoinsNumber;
  private TextView tvCoinsDesc;

  private ArrayList<AccountInfo> accountList = new ArrayList<>();

  private Boolean isRunning = false;
  private int offset;

  private static final int START_TO_FU_KUAN = 1;
  private static final int GET_TOKEN_SUCCESS = 2;
  private static final int GET_TOKEN_FAILED = 3;
  private static final int USER_PWD_WRONG = 4;
  private static final int START_TO_GET_USERINFO = 5;
  private static final int GET_USERINFO_SUCCESS = 6;
  private static final int GET_USERINFO_FAILED = 7;
  private static final int START_TO_GET_ORDER_LIST = 8;
  private static final int GET_ORDER_LIST_SUCCESS = 9;
  private static final int START_TO_GET_ALL_PAYMENT = 10;
  private static final int GET_ALL_PAYMENT_SUCCESS = 11;
  private static final int START_TO_GET_PAYINFO = 12;
  private static final int GET_PAYINFO_SUCCESS = 13;

  private static final int GET_ORDER_LIST_FAILED = 14;
  private static final int GET_ALL_PAYMENT_FAILED = 15;
  private static final int GET_PAYINFO_FAILED = 16;

  private static final int PAY_TYPE_KUAIQIAN_ZHIFU = 20;
  private static final int PAY_TYPE_TONGLIAN_ZHIFU = 21;
  private static final int PAY_TYPE_KUAIJIE_ZHIFU = 22;
  private static final int PAY_TYPE_JINGDONG_ZHIFU = 23;
  private static final int PAY_TYPE_YILIAN_ZHIFU = 24;


  private int accountIndex = 0;

  private Message message;
  private String accessToken;
  private String user_id;
  private int min_time;
  private int max_time;
  private String errmsg;

  private ArrayList<HmOrder> hmOrders = new ArrayList<>();
  private UserInfo userInfo;

  private int[] fukuanChoice = {0,0,0,0,0};
  private ChoosePayOptionsPopwindow popwindow;
  private int payTypeFlag;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_FU_KUAN:
          if (isRunning) {
            if(userInfo.getCoinsNumber() > 0) {
              if (accountIndex < accountList.size()) {
                showTheResult("开始付款第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
                if (accountList.get(accountIndex).getStatus() == 1) {
                  getAccountToken();
                } else {
                  showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                  accountIndex++;
                  message = this.obtainMessage(START_TO_FU_KUAN);
                  message.sendToTarget();
                }
              } else {
                showTheResult("******所有账号付款结束**********\n");
                isRunning = false;
                btn_start.setText("付款完成");
              }
            }else {
              showTheResult("******金币余额不足，付款结束**********\n");
              isRunning = false;
              btn_start.setText("开始付款");
            }
          } else {
            showTheResult("**用户自己终止付款**当前已经执行完成" + accountIndex + "个小号\n");
          }
          break;
        case GET_TOKEN_SUCCESS:
          showTheResult("--获取token成功\n");
          message = this.obtainMessage(START_TO_GET_USERINFO);
          message.sendToTarget();
          break;
        case START_TO_GET_USERINFO:
          showTheResult("----获取个人信息：");
          getUserInfo();
          break;
        case GET_USERINFO_SUCCESS:
          showTheResult("----成功\n");
          GetUserModel getUserModel = (GetUserModel)msg.obj;
          user_id = getUserModel.getValuse().getUserModel().getUser_Id();
          this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
          break;
        case START_TO_GET_ORDER_LIST:
          showTheResult("------开始获取订单列表\n");
          getOrderList(user_id);
          break;
        case GET_ORDER_LIST_SUCCESS:
          showTheResult("---------订单列表获取成功\n");
          hmOrders.clear();
          GetOrderListModel getOrderListModel = (GetOrderListModel)msg.obj;
            if(getOrderListModel.getValuse() != null){
                for(int i = 0; i < getOrderListModel.getValuse().size(); i++){
                    hmOrders.add(getOrderListModel.getValuse().get(i));
                }
                if(hmOrders.size() > 0){
                  showTheResult("------------共有" + hmOrders.size() + "个订单\n");
                  if(userInfo.getPayStatus() == null) {
                    updateTheCoinsNumber();
                    tvCoinsNumber.setText(userInfo.getCoinsNumber()+"");
                    showTheResult("--------------金币余额-1\n");
                  } else {
                    if(userInfo.getPayStatus() != 1) {
                      updateTheCoinsNumber();
                      tvCoinsNumber.setText(userInfo.getCoinsNumber()+"");
                      showTheResult("--------------金币余额-1\n");
                    }
                  }
                  this.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
                }else {
                  showTheResult("-------------当前无可支付订单，继续下一个小号\n\n\n");
                  accountIndex++;
                  this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
                }
            }
          break;

          case START_TO_GET_ALL_PAYMENT:
              showTheResult("-------------开始获取支付方式\n");
              getAllPayment();
              break;

          case GET_ALL_PAYMENT_SUCCESS:
            GetAllPaymentModel getAllPaymentModel = (GetAllPaymentModel) msg.obj;
            for(int i = 0; i < getAllPaymentModel.getValuse().size(); i++){
              //testcode
              if(userInfo != null &&
                      (userInfo.getUsername().equals("13027909110") ||
                              userInfo.getUsername().equals("18002570032") ||
                              userInfo.getUsername().equals("18671400766"))){
                showTheResult("---------------支付方式" + (i + 1) + "剩余金额：" + getAllPaymentModel.getValuse().get(i).getChannelamount() + "\n");
              }
            }
            initFukuanChoice(getAllPaymentModel.getValuse());
            showChooseFukuanMode();
            break;

        case START_TO_GET_PAYINFO:
          if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU){
            showTheResult("---------------用户选择快钱支付\n");
            //payType = 3 表示快钱支付
            getPayinfo(3);
          }else if(payTypeFlag == PAY_TYPE_TONGLIAN_ZHIFU){
            showTheResult("---------------用户选择通联支付\n");
            //payType = 7 表示通联支付
            getPayinfo(7);
          }else if(payTypeFlag == PAY_TYPE_KUAIJIE_ZHIFU){
            //payType = 6 表示快捷支付
            getPayinfo(6);
          }else if(payTypeFlag == PAY_TYPE_JINGDONG_ZHIFU){
            //payType = 5 表示京东支付
            getPayinfo(5);
          }
          break;

        case GET_PAYINFO_SUCCESS:
          showTheResult("------------------获取支付详情成功，前往支付页面\n");
          if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU){
            GetPayInfoModel payInfoModel = (GetPayInfoModel) msg.obj;
            getKuaiqianPageInfo(payInfoModel);
          }else if(payTypeFlag == PAY_TYPE_TONGLIAN_ZHIFU){
            GetPayInfoTonglianModel payInfoTonglianModel = (GetPayInfoTonglianModel) msg.obj;
            getTonglianPageInfo(payInfoTonglianModel);
          } else if(payTypeFlag == PAY_TYPE_KUAIJIE_ZHIFU){
            GetPayInfoKuaijieModel payInfoKuaijieModel = (GetPayInfoKuaijieModel) msg.obj;
            getKuaijiePageInfo(payInfoKuaijieModel);
          } else if(payTypeFlag == PAY_TYPE_JINGDONG_ZHIFU){
            GetPayInfoJingdongModel getPayInfoJingdongModel = (GetPayInfoJingdongModel) msg.obj;
            getJingdongPageInfo(getPayInfoJingdongModel);
          }

          break;

          case GET_TOKEN_FAILED:
            showTheResult("---获取token失败，重新开始该小号\n");
            this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
            break;

        case GET_USERINFO_FAILED:
          showTheResult("----失败，继续获取\n");
          this.sendEmptyMessageDelayed(START_TO_GET_USERINFO,getDelayTime());
          break;

        case GET_ORDER_LIST_FAILED:
          showTheResult("---------订单列表获取失败\n");
          this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
          break;

        case GET_ALL_PAYMENT_FAILED:
          showTheResult("---------支付方式获取失败，继续尝试\n");
          this.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
          break;

        case GET_PAYINFO_FAILED:
          showTheResult("------------------获取支付详情失败，重新获取支付方式\n");
          this.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
          break;

        case USER_PWD_WRONG:
            showTheResult("***错误信息："+ errmsg + "\n");
            showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
            accountIndex++;
            this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
            break;
        default:
          break;
      }
    }
  };

  private void initFukuanChoice(List<Payment> allpayments) {
      int index = 0;
      fukuanChoice[index] = 0;
      for(Payment pyment:allpayments){
        if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("快捷支付")){
          fukuanChoice[index] = 1;
        }
      }

    index = 1;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("快钱支付")){
        fukuanChoice[index] = 1;
      }
    }

    index = 2;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("通联支付")){
        fukuanChoice[index] = 1;
      }
    }

    index = 3;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("京东支付")){
        fukuanChoice[index] = 1;
      }
    }

    index = 4;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("易联支付")){
        fukuanChoice[index] = 1;
      }
    }
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fukuan);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    initView();
    setListener();
    initData();
    //setupSpotAd();
  }

  @Override
  protected void onResume() {
    super.onResume();
  }

  private void initView() {
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvCoinsNumber = (TextView) findViewById(R.id.tv_coins_number);
    tvCoinsDesc =  (TextView) findViewById(R.id.tv_coins_desc);
    if(userInfo.getPayStatus() != null && userInfo.getPayStatus() == 1) {
      tvCoinsNumber.setVisibility(View.GONE);
      tvCoinsDesc.setText("付款永久用户(付款时不消耗金币)");
    }
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
  }

  private void initData() {
    accountList.clear();
   tvCoinsNumber.setText(userInfo.getCoinsNumber() + "");
    SQLiteDatabase db = DBManager.getInstance(this).getDb();
    Cursor cursor = db.rawQuery("select * from account",null);
    if(cursor.moveToFirst()){
      do {
        AccountInfo info = new AccountInfo();
        info.setPassWord(cursor.getString(cursor.getColumnIndex("passWord")));
        info.setPhoneNumber(cursor.getString(cursor.getColumnIndex("phoneNumber")));
        info.setNickName(cursor.getString(cursor.getColumnIndex("nickName")));
        info.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
        info.setId(cursor.getInt(cursor.getColumnIndex("id")));
        accountList.add(info);
      }while(cursor.moveToNext());
    }
    cursor.close();
    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()){
      case R.id.img_back:	//返回
        finish();
        break;
      case R.id.btn_start:
        if(userInfo != null && userInfo.getCoinsNumber() <= 0){
          Toast.makeText(this,"金币余额不足，无法使用付款功能",Toast.LENGTH_LONG).show();
          return;
        }
        if("付款完成".equals(btn_start.getText().toString().trim())){
          Toast.makeText(this,"付款已完成，如需继续付款请重新进入本页面",Toast.LENGTH_LONG).show();
        }else {
          if (isRunning == false) {
            isRunning = true;
            btn_start.setText("停止付款");
            startToFuKuan();
          } else {
            isRunning = false;
            mHandler.removeCallbacksAndMessages(null);
            btn_start.setText("开始付款");
          }
        }
        break;
      case R.id.btn_fukuan_kuaijie://快捷支付
        Log.e("test","click btn_fukuan_kuaijie");
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        payTypeFlag = PAY_TYPE_KUAIJIE_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_kuaiqian://快钱支付
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        Log.e("test","click btn_fukuan_kuaiqian");
        payTypeFlag = PAY_TYPE_KUAIQIAN_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_jingdong://京东支付
        Log.e("test","click btn_fukuan_jingdong");
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        payTypeFlag = PAY_TYPE_JINGDONG_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_tonglian://通联支付
        Log.e("test","click btn_fukuan_tonglian");
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        payTypeFlag = PAY_TYPE_TONGLIAN_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_yilian://易联支付
        Log.e("test","click btn_fukuan_yilian");
        toast("暂不支持易联支付，请耐心等待下次更新");
        break;
      default:
        break;
    }
  }

  private void getAccountToken() {
    String url = "http://ssl.healthmall.cn/data/app/token/accessToken.do";
    JsonObject job = new JsonObject();
    job.addProperty("userPassword", CommonUntils.md5(accountList.get(accountIndex).getPassWord()));
    job.addProperty("grantType","app_credential");
    job.addProperty("userName",accountList.get(accountIndex).getPhoneNumber());
    job.addProperty("thirdLoginType","0");

    FormBody body = new FormBody.Builder()
        .add("data",job.toString())
        .build();

    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        //获取token成功之后Log.e("test","response = " + response.body().toString());
        try{
          Gson gson = new Gson();
          TokenModel tokenmodel = gson.fromJson(response.body().charStream(), TokenModel.class);
          response.body().close();
          if(tokenmodel.getData() == null){
            //一般是用户名或者密码错误
            Log.e("test","message = " + tokenmodel.getMsg());
            errmsg = tokenmodel.getMsg();
            DBManager.getInstance(FukuanActivity.this).setPwdInvailed(accountList.get(accountIndex).getPhoneNumber());
            mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,getDelayTime());
          } else {
            //更新小号昵称
            DBManager.getInstance(FukuanActivity.this).updateNickName(accountList.get(accountIndex).getPhoneNumber(),
                    tokenmodel.getData().getHmMemberUserVo().getNickName());
            accessToken = tokenmodel.getData().getAccessToken();
            Message msg = mHandler.obtainMessage(GET_TOKEN_SUCCESS);
            msg.sendToTarget();
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
        }
      }
    });
  }

  private void getUserInfo() {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("whichFunc","GETUSERMODEL");

    FormBody body = new FormBody.Builder()
        .add("accessToken",accessToken)
        .add("data",job.toString())
        .build();
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_USERINFO_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try{
          Gson gson = new Gson();
          GetUserModel getUserModel = gson.fromJson(response.body().charStream(), GetUserModel.class);
          response.body().close();
          //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
          //获取成功之后
          if(getUserModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_USERINFO_SUCCESS);
            msg.obj = getUserModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_USERINFO_FAILED,getDelayTime());
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_USERINFO_FAILED,getDelayTime());
        }

      }
    });
  }

  private void getOrderList(String userId) {
    String url = "http://api.healthmall.cn/Post";
    JsonObject job = new JsonObject();
    job.addProperty("whichFunc","GETNEEDMERGEDORDER");
    job.addProperty("user_id",userId);

    FormBody body = new FormBody.Builder()
            .add("accessToken",accessToken)
            .add("data",job.toString())
            .build();
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();
          GetOrderListModel getOrderListModel = gson.fromJson(response.body().charStream(), GetOrderListModel.class);
          response.body().close();
          //获取成功之后
          if(getOrderListModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_ORDER_LIST_SUCCESS);
            msg.obj = getOrderListModel;
            msg.sendToTarget();
          }else{
            mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
        }
      }
    });
  }


    private void getAllPayment() {
        String url = "http://api.healthmall.cn/Post";
        JsonObject job = new JsonObject();
        job.addProperty("whichFunc","GetAllPayment");

        FormBody body = new FormBody.Builder()
                .add("accessToken",accessToken)
                .add("data",job.toString())
                .build();
        HttpUntils.getInstance(this).postForm(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              mHandler.sendEmptyMessageDelayed(GET_ALL_PAYMENT_FAILED,getDelayTime());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
              try {
                Gson gson = new Gson();
                GetAllPaymentModel getAllPaymentModel = gson.fromJson(response.body().charStream(), GetAllPaymentModel.class);
                response.body().close();
                //获取成功之后
                if(getAllPaymentModel.isSucceed()){
                  Message msg = mHandler.obtainMessage(GET_ALL_PAYMENT_SUCCESS);
                  msg.obj = getAllPaymentModel;
                  msg.sendToTarget();
                }else{
                  mHandler.sendEmptyMessageDelayed(GET_ALL_PAYMENT_FAILED,getDelayTime());
                }
              }catch (Exception e){
                mHandler.sendEmptyMessageDelayed(GET_ALL_PAYMENT_FAILED,getDelayTime());
              }
            }
        });
    }

  private void getPayinfo(final int payType) {
    String url = "http://api.healthmall.cn/Post";

    List<String> ordIds = new ArrayList<>();
    ordIds.clear();
    for(HmOrder order:hmOrders){
      ordIds.add(order.getHM_OrderId());
    }
    JsonArray jsonArray = new Gson().toJsonTree(ordIds, new TypeToken<List<String>>() {}.getType()).getAsJsonArray();

    JsonObject job = new JsonObject();
    job.addProperty("whichFunc","GETPAYINFO");
    job.addProperty("OrderType","mergerpayment");
    job.addProperty("PayType",payType);
    job.add("OrderIds",jsonArray);

    FormBody body = new FormBody.Builder()
            .add("accessToken",accessToken)
            .add("data",job.toString())
            .build();
    HttpUntils.getInstance(this).postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        try {
          Gson gson = new Gson();
          if (payType == 3){
            GetPayInfoModel getPayInfoModel = gson.fromJson(response.body().charStream(), GetPayInfoModel.class);
            response.body().close();
            //获取成功之后
            if(getPayInfoModel.isSucceed()){
              Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
              msg.obj = getPayInfoModel;
              msg.sendToTarget();
            }else{
              mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
            }
          }else if(payType == 7){
            GetPayInfoTonglianModel getPayInfoTonglianModel = gson.fromJson(response.body().charStream(), GetPayInfoTonglianModel.class);
            response.body().close();
            //获取成功之后
            if(getPayInfoTonglianModel.isSucceed()){
              Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
              msg.obj = getPayInfoTonglianModel;
              msg.sendToTarget();
            }else{
              mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
            }
          }else if(payType == 6) {
            GetPayInfoKuaijieModel getPayInfoKuaijieModel = gson.fromJson(response.body().charStream(), GetPayInfoKuaijieModel.class);
            response.body().close();
            //获取成功之后
            if(getPayInfoKuaijieModel.isSucceed()){
              Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
              msg.obj = getPayInfoKuaijieModel;
              msg.sendToTarget();
            }else{
              mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
            }
          }else if(payType == 5) {
            GetPayInfoJingdongModel getPayInfoJingdongModel = gson.fromJson(response.body().charStream(), GetPayInfoJingdongModel.class);
            response.body().close();
            //获取成功之后
            if(getPayInfoJingdongModel.isSucceed()){
              Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
              msg.obj = getPayInfoJingdongModel;
              msg.sendToTarget();
            }else{
              mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
            }
          }
        }catch (Exception e){
          mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
        }
      }
    });
  }

  private void getKuaiqianPageInfo(GetPayInfoModel payInfoModel) {
    PayinfoDetail payinfoDetail = payInfoModel.getValuse();
    String cloudCodeName = "getPayPage";
    JSONObject job = new JSONObject();
    try {
      job.put("action",payinfoDetail.getPostUrl());

      job.put("signType",payinfoDetail.getSignType());
      job.put("merchantAcctId",payinfoDetail.getMerchantAcctId());
      job.put("orderTime",payinfoDetail.getOrderTime());
      job.put("version",payinfoDetail.getVersion());
      job.put("payerIdType",payinfoDetail.getPayerIdType());
      job.put("productDesc",payinfoDetail.getProductDesc());
      job.put("inputCharset",payinfoDetail.getInputCharset());
      job.put("bgUrl",payinfoDetail.getBg_Url());
      job.put("ext1",payinfoDetail.getExt1());
      job.put("orderAmount",payinfoDetail.getOrderAmount());
      job.put("productNum",payinfoDetail.getProductNum());
      job.put("signMsg",payinfoDetail.getSignMsg());
      job.put("payType",payinfoDetail.getPayType());
      job.put("payerId",payinfoDetail.getPayerId());
      job.put("language",payinfoDetail.getLanguage());
      job.put("productName",payinfoDetail.getProductName());
      job.put("orderId",payinfoDetail.getOrderId());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(e == null){
          //Log.e("test","云端逻辑调用成功：" + o.toString());
          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
          intent.putExtra("title","快钱支付");
          intent.putExtra("orders",(Serializable)hmOrders);
          //showTheResult(o.toString());
          intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
          startActivityForResult(intent,payTypeFlag);
        }else {
          Log.e("test","云端逻辑调用失败：" + e.toString());
        }
      }
    });
  }

  private void getTonglianPageInfo(GetPayInfoTonglianModel payInfoTonglianModel) {
    PayinfoTonglianDetail payinfoDetail = payInfoTonglianModel.getValuse();
    String cloudCodeName = "getTonglianPayPage";
    JSONObject job = new JSONObject();
    try {
      job.put("action",payinfoDetail.getServerUrl());

      job.put("payerIDCard",payinfoDetail.getPayerIDCard());
      job.put("signType",payinfoDetail.getSignType());
      job.put("payerEmail",payinfoDetail.getPayerEmail());
      job.put("version",payinfoDetail.getVersion());
      job.put("inputCharset",payinfoDetail.getInputCharset());
      job.put("receiveUrl",payinfoDetail.getReceiveUrl());
      job.put("orderAmount",payinfoDetail.getOrderAmount());
      job.put("productNum",payinfoDetail.getProductNum());
      job.put("merchantId",payinfoDetail.getMerchantId());
      job.put("tradeNature",payinfoDetail.getTradeNature());
      job.put("extTL",payinfoDetail.getExtTL());
      job.put("pickupUrl",payinfoDetail.getPickupUrl());
      job.put("pid",payinfoDetail.getPid());
      job.put("orderCurrency",payinfoDetail.getOrderCurrency());
      job.put("payerTelephone",payinfoDetail.getPayerTelephone());
      job.put("pan",payinfoDetail.getPan());
      job.put("productId",payinfoDetail.getProductId());

      job.put("issuerId",payinfoDetail.getIssuerId());
      job.put("productDesc",payinfoDetail.getProductDesc());
      job.put("orderNo",payinfoDetail.getOrderNo());
      job.put("ext1",payinfoDetail.getExt1());
      job.put("ext2",payinfoDetail.getExt2());
      job.put("orderExpireDatetime",payinfoDetail.getOrderExpireDatetime());
      job.put("signMsg",payinfoDetail.getSignMsg());
      job.put("payType",payinfoDetail.getPayType());
      job.put("language",payinfoDetail.getLanguage());
      job.put("orderDatetime",payinfoDetail.getOrderDatetime());
      job.put("productPrice",payinfoDetail.getProductPrice());
      job.put("productName",payinfoDetail.getProductName());
      job.put("payerName",payinfoDetail.getPayerName());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(e == null){
          //Log.e("test","云端逻辑调用成功：" + o.toString());
          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
          intent.putExtra("title","通联支付");
          intent.putExtra("orders",(Serializable)hmOrders);
          //showTheResult(o.toString());
          intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
          startActivityForResult(intent,payTypeFlag);
        }else {
          Log.e("test","云端逻辑调用失败：" + e.toString());
        }
      }
    });
  }


  private void getKuaijiePageInfo(GetPayInfoKuaijieModel payInfoKuaijieModel) {
    PayinfoKuaijieDetail payinfoDetail = payInfoKuaijieModel.getValuse();
    String cloudCodeName = "getKuaijiePayPage";
    JSONObject job = new JSONObject();
    try {
      job.put("action",payinfoDetail.getPostUrl());

      job.put("userIP",payinfoDetail.getUserIP());
      job.put("requestTime",payinfoDetail.getRequestTime());
      job.put("pageUrl",payinfoDetail.getPageUrl());
      job.put("signType",payinfoDetail.getSignType());
      job.put("outMemberRegistTime",payinfoDetail.getOutMemberRegistTime());
      job.put("charset",payinfoDetail.getCharset());
      job.put("jsCallback",payinfoDetail.getJsCallback());
      job.put("outMemberName",payinfoDetail.getOutMemberName());
      job.put("bankCode",payinfoDetail.getBankCode());
      job.put("currency",payinfoDetail.getCurrency());

      job.put("outMemberVerifyStatus",payinfoDetail.getOutMemberVerifyStatus());
      job.put("amount",payinfoDetail.getAmount());
      job.put("productDesc",payinfoDetail.getProductDesc());
      job.put("outMemberId",payinfoDetail.getOutMemberId());
      job.put("exts",payinfoDetail.getExts().replaceAll("\"", "&quot;")); //s.replaceAll("\"", "&quot;")
      job.put("outMemberMobile",payinfoDetail.getOutMemberMobile());
      job.put("backUrl",payinfoDetail.getBackUrl());
      job.put("merchantOrderNo",payinfoDetail.getMerchantOrderNo());
      job.put("signMsg",payinfoDetail.getSignMsg());
      job.put("merchantNo",payinfoDetail.getMerchantNo());

      job.put("outMemberRegistIP",payinfoDetail.getOutMemberRegistIP());
      job.put("notifyUrl",payinfoDetail.getNotifyUrl());
      job.put("productName",payinfoDetail.getProductName());
      job.put("bankCardType",payinfoDetail.getBankCardType());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(e == null){
          //Log.e("test","云端逻辑调用成功：" + o.toString());
          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
          intent.putExtra("title","快捷支付");
          intent.putExtra("orders",(Serializable)hmOrders);
          //showTheResult(o.toString());
          intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
          startActivityForResult(intent,payTypeFlag);
        }else {
          Log.e("test","云端逻辑调用失败：" + e.toString());
        }
      }
    });
  }

  private void getJingdongPageInfo(GetPayInfoJingdongModel getPayInfoJingdongModel) {
    PayinfoJingdongDetail payinfoDetail = getPayInfoJingdongModel.getValuse();
    String cloudCodeName = "getJingdongPayPage";
    JSONObject job = new JSONObject();
    try {
      job.put("action",payinfoDetail.getServerUrl());

      job.put("successCallbackUrl",payinfoDetail.getSuccessCallbackUrl());
      job.put("tradeDescription",payinfoDetail.getTradeDescription());
      job.put("tradeTime",payinfoDetail.getTradeTime());
      job.put("tradeNum",payinfoDetail.getTradeNum());
      job.put("tradeName",payinfoDetail.getTradeName());
      job.put("merchantRemark",payinfoDetail.getMerchantRemark());
      job.put("version",payinfoDetail.getVersion());
      job.put("currency",payinfoDetail.getCurrency());
      job.put("merchantSign",payinfoDetail.getMerchantSign());
      job.put("token",payinfoDetail.getToken());

      job.put("tradeAmount",payinfoDetail.getTradeAmount());
      job.put("notifyUrl",payinfoDetail.getNotifyUrl());
      job.put("merchantNum",payinfoDetail.getMerchantNum());
      job.put("failCallbackUrl",payinfoDetail.getFailCallbackUrl());
    } catch (JSONException e) {
      e.printStackTrace();
    }

    //创建云端逻辑
    AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
    cloudCode.callEndpoint(cloudCodeName, job, new CloudCodeListener() {
      @Override
      public void done(Object o, BmobException e) {
        if(e == null){
          //Log.e("test","云端逻辑调用成功：" + o.toString());
          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
          intent.putExtra("title","京东支付");
          intent.putExtra("orders",(Serializable)hmOrders);
          //showTheResult(o.toString());
          intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
          startActivityForResult(intent,payTypeFlag);
        }else {
          Log.e("test","云端逻辑调用失败：" + e.toString());
        }
      }
    });

  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("test","onActivityResult requestCode = " + requestCode);
    if(requestCode == PAY_TYPE_TONGLIAN_ZHIFU || requestCode == PAY_TYPE_KUAIQIAN_ZHIFU || requestCode == PAY_TYPE_KUAIJIE_ZHIFU){
        showContinueDialog();
    }
  }

  private void showContinueDialog(){
    AlertDialog dialog = new AlertDialog.Builder(FukuanActivity.this)
            .setTitle("下一步")
            .setMessage("请选择下一步操作(如果付款成功，请选择继续付款，如果付款失败请选择重新付款)")
            .setNegativeButton("继续付款", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showTheResult("----------------继续付款,开始付款下一个帐号\n\n\n");
                accountIndex++;
                mHandler.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
              }
            })
            .setPositiveButton("重新付款", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e("test","重新付款,重新开始付款此帐号");
                showTheResult("----------------重新付款此小号\n");
                mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
              }
            }).create();
    dialog.show();
  }

  private void showChooseFukuanMode(){
    //显示popupwindow
    popwindow = new ChoosePayOptionsPopwindow(this,fukuanChoice,this);
    popwindow.showAtLocation(findViewById(R.id.main_view), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
    WindowManager.LayoutParams lp = getWindow().getAttributes();
    lp.alpha = 0.5f;
    getWindow().setAttributes(lp);
    popwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
      @Override
      public void onDismiss() {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha=1f;
        getWindow().setAttributes(lp);
        //showContinueDialog();
      }
    });
  }

  private void startToFuKuan() {
    Message msg = mHandler.obtainMessage(START_TO_FU_KUAN);
    msg.sendToTarget();
  }


  private int getDelayTime() {
    int randTime = CommonUntils.getRandomInt(min_time,max_time);
    Log.d("test","randTime = " + randTime);
    return randTime;
  }

  private void updateTheCoinsNumber(){
    userInfo.setCoinsNumber(userInfo.getCoinsNumber() - 1);
    userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
      @Override
      public void done(BmobException e) {
        if(e == null){
          Log.e("test","updateTheCoinsNumber 更新用户积分成功");
        }
      }
    });
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight() + 5;
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }
}
