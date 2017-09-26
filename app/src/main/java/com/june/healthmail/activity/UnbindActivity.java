package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.june.healthmail.Config.CommonConfig;
import com.june.healthmail.R;
import com.june.healthmail.model.PayDeviceInfo;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.ShowProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/4/24.
 */

public class UnbindActivity extends BaseActivity {

  @BindView(R.id.tv_user_name)
  TextView tvUserName;
  @BindView(R.id.tv_coins_number)
  TextView tvCoinsNumber;
  @BindView(R.id.tv_unbind_times)
  TextView tvUnbindTimes;
  @BindView(R.id.tv_bind_status)
  TextView tvbindStatus;
  @BindView(R.id.btn_unbind_free)
  Button btnUnbindFree;
  @BindView(R.id.btn_unbind_use_coins)
  Button btnUnbindUseCoins;

  private PayDeviceInfo payDeviceInfo;
  private UserInfo currentUser;
  private ShowProgress showProgress;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_unbind_device);
    ButterKnife.bind(this);
    currentUser = BmobUser.getCurrentUser(UserInfo.class);
    Intent intent = this.getIntent();
    if(intent != null) {
      payDeviceInfo = (PayDeviceInfo) intent.getSerializableExtra("payDeviceInfo");
    } else {
      return;
    }
    initData();
  }

  private void initData() {
    showProgress = new ShowProgress(this);
    tvUserName.setText(payDeviceInfo.getUsername());
    tvCoinsNumber.setText(currentUser.getCoinsNumber() + "个");
    if (payDeviceInfo.getUnbindTimes() < 0) {
      tvUnbindTimes.setText("0次");
    } else {
      tvUnbindTimes.setText(payDeviceInfo.getUnbindTimes() + "次");
    }
    if (TextUtils.isEmpty(payDeviceInfo.getDeviceId())){
      tvbindStatus.setText("未绑定设备");
    } else {
      tvbindStatus.setText("已绑定");
    }
  }

  @OnClick(R.id.btn_unbind_free)
  public void unbindFree(View view) {
    if (TextUtils.isEmpty(payDeviceInfo.getDeviceId())){
      toast("当前账号暂未绑定设备，无需解绑");
      return;
    }
    if(payDeviceInfo.getUnbindTimes() <= 0) {
      toast("免费解绑次数已用完，请用金币解绑");
      return;
    }
    showUnbindDialog();
  }

  @OnClick(R.id.btn_unbind_use_coins)
  public void unbindUseCoins(View view) {
    if (TextUtils.isEmpty(payDeviceInfo.getDeviceId())){
      toast("当前账号暂未绑定设备，无需解绑");
      return;
    }
    showUnbindDialogUseCoins();
  }

  @OnClick(R.id.img_back)
  public void goBack(View view) {
    finish();
  }

  @OnClick(R.id.tv_go_to_buy_coins)
  public void openTaobaoShopping(View view){
    String url = "https://item.taobao.com/item.htm?spm=a1z10.1-c.w4023-14573908235.8.a6V6o9&id=547061088671";
    Intent intent = new Intent();
    if (CommonUntils.checkPackage(this,"com.taobao.taobao")){
      Log.e("test","taobao is not installed");
      intent.setAction("android.intent.action.VIEW");
      Uri uri = Uri.parse(url);
      intent.setData(uri);
      startActivity(intent);
    } else {
      intent.putExtra("url",url);
      intent.setClass(this,WebViewActivity.class);
      startActivity(intent);
    }
  }

  private void showUnbindDialog() {
    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("重要提示")
        .setMessage("每个帐号可以解除三次设备绑定，当前剩余解绑次数：" + payDeviceInfo.getUnbindTimes() + "\n\n是否确定解绑?")
        .setNegativeButton("取消解绑", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        })
        .setPositiveButton("确定解绑", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            Log.d("test", "用户选择确定解绑");
            if(payDeviceInfo.getUnbindTimes() < 0) {
              toast("解绑次数已用完，无法解绑");
              return;
            }
            if (showProgress != null && !showProgress.isShowing()) {
              showProgress.setMessage("正在解绑...");
              showProgress.show();
            }
            payDeviceInfo.setDeviceId("");
            payDeviceInfo.setDeviceMac("");
            payDeviceInfo.setDeviceDesc("");
            payDeviceInfo.setUnbindTimes(payDeviceInfo.getUnbindTimes()-1);
            payDeviceInfo.update(new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if (showProgress != null && showProgress.isShowing()) {
                  showProgress.dismiss();
                }
                if(e == null){
                  Log.d("test","解绑成功");
                  toast("解绑成功");
                  runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      if (payDeviceInfo.getUnbindTimes() < 0) {
                        tvUnbindTimes.setText("0次");
                      } else {
                        tvUnbindTimes.setText(payDeviceInfo.getUnbindTimes() + "次");
                      }
                      if (TextUtils.isEmpty(payDeviceInfo.getDeviceId())){
                        tvbindStatus.setText("未绑定设备");
                      } else {
                        tvbindStatus.setText("已绑定");
                      }
                    }
                  });
                }else{
                  toast("解绑失败，请重试");
                  Log.d("test","解绑失败，" + e.getMessage());
                }
              }
            });
          }
        }).create();
    dialog.show();
  }

  private void showUnbindDialogUseCoins() {

    AlertDialog dialog = new AlertDialog.Builder(this)
        .setTitle("提示")
        .setMessage("解除绑定将消耗" + CommonConfig.UnbindCost + "个金币，是否确定解绑?")
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
            Log.d("test", "用户选择确定解绑");
            if(currentUser.getCoinsNumber() < 88) {
              toast("金币余额不足88个，请先充值");
              return;
            }
            currentUser.setCoinsNumber(currentUser.getCoinsNumber() - CommonConfig.UnbindCost);
            currentUser.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
              @Override
              public void done(BmobException e) {
                if(e == null) {
                  toast("金币扣除成功");
                  if (showProgress != null && !showProgress.isShowing()) {
                    showProgress.setMessage("正在解绑...");
                    showProgress.show();
                  }
                  payDeviceInfo.setDeviceId("");
                  payDeviceInfo.setDeviceMac("");
                  payDeviceInfo.setDeviceDesc("");
                  payDeviceInfo.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                      if (showProgress != null && showProgress.isShowing()) {
                        showProgress.dismiss();
                      }
                      if (e == null) {
                        Log.d("test", "解绑成功");
                        toast("解绑成功,请前往登录界面登录");
                        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                            tvCoinsNumber.setText(currentUser.getCoinsNumber() + "个");
                            if (TextUtils.isEmpty(payDeviceInfo.getDeviceId())){
                              tvbindStatus.setText("未绑定设备");
                            } else {
                              tvbindStatus.setText("已绑定");
                            }
                          }
                        });
                      }
                    }
                  });
                }
              }
            });
          }
        }).create();
    dialog.show();
  }
}
