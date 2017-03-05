package com.june.healthmail.model;

import java.util.List;

/**
 * Created by june on 2017/3/5.
 */

public class GuanzhuListModel {
    private int errmsg;
    private boolean succeed;
    private String MsgTime;
    private List<Guanzhu> valuse;
    private String valuseex;
    private String RequestAccessToken;
    private AccessToken accessToken;
    private String msg;
    private String Request;

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

    public List<Guanzhu> getValuse() {
        return valuse;
    }

    public void setValuse(List<Guanzhu> valuse) {
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

    public String getRequest() {
        return Request;
    }

    public void setRequest(String request) {
        Request = request;
    }
}
