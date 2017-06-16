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
import com.june.healthmail.model.ProxyInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.untils.Tools;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/6/15.
 */

public class ProxyPersonActivity extends Activity implements View.OnClickListener {

  private ShowProgress showProgress;
  private UserInfo mUserInfo;
  private UserInfo currentUser;
  private ProxyInfo mProxyInfo;

  private Button btnAuthorizeOneDay;
  private Button btnAuthorizeForever;
  private Button btnUpgradeUserLevel;

  private TextView tvLeftTimes1;
  private TextView tvLeftTimes2;

  private EditText etInputPhonenumber;
  private Button btnGetUserInfo;
  private TextView tvUserName;
  private TextView tvUserType;
  private TextView tvAllowDays;
  private TextView tvCoinsNumber;

  private TextView tvShowQQGroup;
  private TextView tvEditQQGroup;

  private static final int UPDATE_PROXY_INFO = 1;
  private static final int UPDATE_USER_INFO = 2;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what){
        case UPDATE_PROXY_INFO:
          if(mProxyInfo != null){
            tvLeftTimes1.setText(mProxyInfo.getLeftTimes1() + "");
            tvLeftTimes2.setText(mProxyInfo.getLeftTimes2() + "");
            String[] array = mProxyInfo.getDesc().split("::");
            tvShowQQGroup.setText(array[1] + "");
          }
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
    if(currentUser.getUserType() < 98) {
      Toast.makeText(this,"当前用户非管理员或者代理人，无法进入代理人页面",Toast.LENGTH_SHORT).show();
      finish();
    }
    setContentView(R.layout.activity_proxy_preson);
    initView();
    setListener();
    getProxyInfo(currentUser.getUsername());
  }




  private void initView() {
    showProgress = new ShowProgress(ProxyPersonActivity.this);
    etInputPhonenumber = (EditText) findViewById(R.id.et_input_phone_number);
    btnGetUserInfo = (Button) findViewById(R.id.btn_get_user_info);
    btnAuthorizeOneDay = (Button) findViewById(R.id.btn_authorize_one_day);
    btnAuthorizeForever = (Button) findViewById(R.id.btn_authorize_forever);
    btnUpgradeUserLevel = (Button) findViewById(R.id.btn_upgrade_user_level);
    tvUserName = (TextView) findViewById(R.id.tv_user_name);
    tvUserType = (TextView) findViewById(R.id.tv_user_type);
    tvAllowDays = (TextView) findViewById(R.id.tv_allow_days);
    tvCoinsNumber = (TextView) findViewById(R.id.tv_coins_number);
    tvLeftTimes1 = (TextView) findViewById(R.id.tv_left_times_1);
    tvLeftTimes2 = (TextView) findViewById(R.id.tv_left_times_2);
    tvShowQQGroup = (TextView) findViewById(R.id.tv_show_qq_group);
    tvEditQQGroup = (TextView) findViewById(R.id.tv_edit_qq_group);
    //tvCoinsNumber.setText(currentUser.get);
  }

  private void setListener() {
    btnGetUserInfo.setOnClickListener(this);
    btnAuthorizeOneDay.setOnClickListener(this);
    btnAuthorizeForever.setOnClickListener(this);
    btnUpgradeUserLevel.setOnClickListener(this);
    findViewById(R.id.img_back).setOnClickListener(this);
    tvEditQQGroup.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_back:    //返回
        finish();
        break;
      case R.id.tv_edit_qq_group:    //返回
        showEditQQDialog();
        break;
      case R.id.btn_get_user_info:
        String userName = etInputPhonenumber.getText().toString().trim();
        getUserInfo(userName);
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
          if(mUserInfo.getUserType() >= 2) {
            toast("该用户已经是永久用户或者高级永久用户");
            return;
          }

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
                  dialog.dismiss();
                  if(mProxyInfo.getLeftTimes1() <= 0){
                    toast("剩余次数不足，请充值");
                  }else {
                    mProxyInfo.setLeftTimes1(mProxyInfo.getLeftTimes1()-1);
                    mProxyInfo.update(new UpdateListener() {
                      @Override
                      public void done(BmobException e) {
                        if(e == null){
                          authorizeUserForever();
                          mHandler.sendEmptyMessage(UPDATE_PROXY_INFO);
                        }else {
                          toast("异常，错误信息：" + e.getMessage());
                        }
                      }
                    });
                  }
                }
              })
              .create();
          dialog.show();
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
                  dialog.dismiss();
                  if(mProxyInfo.getLeftTimes2() <= 0){
                    toast("剩余次数不足，请充值");
                  }else {
                    mProxyInfo.setLeftTimes2(mProxyInfo.getLeftTimes2()-1);
                    mProxyInfo.update(new UpdateListener() {
                      @Override
                      public void done(BmobException e) {
                        if(e == null){
                          upgradeUserLevel();
                          mHandler.sendEmptyMessage(UPDATE_PROXY_INFO);
                        }else {
                          toast("异常，错误信息：" + e.getMessage());
                        }
                      }
                    });
                  }

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

  private void toast(String msg){
    Toast.makeText(ProxyPersonActivity.this,msg, Toast.LENGTH_LONG).show();
  }

  private void upgradeUserLevel() {
    //升级高级永久
    MessageDetails messageDetails = new MessageDetails();
    messageDetails.setUserName(mUserInfo.getUsername());
    messageDetails.setStatus(1);
    messageDetails.setScore(0);
    messageDetails.setType(7);
    messageDetails.setReasons("代理人升级高级永久::"+mProxyInfo.getUserName());
    messageDetails.setRelatedUserName("");
    messageDetails.setNotice("操作人员：" + mProxyInfo.getUserName());
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

    //赠送升级人1888金币
    MessageDetails updateMessageDetails = new MessageDetails();
    updateMessageDetails.setUserName(mUserInfo.getUsername());
    updateMessageDetails.setStatus(1);
    updateMessageDetails.setScore(1888);
    updateMessageDetails.setType(3);
    updateMessageDetails.setReasons("升级高级永久，赠送金币1888");
    updateMessageDetails.setRelatedUserName(mUserInfo.getUsername());
    updateMessageDetails.save(new SaveListener<String>() {
      @Override
      public void done(String s, BmobException e) {
        if(e==null){
          Log.d("test","升级高级永久，赠送金币1888成功" + s);
          toast("用户升级高级赠送1888金币成功");
        }else{
          Log.e("test","升级高级永久赠送金币失败："+e.getMessage()+","+e.getErrorCode());
          toast("用户升级高级赠送金币失败");
        }
      }
    });
  }

  private void authorizeUserForever() {
    //永久授权该用户
    MessageDetails messageDetails = new MessageDetails();
    messageDetails.setUserName(mUserInfo.getUsername());
    messageDetails.setStatus(1);
    messageDetails.setScore(0);
    messageDetails.setType(6);
    messageDetails.setReasons("代理人开通永久::"+mProxyInfo.getUserName());
    messageDetails.setRelatedUserName("");
    messageDetails.setNotice("操作人员：" + mProxyInfo.getUserName());
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

  private void authorizeUserOneDay() {
    if(mUserInfo.getUserType() == 1 || mUserInfo.getUserType() == 2) {
      toast("该用户已有授权，无需开通试用权限");
    } else {
      if(mUserInfo.getAllowDays() > 0) {
        toast("一个用户最多开通一次试用");
        return;
      }
      //开通试用授权
      MessageDetails messageDetails = new MessageDetails();
      messageDetails.setUserName(mUserInfo.getUsername());
      messageDetails.setStatus(1);
      messageDetails.setScore(1);
      messageDetails.setType(5);
      messageDetails.setReasons("代理人开通试用::"+mProxyInfo.getUserName());
      messageDetails.setRelatedUserName("");
      messageDetails.setNotice("操作人员：" + mProxyInfo.getUserName());
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
  private void updateUserInfo() {
    if(mUserInfo != null){
      tvUserName.setText(mUserInfo.getUsername());
      tvUserType.setText(Tools.getUserTypeDsec(mUserInfo.getUserType()));
      tvAllowDays.setText(mUserInfo.getAllowDays()+"");
      if(mUserInfo.getPayStatus() != null && mUserInfo.getPayStatus() == 1) {
        tvCoinsNumber.setText(mUserInfo.getCoinsNumber()+"");
      } else {
        tvCoinsNumber.setText(mUserInfo.getCoinsNumber()+"");
      }
    }
  }

  private void getProxyInfo(String userName){
    if(showProgress != null){
      showProgress.setMessage("正在获取代理人信息...");
      showProgress.setCanceledOnTouchOutside(false);
      showProgress.show();
    }
    BmobQuery<ProxyInfo> query = new BmobQuery<ProxyInfo>();
    query.addWhereEqualTo("userName",userName);
    query.findObjects(new FindListener<ProxyInfo>() {
      @Override
      public void done(List<ProxyInfo> list, BmobException e) {
        if (showProgress != null && showProgress.isShowing()) {
          showProgress.dismiss();
        }
        if(e == null) {
          if(list.size() == 0){
            toast("不存在该代理人");
          }else {
            mProxyInfo = list.get(0);
            mHandler.sendEmptyMessage(UPDATE_PROXY_INFO);
          }
        } else {
          toast("获取代理人信息异常,请退出页面重进，错误信息" + e.getMessage());
        }
      }
    });
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

  private void showEditQQDialog() {
    View diaog_view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_qq_group_layout,null);
    final EditText edit_text = (EditText) diaog_view.findViewById(R.id.edit_text);
    edit_text.setInputType(InputType.TYPE_CLASS_NUMBER);

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("修改群号码 ");
    builder.setView(diaog_view);
    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        String text = edit_text.getText().toString().trim();
        if(text.length() > 0){
          dialog.dismiss();
          String[] array = mProxyInfo.getDesc().split("::");
          mProxyInfo.setDesc(array[0] +"::" + text + "::" + array[2]);
          mProxyInfo.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if(e == null){
                toast("QQ群号码修改成功");
                mHandler.sendEmptyMessage(UPDATE_PROXY_INFO);
              }else {
                toast("异常，错误信息：" + e.getMessage());
              }
            }
          });
        }else {
          Toast.makeText(ProxyPersonActivity.this,"请输入新的QQ群号码",Toast.LENGTH_LONG).show();
        }
      }
    });
    builder.create().show();
  }
}
