package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class TransferListModel extends BaseRes {


    /**
     * data : {"list":[{"accountType":"string","amount":"string","asset":"string","nostro":"string","remark":"string","transferTime":"2018-05-13T08:03:27.294Z","transferType":0}],"pages":0,"total":0}
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
         * list : [{"accountType":"string","amount":"string","asset":"string","nostro":"string","remark":"string","transferTime":"2018-05-13T08:03:27.294Z","transferType":0}]
         * pages : 0
         * total : 0
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

        public  class ListBean extends RecodingRes{
            /**
             * accountType : string
             * amount : string
             * asset : string
             * nostro : string
             * remark : string
             * transferTime : 2018-05-13T08:03:27.294Z
             * transferType : 0
             */

            private String accountType;
            private String amount;
            private String asset;
            private String nostro;
            private String remark;
            private String transferTime;
            private int transferType;

            public String getAccountType() {
                return accountType;
            }

            public void setAccountType(String accountType) {
                this.accountType = accountType;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }

            public String getNostro() {
                return nostro;
            }

            public void setNostro(String nostro) {
                this.nostro = nostro;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getTransferTime() {
                return transferTime;
            }

            public void setTransferTime(String transferTime) {
                this.transferTime = transferTime;
            }

            public int getTransferType() {
                return transferType;
            }

            public void setTransferType(int transferType) {
                this.transferType = transferType;
            }
        }
    }


}
