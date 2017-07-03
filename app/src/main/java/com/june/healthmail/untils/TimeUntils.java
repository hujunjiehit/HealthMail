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

  /*
   * 将时间转换为时间戳
   */
  public static long dateToStamp(String s) throws ParseException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date = simpleDateFormat.parse(s);
    return date.getTime();
  }
}
