package com.june.healthmail.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import net.youmi.android.normal.common.ErrorCode;
import net.youmi.android.normal.spot.SpotListener;
import net.youmi.android.normal.spot.SpotManager;

/**
 * Created by bjhujunjie on 2017/4/8.
 */

public class BaseActivity extends Activity{

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }


//
//    @Override
//    public void onBackPressed() {
//        // 点击后退关闭插屏广告
//        if (SpotManager.getInstance(mContext).isSpotShowing()) {
//            SpotManager.getInstance(mContext).hideSpot();
//        } else {
//            super.onBackPressed();
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        // 插屏广告
//        SpotManager.getInstance(mContext).onPause();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        // 插屏广告
//        SpotManager.getInstance(mContext).onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        // 插屏广告
//        SpotManager.getInstance(mContext).onDestroy();
//    }
//
//    /**
//     * 设置插屏广告
//     */
//    protected void setupSpotAd() {
//        // 设置插屏图片类型，默认竖图
//        // 横图
//        //SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_HORIZONTAL);
//        // 竖图
//        SpotManager.getInstance(mContext).setImageType(SpotManager.IMAGE_TYPE_VERTICAL);
//
//        // 设置动画类型，默认高级动画
//        // 无动画
//        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_NONE);
//       // 简单动画
//        //SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_SIMPLE);
//        // 高级动画
//        SpotManager.getInstance(mContext).setAnimationType(SpotManager.ANIMATION_TYPE_ADVANCED);
//
//
//        // 展示插屏广告
//        SpotManager.getInstance(mContext).showSpot(mContext, new SpotListener() {
//
//            @Override
//            public void onShowSuccess() {
//                Log.e("test","插屏展示成功");
//            }
//
//            @Override
//            public void onShowFailed(int errorCode) {
//                Log.e("test","插屏展示失败");
//                switch (errorCode) {
//                    case ErrorCode.NON_NETWORK:
//                        Log.e("test","网络异常");
//                        break;
//                    case ErrorCode.NON_AD:
//                        Log.e("test","暂无插屏广告");
//                        break;
//                    case ErrorCode.RESOURCE_NOT_READY:
//                        Log.e("test","插屏资源还没准备好");
//                        break;
//                    case ErrorCode.SHOW_INTERVAL_LIMITED:
//                        Log.e("test","请勿频繁展示");
//                        break;
//                    case ErrorCode.WIDGET_NOT_IN_VISIBILITY_STATE:
//                        Log.e("test","请设置插屏为可见状态");
//                        break;
//                    default:
//                        Log.e("test","请稍后再试");
//                        break;
//                }
//            }
//
//            @Override
//            public void onSpotClosed() {
//                Log.e("test","插屏被关闭");
//            }
//
//            @Override
//            public void onSpotClicked(boolean isWebPage) {
//                Log.e("test","插屏被点击");
//                Log.e("test","是否是网页广告: " + isWebPage);
//            }
//        });
//    }

}
