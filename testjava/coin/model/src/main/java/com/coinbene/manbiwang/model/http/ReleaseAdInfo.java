package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.HashMap;

public class ReleaseAdInfo extends BaseRes {


    /**
     * data : {"marketPrice":{"BTC":"69469.40","ETH":"2129.75","USDT":"6.94"},"maxPrice":"7.06","amount":"100000000.000000"}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * marketPrice : {"BTC":"69469.40","ETH":"2129.75","USDT":"6.94"}
         * maxPrice : 7.06
         * amount : 100000000.000000
         */

        private HashMap<String, String> marketPrice;
        private String maxPrice;
        private String amount;

        public HashMap<String, String> getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(HashMap<String, String> marketPrice) {
            this.marketPrice = marketPrice;
        }

        public String getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(String maxPrice) {
            this.maxPrice = maxPrice;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
