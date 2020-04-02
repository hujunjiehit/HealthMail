package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class OptionBalacneModel extends BaseRes {

    /**
     * message : null
     * timezone : null
     * data : [{"brokerId":"14","assetId":2,"assetName":"BTC","amount":"7.1998763200000000","sort":0}]
     */


    private String timezone;
    private List<DataBean> data;


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * brokerId : 14
         * assetId : 2
         * assetName : BTC
         * amount : 7.1998763200000000
         * sort : 0
         */

        private String brokerId;
        private int assetId;
        private String assetName;
        private String amount;
        private int sort;

        public String getBrokerId() {
            return brokerId;
        }

        public void setBrokerId(String brokerId) {
            this.brokerId = brokerId;
        }

        public int getAssetId() {
            return assetId;
        }

        public void setAssetId(int assetId) {
            this.assetId = assetId;
        }

        public String getAssetName() {
            return assetName;
        }

        public void setAssetName(String assetName) {
            this.assetName = assetName;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
