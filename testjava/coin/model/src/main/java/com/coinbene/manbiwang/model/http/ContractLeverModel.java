package com.coinbene.manbiwang.model.http;

import com.coinbene.manbiwang.model.base.BaseRes;

public class ContractLeverModel extends BaseRes {


    /**
     * data : {"curLeverage":0}
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
         * curLeverage : 0  代表之前没有
         */

        private int curLeverage;
        private  String marginMode;
        private String marginModeSetting;

        public String getMarginModeSetting() {
            return marginModeSetting;
        }

        public void setMarginModeSetting(String marginModeSetting) {
            this.marginModeSetting = marginModeSetting;
        }

        public String getMarginMode() {
            return marginMode;
        }

        public void setMarginMode(String marginMode) {
            this.marginMode = marginMode;
        }

        public int getCurLeverage() {
            return curLeverage;
        }

        public void setCurLeverage(int curLeverage) {
            this.curLeverage = curLeverage;
        }
    }
}
