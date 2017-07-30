package com.example;

import java.text.ParseException;

public class MyClass {

  public static void main(String[] args) {
    System.out.println("Hello");
    String date = "2017-07-18 23:23:34";
    System.out.println("result = " + TimeUntils.transForDate1(System.currentTimeMillis()/1000).split(" ")[0]);

  }

  private static int getCourseDay(String hm_gbc_date) {
    if(hm_gbc_date.contains("T")) {
      String array[] = hm_gbc_date.split("T")[0].split("-");
      return Integer.parseInt(array[2]);
    }else {
      return 0;
    }
  }

  public static boolean isToday(String str) {
    int day1 = getCourseDay(str);
    int today = Integer.parseInt(TimeUntils.transForDate1(System.currentTimeMillis()/1000).split(" ")[0].split("-")[2]);
    System.out.println(day1 + "---" + today);
    if(day1 == today){
      return true;
    }else {
      return false;
    }
  }


  public static boolean isBeforeNow(String time) {
    try {
      long time1 = TimeUntils.dateToStamp(time);
      if(time1 > System.currentTimeMillis()) {
        return false;
      }else {
        return true;
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return false;
  }

  public static boolean isInThreeDays(String time) {
    try {
      long time1 = TimeUntils.dateToStamp(time);
      long diff = time1 - (System.currentTimeMillis() + 1000*3600*24*3);
      System.out.println("diff = " + diff);
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
}
