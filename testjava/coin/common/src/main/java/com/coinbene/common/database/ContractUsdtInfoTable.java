package com.coinbene.common.database;

import android.text.TextUtils;

import com.coinbene.manbiwang.model.http.ContractListModel;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class ContractUsdtInfoTable {

    @Id
    public long id;

    public String name;
    public String multiplier;
    public String minTradeAmount;
    public String maxTradeAmount;
    public String minPriceChange;
    public int precision;
    public String baseAsset;
    public String quoteAsset;
    public String costPriceMultiplier;
    public String leverages;
	public int curLever;


	public boolean equalsObject(ContractListModel.DataBean.ListBean listBean) {
        if (TextUtils.isEmpty(maxTradeAmount) || TextUtils.isEmpty(minTradeAmount) || TextUtils.isEmpty(name)
                || TextUtils.isEmpty(multiplier) || TextUtils.isEmpty(minPriceChange) || TextUtils.isEmpty(baseAsset)
                || TextUtils.isEmpty(quoteAsset) || TextUtils.isEmpty(costPriceMultiplier) || TextUtils.isEmpty(leverages)) {
            return false;
        }
        return maxTradeAmount.equals(listBean.getMaxTradeAmount())
                && minTradeAmount.equals(listBean.getMinTradeAmount())
                && name.equals(listBean.getSymbol())
                && multiplier.equals(listBean.getMultiplier())
                && minPriceChange.equals(listBean.getMinPriceChange())
                && precision == listBean.getPrecision()
                && baseAsset.equals(listBean.getBaseAsset())
                && quoteAsset.equals(listBean.getQuoteAsset())
                && costPriceMultiplier.equals(listBean.getCostPriceMultiplier())
                && leverages.equals(listBean.getLeverages());
    }
}
