package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-05-16
 * com.coinbene.manbiwang.model.http
 */
public class ForceCalculateModel extends BaseRes {
    /**
     * message : null
     * timezone : null
     * data : 913.0
     */


    private String timezone;
    private String data;


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
