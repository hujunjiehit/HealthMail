package com.june.healthmail.http.bean;

import android.support.annotation.NonNull;

/**
 * Created by june on 2017/11/16.
 */

public class Topic  implements Comparable<Topic>{
    /**
     * moduleName : 国庆中秋活动1
     * moduleContent : 1、金币购买优惠活动，88元1999金币，101元2388金币，128元3000金币，168元4000金币，每个账号仅限参加一次活动，只能选择一种套餐购买(付款备注猫友圈账号，或者将猫友圈账号发给客服也行)；
     2、活动期间发布课程免金币，特殊功能免金币；
     3、新增史上最安全的付款助手软件，完全模拟人手动点击来登录健康猫付款，详情看群共享文件
     * url : https://item.taobao.com/item.htm?spm=686.1000925.0.0.18b4bb7e3359jd&id=559386553971
     * urlDesc : 点击参与活动
     * order : 1
     * enable : 1
     */

    private String moduleName;
    private String moduleContent;
    private String url;
    private String urlDesc;
    private int order;
    private int enable;

    public String getModuleName() {
      return moduleName;
    }

    public void setModuleName(String moduleName) {
      this.moduleName = moduleName;
    }

    public String getModuleContent() {
      return moduleContent;
    }

    public void setModuleContent(String moduleContent) {
      this.moduleContent = moduleContent;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }

    public String getUrlDesc() {
      return urlDesc;
    }

    public void setUrlDesc(String urlDesc) {
      this.urlDesc = urlDesc;
    }

    public int getOrder() {
      return order;
    }

    public void setOrder(int order) {
      this.order = order;
    }

    public int getEnable() {
      return enable;
    }

    public void setEnable(int enable) {
      this.enable = enable;
    }

  public boolean equals(Topic o) {
    if (!compareStr(moduleName, o.getModuleName())) {
      return false;
    }

    if (!compareStr(moduleContent, o.getModuleContent())) {
      return false;
    }

    if (!compareStr(url, o.getUrl())) {
      return false;
    }

    if (!compareStr(urlDesc, o.getUrlDesc())) {
      return false;
    }

    if (order != o.getOrder()) {
      return false;
    }

    if (enable != o.getEnable()) {
      return false;
    }
    return true;
  }

  private boolean compareStr(String str1, String str2) {
    if ((str1 == null && str2 == null) || str1 != null && str2 != null && str1.equals(str2)) {
      return true;
    } else {
      return false;
    }
  }


  @Override
  public int compareTo(@NonNull Topic o) {
    return this.order - o.getOrder();
  }
}
