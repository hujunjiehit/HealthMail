package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class FavoriteListModel extends BaseRes{


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * site : MAIN
         * tradePair : BTCUSDT
         * tradePairName : BTC/USDT
         */

        private String site;
        private String tradePair;
        private String tradePairName;
        private int sort;//排序，倒序排列

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public String getTradePair() {
            return tradePair;
        }

        public void setTradePair(String tradePair) {
            this.tradePair = tradePair;
        }

        public String getTradePairName() {
            return tradePairName;
        }

        public void setTradePairName(String tradePairName) {
            this.tradePairName = tradePairName;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
