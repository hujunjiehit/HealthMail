package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class SerialnumModel extends BaseRes {

    /**
     * message : null
     * timezone : null
     * data : {"serialnum":"f1c5c2ae093c4bc78a94350c54248e5f"}
     */

    private DataBean data;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    private String timezone;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * serialnum : f1c5c2ae093c4bc78a94350c54248e5f
         */

        private String serialnum;

        public String getSerialnum() {
            return serialnum;
        }

        public void setSerialnum(String serialnum) {
            this.serialnum = serialnum;
        }
    }
}
