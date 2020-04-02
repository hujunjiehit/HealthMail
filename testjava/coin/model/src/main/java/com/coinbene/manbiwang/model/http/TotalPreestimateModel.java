package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by mxd on 2019/3/5.
 */

public class TotalPreestimateModel extends BaseRes {


    /**
     * data : {"btcTotalPreestimate":"string","currencyCode":"string","currencySymbol":"string","localTotalPreestimate":"string"}
     * timezone : 0
     */

    private DataBean data;
    private int timezone;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public static class DataBean {
        /**
         * btcTotalPreestimate : string
         * currencyCode : string
         * currencySymbol : string
         * localTotalPreestimate : string
         */

        private String btcTotalPreestimate;
        private String currencyCode;
        private String currencySymbol;
        private String localTotalPreestimate;

        public String getBtcTotalPreestimate() {
            return btcTotalPreestimate;
        }

        public void setBtcTotalPreestimate(String btcTotalPreestimate) {
            this.btcTotalPreestimate = btcTotalPreestimate;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public void setCurrencyCode(String currencyCode) {
            this.currencyCode = currencyCode;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public String getLocalTotalPreestimate() {
            return localTotalPreestimate;
        }

        public void setLocalTotalPreestimate(String localTotalPreestimate) {
            this.localTotalPreestimate = localTotalPreestimate;
        }
    }
}
