package com.june.healthmail.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by june on 2017/11/15.
 */

public class KeyCoins extends BmobObject {

  private String key;     //卡密
  private Integer value;  //卡密对应的金币数量
  private Integer status; //卡密状态 1：表示卡密可用，0：表示卡密已经失效
  private String notice;  //备注：显示卡密被哪个用户消费了

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Integer getValue() {
    return value;
  }

  public void setValue(Integer value) {
    this.value = value;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getNotice() {
    return notice;
  }

  public void setNotice(String notice) {
    this.notice = notice;
  }
}
