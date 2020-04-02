package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * @author ding
 * 资金费用Model
 */
public class FundCostModel extends BaseRes {


    /**
     * message : null
     * timezone : null
     * data : {"pageNum":1,"total":9,"pageSize":1,"list":[{"id":236716,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551758400000},{"id":241336,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551758400000},{"id":232022,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":234983,"symbol":"BTCUSDT","side":"short","position":"1","markPrice":"3722.9000","positionValue":"0.0003","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":236689,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":227331,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551729600000},{"id":231955,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551729600000},{"id":222617,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"113.2500","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551700800000},{"id":227309,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"113.2500","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551700800000}]}
     */


    private String timezone;
    private DataBean data;


    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pageNum : 1
         * total : 9
         * pageSize : 1
         * list : [{"id":236716,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551758400000},{"id":241336,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551758400000},{"id":232022,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":234983,"symbol":"BTCUSDT","side":"short","position":"1","markPrice":"3722.9000","positionValue":"0.0003","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":236689,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551744000000},{"id":227331,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"112.0000","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551729600000},{"id":231955,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"112.0000","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551729600000},{"id":222617,"symbol":"ETHUSDT","side":"short","position":"25","markPrice":"113.2500","positionValue":"0.0028","fee":"0.0000","feeRate":"-0.0038","time":1551700800000},{"id":227309,"symbol":"ETHUSDT","side":"long","position":"30","markPrice":"113.2500","positionValue":"0.0034","fee":"0.0000","feeRate":"-0.0038","time":1551700800000}]
         */

        private int pageNum;
        private int total;
        private int pageSize;
        private List<ListBean> list;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * id : 236716
             * symbol : ETHUSDT
             * side : short
             * position : 25
             * markPrice : 112.0000
             * positionValue : 0.0028
             * fee : 0.0000
             * feeRate : -0.0038
             * time : 1551758400000
             */

            private int id;
            private String symbol;
            private String side;
            private String position;
            private String markPrice;
            private String positionValue;
            private String fee;
            private String feeRate;
            private long time;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getSide() {
                return side;
            }

            public void setSide(String side) {
                this.side = side;
            }

            public String getPosition() {
                return position;
            }

            public void setPosition(String position) {
                this.position = position;
            }

            public String getMarkPrice() {
                return markPrice;
            }

            public void setMarkPrice(String markPrice) {
                this.markPrice = markPrice;
            }

            public String getPositionValue() {
                return positionValue;
            }

            public void setPositionValue(String positionValue) {
                this.positionValue = positionValue;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public String getFeeRate() {
                return feeRate;
            }

            public void setFeeRate(String feeRate) {
                this.feeRate = feeRate;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }
        }
    }
}
