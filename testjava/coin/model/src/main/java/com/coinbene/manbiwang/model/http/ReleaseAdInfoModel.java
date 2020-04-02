package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class ReleaseAdInfoModel extends BaseRes {

    /**
     * data : {"assetList":["USDT","BTC","ETH"],"currencyList":["BRL","CNY","JPY"],"sellCurrency":"CNY","paymentWayList":[]}
     *
     * PreAdInfo {
     * assetList (Array[string], optional): 数字资产列表 例如：USDT ,
     * currencyList (Array[string], optional): 现货列表 例如：CNY ,
     * paymentWayList (Array[OtcPaymentWay], optional): 开启对支付方式 ,
     * sellCurrency (string, optional): 卖出货币类型
     * }OtcPaymentWay {
     * bankAccount (string, optional): 银行账号 ,
     * bankAddress (string, optional): 银行地址 ,
     * bankName (string, optional): 银行名字 ,
     * id (integer, optional): 支付方式id ,
     * online (integer, optional): 是否开启1：开启，0：未开启 ,
     * payAccount (string, optional): 其他支付账号比如微信 ,
     * payQrCode (string, optional): 支付二维码 ,
     * type (integer, optional): 支付类型1：银行卡 ,
     * userName (string, optional): 用户名字
     * }
     *
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
         * assetList : ["USDT","BTC","ETH"]
         * currencyList : ["BRL","CNY","JPY"]
         * sellCurrency : CNY
         * paymentWayList : []
         */

        private String sellCurrency;//
        private List<String> assetList;
        private List<String> currencyList;
        private List<OtcPaymentWay> paymentWayList;

        public String getSellCurrency() {
            return sellCurrency;
        }

        public void setSellCurrency(String sellCurrency) {
            this.sellCurrency = sellCurrency;
        }

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

        public List<OtcPaymentWay> getPaymentWayList() {
            return paymentWayList;
        }

        public void setPaymentWayList(List<OtcPaymentWay> paymentWayList) {
            this.paymentWayList = paymentWayList;
        }
    }

   public static class OtcPaymentWay {

        /**
         * bankAccount : string
         * bankAddress : string
         * bankName : string
         * id : 0
         * online : 0
         * payAccount : string
         * payQrCode : string
         * type : 0
         * userName : string
         */

        private String bankAccount;
        private String bankAddress;
        private String bankName;
        private int id;
        private int online;
        private String payAccount;
        private String payQrCode;
        private int type;
        private String userName;

        public String getBankAccount() {
            return bankAccount;
        }

        public void setBankAccount(String bankAccount) {
            this.bankAccount = bankAccount;
        }

        public String getBankAddress() {
            return bankAddress;
        }

        public void setBankAddress(String bankAddress) {
            this.bankAddress = bankAddress;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOnline() {
            return online;
        }

        public void setOnline(int online) {
            this.online = online;
        }

        public String getPayAccount() {
            return payAccount;
        }

        public void setPayAccount(String payAccount) {
            this.payAccount = payAccount;
        }

        public String getPayQrCode() {
            return payQrCode;
        }

        public void setPayQrCode(String payQrCode) {
            this.payQrCode = payQrCode;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }
}
