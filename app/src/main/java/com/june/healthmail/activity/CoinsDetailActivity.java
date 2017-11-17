package com.june.healthmail.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.june.healthmail.R;
import com.june.healthmail.model.KeyCoins;
import com.june.healthmail.model.UserInfo;
import com.june.healthmail.untils.CommonUntils;
import com.june.healthmail.untils.PreferenceHelper;
import com.june.healthmail.untils.ShowProgress;
import com.june.healthmail.untils.TimeUntils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by june on 2017/11/15.
 */

public class CoinsDetailActivity extends BaseActivity implements View.OnClickListener {

  @BindView(R.id.img_back)
  ImageView mImgBack;
  @BindView(R.id.tv_coins_number)
  TextView mTvCoinsNumber;
  @BindView(R.id.edit_input_key)
  EditText mEditInputKey;
  @BindView(R.id.btn_ok)
  Button mBtnOk;
  @BindView(R.id.buy_coins_key)
  TextView mBuyCoinsKey;
  @BindView(R.id.tv_notice)
  TextView mTvNotice;
  private UserInfo userInfo;
  private Unbinder unbinder;
  private ShowProgress showProgress;
  private AlertDialog.Builder dialogBuilder;
  private KeyCoins keyCoins;

  private static final int UPDATE_COINS = 1;
  private static final int UPDATE_TIMES = 2;

  private int tryTimes = 0;

  private Handler mHandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case UPDATE_COINS:
          mTvCoinsNumber.setText(userInfo.getCoinsNumber() + "");
          break;

        case UPDATE_TIMES:
          long time = (System.currentTimeMillis() - PreferenceHelper.getInstance().getRestrictedTime())/1000;
          if(mTvNotice != null) {
            if(time <= 300) {
              mTvNotice.setText("失败次数过多，请" + (300 - time) + "秒后再重新尝试");
              this.sendEmptyMessageDelayed(UPDATE_TIMES,1000);
            }else {
              mTvNotice.setVisibility(View.GONE);
              tryTimes = 0;
              mBtnOk.setEnabled(true);
            }
          }
          break;

