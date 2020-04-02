package com.coinbene.manbiwang.spot.spot.impl;

/**
 * Created by mengxiangdong on 2017/12/21.
 */

public interface TradeFragmentListener {
    void buyOrSellListener(int orderType,String userId, String tradeType, String tradePairId, String cnt, String price);

    void cancelEntrustData(String tradeId);

//    public void getBalanceOneRequest();

    /**
     *k线图点击效果
     */
    void kChartViewClick();
}
