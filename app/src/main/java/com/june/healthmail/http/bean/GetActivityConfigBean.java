package com.june.healthmail.http.bean;

/**
 * Created by june on 2017/10/28.
 */

public class GetActivityConfigBean extends BaseBean {

  /**
   * activityTitle : 国庆中秋活动
   * activityContent : 1、金币购买优惠活动，88元1999金币，101元2388金币，128元3000金币，168元4000金币，每个账号仅限参加一次活动，只能选择一种套餐购买(付款备注猫友圈账号，或者将猫友圈账号发给客服也行)；
   2、活动期间发布课程免金币，特殊功能免金币；
   3、新增史上最安全的付款助手软件，完全模拟人手动点击来登录健康猫付款，详情看群共享文件
   * activityUrl : https://item.taobao.com/item.htm?spm=686.1000925.0.0.18b4bb7e3359jd&id=559386553971
   * urlDesc : 点击参与活动
   */

  private String activityTitle;
  private String activityContent;
  private String activityUrl;
  private String urlDesc;

  public String getActivityTitle() {
    return activityTitle;
  }

  public void setActivityTitle(String activityTitle) {
    this.activityTitle = activityTitle;
  }

  public String getActivityContent() {
    return activityContent;
  }

  public void setActivityContent(String activityContent) {
    this.activityContent = activityContent;
  }

  public String getActivityUrl() {
    return activityUrl;
  }

  public void setActivityUrl(String activityUrl) {
    this.activityUrl = activityUrl;
  }

  public String getUrlDesc() {
    return urlDesc;
  }

  public void setUrlDesc(String urlDesc) {
    this.urlDesc = urlDesc;
  }
}
