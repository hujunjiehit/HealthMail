package com.example;

public class MyClass {

  public static void main(String[] args) {
    System.out.println("Hello");
    String date = "2017-07-23T23:23:34";
    System.out.println("result = " + getCourseDay(date));

  }

  private static int getCourseDay(String hm_gbc_date) {
    if(hm_gbc_date.contains("T")) {
      String array[] = hm_gbc_date.split("T")[0].split("-");
      return Integer.parseInt(array[2]);
    }else {
      return 0;
    }
  }
}
