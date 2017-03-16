package com.june.healthmail.model;

import java.util.Date;

import cn.bmob.v3.BmobUser;

/**
 * Created by bjhujunjie on 2016/9/18.
 */
public class UserInfo extends BmobUser {
  private Integer userType;
  private Integer allowDays;
  private Long beginTime;
  private String bindMac;
  private String bindDesc;
  private Integer unbindTimes;
  private Integer appVersion;

  private Integer coinsNumber;  //金币数量
  private String invitePeoplePhone;  //邀请人手机号

  public Integer getCoinsNumber() {
    return coinsNumber;
  }

  public void setCoinsNumber(Integer coinsNumber) {
    this.coinsNumber = coinsNumber;
  }

  public String getInvitePeoplePhone() {
    return invitePeoplePhone;
  }

  public void setInvitePeoplePhone(String invitePeoplePhone) {
    this.invitePeoplePhone = invitePeoplePhone;
  }

  public Integer getUserType() {
    return userType;
  }

  public void setUserType(Integer userType) {
    this.userType = userType;
  }

  public Integer getAllowDays() {
    return allowDays;
  }

  public void setAllowDays(Integer allowDays) {
    this.allowDays = allowDays;
  }

  public String getBindMac() {
    return bindMac;
  }

  public void setBindMac(String bindMac) {
    this.bindMac = bindMac;
  }

  public String getBindDesc() {
    return bindDesc;
  }

  public void setBindDesc(String bindDesc) {
    this.bindDesc = bindDesc;
  }

  public Integer getUnbindTimes() {
    return unbindTimes;
  }

  public void setUnbindTimes(Integer unbindTimes) {
    this.unbindTimes = unbindTimes;
  }

  public Long getBeginTime() {
    return beginTime;
  }

  public void setBeginTime(Long beginTime) {
    this.beginTime = beginTime;
  }

  public Integer getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(Integer appVersion) {
    this.appVersion = appVersion;
  }

  @Override
  public String toString() {
    return "objectId:" + this.getObjectId() + "  userName:" + this.getUsername() + "  CreatedAt:" + this.getCreatedAt() + "  UpdatedAt:" + this.getUpdatedAt() +
        "  userType:" + this.getUserType() + "  allowDays:" + this.getAllowDays() + "  bindMac:" + this.getBindMac() + "  bindDesc:" + this.getBindDesc() +
        "  unbindTimes:" + this.getUnbindTimes() + "  coinsNumber:" + this.getCoinsNumber();
  }
}
