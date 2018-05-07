package com.june.healthmail.model;

/**
 * Created by june on 2017/3/15.
 */

public class PayinfoYinshengbaoDetail {


    /**
     * requestUrl : http://114.80.54.74:8082/quickpay-front/quickPayWap/prePay
     * requestParam : accountId=2120180112114839001&customerId=261991398&orderNo=M18050615355238045821226&commodityName=健康猫APP合并支付订单&amount=1900.00&responseUrl=http://paynotify.healthmall.cn/YsbNotify&pageResponseUrl=http://paynotify.healthmall.cn/PayResult/YsbPay&mac=1A5E4C24EF5C52B2B9B4CF5F3F405976
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
