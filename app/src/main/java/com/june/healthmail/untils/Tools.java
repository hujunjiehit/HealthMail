package com.june.healthmail.untils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.june.healthmail.model.Course;

import java.text.ParseException;
import java.util.List;

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
    String typeDesc = "";
    if(userType == 0){
      typeDesc = "普通用户";
    }else if(userType == 1){
      typeDesc = "月卡用户";
    }else if(userType == 2) {
      typeDesc = "永久用户";
    }else if(userType == 3) {
      typeDesc = "高级永久用户";
    }else if(userType == 98){
      typeDesc = "总代理";
    }else if(userType == 99) {
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
    return (int) (context.getResources().getDisplayMetrics().density * dip
        + 0.5f);
  }

  //读取setting设置来判断相关服务是否开启:
  public static boolean isServiceOpenedByReadSettings(Context context, String service) {
    int ok = 0;
    try {
      ok = Settings.Secure.getInt(context.getApplicationContext().getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
    } catch (Settings.SettingNotFoundException e) {

    }
    TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
    if (ok == 1) {
      String settingValue = Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
          Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
      if (settingValue != null) {
        ms.setString(settingValue);
        while (ms.hasNext()) {
          String accessibilityService = ms.next();
          if (accessibilityService.equalsIgnoreCase(service)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static String getLeftTimeDesc(int leftHours) {
    Log.e("test","leftHours = " + leftHours);
    StringBuilder sb = new StringBuilder();
    if(leftHours >= 24) {
      sb.append(leftHours/24+"天");
    }
    sb.append(leftHours%24 + "小时");
    return sb.toString();
  }
}
