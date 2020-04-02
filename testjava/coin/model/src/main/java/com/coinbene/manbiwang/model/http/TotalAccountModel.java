package com.coinbene.manbiwang.model.http;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mxd on 2019/3/5.
 */

public class TotalAccountModel extends BaseRes {


    /**
     * data : {"accountData":{"btcPreestimate":"string","currencySymbol":"string","list":[{"accountType":"string","asset":"string","assetType":"string","availableBalance":"string","banDepositReason":"string","banTransferReason":"string","banWithdrawReason":"string","bank":"string","deposit":false,"depositHints":"string","depositMinConfirmations":0,"depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","preestimateBTC":"string","sort":0,"tagLabel":"string","tagRule":"string","totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawHints":"string","withdrawMinConfirmations":0,"withdrawPrecision":"string","withdrawTag":false}],"localPreestimate":"string","preestimateType":"string"},"btcTotalPreestimate":"string","contractData":{"availableBalance":"string","balance":"string","btcPreestimate":"string","frozenBalance":"string","localPreestimate":"string","marginRate":"string","positionMargin":"string","unrealisedPnl":"string"},"currencySymbol":"string","localTotalPreestimate":"string","preestimateType":"string"}
     * timezone : 0
     */

    private DataBean data;

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * accountData : {"btcPreestimate":"string","currencySymbol":"string","list":[{"accountType":"string","asset":"string","assetType":"string","availableBalance":"string","banDepositReason":"string","banTransferReason":"string","banWithdrawReason":"string","bank":"string","deposit":false,"depositHints":"string","depositMinConfirmations":0,"depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","preestimateBTC":"string","sort":0,"tagLabel":"string","tagRule":"string","totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawHints":"string","withdrawMinConfirmations":0,"withdrawPrecision":"string","withdrawTag":false}],"localPreestimate":"string","preestimateType":"string"}
         * btcTotalPreestimate : string
         * contractData : {"availableBalance":"string","balance":"string","btcPreestimate":"string","frozenBalance":"string","localPreestimate":"string","marginRate":"string","positionMargin":"string","unrealisedPnl":"string"}
         * currencySymbol : string
         * localTotalPreestimate : string
         * preestimateType : string
         */

        private AccountDataBean accountData;
        private String btcTotalPreestimate;
        private ContractDataBean contractData;
        private ContractDataBean usdtContractData;
        private String currencySymbol;
        private String localTotalPreestimate;
        private String preestimateType;
        private OptionDataBean optionData;
        private MarginDataBean marginData;
        private GameDataBean gameData;
        private FinancialDataBean financialAccountData;

        public AccountDataBean getAccountData() {
            return accountData;
        }

        public void setAccountData(AccountDataBean accountData) {
            this.accountData = accountData;
        }

        public String getBtcTotalPreestimate() {
            return btcTotalPreestimate;
        }

        public void setBtcTotalPreestimate(String btcTotalPreestimate) {
            this.btcTotalPreestimate = btcTotalPreestimate;
        }

        public ContractDataBean getContractData() {
            return contractData;
        }

        public void setContractData(ContractDataBean contractData) {
            this.contractData = contractData;
        }

        public ContractDataBean getUsdtContractData() {
            return usdtContractData;
        }

        public void setUsdtContractData(ContractDataBean usdtContractData) {
            this.usdtContractData = usdtContractData;
        }

        public String getCurrencySymbol() {
            return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
            this.currencySymbol = currencySymbol;
        }

        public String getLocalTotalPreestimate() {
            return localTotalPreestimate;
        }

        public void setLocalTotalPreestimate(String localTotalPreestimate) {
            this.localTotalPreestimate = localTotalPreestimate;
        }

        public String getPreestimateType() {
            return preestimateType;
        }

        public void setPreestimateType(String preestimateType) {
            this.preestimateType = preestimateType;
        }

        public OptionDataBean getOptionData() {
            return optionData;
        }

        public void setOptionData(OptionDataBean optionData) {
            this.optionData = optionData;
        }

        public MarginDataBean getMarginData() {
            return marginData;
        }

        public void setMarginData(MarginDataBean marginData) {
            this.marginData = marginData;
        }

        public GameDataBean getGameData() {
            return gameData;
        }

        public void setGameData(GameDataBean gameData) {
            this.gameData = gameData;
        }

        public FinancialDataBean getFinancialAccountData() {
            return financialAccountData;
        }

