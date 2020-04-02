package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class FundingTimeModel extends BaseRes {


    /**
     * data : {"currentTime":1550565229,"fundingTime":1550577600}
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
         * currentTime : 1550565229
         * fundingTime : 1550577600
         */

        private long currentTime;
        private long fundingTime;

        public long getCurrentTime() {
            return currentTime;
        }

        public void setCurrentTime(long currentTime) {
            this.currentTime = currentTime;
        }

        public long getFundingTime() {
            return fundingTime;
        }

        public void setFundingTime(long fundingTime) {
            this.fundingTime = fundingTime;
        }
    }
}
