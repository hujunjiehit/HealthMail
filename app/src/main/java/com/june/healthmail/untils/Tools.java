package com.june.healthmail.untils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.june.healthmail.activity.WebViewActivity;
import com.june.healthmail.model.UserInfo;

import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

  public static String getMaxNumber(UserInfo userInfo) {
    if(userInfo.getUserType() >= 3) {
      return ""+PreferenceHelper.getInstance().getMaxSetupCourses();
    }else {
      if(userInfo.getMaxNumber() == null) {
        return ""+50;
      }else {
        return  ""+userInfo.getMaxNumber();
      }
    }
  }

  public static void updateCurrentPingjiaTime(int progress) {
    int delta = PreferenceHelper.getInstance().getMaxPingjiaTime() - PreferenceHelper.getInstance().getMinPingjiaTime();
    PreferenceHelper.getInstance().setMinPingjiaTime(progress);
    PreferenceHelper.getInstance().setMaxPingjiaTime(progress + delta);
  }

  public static void updateCurrentYuekeTime(int progress) {
    int delta = PreferenceHelper.getInstance().getMaxYuekeTime() - PreferenceHelper.getInstance().getMinYuekeTime();
    PreferenceHelper.getInstance().setMinYuekeTime(progress);
    PreferenceHelper.getInstance().setMaxYuekeTime(progress + delta);
  }

  public static HashMap<String, String> parseParams(Uri uri) {
    if (uri == null) {
      return new HashMap<String, String>();
    }
    HashMap<String, String> temp = new HashMap<String, String>();
    Set<String> keys = getQueryParameterNames(uri);
    for (String key : keys) {
      temp.put(key, uri.getQueryParameter(key));
    }
    return temp;
  }

  public static Set<String> getQueryParameterNames(Uri uri) {
    String query = uri.getEncodedQuery();
    if (query == null) {
      return Collections.emptySet();
    }

    Set<String> names = new LinkedHashSet<String>();
    int start = 0;
    do {
      int next = query.indexOf('&', start);
      int end = (next == -1) ? query.length() : next;

      int separator = query.indexOf('=', start);
      if (separator > end || separator == -1) {
        separator = end;
      }

      String name = query.substring(start, separator);
      names.add(URLDecoder.decode(name));

      start = end + 1;
    } while (start < query.length());

    return Collections.unmodifiableSet(names);
  }

  /**
   * 解析出url参数中的键值对
   */
  public static Map<String,String> parseUrlParam(String strParam) {
    Map<String, String> mapRequest = new HashMap<String, String>();
    String[] arrSplit = null;

    arrSplit = strParam.split("[&]");
    for(String strSplit : arrSplit) {
      String[] arrSplitEqual=null;
      arrSplitEqual= strSplit.split("[=]");
      //解析出键值
      if(arrSplitEqual.length > 1) {
        //正确解析
        mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
      }else {
        if(arrSplitEqual[0] != "") {
          //只有参数没有值，不加入
          mapRequest.put(arrSplitEqual[0], "");
        }
      }
    }
    return mapRequest;
  }
}
