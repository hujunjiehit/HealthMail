package com.coinbene.manbiwang.spot.otc.dialog;

public interface OtcSelectListener {

    void selectPayType(int viewId, String text,int selectPayType);

    void selectFilter(int[] getPriceRange, int payType, String currency);
}
