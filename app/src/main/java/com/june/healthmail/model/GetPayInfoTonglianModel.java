package com.june.healthmail.model;

/**
 * Created by bjhujunjie on 2017/3/13.
 */

public class GetPayInfoTonglianModel {
    private int errmsg;
    private boolean succeed;
    private String MsgTime;
    private PayinfoTonglianDetail valuse;
    private String valuseex;
    private String RequestAccessToken;
    private AccessToken accessToken;
    private String msg;
    private Request Request;

    public int getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(int errmsg) {
        this.errmsg = errmsg;
    }

    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getMsgTime() {
        return MsgTime;
    }

    public void setMsgTime(String msgTime) {
        MsgTime = msgTime;
    }

    public PayinfoTonglianDetail getValuse() {
        return valuse;
    }

    public void setValuse(PayinfoTonglianDetail valuse) {
        this.valuse = valuse;
    }

    public String getValuseex() {
        return valuseex;
    }

    public void setValuseex(String valuseex) {
        this.valuseex = valuseex;
    }

    public String getRequestAccessToken() {
        return RequestAccessToken;
    }

    public void setRequestAccessToken(String requestAccessToken) {
        RequestAccessToken = requestAccessToken;
    }

    public AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public com.june.healthmail.model.Request getRequest() {
        return Request;
    }

    public void setRequest(com.june.healthmail.model.Request request) {
        Request = request;
    }
}
