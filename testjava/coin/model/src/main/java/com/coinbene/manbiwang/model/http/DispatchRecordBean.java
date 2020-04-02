package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class DispatchRecordBean extends BaseRes {

    /**
     * data : {"list":[{"id":1123,"orderId":"12313","userId":117906,"bank":"MAIN","asset":"USDT","amount":9,"remark":"武翔测试6666666666666666666666666666111111113333332222222222","createTime":"2018-12-24 16:46:23","updateTime":"2018-12-24 17:20:20"}],"pages":1,"total":1}
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
         * list : [{"id":1123,"orderId":"12313","userId":117906,"bank":"MAIN","asset":"USDT","amount":9,"remark":"武翔测试6666666666666666666666666666111111113333332222222222","createTime":"2018-12-24 16:46:23","updateTime":"2018-12-24 17:20:20"}]
         * pages : 1
         * total : 1
         */

        private int pages;
        private int total;
        private List<ListBean> list;

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
             * id : 1123
             * orderId : 12313
             * userId : 117906
             * bank : MAIN
             * asset : USDT
             * amount : 9
             * remark : 武翔测试6666666666666666666666666666111111113333332222222222
             * createTime : 2018-12-24 16:46:23
             * updateTime : 2018-12-24 17:20:20
             */

            private int id;
            private String orderId;
            private int userId;
            private String bank;
            private String asset;
            private String amount;
            private String remark;
            private String createTime;
            private String updateTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getBank() {
                return bank;
            }

            public void setBank(String bank) {
                this.bank = bank;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }
        }
    }
}
