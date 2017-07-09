package com.june.healthmail.untils;

import android.text.TextUtils;

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
}
