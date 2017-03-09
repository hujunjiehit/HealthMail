package com.june.healthmail.model;

/**
 * Created by june on 2017/3/5.
 */

public class PingjiaModel {

    private String errmsg;
    private boolean succeed;
    private String MsgTime;

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
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
}
