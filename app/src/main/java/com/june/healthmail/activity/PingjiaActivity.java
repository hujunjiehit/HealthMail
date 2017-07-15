package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.audiofx.LoudnessEnhancer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.june.healthmail.R;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created by june on 2017/3/4.
 */

public class PingjiaActivity extends BaseActivity implements View.OnClickListener{

    private Button btn_edit_words;
    private TextView tv_show_words;

    private Button btn_start;
    private TextView tvShowResult;
    private TextView tvRemainTimes;
    private UserInfo userInfo;
    private ArrayList<AccountInfo> accountList = new ArrayList<>();

    private Boolean isRunning = false;

    private int offset;

    private static final int START_TO_PING_JIA = 1;
    private static final int GET_TOKEN_SUCCESS = 2;
    private static final int START_TO_GET_ORDER_LIST = 3;
    private static final int GET_ORDER_LIST_SUCCESS = 4;
    private static final int START_TO_PING_JIA_ONE_COURSE = 5;
    private static final int PING_JIA_ONE_COURSE_SUCCESS = 6;
    private static final int PING_JIA_ONE_COURSE_FAILED = 7;
    private static final int GET_ORDER_LIST_FAILED = 8;
    private static final int GET_TOKEN_FAILED = 9;
    private static final int USER_PWD_WRONG = 10;
    private static final int REQUEST_INVAILED = 11;


    private int accountIndex = 0;
    private int pageIndex = 0;
    private int courseIndex = 0;
    private String accessToken;

    private Message message;

    private ArrayList<Order> coureseList = new ArrayList<>();

    private static final int DEELAY_TIME = 1000;

    private int min_time;
    private int max_time;
    private String errmsg;

    private Gson gson = new Gson(); //java.lang.IllegalStateException
    private OrdersModel ordersModel;
    private PingjiaModel pingjiaModel;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START_TO_PING_JIA:
                    if(isRunning) {
                        if (accountIndex < accountList.size()) {
                            if (PreferenceHelper.getInstance().getRemainPingjiaTimes() <= 0) {
                                showTheResult("剩余评价次数不足，请先充值");
                                toast("剩余评价次数不足，请先充值");
                                isRunning = false;
                                btn_start.setText("评价完成");
                                return;
                            }
                            showTheResult("开始评价第" + (accountIndex + 1) + "个号：" + accountList.get(accountIndex).getPhoneNumber() + "\n");
                            if (accountList.get(accountIndex).getStatus() == 1) {
                                getAccountToken();
                            } else {
                                showTheResult("******当前小号未启用，跳过，继续下一个小号\n\n\n");
                                accountIndex++;
                                message = this.obtainMessage(START_TO_PING_JIA);
                                message.sendToTarget();
                            }
                        } else {
                            showTheResult("******所有账号评价结束**********\n");
                            isRunning = false;
                            btn_start.setText("评价完成");
                        }
                    } else {
                        showTheResult("**用户自己终止评价**当前已经执行完成"+ accountIndex + "个小号\n");
                    }

                    break;
                case GET_TOKEN_SUCCESS:
                    showTheResult("--获取token成功\n");
                    pageIndex = 0;
                    message = this.obtainMessage(START_TO_GET_ORDER_LIST);
                    message.sendToTarget();
                    break;

                case START_TO_GET_ORDER_LIST:
                    if (pageIndex < 5) {
                        showTheResult("----开始获取第"+ (pageIndex + 1) + "页订单列表:");
                        getOrderList();
                    } else {
                        accountIndex++;
                        showTheResult("此账号评价结束************************\n\n\n");
                        message = this.obtainMessage(START_TO_PING_JIA);
                        message.sendToTarget();
                    }
                    break;

                case GET_ORDER_LIST_SUCCESS:
                    showTheResult("订单列表获取成功\n");
                    //保存可以评价的课程列表
                    courseIndex = 0;
                    coureseList.clear();
                    if(ordersModel.getValuse() != null){
                      boolean needPingjia = false;
                      for(int i = 0; i < ordersModel.getValuse().size(); i++){
                        coureseList.add(ordersModel.getValuse().get(i));
                        if(ordersModel.getValuse().get(i).getHm_go_orderstatus() == 9){
                          needPingjia = true;
                        }
                      }
                      if(needPingjia){
                        //有待评价的课程
                        message = this.obtainMessage(START_TO_PING_JIA_ONE_COURSE);
                        message.sendToTarget();
                      }else {
                        showTheResult("------当前页无可评价订单\n");
                        if(coureseList.size() < 20){
                          showTheResult("******第" + (pageIndex +1) + "页订单小于20，继续评价下一个小号\n");
                          showTheResult("此账号评价结束************************\n\n\n");
                          accountIndex++;
                          message = this.obtainMessage(START_TO_PING_JIA);
                          message.sendToTarget();
                        }else {
                          pageIndex++;
                          message = this.obtainMessage(START_TO_GET_ORDER_LIST);
                          message.sendToTarget();
                        }
                      }
                    }else {
                      showTheResult("--------订单列表为空，重新获取\n");
                      this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
                    }
                    break;

