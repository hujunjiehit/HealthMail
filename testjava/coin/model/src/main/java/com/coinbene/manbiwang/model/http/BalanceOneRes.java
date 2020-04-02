package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

/**
 * Created by mengxiangdong
 */

public class BalanceOneRes extends BaseRes {

    /**
     * data : {"frozenBalance":"string","totalBalance":"string"}
     * timezone : 0
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
         * frozenBalance : string
         * totalBalance : string
         */

        private String frozenBalance;
        private String totalBalance;
        private String availableBalance;

        public String getFrozenBalance() {
            return frozenBalance;
        }

        public void setFrozenBalance(String frozenBalance) {
            this.frozenBalance = frozenBalance;
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
    }
}
