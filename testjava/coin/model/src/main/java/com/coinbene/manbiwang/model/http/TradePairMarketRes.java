package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mengxiangdong on 2017/11/26.
 */

public class TradePairMarketRes extends BaseRes {

    /**
     * data : [{"a":"string","n":0,"nl":"string","nu":"string","o":0,"p":"string","r":0,"s":"string","v":0,"v24":0}]
     * timezone : 0
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable, Cloneable{
        /**
         * a : string
         * n : 0
         * nl : string
         * nu : string
         * o : 0
         * p : string
         * r : 0
         * s : string
         * v : 0
         * v24 : 0
         */

        private String a;// // 涨跌(Price change amount)
        private String n;// 最新价(Now price)
        private String nl;// 以本地火币为单位计价(Local now price)
        private String nu;// 以USDT为单位计价
        private String o;
        private String p;// 涨跌百分比(Price change percent)
        private String r; // quote asset 对应的本地火币汇率( n * r = nl)
        private String s;// 交易对代码(Symbol)
        private String v;
        private String v24; // 24小时交易量(Total traded quote asset volume)
        private String amt24;

        private boolean latest;
        private boolean hot;
        private String chineseName;
        private String englishName;
        private String name;
        private String pairID;
        private String v24Str;
        private String pStr;
        private int Precision;
        private int sort;//排序字段

        private boolean isOptional;
        private String baseAsset;

        private int contractType;

        public String getBaseAsset() {
            return baseAsset;
        }

        public void setBaseAsset(String baseAsset) {
            this.baseAsset = baseAsset;
        }

        public boolean isOptional() {
            return isOptional;
        }

        public void setOptional(boolean optional) {
            isOptional = optional;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }

        public boolean isHot() {
            return hot;
        }

        public void setHot(boolean hot) {
            this.hot = hot;
        }

        public boolean isLatest() {
            return latest;
        }

        public void setLatest(boolean latest) {
            this.latest = latest;
        }

        public int getPrecision() {
            return Precision;
        }

        public void setPrecision(int precision) {
            Precision = precision;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public String getN() {
            return n;
        }

        public void setN(String n) {
            this.n = n;
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

        public String getO() {
            return o;
        }

        public void setO(String o) {
            this.o = o;
        }

        public String getP() {
            return p;
        }

        public void setP(String p) {
            this.p = p;
        }

        public String getR() {
            return r;
        }

        public void setR(String r) {
            this.r = r;
        }

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public String getV() {
            return v;
        }

        public void setV(String v) {
            this.v = v;
        }

        public String getV24() {
            return v24;
        }

        public void setV24(String v24) {
            this.v24 = v24;
        }

        public String getChineseName() {
            return chineseName;
        }

        public void setChineseName(String chineseName) {
            this.chineseName = chineseName;
        }

        public String getEnglishName() {
            return englishName;
        }

        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPairID() {
            return pairID;
        }

        public void setPairID(String pairID) {
            this.pairID = pairID;
        }

        public String getV24Str() {
            return v24Str;
        }

        public void setV24Str(String v24Str) {
            this.v24Str = v24Str;
        }

        public String getpStr() {
            return pStr;
        }

        public void setpStr(String pStr) {
            this.pStr = pStr;
        }

        public String getAmt24() {
            return amt24;
        }

        public void setAmt24(String amt24) {
            this.amt24 = amt24;
        }

        public int getContractType() {
            return contractType;
        }

        public void setContractType(int contractType) {
            this.contractType = contractType;
        }

        @Override
        public Object clone(){
            DataBean dataBean = null;
            try {
                dataBean = (DataBean) super.clone();
            }catch (Exception CloneNotSupportedException) {

            }
            return dataBean;
        }
    }
}
