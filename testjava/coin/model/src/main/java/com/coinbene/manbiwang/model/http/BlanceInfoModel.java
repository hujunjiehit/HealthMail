package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

import java.util.List;

/**
 * Create by huyong
 * on 2018/8/8
 */
public class BlanceInfoModel extends BaseRes {


    /**
     * data : {"accountAssetPoList":[{"accountName":"string","accountType":0,"estimateAssetPoList":[{"assetType":"string","baseEstimateAsset":{"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}}]}],"totalEstimateAssetPo":{"assetType":"string","baseEstimateAsset":{"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}}}
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
         * accountAssetPoList : [{"accountName":"string","accountType":0,"estimateAssetPoList":[{"assetType":"string","baseEstimateAsset":{"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}}]}]
         * totalEstimateAssetPo : {"assetType":"string","baseEstimateAsset":{"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}}
         */

        private TotalEstimateAssetPoBean totalEstimateAssetPo;
        private List<AccountAssetPoListBean> accountAssetPoList;

        public TotalEstimateAssetPoBean getTotalEstimateAssetPo() {
            return totalEstimateAssetPo;
        }

        public void setTotalEstimateAssetPo(TotalEstimateAssetPoBean totalEstimateAssetPo) {
            this.totalEstimateAssetPo = totalEstimateAssetPo;
        }

        public List<AccountAssetPoListBean> getAccountAssetPoList() {
            return accountAssetPoList;
        }

        public void setAccountAssetPoList(List<AccountAssetPoListBean> accountAssetPoList) {
            this.accountAssetPoList = accountAssetPoList;
        }

        public static class TotalEstimateAssetPoBean {
            /**
             * assetType : string
             * baseEstimateAsset : {"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}
             */

            private String assetType;
            private BaseEstimateAssetBean baseEstimateAsset;

            public String getAssetType() {
                return assetType;
            }

            public void setAssetType(String assetType) {
                this.assetType = assetType;
            }

            public BaseEstimateAssetBean getBaseEstimateAsset() {
                return baseEstimateAsset;
            }

            public void setBaseEstimateAsset(BaseEstimateAssetBean baseEstimateAsset) {
                this.baseEstimateAsset = baseEstimateAsset;
            }

            public static class BaseEstimateAssetBean {
                /**
                 * baseAmount : string
                 * baseAsset : string
                 * legalAmount : string
                 * legalAsset : string
                 */

                private String baseAmount;
                private String baseAsset;
                private String legalAmount;
                private String legalAsset;

                public String getBaseAmount() {
                    return baseAmount;
                }

                public void setBaseAmount(String baseAmount) {
                    this.baseAmount = baseAmount;
                }

                public String getBaseAsset() {
                    return baseAsset;
                }

                public void setBaseAsset(String baseAsset) {
                    this.baseAsset = baseAsset;
                }

                public String getLegalAmount() {
                    return legalAmount;
                }

                public void setLegalAmount(String legalAmount) {
                    this.legalAmount = legalAmount;
                }

                public String getLegalAsset() {
                    return legalAsset;
                }

                public void setLegalAsset(String legalAsset) {
                    this.legalAsset = legalAsset;
                }
            }
        }

        public static class AccountAssetPoListBean {
            /**
             * accountName : string
             * accountType : 0
             * estimateAssetPoList : [{"assetType":"string","baseEstimateAsset":{"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}}]
             */

            private String accountName;
            private int accountType;
            private List<EstimateAssetPoListBean> estimateAssetPoList;

            public String getAccountName() {
                return accountName;
            }

            public void setAccountName(String accountName) {
                this.accountName = accountName;
            }

            public int getAccountType() {
                return accountType;
            }

            public void setAccountType(int accountType) {
                this.accountType = accountType;
            }

            public List<EstimateAssetPoListBean> getEstimateAssetPoList() {
                return estimateAssetPoList;
            }

            public void setEstimateAssetPoList(List<EstimateAssetPoListBean> estimateAssetPoList) {
                this.estimateAssetPoList = estimateAssetPoList;
            }

            public static class EstimateAssetPoListBean {
                /**
                 * assetType : string
                 * baseEstimateAsset : {"baseAmount":"string","baseAsset":"string","legalAmount":"string","legalAsset":"string"}
                 */

                private String assetType;
                private BaseEstimateAssetBeanX baseEstimateAsset;

                public String getAssetType() {
                    return assetType;
                }

                public void setAssetType(String assetType) {
                    this.assetType = assetType;
                }

                public BaseEstimateAssetBeanX getBaseEstimateAsset() {
                    return baseEstimateAsset;
                }

                public void setBaseEstimateAsset(BaseEstimateAssetBeanX baseEstimateAsset) {
                    this.baseEstimateAsset = baseEstimateAsset;
                }

                public static class BaseEstimateAssetBeanX {
                    /**
                     * baseAmount : string
                     * baseAsset : string
                     * legalAmount : string
                     * legalAsset : string
                     */

                    private String baseAmount;
                    private String baseAsset;
                    private String legalAmount;
                    private String legalAsset;

                    public String getBaseAmount() {
                        return baseAmount;
                    }

                    public void setBaseAmount(String baseAmount) {
                        this.baseAmount = baseAmount;
                    }

                    public String getBaseAsset() {
                        return baseAsset;
                    }

                    public void setBaseAsset(String baseAsset) {
                        this.baseAsset = baseAsset;
                    }

                    public String getLegalAmount() {
                        return legalAmount;
                    }

                    public void setLegalAmount(String legalAmount) {
                        this.legalAmount = legalAmount;
                    }

                    public String getLegalAsset() {
                        return legalAsset;
                    }

                    public void setLegalAsset(String legalAsset) {
                        this.legalAsset = legalAsset;
                    }
                }
            }
        }
    }
}
