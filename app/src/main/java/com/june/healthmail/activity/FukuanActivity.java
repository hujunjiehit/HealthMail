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
import com.june.healthmail.model.GetPayInfoModel;
import com.june.healthmail.model.GetPayInfoTonglianModel;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.HmOrder;
import com.june.healthmail.model.PayinfoDetail;
import com.june.healthmail.model.PayinfoTonglianDetail;
import com.june.healthmail.model.TokenModel;
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

public class FukuanActivity extends Activity implements View.OnClickListener{

  private Button btn_start;
  private TextView tvShowResult;

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


  private int[] fukuanChoice = {0,0,0,0,0};
  private ChoosePayOptionsPopwindow popwindow;
  private int payTypeFlag;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_FU_KUAN:
          if (isRunning) {
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
              showTheResult("******所有账号约课结束**********\n");
              isRunning = false;
              btn_start.setText("付款完成");
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
              if (getAllPaymentModel.getValuse().get(i).getChannelamount() > 0) {
                fukuanChoice[i] = 1;
              } else {
                fukuanChoice[i] = 0;
              }
            }
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
          }

          break;

          case GET_TOKEN_FAILED:
            showTheResult("---获取token失败，重新开始该小号\n");
            this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
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

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fukuan);
    initView();
    setListener();
    initData();
  }



  private void initView() {
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
  }

  private void initData() {
    accountList.clear();
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

    min_time = PreferenceHelper.getInstance().getMinYuekeTime();
    max_time = PreferenceHelper.getInstance().getMaxYuekeTime();
  }

  @Override
  public void onClick(View v) {
    if(popwindow != null && popwindow.isShowing()){
      popwindow.dismiss();
    }
    switch (v.getId()){
      case R.id.btn_start:
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
        break;
      case R.id.btn_fukuan_kuaiqian://块钱支付
        Log.e("test","click btn_fukuan_kuaiqian");
        payTypeFlag = PAY_TYPE_KUAIQIAN_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_jingdong://京东支付
        Log.e("test","click btn_fukuan_jingdong");
        break;
      case R.id.btn_fukuan_tonglian://通联支付
        Log.e("test","click btn_fukuan_tonglian");
        payTypeFlag = PAY_TYPE_TONGLIAN_ZHIFU;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_yilian://易联支付
        Log.e("test","click btn_fukuan_yilian");
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
        Gson gson = new Gson();
        TokenModel tokenmodel = gson.fromJson(response.body().charStream(), TokenModel.class);

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
        //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        Gson gson = new Gson();
        GetUserModel getUserModel = gson.fromJson(response.body().charStream(), GetUserModel.class);
        //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
        //获取成功之后
        if(getUserModel.isSucceed()){
          Message msg = mHandler.obtainMessage(GET_USERINFO_SUCCESS);
          msg.obj = getUserModel;
          msg.sendToTarget();
        }else{
          //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
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
        //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        Gson gson = new Gson();
        GetOrderListModel getOrderListModel = gson.fromJson(response.body().charStream(), GetOrderListModel.class);
        //获取成功之后
        if(getOrderListModel.isSucceed()){
          Message msg = mHandler.obtainMessage(GET_ORDER_LIST_SUCCESS);
          msg.obj = getOrderListModel;
          msg.sendToTarget();
        }else{
          //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
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
                //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Gson gson = new Gson();
                GetAllPaymentModel getAllPaymentModel = gson.fromJson(response.body().charStream(), GetAllPaymentModel.class);
                //获取成功之后
                if(getAllPaymentModel.isSucceed()){
                    Message msg = mHandler.obtainMessage(GET_ALL_PAYMENT_SUCCESS);
                    msg.obj = getAllPaymentModel;
                    msg.sendToTarget();
                }else{
                    //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
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
        //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        Gson gson = new Gson();
        if (payType == 3){
          GetPayInfoModel getPayInfoModel = gson.fromJson(response.body().charStream(), GetPayInfoModel.class);
          //获取成功之后
          if(getPayInfoModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
            msg.obj = getPayInfoModel;
            msg.sendToTarget();
          }else{
            //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
          }
        }else if(payType == 7){
          GetPayInfoTonglianModel getPayInfoTonglianModel = gson.fromJson(response.body().charStream(), GetPayInfoTonglianModel.class);
          //获取成功之后
          if(getPayInfoTonglianModel.isSucceed()){
            Message msg = mHandler.obtainMessage(GET_PAYINFO_SUCCESS);
            msg.obj = getPayInfoTonglianModel;
            msg.sendToTarget();
          }else{
            //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
          }
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
          Log.e("test","云端逻辑调用成功：" + o.toString());

          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
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
          Log.e("test","云端逻辑调用成功：" + o.toString());
          Intent intent = new Intent();
          intent.putExtra("data",o.toString());
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

  private void openWebviewActivity(GetPayInfoModel payInfoModel){
    PayinfoDetail payinfoDetail = payInfoModel.getValuse();
    StringBuilder sb = new StringBuilder();

    String url = payinfoDetail.getPostUrl();

    JsonObject job = new JsonObject();
    job.addProperty("MerchantAcctId",payinfoDetail.getMerchantAcctId());
    job.addProperty("Bg_Url",payinfoDetail.getBg_Url());
    job.addProperty("PostUrl",payinfoDetail.getPostUrl());
    job.addProperty("SignMsg",payinfoDetail.getSignMsg());
    job.addProperty("InputCharset",payinfoDetail.getInputCharset());
    job.addProperty("Version",payinfoDetail.getVersion());
    job.addProperty("Language",payinfoDetail.getLanguage());
    job.addProperty("SignType",payinfoDetail.getSignType());
    job.addProperty("ext1",payinfoDetail.getExt1());
    job.addProperty("PayerIdType",payinfoDetail.getPayerIdType());
    job.addProperty("PayType",payinfoDetail.getPayType());
    job.addProperty("productDesc",payinfoDetail.getProductDesc());
    job.addProperty("orderAmount",payinfoDetail.getOrderAmount());
    job.addProperty("productName",payinfoDetail.getProductName());
    job.addProperty("productNum",payinfoDetail.getProductNum());
    job.addProperty("orderId",payinfoDetail.getOrderId());
    job.addProperty("orderTime",payinfoDetail.getOrderTime());
    job.addProperty("payerId",payinfoDetail.getPayerId());
    job.addProperty("pageUrl",payinfoDetail.getPageUrl());


//    FormBody body = new FormBody.Builder()
//            .add("orderId",payinfoDetail.getOrderId())
//            .add("orderAmount",payinfoDetail.getOrderAmount())
//            .add("productName",payinfoDetail.getProductName())
//            .add("productNum",payinfoDetail.getProductNum())
//            .add("productDesc",payinfoDetail.getProductDesc())
//            .add("ext1",payinfoDetail.getExt1())
//            .add("signMsg",payinfoDetail.getSignMsg())
//            .add("merchantAcctId",payinfoDetail.getMerchantAcctId())
//            .add("inputCharset",payinfoDetail.getInputCharset())
//            .add("bgUrl",payinfoDetail.getBg_Url())
//            .add("version",payinfoDetail.getVersion())
//            .add("language",payinfoDetail.getLanguage())
//            .add("signType",payinfoDetail.getSignType())
//            .add("payerIdType",payinfoDetail.getPayerIdType())
//            .add("payerId",payinfoDetail.getPayerId())
//            .add("orderTime",payinfoDetail.getOrderTime())
//            .add("payType",payinfoDetail.getPayType())
//            .build();
//
//    HttpUntils.getInstance(this).postForm(url,body,new Callback() {
//      @Override
//      public void onFailure(Call call, IOException e) {
//        //mHandler.sendEmptyMessageDelayed(GET_GUANZHU_LIST_FAILED,getDelayTime());
//      }
//      @Override
//      public void onResponse(Call call, Response response) throws IOException {
//
//      }
//    });

    Intent intent = new Intent();
    intent.putExtra("url","http://mao.yikey.net/payMoneyType.do?"+"type=" + payinfoDetail.getPayerIdType() + "&token="+accessToken+
    "&data="+job.toString());
    showTheResult(url);
    intent.setClass(this,WebViewActivity.class);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Log.e("test","onActivityResult requestCode = " + requestCode);
    if(requestCode == PAY_TYPE_TONGLIAN_ZHIFU || requestCode == PAY_TYPE_KUAIQIAN_ZHIFU){
        showContinueDialog();
    }
  }

  private void showContinueDialog(){
    AlertDialog dialog = new AlertDialog.Builder(FukuanActivity.this)
            .setTitle("下一步")
            .setMessage("请选择下一步操作(如果支付成功，请选择继续付款，如果付款失败请选择重新付款)")
            .setNegativeButton("继续付款", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e("test","继续付款,开始付款下一个帐号");
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

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight();
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }
  private void toast(String msg){
    Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
  }
}
