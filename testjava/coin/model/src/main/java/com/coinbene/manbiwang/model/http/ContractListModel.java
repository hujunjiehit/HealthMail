package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class ContractListModel extends BaseRes {


    /**
     * data : {"list":[{"name":"BTCUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"100000","minPriceChange":"0.5","precision":1,"sort":0},{"name":"ETHUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"1000000","minPriceChange":"0.005","precision":4,"sort":0},{"name":"EOSUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"1000000","minPriceChange":"0.005","precision":4,"sort":0}],"config":{"makerFeeRate":"-0.003","takerFeeRate":"0.006","showPrecision":"4","maintainMarginRate":"0.05","leverages":"2, 3, 5, 10, 20, 40, 50, 100","marketOrderFloatRate":"0.1","maxOrder":"50"}}
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
         * list : [{"name":"BTCUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"100000","minPriceChange":"0.5","precision":1,"sort":0},{"name":"ETHUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"1000000","minPriceChange":"0.005","precision":4,"sort":0},{"name":"EOSUSDT","multiplier":"0.0001","minTradeAmount":"1","maxTradeAmount":"1000000","minPriceChange":"0.005","precision":4,"sort":0}]
         * config : {"makerFeeRate":"-0.003","takerFeeRate":"0.006","showPrecision":"4","maintainMarginRate":"0.05","leverages":"2, 3, 5, 10, 20, 40, 50, 100","marketOrderFloatRate":"0.1","maxOrder":"50"}
         */

        private ConfigBean config;
        private List<ListBean> list;

        public ConfigBean getConfig() {
            return config;
        }

        public void setConfig(ConfigBean config) {
            this.config = config;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ConfigBean {
            /**
             * makerFeeRate : -0.003
             * takerFeeRate : 0.006
             * showPrecision : 4
             * maintainMarginRate : 0.05
             * leverages : 2, 3, 5, 10, 20, 40, 50, 100
             * marketOrderFloatRate : 0.1
             * maxOrder : 50
             */

            private String makerFeeRate;
            private String takerFeeRate;
            private String showPrecision;
            private String maintainMarginRate;
            private String leverages;
            private String marketOrderFloatRate;
            private String maxOrder;

            public String getMakerFeeRate() {
                return makerFeeRate;
            }

            public void setMakerFeeRate(String makerFeeRate) {
                this.makerFeeRate = makerFeeRate;
            }

            public String getTakerFeeRate() {
                return takerFeeRate;
            }

            public void setTakerFeeRate(String takerFeeRate) {
                this.takerFeeRate = takerFeeRate;
            }

            public String getShowPrecision() {
                return showPrecision;
            }

            public void setShowPrecision(String showPrecision) {
                this.showPrecision = showPrecision;
            }

            public String getMaintainMarginRate() {
                return maintainMarginRate;
            }

            public void setMaintainMarginRate(String maintainMarginRate) {
                this.maintainMarginRate = maintainMarginRate;
            }

            public String getLeverages() {
                return leverages;
            }

            public void setLeverages(String leverages) {
                this.leverages = leverages;
            }

            public String getMarketOrderFloatRate() {
                return marketOrderFloatRate;
            }

            public void setMarketOrderFloatRate(String marketOrderFloatRate) {
                this.marketOrderFloatRate = marketOrderFloatRate;
            }

            public String getMaxOrder() {
                return maxOrder;
            }

            public void setMaxOrder(String maxOrder) {
                this.maxOrder = maxOrder;
            }
        }

        public static class ListBean {
            /**
             * name : BTCUSDT
             * multiplier : 0.0001
             * minTradeAmount : 1
             * maxTradeAmount : 100000
             * minPriceChange : 0.5
             * precision : 1
             * sort : 0
             */

            private String symbol;
            private String multiplier;
            private String baseAsset;
            private String quoteAsset;
            private String minTradeAmount;
            private String maxTradeAmount;
            private String minPriceChange;
            private String costPriceMultiplier;
            private int precision;

            private String leverages;

            public String getLeverages() {
                return leverages;
            }

            public void setLeverages(String leverages) {
                this.leverages = leverages;
            }

            public String getCostPriceMultiplier() {
                return costPriceMultiplier;
            }

            public void setCostPriceMultiplier(String costPriceMultiplier) {
                this.costPriceMultiplier = costPriceMultiplier;
            }

            public String getBaseAsset() {
                return baseAsset;
            }

            public void setBaseAsset(String baseAsset) {
                this.baseAsset = baseAsset;
            }

            public String getQuoteAsset() {
                return quoteAsset;
            }

            public void setQuoteAsset(String quoteAsset) {
                this.quoteAsset = quoteAsset;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getMultiplier() {
                return multiplier;
            }

            public void setMultiplier(String multiplier) {
                this.multiplier = multiplier;
            }

            public String getMinTradeAmount() {
                return minTradeAmount;
            }

            public void setMinTradeAmount(String minTradeAmount) {
                this.minTradeAmount = minTradeAmount;
            }

            public String getMaxTradeAmount() {
                return maxTradeAmount;
            }

            public void setMaxTradeAmount(String maxTradeAmount) {
                this.maxTradeAmount = maxTradeAmount;
            }

            public String getMinPriceChange() {
                return minPriceChange;
            }

            public void setMinPriceChange(String minPriceChange) {
                this.minPriceChange = minPriceChange;
            }

            public int getPrecision() {
                return precision;
            }

            public void setPrecision(int precision) {
                this.precision = precision;
            }

        }
    }
}
