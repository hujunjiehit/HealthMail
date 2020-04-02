package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by zhangle on 2018/3/20.
 */

public class KlineDealMedel extends BaseRes {


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class  DataBean implements Cloneable{
        /**
         * d : string
         * p : string
         * t : 0
         * v : string
         */

        private String sn;
        private String d;
        private String p;
        private String t;
        private String v;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public String getD() {
            return d;
        }

        public void setD(String d) {
            this.d = d;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getT() {
            return t;
        }

        public void setT(String t) {
            this.t = t;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        @Override
        public Object clone(){
            KlineDealMedel.DataBean dataBean = null;
            try {
                dataBean = (KlineDealMedel.DataBean) super.clone();
            }catch (Exception CloneNotSupportedException) {

            }
            return dataBean;
        }
    }
}
