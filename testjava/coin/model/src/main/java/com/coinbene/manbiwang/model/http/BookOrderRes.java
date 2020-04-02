package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class BookOrderRes extends BaseRes {
    /**
     * data : {"asks":[["",""],["",""]],"bids":[["",""],["",""]]],"s":"string"}
     * timezone : 0
     */

    /**
     * data : {"orderDepth":{"s":"XRPUSDT","n":"0.21","r":"6.2925","asks":[["0.21","2"]]},"quote":{"v24":"0","v":"0","o":"0.3","n":"0.21","a":"-0.09","p":"-30%","nl":"￥1.3214","nu":"$0.2100","l":"0.3","r":"6.2925"}}
     */

    public BookOrderRes.DataBean data;

    public BookOrderRes.DataBean getData() {
        return data;
    }

    public void setData(BookOrderRes.DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * orderDepth : {"s":"XRPUSDT","n":"0.21","r":"6.2925","asks":[["0.21","2"]]}
         * quote : {"v24":"0","v":"0","o":"0.3","n":"0.21","a":"-0.09","p":"-30%","nl":"￥1.3214","nu":"$0.2100","l":"0.3","r":"6.2925"}
         */

        public BookOrderRes.DataBean.OrderDepthBean orderDepth;
        public BookOrderRes.DataBean.QuoteBean quote;
        public boolean full;

        public boolean isFull() {
            return full;
        }

        public void setFull(boolean full) {
            this.full = full;
        }

        public BookOrderRes.DataBean.OrderDepthBean getOrderDepth() {
            return orderDepth;
        }

        public void setOrderDepth(BookOrderRes.DataBean.OrderDepthBean orderDepth) {
            this.orderDepth = orderDepth;
        }

        public BookOrderRes.DataBean.QuoteBean getQuote() {
            return quote;
        }

        public void setQuote(BookOrderRes.DataBean.QuoteBean quote) {
            this.quote = quote;
        }

        public static class OrderDepthBean {
            /**
             * s : XRPUSDT
             * n : 0.21
             * r : 6.2925
             * asks : [["0.21","2"]]
             */

            public String s;
            public String n;
            public String r;
            // websocket 卖   http 买
            public List<String[]> asks;
            // websocket 买   http 卖
            public List<String[]> bids;


            public String getS() {
                return s;
            }

            public void setS(String s) {
                this.s = s;
            }

            public String getN() {
                return n;
            }

            public void setN(String n) {
                this.n = n;
            }

            public String getR() {
                return r;
            }

            public void setR(String r) {
                this.r = r;
            }
        }

        public static class QuoteBean {
            /**
             * v24 : 0
             * v : 0
             * o : 0.3
             * n : 0.21
             * a : -0.09
             * p : -30%
             * nl : ￥1.3214
             * nu : $0.2100
             * l : 0.3
             * r : 6.2925
             */

            public String v24;
            public String v;
            public String o;
            public String n;
            public String a;
            public String p;
            public String nl;
            public String nu;
            public String l;
            public String r;
            public String h;
            public String amt24;

            public String low24;
            public String high24;
            public boolean hot;

            //添加给合约k线用
            private String mp;
            private String f8;

            public String getH() {
                return h;
            }

            public void setH(String h) {
                this.h = h;
            }

            public String getV24() {
                return v24;
            }

            public void setV24(String v24) {
                this.v24 = v24;
            }

            public String getV() {
                return v;
            }

            public void setV(String v) {
                this.v = v;
            }

            public String getO() {
                return o;
            }

            public void setO(String o) {
                this.o = o;
            }

            public String getN() {
                return n;
            }

            public void setN(String n) {
                this.n = n;
            }

            public String getA() {
                return a;
            }

            public void setA(String a) {
                this.a = a;
            }

            public String getP() {
                return p;
            }

            public void setP(String p) {
                this.p = p;
            }

            public String getNl() {
                return nl;
            }

            public void setNl(String nl) {
                this.nl = nl;
            }

            public String getNu() {
                return nu;
            }

            public void setNu(String nu) {
                this.nu = nu;
            }

            public String getL() {
                return l;
            }

            public void setL(String l) {
                this.l = l;
            }

            public String getR() {
                return r;
            }

            public void setR(String r) {
                this.r = r;
            }

            public String getAmt24() {
                return amt24;
            }

            public void setAmt24(String amt24) {
                this.amt24 = amt24;
            }

            public String getLow24() {
                return low24;
            }

            public void setLow24(String low24) {
                this.low24 = low24;
            }

            public String getHigh24() {
                return high24;
            }

            public void setHigh24(String high24) {
                this.high24 = high24;
            }

            public boolean isHot() {
                return hot;
            }

            public void setHot(boolean hot) {
                this.hot = hot;
            }

            public String getMp() {
                return mp;
            }

            public void setMp(String mp) {
                this.mp = mp;
            }

            public String getF8() {
                return f8;
            }

            public void setF8(String f8) {
                this.f8 = f8;
            }
        }
    }
}

