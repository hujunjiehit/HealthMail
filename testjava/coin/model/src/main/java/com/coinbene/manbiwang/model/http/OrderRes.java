package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * 下单的返回体
 */
public class OrderRes extends BaseRes {

    /**
     * data : string
     * timezone : 0
     */

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
