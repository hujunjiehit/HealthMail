package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.june.healthmail.Config.YoumiConfig;
import com.june.healthmail.R;

import net.youmi.android.AdManager;
import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.spot.SplashViewSettings;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

import static com.tencent.bugly.crashreport.inner.InnerAPI.context;


public class SplashActivity extends Activity {

  private ImageView mSplashItem_iv;
  private Context mContext;
  private RelativeLayout splashContent;
  private RelativeLayout adContent;

  private static final int ON_ANIMATION_END = 1;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case ON_ANIMATION_END:
          //setupSplashAd();
          startActivity(new Intent(SplashActivity.this, LoginActivity.class));
          overridePendingTransition(0, 0);
          SplashActivity.this.finish();
          break;
        default:
          break;
      }
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = this;

    //有米测试广告
    //AdManager.getInstance(this).init(YoumiConfig.appId, YoumiConfig.appSecret,YoumiConfig.isTestModel, YoumiConfig.isEnableYoumiLog);

    setContentView(R.layout.activity_splash);
    mSplashItem_iv = (ImageView) findViewById(R.id.splash_loading_item);
    splashContent = (RelativeLayout) findViewById(R.id.splash_content);
    adContent = (RelativeLayout) findViewById(R.id.ad_content);
    initView();
  }

  @Override
  protected void onResume() {
    super.onResume();
    //JPushInterface.onResume(this);
  }

  @Override
  protected void onPause() {
    super.onPause();
    //JPushInterface.onPause(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 开屏展示界面的 onDestroy() 回调方法中调用
    SpotManager.getInstance(mContext).onDestroy();
  }

  private void initView() {
    Animation translate = AnimationUtils.loadAnimation(this, R.anim.splash_loading);
    translate.setAnimationListener(new Animation.AnimationListener() {

      @Override
      public void onAnimationStart(Animation animation) {
      }

      @Override
      public void onAnimationRepeat(Animation animation) {
      }

      @Override
      public void onAnimationEnd(Animation animation) {
        mHandler.sendEmptyMessageDelayed(ON_ANIMATION_END,50);
      }
    });
    mSplashItem_iv.setAnimation(translate);
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    //不允许返回
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      return false;
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * 设置开屏广告
   */
  private void setupSplashAd() {
    // 创建开屏容器
    splashContent.setVisibility(View.GONE);
    adContent.setVisibility(View.VISIBLE);

    RelativeLayout.LayoutParams params =
            new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    params.addRule(RelativeLayout.ABOVE, R.id.view_divider);

    // 对开屏进行设置
    SplashViewSettings splashViewSettings = new SplashViewSettings();
    //		// 设置是否展示失败自动跳转，默认自动跳转
    //splashViewSettings.setAutoJumpToTargetWhenShowFailed(false);
    // 设置跳转的窗口类
    splashViewSettings.setTargetClass(LoginActivity.class);
    // 设置开屏的容器
    splashViewSettings.setSplashViewContainerAndLayoutParams(adContent,params);

    // 展示开屏广告
    SpotManager.getInstance(mContext)
            .showSplash(mContext, splashViewSettings, new SpotListener() {

              @Override
              public void onShowSuccess() {
                Log.e("test","开屏展示成功");
              }

              @Override
              public void onShowFailed(int errorCode) {
                Log.e("test","开屏展示失败");
                switch (errorCode) {
                  case ErrorCode.NON_NETWORK:
                    Log.e("test","网络异常");
                    break;
                  case ErrorCode.NON_AD:
                    Log.e("test","暂无开屏广告");
                    break;
                  case ErrorCode.RESOURCE_NOT_READY:
                    Log.e("test","开屏资源还没准备好");
                    break;
                  case ErrorCode.SHOW_INTERVAL_LIMITED:
                    Log.e("test","开屏展示间隔限制");
                    break;
                  case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
                    Log.e("test","开屏控件处在不可见状态");
                    break;
                  default:
                    Log.e("test","errorCode:" + errorCode);
                    break;
                }
              }

              @Override
              public void onSpotClosed() {
                Log.e("test","开屏被关闭");
              }

              @Override
              public void onSpotClicked(boolean isWebPage) {
                Log.e("test","开屏被点击");
                Log.e("test","是否是网页广告:" + isWebPage);
              }
            });
    }
}
