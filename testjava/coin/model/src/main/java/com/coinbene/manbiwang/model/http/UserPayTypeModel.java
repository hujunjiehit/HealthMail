package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

public class UserPayTypeModel extends BaseRes {


    /**
     * data : {"paymentWayList":[{"id":101,"userName":"huyong","type":1,"bankName":"sbsbhsqbz","bankAddress":"xwjbbuwx","bankAccount":"5748274387381","payAccount":"","payQrCode":"","online":0}]}
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<PaymentWayListBean> paymentWayList;

        public List<PaymentWayListBean> getPaymentWayList() {
            return paymentWayList;
        }

        public void setPaymentWayList(List<PaymentWayListBean> paymentWayList) {
            this.paymentWayList = paymentWayList;
        }

        public static class PaymentWayListBean {
            /**
             * id : 101
             * userName : huyong
             * type : 1
             * bankName : sbsbhsqbz
             * bankAddress : xwjbbuwx
             * bankAccount : 5748274387381
             * payAccount :
             * payQrCode :
             * online : 0
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
    }
}
