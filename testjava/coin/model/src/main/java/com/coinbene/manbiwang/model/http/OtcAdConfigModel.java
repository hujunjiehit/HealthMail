package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class OtcAdConfigModel extends BaseRes {
    /**
     * data : {"assetList":["USDT","BTC","ETH"],"currencyList":["BRL","CNY","JPY"],"payTypesList":[{"payTypeId":1,"payId":0,"payTypeName":"银行卡"},{"payTypeId":2,"payId":0,"payTypeName":"支付宝"},{"payTypeId":3,"payId":0,"payTypeName":"微信"}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<String> assetList;
        private List<String> currencyList;
        private List<PayTypesListBean> payTypesList;

        public List<String> getAssetList() {
            return assetList;
        }

        public void setAssetList(List<String> assetList) {
            this.assetList = assetList;
        }

        public List<String> getCurrencyList() {
            return currencyList;
        }

        public void setCurrencyList(List<String> currencyList) {
            this.currencyList = currencyList;
        }

        public List<PayTypesListBean> getPayTypesList() {
            return payTypesList;
        }

        public void setPayTypesList(List<PayTypesListBean> payTypesList) {
            this.payTypesList = payTypesList;
        }

        public static class PayTypesListBean {
            public PayTypesListBean(int payTypeId, String payTypeName) {
                this.payTypeId = payTypeId;
                this.payTypeName = payTypeName;
            }

            /**
             * payTypeId : 1
             * payId : 0
             * payTypeName : 银行卡
             */



            private int payTypeId;
            private int payId;
            private String payTypeName;

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
    }


    /**
     * data : {"assetList":["USDT","BTC","ETH"],"currencyList":["BRL","CNY","JPY"]}
     */


}
