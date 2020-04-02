package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class OptionsRecordMode extends BaseRes {
    /**
     * message : null
     * timezone : null
     * data : {"pageNum":1,"total":10,"pageSize":10,"list":[{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879810000,"settlementTime":1554879840000,"price":"12.00","profitOrLoss":null,"profit":"-12.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879803500,"settlementTime":1554879840000,"price":"1.00","profitOrLoss":null,"profit":"-1.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879290500,"settlementTime":1554879360000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879160500,"settlementTime":1554879240000,"price":"900.00","profitOrLoss":null,"profit":"918.00","finalValue":"1818.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879156000,"settlementTime":1554879180000,"price":"900.00","profitOrLoss":null,"profit":"858.69","finalValue":"1758.69"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879152500,"settlementTime":1554879180000,"price":"900.00","profitOrLoss":null,"profit":"867.96","finalValue":"1767.96"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052500,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052500,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052000,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052000,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"}]}
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
         * pageNum : 1
         * total : 10
         * pageSize : 10
         * list : [{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879810000,"settlementTime":1554879840000,"price":"12.00","profitOrLoss":null,"profit":"-12.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879803500,"settlementTime":1554879840000,"price":"1.00","profitOrLoss":null,"profit":"-1.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879290500,"settlementTime":1554879360000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879160500,"settlementTime":1554879240000,"price":"900.00","profitOrLoss":null,"profit":"918.00","finalValue":"1818.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879156000,"settlementTime":1554879180000,"price":"900.00","profitOrLoss":null,"profit":"858.69","finalValue":"1758.69"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879152500,"settlementTime":1554879180000,"price":"900.00","profitOrLoss":null,"profit":"867.96","finalValue":"1767.96"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052500,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052500,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052000,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"},{"assetId":"999","asset":"VFOTA","underlying":"BTC/USD","tradingTime":1554879052000,"settlementTime":1554879120000,"price":"900.00","profitOrLoss":null,"profit":"-900.00","finalValue":"0.0"}]
         */

        private int pageNum;
        private int total;
        private int pageSize;
        private List<ListBean> list;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * assetId : 999
             * asset : VFOTA
             * underlying : BTC/USD
             * tradingTime : 1554879810000
             * settlementTime : 1554879840000
             * price : 12.00
             * profitOrLoss : null
             * profit : -12.00
             * finalValue : 0.0
             */

            private String assetId;
            private String asset;
            private String underlying;
            private long tradingTime;
            private long settlementTime;
            private String price;
            private Object profitOrLoss;
            private String profit;
            private String finalValue;

            public String getAssetId() {
                return assetId;
            }

            public void setAssetId(String assetId) {
                this.assetId = assetId;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public String getUnderlying() {
                return underlying;
            }

            public void setUnderlying(String underlying) {
                this.underlying = underlying;
            }

            public long getTradingTime() {
                return tradingTime;
            }

            public void setTradingTime(long tradingTime) {
                this.tradingTime = tradingTime;
            }

            public long getSettlementTime() {
                return settlementTime;
            }

            public void setSettlementTime(long settlementTime) {
                this.settlementTime = settlementTime;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public Object getProfitOrLoss() {
                return profitOrLoss;
            }

            public void setProfitOrLoss(Object profitOrLoss) {
                this.profitOrLoss = profitOrLoss;
            }

            public String getProfit() {
                return profit;
            }

            public void setProfit(String profit) {
                this.profit = profit;
            }

            public String getFinalValue() {
                return finalValue;
            }

            public void setFinalValue(String finalValue) {
                this.finalValue = finalValue;
            }
        }
    }
}
