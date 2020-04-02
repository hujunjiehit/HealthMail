package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

public class SellerAdModel extends BaseRes implements Serializable {

    /**
     * data : {"list":[{"adType":1,"asset":"ETH","stock":"1000.000000","price":"1.00","minOrder":"100.00","maxOrder":"10000.00","releaseType":0,"remark":"","adId":197,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 16:46:48","currency":"JPY","currencySymbol":"J￥"},{"adType":2,"asset":"USDT","stock":"9777.000000","price":"1.00","minOrder":"200.00","maxOrder":"10000.00","releaseType":1,"remark":"","adId":202,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 18:40:38","currency":"CNY","currencySymbol":"¥"},{"adType":1,"asset":"BTC","stock":"9800.000000","price":"1.00","minOrder":"200.00","maxOrder":"10000.00","releaseType":1,"remark":"","adId":203,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 18:53:14","currency":"CNY","currencySymbol":"¥"}],"pages":1,"total":3,"curPage":1}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * list : [{"adType":1,"asset":"ETH","stock":"1000.000000","price":"1.00","minOrder":"100.00","maxOrder":"10000.00","releaseType":0,"remark":"","adId":197,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 16:46:48","currency":"JPY","currencySymbol":"J￥"},{"adType":2,"asset":"USDT","stock":"9777.000000","price":"1.00","minOrder":"200.00","maxOrder":"10000.00","releaseType":1,"remark":"","adId":202,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 18:40:38","currency":"CNY","currencySymbol":"¥"},{"adType":1,"asset":"BTC","stock":"9800.000000","price":"1.00","minOrder":"200.00","maxOrder":"10000.00","releaseType":1,"remark":"","adId":203,"bankName":null,"hasAli":null,"hasWechat":null,"name":null,"payWayList":[{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}],"finishOrderCount":0,"finishOrderRate":0,"finishOrderTime":0,"merchantRegisterTime":null,"createTime":"2019-04-16 18:53:14","currency":"CNY","currencySymbol":"¥"}]
         * pages : 1
         * total : 3
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

        public static class ListBean implements Serializable {
            /**
             * adType : 1
             * asset : ETH
             * stock : 1000.000000
             * price : 1.00
             * minOrder : 100.00
             * maxOrder : 10000.00
             * releaseType : 0
             * remark :
             * adId : 197
             * bankName : null
             * hasAli : null
             * hasWechat : null
             * name : null
             * payWayList : [{"payTypeId":1,"payId":110,"payTypeName":"银行卡","bankName":"中国人民银行","bankAccount":"8901232138797923","payAccount":""},{"payTypeId":2,"payId":111,"payTypeName":"支付宝","bankName":"","bankAccount":"","payAccount":"123123123"},{"payTypeId":3,"payId":112,"payTypeName":"微信","bankName":"","bankAccount":"","payAccount":"wechat123"}]
             * finishOrderCount : 0
             * finishOrderRate : 0
             * finishOrderTime : 0
             * merchantRegisterTime : null
             * createTime : 2019-04-16 16:46:48
             * currency : JPY
             * currencySymbol : J￥
             */

            private int adType;
            private String asset;
            private String stock;
            private String price;
            private String minOrder;
            private String maxOrder;
            private String releaseType;
            private String remark;
            private int adId;
            private Object bankName;
            private Object hasAli;
            private Object hasWechat;
            private Object name;
            private int finishOrderCount;
            private int finishOrderRate;
            private int finishOrderTime;
            private Object merchantRegisterTime;
            private String createTime;
            private String currency;
            private String currencySymbol;
            private List<PayWayListBean> payWayList;
            private int adPriceMode;
            private String floatingRatio;

            public int getAdPriceMode() {
                return adPriceMode;
            }

            public void setAdPriceMode(int adPriceMode) {
                this.adPriceMode = adPriceMode;
            }

            public String getFloatingRatio() {
                return floatingRatio;
            }

            public void setFloatingRatio(String floatingRatio) {
                this.floatingRatio = floatingRatio;
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

            public String getMinOrder() {
                return minOrder;
            }

            public void setMinOrder(String minOrder) {
                this.minOrder = minOrder;
            }

            public String getMaxOrder() {
                return maxOrder;
            }

            public void setMaxOrder(String maxOrder) {
                this.maxOrder = maxOrder;
            }

            public String getReleaseType() {
                return releaseType;
            }

            public void setReleaseType(String releaseType) {
                this.releaseType = releaseType;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public int getAdId() {
                return adId;
            }

            public void setAdId(int adId) {
                this.adId = adId;
            }

            public Object getBankName() {
                return bankName;
            }

            public void setBankName(Object bankName) {
                this.bankName = bankName;
            }

            public Object getHasAli() {
                return hasAli;
            }

            public void setHasAli(Object hasAli) {
                this.hasAli = hasAli;
            }

            public Object getHasWechat() {
                return hasWechat;
            }

            public void setHasWechat(Object hasWechat) {
                this.hasWechat = hasWechat;
            }

            public Object getName() {
                return name;
            }

            public void setName(Object name) {
                this.name = name;
            }

            public int getFinishOrderCount() {
                return finishOrderCount;
            }

            public void setFinishOrderCount(int finishOrderCount) {
                this.finishOrderCount = finishOrderCount;
            }

            public int getFinishOrderRate() {
                return finishOrderRate;
            }

            public void setFinishOrderRate(int finishOrderRate) {
                this.finishOrderRate = finishOrderRate;
            }

            public int getFinishOrderTime() {
                return finishOrderTime;
            }

            public void setFinishOrderTime(int finishOrderTime) {
                this.finishOrderTime = finishOrderTime;
            }

            public Object getMerchantRegisterTime() {
                return merchantRegisterTime;
            }

            public void setMerchantRegisterTime(Object merchantRegisterTime) {
                this.merchantRegisterTime = merchantRegisterTime;
            }

            public String getCreateTime() {
                return createTime;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
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

            public List<PayWayListBean> getPayWayList() {
                return payWayList;
            }

            public void setPayWayList(List<PayWayListBean> payWayList) {
                this.payWayList = payWayList;
            }

            public static class PayWayListBean implements Serializable {
                /**
                 * payTypeId : 1
                 * payId : 110
                 * payTypeName : 银行卡
                 * bankName : 中国人民银行
                 * bankAccount : 8901232138797923
                 * payAccount :
                 */

                private int payTypeId;
                private int payId;
                private String payTypeName;
                private String bankName;
                private String bankAccount;
                private String payAccount;

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

                public String getBankName() {
                    return bankName;
                }

                public void setBankName(String bankName) {
                    this.bankName = bankName;
                }

                public String getBankAccount() {
                    return bankAccount;
                }

                public void setBankAccount(String bankAccount) {
                    this.bankAccount = bankAccount;
                }

                public String getPayAccount() {
                    return payAccount;
                }

                public void setPayAccount(String payAccount) {
                    this.payAccount = payAccount;
                }
            }
        }
    }
}
