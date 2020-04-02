package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * @author ding
 * 合约历史委托Model
 */
public class HistoryDelegationModel extends BaseRes {

    /**
     * data : {"pageNum":1,"pageSize":10,"total":2,"pages":1,"list":[{"orderId":"552219446899376128","direction":"closeShort","leverage":0,"symbol":"ETHUSDT","orderType":"limit","quantity":50000,"orderPrice":"113.5500","orderValue":"0.0000","fee":"0.0034","filledQuantity":50000,"averagePrice":"113.5500","orderTime":1551700976000,"status":"filled"},{"orderId":"552219413336555520","direction":"openShort","leverage":20,"symbol":"ETHUSDT","orderType":"limit","quantity":100000,"orderPrice":"112.0000","orderValue":"11.2050","fee":"0.0067","filledQuantity":100000,"averagePrice":"112.0000","orderTime":1551700968000,"status":"filled"}]}
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
         * pageNum : 1
         * pageSize : 10
         * total : 2
         * pages : 1
         * list : [{"orderId":"552219446899376128","direction":"closeShort","leverage":0,"symbol":"ETHUSDT","orderType":"limit","quantity":50000,"orderPrice":"113.5500","orderValue":"0.0000","fee":"0.0034","filledQuantity":50000,"averagePrice":"113.5500","orderTime":1551700976000,"status":"filled"},{"orderId":"552219413336555520","direction":"openShort","leverage":20,"symbol":"ETHUSDT","orderType":"limit","quantity":100000,"orderPrice":"112.0000","orderValue":"11.2050","fee":"0.0067","filledQuantity":100000,"averagePrice":"112.0000","orderTime":1551700968000,"status":"filled"}]
         */

        private int pageNum;
        private int pageSize;
        private int total;
        private int pages;
        private List<ListBean> list;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean  implements Serializable {
            /**
             * orderId : 552219446899376128
             * direction : closeShort
             * leverage : 0
             * symbol : ETHUSDT
             * orderType : limit
             * quantity : 50000
             * orderPrice : 113.5500
             * orderValue : 0.0000
             * fee : 0.0034
             * filledQuantity : 50000
             * averagePrice : 113.5500
             * orderTime : 1551700976000
             * status : filled
             */

            private String orderId;
            private String direction;
            private int leverage;
            private String symbol;
            private String orderType;
            private String quantity;
            private String orderPrice;
            private String orderValue;
            private String fee;
            private String filledQuantity;
            private String averagePrice;
            private long orderTime;
            private String status;
            private String profit;

            public String getProfit() {
                return profit;
            }

            public void setProfit(String profit) {
                this.profit = profit;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
            }

            public int getLeverage() {
                return leverage;
            }

            public void setLeverage(int leverage) {
                this.leverage = leverage;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getOrderType() {
                return orderType;
            }

            public void setOrderType(String orderType) {
                this.orderType = orderType;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getOrderPrice() {
                return orderPrice;
            }

            public void setOrderPrice(String orderPrice) {
                this.orderPrice = orderPrice;
            }

            public String getOrderValue() {
                return orderValue;
            }

            public void setOrderValue(String orderValue) {
                this.orderValue = orderValue;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public String getFilledQuantity() {
                return filledQuantity;
            }

            public void setFilledQuantity(String filledQuantity) {
                this.filledQuantity = filledQuantity;
            }

            public String getAveragePrice() {
                return averagePrice;
            }

            public void setAveragePrice(String averagePrice) {
                this.averagePrice = averagePrice;
            }

            public long getOrderTime() {
                return orderTime;
            }

            public void setOrderTime(long orderTime) {
                this.orderTime = orderTime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }
        }
    }
}
