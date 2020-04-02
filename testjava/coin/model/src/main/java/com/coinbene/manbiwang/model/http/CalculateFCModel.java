package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-05-20
 * com.coinbene.manbiwang.model.http
 */
public class CalculateFCModel extends BaseRes {
    /**
     * message : null
     * timezone : null
     * data : {"liquidationPrice":"99.5"}
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
         * liquidationPrice : 99.5
         */

        private String liquidationPrice;

        public String getLiquidationPrice() {
            return liquidationPrice;
        }

        public void setLiquidationPrice(String liquidationPrice) {
            this.liquidationPrice = liquidationPrice;
        }
    }
}