        default:
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    userInfo = BmobUser.getCurrentUser(UserInfo.class);
    setContentView(R.layout.activity_coins_detail);
    unbinder = ButterKnife.bind(this);
    setOnListener();
    init();
  }

  private void setOnListener() {
    mBtnOk.setOnClickListener(this);
    mImgBack.setOnClickListener(this);
    mBuyCoinsKey.setOnClickListener(this);
  }

  private void init() {
    if(PreferenceHelper.getInstance().getIsRestricted()) {
      if((System.currentTimeMillis() - PreferenceHelper.getInstance().getRestrictedTime())/1000 >= 300) {
        mBtnOk.setEnabled(true);
        mTvNotice.setVisibility(View.GONE);
        PreferenceHelper.getInstance().setIsRestricted(false);
      }else {
        mBtnOk.setEnabled(false);
        mTvNotice.setVisibility(View.VISIBLE);
        mHandler.sendEmptyMessage(UPDATE_TIMES);
      }
    }
    dialogBuilder = new AlertDialog.Builder(CoinsDetailActivity.this);
    mTvCoinsNumber.setText(userInfo.getCoinsNumber() + "");
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (unbinder != null && unbinder != Unbinder.EMPTY) {
      unbinder.unbind();
    }
    mHandler.removeCallbacksAndMessages(null);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.img_back:
        finish();
        break;
      case R.id.btn_ok:
        handleAddCoins();
        break;
      case R.id.buy_coins_key:
        openTaobaoShopping(PreferenceHelper.getInstance().getBuyCoinsUrl());
        break;
      default:
        break;
    }
  }

  private void handleAddCoins() {
    String key = mEditInputKey.getText().toString().toLowerCase().trim();
    if (!preCheckKey(key)) {
      return;
    }
    if (showProgress == null) {
      showProgress = new ShowProgress(this);
    }
    if (!showProgress.isShowing()) {
      showProgress.setMessage("正在查询卡密信息，请稍后...");
      showProgress.show();
    }

    BmobQuery<KeyCoins> query = new BmobQuery<>();
    query.addWhereEqualTo("key", key);
    query.findObjects(new FindListener<KeyCoins>() {
      @Override
      public void done(List<KeyCoins> list, BmobException e) {
        if (showProgress != null && showProgress.isShowing()) {
          showProgress.dismiss();
        }
        if (e == null) {
          if (list.size() == 0) {
            //卡密不存在
            //toast("卡密不存在");
            tryTimes++;
            Log.e("test"," tryTimes = " + tryTimes);
            showdialog("提示", "卡密不存在，请确认之后再输入", "我知道了", null, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
              }
            }, null);
          } else {
            //卡密存在
            keyCoins = list.get(0);
            if (keyCoins.getStatus() == 0) {
              showdialog("提示", "卡密已失效：" + keyCoins.getNotice(), "我知道了", null, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              }, null);
            } else {
              showdialog("提示", "当前卡密可充值" + keyCoins.getValue() + "个金币，充值后卡密失效，是否确定充值？", "确定充值", "取消充值", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                  addCoins();
                }
              }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.dismiss();
                }
              });
            }
          }
        } else {
          Log.i("bmob", "查询失败：" + e.getMessage() + "," + e.getErrorCode());
        }
      }
    });
  }

  private boolean preCheckKey(String key) {
    if (key.length() != 16) {
      toast("卡密长度为16位，请确认您的卡密");
      return false;
    }

    for (int i = 0; i < key.length(); i++) {
      if (!Character.isLetterOrDigit(key.charAt(i))) {
        toast("卡密只能包含字母和数字");
        return false;
      }
    }

    if (tryTimes >= 10) {
      //尝试次数过多
      PreferenceHelper.getInstance().setIsRestricted(true);
      PreferenceHelper.getInstance().setRestrictedTime(System.currentTimeMillis());
      mTvNotice.setVisibility(View.VISIBLE);
      mHandler.sendEmptyMessageDelayed(UPDATE_TIMES,1000);
      mBtnOk.setEnabled(false);
      return false;
    }
    return true;
  }

  private void addCoins() {
    if (showProgress == null) {
      showProgress = new ShowProgress(this);
    }
    if (!showProgress.isShowing()) {
      showProgress.setMessage("正在为您充值金币，请稍后...");
      showProgress.show();
    }
    keyCoins.setStatus(0);
    keyCoins.setNotice(userInfo.getUsername() + "于" + TimeUntils.transForDate1(System.currentTimeMillis() / 1000) +
      "使用该卡密进行充值");
    keyCoins.update(new UpdateListener() {
      @Override
      public void done(BmobException e) {
        if (e == null) {
          userInfo.setCoinsNumber(userInfo.getCoinsNumber() + keyCoins.getValue());
          userInfo.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
              if (e == null) {
                if (showProgress != null && showProgress.isShowing()) {
                  showProgress.dismiss();
                }
                toast(keyCoins.getValue() + "个金币充值成功");
                showdialog("提示", "金币充值成功，金币余额增加：" + keyCoins.getValue(), "我知道了", null, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                }, null);
                mHandler.sendEmptyMessage(UPDATE_COINS);
              }
            }
          });
        } else {
          if (showProgress != null && showProgress.isShowing()) {
            showProgress.dismiss();
          }
          toast("金币充值失败");
        }
      }
    });

  }


  private void showdialog(String title, String message, String positiveText, String negativeText,
                          DialogInterface.OnClickListener listenerPositive, DialogInterface.OnClickListener listenerNegative) {
    if (dialogBuilder == null) {
      dialogBuilder = new AlertDialog.Builder(CoinsDetailActivity.this);
    }

    dialogBuilder.setTitle(title);
    dialogBuilder.setMessage(message);
    if (positiveText != null) {
      dialogBuilder.setPositiveButton(positiveText, listenerPositive);
    }else {
      dialogBuilder.setPositiveButton(null,null);
    }
    if (negativeText != null) {
      dialogBuilder.setNegativeButton(negativeText, listenerNegative);
    }else {
      dialogBuilder.setNegativeButton(null,null);
    }
    dialogBuilder.create().show();
  }

  private void openTaobaoShopping(final String url) {
    Intent intent = new Intent();
    if (CommonUntils.checkPackage(this, "com.taobao.taobao")) {
      Log.e("test", "taobao is not installed");
      intent.setAction("android.intent.action.VIEW");
      Uri uri = Uri.parse(url);
      intent.setData(uri);
      startActivity(intent);
    } else {
      intent.putExtra("url", url);
      intent.setClass(this, WebViewActivity.class);
      startActivity(intent);
    }
  }
}
