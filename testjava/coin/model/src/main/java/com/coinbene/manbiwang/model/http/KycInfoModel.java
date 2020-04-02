package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class KycInfoModel extends BaseRes {


    /**
     * data : {"name":"hy","idCardHold":"http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179071528425075497back.jpg","nationEn":"China","type":1,"error":"1、test    \\n2、67hgh","idNumber":"430802198909180837","idCardUp":"http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179051528425075494front.jpg","idCardDown":"http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179061528425075497back.jpg","nationZh":"中国","status":9}
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
         * name : hy
         * idCardHold : http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179071528425075497back.jpg
         * nationEn : China
         * type : 1
         * error : 1、test    \n2、67hgh
         * idNumber : 430802198909180837
         * idCardUp : http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179051528425075494front.jpg
         * idCardDown : http://res-bj.oss-cn-beijing.aliyuncs.com/coinbene-idcard/1179061528425075497back.jpg
         * nationZh : 中国
         * status : 9
         */

        private String name;
        private String idCardHold;
        private String nationEn;
        private int type;
//        private List<String> error;
        private String error;
        private String idNumber;
        private String idCardUp;
        private String idCardDown;
        private String nationZh;
        private int status;
        private String country;//CN

        private String countryName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIdCardHold() {
            return idCardHold;
        }

        public void setIdCardHold(String idCardHold) {
            this.idCardHold = idCardHold;
        }

        public String getNationEn() {
            return nationEn;
        }

        public void setNationEn(String nationEn) {
            this.nationEn = nationEn;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getIdNumber() {
            return idNumber;
        }

        public void setIdNumber(String idNumber) {
            this.idNumber = idNumber;
        }

        public String getIdCardUp() {
            return idCardUp;
        }

        public void setIdCardUp(String idCardUp) {
            this.idCardUp = idCardUp;
        }

        public String getIdCardDown() {
            return idCardDown;
        }

        public void setIdCardDown(String idCardDown) {
            this.idCardDown = idCardDown;
        }

        public String getNationZh() {
            return nationZh;
        }

        public void setNationZh(String nationZh) {
            this.nationZh = nationZh;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }
}
