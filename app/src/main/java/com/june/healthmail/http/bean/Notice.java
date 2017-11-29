package com.june.healthmail.http.bean;

/**
 * Created by june on 2017/11/16.
 */

public class Notice {


  /**
   * content : 小号失效变绿色是
   * url : https://www.baidu.com/
   * autoDismiss : true
   * autoDissmissDuration : 3000
   * showIndicator : true
   * enable : 1
   */

  private String content;
  private String url;
  private boolean autoDismiss;
  private int autoDissmissDuration;
  private boolean showIndicator;
  private int enable;

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isAutoDismiss() {
    return autoDismiss;
  }

  public void setAutoDismiss(boolean autoDismiss) {
    this.autoDismiss = autoDismiss;
  }

  public int getAutoDissmissDuration() {
    return autoDissmissDuration;
  }

  public void setAutoDissmissDuration(int autoDissmissDuration) {
    this.autoDissmissDuration = autoDissmissDuration;
  }

  public boolean isShowIndicator() {
    return showIndicator;
  }

  public void setShowIndicator(boolean showIndicator) {
    this.showIndicator = showIndicator;
  }

  public int getEnable() {
    return enable;
  }

  public void setEnable(int enable) {
    this.enable = enable;
  }
}
