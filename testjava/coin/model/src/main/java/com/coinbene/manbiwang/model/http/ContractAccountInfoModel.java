package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class ContractAccountInfoModel extends BaseRes {


    /**
     * message : null
     * timezone : null
     * data : {"userId":4680,"symbol":"BTCUSDT","balance":"0.100000000000000000","frozenBalance":"0.100000000000000000","availableBalance":"0.000000000000000000","currentAsset":"-0.000306526600000000","liquidationPrice":"0.1859353363","positionMargin":"1.000001564700000000","unrealisedPnl":"-0.0003065266","roe":"-0.0306526100%","active":1}
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
         * userId : 4680
         * symbol : BTCUSDT
         * balance : 0.100000000000000000
         * frozenBalance : 0.100000000000000000
         * availableBalance : 0.000000000000000000
         * currentAsset : -0.000306526600000000
         * liquidationPrice : 0.1859353363
         * positionMargin : 1.000001564700000000
         * unrealisedPnl : -0.0003065266
         * roe : -0.0306526100%
         * active : 1
         */

        private int userId;
        private String symbol;
        private String balance;
        private String frozenBalance;
        private String availableBalance;
        private String marginBalance;
        private String liquidationPrice;
        private String positionMargin;
        private String unrealisedPnl;
        private String roe;
        private int active;
        private String longQuantity;
        private String shortQuantity;
        private String marginModel;

        public String getMarginModel() {
            return marginModel;
        }

        public void setMarginModel(String marginModel) {
            this.marginModel = marginModel;
        }

        public String getLongQuantity() {
            return longQuantity;
        }

        public void setLongQuantity(String longQuantity) {
            this.longQuantity = longQuantity;
        }

        public String getShortQuantity() {
            return shortQuantity;
        }

        public void setShortQuantity(String shortQuantity) {
            this.shortQuantity = shortQuantity;
        }

        public String getMarginBalance() {
            return marginBalance;
        }

        public void setMarginBalance(String marginBalance) {
            this.marginBalance = marginBalance;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getBalance() {
            return balance;
        }

        public void setBalance(String balance) {
            this.balance = balance;
        }

        public String getFrozenBalance() {
            return frozenBalance;
        }

        public void setFrozenBalance(String frozenBalance) {
            this.frozenBalance = frozenBalance;
        }

        public String getAvailableBalance() {
            return availableBalance;
        }

        public void setAvailableBalance(String availableBalance) {
            this.availableBalance = availableBalance;
        }

        public String getLiquidationPrice() {
            return liquidationPrice;
        }

        public void setLiquidationPrice(String liquidationPrice) {
            this.liquidationPrice = liquidationPrice;
        }

        public String getPositionMargin() {
            return positionMargin;
        }

        public void setPositionMargin(String positionMargin) {
            this.positionMargin = positionMargin;
        }

        public String getUnrealisedPnl() {
            return unrealisedPnl;
        }

        public void setUnrealisedPnl(String unrealisedPnl) {
            this.unrealisedPnl = unrealisedPnl;
        }

        public String getRoe() {
            return roe;
        }

        public void setRoe(String roe) {
            this.roe = roe;
        }

        public int getActive() {
            return active;
        }

        public void setActive(int active) {
            this.active = active;
        }
    }
}
