package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangle on 2018/4/9.
 */

public class DepositListModel extends BaseRes {


    /**
     * data : {"list":[{"address":"string","amount":0,"asset":"string","orderTime":"string","remark":"string","status":"string","type":"string"}],"pages":0,"total":0}
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
         * list : [{"address":"string","amount":0,"asset":"string","orderTime":"string","remark":"string","status":"string","type":"string"}]
         * pages : 0
         * total : 0
         */

        public int pages;
        public int total;
        public List<ListBean> list;

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

        public static class ListBean extends RecodingRes implements Serializable {
            /**
             * address : string
             * amount : 0
             * asset : string
             * orderTime : string
             * remark : string
             * status : string
             * type : string'
             * <p>
             * body:{"code":200,"data":{"list":[
             * {"applyId":277849,"orderTime":"2018-11-06 23:45:25","asset":"USDT","amount":"6998.18","fee":"10","type":1,"status":4,"address":"18Z4F4LBP46UWQehJ9te7TAfU8JDktUJDN","txHash":"d2f8d46c48107c8e55c841ce1833844e9081b77c4a57d0e5b5a3568341a405ee","addrUrl":"https://omniexplorer.info/address/18Z4F4LBP46UWQehJ9te7TAfU8JDktUJDN","txUrl":"https://omniexplorer.info/tx/d2f8d46c48107c8e55c841ce1833844e9081b77c4a57d0e5b5a3568341a405ee"},
             * {"applyId":116463,"orderTime":"2018-06-24 12:59:55","asset":"USDT","amount":"3124.26","fee":"10","type":1,"status":4,"address":"18Z4F4LBP46UWQehJ9te7TAfU8JDktUJDN","txHash":"e521786aa0ebaa42ab83b1a628508ed07dc08fdbca3a140100cb97681d8d96cb","addrUrl":"https://omniexplorer.info/address/18Z4F4LBP46UWQehJ9te7TAfU8JDktUJDN","txUrl":"https://omniexplorer.info/tx/e521786aa0ebaa42ab83b1a628508ed07dc08fdbca3a140100cb97681d8d96cb"},{"applyId":95563,"orderTime":"2018-06-15 19:03:13","asset":"ETH","amount":"17.995000","fee":"0.005","type":1,"status":4,"address":"0x75e62f5fc10d80ba65a568bd8835db1aea487163","txHash":"0x0ebb30ec747877cabb7e203f1bdf6fca3622a5c430b5e936eb178104d9c3ee55","addrUrl":"https://etherscan.io/address/0x75e62f5fc10d80ba65a568bd8835db1aea487163","txUrl":"https://etherscan.io/tx/0x0ebb30ec747877cabb7e203f1bdf6fca3622a5c430b5e936eb178104d9c3ee55"}],"pages":1,"total":3}}
             * 11-11 12:10:46.619 9415-11883/com.coinbene.manbiwang I/HYHTTP: <-- END HTTP
             */

            private String address;
            private String amount;
            private String asset;
            private String orderTime;
            private String remark;
            private String status;
            private String type;
            private String applyId;
            private String fee;
            private String txHash;
            private String addrUrl;
            private String txUrl;
            private String localAssetName;

            public String getAddress() {
                return address;
            }

            public void setAddress(String address) {
                this.address = address;
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

            public String getOrderTime() {
                return orderTime;
            }

            public void setOrderTime(String orderTime) {
                this.orderTime = orderTime;
            }

            public String getRemark() {
                return remark;
            }

            public void setRemark(String remark) {
                this.remark = remark;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getApplyId() {
                return applyId;
            }

            public void setApplyId(String applyId) {
                this.applyId = applyId;
            }

            public String getFee() {
                return fee;
            }

            public void setFee(String fee) {
                this.fee = fee;
            }

            public String getTxHash() {
                return txHash;
            }

            public void setTxHash(String txHash) {
                this.txHash = txHash;
            }

            public String getAddrUrl() {
                return addrUrl;
            }

            public void setAddrUrl(String addrUrl) {
                this.addrUrl = addrUrl;
            }

            public String getTxUrl() {
                return txUrl;
            }

            public void setTxUrl(String txUrl) {
                this.txUrl = txUrl;
            }

            public String getLocalAssetName() {
                return localAssetName;
            }

            public void setLocalAssetName(String localAssetName) {
                this.localAssetName = localAssetName;
            }
        }
    }
}
