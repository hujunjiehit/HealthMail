package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class ProfitPercentMode extends BaseRes {
    /**
     * message : null
     * code : 200
     * timezone : null
     * data : {"userId":10000328,"currency":"","profit":"","profitPercent":"","time":"1555985100521"}
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
         * userId : 10000328
         * currency :
         * profit :
         * profitPercent :
         * time : 1555985100521
         */

        private int userId;
        private String currency;
        private String profit;
        private String profitPercent;
        private String time;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getProfit() {
            return profit;
        }

        public void setProfit(String profit) {
            this.profit = profit;
        }

        public String getProfitPercent() {
            return profitPercent;
        }

        public void setProfitPercent(String profitPercent) {
            this.profitPercent = profitPercent;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
