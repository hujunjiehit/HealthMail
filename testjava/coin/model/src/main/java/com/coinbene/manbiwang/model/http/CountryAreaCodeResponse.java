package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 */

public class CountryAreaCodeResponse extends BaseRes {

    public List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }


    public static class DataBean {
        public String label;
        public List<Country> country;
    }

    public static class Country {
        public String code;
        public String name;
        public String areaCode;
    }
}
