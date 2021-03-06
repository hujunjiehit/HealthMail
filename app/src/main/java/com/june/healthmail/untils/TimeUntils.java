package com.june.healthmail.untils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by june on 2017/4/24.
 */

public class TimeUntils {

  /**
   * 时间戳转日期
   * @param ms
   * @return
   */
  public static Date transForDate(Long ms){
    if(ms==null){
      ms=(long)0;
    }
    long msl=(long)ms*1000;
    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date temp=null;
    if(ms!=null){
      try {
        String str=sdf.format(msl);
        temp=sdf.parse(str);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    return temp;
  }

  /**
   * 时间戳转日期
   * @param ms
   * @return 2017-04-24
   */
  public static String transForDate1(Integer ms){
    String str = "";
    if(ms!=null){
      long msl=(long)ms*1000;
      SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

      if(ms!=null){
        try {
          str=sdf.format(msl); //2017-04-24 23:32:24
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
    return str.split(" ")[0];
  }

  /**
   * 时间戳转时间日期
   * @param ms
   * @return 2017-04-24
   */
  public static String transForDate1(long ms){
    String str = "";
      long msl=(long)ms*1000;
      SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      try {
        str=sdf.format(msl); //2017-04-24 23:32:24
      } catch (Exception e) {
        e.printStackTrace();
      }
    return str;
  }

  /*
   * 将时间转换为时间戳
   */
  public static long dateToStamp(String s) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = simpleDateFormat.parse(s);
    return date.getTime();
  }

  public static boolean isInTenDays(String time) {
    try {
      long time1 = TimeUntils.dateToStamp(time);
      long diff = time1 - (System.currentTimeMillis() + 1000*3600*24*10);
      if(diff > 0){
        return false;
      }else {
        return true;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  /*
  * 获取今日的日期 eg：2017-07-30
  */
  public static String getTodayStr(){
    return transForDate1(System.currentTimeMillis()/1000).split(" ")[0].trim();
  }
}
