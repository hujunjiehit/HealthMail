package com.coinbene.manbiwang.kline.bean;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by zhangle on 2018/3/8.
 */

public class KlineData extends BaseRes{


    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * t : 1509699600
         * o : 0.3
         * c : 0.3
         * l : 0.3
         * h : 0.3
         * v : 0
         */

        private String t;
        private float o;
        private float c;
        private float l;
        private float h;
        private String v;
        private float m;//标记价格

        public String getT() {
            return t;
        }

        public void setT(String t) {
            this.t = t;
        }

        public float getO() {
            return o;
        }

        public void setO(float o) {
            this.o = o;
        }

        public float getC() {
            return c;
        }

        public void setC(float c) {
            this.c = c;
        }

        public float getL() {
            return l;
        }

        public void setL(float l) {
            this.l = l;
        }

        public float getH() {
            return h;
        }

        public void setH(float h) {
            this.h = h;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public float getM() {
            return m;
        }

        public void setM(float m) {
            this.m = m;
        }
    }
}
