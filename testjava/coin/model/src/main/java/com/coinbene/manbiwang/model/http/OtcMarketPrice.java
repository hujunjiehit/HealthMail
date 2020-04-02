package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class OtcMarketPrice extends BaseRes {


    /**
     * data : {"marketPrice":{"BTC":"74135.06","ETH":"2114.97","USDT":"6.93"}}
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
         * marketPrice : {"BTC":"74135.06","ETH":"2114.97","USDT":"6.93"}
         */

        private MarketPriceBean marketPrice;

        public MarketPriceBean getMarketPrice() {
            return marketPrice;
        }

        public void setMarketPrice(MarketPriceBean marketPrice) {
            this.marketPrice = marketPrice;
        }

        public static class MarketPriceBean {
            /**
             * BTC : 74135.06
             * ETH : 2114.97
             * USDT : 6.93
             */

            private String BTC;
            private String ETH;
            private String USDT;

            public String getBTC() {
                return BTC;
            }

            public void setBTC(String BTC) {
                this.BTC = BTC;
            }

            public String getETH() {
                return ETH;
            }

            public void setETH(String ETH) {
                this.ETH = ETH;
            }

            public String getUSDT() {
                return USDT;
            }

            public void setUSDT(String USDT) {
                this.USDT = USDT;
            }
        }
    }
}
