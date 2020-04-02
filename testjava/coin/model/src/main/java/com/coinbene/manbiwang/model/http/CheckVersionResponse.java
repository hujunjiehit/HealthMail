package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 */

public class CheckVersionResponse extends BaseRes {

    /**
     * data : {"des":"string","downUrl":"string","forcedUpdate":true}
     * timezone : 0
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
         * des : string
         * downUrl : string
         * forcedUpdate : true
         */

        private String des;
        private String downUrl;
        private boolean forcedUpdate;

        public String getDes() {
            return des;
        }

        public void setDes(String des) {
            this.des = des;
        }

        public String getDownUrl() {
            return downUrl;
        }

        public void setDownUrl(String downUrl) {
            this.downUrl = downUrl;
        }

        public boolean isForcedUpdate() {
            return forcedUpdate;
        }

        public void setForcedUpdate(boolean forcedUpdate) {
            this.forcedUpdate = forcedUpdate;
        }
    }
}
