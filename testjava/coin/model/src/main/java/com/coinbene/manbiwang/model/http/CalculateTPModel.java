package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-06-03
 * com.coinbene.manbiwang.model.http
 */
public class CalculateTPModel extends BaseRes {

    /**
     * message : null
     * timezone : null
     * data : {"targetPrice":"666.5"}
     */


    private String timezone;
    private DataBean data;


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * targetPrice : 666.5
         */

        private String targetPrice;

        public String getTargetPrice() {
            return targetPrice;
        }

        public void setTargetPrice(String targetPrice) {
            this.targetPrice = targetPrice;
        }
    }
}
