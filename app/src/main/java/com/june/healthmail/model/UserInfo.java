package com.june.healthmail.model;

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
  private String installId;

  private String lastDay;
  private Integer yuekeTimes;
  private Integer pingjiaTimes;

  private Integer coinsNumber;  //金币数量
  private String invitePeoplePhone;  //邀请人手机号
  private Integer payStatus;  //付款插件开通状态

  private Integer autoPay;  //自动付款开通状态
  private Integer payDays;  //自动付款剩余天数
  private Long payBegin;  //自动付款授权开始时间

  private String proxyPerson; //代理人信息

  private Integer XTimes; //次数倍数
  private Integer maxNumber; //最多约课人数


  public Integer getAutoPay() {
    return autoPay;
  }

  public void setAutoPay(Integer autoPay) {
    this.autoPay = autoPay;
  }

  public Integer getPayDays() {
    return payDays;
  }

  public void setPayDays(Integer payDays) {
    this.payDays = payDays;
  }

  public Long getPayBegin() {
    return payBegin;
  }

  public void setPayBegin(Long payBegin) {
    this.payBegin = payBegin;
  }

  public Integer getPayStatus() {
    return payStatus;
  }

  public void setPayStatus(Integer payStatus) {
    this.payStatus = payStatus;
  }

  public String getLastDay() {
    return lastDay;
  }

  public void setLastDay(String lastDay) {
    this.lastDay = lastDay;
  }

  public Integer getYuekeTimes() {
    return yuekeTimes;
  }

  public void setYuekeTimes(Integer yuekeTimes) {
    this.yuekeTimes = yuekeTimes;
  }

  public Integer getPingjiaTimes() {
    return pingjiaTimes;
  }

  public void setPingjiaTimes(Integer pingjiaTimes) {
    this.pingjiaTimes = pingjiaTimes;
  }

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

  public String getInstallId() {
    return installId;
  }

  public void setInstallId(String installId) {
    this.installId = installId;
  }

  public String getProxyPerson() {
    return proxyPerson;
  }

  public void setProxyPerson(String proxyPerson) {
    this.proxyPerson = proxyPerson;
  }

  public Integer getXTimes() {
    return XTimes;
  }

  public void setXTimes(Integer XTimes) {
    this.XTimes = XTimes;
  }

  public Integer getMaxNumber() {
    return maxNumber;
  }

  public void setMaxNumber(Integer maxNumber) {
    this.maxNumber = maxNumber;
  }

  @Override
  public String toString() {
    return "objectId:" + this.getObjectId() + "  userName:" + this.getUsername() + "  CreatedAt:" + this.getCreatedAt() + "  UpdatedAt:" + this.getUpdatedAt() +
        "  userType:" + this.getUserType() + "  allowDays:" + this.getAllowDays() + "  bindMac:" + this.getBindMac() + "  bindDesc:" + this.getBindDesc() +
        "  unbindTimes:" + this.getUnbindTimes() + "  coinsNumber:" + this.getCoinsNumber();
  }
}
