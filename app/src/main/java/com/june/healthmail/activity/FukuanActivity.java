package com.june.healthmail.activity;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.june.healthmail.R;
import com.june.healthmail.adapter.OrderListAdapter;
import com.june.healthmail.http.ApiService;
import com.june.healthmail.http.HttpManager;
import com.june.healthmail.http.bean.BaseBean;
import com.june.healthmail.model.AccountInfo;
import com.june.healthmail.model.GetAllPaymentModel;
import com.june.healthmail.model.GetOrderListModel;
import com.june.healthmail.model.GetPayInfoJingdongModel;
import com.june.healthmail.model.GetPayInfoKuaijieModel;
import com.june.healthmail.model.GetPayInfoModel;
import com.june.healthmail.model.GetPayInfoModelKuaiqian2;
import com.june.healthmail.model.GetPayInfoTonglianModel;
import com.june.healthmail.model.GetUserModel;
import com.june.healthmail.model.HmOrder;
import com.june.healthmail.model.PayinfoDetail;
import com.june.healthmail.model.PayinfoDetailKuiqian2;
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
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.view.ChoosePayOptionsPopwindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;
import retrofit2.Retrofit;

/**
 * Created by bjhujunjie on 2017/3/9.
 */

public class FukuanActivity extends BaseActivity implements View.OnClickListener, OrderListAdapter.Callback{

  private Button btn_start;
  private TextView tvShowResult;
  private TextView tvCoinsNumber;
  private TextView tvCoinsDesc;
  private CheckBox cbPayAllOrders;
  private ShowProgress showProgress;
  private Retrofit mRetrofit;

  private View mRootView;

  private ArrayList<AccountInfo> accountList = new ArrayList<>();

  private Boolean isRunning = false;
  private int offset;
  private int coinsCost;

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

  private static final int PAY_TYPE_KUAIQIAN_ZHIFU_1 = 20;
  private static final int PAY_TYPE_TONGLIAN_ZHIFU = 21;
  private static final int PAY_TYPE_KUAIJIE_ZHIFU = 22;
  private static final int PAY_TYPE_JINGDONG_ZHIFU = 23;
  private static final int PAY_TYPE_YILIAN_ZHIFU = 24;
  private static final int PAY_TYPE_KUAIQIAN_ZHIFU_2 = 25;
  private static final int REQUEST_INVAILED = 26;

  private int accountIndex = 0;

  private Message message;
  private String accessToken;
  private String user_id;
  private int min_time;
  private int max_time;
  private String errmsg;

  private ArrayList<HmOrder> hmOrders = new ArrayList<>();
  private UserInfo userInfo;

  private int[] fukuanChoice = {0,0,0,0,0,0};
  private ChoosePayOptionsPopwindow popwindow;
  private int payTypeFlag;
  ArrayList<Double> values;
  private OrderListAdapter orderListAdapter;

