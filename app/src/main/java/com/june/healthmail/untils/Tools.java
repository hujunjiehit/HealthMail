package com.june.healthmail.untils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.june.healthmail.activity.WebViewActivity;

/**
 * Created by june on 2017/6/15.
 */

public class Tools {

  public static int getInt(Integer value){
    int result;
    if (value == null) {
      result = 0;
    } else {
      result = value.intValue();
    }
    return result;
  }

  public static int parseInt(String src){
    int result;
    if(TextUtils.isEmpty(src)){
      result = 0;
    }else {
      result = Integer.parseInt(src.trim());
    }
    return result;
  }

  public static float parseFloat(String src){
    float result;
    if(TextUtils.isEmpty(src)){
      result = 0;
    }else {
      result = Float.parseFloat(src.trim());
    }
    return result;
  }

  public static String getUserTypeDsec(Integer userType) {
    String typeDesc;
    if(userType == 0){
      typeDesc = "普通用户";
    }else if(userType == 1){
      typeDesc = "月卡用户";
    }else if(userType == 2){
      typeDesc = "永久用户";
    }else if(userType == 3){
      typeDesc = "高级永久用户";
    }else if(userType == 98){
      typeDesc = "总代理";
    }else if(userType == 99){
      typeDesc = "管理员用户";
    }else if(userType == 100) {
      typeDesc = "超级管理员用户";
    }else {
      typeDesc = "过期用户";
    }
    return typeDesc;
  }

  public static boolean isToday(String str) {
    int day1 = getCourseDay(str);
    int today = parseInt(TimeUntils.transForDate1(System.currentTimeMillis()/1000).split(" ")[0].split("-")[2]);
    if(day1 == today){
      return true;
    }else {
      return false;
    }
  }

  public static int getCourseDay(String hm_gbc_date) {
    if(hm_gbc_date.contains("T")) {
      String array[] = hm_gbc_date.split("T")[0].split("-");
      return parseInt(array[2]);
    }else {
      return 0;
    }
  }

  public static int getPixelByDip(Context context, int dip) {
    return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
  }

  public static int dip2px(Context context, int px) {
    return getPixelByDip(context, px);
  }

  public static void openTaobaoShopping(Context context, final String url){
    Intent intent = new Intent();
    if (CommonUntils.checkPackage(context,"com.taobao.taobao")){
      Log.e("test","taobao is not installed");
      intent.setAction("android.intent.action.VIEW");
      Uri uri = Uri.parse(url);
      intent.setData(uri);
      context.startActivity(intent);
    } else {
      intent.putExtra("url",url);
      intent.setClass(context,WebViewActivity.class);
      context.startActivity(intent);
    }
  }
}
