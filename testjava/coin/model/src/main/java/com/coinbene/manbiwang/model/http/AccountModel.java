package com.coinbene.manbiwang.model.http;//package com.coinbene.manbiwang.model;
//
//import com.coinbene.manbiwang.model.base.BaseRes;
//
//import java.io.Serializable;
//import java.util.List;
//
///**
// * Created by zhangle on 2018/4/8.
// */
//
//public class AccountModel extends BaseRes {
//
//
//    /**
//     * data : {"btcPreestimate":"string","list":[{"accountType":"string","asset":"string","bank":"string","deposit":false,"depositMinConfirmations":"string","depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","iconUrl":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","precision":"string","preestimateBTC":"string","smallAmountThreshold":"string","sort":0,"totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawMinConfirmations":"string","withdrawPrecision":"string"}],"localPreestimate":"string"}
//     * timezone : 0
//     */
//
//    private DataBean data;
//
//    public DataBean getData() {
//        return data;
//    }
//
//    public void setData(DataBean data) {
//        this.data = data;
//    }
//
//    public static class DataBean implements Serializable {
//        /**
//         * btcPreestimate : string
//         * list : [{"accountType":"string","asset":"string","bank":"string","deposit":false,"depositMinConfirmations":"string","depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","iconUrl":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","precision":"string","preestimateBTC":"string","smallAmountThreshold":"string","sort":0,"totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawMinConfirmations":"string","withdrawPrecision":"string"}]
//         * localPreestimate : string
//         */
//        private String preestimateType;
//        private String btcPreestimate;
//        private String localPreestimate;
//        private String currencySymbol;//$
//        private List<ListBean> list;
//
//        public String getPreestimateType() {
//            return preestimateType;
//        }
//
//        public void setPreestimateType(String preestimateType) {
//            this.preestimateType = preestimateType;
//        }
//
//        public String getBtcPreestimate() {
//            return btcPreestimate;
//        }
//
//        public void setBtcPreestimate(String btcPreestimate) {
//            this.btcPreestimate = btcPreestimate;
//        }
//
//        public String getLocalPreestimate() {
//            return localPreestimate;
//        }
//
//        public void setLocalPreestimate(String localPreestimate) {
//            this.localPreestimate = localPreestimate;
//        }
//
//        public List<ListBean> getList() {
//            return list;
//        }
//
//        public void setList(List<ListBean> list) {
//            this.list = list;
//        }
//
//        public String getCurrencySymbol() {
//            return currencySymbol;
//        }
//
//        public void setCurrencySymbol(String currencySymbol) {
//            this.currencySymbol = currencySymbol;
//        }
//
//        public static class ListBean  implements Serializable{
//            /**
//             * accountType : string
//             * asset : string
//             * bank : string
//             * deposit : false
//             * depositMinConfirmations : string
//             * depositPrecision : string
//             * englishAssetName : string
//             * frozenBalance : string
//             * iconUrl : string
//             * localAssetName : string
//             * localPreestimate : string
//             * minDeposit : string
//             * minWithdraw : string
//             * precision : string
//             * preestimateBTC : string
//             * smallAmountThreshold : string
//             * sort : 0
//             * totalBalance : string
//             * transfer : false
//             * transferPrecision : string
//             * useTag : false
//             * withdraw : false
//             * withdrawFee : string
//             * withdrawMinConfirmations : string
//             * withdrawPrecision : string
//             */
//
//            private String accountType;
//            private String asset;
//            private String bank;
//            private boolean deposit;
//            private String depositMinConfirmations;
//            private String depositPrecision;
//            private String englishAssetName;
//            private String frozenBalance;
//            private String iconUrl;
//            private String localAssetName;
//            private String localPreestimate;
//            private String minDeposit;
//            private String minWithdraw;
//            private String precision;
//            private String preestimateBTC;
//            private String smallAmountThreshold;
//            private int sort;
//            private String totalBalance;
//            private boolean transfer;
//            private String transferPrecision;
//            private boolean useTag;
//            private boolean withdraw;
//            private String withdrawFee;
//            private String withdrawMinConfirmations;
//            private String withdrawPrecision;
//            private String balance;
//            public String depositHints;//充值提示语 ,
//            public String tagLabel;//tag标签名称  ,
//            public String withdrawHints;//提现提示语 , ,
//            public boolean withdrawTag;//提现tag是否必填 ,
//
//            private boolean isValueHide;//是否隐藏币的价格
//            private String currencySymbol;//$
//
//            private String banDepositReason;//禁止充币 原因
//            private String banWithdrawReason;//禁止 提币 原因
//            private String banTransferReason;//禁止转账 原因
//
//            public String getAccountType() {
//                return accountType;
//            }
//
//            public String getBalance() {
//                return balance;
//            }
//
//            public void setBalance(String balance) {
//                this.balance = balance;
//            }
//
//            public void setAccountType(String accountType) {
//                this.accountType = accountType;
//            }
//
//            public String getAsset() {
//                return asset;
//            }
//
//            public void setAsset(String asset) {
//                this.asset = asset;
//            }
//
//            public String getBank() {
//                return bank;
//            }
//
//            public void setBank(String bank) {
//                this.bank = bank;
//            }
//
//            public boolean isDeposit() {
//                return deposit;
//            }
//
//            public void setDeposit(boolean deposit) {
//                this.deposit = deposit;
//            }
//
//            public String getDepositMinConfirmations() {
//                return depositMinConfirmations;
//            }
//
//            public void setDepositMinConfirmations(String depositMinConfirmations) {
//                this.depositMinConfirmations = depositMinConfirmations;
//            }
//
//            public String getDepositPrecision() {
//                return depositPrecision;
//            }
//
//            public void setDepositPrecision(String depositPrecision) {
//                this.depositPrecision = depositPrecision;
//            }
//
//            public String getEnglishAssetName() {
//                return englishAssetName;
//            }
//
//            public void setEnglishAssetName(String englishAssetName) {
//                this.englishAssetName = englishAssetName;
//            }
//
//            public String getFrozenBalance() {
//                return frozenBalance;
//            }
//
//            public void setFrozenBalance(String frozenBalance) {
//                this.frozenBalance = frozenBalance;
//            }
//
//            public String getIconUrl() {
//                return iconUrl;
//            }
//
//            public void setIconUrl(String iconUrl) {
//                this.iconUrl = iconUrl;
//            }
//
//            public String getLocalAssetName() {
//                return localAssetName;
//            }
//
//            public void setLocalAssetName(String localAssetName) {
//                this.localAssetName = localAssetName;
//            }
//
//            public String getLocalPreestimate() {
//                return localPreestimate;
//            }
//
//            public void setLocalPreestimate(String localPreestimate) {
//                this.localPreestimate = localPreestimate;
//            }
//
//            public String getMinDeposit() {
//                return minDeposit;
//            }
//
//            public void setMinDeposit(String minDeposit) {
//                this.minDeposit = minDeposit;
//            }
//
//            public String getMinWithdraw() {
//                return minWithdraw;
//            }
//
//            public void setMinWithdraw(String minWithdraw) {
//                this.minWithdraw = minWithdraw;
//            }
//
//            public String getPrecision() {
//                return precision;
//            }
//
//            public void setPrecision(String precision) {
//                this.precision = precision;
//            }
//
//            public String getPreestimateBTC() {
//                return preestimateBTC;
//            }
//
//            public void setPreestimateBTC(String preestimateBTC) {
//                this.preestimateBTC = preestimateBTC;
//            }
//
//            public String getSmallAmountThreshold() {
//                return smallAmountThreshold;
//            }
//
//            public void setSmallAmountThreshold(String smallAmountThreshold) {
//                this.smallAmountThreshold = smallAmountThreshold;
//            }
//
//            public int getSort() {
//                return sort;
//            }
//
//            public void setSort(int sort) {
//                this.sort = sort;
//            }
//
//            public String getTotalBalance() {
//                return totalBalance;
//            }
//
//            public void setTotalBalance(String totalBalance) {
//                this.totalBalance = totalBalance;
//            }
//
//            public boolean isTransfer() {
//                return transfer;
//            }
//
//            public void setTransfer(boolean transfer) {
//                this.transfer = transfer;
//            }
//
//            public String getTransferPrecision() {
//                return transferPrecision;
//            }
//
//            public void setTransferPrecision(String transferPrecision) {
//                this.transferPrecision = transferPrecision;
//            }
//
//            public boolean isUseTag() {
//                return useTag;
//            }
//
//            public void setUseTag(boolean useTag) {
//                this.useTag = useTag;
//            }
//
//            public boolean isWithdraw() {
//                return withdraw;
//            }
//
//            public void setWithdraw(boolean withdraw) {
//                this.withdraw = withdraw;
//            }
//
//            public String getWithdrawFee() {
//                return withdrawFee;
//            }
//
//            public void setWithdrawFee(String withdrawFee) {
//                this.withdrawFee = withdrawFee;
//            }
//
//            public String getWithdrawMinConfirmations() {
//                return withdrawMinConfirmations;
//            }
//
//            public void setWithdrawMinConfirmations(String withdrawMinConfirmations) {
//                this.withdrawMinConfirmations = withdrawMinConfirmations;
//            }
//
//            public String getWithdrawPrecision() {
//                return withdrawPrecision;
//            }
//
//            public void setWithdrawPrecision(String withdrawPrecision) {
//                this.withdrawPrecision = withdrawPrecision;
//            }
//
//            public boolean isValueHide() {
//                return isValueHide;
//            }
//
//            public void setValueHide(boolean valueHide) {
//                isValueHide = valueHide;
//            }
//
//            public String getCurrencySymbol() {
//                return currencySymbol;
//            }
//
//            public void setCurrencySymbol(String currencySymbol) {
//                this.currencySymbol = currencySymbol;
//            }
//
//            public String getBanDepositReason() {
//                return banDepositReason;
//            }
//
//            public void setBanDepositReason(String banDepositReason) {
//                this.banDepositReason = banDepositReason;
//            }
//
//            public String getBanWithdrawReason() {
//                return banWithdrawReason;
//            }
//
//            public void setBanWithdrawReason(String banWithdrawReason) {
//                this.banWithdrawReason = banWithdrawReason;
//            }
//
//            public String getBanTransferReason() {
//                return banTransferReason;
//            }
//
//            public void setBanTransferReason(String banTransferReason) {
//                this.banTransferReason = banTransferReason;
//            }
//        }
//    }
//}