  private Handler mHandler = new Handler() {

    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case START_TO_FU_KUAN:
          if (isRunning) {
            if(userInfo.getCoinsNumber() > 0 || CommonUntils.isPayUser(userInfo)) {
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
                    getOrderListModel.getValuse().get(i).setSelected(true);
                    hmOrders.add(getOrderListModel.getValuse().get(i));
                }
                if(hmOrders.size() > 0){
                  showTheResult("------------共有" + hmOrders.size() + "个订单\n");
                  if(userInfo.getPayStatus() == null) {
                    updateTheCoinsNumber();
                    tvCoinsNumber.setText(userInfo.getCoinsNumber()+"");
                    showTheResult("--------------金币余额-" + coinsCost*((hmOrders.size() -1)/20 + 1) + "\n");
                  } else {
                    if(userInfo.getPayStatus() != 1) {
                      updateTheCoinsNumber();
                      tvCoinsNumber.setText(userInfo.getCoinsNumber()+"");
                      showTheResult("--------------金币余额-" + coinsCost*((hmOrders.size() -1)/20 + 1) + "\n");
                    }
                  }

                  if(cbPayAllOrders.isChecked()) {
                    this.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
                  } else {
                    //显示订单选择对话框
                    showChooseOlderDialog();
                  }
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
            if(values == null) {
              values = new ArrayList<>();
            }
            values.clear();
            for(int i = 0; i < getAllPaymentModel.getValuse().size(); i++){
              values.add(i,getAllPaymentModel.getValuse().get(i).getChannelamount());
              //testcode
              if(userInfo != null &&
                      (userInfo.getUsername().equals("13027909110") ||
                              userInfo.getUsername().equals("18002570032") ||
                              userInfo.getUsername().equals("18671400766"))){
                showTheResult("---------------支付方式" + (i + 1) + "剩余支付金额：" + getAllPaymentModel.getValuse().get(i).getChannelamount() + "\n");
              }
            }
            //上传金额到服务器
            //CommonUntils.update(getAllPaymentModel.getMsgTime(),values);
            initFukuanChoice(getAllPaymentModel.getValuse());
            showChooseFukuanMode();
            break;

        case START_TO_GET_PAYINFO:
          if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU_1){
            showTheResult("---------------用户选择快钱支付--储蓄卡\n");
            //payType = 3 表示快钱支付-储蓄卡
            getPayinfo(3);
          }else if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU_2){
            showTheResult("---------------用户选择快钱支付--信用卡\n");
            //payType = 13 表示快钱支付-信用卡
            getPayinfo(13);
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
          if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU_1){
            GetPayInfoModel payInfoModel = (GetPayInfoModel) msg.obj;
            getKuaiqianPageInfo(payInfoModel.getValuse());
          }else if(payTypeFlag == PAY_TYPE_KUAIQIAN_ZHIFU_2){
            GetPayInfoModelKuaiqian2 payInfoModel =  (GetPayInfoModelKuaiqian2) msg.obj;
            getKuaiqianPageInfo(dealPayInfo(payInfoModel.getValuse()));
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
        case REQUEST_INVAILED:
          showTheResult("***错误信息："+ errmsg + "\n");
          showTheResult("***请求失效，小号管理标记为绿色，继续下一个****************\n\n\n");
          accountIndex++;
          this.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
          break;
        default:
          break;
      }
    }
  };

  private PayinfoDetail dealPayInfo(PayinfoDetailKuiqian2 payInfoDetail) {
    PayinfoDetail payInfo = new PayinfoDetail();
    payInfo.setPostUrl(payInfoDetail.getRequestUrl());
    Map<String,String> map = CommonUntils.splitUrlparam(payInfoDetail.getRequestParam());
    payInfo.setSignType(map.get("signType"));
    payInfo.setMerchantAcctId(map.get("merchantAcctId"));
    payInfo.setOrderTime(map.get("orderTime"));
    payInfo.setVersion(map.get("version"));
    payInfo.setPayerIdType(map.get("payerIdType"));
    payInfo.setProductDesc(map.get("productDesc"));
    payInfo.setInputCharset(map.get("inputCharset"));
    payInfo.setBg_Url(map.get("bgUrl"));
    payInfo.setExt1(map.get("ext1"));
    payInfo.setOrderAmount(map.get("orderAmount"));
    payInfo.setProductNum(map.get("productNum"));
    payInfo.setSignMsg(map.get("signMsg"));
    payInfo.setPayType(map.get("payType"));
    payInfo.setPayerId(map.get("payerId"));
    payInfo.setLanguage(map.get("language"));
    payInfo.setProductName(map.get("productName"));
    payInfo.setOrderId(map.get("orderId"));
    return payInfo;
  }

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
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("快钱支付(储蓄卡)")){
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
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("易联支付")){
        fukuanChoice[index] = 1;
      }
    }

    index = 4;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("汇付支付")){
        fukuanChoice[index] = 1;
      }
    }

    index = 5;
    fukuanChoice[index] = 0;
    for(Payment pyment:allpayments){
      if(pyment.getChannelamount() > 0 && pyment.getHm_p_name().equals("快钱支付(信用卡)")){
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
    showProgress = new ShowProgress(this);
    btn_start = (Button) findViewById(R.id.btn_start);
    tvShowResult = (TextView) findViewById(R.id.et_show_result);
    tvShowResult.setMovementMethod(ScrollingMovementMethod.getInstance());
    tvCoinsNumber = (TextView) findViewById(R.id.tv_coins_number);
    tvCoinsDesc =  (TextView) findViewById(R.id.tv_coins_desc);
    cbPayAllOrders = (CheckBox) findViewById(R.id.cb_pay_all_orders);
    mRootView = findViewById(R.id.main_view);
    if (PreferenceHelper.getInstance().getPayAllOrders()) {
      cbPayAllOrders.setChecked(true);
    } else {
      cbPayAllOrders.setChecked(false);
    }
    if(userInfo.getPayStatus() != null && userInfo.getPayStatus() == 1) {
      tvCoinsNumber.setVisibility(View.GONE);
      tvCoinsDesc.setText("付款永久用户(付款时不消耗金币)");
    }
  }

  private void setListener() {
    btn_start.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
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
  }

  private void initData() {
    coinsCost = PreferenceHelper.getInstance().getPayCost();
    if(coinsCost == 0) {
      tvCoinsNumber.setVisibility(View.GONE);
      tvCoinsDesc.setText("今日活动，付款免费");
    }

    accountList.clear();
    tvCoinsNumber.setText(userInfo.getCoinsNumber() + "");
    SQLiteDatabase db = DBManager.getInstance().getDb();
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
      case R.id.btn_fukuan_kuaiqian_1://快钱支付 储蓄卡
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        Log.e("test","click btn_fukuan_kuaiqian_1 chuxuka");
        payTypeFlag = PAY_TYPE_KUAIQIAN_ZHIFU_1;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_kuaiqian_2://快钱支付 信用卡
        if(popwindow != null && popwindow.isShowing()){
          popwindow.dismiss();
        }
        Log.e("test","click btn_fukuan_kuaiqian_2 xinyongka");
        payTypeFlag = PAY_TYPE_KUAIQIAN_ZHIFU_2;
        mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
        break;
      case R.id.btn_fukuan_huifu://汇付支付
        Log.e("test","click btn_fukuan_yilian");
        toast("暂不支持汇付支付");
        //payTypeFlag = PAY_TYPE_JINGDONG_ZHIFU;
        //mHandler.sendEmptyMessageDelayed(START_TO_GET_PAYINFO,getDelayTime());
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

    HttpUntils.getInstance().postForm(url, body, new Callback() {
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
            //一般是用户名或者密码错误 40022
            Log.e("test","message = " + tokenmodel.getMsg());
            errmsg = tokenmodel.getMsg();
            if(errmsg.contains("密码")){
              DBManager.getInstance().setPwdInvailed(accountList.get(accountIndex).getPhoneNumber());
              mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,getDelayTime());
            }else {
              //请求失效
              DBManager.getInstance().setRequestInvailed(accountList.get(accountIndex).getPhoneNumber());
              mHandler.sendEmptyMessageDelayed(REQUEST_INVAILED,getDelayTime());
            }
          } else {
            //更新小号昵称
            DBManager.getInstance().updateUserInfo(accountList.get(accountIndex).getPhoneNumber(),
                tokenmodel.getData().getHmMemberUserVo());
            accessToken = tokenmodel.getData().getAccessToken();

            //缓存accessToken到sharepreference，避免被内存回收

            PreferenceHelper.getInstance().setAccessToken(accessToken);
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
        .add("accessToken",getAccessToken())
        .add("data",job.toString())
        .build();
    HttpUntils.getInstance().postForm(url, body, new Callback() {
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
            .add("accessToken",getAccessToken())
            .add("data",job.toString())
            .build();
    HttpUntils.getInstance().postForm(url, body, new Callback() {
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
                .add("accessToken",getAccessToken())
                .add("data",job.toString())
                .build();

      if(showProgress != null && !showProgress.isShowing()){
        if(!isFinishing()){
          showProgress.setMessage("正在获取支付方式...");
          showProgress.show();
        }else {
          //activity已经finish，直接返回
          return;
        }
      }

        HttpUntils.getInstance().postForm(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
              if(showProgress != null && showProgress.isShowing()){
                showProgress.dismiss();
              }
              mHandler.sendEmptyMessageDelayed(GET_ALL_PAYMENT_FAILED,getDelayTime());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
              if(showProgress != null && showProgress.isShowing()){
                showProgress.dismiss();
              }
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
      if(order.isSelected()){
        ordIds.add(order.getHM_OrderId());
      }
    }
    if(ordIds.size() <= 0) {
      toast("需要付款的订单不能为空");
      showTheResult("---------订单不能为空，重新获取订单列表\n");
      mHandler.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
      return;
    }
    JsonArray jsonArray = new Gson().toJsonTree(ordIds, new TypeToken<List<String>>() {}.getType()).getAsJsonArray();

    JsonObject job = new JsonObject();
    job.addProperty("whichFunc","GETPAYINFO");
    job.addProperty("OrderType","mergerpayment");
    job.addProperty("PayType",payType);
    job.add("OrderIds",jsonArray);

    FormBody body = new FormBody.Builder()
            .add("accessToken",getAccessToken())
            .add("data",job.toString())
            .build();

    if(showProgress != null && !showProgress.isShowing()){
      showProgress.setMessage("正在获取支付详情...");
      showProgress.show();
    }

    HttpUntils.getInstance().postForm(url, body, new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if(showProgress != null && showProgress.isShowing()){
          showProgress.dismiss();
        }
        mHandler.sendEmptyMessageDelayed(GET_PAYINFO_FAILED,getDelayTime());
      }
      @Override
      public void onResponse(Call call, Response response) throws IOException {
        if(showProgress != null && showProgress.isShowing()){
          showProgress.dismiss();
        }
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
          }else if(payType == 13){
            GetPayInfoModelKuaiqian2 getPayInfoModel = gson.fromJson(response.body().charStream(), GetPayInfoModelKuaiqian2.class);
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

  private void getKuaiqianPageInfo(PayinfoDetail payinfoDetail) {
    FormBody body = new FormBody.Builder()
        .add("action",payinfoDetail.getPostUrl())
        .add("signType",payinfoDetail.getSignType())
        .add("merchantAcctId",payinfoDetail.getMerchantAcctId())
        .add("orderTime",payinfoDetail.getOrderTime())
        .add("version",payinfoDetail.getVersion())
        .add("payerIdType",payinfoDetail.getPayerIdType())
        .add("productDesc",payinfoDetail.getProductDesc())
        .add("inputCharset",payinfoDetail.getInputCharset())
        .add("bgUrl",payinfoDetail.getBg_Url())
        .add("ext1",payinfoDetail.getExt1())
        .add("orderAmount",payinfoDetail.getOrderAmount())
        .add("productNum",payinfoDetail.getProductNum())
        .add("signMsg",payinfoDetail.getSignMsg())
        .add("payType",payinfoDetail.getPayType())
        .add("payerId",payinfoDetail.getPayerId())
        .add("language",payinfoDetail.getLanguage())
        .add("productName",payinfoDetail.getProductName())
        .add("orderId",payinfoDetail.getOrderId())
        .build();

    if(mRetrofit == null) {
      mRetrofit = HttpManager.getInstance().getRetrofit();
    }
    mRetrofit.create(ApiService.class).getPayInfo("getKuaiqianPageInfo",body).enqueue(new retrofit2.Callback<BaseBean>() {
      @Override
      public void onResponse(retrofit2.Call<BaseBean> call, retrofit2.Response<BaseBean> response) {
        Log.e("test","body = " + response.body());
        Intent intent = new Intent();
        intent.putExtra("data",response.body().getMessage());
        intent.putExtra("title","快钱支付");
        intent.putExtra("orders",(Serializable)hmOrders);
        intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
        startActivityForResult(intent,payTypeFlag);
      }

      @Override
      public void onFailure(retrofit2.Call<BaseBean> call, Throwable t) {
        Log.e("test","getKuaiqianPageInfo failed");
        showTheResult("-------------------前往支付页面失败，重新选择支付方式\n");
        mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
      }
    });
  }

  private void getTonglianPageInfo(GetPayInfoTonglianModel payInfoTonglianModel) {
    PayinfoTonglianDetail payinfoDetail = payInfoTonglianModel.getValuse();
    FormBody body = new FormBody.Builder()
        .add("action",payinfoDetail.getServerUrl())
        .add("payerIDCard",payinfoDetail.getPayerIDCard())
        .add("signType",payinfoDetail.getSignType())
        .add("payerEmail",payinfoDetail.getPayerEmail())
        .add("version",payinfoDetail.getVersion())
        .add("inputCharset",payinfoDetail.getInputCharset())
        .add("receiveUrl",payinfoDetail.getReceiveUrl())
        .add("orderAmount",payinfoDetail.getOrderAmount())
        .add("productNum",payinfoDetail.getProductNum())
        .add("merchantId",payinfoDetail.getMerchantId())
        .add("tradeNature",payinfoDetail.getTradeNature())
        .add("extTL",payinfoDetail.getExtTL())
        .add("pickupUrl",payinfoDetail.getPickupUrl())
        .add("pid",payinfoDetail.getPid())
        .add("orderCurrency",payinfoDetail.getOrderCurrency())
        .add("payerTelephone",payinfoDetail.getPayerTelephone())
        .add("pan",payinfoDetail.getPan())
        .add("productId",payinfoDetail.getProductId())

        .add("issuerId",payinfoDetail.getIssuerId())
        .add("productDesc",payinfoDetail.getProductDesc())
        .add("orderNo",payinfoDetail.getOrderNo())
        .add("ext1",payinfoDetail.getExt1())
        .add("ext2",payinfoDetail.getExt2())
        .add("orderExpireDatetime",payinfoDetail.getOrderExpireDatetime())
        .add("signMsg",payinfoDetail.getSignMsg())
        .add("payType",payinfoDetail.getPayType())
        .add("language",payinfoDetail.getLanguage())
        .add("orderDatetime",payinfoDetail.getOrderDatetime())
        .add("productPrice",payinfoDetail.getProductPrice())
        .add("productName",payinfoDetail.getProductName())
        .add("payerName",payinfoDetail.getPayerName())
        .build();

    if(mRetrofit == null) {
      mRetrofit = HttpManager.getInstance().getRetrofit();
    }
    mRetrofit.create(ApiService.class).getPayInfo("getTonglianPageInfo", body).enqueue(new retrofit2.Callback<BaseBean>() {
      @Override
      public void onResponse(retrofit2.Call<BaseBean> call, retrofit2.Response<BaseBean> response) {
        Intent intent = new Intent();
        intent.putExtra("data",response.body().getMessage());
        intent.putExtra("title","通联支付");
        intent.putExtra("orders",(Serializable)hmOrders);
        intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
        startActivityForResult(intent,payTypeFlag);
      }

      @Override
      public void onFailure(retrofit2.Call<BaseBean> call, Throwable t) {
        showTheResult("-------------------前往支付页面失败，重新选择支付方式\n");
        mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
      }
    });
  }


  private void getKuaijiePageInfo(GetPayInfoKuaijieModel payInfoKuaijieModel) {
    PayinfoKuaijieDetail payinfoDetail = payInfoKuaijieModel.getValuse();
    FormBody body = new FormBody.Builder()
        .add("action",payinfoDetail.getPostUrl())
        .add("userIP",payinfoDetail.getUserIP())
        .add("requestTime",payinfoDetail.getRequestTime())
        .add("pageUrl",payinfoDetail.getPageUrl())
        .add("signType",payinfoDetail.getSignType())
        .add("outMemberRegistTime",payinfoDetail.getOutMemberRegistTime())
        .add("charset",payinfoDetail.getCharset())
        .add("jsCallback",payinfoDetail.getJsCallback())
        .add("outMemberName",payinfoDetail.getOutMemberName())
        .add("bankCode",payinfoDetail.getBankCode())
        .add("currency",payinfoDetail.getCurrency())
        .add("outMemberVerifyStatus",payinfoDetail.getOutMemberVerifyStatus())
        .add("amount",payinfoDetail.getAmount())
        .add("productDesc",payinfoDetail.getProductDesc())
        .add("outMemberId",payinfoDetail.getOutMemberId())
        .add("exts",payinfoDetail.getExts().replaceAll("\"", "&quot;")) //s.replaceAll("\"", "&quot;")
        .add("outMemberMobile",payinfoDetail.getOutMemberMobile())
        .add("backUrl",payinfoDetail.getBackUrl())
        .add("merchantOrderNo",payinfoDetail.getMerchantOrderNo())
        .add("signMsg",payinfoDetail.getSignMsg())
        .add("merchantNo",payinfoDetail.getMerchantNo())
        .add("outMemberRegistIP",payinfoDetail.getOutMemberRegistIP())
        .add("notifyUrl",payinfoDetail.getNotifyUrl())
        .add("productName",payinfoDetail.getProductName())
        .add("bankCardType",payinfoDetail.getBankCardType())
        .build();
    if(mRetrofit == null) {
      mRetrofit = HttpManager.getInstance().getRetrofit();
    }
    mRetrofit.create(ApiService.class).getPayInfo("getKuaijiePageInfo",body).enqueue(new retrofit2.Callback<BaseBean>() {
      @Override
      public void onResponse(retrofit2.Call<BaseBean> call, retrofit2.Response<BaseBean> response) {
        Intent intent = new Intent();
        intent.putExtra("data",response.body().getMessage());
        intent.putExtra("title","快捷支付");
        intent.putExtra("orders",(Serializable)hmOrders);
        intent.setClass(FukuanActivity.this,PayWebviewActivity.class);
        startActivityForResult(intent,payTypeFlag);
      }

      @Override
      public void onFailure(retrofit2.Call<BaseBean> call, Throwable t) {
        showTheResult("-------------------前往支付页面失败，重新选择支付方式\n");
        mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
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
    if(requestCode == PAY_TYPE_TONGLIAN_ZHIFU || requestCode == PAY_TYPE_KUAIQIAN_ZHIFU_1 || requestCode == PAY_TYPE_KUAIQIAN_ZHIFU_2 || requestCode == PAY_TYPE_KUAIJIE_ZHIFU){
        showContinueDialog();
    }
  }

  private void showContinueDialog(){
    AlertDialog dialog = new AlertDialog.Builder(FukuanActivity.this)
            .setTitle("下一步")
            .setMessage("请选择下一步操作(如果付款成功，请选择继续付款，如果付款失败请选择重新付款)")
            .setNegativeButton("重新付款", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Log.e("test","重新付款,重新开始付款此帐号");
                showTheResult("----------------重新付款此小号\n");
                mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
              }
            })
            .setPositiveButton("继续付款", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                showTheResult("----------------继续付款,开始付款下一个帐号\n\n\n");
                accountIndex++;
                mHandler.sendEmptyMessageDelayed(START_TO_FU_KUAN,getDelayTime());
              }
            }).create();
    dialog.show();
  }

  private void showChooseFukuanMode(){
    //显示popupwindow
    if(!isFinishing()){
      popwindow = new ChoosePayOptionsPopwindow(this,fukuanChoice,this);
      if(!popwindow.isShowing()){
        Log.e("test","mRootView.height = " + mRootView.getHeight());
        if(mRootView != null && mRootView.getHeight() > 0) {
          mRootView.post(new Runnable() {
            @Override
            public void run() {
              if(mRootView.getHeight() > 0) {
                popwindow.showAtLocation(mRootView, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
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
            }
          });
        }
      }
    }
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
    if(coinsCost > 0) {
      userInfo.setCoinsNumber(userInfo.getCoinsNumber() - coinsCost*((hmOrders.size() -1)/20 + 1));
      userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
        @Override
        public void done(BmobException e) {
          if(e == null){
            Log.e("test","updateTheCoinsNumber 更新用户积分成功");
          }
        }
      });
    }
  }

  private void showTheResult(String str){
    tvShowResult.append(str);
    offset = tvShowResult.getLineCount()* tvShowResult.getLineHeight() + 5;
    if(offset > tvShowResult.getHeight()){
      tvShowResult.scrollTo(0,offset- tvShowResult.getHeight());
    }
  }

  private void showChooseOlderDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_show_all_orders_layout,null);

    ListView listView = (ListView) diaog_view.findViewById(R.id.list_view);
    orderListAdapter = new OrderListAdapter(this,hmOrders,this);
    listView.setAdapter(orderListAdapter);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("订单选择");
    builder.setView(diaog_view);
    builder.setCancelable(false);
    builder.setPositiveButton("继续付款", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        mHandler.sendEmptyMessageDelayed(START_TO_GET_ALL_PAYMENT,getDelayTime());
      }
    });
    AlertDialog dialog = builder.create();
    if(!dialog.isShowing()) {
      if(!isFinishing()){
        dialog.show();
      }else {
        return;
      }
    }
  }

  @Override
  public void click(View v) {
    if(orderListAdapter != null && orderListAdapter.getSelected().containsKey((Integer) v.getTag())) {
      hmOrders.get((Integer) v.getTag()).setSelected(true);
    }else {
      hmOrders.get((Integer) v.getTag()).setSelected(false);
    }
  }

  private String getAccessToken() {
    /**
     * 此处只是处理了accessToken被内存回收的情况，其实所有的成员变量都可能被回收
     */
    if(accessToken == null) {
      return PreferenceHelper.getInstance().getAccessToken();
    }else {
      return accessToken;
    }
  }
}
