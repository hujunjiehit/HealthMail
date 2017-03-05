package com.june.healthmail.model;

/**
 * Created by june on 2017/3/5.
 */

public class PingjiaModel {

    private String errmsg;
    private Boolean succeed;
    private String MsgTime;

    public Boolean getSucceed() {
        return succeed;
    }

    public String getMsgTime() {
        return MsgTime;
    }

    public void setMsgTime(String msgTime) {
        MsgTime = msgTime;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(Boolean succeed) {
        this.succeed = succeed;
    }
}
