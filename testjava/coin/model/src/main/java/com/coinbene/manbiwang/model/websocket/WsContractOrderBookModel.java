package com.coinbene.manbiwang.model.websocket;

import java.util.List;

public class WsContractOrderBookModel extends WsBaseResponse {


    /**
     * ts : 1550633750878
     * data : {"bids":[],"asks":[],"quote":{"mp":"0.0000","n":"0.0000"}}
     */

    private DataBean data;
    private boolean full;

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bids : []
         * asks : []
         * quote : {"mp":"0.0000","n":"0.0000"}
         */

        private QuoteBean quote;
        // websocket 卖   http 买
        public List<String[]> asks;
        // websocket 买   http 卖
        public List<String[]> bids;

        public QuoteBean getQuote() {
            return quote;
        }

        public void setQuote(QuoteBean quote) {
            this.quote = quote;
        }

        public List<String[]> getAsks() {
            return asks;
        }

        public void setAsks(List<String[]> asks) {
            this.asks = asks;
        }

        public List<String[]> getBids() {
            return bids;
        }

        public void setBids(List<String[]> bids) {
            this.bids = bids;
        }

        public static class QuoteBean {
            /**
             * mp : 0.0000
             * n : 0.0000
             */

            private String mp;
            private String n;

            public String getMp() {
                return mp;
            }

            public void setMp(String mp) {
                this.mp = mp;
            }

            public String getN() {
                return n;
            }

            public void setN(String n) {
                this.n = n;
            }
        }
    }
}
