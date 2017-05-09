package com.june.healthmail.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.june.healthmail.R;
import com.june.healthmail.model.MessageDetails;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by bjhujunjie on 2017/3/14.
 */

public class SuperRootActivity extends Activity implements View.OnClickListener{

    private EditText etInputPhonenumber;

    private Button btnGetUserInfo;
    private Button btnAuthorizeByDays;
    private Button btnAuthorizeOneDay;
    private Button btnAuthorizeForever;
    private Button btnGiveTheCoins;
    private Button btnUpgradeUserLevel;
    private Button btnUpdatePaystatus;
    private Button btnAddTheCoins;

    private TextView tvUserName;
    private TextView tvUserType;
    private TextView tvAllowDays;
    private TextView tvCoinsNumber;

    private static final int UPDATE_USER_INFO = 1;

    private ShowProgress showProgress;
    private UserInfo mUserInfo;
    private UserInfo currentUser;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE_USER_INFO:
                    updateUserInfo();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = BmobUser.getCurrentUser(UserInfo.class);
        if(currentUser.getUserType() != 99 && currentUser.getUserType() != 100) {
            Toast.makeText(this,"当前用户非管理员，无法进入管理页面",Toast.LENGTH_SHORT).show();
            finish();
        }
        setContentView(R.layout.activity_super_root);
        initView(); 
        setListener();
    }

    private void initView() {
        showProgress = new ShowProgress(SuperRootActivity.this);
        etInputPhonenumber = (EditText) findViewById(R.id.et_input_phone_number);
        btnGetUserInfo = (Button) findViewById(R.id.btn_get_user_info);
        btnAuthorizeByDays = (Button) findViewById(R.id.btn_authorize_by_days);
        btnAuthorizeOneDay = (Button) findViewById(R.id.btn_authorize_one_day);
        btnAuthorizeForever = (Button) findViewById(R.id.btn_authorize_forever);
        btnGiveTheCoins = (Button) findViewById(R.id.btn_give_the_coins);
        btnUpgradeUserLevel = (Button) findViewById(R.id.btn_upgrade_user_level);
        btnUpdatePaystatus = (Button) findViewById(R.id.btn_update_pay_status);
        btnAddTheCoins = (Button) findViewById(R.id.btn_add_coins);
        tvUserName = (TextView) findViewById(R.id.tv_user_name);
        tvUserType = (TextView) findViewById(R.id.tv_user_type);
        tvAllowDays = (TextView) findViewById(R.id.tv_allow_days);
        tvCoinsNumber = (TextView) findViewById(R.id.tv_coins_number);
        if(currentUser.getUserType() == 99){
            btnAuthorizeForever.setVisibility(View.GONE);
            btnAuthorizeByDays.setVisibility(View.GONE);
            btnAddTheCoins.setVisibility(View.GONE);
            btnUpdatePaystatus.setVisibility(View.GONE);
        }
    }

    private void setListener() {
        btnGetUserInfo.setOnClickListener(this);
        btnAuthorizeByDays.setOnClickListener(this);
        btnAuthorizeOneDay.setOnClickListener(this);
        btnAuthorizeForever.setOnClickListener(this);
        btnGiveTheCoins.setOnClickListener(this);
        btnUpgradeUserLevel.setOnClickListener(this);
        btnUpdatePaystatus.setOnClickListener(this);
        btnAddTheCoins.setOnClickListener(this);
        findViewById(R.id.img_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back:    //返回
                finish();
                break;
            case R.id.btn_get_user_info:
                String userName = etInputPhonenumber.getText().toString().trim();
                getUserInfo(userName);
                break;
            case R.id.btn_authorize_by_days:
                if(mUserInfo != null){
                    authorizeUserByDays();
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_authorize_one_day:
                if(mUserInfo != null){
                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提醒")
                        .setMessage("是否为该用户开通试用授权？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                authorizeUserOneDay();
                                dialog.dismiss();
                            }
                        })
                        .create();
                    dialog.show();
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_authorize_forever:
                if(mUserInfo != null){
                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提醒")
                        .setMessage("是否为该用户开通永久授权？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                authorizeUserForever();
                                dialog.dismiss();
                            }
                        })
                        .create();
                    dialog.show();
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_give_the_coins:
                if(mUserInfo != null){
                    giveTheConisToUser(true);
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_add_coins:
                if(mUserInfo != null){
                    giveTheConisToUser(false);
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_upgrade_user_level:
                //升级高级永久
                if(mUserInfo != null){
                    if(mUserInfo.getUserType() != 2) {
                        toast("只有永久用户才能升级高级永久");
                        return;
                    }

                    //管理员用户不让升级高级永久，超级管理员才行
                    if (currentUser.getUserType() == 99) {
                        return;
                    }

                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提醒")
                        .setMessage("是否为该用户升级高级永久？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                upgradeUserLevel();
                                dialog.dismiss();
                            }
                        })
                        .create();
                    dialog.show();
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            case R.id.btn_update_pay_status:
                if(mUserInfo != null){
                    if(mUserInfo.getUserType() != 2) {
                        toast("只有永久用户才能开通付款永久");
                        return;
                    }

                    //管理员用户不让开通付款永久，超级管理员才行
                    if (currentUser.getUserType() == 99) {
                        return;
                    }

                    AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("提醒")
                        .setMessage("是否为该用户开通付款永久？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updatePaystatus();
                                dialog.dismiss();
                            }
                        })
                        .create();
                    dialog.show();
                }else {
                    toast("请先输入用户帐号，并获取用户信息！");
                }
                break;
            default:
                break;
        }
    }

    private void updatePaystatus() {
        //开通付款永久
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setUserName(mUserInfo.getUsername());
        messageDetails.setStatus(1);
        messageDetails.setScore(0);
        messageDetails.setType(8);
        messageDetails.setReasons("用户开通付款永久");
        messageDetails.setRelatedUserName("");
        messageDetails.setNotice("操作人员：" + currentUser.getUsername());
        messageDetails.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.d("test","用户开通付款永久成功：" + s);
                    toast("用户开通付款永久成功");
                }else{
                    Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                    toast("用户开通付款永久失败:" + e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void upgradeUserLevel() {
        //升级高级永久
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setUserName(mUserInfo.getUsername());
        messageDetails.setStatus(1);
        messageDetails.setScore(0);
        messageDetails.setType(7);
        messageDetails.setReasons("用户升级高级永久");
        messageDetails.setRelatedUserName("");
        messageDetails.setNotice("操作人员：" + currentUser.getUsername());
        messageDetails.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.d("test","用户升级高级永久成功：" + s);
                    toast("用户升级高级永久成功");
                }else{
                    Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                    toast("用户升级高级永久失败:" + e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void authorizeUserOneDay() {
        if(mUserInfo.getUserType() == 1 || mUserInfo.getUserType() == 2) {
            toast("该用户已有授权，无需开通试用权限");
        } else {
            //开通试用授权
            MessageDetails messageDetails = new MessageDetails();
            messageDetails.setUserName(mUserInfo.getUsername());
            messageDetails.setStatus(1);
            messageDetails.setScore(1);
            messageDetails.setType(5);
            messageDetails.setReasons("用户开通试用授权");
            messageDetails.setRelatedUserName("");
            messageDetails.setNotice("操作人员：" + currentUser.getUsername());
            messageDetails.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.d("test","用户开通月卡成功：" + s);
                        toast("试用授权开通成功，请让对方重新登录");
                        mUserInfo.setUserType(1);
                    }else{
                        Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                        toast("试用授权开通失败:" + e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    private void giveTheConisToUser(boolean isGive) {
        showInputCoinsDialog(isGive);
    }



    private void authorizeUserByDays() {
        showInputDaysDialog();
    }

    private void authorizeUserForever() {
        //永久授权该用户
        MessageDetails messageDetails = new MessageDetails();
        messageDetails.setUserName(mUserInfo.getUsername());
        messageDetails.setStatus(1);
        messageDetails.setScore(0);
        messageDetails.setType(6);
        messageDetails.setReasons("用户开通永久授权");
        messageDetails.setRelatedUserName("");
        messageDetails.setNotice("操作人员：" + currentUser.getUsername());
        messageDetails.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null){
                    Log.d("test","用户开通永久授权成功：" + s);
                    toast("用户开通永久授权成功");
                }else{
                    Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                    toast("用户开通永久授权失败:" + e.getMessage()+","+e.getErrorCode());
                }
            }
        });
        if(!TextUtils.isEmpty(mUserInfo.getInvitePeoplePhone())){
            Log.d("test","有邀请人，邀请人电话：" + mUserInfo.getInvitePeoplePhone());
            MessageDetails inviteMessageDetails = new MessageDetails();
            inviteMessageDetails.setUserName(mUserInfo.getInvitePeoplePhone());
            inviteMessageDetails.setStatus(1);
            inviteMessageDetails.setScore(1888);
            inviteMessageDetails.setType(3);
            inviteMessageDetails.setReasons("邀请人升级永久授权，赠送金币1888");
            inviteMessageDetails.setRelatedUserName(mUserInfo.getUsername());
            inviteMessageDetails.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null){
                        Log.d("test","邀请人升级永久授权，赠送金币1888成功" + s);
                    }else{
                        Log.e("test","邀请人升级永久授权赠送金币失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }else {
            Log.d("test","没有邀请人");
        }

    }

    private void updateUserInfo() {
        if(mUserInfo != null){
            tvUserName.setText(mUserInfo.getUsername());
            tvUserType.setText(getUserTypeDsec(mUserInfo.getUserType()));
            tvAllowDays.setText(mUserInfo.getAllowDays()+"");
            tvCoinsNumber.setText(mUserInfo.getCoinsNumber()+"");
        }
    }

    private void showInputDaysDialog() {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("授权天数：");
        edit_text.setHint("请输入授权天数");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("授权天数设置");
        builder.setView(diaog_view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String daysNum = edit_text.getText().toString().trim();
                dialog.dismiss();
                Log.d("test","daysNum = " + daysNum);

                MessageDetails messageDetails = new MessageDetails();
                messageDetails.setUserName(mUserInfo.getUsername());
                messageDetails.setStatus(1);
                messageDetails.setScore(Integer.parseInt(daysNum));
                messageDetails.setType(5);
                messageDetails.setReasons("用户开通月卡授权，授权天数见score字段");
                messageDetails.setNotice("操作人员：" + currentUser.getUsername());
                messageDetails.setRelatedUserName("");
                messageDetails.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.d("test","用户开通月卡成功：" + s);
                            toast("按天授权成功，授权天数：" + Integer.parseInt(daysNum));
                        }else{
                            Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                            toast("按天授权失败:" + e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
                if(!TextUtils.isEmpty(mUserInfo.getInvitePeoplePhone()) && Integer.parseInt(daysNum) >= 30){
                    Log.d("test","有邀请人，邀请人电话：" + mUserInfo.getInvitePeoplePhone());
                    MessageDetails inviteMessageDetails = new MessageDetails();
                    inviteMessageDetails.setUserName(mUserInfo.getInvitePeoplePhone());
                    inviteMessageDetails.setStatus(1);
                    inviteMessageDetails.setScore(588);
                    inviteMessageDetails.setType(2);
                    inviteMessageDetails.setReasons("邀请人开通月卡授权，赠送金币588");
                    inviteMessageDetails.setRelatedUserName(mUserInfo.getUsername());
                    inviteMessageDetails.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                Log.d("test","邀请人开通月卡赠送金币588成功" + s);
                            }else{
                                Log.e("test","邀请人开通月卡赠送金币失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }
                    });
                }else {
                    Log.d("test","没有邀请人");
                }
            }
        });
        builder.create().show();
    }

    private void showInputCoinsDialog(final boolean isGive) {
        View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_pingjia_word_layout,null);
        final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
        final TextView tv_text = (TextView) diaog_view.findViewById(R.id.tv_desc);
        tv_text.setText("金币数：");
        edit_text.setHint("请输入赠送的金币数量");
        edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("金币赠送");
        builder.setView(diaog_view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String coinsNum = edit_text.getText().toString().trim();
                dialog.dismiss();
                Log.d("test","coinsNum = " + coinsNum);
                MessageDetails messageDetails = new MessageDetails();
                messageDetails.setUserName(mUserInfo.getUsername());
                messageDetails.setStatus(1);
                messageDetails.setScore(Integer.parseInt(coinsNum));
                messageDetails.setType(4); //4代表金币充值或者管理员赠送
                if(isGive) {
                    //赠送金币
                    messageDetails.setReasons("管理员赠送金币，数量：" + Integer.parseInt(coinsNum));
                }else {
                    //充值金币
                    messageDetails.setReasons("充值金币成功，数量：" + Integer.parseInt(coinsNum));
                }
                messageDetails.setNotice("操作人员：" + currentUser.getUsername());
                messageDetails.setRelatedUserName("");
                messageDetails.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            Log.d("test","管理员赠送金币成功：" + s);
                            if(isGive) {
                                toast("管理员赠送金币成功，赠送金币数量：" + Integer.parseInt(coinsNum));
                            }else {
                                toast("金币充值成功，充值金币数量：" + Integer.parseInt(coinsNum));
                            }
                        }else{
                            Log.e("test","失败："+e.getMessage()+","+e.getErrorCode());
                            toast("管理员赠送金币失败:" + e.getMessage()+","+e.getErrorCode());
                        }
                    }
                });
            }
        });
        builder.create().show();
    }

    private String getUserTypeDsec(Integer userType) {
        String typeDesc = "";
        if(userType == 0){
            typeDesc = "普通用户";
        }else if(userType == 1){
            typeDesc = "月卡用户";
        }else if(userType == 2) {
            typeDesc = "永久用户";
        }else if(userType == 3) {
            typeDesc = "高级永久用户";
        }else if(userType == 99) {
            typeDesc = "管理员用户";
        }else if(userType == 100) {
            typeDesc = "超级管理员用户";
        }else {
            typeDesc = "过期用户";
        }
        return typeDesc;
    }

    private void toast(String str){
        Toast.makeText(SuperRootActivity.this,str,Toast.LENGTH_LONG).show();
    }
    private void getUserInfo(String userName) {
        if(TextUtils.isEmpty(userName)){
            toast("手机号不能为空");
           return;
        }
        if(userName.length() != 11){
            toast("请输入正确的手机号码(11位数字)");
            return;
        }
        if(showProgress != null){
            showProgress.setMessage("正在获取用户信息...");
            showProgress.setCanceledOnTouchOutside(false);
            showProgress.show();
        }
        BmobQuery<UserInfo> query = new BmobQuery<UserInfo>();
        query.addWhereEqualTo("username",userName);
        query.findObjects(new FindListener<UserInfo>() {
            @Override
            public void done(final List<UserInfo> object, BmobException e) {
                if (showProgress != null && showProgress.isShowing()) {
                    showProgress.dismiss();
                }
                if (e == null) {
                    if (object.size() == 0) {
                        toast("要查找的用户不存在，请确认之后再输入");
                    } else {
                        Log.d("test", "查询成功，用户个人信息：" + object.get(0).toString());
                        mUserInfo = object.get(0);
                        mHandler.sendEmptyMessage(UPDATE_USER_INFO);
                    }
                }
            }
        });
    }
}
