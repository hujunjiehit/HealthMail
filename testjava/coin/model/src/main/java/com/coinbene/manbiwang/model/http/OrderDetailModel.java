package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class OrderDetailModel extends BaseRes {


    /**
     * data : {"payment":{"id":106,"userName":"huyong","type":3,"bankName":"","bankAddress":"","bankAccount":"","payAccount":"hubu","payQrCode":"http://res.mobilecoinabcbene.nlren.cn/coinbene-otc/10000149_10fcb23a","online":1},"timeLeft":1199565,"order":{"site":"MAIN","type":2,"orderId":"1904151742499112794","productId":188,"pairName":"USDT","unitPrice":"2.00","quantity":"200.000000","totalMoney":"400.00","status":1,"reference":"112794","placeId":10000149,"createTime":"2019-04-15 17:42:50","updateTime":"2019-04-15 17:42:50","moneyTrade":"CNY","supplierId":10000612,"supplierName":"天使开店","placeName":"huyong","userRegisterTime":"2018-09-20 10:47:40","userFinishOrderNum":1,"accountName":"","bankName":"","cardNumber":"","merchantFee":"0.200000","userFee":"0.800000","showReferenceNo":0,"merchantRemarks":"test","currencySymbol":"¥","merchantRegisterTime":"2019-01-11 21:26:48","merchantFinishOrderNum":2}}
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
         * payment : {"id":106,"userName":"huyong","type":3,"bankName":"","bankAddress":"","bankAccount":"","payAccount":"hubu","payQrCode":"http://res.mobilecoinabcbene.nlren.cn/coinbene-otc/10000149_10fcb23a","online":1}
         * timeLeft : 1199565
         * order : {"site":"MAIN","type":2,"orderId":"1904151742499112794","productId":188,"pairName":"USDT","unitPrice":"2.00","quantity":"200.000000","totalMoney":"400.00","status":1,"reference":"112794","placeId":10000149,"createTime":"2019-04-15 17:42:50","updateTime":"2019-04-15 17:42:50","moneyTrade":"CNY","supplierId":10000612,"supplierName":"天使开店","placeName":"huyong","userRegisterTime":"2018-09-20 10:47:40","userFinishOrderNum":1,"accountName":"","bankName":"","cardNumber":"","merchantFee":"0.200000","userFee":"0.800000","showReferenceNo":0,"merchantRemarks":"test","currencySymbol":"¥","merchantRegisterTime":"2019-01-11 21:26:48","merchantFinishOrderNum":2}
         */

        private PaymentBean payment;
        private String timeLeft;
        private OrderBean order;

        public PaymentBean getPayment() {
            return payment;
        }

        public void setPayment(PaymentBean payment) {
            this.payment = payment;
        }

        public String getTimeLeft() {
            return timeLeft;
        }

        public void setTimeLeft(String timeLeft) {
            this.timeLeft = timeLeft;
        }

        public OrderBean getOrder() {
            return order;
        }

        public void setOrder(OrderBean order) {
            this.order = order;
        }

        public static class PaymentBean {
            /**
             * id : 106
             * userName : huyong
             * type : 3
             * bankName :
             * bankAddress :
             * bankAccount :
             * payAccount : hubu
             * payQrCode : http://res.mobilecoinabcbene.nlren.cn/coinbene-otc/10000149_10fcb23a
             * online : 1
             */

            private int id;
            private String userName;
            private int type;
            private String bankName;
            private String bankAddress;
            private String bankAccount;
            private String payAccount;
            private String payQrCode;
            private int online;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public String getBankAddress() {
                return bankAddress;
            }

            public void setBankAddress(String bankAddress) {
                this.bankAddress = bankAddress;
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

            public String getPayQrCode() {
                return payQrCode;
            }

            public void setPayQrCode(String payQrCode) {
                this.payQrCode = payQrCode;
            }

            public int getOnline() {
                return online;
            }

            public void setOnline(int online) {
                this.online = online;
            }
        }

        public static class OrderBean {
            /**
             * site : MAIN
             * type : 2
             * orderId : 1904151742499112794
             * productId : 188
             * pairName : USDT
             * unitPrice : 2.00
             * quantity : 200.000000
             * totalMoney : 400.00
             * status : 1
             * reference : 112794
             * placeId : 10000149
             * createTime : 2019-04-15 17:42:50
             * updateTime : 2019-04-15 17:42:50
             * moneyTrade : CNY
             * supplierId : 10000612
             * supplierName : 天使开店
             * placeName : huyong
             * userRegisterTime : 2018-09-20 10:47:40
             * userFinishOrderNum : 1
             * accountName :
             * bankName :
             * cardNumber :
             * merchantFee : 0.200000
             * userFee : 0.800000
             * showReferenceNo : 0
             * merchantRemarks : test
             * currencySymbol : ¥
             * merchantRegisterTime : 2019-01-11 21:26:48
             * merchantFinishOrderNum : 2
             */

            private String site;
            private int type;
            private String orderId;
            private int productId;
            private String pairName;
            private String unitPrice;
            private String quantity;
            private String totalMoney;
            private int status;
            private String reference;
            private int placeId;
            private String createTime;
            private String updateTime;
            private String moneyTrade;
            private int supplierId;
            private String supplierName;
            private String placeName;
            private String userRegisterTime;
            private String userFinishOrderNum;
            private String accountName;
            private String bankName;
            private String cardNumber;
            private String merchantFee;
            private String userFee;
            private int showReferenceNo;
            private String merchantRemarks;
            private String currencySymbol;
            private String merchantRegisterTime;
            private String merchantFinishOrderNum;

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public String getOrderId() {
                return orderId;
            }

            public void setOrderId(String orderId) {
                this.orderId = orderId;
            }

            public int getProductId() {
                return productId;
            }

            public void setProductId(int productId) {
                this.productId = productId;
            }

            public String getPairName() {
                return pairName;
            }

            public void setPairName(String pairName) {
                this.pairName = pairName;
            }

            public String getUnitPrice() {
                return unitPrice;
            }

            public void setUnitPrice(String unitPrice) {
                this.unitPrice = unitPrice;
            }

            public String getQuantity() {
                return quantity;
            }

            public void setQuantity(String quantity) {
                this.quantity = quantity;
            }

            public String getTotalMoney() {
                return totalMoney;
            }

            public void setTotalMoney(String totalMoney) {
                this.totalMoney = totalMoney;
            }

            public int getStatus() {
                return status;
            }

            public void setStatus(int status) {
                this.status = status;
            }

            public String getReference() {
                return reference;
            }

            public void setReference(String reference) {
                this.reference = reference;
            }

            public int getPlaceId() {
                return placeId;
            }

            public void setPlaceId(int placeId) {
                this.placeId = placeId;
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

            public String getMoneyTrade() {
                return moneyTrade;
            }

            public void setMoneyTrade(String moneyTrade) {
                this.moneyTrade = moneyTrade;
            }

            public int getSupplierId() {
                return supplierId;
            }

            public void setSupplierId(int supplierId) {
                this.supplierId = supplierId;
            }

            public String getSupplierName() {
                return supplierName;
            }

            public void setSupplierName(String supplierName) {
                this.supplierName = supplierName;
            }

            public String getPlaceName() {
                return placeName;
            }

            public void setPlaceName(String placeName) {
                this.placeName = placeName;
            }

            public String getUserRegisterTime() {
                return userRegisterTime;
            }

            public void setUserRegisterTime(String userRegisterTime) {
                this.userRegisterTime = userRegisterTime;
            }

            public String getUserFinishOrderNum() {
                return userFinishOrderNum;
            }

            public void setUserFinishOrderNum(String userFinishOrderNum) {
                this.userFinishOrderNum = userFinishOrderNum;
            }

            public String getAccountName() {
                return accountName;
            }

            public void setAccountName(String accountName) {
                this.accountName = accountName;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public String getCardNumber() {
                return cardNumber;
            }

            public void setCardNumber(String cardNumber) {
                this.cardNumber = cardNumber;
            }

            public String getMerchantFee() {
                return merchantFee;
            }

            public void setMerchantFee(String merchantFee) {
                this.merchantFee = merchantFee;
            }

            public String getUserFee() {
                return userFee;
            }

            public void setUserFee(String userFee) {
                this.userFee = userFee;
            }

            public int getShowReferenceNo() {
                return showReferenceNo;
            }

            public void setShowReferenceNo(int showReferenceNo) {
                this.showReferenceNo = showReferenceNo;
            }

            public String getMerchantRemarks() {
                return merchantRemarks;
            }

            public void setMerchantRemarks(String merchantRemarks) {
                this.merchantRemarks = merchantRemarks;
            }

            public String getCurrencySymbol() {
                return currencySymbol;
            }

            public void setCurrencySymbol(String currencySymbol) {
                this.currencySymbol = currencySymbol;
            }

            public String getMerchantRegisterTime() {
                return merchantRegisterTime;
            }

            public void setMerchantRegisterTime(String merchantRegisterTime) {
                this.merchantRegisterTime = merchantRegisterTime;
            }

            public String getMerchantFinishOrderNum() {
                return merchantFinishOrderNum;
            }

            public void setMerchantFinishOrderNum(String merchantFinishOrderNum) {
                this.merchantFinishOrderNum = merchantFinishOrderNum;
            }
        }
    }
}
