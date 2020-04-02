package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * ding
 * 2019-05-15
 * com.coinbene.manbiwang.model.http
 */
public class ContractDetailMode extends BaseRes {


    /**
     * message : null
     * timezone : null
     * data : {"pageNum":1,"pageSize":10,"total":-1,"pages":1,"list":[{"orderPrice":"5800.0","quantity":54,"tradeTime":1557905099360,"fee":"0.0000"}]}
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
         * pageSize : 10
         * total : -1
         * pages : 1
         * list : [{"orderPrice":"5800.0","quantity":54,"tradeTime":1557905099360,"fee":"0.0000"}]
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

        public static class ListBean {
            /**
             * orderPrice : 5800.0
             * quantity : 54
             * tradeTime : 1557905099360
             * fee : 0.0000
             */

            private String orderPrice;
            private String quantity;
            private long tradeTime;
            private String fee;

            public String getOrderPrice() {
                return orderPrice;
            }

            public void setOrderPrice(String orderPrice) {
                this.orderPrice = orderPrice;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public long getTradeTime() {
                return tradeTime;
            }

            public void setTradeTime(long tradeTime) {
                this.tradeTime = tradeTime;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }
        }
    }
}
