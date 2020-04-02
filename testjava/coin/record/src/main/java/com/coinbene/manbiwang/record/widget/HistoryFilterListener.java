package com.coinbene.manbiwang.record.widget;

public interface HistoryFilterListener {

    void onPopWindowDimiss();

    void doHttpFilter(String inputBaseAsset, String quoteAsset, String beginTime, String endTime, int typeDirection, boolean ignoreCancelled);

    void invalidTradepair();
}
