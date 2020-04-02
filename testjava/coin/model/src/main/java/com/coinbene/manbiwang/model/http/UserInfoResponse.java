package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 */

public class UserInfoResponse extends BaseRes {
    /**
     * data : {"auth":false,"bank":"string","email":"string","emailStatus":0,"googleBind":false,"loginId":"string","loginVerify":false,"phone":"string","pinSetting":false,"site":"string","userId":0,"verifyWay":"string"}
     * timezone : 0
     */

    public DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }


    public static class DataBean {
        /**
         * auth : false
         * bank : string
         * email : string
         * emailStatus : 0
         * googleBind : false
         * loginId : string
         * loginVerify : false
         * phone : string
         * pinSetting : false
         * site : string
         * userId : 0
         * verifyWay : string
         */

        public boolean auth;
        public String bank;
        public String email;
        public int emailStatus;
        public boolean googleBind;
        public String loginId;
        public boolean loginVerify;
        public String phone;
        public boolean pinSetting;
        public String site;
        public int userId;
        public String verifyWay;
        public String token;
        public String areaCode;

        public String refreshToken;
        public String transferBanReason;
        public String withdrawBanReason;
        public boolean kyc;
        public boolean payment;
        public Permissions permissions;
        public boolean supplier;
        public boolean transfer;

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String level;

        public String clientData; //通过jsbridge提供给web端

        public Permissions getPermissions() {
            return permissions;
        }

        public void setPermissions(Permissions permissions) {
            this.permissions = permissions;
        }

        public String getAreaCode() {
            return areaCode;
        }

        public void setAreaCode(String areaCode) {
            this.areaCode = areaCode;
        }

        public String coniDiscountSwitch;//ON 打开，OFF 关闭
        public String discountSwitchDes;//显示的文案
        public String displayUserId;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isAuth() {
            return auth;
        }

        public void setAuth(boolean auth) {
            this.auth = auth;
        }

        public String getBank() {
            return bank;
        }

        public void setBank(String bank) {
            this.bank = bank;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getEmailStatus() {
            return emailStatus;
        }

        public void setEmailStatus(int emailStatus) {
            this.emailStatus = emailStatus;
        }

        public boolean isGoogleBind() {
            return googleBind;
        }

        public void setGoogleBind(boolean googleBind) {
            this.googleBind = googleBind;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public boolean isLoginVerify() {
            return loginVerify;
        }

        public void setLoginVerify(boolean loginVerify) {
            this.loginVerify = loginVerify;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public boolean isPinSetting() {
            return pinSetting;
        }

        public void setPinSetting(boolean pinSetting) {
            this.pinSetting = pinSetting;
        }

        public String getSite() {
            return site;
        }

        public void setSite(String site) {
            this.site = site;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getVerifyWay() {
            return verifyWay;
        }

        public void setVerifyWay(String verifyWay) {
            this.verifyWay = verifyWay;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getConiDiscountSwitch() {
            return coniDiscountSwitch;
        }

        public void setConiDiscountSwitch(String coniDiscountSwitch) {
            this.coniDiscountSwitch = coniDiscountSwitch;
        }

        public String getClientData() {
            return clientData;
        }

        public void setClientData(String clientData) {
            this.clientData = clientData;
        }

        public static class Permissions {

            public boolean deposit;//1是true  0 是false
            public boolean withdraw;//1是true  0 是false

            public boolean fundTrans; // true：允许资金划转，  false：暂停资金划转

            public boolean isDeposit() {
                return deposit;
            }

            public void setDeposit(boolean deposit) {
                this.deposit = deposit;
            }

            public boolean isWithdraw() {
                return withdraw;
            }

            public void setWithdraw(boolean withdraw) {
                this.withdraw = withdraw;
            }

            public boolean isFundTrans() {
                return fundTrans;
            }

            public void setFundTrans(boolean fundTrans) {
                this.fundTrans = fundTrans;
            }
        }
    }
//    public String token;
//    public UserModel user;
//
//    public class UserModel {
//        public String createTime;
//        public String isActive;
//        public String isAuth;
//        public String loginId;
//        public String registIp;
//        public String userId;
//        public String isVip;//0=1
//        public String areaCode;//手机国家区号
//        public String phoneNo;//手机号
//        public String verifyWay;//首选验证方式1手机,5谷歌
//        public String email;
//        public String emailStatus;//1未绑定2未认证3已认证
//        public String mobileDevice;//谷歌验证
//        public String moneyPasswd;//设置与否
//
//        public String isLoginVerify;//是否开启二次验证，1开启
//
//    }


}