                case START_TO_PING_JIA_ONE_COURSE:
                    if(courseIndex < coureseList.size()){

                        showTheResult("-----------开始评价第"+ (courseIndex + 1) + "节课程[" +
                                coureseList.get(courseIndex).getHm_go_orderstatus() + "]:");

                        if(coureseList.get(courseIndex).getHm_go_orderstatus() != 9){
                            showTheResult("无需评价\n");
                            courseIndex++;
                            message = this.obtainMessage(START_TO_PING_JIA_ONE_COURSE);
                            message.sendToTarget();
                        }else{
                            pingjiaTheCourse(coureseList.get(courseIndex).getGrouporder_id());
                        }
                    } else {
                        //需要判断是继续获取订单还是开始下一个小号
                        if(coureseList.size() < 20){
                            showTheResult("******第" + (pageIndex +1) + "页订单小于20，继续评价下一个小号\n");
                            showTheResult("此账号评价结束************************\n\n\n");
                            accountIndex++;
                            updateUserInfo();
                            message = this.obtainMessage(START_TO_PING_JIA);
                            message.sendToTarget();
                        }else {
                            pageIndex++;
                            message = this.obtainMessage(START_TO_GET_ORDER_LIST);
                            message.sendToTarget();
                        }
                    }
                    break;

                case PING_JIA_ONE_COURSE_SUCCESS:
                    showTheResult("评价成功\n");
                    courseIndex++;
                    CommonUntils.minusPingjiaTimes();
                    tvRemainTimes.setText(PreferenceHelper.getInstance().getRemainPingjiaTimes() + "");
                    this.sendEmptyMessageDelayed(START_TO_PING_JIA_ONE_COURSE,getDelayTime());
                    break;

                case PING_JIA_ONE_COURSE_FAILED:
                    showTheResult("评价失败，继续尝试 错误信息:"+errmsg+"\n");
                    this.sendEmptyMessageDelayed(START_TO_PING_JIA_ONE_COURSE,getDelayTime());
                    break;
                case GET_ORDER_LIST_FAILED:
                    showTheResult("订单列表获取失败，继续尝试\n");
                    this.sendEmptyMessageDelayed(START_TO_GET_ORDER_LIST,getDelayTime());
                    break;

                case GET_TOKEN_FAILED:
                    showTheResult("--获取token失败，重新开始该小号\n");
                    this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
                    break;
                case USER_PWD_WRONG:
                    showTheResult("***错误信息："+ errmsg + "\n");
                    showTheResult("***忽略错误的小号，继续下一个****************\n\n\n");
                    accountIndex++;
                    this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
                    break;
                case REQUEST_INVAILED:
                    showTheResult("***错误信息："+ errmsg + "\n");
                    showTheResult("***请求失效，小号管理标记为绿色，继续下一个****************\n\n\n");
                    accountIndex++;
                    this.sendEmptyMessageDelayed(START_TO_PING_JIA,getDelayTime());
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
        userInfo = BmobUser.getCurrentUser(UserInfo.class);
        if(getIntent() != null){
            if(getIntent().getBooleanExtra("exception",false)){
                PreferenceHelper.getInstance().setRemainPingjiaTimes(3000);
            }
        }
        initView();
        setListener();
        initData();
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

