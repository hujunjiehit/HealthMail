package com.coinbene.common.utils;

import android.text.TextUtils;
import android.util.Log;

import com.coinbene.common.BuildConfig;
import com.coinbene.common.context.CBRepository;

/**
 *
 * 日志utils
 * Created by mengxiangdong on 2018/1/17.
 */

public class DLog {
    public static void e(String tag, String msg) {
        if (BuildConfig.ENABLE_DEBUG || CBRepository.isInDebugWhiteList()) {
            if (!TextUtils.isEmpty(msg))
                Log.e(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (BuildConfig.ENABLE_DEBUG || CBRepository.isInDebugWhiteList()) {
            if (!TextUtils.isEmpty(msg)) {
                Log.d(tag, msg);
            }
        }
    }

    public static void i(String tag, String msg) {
        if (BuildConfig.ENABLE_DEBUG || CBRepository.isInDebugWhiteList()) {
            if (!TextUtils.isEmpty(msg)) {
                Log.i(tag, msg);
            }
        }
    }

    public static void w(String tag, String msg) {
        if (BuildConfig.ENABLE_DEBUG || CBRepository.isInDebugWhiteList()) {
            if (!TextUtils.isEmpty(msg)) {
                Log.w(tag, msg);
            }
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (BuildConfig.ENABLE_DEBUG || CBRepository.isInDebugWhiteList()) {
            if (!TextUtils.isEmpty(msg)) {
                Log.e(tag, msg, tr);
            }
        }
    }
}
