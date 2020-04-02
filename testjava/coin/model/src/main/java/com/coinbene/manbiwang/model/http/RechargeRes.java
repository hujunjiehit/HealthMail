package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by mengxiangdong on 2017/11/26.
 * 查询充值地址返回
 */

public class RechargeRes extends BaseRes {

    /**
     * data : {"address":"string","tag":"string"}
     * timezone : 0
     */

    public DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * address : string
         * tag : string
         */

        public String address;
        public String tag;

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }
}
