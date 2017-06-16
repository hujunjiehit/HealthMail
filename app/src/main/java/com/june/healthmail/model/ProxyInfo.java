package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/6/15.
 */

public class ProxyInfo extends BmobObject {

  private String userName;
  private Integer leftTimes1;
  private Integer leftTimes2;
  private String desc;

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Integer getLeftTimes1() {
    return leftTimes1;
  }

  public void setLeftTimes1(Integer leftTimes1) {
    this.leftTimes1 = leftTimes1;
  }

  public Integer getLeftTimes2() {
    return leftTimes2;
  }

  public void setLeftTimes2(Integer leftTimes2) {
    this.leftTimes2 = leftTimes2;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
