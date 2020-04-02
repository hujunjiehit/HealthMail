package com.coinbene.manbiwang.model.websocket;

import java.util.List;

public class WsContractQouteResponse extends WsBaseResponse {


    /**
     * ts : 1550494205623
     * data : [{"s":"BTCUSDT","mp":"0.0","n":"10.0","f8":"0.0","v24":"","h":"","l":"","p":""},{"s":"ETHUSDT","mp":"0.0000","n":"0.0000","f8":"0.0000","v24":"","h":"","l":"","p":""},{"s":"EOSUSDT","mp":"0.0000","n":"0.0000","f8":"0.0000","v24":"","h":"","l":"","p":""}]
     */

    private List<DataBean> data;
    private boolean full;

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * s : BTCUSDT
         * mp : 0.0
         * n : 10.0
         * f8 : 0.0
         * v24 :
         * h :
         * l :
         * p :
         */

        private String s;
        private String mp;
        private String n;
        private String f8;
        private String v24;
        private String h;
        private String l;
        private String p;
        private String a;
        private String base;

        public String getBase() {
            return base;
        }

        public void setBase(String base) {
            this.base = base;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getMp() {
            return mp;
        }

        public void setMp(String mp) {
            this.mp = mp;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
        }

        public String getF8() {
            return f8;
        }

        public void setF8(String f8) {
            this.f8 = f8;
        }

        public String getV24() {
            return v24;
        }

        public void setV24(String v24) {
            this.v24 = v24;
        }

        public String getH() {
            return h;
        }

        public void setH(String h) {
            this.h = h;
        }

        public String getL() {
            return l;
        }

        public void setL(String l) {
            this.l = l;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }
    }
}
