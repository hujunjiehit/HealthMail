package com.coinbene.manbiwang.model.http;

import java.io.Serializable;

public class SettleModel implements Serializable {

    private String profit;

    private int assetCode;

    private int currency;

    private String profitRate;

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }

    public int getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(int assetCode) {
        this.assetCode = assetCode;
    }

    public int getAccountCurrency() {
        return currency;
    }

    public void setAccountCurrency(int currency) {
        this.currency = currency;
    }

    public String getProfitRate() {
        return profitRate;
    }

    public void setProfitRate(String profitRate) {
        this.profitRate = profitRate;
    }
}
