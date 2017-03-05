package com.june.healthmail.model;

/**
 * Created by june on 2017/3/4.
 */

public class TokenModel {
    private int code;
    private String msg;
    private TokenData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public TokenData getData() {
        return data;
    }

    public void setData(TokenData data) {
        this.data = data;
    }
}
