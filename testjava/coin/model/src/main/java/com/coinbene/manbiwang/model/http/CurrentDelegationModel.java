package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * 合约当前委托Model
 *
 * @author ding
 */
public class CurrentDelegationModel extends BaseRes {

    /**
     * data : {"list":[{"averagePrice":"string","direction":"string","fee":"string","filledQuantity":0,"leverage":0,"orderId":"string","orderPrice":"string","orderTime":0,"orderType":"string","orderValue":"string","quantity":0,"status":0,"symbol":"string"}],"pageNum":0,"pageSize":0,"pages":0,"total":0}
     * timezone : 0
     */

    private DataBean data;
    private int timezone;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public static class DataBean {
        /**
         * list : [{"averagePrice":"string","direction":"string","fee":"string","filledQuantity":0,"leverage":0,"orderId":"string","orderPrice":"string","orderTime":0,"orderType":"string","orderValue":"string","quantity":0,"status":0,"symbol":"string"}]
         * pageNum : 0
         * pageSize : 0
         * pages : 0
         * total : 0
         */

        private int pageNum;
        private int pageSize;
        private int pages;
        private int total;
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

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public List<ListBean> getList() {
            return list;
        }

        public void setList(List<ListBean> list) {
            this.list = list;
        }

        public static class ListBean {
            /**
             * averagePrice : string
             * direction : string
             * fee : string
             * filledQuantity : 0
             * leverage : 0
             * orderId : string
             * orderPrice : string
             * orderTime : 0
             * orderType : string
             * orderValue : string
             * quantity : 0
             * status : 0
             * symbol : string
             */

            private String averagePrice;
            private String direction;
            private String fee;
            private String filledQuantity;
            private int leverage;
            private String orderId;
            private String orderPrice;
            private long orderTime;
            private String orderType;
            private String orderValue;
            private String quantity;
            private String status;
            private String symbol;


            public String getAveragePrice() {
                return averagePrice;
            }

            public void setAveragePrice(String averagePrice) {
                this.averagePrice = averagePrice;
            }

            public String getDirection() {
                return direction;
            }

            public void setDirection(String direction) {
                this.direction = direction;
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

            public int getLeverage() {
                return leverage;
            }

            public void setLeverage(int leverage) {
                this.leverage = leverage;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public String getOrderPrice() {
                return orderPrice;
            }

            public void setOrderPrice(String orderPrice) {
                this.orderPrice = orderPrice;
            }

            public long getOrderTime() {
                return orderTime;
            }

            public void setOrderTime(long orderTime) {
                this.orderTime = orderTime;
            }

            public String getOrderType() {
                return orderType;
            }

            public void setOrderType(String orderType) {
                this.orderType = orderType;
            }

            public String getOrderValue() {
                return orderValue;
            }

            public void setOrderValue(String orderValue) {
                this.orderValue = orderValue;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }
        }
    }
}
