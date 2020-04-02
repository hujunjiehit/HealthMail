package com.coinbene.manbiwang.model.http;

import android.os.Parcel;
import android.os.Parcelable;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.ArrayList;
import java.util.List;


public class OtcBindInfo extends BaseRes implements Parcelable {


    /**
     * data : {"otcPaymentWay":{"id":14,"userId":1000021,"site":"MAIN","userName":"hy","type":null,"bankName":"hy","telphone":"18510167357","bankAddress":"hy","bankAccount":"12484884","alipayAccount":"","alipayName":"","alipayQrCode":"","wechatAccount":"","wechatName":"","wechatQrCode":"","createTime":"2018-07-02 20:05:55","updateTime":"2018-07-02 20:05:55"},"bangStates":[1,0,0]}
     */

    private DataBean data;


    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Parcelable {
        /**
         * otcPaymentWay : {"id":14,"userId":1000021,"site":"MAIN","userName":"hy","type":null,"bankName":"hy","telphone":"18510167357","bankAddress":"hy","bankAccount":"12484884","alipayAccount":"","alipayName":"","alipayQrCode":"","wechatAccount":"","wechatName":"","wechatQrCode":"","createTime":"2018-07-02 20:05:55","updateTime":"2018-07-02 20:05:55"}
         * bangStates : [1,0,0]
         */

        private OtcPaymentWayBean otcPaymentWay;
        private List<Integer> bangStates;

        public OtcPaymentWayBean getOtcPaymentWay() {
            return otcPaymentWay;
        }

        public void setOtcPaymentWay(OtcPaymentWayBean otcPaymentWay) {
            this.otcPaymentWay = otcPaymentWay;
        }

        public List<Integer> getBangStates() {
            return bangStates;
        }

        public void setBangStates(List<Integer> bangStates) {
            this.bangStates = bangStates;
        }

        public static class OtcPaymentWayBean implements Parcelable {
            /**
             * id : 14
             * userId : 1000021
             * site : MAIN
             * userName : hy
             * type : null
             * bankName : hy
             * telphone : 18510167357
             * bankAddress : hy
             * bankAccount : 12484884
             * alipayAccount :
             * alipayName :
             * alipayQrCode :
             * wechatAccount :
             * wechatName :
             * wechatQrCode :
             * createTime : 2018-07-02 20:05:55
             * updateTime : 2018-07-02 20:05:55
             */

            private int id;
            private int userId;
            private String site;
            private String userName;
            private String type;
            private String bankName;
            private String telphone;
            private String bankAddress;
            private String bankAccount;
            private String alipayAccount;
            private String alipayName;
            private String alipayQrCode;
            private String wechatAccount;
            private String wechatName;
            private String wechatQrCode;
            private String createTime;
            private String updateTime;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getSite() {
                return site;
            }

            public void setSite(String site) {
                this.site = site;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getBankName() {
                return bankName;
            }

            public void setBankName(String bankName) {
                this.bankName = bankName;
            }

            public String getTelphone() {
                return telphone;
            }

            public void setTelphone(String telphone) {
                this.telphone = telphone;
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

            public String getAlipayAccount() {
                return alipayAccount;
            }

            public void setAlipayAccount(String alipayAccount) {
                this.alipayAccount = alipayAccount;
            }

            public String getAlipayName() {
                return alipayName;
            }

            public void setAlipayName(String alipayName) {
                this.alipayName = alipayName;
            }

            public String getAlipayQrCode() {
                return alipayQrCode;
            }

            public void setAlipayQrCode(String alipayQrCode) {
                this.alipayQrCode = alipayQrCode;
            }

            public String getWechatAccount() {
                return wechatAccount;
            }

            public void setWechatAccount(String wechatAccount) {
                this.wechatAccount = wechatAccount;
            }

            public String getWechatName() {
                return wechatName;
            }

            public void setWechatName(String wechatName) {
                this.wechatName = wechatName;
            }

            public String getWechatQrCode() {
                return wechatQrCode;
            }

            public void setWechatQrCode(String wechatQrCode) {
                this.wechatQrCode = wechatQrCode;
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

            @Override
            public int describeContents() {
                return 0;
            }

            @Override
            public void writeToParcel(Parcel dest, int flags) {
                dest.writeInt(this.id);
                dest.writeInt(this.userId);
                dest.writeString(this.site);
                dest.writeString(this.userName);
                dest.writeString(this.type);
                dest.writeString(this.bankName);
                dest.writeString(this.telphone);
                dest.writeString(this.bankAddress);
                dest.writeString(this.bankAccount);
                dest.writeString(this.alipayAccount);
                dest.writeString(this.alipayName);
                dest.writeString(this.alipayQrCode);
                dest.writeString(this.wechatAccount);
                dest.writeString(this.wechatName);
                dest.writeString(this.wechatQrCode);
                dest.writeString(this.createTime);
                dest.writeString(this.updateTime);
            }

            public OtcPaymentWayBean() {
            }

            protected OtcPaymentWayBean(Parcel in) {
                this.id = in.readInt();
                this.userId = in.readInt();
                this.site = in.readString();
                this.userName = in.readString();
                this.type = in.readParcelable(Object.class.getClassLoader());
                this.bankName = in.readString();
                this.telphone = in.readString();
                this.bankAddress = in.readString();
                this.bankAccount = in.readString();
                this.alipayAccount = in.readString();
                this.alipayName = in.readString();
                this.alipayQrCode = in.readString();
                this.wechatAccount = in.readString();
                this.wechatName = in.readString();
                this.wechatQrCode = in.readString();
                this.createTime = in.readString();
                this.updateTime = in.readString();
            }

            public static final Creator<OtcPaymentWayBean> CREATOR = new Creator<OtcPaymentWayBean>() {
                @Override
                public OtcPaymentWayBean createFromParcel(Parcel source) {
                    return new OtcPaymentWayBean(source);
                }

                @Override
                public OtcPaymentWayBean[] newArray(int size) {
                    return new OtcPaymentWayBean[size];
                }
            };
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.otcPaymentWay, flags);
            dest.writeList(this.bangStates);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.otcPaymentWay = in.readParcelable(OtcPaymentWayBean.class.getClassLoader());
            this.bangStates = new ArrayList<Integer>();
            in.readList(this.bangStates, Integer.class.getClassLoader());
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    public OtcBindInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.data, flags);
        dest.writeInt(this.code);
        dest.writeString(this.message);
    }

    protected OtcBindInfo(Parcel in) {
        this.data = in.readParcelable(DataBean.class.getClassLoader());
        this.code = in.readInt();
        this.message = in.readString();
    }

    public static final Creator<OtcBindInfo> CREATOR = new Creator<OtcBindInfo>() {
        @Override
        public OtcBindInfo createFromParcel(Parcel source) {
            return new OtcBindInfo(source);
        }

        @Override
        public OtcBindInfo[] newArray(int size) {
            return new OtcBindInfo[size];
        }
    };
}
