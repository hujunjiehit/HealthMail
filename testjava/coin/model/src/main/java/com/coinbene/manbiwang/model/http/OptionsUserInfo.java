package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class OptionsUserInfo extends BaseRes {


    /**
     * message : null
     * timezone : null
     * data : {"fotauid":"2509642342249006080","first":true,"token":"t9jj5a7lzl"}
     */


    private String timezone;
    private DataBean data;


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * fotauid : 2509642342249006080
         * first : true
         * token : t9jj5a7lzl
         */

        private String fotauid;
        private boolean first;
        private String token;

        public String getFotauid() {
            return fotauid;
        }

        public void setFotauid(String fotauid) {
            this.fotauid = fotauid;
        }

        public boolean isFirst() {
            return first;
        }

        public void setFirst(boolean first) {
            this.first = first;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