        min_time = PreferenceHelper.getInstance().getMinPingjiaTime();
        max_time = PreferenceHelper.getInstance().getMaxPingjiaTime();
    }

    private int getDelayTime() {
        int randTime = CommonUntils.getRandomInt(min_time,max_time);
        Log.d("test","randTime = " + randTime);
        return randTime;
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
                        btn_start.setText("停止评价");
                        startToPingjia();
                    } else {
                        isRunning = false;
                        mHandler.removeCallbacksAndMessages(null);
                        btn_start.setText("开始评价");
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

    private void startToPingjia() {

        Message msg = mHandler.obtainMessage(START_TO_PING_JIA);
        msg.sendToTarget();
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
                        if(errmsg.contains("密码")){
                            DBManager.getInstance(PingjiaActivity.this).setPwdInvailed(accountList.get(accountIndex).getPhoneNumber());
                            mHandler.sendEmptyMessageDelayed(USER_PWD_WRONG,getDelayTime());
                        }else {
                            //请求失效
                            DBManager.getInstance(PingjiaActivity.this).setRequestInvailed(accountList.get(accountIndex).getPhoneNumber());
                            mHandler.sendEmptyMessageDelayed(REQUEST_INVAILED,getDelayTime());
                        }
                    } else {
                        //更新小号昵称
                        DBManager.getInstance(PingjiaActivity.this).updateNickName(accountList.get(accountIndex).getPhoneNumber(),
                                tokenmodel.getData().getHmMemberUserVo().getNickName());
                        accessToken = tokenmodel.getData().getAccessToken();
                        mHandler.sendEmptyMessageDelayed(GET_TOKEN_SUCCESS,getDelayTime());
                    }
                }catch (Exception e){
                    mHandler.sendEmptyMessageDelayed(GET_TOKEN_FAILED,getDelayTime());
                }
            }
        });
    }


    private void getOrderList() {

        String url = "http://api.healthmall.cn/Post";
        JsonObject job = new JsonObject();
        job.addProperty("count","20");
        job.addProperty("whichFunc","Getorderlist");
        job.addProperty("type","ALL");
        job.addProperty("page",pageIndex+1);
        job.addProperty("Which","user");

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
                try{
                    ordersModel = gson.fromJson(response.body().charStream(), OrdersModel.class);
                    response.body().close();
                    //Log.e("test","userName = " + ordersModel.getAccessToken().getUserName());
                    if(ordersModel.isSucceed()){
                        //获取成功之后
                        Message msg = mHandler.obtainMessage(GET_ORDER_LIST_SUCCESS);
                        msg.sendToTarget();
                    }else {
                        mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
                    }
                }catch (Exception e){
                    Log.e("test","Exception:" + e.toString());
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(GET_ORDER_LIST_FAILED,getDelayTime());
                }
            }
        });
    }

    private void pingjiaTheCourse(String hm_orderid) {

        String url = "http://api.healthmall.cn/Post";

       // {
        // "model":
        //      {
        //         "hm_orderid":"go170228213618603667",
        //         "hm_ptc_content":"非常感谢教练的指导",
        //         "hm_ptc_score":5
        //      },
        //  "type":"GBC",
        //  "whichFunc":"IN_PTCOMMENT"
        // }

        JsonObject modelJob  = new JsonObject();
        modelJob.addProperty("hm_orderid",hm_orderid);
        modelJob.addProperty("hm_ptc_content",tv_show_words.getText().toString().trim());
        modelJob.addProperty("hm_ptc_score",5);

        JsonObject job = new JsonObject();
        job.add("model",modelJob);
        job.addProperty("type","GBC");
        job.addProperty("whichFunc","IN_PTCOMMENT");

        FormBody body = new FormBody.Builder()
                .add("accessToken",accessToken)
                .add("data",job.toString())
                .build();
        HttpUntils.getInstance(this).postForm(url, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    pingjiaModel = gson.fromJson(response.body().charStream(), PingjiaModel.class);
                    response.body().close();
                    Log.e("test","succeed = " + pingjiaModel.isSucceed());
                    if(pingjiaModel.isSucceed()){
                        mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_SUCCESS,getDelayTime());
                    } else {
                        errmsg = pingjiaModel.getErrmsg();
                        mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
                    }
                }catch (Exception e){
                    mHandler.sendEmptyMessageDelayed(PING_JIA_ONE_COURSE_FAILED,getDelayTime());
                }
            }
        });
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
                    dialog.dismiss();
                }else {
                    Toast.makeText(PingjiaActivity.this,"评价语需要大于五个字，请重新输入",Toast.LENGTH_LONG).show();
                }
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        updateUserInfo();
    }

    public void updateUserInfo(){
        userInfo.setYuekeTimes(PreferenceHelper.getInstance().getRemainYuekeTimes());
        userInfo.setPingjiaTimes(PreferenceHelper.getInstance().getRemainPingjiaTimes());
        userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    Log.e("test","更新用户信息成功");
                }else{
                    Log.e("test","更新用户信息失败");
                }
            }
        });
    }
}
