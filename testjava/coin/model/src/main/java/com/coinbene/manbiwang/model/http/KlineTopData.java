package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Created by zhangle on 2018/3/6.
 */

public class KlineTopData extends BaseRes{


    /**
     * data : {"orderDepth":{"s":"XRPUSDT","n":"0.21","r":"6.2925","asks":[["0.21","2"]]},"quote":{"v24":"0","v":"0","o":"0.3","n":"0.21","a":"-0.09","p":"-30%","nl":"￥1.3214","nu":"$0.2100","l":"0.3","r":"6.2925"}}
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
         * orderDepth : {"s":"XRPUSDT","n":"0.21","r":"6.2925","asks":[["0.21","2"]]}
         * quote : {"v24":"0","v":"0","o":"0.3","n":"0.21","a":"-0.09","p":"-30%","nl":"￥1.3214","nu":"$0.2100","l":"0.3","r":"6.2925"}
         */

        public OrderDepthBean orderDepth;
        public BookOrderRes.DataBean.QuoteBean quote;

        public OrderDepthBean getOrderDepth() {
            return orderDepth;
        }

        public void setOrderDepth(OrderDepthBean orderDepth) {
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
            public List<String[]> asks;
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
        }
    }
}
