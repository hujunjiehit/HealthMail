package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class AdModel extends BaseRes {

    /**
     * data : {"adType":1,"asset":"USDT","stock":"1200.000000","price":"11.23","minOrder":"100.00","maxOrder":"5000.00","releaseType":0,"remark":"","adId":268,"bankName":"北京银行","hasAli":1,"hasWechat":1,"name":null}
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
         * adType : 1
         * asset : USDT
         * stock : 1200.000000
         * price : 11.23
         * minOrder : 100.00
         * maxOrder : 5000.00
         * releaseType : 0
         * remark :
         * adId : 268
         * bankName : 北京银行
         * hasAli : 1
         * hasWechat : 1
         * name : null
         */

        private int adType;
        private String asset;
        private String stock;
        private String price;
        private String minOrder;
        private String maxOrder;
        private int releaseType;
        private String remark;
        private int adId;
        private String bankName;
        private int hasAli;
        private int hasWechat;
        private String name;

        public int getAdType() {
            return adType;
        }

        public void setAdType(int adType) {
            this.adType = adType;
        }

        public String getAsset() {
            return asset;
        }

        public void setAsset(String asset) {
            this.asset = asset;
        }

        public String getStock() {
            return stock;
        }

        public void setStock(String stock) {
            this.stock = stock;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getMinOrder() {
            return minOrder;
        }

        public void setMinOrder(String minOrder) {
            this.minOrder = minOrder;
        }

        public String getMaxOrder() {
            return maxOrder;
        }

        public void setMaxOrder(String maxOrder) {
            this.maxOrder = maxOrder;
        }

        public int getReleaseType() {
            return releaseType;
        }

        public void setReleaseType(int releaseType) {
            this.releaseType = releaseType;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public int getAdId() {
            return adId;
        }

        public void setAdId(int adId) {
            this.adId = adId;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public int getHasAli() {
            return hasAli;
        }

        public void setHasAli(int hasAli) {
            this.hasAli = hasAli;
        }

        public int getHasWechat() {
            return hasWechat;
        }

        public void setHasWechat(int hasWechat) {
            this.hasWechat = hasWechat;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