        public void setFinancialAccountData(FinancialDataBean financialAccountData) {
            this.financialAccountData = financialAccountData;
        }

        public static class AccountDataBean implements Serializable{
            /**
             * btcPreestimate : string
             * currencySymbol : string
             * list : [{"accountType":"string","asset":"string","assetType":"string","availableBalance":"string","banDepositReason":"string","banTransferReason":"string","banWithdrawReason":"string","bank":"string","deposit":false,"depositHints":"string","depositMinConfirmations":0,"depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","preestimateBTC":"string","sort":0,"tagLabel":"string","tagRule":"string","totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawHints":"string","withdrawMinConfirmations":0,"withdrawPrecision":"string","withdrawTag":false}]
             * localPreestimate : string
             * preestimateType : string
             */

            private String btcPreestimate;
            private String currencySymbol;
            private String localPreestimate;
            private String preestimateType;
            private List<ListBean> list;

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getCurrencySymbol() {
                return currencySymbol;
            }

            public void setCurrencySymbol(String currencySymbol) {
                this.currencySymbol = currencySymbol;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public String getPreestimateType() {
                return preestimateType;
            }

            public void setPreestimateType(String preestimateType) {
                this.preestimateType = preestimateType;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean implements Serializable {
                /**
                 * accountType : string
                 * asset : string
                 * assetType : string
                 * availableBalance : string
                 * banDepositReason : string
                 * banTransferReason : string
                 * banWithdrawReason : string
                 * bank : string
                 * deposit : false
                 * depositHints : string
                 * depositMinConfirmations : 0
                 * depositPrecision : string
                 * englishAssetName : string
                 * frozenBalance : string
                 * localAssetName : string
                 * localPreestimate : string
                 * minDeposit : string
                 * minWithdraw : string
                 * preestimateBTC : string
                 * sort : 0
                 * tagLabel : string
                 * tagRule : string
                 * totalBalance : string
                 * transfer : false
                 * transferPrecision : string
                 * useTag : false
                 * withdraw : false
                 * withdrawFee : string
                 * withdrawHints : string
                 * withdrawMinConfirmations : 0
                 * withdrawPrecision : string
                 * withdrawTag : false
                 */

                private String accountType;
                private String asset;
                private String assetType;
                private String availableBalance;
                private String banDepositReason;
                private String banTransferReason;
                private String banWithdrawReason;
                private String bank;
                private boolean deposit;
                private String depositHints;
                private int depositMinConfirmations;
                private String depositPrecision;
                private String englishAssetName;
                private String frozenBalance;
                private String localAssetName;
                private String localPreestimate;
                private String minDeposit;
                private String minWithdraw;
                private String preestimateBTC;
                private int sort;
                private String tagLabel;
                private String tagRule;
                private String totalBalance;
                private boolean transfer;
                private String transferPrecision;
                private boolean useTag;
                private boolean withdraw;
                private String withdrawFee;
                private String withdrawHints;
                private int withdrawMinConfirmations;
                private String withdrawPrecision;
                private boolean withdrawTag;
                private String chain;
                private boolean hideValue;
                private List<Chain> chains;

                public String getChain() {
                    return chain;
                }

                public void setChain(String chain) {
                    this.chain = chain;
                }

                public List<Chain> getChains() {
                    return chains;
                }

                public void setChains(List<Chain> chains) {
                    this.chains = chains;
                }

                public static class Chain implements Serializable {
                    private String tagLabel;
                    private String chain;
                    private String asset;
                    private String protocolName;
                    private String bank;
                    private String withdrawPrecision;
                    private String transferPrecision;
                    private String depositPrecision;
                    private boolean deposit;
                    private boolean transfer;
                    private boolean withdraw;
                    private boolean useTag;
                    private String minDeposit;
                    private String minWithdraw;
                    private String withdrawFee;
                    private int withdrawMinConfirmations;
                    private String depositMinConfirmations;
                    private int assetType;
                    private String banDepositReason;
                    private String banWithdrawReason;
                    private String banTransferReason;
                    public String depositHints;
                    public String withdrawHints;

                    public String getChain() {
                        return chain;
                    }

                    public void setChain(String chain) {
                        this.chain = chain;
                    }

                    public String getDepositHints() {
                        return depositHints;
                    }

                    public void setDepositHints(String depositHints) {
                        this.depositHints = depositHints;
                    }

                    public String getTagLabel() {
                        return tagLabel;
                    }

                    public void setTagLabel(String tagLabel) {
                        this.tagLabel = tagLabel;
                    }

                    public String getWithdrawHints() {
                        return withdrawHints;
                    }

                    public void setWithdrawHints(String withdrawHints) {
                        this.withdrawHints = withdrawHints;
                    }

                    public String getAsset() {
                        return asset;
                    }

                    public void setAsset(String asset) {
                        this.asset = asset;
                    }

                    public String getProtocolName() {
                        return protocolName;
                    }

                    public void setProtocolName(String protocolName) {
                        this.protocolName = protocolName;
                    }

                    public String getBank() {
                        return bank;
                    }

                    public void setBank(String bank) {
                        this.bank = bank;
                    }


                    public String getWithdrawPrecision() {
                        return withdrawPrecision;
                    }

                    public void setWithdrawPrecision(String withdrawPrecision) {
                        this.withdrawPrecision = withdrawPrecision;
                    }

                    public String getTransferPrecision() {
                        return transferPrecision;
                    }

                    public void setTransferPrecision(String transferPrecision) {
                        this.transferPrecision = transferPrecision;
                    }

                    public String getDepositPrecision() {
                        return depositPrecision;
                    }

                    public void setDepositPrecision(String depositPrecision) {
                        this.depositPrecision = depositPrecision;
                    }

                    public boolean isDeposit() {
                        return deposit;
                    }

                    public void setDeposit(boolean deposit) {
                        this.deposit = deposit;
                    }

                    public boolean isTransfer() {
                        return transfer;
                    }

                    public void setTransfer(boolean transfer) {
                        this.transfer = transfer;
                    }

                    public boolean isWithdraw() {
                        return withdraw;
                    }

                    public void setWithdraw(boolean withdraw) {
                        this.withdraw = withdraw;
                    }

                    public boolean isUseTag() {
                        return useTag;
                    }

                    public void setUseTag(boolean useTag) {
                        this.useTag = useTag;
                    }

                    public String getMinDeposit() {
                        return minDeposit;
                    }

                    public void setMinDeposit(String minDeposit) {
                        this.minDeposit = minDeposit;
                    }

                    public String getMinWithdraw() {
                        return minWithdraw;
                    }

                    public void setMinWithdraw(String minWithdraw) {
                        this.minWithdraw = minWithdraw;
                    }

                    public String getWithdrawFee() {
                        return withdrawFee;
                    }

                    public void setWithdrawFee(String withdrawFee) {
                        this.withdrawFee = withdrawFee;
                    }

                    public int getWithdrawMinConfirmations() {
                        return withdrawMinConfirmations;
                    }

                    public void setWithdrawMinConfirmations(int withdrawMinConfirmations) {
                        this.withdrawMinConfirmations = withdrawMinConfirmations;
                    }

                    public String getDepositMinConfirmations() {
                        return depositMinConfirmations;
                    }

                    public void setDepositMinConfirmations(String depositMinConfirmations) {
                        this.depositMinConfirmations = depositMinConfirmations;
                    }

                    public int getAssetType() {
                        return assetType;
                    }

                    public void setAssetType(int assetType) {
                        this.assetType = assetType;
                    }

                    public String getBanDepositReason() {
                        return banDepositReason;
                    }

                    public void setBanDepositReason(String banDepositReason) {
                        this.banDepositReason = banDepositReason;
                    }

                    public String getBanWithdrawReason() {
                        return banWithdrawReason;
                    }

                    public void setBanWithdrawReason(String banWithdrawReason) {
                        this.banWithdrawReason = banWithdrawReason;
                    }

                    public String getBanTransferReason() {
                        return banTransferReason;
                    }

                    public void setBanTransferReason(String banTransferReason) {
                        this.banTransferReason = banTransferReason;
                    }
                }

                public String getAccountType() {
                    return accountType;
                }

                public void setAccountType(String accountType) {
                    this.accountType = accountType;
                }

                public String getAsset() {
                    return asset;
                }

                public void setAsset(String asset) {
                    this.asset = asset;
                }

                public String getAssetType() {
                    return assetType;
                }

                public void setAssetType(String assetType) {
                    this.assetType = assetType;
                }

                public String getAvailableBalance() {
                    return availableBalance;
                }

                public void setAvailableBalance(String availableBalance) {
                    this.availableBalance = availableBalance;
                }

                public String getBanDepositReason() {
                    return banDepositReason;
                }

                public void setBanDepositReason(String banDepositReason) {
                    this.banDepositReason = banDepositReason;
                }

                public String getBanTransferReason() {
                    return banTransferReason;
                }

                public void setBanTransferReason(String banTransferReason) {
                    this.banTransferReason = banTransferReason;
                }

                public String getBanWithdrawReason() {
                    return banWithdrawReason;
                }

                public void setBanWithdrawReason(String banWithdrawReason) {
                    this.banWithdrawReason = banWithdrawReason;
                }

                public String getBank() {
                    return bank;
                }

                public void setBank(String bank) {
                    this.bank = bank;
                }

                public boolean isDeposit() {
                    return deposit;
                }

                public void setDeposit(boolean deposit) {
                    this.deposit = deposit;
                }

                public String getDepositHints() {
                    return depositHints;
                }

                public void setDepositHints(String depositHints) {
                    this.depositHints = depositHints;
                }

                public int getDepositMinConfirmations() {
                    return depositMinConfirmations;
                }

                public void setDepositMinConfirmations(int depositMinConfirmations) {
                    this.depositMinConfirmations = depositMinConfirmations;
                }

                public String getDepositPrecision() {
                    return depositPrecision;
                }

                public void setDepositPrecision(String depositPrecision) {
                    this.depositPrecision = depositPrecision;
                }

                public String getEnglishAssetName() {
                    return englishAssetName;
                }

                public void setEnglishAssetName(String englishAssetName) {
                    this.englishAssetName = englishAssetName;
                }

                public String getFrozenBalance() {
                    return frozenBalance;
                }

                public void setFrozenBalance(String frozenBalance) {
                    this.frozenBalance = frozenBalance;
                }

                public String getLocalAssetName() {
                    return localAssetName;
                }

                public void setLocalAssetName(String localAssetName) {
                    this.localAssetName = localAssetName;
                }

                public String getLocalPreestimate() {
                    return localPreestimate;
                }

                public void setLocalPreestimate(String localPreestimate) {
                    this.localPreestimate = localPreestimate;
                }

                public String getMinDeposit() {
                    return minDeposit;
                }

                public void setMinDeposit(String minDeposit) {
                    this.minDeposit = minDeposit;
                }

                public String getMinWithdraw() {
                    return minWithdraw;
                }

                public void setMinWithdraw(String minWithdraw) {
                    this.minWithdraw = minWithdraw;
                }

                public String getPreestimateBTC() {
                    return preestimateBTC;
                }

                public void setPreestimateBTC(String preestimateBTC) {
                    this.preestimateBTC = preestimateBTC;
                }

                public int getSort() {
                    return sort;
                }

                public void setSort(int sort) {
                    this.sort = sort;
                }

                public String getTagLabel() {
                    return tagLabel;
                }

                public void setTagLabel(String tagLabel) {
                    this.tagLabel = tagLabel;
                }

                public String getTagRule() {
                    return tagRule;
                }

                public void setTagRule(String tagRule) {
                    this.tagRule = tagRule;
                }

                public String getTotalBalance() {
                    return totalBalance;
                }

                public void setTotalBalance(String totalBalance) {
                    this.totalBalance = totalBalance;
                }

                public boolean isTransfer() {
                    return transfer;
                }

                public void setTransfer(boolean transfer) {
                    this.transfer = transfer;
                }

                public String getTransferPrecision() {
                    return transferPrecision;
                }

                public void setTransferPrecision(String transferPrecision) {
                    this.transferPrecision = transferPrecision;
                }

                public boolean isUseTag() {
                    return useTag;
                }

                public void setUseTag(boolean useTag) {
                    this.useTag = useTag;
                }

                public boolean isWithdraw() {
                    return withdraw;
                }

                public void setWithdraw(boolean withdraw) {
                    this.withdraw = withdraw;
                }

                public String getWithdrawFee() {
                    return withdrawFee;
                }

                public void setWithdrawFee(String withdrawFee) {
                    this.withdrawFee = withdrawFee;
                }

                public String getWithdrawHints() {
                    return withdrawHints;
                }

                public void setWithdrawHints(String withdrawHints) {
                    this.withdrawHints = withdrawHints;
                }

                public int getWithdrawMinConfirmations() {
                    return withdrawMinConfirmations;
                }

                public void setWithdrawMinConfirmations(int withdrawMinConfirmations) {
                    this.withdrawMinConfirmations = withdrawMinConfirmations;
                }

                public String getWithdrawPrecision() {
                    return withdrawPrecision;
                }

                public void setWithdrawPrecision(String withdrawPrecision) {
                    this.withdrawPrecision = withdrawPrecision;
                }

                public boolean isWithdrawTag() {
                    return withdrawTag;
                }

                public void setWithdrawTag(boolean withdrawTag) {
                    this.withdrawTag = withdrawTag;
                }

                public void setValueHide(boolean hideValue) {
                    this.hideValue = hideValue;
                }

                public boolean getValueHide() {
                    return hideValue;
                }

            }
        }

        public static class ContractDataBean implements Serializable {
            /**
             * availableBalance : string
             * balance : string
             * btcPreestimate : string
             * frozenBalance : string
             * localPreestimate : string
             * marginRate : string
             * positionMargin : string
             * unrealisedPnl : string
             */


            private String availableBalance;
            private String balance;
            private String btcPreestimate;
            private String frozenBalance;
            private String localPreestimate;
            private String marginRate;
            private String positionMargin;
            private String unrealisedPnl;
            private String preestimate;

            /**
             * userId : 0
             * symbol : BTC
             * roe : 0.0002
             * active : 1
             * longQuantity : 0
             * shortQuantity : 0
             */
            private int userId;
            private String symbol;
            private String roe;
            private int active;
            private int longQuantity;
            private int shortQuantity;

            public String getAvailableBalance() {
                return availableBalance;
            }

            public void setAvailableBalance(String availableBalance) {
                this.availableBalance = availableBalance;
            }

            public String getBalance() {
                return balance;
            }

            public void setBalance(String balance) {
                this.balance = balance;
            }

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getFrozenBalance() {
                return frozenBalance;
            }

            public void setFrozenBalance(String frozenBalance) {
                this.frozenBalance = frozenBalance;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public String getMarginRate() {
                return marginRate;
            }

            public void setMarginRate(String marginRate) {
                this.marginRate = marginRate;
            }

            public String getPositionMargin() {
                return positionMargin;
            }

            public void setPositionMargin(String positionMargin) {
                this.positionMargin = positionMargin;
            }

            public String getUnrealisedPnl() {
                return unrealisedPnl;
            }

            public void setUnrealisedPnl(String unrealisedPnl) {
                this.unrealisedPnl = unrealisedPnl;
            }

            public int getUserId() {
                return userId;
            }

            public void setUserId(int userId) {
                this.userId = userId;
            }

            public String getSymbol() {
                return symbol;
            }

            public void setSymbol(String symbol) {
                this.symbol = symbol;
            }

            public String getRoe() {
                return roe;
            }

            public void setRoe(String roe) {
                this.roe = roe;
            }

            public int getActive() {
                return active;
            }

            public void setActive(int active) {
                this.active = active;
            }

            public int getLongQuantity() {
                return longQuantity;
            }

            public void setLongQuantity(int longQuantity) {
                this.longQuantity = longQuantity;
            }

            public int getShortQuantity() {
                return shortQuantity;
            }

            public void setShortQuantity(int shortQuantity) {
                this.shortQuantity = shortQuantity;
            }

            public String getPreestimate() {
                return preestimate;
            }

            public void setPreestimate(String preestimate) {
                this.preestimate = preestimate;
            }
        }

        public static class OptionDataBean implements Serializable{
            /**
             * btcPreestimate : string
             * currencySymbol : string
             * list : [{"accountType":"string","asset":"string","assetType":"string","availableBalance":"string","banDepositReason":"string","banTransferReason":"string","banWithdrawReason":"string","bank":"string","deposit":false,"depositHints":"string","depositMinConfirmations":0,"depositPrecision":"string","englishAssetName":"string","frozenBalance":"string","localAssetName":"string","localPreestimate":"string","minDeposit":"string","minWithdraw":"string","preestimateBTC":"string","sort":0,"tagLabel":"string","tagRule":"string","totalBalance":"string","transfer":false,"transferPrecision":"string","useTag":false,"withdraw":false,"withdrawFee":"string","withdrawHints":"string","withdrawMinConfirmations":0,"withdrawPrecision":"string","withdrawTag":false}]
             * localPreestimate : string
             * preestimateType : string
             */

            private String btcPreestimate;
            private String localPreestimate;
            private List<ListBean> list;

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean implements Serializable {


                /**
                 * brokerId : 14
                 * assetId : 13
                 * assetName : CONI
                 * amount : 1.000000000000000000
                 * sort : 1
                 * preestimateBTC : 0.00001700
                 * localPreestimate : 0
                 */

                private String brokerId;
                private int assetId;
                private String assetName;
                private String amount;
                private int sort;
                private String preestimateBTC;
                private String localPreestimate;
                private boolean hideValue;

                public String getBrokerId() {
                    return brokerId;
                }

                public void setBrokerId(String brokerId) {
                    this.brokerId = brokerId;
                }

                public int getAssetId() {
                    return assetId;
                }

                public void setAssetId(int assetId) {
                    this.assetId = assetId;
                }

                public String getAssetName() {
                    return assetName;
                }

                public void setAssetName(String assetName) {
                    this.assetName = assetName;
                }

                public String getAmount() {
                    return amount;
                }

                public void setAmount(String amount) {
                    this.amount = amount;
                }

                public int getSort() {
                    return sort;
                }

                public void setSort(int sort) {
                    this.sort = sort;
                }

                public String getPreestimateBTC() {
                    return preestimateBTC;
                }

                public void setPreestimateBTC(String preestimateBTC) {
                    this.preestimateBTC = preestimateBTC;
                }

                public String getLocalPreestimate() {
                    return localPreestimate;
                }

                public void setLocalPreestimate(String localPreestimate) {
                    this.localPreestimate = localPreestimate;
                }

                public boolean isHideValue() {
                    return hideValue;
                }

                public void setHideValue(boolean hideValue) {
                    this.hideValue = hideValue;
                }
            }
        }

        public static class MarginDataBean implements Serializable {

            /**
             * btcPreestimate : 0
             * localPreestimate : 0
             * list : [{"block":"0","forceClosePrice":"--","riskRate":"0","symbol":"ETH/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"ETH","frozen":"0.00000000"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000"}]},{"block":"0","forceClosePrice":"--","riskRate":"0","symbol":"BTC/USDT","balanceList":[{"available":"0.00000000","borrow":"0.00000000","asset":"BTC","frozen":"0.00000000"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000"}]}]
             */

            private String btcPreestimate;
            private String localPreestimate;
            private List<ListBean> list;

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean implements Serializable {
                /**
                 * block : 0
                 * forceClosePrice : --
                 * riskRate : 0
                 * symbol : ETH/USDT
                 * balanceList : [{"available":"0.00000000","borrow":"0.00000000","asset":"ETH","frozen":"0.00000000"},{"available":"0.00000000","borrow":"0.00000000","asset":"USDT","frozen":"0.00000000"}]
                 */

                private String block;
                private String forceClosePrice;
                private String riskRate;
                private String symbol;
                private List<BalanceListBean> balanceList;

                public String getBlock() {
                    return block;
                }

                public void setBlock(String block) {
                    this.block = block;
                }

                public String getForceClosePrice() {
                    return forceClosePrice;
                }

                public void setForceClosePrice(String forceClosePrice) {
                    this.forceClosePrice = forceClosePrice;
                }

                public String getRiskRate() {
                    return riskRate;
                }

                public void setRiskRate(String riskRate) {
                    this.riskRate = riskRate;
                }

                public String getSymbol() {
                    return symbol;
                }

                public void setSymbol(String symbol) {
                    this.symbol = symbol;
                }

                public List<BalanceListBean> getBalanceList() {
                    return balanceList;
                }

                public void setBalanceList(List<BalanceListBean> balanceList) {
                    this.balanceList = balanceList;
                }

                public static class BalanceListBean implements Serializable{
                    /**
                     * available : 0.00000000
                     * borrow : 0.00000000
                     * asset : ETH
                     * frozen : 0.00000000
                     */

                    private String available;
                    private String borrow;
                    private String asset;
                    private String frozen;

                    public String getAvailable() {
                        return available;
                    }

                    public void setAvailable(String available) {
                        this.available = available;
                    }

                    public String getBorrow() {
                        return borrow;
                    }

                    public void setBorrow(String borrow) {
                        this.borrow = borrow;
                    }

                    public String getAsset() {
                        return asset;
                    }

                    public void setAsset(String asset) {
                        this.asset = asset;
                    }

                    public String getFrozen() {
                        return frozen;
                    }

                    public void setFrozen(String frozen) {
                        this.frozen = frozen;
                    }
                }
            }
        }

        public static class GameDataBean implements Serializable {

            /**
             * list : [{"asset":"USDT","totalBalance":"1010.000000000000000000","availableBalance":"1010.000000000000000000","frozenBalance":"0.000000000000000000","preestimateBTC":"0.12625000","localPreestimate":"7216.73"}]
             * localPreestimate : 7216.73
             * btcPreestimate : 0.12625000
             */

            private String localPreestimate;
            private String btcPreestimate;
            private List<ListBean> list;

            public String getLocalPreestimate() {
                if (TextUtils.isEmpty(localPreestimate)) {
                    return "--";
                }
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public String getBtcPreestimate() {
                if (TextUtils.isEmpty(btcPreestimate)) {
                    return "--";
                }
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean implements Serializable{
                /**
                 * asset : USDT
                 * totalBalance : 1010.000000000000000000
                 * availableBalance : 1010.000000000000000000
                 * frozenBalance : 0.000000000000000000
                 * preestimateBTC : 0.12625000
                 * localPreestimate : 7216.73
                 */

                private String asset;
                private String totalBalance;
                private String availableBalance;
                private String frozenBalance;
                private String preestimateBTC;
                private String localPreestimate;

                public String getAsset() {
                    return asset;
                }

                public void setAsset(String asset) {
                    this.asset = asset;
                }

                public String getTotalBalance() {
                    return totalBalance;
                }

                public void setTotalBalance(String totalBalance) {
                    this.totalBalance = totalBalance;
                }

                public String getAvailableBalance() {
                    return availableBalance;
                }

                public void setAvailableBalance(String availableBalance) {
                    this.availableBalance = availableBalance;
                }

                public String getFrozenBalance() {
                    return frozenBalance;
                }

                public void setFrozenBalance(String frozenBalance) {
                    this.frozenBalance = frozenBalance;
                }

                public String getPreestimateBTC() {
                    return preestimateBTC;
                }

                public void setPreestimateBTC(String preestimateBTC) {
                    this.preestimateBTC = preestimateBTC;
                }

                public String getLocalPreestimate() {
                    return localPreestimate;
                }

                public void setLocalPreestimate(String localPreestimate) {
                    this.localPreestimate = localPreestimate;
                }
            }
        }

        public static class FinancialDataBean implements Serializable {

            /**
             * btcPreestimate : 0
             * localPreestimate : 0
             * financialAccountList : [{"asset":"BCHSV","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"BTC","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"USDT","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"ETC","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"TRX","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"LTC","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"ETH","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"},{"asset":"EOS","availableBalance":"0.00000000","preestimateBTC":"0","localPreestimate":"0"}]
             * ybbAccountDTO : {"btcPreestimate":"0","localPreestimate":"0","currentAccountList":[{"asset":"BCHSV","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"3824.340000"},{"asset":"BTC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"1616.487077"},{"asset":"USDT","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"5699.796297"},{"asset":"ETC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"2.000000"},{"asset":"TRX","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"0.000000"},{"asset":"LTC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"38.880999"},{"asset":"ETH","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"5.000000"},{"asset":"EOS","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"6484.000000"}]}
             */

            private String btcPreestimate;
            private String localPreestimate;
            private YbbAccountDTOBean ybbAccountDTO;
            private List<FinancialAccountListBean> financialAccountList;

            public String getBtcPreestimate() {
                return btcPreestimate;
            }

            public void setBtcPreestimate(String btcPreestimate) {
                this.btcPreestimate = btcPreestimate;
            }

            public String getLocalPreestimate() {
                return localPreestimate;
            }

            public void setLocalPreestimate(String localPreestimate) {
                this.localPreestimate = localPreestimate;
            }

            public YbbAccountDTOBean getYbbAccountDTO() {
                return ybbAccountDTO;
            }

            public void setYbbAccountDTO(YbbAccountDTOBean ybbAccountDTO) {
                this.ybbAccountDTO = ybbAccountDTO;
            }

            public List<FinancialAccountListBean> getFinancialAccountList() {
                return financialAccountList;
            }

            public void setFinancialAccountList(List<FinancialAccountListBean> financialAccountList) {
                this.financialAccountList = financialAccountList;
            }

            public static class YbbAccountDTOBean implements Serializable{
                /**
                 * btcPreestimate : 0
                 * localPreestimate : 0
                 * currentAccountList : [{"asset":"BCHSV","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"3824.340000"},{"asset":"BTC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"1616.487077"},{"asset":"USDT","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"5699.796297"},{"asset":"ETC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"2.000000"},{"asset":"TRX","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"0.000000"},{"asset":"LTC","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"38.880999"},{"asset":"ETH","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"5.000000"},{"asset":"EOS","availableBalance":"0.00000000","totalProfit":"0.00000000","yesterdayProfit":"0.00000000","lastSevenDayAnnual":"0.000000","yesterdayDividend":"0.000000","historyDividend":"6484.000000"}]
                 */

                private String btcPreestimate;
                private String localPreestimate;
                private List<CurrentAccountListBean> currentAccountList;

                public String getBtcPreestimate() {
                    if (TextUtils.isEmpty(btcPreestimate)) {
                        return "--";
                    }
                    return btcPreestimate;
                }

                public void setBtcPreestimate(String btcPreestimate) {
                    this.btcPreestimate = btcPreestimate;
                }

                public String getLocalPreestimate() {
                    if (TextUtils.isEmpty(localPreestimate)) {
                        return "--";
                    }
                    return localPreestimate;
                }

                public void setLocalPreestimate(String localPreestimate) {
                    this.localPreestimate = localPreestimate;
                }

                public List<CurrentAccountListBean> getCurrentAccountList() {
                    return currentAccountList;
                }

                public void setCurrentAccountList(List<CurrentAccountListBean> currentAccountList) {
                    this.currentAccountList = currentAccountList;
                }

                public static class CurrentAccountListBean implements Serializable{
                    /**
                     * asset : BCHSV
                     * availableBalance : 0.00000000
                     * totalProfit : 0.00000000
                     * yesterdayProfit : 0.00000000
                     * lastSevenDayAnnual : 0.000000
                     * yesterdayDividend : 0.000000
                     * historyDividend : 3824.340000
                     */

                    private String asset;
                    private String availableBalance;
                    private String totalProfit;
                    private String yesterdayProfit;
                    private String lastSevenDayAnnual;
                    private String yesterdayDividend;
                    private String historyDividend;

                    public String getAsset() {
                        return asset;
                    }

                    public void setAsset(String asset) {
                        this.asset = asset;
                    }

                    public String getAvailableBalance() {
                        return availableBalance;
                    }

                    public void setAvailableBalance(String availableBalance) {
                        this.availableBalance = availableBalance;
                    }

                    public String getTotalProfit() {
                        return totalProfit;
                    }

                    public void setTotalProfit(String totalProfit) {
                        this.totalProfit = totalProfit;
                    }

                    public String getYesterdayProfit() {
                        return yesterdayProfit;
                    }

                    public void setYesterdayProfit(String yesterdayProfit) {
                        this.yesterdayProfit = yesterdayProfit;
                    }

                    public String getLastSevenDayAnnual() {
                        return lastSevenDayAnnual;
                    }

                    public void setLastSevenDayAnnual(String lastSevenDayAnnual) {
                        this.lastSevenDayAnnual = lastSevenDayAnnual;
                    }

                    public String getYesterdayDividend() {
                        return yesterdayDividend;
                    }

                    public void setYesterdayDividend(String yesterdayDividend) {
                        this.yesterdayDividend = yesterdayDividend;
                    }

                    public String getHistoryDividend() {
                        return historyDividend;
                    }

                    public void setHistoryDividend(String historyDividend) {
                        this.historyDividend = historyDividend;
                    }
                }
            }

            public static class FinancialAccountListBean implements Serializable{
                /**
                 * asset : BCHSV
                 * availableBalance : 0.00000000
                 * preestimateBTC : 0
                 * localPreestimate : 0
                 */

                private String asset;
                private String availableBalance;
                private String preestimateBTC;
                private String localPreestimate;

                public String getAsset() {
                    if (TextUtils.isEmpty(asset)) {
                        return "--";
                    }
                    return asset;
                }

                public void setAsset(String asset) {
                    this.asset = asset;
                }

                public String getAvailableBalance() {
                    if (TextUtils.isEmpty(availableBalance)) {
                        return "--";
                    }
                    return availableBalance;
                }

                public void setAvailableBalance(String availableBalance) {
                    this.availableBalance = availableBalance;
                }

                public String getPreestimateBTC() {
                    return preestimateBTC;
                }

                public void setPreestimateBTC(String preestimateBTC) {
                    this.preestimateBTC = preestimateBTC;
                }

                public String getLocalPreestimate() {
                    return localPreestimate;
                }

                public void setLocalPreestimate(String localPreestimate) {
                    this.localPreestimate = localPreestimate;
                }
            }
        }
    }
}
