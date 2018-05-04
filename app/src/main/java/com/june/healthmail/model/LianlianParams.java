package com.june.healthmail.model;

/**
 * Created by june on 2018/1/19.
 */

public class LianlianParams {


  /**
   * app_request : 3
   * busi_partner : 109001
   * dt_order : 20180119131359
   * info_order : 健康猫APP合并支付订单：M1801191313585832064
   * money_order : 1000.00
   * name_goods : 健康猫APP合并支付订单
   * no_order : M1801191313585832064
   * notify_url : http://paynotify.healthmall.cn/LlNotify
   * oid_partner : 201704171001650533
   * risk_item : {"user_info_mercht_userno":"261991398","user_info_bind_phone":"13027909110","user_info_dt_register":"20180119131359","risk_state":"1","frms_ware_category":"4001"}
   * sign : EkPp5sv5Elz3Ps8bru7hCokcQGjozNaj1bUZuP0YIF9FDTQmB%2b9dutyX%2fMTkrMnL9R70oqQyR5Bcd6LOpIBEqzjjHaK8WlBDSth0sUZ4TleSAs0RVYBRWbJJd%2bAVdNRHsZT9oA9XIAk14CkR27TYnIUhJmq1Rs4rg53iqbc6vxE%3d
   * sign_type : RSA
   * timestamp : 20180119131359
   * url_order :
   * url_return : http://paynotify.healthmall.cn/PayResult/LlH5Pay
   * user_id : 261991398
   * valid_order : 90
   * version : 1.0
   */

  private String app_request;
  private String busi_partner;
  private String dt_order;
  private String info_order;
  private String money_order;
  private String name_goods;
  private String no_order;
  private String notify_url;
  private String oid_partner;
  private String risk_item;
  private String sign;
  private String sign_type;
  private String timestamp;
  private String url_order;
  private String url_return;
  private String user_id;
  private String valid_order;
  private String version;

  public String getApp_request() {
    return app_request;
  }

  public void setApp_request(String app_request) {
    this.app_request = app_request;
  }

  public String getBusi_partner() {
    return busi_partner;
  }

  public void setBusi_partner(String busi_partner) {
    this.busi_partner = busi_partner;
  }

  public String getDt_order() {
    return dt_order;
  }

  public void setDt_order(String dt_order) {
    this.dt_order = dt_order;
  }

  public String getInfo_order() {
    return info_order;
  }

  public void setInfo_order(String info_order) {
    this.info_order = info_order;
  }

  public String getMoney_order() {
    return money_order;
  }

  public void setMoney_order(String money_order) {
    this.money_order = money_order;
  }

  public String getName_goods() {
    return name_goods;
  }

  public void setName_goods(String name_goods) {
    this.name_goods = name_goods;
  }

  public String getNo_order() {
    return no_order;
  }

  public void setNo_order(String no_order) {
    this.no_order = no_order;
  }

  public String getNotify_url() {
    return notify_url;
  }

  public void setNotify_url(String notify_url) {
    this.notify_url = notify_url;
  }

  public String getOid_partner() {
    return oid_partner;
  }

  public void setOid_partner(String oid_partner) {
    this.oid_partner = oid_partner;
  }

  public String getRisk_item() {
    return risk_item;
  }

  public void setRisk_item(String risk_item) {
    this.risk_item = risk_item;
  }

  public String getSign() {
    return sign;
  }

  public void setSign(String sign) {
    this.sign = sign;
  }

  public String getSign_type() {
    return sign_type;
  }

  public void setSign_type(String sign_type) {
    this.sign_type = sign_type;
  }

  public String getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }

  public String getUrl_order() {
    return url_order;
  }

  public void setUrl_order(String url_order) {
    this.url_order = url_order;
  }

  public String getUrl_return() {
    return url_return;
  }

  public void setUrl_return(String url_return) {
    this.url_return = url_return;
  }

  public String getUser_id() {
    return user_id;
  }

  public void setUser_id(String user_id) {
    this.user_id = user_id;
  }

  public String getValid_order() {
    return valid_order;
  }

  public void setValid_order(String valid_order) {
    this.valid_order = valid_order;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
