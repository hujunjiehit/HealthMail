package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * ding
 * 2019-05-16
 * com.coinbene.manbiwang.model.http
 */
public class MarginInfoModel extends BaseRes {
    /**
     * code : 0
     * data : {"avgPrice":"string","margin":"string","maxAdd":"string","maxSub":"string","positionId":0,"quatity":0,"serviceFee":"string","side":"string","symbol":"string","unfrozenBalance":"string"}
     * message : string
     * timezone : 0
     */

    private DataBean data;
    private String timezone;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public static class DataBean {
        /**
         * avgPrice : string
         * margin : string
         * maxAdd : string
         * maxSub : string
         * positionId : 0
         * quatity : 0
         * serviceFee : string
         * side : string
         * symbol : string
         * unfrozenBalance : string
         */

        private String avgPrice;
        private String margin;
        private String maxAdd;
        private String maxSub;
        private int positionId;
        private int quatity;
        private String serviceFee;
        private String side;
        private String symbol;
        private String unfrozenBalance;

        public String getAvgPrice() {
            return avgPrice;
        }

        public void setAvgPrice(String avgPrice) {
            this.avgPrice = avgPrice;
        }

        public String getMargin() {
            return margin;
        }

        public void setMargin(String margin) {
            this.margin = margin;
        }

        public String getMaxAdd() {
            return maxAdd;
        }

        public void setMaxAdd(String maxAdd) {
            this.maxAdd = maxAdd;
        }

        public String getMaxSub() {
            return maxSub;
        }

        public void setMaxSub(String maxSub) {
            this.maxSub = maxSub;
        }

        public int getPositionId() {
            return positionId;
        }

        public void setPositionId(int positionId) {
            this.positionId = positionId;
        }

        public int getQuatity() {
            return quatity;
        }

        public void setQuatity(int quatity) {
            this.quatity = quatity;
        }

        public String getServiceFee() {
            return serviceFee;
        }

        public void setServiceFee(String serviceFee) {
            this.serviceFee = serviceFee;
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

        public String getUnfrozenBalance() {
            return unfrozenBalance;
        }

        public void setUnfrozenBalance(String unfrozenBalance) {
            this.unfrozenBalance = unfrozenBalance;
        }
    }
}
