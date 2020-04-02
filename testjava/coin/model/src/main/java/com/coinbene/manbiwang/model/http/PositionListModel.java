package com.coinbene.manbiwang.model.http;//package com.coinbene.manbiwang.model;
//
//import com.coinbene.manbiwang.model.base.BaseRes;
//
//import java.util.List;
//
///**
// * 持仓列表的model
// */
//public class PositionListModel extends BaseRes {
//
//
//    /**
//     * data : [{"avaliableQuantity":0,"avgPrice":"string","leverage":0,"liquidationPrice":"string","markPrice":"string","positionMargin":"string","positionValue":"string","quantity":0,"realisedPnl":"string","roe":"string","side":"string","symbol":"string","unrealisedPnl":"string","weight":0}]
//     * timezone : 0
//     */
//
//    private List<DataBean> data;
//
//    public List<DataBean> getData() {
//        return data;
//    }
//
//    public void setData(List<DataBean> data) {
//        this.data = data;
//    }
//
//    public static class DataBean {
//        /**
//         * avaliableQuantity : 0
//         * avgPrice : string
//         * leverage : 0
//         * liquidationPrice : string
//         * markPrice : string
//         * positionMargin : string
//         * positionValue : string
//         * quantity : 0
//         * realisedPnl : string
//         * roe : string
//         * side : string
//         * symbol : string
//         * unrealisedPnl : string
//         * weight : 0
//         */
//
//        private int avaliableQuantity;
//        private String avgPrice;
//        private int leverage;
//        private String liquidationPrice;
//        private String markPrice;
//        private String positionMargin;
//        private String positionValue;
//        private int quantity;
//        private String realisedPnl;
//        private String roe;
//        private String side;
//        private String symbol;
//        private String unrealisedPnl;
//        private int weight;
//
//        public int getAvaliableQuantity() {
//            return avaliableQuantity;
//        }
//
//        public void setAvaliableQuantity(int avaliableQuantity) {
//            this.avaliableQuantity = avaliableQuantity;
//        }
//
//        public String getAvgPrice() {
//            return avgPrice;
//        }
//
//        public void setAvgPrice(String avgPrice) {
//            this.avgPrice = avgPrice;
//        }
//
//        public int getLeverage() {
//            return leverage;
//        }
//
//        public void setLeverage(int leverage) {
//            this.leverage = leverage;
//        }
//
//        public String getLiquidationPrice() {
//            return liquidationPrice;
//        }
//
//        public void setLiquidationPrice(String liquidationPrice) {
//            this.liquidationPrice = liquidationPrice;
//        }
//
//        public String getMarkPrice() {
//            return markPrice;
//        }
//
//        public void setMarkPrice(String markPrice) {
//            this.markPrice = markPrice;
//        }
//
//        public String getPositionMargin() {
//            return positionMargin;
//        }
//
//        public void setPositionMargin(String positionMargin) {
//            this.positionMargin = positionMargin;
//        }
//
//        public String getPositionValue() {
//            return positionValue;
//        }
//
//        public void setPositionValue(String positionValue) {
//            this.positionValue = positionValue;
//        }
//
//        public int getQuantity() {
//            return quantity;
//        }
//
//        public void setQuantity(int quantity) {
//            this.quantity = quantity;
//        }
//
//        public String getRealisedPnl() {
//            return realisedPnl;
//        }
//
//        public void setRealisedPnl(String realisedPnl) {
//            this.realisedPnl = realisedPnl;
//        }
//
//        public String getRoe() {
//            return roe;
//        }
//
//        public void setRoe(String roe) {
//            this.roe = roe;
//        }
//
//        public String getSide() {
//            return side;
//        }
//
//        public void setSide(String side) {
//            this.side = side;
//        }
//
//        public String getSymbol() {
//            return symbol;
//        }
//
//        public void setSymbol(String symbol) {
//            this.symbol = symbol;
//        }
//
//        public String getUnrealisedPnl() {
//            return unrealisedPnl;
//        }
//
//        public void setUnrealisedPnl(String unrealisedPnl) {
//            this.unrealisedPnl = unrealisedPnl;
//        }
//
//        public int getWeight() {
//            return weight;
//        }
//
//        public void setWeight(int weight) {
//            this.weight = weight;
//        }
//    }
//}
