package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-05-17
 * com.coinbene.manbiwang.model.http
 */
public class CalculatePLModel extends BaseRes {

    /**
     * message : null
     * timezone : null
     * data : {"margin":"0.0625","profit":"-0.0032","roe":"-0.0512"}
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
         * margin : 0.0625
         * profit : -0.0032
         * roe : -0.0512
         */

        private String margin;
        private String profit;
        private String roe;

        public String getMargin() {
            return margin;
        }

        public void setMargin(String margin) {
            this.margin = margin;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getRoe() {
            return roe;
        }

        public void setRoe(String roe) {
            this.roe = roe;
        }
    }
}
