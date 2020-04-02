package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class OtcAdListModel extends BaseRes{


    /**
     * code : 200
     * message :
     * data : {"list":[{"adType":1,"asset":"USDT","stock":1.1793165145095576E7,"price":6.5,"minOrder":200,"maxOrder":7.666132044E7,"releaseType":1,"adId":12,"bankName":"招商银行","hasAli":0,"hasWechat":0,"name":"田涛"},{"adType":1,"asset":"USDT","stock":199,"price":6.5,"minOrder":650,"maxOrder":1293.5,"releaseType":1,"adId":27,"bankName":"北京银行","hasAli":1,"hasWechat":1,"name":"楚勇"}],"pages":1,"total":2,"curPage":1}
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
         * list : [{"adType":1,"asset":"USDT","stock":1.1793165145095576E7,"price":6.5,"minOrder":200,"maxOrder":7.666132044E7,"releaseType":1,"adId":12,"bankName":"招商银行","hasAli":0,"hasWechat":0,"name":"田涛"},{"adType":1,"asset":"USDT","stock":199,"price":6.5,"minOrder":650,"maxOrder":1293.5,"releaseType":1,"adId":27,"bankName":"北京银行","hasAli":1,"hasWechat":1,"name":"楚勇"}]
         * pages : 1
         * total : 2
         * curPage : 1
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
             * adType : 1
             * asset : USDT
             * stock : 1.1793165145095576E7
             * price : 6.5
             * minOrder : 200
             * maxOrder : 7.666132044E7
             * releaseType : 1
             * adId : 12
             * bankName : 招商银行
             * hasAli : 0
             * hasWechat : 0
             * name : 田涛
             */

            private int adType;
            private String asset;
            private String stock;
            private String price;
            private String minOrder;
            private String maxOrder;
            private int releaseType;
            private String adId;
            private String remark;
            private String bankName;
            private int hasAli;
            private int hasWechat;
            private String name;
            private boolean isInputVisible;
            private String inputUsdt;
            private String inputCny;
            private String currency;
            private String currencySymbol;
            private String finishOrderCount;
            private String finishOrderRate;
            private List<PayType> payWayList;


            public static class PayType {
                private int payTypeId;
                private int payId;
                private String payTypeName;

                private String bankName;

                public String getBankName() {
                    return bankName;
                }

                public void setBankName(String bankName) {
                    this.bankName = bankName;
                }

                public int getPayTypeId() {
                    return payTypeId;
                }

                public void setPayTypeId(int payTypeId) {
                    this.payTypeId = payTypeId;
                }

                public int getPayId() {
                    return payId;
                }

                public void setPayId(int payId) {
                    this.payId = payId;
                }

                public String getPayTypeName() {
                    return payTypeName;
                }

                public void setPayTypeName(String payTypeName) {
                    this.payTypeName = payTypeName;
                }
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public List<PayType> getPayTypes() {
                return payWayList;
            }

            public void setPayTypes(List<PayType> payTypes) {
                this.payWayList = payTypes;
            }

            public String getFinishOrderCount() {
                return finishOrderCount;
            }

            public void setFinishOrderCount(String finishOrderCount) {
                this.finishOrderCount = finishOrderCount;
            }

            public String getFinishOrderRate() {
                return finishOrderRate;
            }

            public void setFinishOrderRate(String finishOrderRate) {
                this.finishOrderRate = finishOrderRate;
            }

            public String getCurrency() {
                return currency;
            }

            public void setCurrency(String currency) {
                this.currency = currency;
            }

            public String getCurrencySymbol() {
                return currencySymbol;
            }

            public void setCurrencySymbol(String currencySymbol) {
                this.currencySymbol = currencySymbol;
            }

            public String getInputUsdt() {
                return inputUsdt;
            }

            public void setInputUsdt(String inputUsdt) {
                this.inputUsdt = inputUsdt;
            }

            public String getInputCny() {
                return inputCny;
            }

            public void setInputCny(String inputCny) {
                this.inputCny = inputCny;
            }

            public boolean isInputVisible() {
                return isInputVisible;
            }

            public void setInputVisible(boolean inputVisible) {
                isInputVisible = inputVisible;
            }

            public int getAdType() {
                return adType;
            }

            public void setAdType(int adType) {
                this.adType = adType;
            }

            public String getAsset() {
                return asset;
            }

            public void setAsset(String asset) {
                this.asset = asset;
            }


            public String getMinOrder() {
                return minOrder;
            }

            public void setMinOrder(String minOrder) {
                this.minOrder = minOrder;
            }

            public String getStock() {
                return stock;
            }

            public void setStock(String stock) {
                this.stock = stock;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getMaxOrder() {
                return maxOrder;
            }

            public void setMaxOrder(String maxOrder) {
                this.maxOrder = maxOrder;
            }

            public int getReleaseType() {
                return releaseType;
            }

            public void setReleaseType(int releaseType) {
                this.releaseType = releaseType;
            }

            public String getAdId() {
                return adId;
            }

            public void setAdId(String adId) {
                this.adId = adId;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public int getHasAli() {
                return hasAli;
            }

            public void setHasAli(int hasAli) {
                this.hasAli = hasAli;
            }

            public int getHasWechat() {
                return hasWechat;
            }

            public void setHasWechat(int hasWechat) {
                this.hasWechat = hasWechat;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }


        }
    }
}
