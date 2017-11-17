package com.june.healthmail.http.bean;

import java.util.List;

/**
 * Created by june on 2017/11/16.
 */

public class GetModuleConfigBean {

  /**
   * status : ok
   * ret : [{"moduleName":"国庆中秋活动1","moduleContent":"1、金币购买优惠活动，88元1999金币，101元2388金币，128元3000金币，168元4000金币，每个账号仅限参加一次活动，只能选择一种套餐购买(付款备注猫友圈账号，或者将猫友圈账号发给客服也行)；\n2、活动期间发布课程免金币，特殊功能免金币；\n3、新增史上最安全的付款助手软件，完全模拟人手动点击来登录健康猫付款，详情看群共享文件","url":"https://item.taobao.com/item.htm?spm=686.1000925.0.0.18b4bb7e3359jd&id=559386553971","urlDesc":"点击参与活动","order":1,"enable":1},{"moduleName":"国庆中秋活动2","moduleContent":"1、金币购买优惠活动，88元1999金币，101元2388金币，128元3000金币，168元4000金币，每个账号仅限参加一次活动，只能选择一种套餐购买(付款备注猫友圈账号，或者将猫友圈账号发给客服也行)；\n2、活动期间发布课程免金币，特殊功能免金币；\n3、新增史上最安全的付款助手软件，完全模拟人手动点击来登录健康猫付款，详情看群共享文件","url":"https://item.taobao.com/item.htm?spm=686.1000925.0.0.18b4bb7e3359jd&id=559386553971","urlDesc":"点击参与活动","order":2,"enable":1},{"moduleName":"国庆中秋活动3","moduleContent":"1、金币购买优惠活动，88元1999金币，101元2388金币，128元3000金币，168元4000金币，每个账号仅限参加一次活动，只能选择一种套餐购买(付款备注猫友圈账号，或者将猫友圈账号发给客服也行)；\n2、活动期间发布课程免金币，特殊功能免金币；\n3、新增史上最安全的付款助手软件，完全模拟人手动点击来登录健康猫付款，详情看群共享文件","url":"https://item.taobao.com/item.htm?spm=686.1000925.0.0.18b4bb7e3359jd&id=559386553971","urlDesc":"点击参与活动","order":3,"enable":1}]
   */
  private String status;
  private List<Topic> ret;

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public List<Topic> getRet() {
    return ret;
  }

  public void setRet(List<Topic> ret) {
    this.ret = ret;
  }
}
