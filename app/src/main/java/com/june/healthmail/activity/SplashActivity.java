package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.june.healthmail.R;


public class SplashActivity extends Activity {

  private ImageView mSplashItem_iv;

  private static final int ON_ANIMATION_END = 1;

  private Handler mHandler = new Handler(){
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        case ON_ANIMATION_END:
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
    setContentView(R.layout.activity_splash);
    mSplashItem_iv = (ImageView) findViewById(R.id.splash_loading_item);
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

}
