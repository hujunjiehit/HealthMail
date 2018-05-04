package com.june.healthmail.model;

/**
 * Created by june on 2017/3/15.
 */

public class PayinfoLianlianDetail {

    /**
     * requestUrl : https://wap.lianlianpay.com/authpay_special.htm
     * requestParam : req_data={"app_request":"3","busi_partner":"109001","dt_order":"20180119131359",
     * "info_order":"健康猫APP合并支付订单：M1801191313585832064","money_order":"1000.00","name_goods":"健康猫APP合并支付订单",
     * "no_order":"M1801191313585832064","notify_url":"http://paynotify.healthmall.cn/LlNotify","oid_partner":
     * "201704171001650533","risk_item":"{\"user_info_mercht_userno\":\"261991398\",\"user_info_bind_phone\":
     * \"13027909110\",\"user_info_dt_register\":\"20180119131359\",\"risk_state\":\"1\",\"frms_ware_category\":
     * \"4001\"}","sign":"EkPp5sv5Elz3Ps8bru7hCokcQGjozNaj1bUZuP0YIF9FDTQmB%2b9dutyX%2fMTkrMnL9R70oqQyR5Bcd6LOpIBEqzjjHaK8WlBDSth0sUZ4TleSAs0RVYBRWbJJd%2bAVdNRHsZT
     * 9oA9XIAk14CkR27TYnIUhJmq1Rs4rg53iqbc6vxE%3d","sign_type":"RSA","timestamp":"20180119131359",
     * "url_order":"","url_return":"http://paynotify.healthmall.cn/PayResult/LlH5Pay","user_id":"261991398",
     * "valid_order":"90","version":"1.0"}
     */

    private String requestUrl;
    private String requestParam;

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestParam() {
        return requestParam;
    }

    public void setRequestParam(String requestParam) {
        this.requestParam = requestParam;
    }
}
