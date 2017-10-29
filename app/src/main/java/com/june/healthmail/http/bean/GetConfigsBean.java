package com.june.healthmail.http.bean;

/**
 * Created by june on 2017/10/28.
 */

public class GetConfigsBean extends BaseBean{

  /**
   * buyAuthUrl : https://item.taobao.com/item.htm?spm=686.1000925.0.0.3f11c9edBRzPCP&id=554827091968
   * buyCoinsUrl : https://item.taobao.com/item.htm?spm=686.1000925.0.0.33df4945lbxAVX&id=559716537351
   * postCoinsCost : 2
   * specialCoinsCost : 1
   * freeTimesPerDay : 1200
   * updateLevelUrl : https://item.taobao.com/item.htm?spm=686.1000925.0.0.3f11c9edBRzPCP&id=554827091968
   * payCoinsCost : 1
   * activityOrNot : 0
   * qqGroup : QQ交流群:524326010
   加群免费领取试用时间
   * notification : 1、小号失效变绿色是由于手机时间不对导致的，将时间设置成自动时间，然后重启手机就好了|2、碰到问题，先自己重启手机，如果仍然有问题，再去qq群提问|3、小号管理左上角可以对小号进行备份，将小号备份到云端之后可以很方便的进行恢
   * jumpOrNot : 0
   * minConfigTime : 100
   */

  private String buyAuthUrl;
  private String buyCoinsUrl;
  private String postCoinsCost;
  private String specialCoinsCost;
  private String freeTimesPerDay;
  private String updateLevelUrl;
  private String payCoinsCost;
  private String activityOrNot;
  private String qqGroup;
  private String notification;
  private String jumpOrNot;
  private String minConfigTime;

  public String getBuyAuthUrl() {
    return buyAuthUrl;
  }

  public void setBuyAuthUrl(String buyAuthUrl) {
    this.buyAuthUrl = buyAuthUrl;
  }

  public String getBuyCoinsUrl() {
    return buyCoinsUrl;
  }

  public void setBuyCoinsUrl(String buyCoinsUrl) {
    this.buyCoinsUrl = buyCoinsUrl;
  }

  public String getPostCoinsCost() {
    return postCoinsCost;
  }

  public void setPostCoinsCost(String postCoinsCost) {
    this.postCoinsCost = postCoinsCost;
  }

  public String getSpecialCoinsCost() {
    return specialCoinsCost;
  }

  public void setSpecialCoinsCost(String specialCoinsCost) {
    this.specialCoinsCost = specialCoinsCost;
  }

  public String getFreeTimesPerDay() {
    return freeTimesPerDay;
  }

  public void setFreeTimesPerDay(String freeTimesPerDay) {
    this.freeTimesPerDay = freeTimesPerDay;
  }

  public String getUpdateLevelUrl() {
    return updateLevelUrl;
  }

  public void setUpdateLevelUrl(String updateLevelUrl) {
    this.updateLevelUrl = updateLevelUrl;
  }

  public String getPayCoinsCost() {
    return payCoinsCost;
  }

  public void setPayCoinsCost(String payCoinsCost) {
    this.payCoinsCost = payCoinsCost;
  }

  public String getActivityOrNot() {
    return activityOrNot;
  }

  public void setActivityOrNot(String activityOrNot) {
    this.activityOrNot = activityOrNot;
  }

  public String getQqGroup() {
    return qqGroup;
  }

  public void setQqGroup(String qqGroup) {
    this.qqGroup = qqGroup;
  }

  public String getNotification() {
    return notification;
  }

  public void setNotification(String notification) {
    this.notification = notification;
  }

  public String getJumpOrNot() {
    return jumpOrNot;
  }

  public void setJumpOrNot(String jumpOrNot) {
    this.jumpOrNot = jumpOrNot;
  }

  public String getMinConfigTime() {
    return minConfigTime;
  }

  public void setMinConfigTime(String minConfigTime) {
    this.minConfigTime = minConfigTime;
  }
}
