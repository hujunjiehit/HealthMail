package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class ContractPositionListModel extends BaseRes {


    /**
     * data : [{"availableQuantity":0,"avgPrice":"string","leverage":0,"liquidationPrice":"string","markPrice":"string","positionMargin":"string","positionValue":"string","quantity":0,"realisedPnl":"string","roe":"string","side":"string","symbol":"string","unrealisedPnl":"string","weight":0}]
     * timezone : 0
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * availableQuantity : 0
         * avgPrice : string
         * leverage : 0
         * liquidationPrice : string
         * markPrice : string
         * positionMargin : string
         * positionValue : string
         * quantity : 0
         * realisedPnl : string
         * roe : string
         * side : string
         * symbol : string
         * unrealisedPnl : string
         * weight : 0
         */

        private String availableQuantity;
        private String avgPrice;
        private int leverage;
        private String liquidationPrice;
        private String markPrice;
        private String positionMargin;
        private String positionValue;
        private String quantity;
        private String realisedPnl;
        private String roe;
        private String side;
        private String symbol;
        private String unrealisedPnl;
        private int weight;
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMarginMode() {
            return marginMode;
        }

        public void setMarginMode(String marginMode) {
            this.marginMode = marginMode;
        }

        private String marginMode;

        public String getAvailableQuantity() {
            return availableQuantity;
        }

        public void setAvailableQuantity(String availableQuantity) {
            this.availableQuantity = availableQuantity;
        }

        public String getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(String avgPrice) {
            this.avgPrice = avgPrice;
        }

        public int getLeverage() {
            return leverage;
        }

        public void setLeverage(int leverage) {
            this.leverage = leverage;
        }

        public String getLiquidationPrice() {
            return liquidationPrice;
        }

        public void setLiquidationPrice(String liquidationPrice) {
            this.liquidationPrice = liquidationPrice;
        }

        public String getMarkPrice() {
            return markPrice;
        }

        public void setMarkPrice(String markPrice) {
            this.markPrice = markPrice;
        }

        public String getPositionMargin() {
            return positionMargin;
        }

        public void setPositionMargin(String positionMargin) {
            this.positionMargin = positionMargin;
        }

        public String getPositionValue() {
            return positionValue;
        }

        public void setPositionValue(String positionValue) {
            this.positionValue = positionValue;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
        }

        public String getRealisedPnl() {
            return realisedPnl;
        }

        public void setRealisedPnl(String realisedPnl) {
            this.realisedPnl = realisedPnl;
        }

        public String getRoe() {
            return roe;
        }

        public void setRoe(String roe) {
            this.roe = roe;
        }

        public String getSide() {
            return side;
        }

        public void setSide(String side) {
            this.side = side;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getUnrealisedPnl() {
            return unrealisedPnl;
        }

        public void setUnrealisedPnl(String unrealisedPnl) {
            this.unrealisedPnl = unrealisedPnl;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }
}
